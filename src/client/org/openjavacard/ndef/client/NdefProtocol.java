package org.openjavacard.ndef.client;

public interface NdefProtocol {

    byte[] AID_NDEF = new byte[] {
            // RID for NXP Germany
            (byte)0xD2, (byte)0x76, 0x00, 0x00, (byte)0x85,
            // PIX for NDEF Type 4
            (byte)0x01, (byte)0x01
    };

    /* Classes */
    byte CLA_ISO = (byte)0x00;

    /* Instructions */
    byte INS_SELECT        = (byte)0xA4;
    byte INS_READ_BINARY   = (byte)0xB0;
    byte INS_UPDATE_BINARY = (byte)0xD6;

    /* File IDs */
    short FILEID_NDEF_CAPABILITIES = (short)0xE103;
    short FILEID_NDEF_DATA         = (short)0xE104;

    /* File access specifications */
    byte FILE_ACCESS_OPEN = (byte)0x00;
    byte FILE_ACCESS_NONE = (byte)0xFF;

    /* Parameters for SELECT */
    byte SELECT_P1_BY_FILEID     = (byte)0x00;
    byte SELECT_P1_BY_NAME       = (byte)0x04;
    byte SELECT_P2_FIRST_OR_ONLY = (byte)0x0C;

    /* NDEF mapping version (specification 2.0) */
    byte NDEF_MAPPING_VERSION = (byte)0x20;

    /* Constants related to capability container */
    byte CC_LEN_HEADER = 7;
    byte CC_OFF_NDEF_FILE_CONTROL = 0x07;
    byte CC_TAG_NDEF_FILE_CONTROL = 0x04;
    byte CC_LEN_NDEF_FILE_CONTROL = 6;

}
