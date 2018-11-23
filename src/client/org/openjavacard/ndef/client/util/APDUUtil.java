package org.openjavacard.ndef.client.util;

import javax.smartcardio.CommandAPDU;

/**
 * Utilities related to APDU objects
 *
 * Namely verbose APDU printing and convenient APDU construction.
 *
 * This fills some convenience and debuggability gaps in the smartcard API.
 */
public class APDUUtil {

    private static final int OFFSET_CLA = 0;
    private static final int OFFSET_INS = 1;
    private static final int OFFSET_P1 = 2;
    private static final int OFFSET_P2 = 3;
    private static final int OFFSET_LC = 4;
    private static final int OFFSET_CDATA = 5;


    /**
     * Convenience method for building command APDUs
     * @param cla
     * @param ins
     * @return
     */
    public static CommandAPDU buildCommand(byte cla, byte ins) {
        return buildCommand(cla, ins, (byte) 0, (byte) 0, null);
    }

    /**
     * Convenience method for building command APDUs
     * @param cla
     * @param ins
     * @param p1
     * @param p2
     * @return
     */
    public static CommandAPDU buildCommand(byte cla, byte ins, byte p1, byte p2) {
        return buildCommand(cla, ins, p1, p2, null);
    }

    /**
     * Convenience method for building command APDUs
     * @param cla
     * @param ins
     * @param data
     * @return
     */
    public static CommandAPDU buildCommand(byte cla, byte ins, byte[] data) {
        return buildCommand(cla, ins, (byte) 0, (byte) 0, data);
    }

    /**
     * Convenience method for building command APDUs
     * @param cla
     * @param ins
     * @param p12
     * @return
     */
    public static CommandAPDU buildCommand(byte cla, byte ins, short p12) {
        return buildCommand(cla, ins, p12, null);
    }

    /**
     * Convenience method for building command APDUs
     * @param cla
     * @param ins
     * @param p12
     * @param data
     * @return
     */
    public static CommandAPDU buildCommand(byte cla, byte ins, short p12, byte[] data) {
        return buildCommand(cla, ins,
                (byte) ((p12 >> 8) & 0xFF),
                (byte) ((p12 >> 0) & 0xFF),
                data);
    }

    /**
     * Convenience method for building command APDUs
     * @param cla
     * @param ins
     * @param p1
     * @param p2
     * @param data
     * @return
     */
    public static CommandAPDU buildCommand(byte cla, byte ins, byte p1, byte p2, byte[] data) {
        int length = 5;
        if (data != null) {
            length += data.length;
        }
        byte[] command = new byte[length];
        command[OFFSET_CLA] = cla;
        command[OFFSET_INS] = ins;
        command[OFFSET_P1] = p1;
        command[OFFSET_P2] = p2;
        if (data == null || data.length == 0) {
            command[OFFSET_LC] = 0;
        } else {
            command[OFFSET_LC] = (byte) data.length;
            System.arraycopy(data, 0, command, OFFSET_CDATA, data.length);
        }
        return new CommandAPDU(command);
    }


}
