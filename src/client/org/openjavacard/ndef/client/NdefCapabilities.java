package org.openjavacard.ndef.client;

import org.openjavacard.ndef.client.util.BinUtil;

import java.util.ArrayList;
import java.util.List;

public class NdefCapabilities {

    byte version;
    short maxRead;
    short maxWrite;
    List<NdefFile> files;

    NdefCapabilities(byte[] data) {
        int off = 0;
        version = data[off++];
        maxRead = BinUtil.getShort(data, off); off += 2;
        maxWrite = BinUtil.getShort(data, off); off += 2;
        if(version != NdefProtocol.NDEF_MAPPING_VERSION) {
            throw new IllegalArgumentException("Invalid capabilities version");
        }
        ArrayList<NdefFile> files = new ArrayList<NdefFile>();
        while(off < data.length) {
            if(data[off + 0] != NdefProtocol.CC_TAG_NDEF_FILE_CONTROL) {
                throw new IllegalArgumentException("NDEF capabilities: bad file control tag");
            }
            if(data[off + 1] != NdefProtocol.CC_LEN_NDEF_FILE_CONTROL) {
                throw new IllegalArgumentException("NDEF capabilities: bad file control len");
            }
            off += 2;
            files.add(new NdefFile(data, off));
            off += NdefProtocol.CC_LEN_NDEF_FILE_CONTROL;
        }
        this.files = files;
    }

    NdefFile findFile(short fileId) {
        for(NdefFile file: files) {
            if(file.fileId == fileId) {
                return file;
            }
        }
        return null;
    }

}
