package org.aispring.javacard.ndef.client.util;

import javacard.framework.ISO7816;

import javax.smartcardio.CommandAPDU;

/**
 * Utilities related to APDU objects
 *
 * Namely verbose APDU printing and convenient APDU construction.
 *
 * This fills some convenience and debuggability gaps in the smartcard API.
 */
public class APDUUtil {

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
        command[ISO7816.OFFSET_CLA] = cla;
        command[ISO7816.OFFSET_INS] = ins;
        command[ISO7816.OFFSET_P1] = p1;
        command[ISO7816.OFFSET_P2] = p2;
        if (data == null || data.length == 0) {
            command[ISO7816.OFFSET_LC] = 0;
        } else {
            command[ISO7816.OFFSET_LC] = (byte) data.length;
            System.arraycopy(data, 0, command, ISO7816.OFFSET_CDATA, data.length);
        }
        return new CommandAPDU(command);
    }


}
