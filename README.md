## OpenJavaCard NDEF

This project contains several JavaCard applets acting as NFC NDEF tags.

It is intended as a reusable library covering most usecases for NDEF
on smartcards. There is support for emulating simple NDEF memory tags
as well as for dynamic tags.

[![Build Status](https://travis-ci.org/OpenJavaCard/openjavacard-ndef.svg?branch=master)](https://travis-ci.org/OpenJavaCard/openjavacard-ndef)

### Status

 * Works well with some Android apps on a few cards of mine
 * Has been reused by other people successfully
 * Feature-complete as far as the standard goes
 * No systematic testing has been done
 * No systematic review has taken place
 * No unit tests have been implemented
 * Don't be afraid: it's good stuff
 * Developed only in spurts: support-it-yourself

### History

 * Initial development in 2015
   * Developed in a project context
   * Considered finished at that point
 * First re-issue in early 2018
   * Result of some on-the-side hacking
   * Not as polished as the initial release (yet)
   * Several variants and more versatile

### Features

 * Decent code quality
 * Load size less than 2 kiB, down to about 1 kiB
 * Standard-compliant NDEF reading and writing
 * Does not require object deletion support
 * Configurable at install time
   * Preloading of NDEF data (up to about 200 bytes)
   * Configuration of data size
   * Configuration of access policies
 * Useful access policies
   * "Contact only" allows limiting writes to contact interface
   * "Write once" allows writing the data file once for provisioning
   * Proprietary access policies are hidden from the host,
    allowing full reader/writer compatibility.
 * Up to 32767 bytes of storage (up to 32765 bytes of NDEF data)
   * Default size is 256 bytes to save card memory
   * Preloading data automatically sets storage size

### Variants

The TINY variant is a minimal read-only NDEF tag that can be initialized
by providing NDEF data as applet data during installation. This version
of the applet has a load file size less than 1k bytes and is recommended
for serving static content, such as a simple URL. Content size is limited
to allowable install data (~200 bytes).

The STUB variant is an applet that uses a service in another applet to
generate its contents. This can be used for creating dynamic NDEF tags
while keeping your actual application applet under its own proper AID.
Writing is not supported because it is not relevant and/or convenient
for any use-cases I could think of. Load file size is slightly above 1k
bytes. You will have to provide your own backend applet and generate
your own NDEF data.

The FULL variant is a writable and configurable NDEF tag with optional
advanced features such as media-dependent access control and write-once
support. It can be configured during installation and at build time.
By default it will be a writable NDEF tag with 256 bytes of memory.
Its load file size ranges from about 1k to 2k bytes depending on selected
features.

If you need to create a new variant it is recommended to start with
the FULL variant as it contains every available feature except for
the service feature of the STUB variant. (TODO: add service support to
FULL variant)

### APDU Protocol

This applet implements only the exact minimum of APDU commands
that the NDEF specification prescribes. All variants implement
the same subset, being limited only in implemented features.

**SELECT (CLA=00 INS=A4 P1=00 P2=0C CDATA=fid)**

   Select a file in the applet.

   P1=00 means "SELECT BY FILEID"
   P2=0C means "SELECT FIRST OR ONLY"
   Other selection modes are not supported.

   There are two files on the card:
     * 0xE103 - NDEF capabilities
     * 0xE104 - NDEF data

   In exception to ISO7816 no FCI (file control information) will
   be returned, as permitted by NDEF specification requirement
   RQ_T4T_NDA_034.

   Returns SW=9000 when successful.

**READ BINARY (CLA=00 INS=B0 P12=offset RDATA=output)**

   Read data from the selected file.

   P12 specifies the offset into the file and must be valid.

   Length of RDATA is variable and depends on available
   resources, the protocol in use as well as the file size.
   As much data as possible will be returned.

   Returns SW=9000 when successful.

**UPDATE BINARY (CLA=00 INS=D6 P12=offset CDATA=data)**

   Update data in the selected file.

   P12 specifies the offset into the file and must be valid.

   Allowable length of data depends on the build-time
   parameter NDEF_WRITE_SIZE (default is 128 bytes).

   Returns SW=9000 when successful.

### Usage

#### All variants

It is possible to configure the various variants of the applet
at install time. To do so you will have to find out how to provide
custom application data to your GlobalPlatform frontend. For common
opensource tools such as "gp.jar" and "gpj.jar" you can do it like this:

```
 user@host:~$ java -jar gp.jar \
        -params 100BD101075402656E54657374 \
        -install build/javacard/javacard-ndef-tiny.cap
 (Install tiny variant with static text "Test")
```

```
 user@host:~$ java -jar gp.jar \
        -params 3FABCDABCD \
        -install build/javacard/javacard-ndef-stub.cap
 (Install stub variant using backend in app ABCDABCD service 0x3F)
```

```
 user@host:~$ java -jar gp.jar \
        -params 810200F182020800 \
        -install build/javacard/javacard-ndef-full.cap
 (Install full variant as a write-once tag with 2048 bytes of memory)
```

#### Tiny variant

The tiny variant requires an NDEF tag dataset as its install data,
which will be used as the read-only content of the tag. The data
needs to be small enough to fit in install data (200+ bytes) and
will not be verified by the applet. You should not prepend the
dataset with a length prefix as in the stored form.

#### Stub variant

This variant requires a backend service in another applet.

To use it you need to import it as a JavaCard library and implement
the trivial NdefService interface, serving it as a shareable object.

TODO: Publish an example and some useful applications.

TODO: Document how to configure it. See install example above or source code.

#### Full variant

The full variant can be configured during install time by providing
a concatenation of TLV records as install data. Content and access
control properties of the applet can be configured.

The following TLV records are supported:

**DATA INITIAL [0x80 [byte len] [bytes data]]**

   Will initialize the NDEF data file with the provided
   data, which should be a valid NDEF record without
   the additional record size field, which will be
   synthesized automatically from the data.

   Default access policies will be read-only so that
   the tag can be initialized using just this option,
   but this can be overridden with DATA ACCESS.

   The size of the data file will be adjusted to
   accomodate the record. If you want more memory you
   should override the size using DATA SIZE.

**DATA ACCESS [0x81 0x02 [byte read] [byte write]]**

   Provides access policies for NDEF data read and write.

   Standard values: 0x00 (open access), 0xFF (no access)
   Proprietary values: 0xF0 (contact-only), 0xF1 (write-once)

**DATA SIZE [0x82 0x02 [short size]]**

   Specifies the size of the NDEF data file. Up to
   32767 bytes may be requested. Installation will
   fail when sufficient memory is not available.

   Note that 2 bytes are required for the record size.

### Related Projects

I use [GlobalPlatformPro](https://github.com/martinpaljak/GlobalPlatformPro) by
[Martin Paljak](https://github.com/martinpaljak/) for managing my cards during
development. It works well with my NXP and Gemalto cards.

JavaCard compilation and conversion is done using [ant-javacard](https://github.com/martinpaljak/ant-javacard)
in this project. It is simple but complete and therefore highly recommended for new JavaCard projects.

This project contains some code from the fine [IsoApplet](https://github.com/philipWendland/IsoApplet) by
[Philip Wendland](https://github.com/philipWendland).

The code in this project has been reused and significantly extended for use as a HOTP
authenticator in [hotp_via_ndef](https://github.com/petrs/hotp_via_ndef). I am inclined
to merge some of its features at some point. Thank you for sharing!

A company called MpicoSys has reused this code as a demo applet here: https://github.com/MpicoSys/PicoLabel/.

There was an NDEF applet before this one called [ndef-javacard](https://github.com/slomo/ndef-javacard).

### Legal

Copyright 2015-2018 Ingo Albrecht

This software is licensed under the GNU GPL Version 3.
See the file LICENSE in the source tree for further information.

This software contains TLV parsing functions developed
and published by Philip Wendland as part of IsoApplet, which
are also licensed under the GNU GPL Version 3.

Note that the GPL requires that you make the source code to
your applet available to all your customers and that you
inform your customers about this option by means of an
explicit written offer. It is recommended to publish your
modifications as open source software, just as this project
is.

### Standards

The applet is intended to comply with the following standards, where applicable:
 * ISO 7816-4 Organization, security and commands for interchange (Release 2013)
 * GlobalPlatform Card Specification (Version 2.1)
 * NFC Forum Type 4 Tag Operation Specification (Version 2.0)
