/*
 * javacard-ndef: JavaCard applet implementing an NDEF tag
 * Copyright (C) 2015  Ingo Albrecht (prom@berlin.ccc.de)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.openjavacard.ndef.tiny;

import javacard.framework.*;

/**
 * \brief Applet implementing a read-only NDEF type 4 tag
 *
 * This is the TINY variant of the applet, intended as a permanent
 * read-only tag. It can be used to direct users to an application
 * or website appropriate to your card.
 *
 * No writing is supported. Data must be initialized by providing
 * raw NDEF data as an applet installation parameter. No verification
 * on the data is performed. The length indicator is appended by the
 * applet and should not be included.
 *
 * Implemented to comply with:
 *   NFC Forum
 *   Type 4 Tag Operation Specification
 *   Version 2.0
 *
 * Conformity remarks:
 *   1. The NDEF data file is restricted in size to the maximum
 *      size of initialization data on the specific platform.
 *   2. No file control information (FCI) is returned in SELECT responses
 *      as allowed by specification requirement RQ_T4T_NDA_034.
 *   3. Proprietary files are not being used.
 *
 */
public final class NdefApplet extends Applet {

    /* Instructions */
    private static final byte INS_SELECT        = ISO7816.INS_SELECT;
    private static final byte INS_READ_BINARY   = (byte)0xB0;
    private static final byte INS_UPDATE_BINARY = (byte)0xD6;

    /* File IDs */
    private static final short FILEID_NONE              = (short)0x0000;
    private static final short FILEID_NDEF_CAPABILITIES = (short)0xE103;
    private static final short FILEID_NDEF_DATA         = (short)0xE104;

    /* File access specifications */
    private static final byte FILE_ACCESS_OPEN = (byte)0x00;
    private static final byte FILE_ACCESS_NONE = (byte)0xFF;

    /* Parameters for SELECT */
    private static final byte SELECT_P1_BY_FILEID     = (byte)0x00;
    private static final byte SELECT_P2_FIRST_OR_ONLY = (byte)0x0C;

    /* NDEF mapping version (specification 2.0) */
    private static final byte NDEF_MAPPING_VERSION = (byte)0x20;

    /* Constants related to capability container */
    private static final byte CC_LEN_HEADER = 7;
    private static final byte CC_TAG_NDEF_FILE_CONTROL = 0x04;
    private static final byte CC_LEN_NDEF_FILE_CONTROL = 6;

    /**
     * Configuration: maximum read block size
     */
    private static final short NDEF_MAX_READ = 128;

    /**
     * Configuration: maximum write block size
     */
    private static final short NDEF_MAX_WRITE = 128;

    /**
     * Configuration: read access
     */
    private static final byte NDEF_READ_ACCESS = FILE_ACCESS_OPEN;

    /**
     * Configuration: write access
     */
    private static final byte NDEF_WRITE_ACCESS = FILE_ACCESS_NONE;

    /** Transient variables */
    private static short[] vars;
    /** Index for currently selected file */
    private static final byte VAR_SELECTED_FILE = (byte)0;
    /** Number of transient variables */
    private static final short NUM_VARS = (short)1;

    /** NDEF capability file contents */
    private static byte[] capsFile;
    /** NDEF data file contents */
    private static byte[] dataFile;

    /**
     * Installs an NDEF applet
     *
     * Will create, initialize and register an instance of
     * this applet as specified by the provided install data.
     *
     * Requested AID will always be honored.
     * Control information is ignored.
     * Applet data will be used for initialization.
     *
     * @param buf containing install data
     * @param off offset of install data in buf
     * @param len length of install data in buf
     */
    public static void install(byte[] buf, short off, byte len) {
        short pos = off;
        // find AID
        byte  lenAID = buf[pos++];
        short offAID = pos;
        pos += lenAID;
        // find control information (ignored)
        byte  lenCI = buf[pos++];
        short offCI = pos;
        pos += lenCI;
        // find applet data
        byte  lenAD = buf[pos++];
        short offAD = pos;
        pos += lenAD;

        // instantiate and initialize the applet
        NdefApplet applet = new NdefApplet(buf, offAD, lenAD);

        // register the applet
        applet.register();
    }

    /**
     * Main constructor
     *
     * This will construct and initialize an instance
     * of this applet according to the provided app data.
     *
     * @param buf containing application data
     * @param off offset of app data in buf
     * @param len length of app data in buf
     */
    protected NdefApplet(byte[] buf, short off, byte len) {
        // length of actual data file
        short dataLen = (short)(len + 2);
        // create transient variables
        vars = JCSystem.makeTransientShortArray(NUM_VARS, JCSystem.CLEAR_ON_DESELECT);
        // create capabilities files
        capsFile = makeCaps(dataLen);
        // create data file
        byte[] data = null;
        if (len > 0) {
            data = new byte[dataLen];
            // container size
            Util.setShort(data, (short) 0, len);
            // initial data
            Util.arrayCopyNonAtomic(buf, off, data, (short) 2, len);
        } else {
            ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        }
        dataFile = data;
    }

