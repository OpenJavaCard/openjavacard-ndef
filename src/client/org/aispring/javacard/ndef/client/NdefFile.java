package org.aispring.javacard.ndef.client;

import org.aispring.javacard.ndef.client.util.BinUtil;

public class NdefFile {
    short fileId;
    int fileSize;
    byte readAccess;
    byte writeAccess;

    NdefFile(byte[] data, int off) {
        fileId = BinUtil.getShort(data, off + 0);
        fileSize = BinUtil.getShort(data, off + 2) & 0xFFFF;
        readAccess = data[off + 4];
        writeAccess = data[off + 5];
    }

}
