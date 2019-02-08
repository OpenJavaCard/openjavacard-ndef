/*
 * openjavacard-ndef: JavaCard applet implementing an NDEF tag
 * Copyright (C) 2015-2018 Ingo Albrecht <copyright@promovicz.org>
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

package org.openjavacard.ndef.generator;

public interface NdefConstants {

    // Flag indicating the beginning of a message
    byte FLAG_MB = (byte)0x80;
    // Flag indicating the end of a message
    byte FLAG_ME = (byte)0x40;
    // Flag indicating that the record is chunked
    byte FLAG_CHUNKED = (byte)0x20;
    // Flag indicating that the record is in short format
    byte FLAG_SHORT = (byte)0x10;
    // Flag indicating presence of an ID length field
    byte FLAG_IL    = (byte)0x80;

    // Mask for TNF in the flag field
    byte TNF_MASK = (byte)0x07;

    byte TNF_EMPTY      = (byte)0x00;
    byte TNF_WELL_KNOWN = (byte)0x01;
    byte TNF_MEDIA      = (byte)0x02;
    byte TNF_URI        = (byte)0x03;
    byte TNF_EXTERNAL   = (byte)0x04;
    byte TNF_UNKNOWN    = (byte)0x05;
    byte TNF_UNCHANGED  = (byte)0x06;
    byte TNF_RESERVED   = (byte)0x07;

    byte RTD_TEXT_0 = 0x54; // 'T'
    byte RTD_URI_0 = 0x55; // 'U'
    byte RTD_SMARTPOSTER_0 = 0x53; // 'S'
    byte RTD_SMARTPOSTER_1 = 0x70; // 'p'

    // No abbreviation
    byte ABBR_NONE = 0;
    // Abbreviation for http://www.
    byte ABBR_HTTP_WWW = 1;
    // Abbreviation for https://www.
    byte ABBR_HTTPS_WWW = 2;
    // Abbreviation for http://
    byte ABBR_HTTP = 3;
    // Abbreviation for https://
    byte ABBR_HTTPS = 4;
    // Abbreviation for tel:
    byte ABBR_TEL = 5;
    // Abbreviation for mailto:
    byte ABBR_MAILTO = 6;
    // Abbreviation for ftp://anonymous:anonymous@
    byte ABBR_FTP_ANONYMOUS = 7;
    // Abbreviation for ftp://ftp.
    byte ABBR_FTP_FTP = 8;
    // Abbreviation for ftps://
    byte ABBR_FTPS = 9;
    // Abbreviation for sftp://
    byte ABBR_SFTP = 10;
    // Abbreviation for smb://
    byte ABBR_SMB = 11;
    // Abbreviation for nfs://
    byte ABBR_NFS = 12;
    // Abbreviation for ftp://
    byte ABBR_FTP = 13;
    // Abbreviation for dav://
    byte ABBR_DAV = 14;
    // Abbreviation for news:
    byte ABBR_NEWS = 15;
    // Abbreviation for telnet://
    byte ABBR_TELNET = 16;
    // Abbreviation for imap:
    byte ABBR_IMAP = 17;
    // Abbreviation for rtsp://
    byte ABBR_RTSP = 18;
    // Abbreviation for urn:
    byte ABBR_URN = 19;
    // Abbreviation for pop:
    byte ABBR_POP = 20;
    // Abbreviation for sip:
    byte ABBR_SIP = 21;
    // Abbreviation for sips:
    byte ABBR_SIPS = 22;
    // Abbreviation for tftp:
    byte ABBR_TFTP = 23;
    // Abbreviation for btspp://
    byte ABBR_BTSPP = 24;
    // Abbreviation for btl2cap://
    byte ABBR_BTL2CAP = 25;
    // Abbreviation for btgoep://
    byte ABBR_BTGOEP = 26;
    // Abbreviation for tcpobex://
    byte ABBR_TCPOBEX = 27;
    // Abbreviation for irdaobex://
    byte ABBR_IRDAOBEX = 28;
    // Abbreviation for file://
    byte ABBR_FILE = 29;
    // Abbreviation for urn:epc:id:
    byte ABBR_URN_EPC_ID = 30;
    // Abbreviation for urn:epc:tag:
    byte ABBR_URN_EPC_TAG = 31;
    // Abbreviation for urn:epc:pat:
    byte ABBR_URN_EPC_PAT = 32;
    // Abbreviation for urn:epc:raw:
    byte ABBR_URN_EPC_RAW = 33;
    // Abbreviation for urn:epc:
    byte ABBR_URN_EPC = 34;
    // Abbreviation for urn:nfc:
    byte ABBR_URN_NFC = 35;

}
