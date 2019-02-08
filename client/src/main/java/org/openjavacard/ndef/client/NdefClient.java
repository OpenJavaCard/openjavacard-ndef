package org.openjavacard.ndef.client;

import org.openjavacard.ndef.client.util.APDUUtil;
import org.openjavacard.ndef.client.util.BinUtil;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class NdefClient {

    private final Card mCard;
    private final CardChannel mChannel;
    private final byte[] mAID;
    private boolean mConnected;
    private NdefCapabilities mCapabilities;

    public NdefClient(CardChannel channel, byte[] aid) {
        mCard = channel.getCard();
        mChannel = channel;
        mAID = aid;
        mConnected = false;
        mCapabilities = null;
    }

    public NdefClient(Card card, byte[] aid) {
        this(card.getBasicChannel(), aid);
    }

    public NdefClient(CardChannel channel) {
        this(channel, NdefProtocol.AID_NDEF);
    }

    public NdefClient(Card card) {
        this(card.getBasicChannel(), NdefProtocol.AID_NDEF);
    }

    public Card getCard() {
        return mCard;
    }

    public CardChannel getChannel() {
        return mChannel;
    }

    public byte[] getAID() {
        return mAID;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public NdefCapabilities getCapabilities() {
        return mCapabilities;
    }

    public boolean detect() {
        boolean res = false;
        try {
            connect();
            res = true;
        } catch (CardException e) {
        }
        return res;
    }

    public void connect() throws CardException {
        try {
            mCard.beginExclusive();
            performSelectApplet(mAID);
            mConnected = true;
            mCapabilities = readCapabilities();
        } catch (CardException e) {
            disconnect();
            throw e;
        }
    }

    public void disconnect() {
        mConnected = false;
        mCapabilities = null;
        try {
            mCard.endExclusive();
        } catch (CardException e) {
        }
    }

    public byte[] readData() throws CardException {
        return readFile(NdefProtocol.FILEID_NDEF_DATA);
    }

    public byte[] readFile(short fileId) throws CardException {
        if(!mConnected) {
            throw new IllegalStateException("Client is not connected");
        }
        // select the file
        performSelectFile(fileId);
        // read the file length
        short fileLen = performReadBinarySize();
        // read in blocks
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        short done = 0;
        short dataLen = fileLen;
        while(done < dataLen) {
            short need = (short)(dataLen - done);
            byte[] data = performReadBinary((short)(2 + done));
            bos.write(data, 0, Math.min(data.length, need));
            done += data.length;
        }
        // return the data
        return bos.toByteArray();
    }

    public void writeData(byte[] data) throws CardException {
        writeFile(NdefProtocol.FILEID_NDEF_DATA, data);
    }

    public void writeFile(short fileId, byte[] data) throws CardException {
        if(!mConnected) {
            throw new IllegalStateException("Client is not connected");
        }
        // find the file
        NdefFile file = mCapabilities.findFile(fileId);
        // check file length
        if(data.length > (file.fileSize - 2)) {
            throw new IllegalArgumentException("Data to large for file");
        }
        // select the file
        performSelectFile(fileId);
        // set the file size to 0 during write
        performUpdateBinarySize((short)0);
        // write in blocks
        int off = 0;
        int end = data.length;
        while(off < end) {
            int need = data.length - off;
            int step = Math.min(need, mCapabilities.maxWrite);
            performUpdateBinary((short)(off + 2), (short)step, data, off);
            off += step;
        }
        // set the file size to the real value
        performUpdateBinarySize((short)data.length);
    }

    private NdefCapabilities readCapabilities() throws CardException {
        // we read capabilities differently because their
        // length field includes the length prefix itself
        performSelectFile(NdefProtocol.FILEID_NDEF_CAPABILITIES);
        byte[] data = performReadBinary((short)2);
        return new NdefCapabilities(data);
    }

    private void performSelectApplet(byte[] aid) throws CardException {
        CommandAPDU command = APDUUtil.buildCommand(
                NdefProtocol.CLA_ISO,
                NdefProtocol.INS_SELECT,
                NdefProtocol.SELECT_P1_BY_NAME,
                NdefProtocol.SELECT_P2_FIRST_OR_ONLY,
                aid
        );
        transactAndCheck(command);
    }

    private void performSelectFile(short fileId) throws CardException {
        byte[] data = new byte[2];
        BinUtil.setShort(data, (short)0, fileId);
        CommandAPDU command = APDUUtil.buildCommand(
                NdefProtocol.CLA_ISO,
                NdefProtocol.INS_SELECT,
                NdefProtocol.SELECT_P1_BY_FILEID,
                NdefProtocol.SELECT_P2_FIRST_OR_ONLY,
                data
        );
        transactAndCheck(command);
    }

    private short performReadBinarySize() throws CardException {
        byte[] sizeBytes = performReadBinary((short)0);
        return BinUtil.getShort(sizeBytes, (short)0);
    }

    private byte[] performReadBinary(short fileOff) throws CardException {
        CommandAPDU command = APDUUtil.buildCommand(
                NdefProtocol.CLA_ISO,
                NdefProtocol.INS_READ_BINARY,
                fileOff
        );
        return transactAndCheck(command).getData();
    }

    private void performUpdateBinarySize(short fileSize) throws CardException {
        byte[] data = new byte[2];
        BinUtil.setShort(data, (short)0, fileSize);
        performUpdateBinary((short)0, (short)2, data, (short)0);
    }

    private void performUpdateBinary(short fileOff, short fileLen, byte[] buf, int bufOff) throws CardException {
        if(fileLen > mCapabilities.maxWrite) {
            throw new CardException("Chunk to long for card capabilities");
        }
        byte[] data = Arrays.copyOfRange(buf, bufOff, bufOff + fileLen);
        CommandAPDU command = APDUUtil.buildCommand(
                NdefProtocol.CLA_ISO,
                NdefProtocol.INS_UPDATE_BINARY,
                fileOff,
                data
        );
        transactAndCheck(command);
    }

    private ResponseAPDU transactAndCheck(CommandAPDU capdu) throws CardException {
        ResponseAPDU rapdu = mChannel.transmit(capdu);
        int sw = rapdu.getSW();
        if(sw != 0x9000) {
            throw new CardException("Card returned error " + sw);
        }
        return rapdu;
    }

}
