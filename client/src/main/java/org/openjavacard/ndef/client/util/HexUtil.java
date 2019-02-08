package org.openjavacard.ndef.client.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/** Utilities for hexadecimal conversion */
public class HexUtil {

    public static String hex8(byte value) {
        return hex8(value & 0xFF);
    }

    public static String hex8(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException();
        }
        return bytesToHex(new byte[]{
                (byte) (value & 0xFF)
        });
    }

    public static String hex16(byte value) {
        return hex16(value & 0xFF);
    }

    public static String hex16(short value) {
        return hex16(value & 0xFFFF);
    }

    public static String hex16(int value) {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException();
        }
        return bytesToHex(new byte[]{
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 0) & 0xFF)
        });
    }

    public static String hex24(byte value) {
        return hex24(value & 0xFF);
    }

    public static String hex24(short value) {
        return hex24(value & 0xFFFF);
    }

    public static String hex24(int value) {
        if (value < 0 || value > ((1 << 24) - 1)) {
            throw new IllegalArgumentException();
        }
        return bytesToHex(new byte[]{
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 0) & 0xFF)
        });
    }

    public static byte byte8(String string) {
        if(string.length() != 2) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = hexToBytes(string);
        return BinUtil.getByte(bytes, 0);
    }

    public static short short16(String string) {
        if(string.length() != 4) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = hexToBytes(string);
        return BinUtil.getShort(bytes, 0);
    }

    public static int unsigned8(String string) {
        if(string.length() != 2) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = hexToBytes(string);
        return BinUtil.getByte(bytes, 0) & 0xFF;
    }

    public static int unsigned16(String string) {
        if(string.length() != 4) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = hexToBytes(string);
        return BinUtil.getShort(bytes, 0) & 0xFFFF;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "(null)";
        } else if (bytes.length == 0) {
            return "(empty)";
        } else {
            return Hex.encodeHexString(bytes);
        }
    }

    public static byte[] hexToBytes(String hex) {
        byte[] result = null;
        if (hex != null) {
            try {
                result = Hex.decodeHex(hex.toCharArray());
            } catch (DecoderException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return result;
    }

}