    /**
     * Create and initialize the CAPABILITIES file
     *
     * @param dataSize to be allocated
     * @return an array for use as the CC file
     */
    private byte[] makeCaps(short dataSize) {
        short capsLen = (short)(CC_LEN_HEADER + 2 + CC_LEN_NDEF_FILE_CONTROL);
        byte[] caps = new byte[capsLen];

        short pos = 0;

        // CC length
        pos = Util.setShort(caps, pos,  capsLen);
        // mapping version
        caps[pos++] = NDEF_MAPPING_VERSION;
        // maximum read size
        pos = Util.setShort(caps, pos, NDEF_MAX_READ);
        // maximum write size
        pos = Util.setShort(caps, pos, NDEF_MAX_WRITE);

        // NDEF File Control TLV
        caps[pos++] = CC_TAG_NDEF_FILE_CONTROL;
        caps[pos++] = CC_LEN_NDEF_FILE_CONTROL;
        // file ID
        pos = Util.setShort(caps, pos, FILEID_NDEF_DATA);
        // file size
        pos = Util.setShort(caps, pos, dataSize);
        // read access
        caps[pos++] = NDEF_READ_ACCESS;
        // write access
        caps[pos++] = NDEF_WRITE_ACCESS;

        // check consistency
        if(pos != capsLen) {
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
        }

        // return the file
        return caps;
    }

    /**
     * Process an APDU
     *
     * This is the outer layer of our APDU dispatch.
     *
     * It deals with the CLA and INS of the APDU,
     * leaving the rest to an INS-specific function.
     *
     * @param apdu to be processed
     * @throws ISOException on error
     */
    @Override
    public final void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        byte ins = buffer[ISO7816.OFFSET_INS];

        // handle selection of the applet
        if(selectingApplet()) {
            vars[VAR_SELECTED_FILE] = FILEID_NONE;
            return;
        }

        // secure messaging is not supported
        if(apdu.isSecureMessagingCLA()) {
            ISOException.throwIt(ISO7816.SW_SECURE_MESSAGING_NOT_SUPPORTED);
        }

        // process commands to the applet
        if(apdu.isISOInterindustryCLA()) {
            if (ins == INS_SELECT) {
                processSelect(apdu);
            } else if (ins == INS_READ_BINARY) {
                processReadBinary(apdu);
            } else if (ins == INS_UPDATE_BINARY) {
                ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
            } else {
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
    }

    /**
     * Process a SELECT command
     *
     * This handles only the one case mandated by the NDEF
     * specification: SELECT FIRST-OR-ONLY BY-FILE-ID.
     *
     * The file ID is specified in the APDU contents. It
     * must be exactly two bytes long and also valid.
     *
     * @param apdu to process
     * @throws ISOException on error
     */
    private void processSelect(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        byte p1 = buffer[ISO7816.OFFSET_P1];
        byte p2 = buffer[ISO7816.OFFSET_P2];

        // we only support what the NDEF spec prescribes
        if(p1 != SELECT_P1_BY_FILEID || p2 != SELECT_P2_FIRST_OR_ONLY) {
            ISOException.throwIt(ISO7816.SW_FUNC_NOT_SUPPORTED);
        }

        // receive data
        short lc = apdu.setIncomingAndReceive();

        // check length, must be for a file ID
        if(lc != 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // retrieve the file ID
        short fileId = Util.getShort(buffer, ISO7816.OFFSET_CDATA);

        // perform selection if the ID is valid
        if(fileId == FILEID_NDEF_CAPABILITIES || fileId == FILEID_NDEF_DATA) {
            vars[VAR_SELECTED_FILE] = fileId;
        } else {
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
    }

    /**
     * Process a READ BINARY command
     *
     * This supports simple reads at any offset.
     *
     * The length of the returned data is limited
     * by the maximum R-APDU length as well as by
     * the maximum read size NDEF_MAX_READ.
     *
     * @param apdu to process
     * @throws ISOException on error
     */
    private void processReadBinary(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();

        // access the file
        byte[] file = accessFileForRead(vars[VAR_SELECTED_FILE]);

        // get and check the read offset
        short offset = Util.getShort(buffer, ISO7816.OFFSET_P1);
        if(offset < 0 || offset >= file.length) {
            ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        }

        // determine the output size
        short le = apdu.setOutgoingNoChaining();
        if(le > NDEF_MAX_READ) {
            le = NDEF_MAX_READ;
        }

        // adjust for end of file
        short limit = (short)(offset + le);
        if(limit < 0) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        if(limit >= file.length) {
            le = (short)(file.length - offset);
        }

        // send the requested data
        apdu.setOutgoingLength(le);
        apdu.sendBytesLong(file, offset, le);
    }

    /**
     * Access a file for reading
     *
     * This function serves to perform precondition checks
     * before actually operating on a file in a read operation.
     *
     * If this function succeeds then the given fileId was
     * valid, security access has been granted and reading
     * of data for this file is possible.
     *
     * @param fileId of the file to be read
     * @return data array of the file
     * @throws ISOException on error
     */
    private byte[] accessFileForRead(short fileId) throws ISOException {
        byte[] file = null;
        // select relevant data
        if(fileId == FILEID_NDEF_CAPABILITIES) {
            file = capsFile;
        }
        if(fileId == FILEID_NDEF_DATA) {
            file = dataFile;
        }
        // check that we got anything
        if(file == null) {
            ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
        return file;
    }

}
