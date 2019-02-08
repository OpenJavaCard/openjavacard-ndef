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

import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class NdefGenerator {

    private short mLength;
    private short mDepth;

    private byte[]  mFlagStk;
    private short[] mLenStk;

    public NdefGenerator() {
    }

    public void begin() {
        mLength = 0;
        mDepth = 0;
    }

    public void beginSmartPoster() {
    }

    public void endSmartPoster() {
    }

    public void buildText(byte[] buf, short off, short len) {
    }

    public void buildURL(byte abbr, byte[] buf, short Off, short Len) {
    }

    public void finish(byte[] buf, short off, short len) {
    }

    private void error() {
        ISOException.throwIt(ISO7816.SW_UNKNOWN);
    }

}
