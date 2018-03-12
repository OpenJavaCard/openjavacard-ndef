## JavaCard NDEF Applet

This project contains a JavaCard applet acting as an NFC NDEF Tag.

It is intended as a convenience applet, allowing storage of
an NDEF record on a smartcard to direct the user to a relevant
host-device application, such as a smartphone app related to the
card or a web page for which the card serves as an authorization token.

Data can be preloaded at install time using standards-compliant
methods so that this generic applet can be used in different
use cases without modification.

Alternatively the applet can be set up to be write-once, allowing
provisioning by the user or during card personalization.

### Status

 * Works well with some Android apps on a few cards of mine
 * Feature-complete as far as the standard goes
 * Only T=1 is supported, high-quality patches welcome
 * No systematic testing has been done
 * No systematic review has taken place
 * No unit tests have been implemented
 * Don't be afraid: it's good stuff

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
by providing NDEF data as install data during installation. This version
of the applet has a load file size less than 1024 bytes and is recommended
for serving static content. A good example would be pointing the user to
a lost-item return service. Please be mindful of the fact that a static
tag allows for easy identification, creating privacy concerns.

The STUB variant is an applet that uses a service to generate its contents.
This can be used for creating dynamic NDEF tags. Possible applications are
found in areas like unidirectional authentication and identification - such
as pseudonymous lost-item return services, proof-of-presence and other types
of token generation. This variant must be provisioned with a service ID and
AID at install time unless it is configured at build time. Writing is not
supported.

The FULL variant is a writable and configurable NDEF tag with advanced
features such as media-dependent access control and write-once support.
It can be configured during installation and at build time to include
various features. By default this will be a writable NDEF tag with 256
bytes of memory. Its load file size ranges from about 1k to 2k bytes
depending on selected features.

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

   P12 specified the offset into the file and must be valid.

   Length of RDATA is variable and depends on available
   resources, the protocol in use as well as the file size.
   As much data as possible will be returned.

   Returns SW=9000 when successful.

**UPDATE BINARY (CLA=00 INS=D6)**

   Update data in the selected file.

### Installation protocol

#### Common information

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
        -params 110200F112020800 \
        -install build/javacard&javacard-ndef-full.cap
 (Install full variant as a write-once tag with 2048 bytes of memory)
```

#### Tiny variant

The tiny variant requires an NDEF tag dataset as its install data.

The provided data will be used as the static data file of the tag.

No verification is performed on the data - it must be valid.

A length prefix should NOT be included.

#### Stub variant

This variant requires a backend service in another applet.

#### Full variant

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
in this project. It is simple but complete and therefore highly recommended
for new JavaCard projects.

This project contains some code from the excellent [IsoApplet](https://github.com/philipWendland/IsoApplet) by
[Philip Wendland](https://github.com/philipWendland). If you are looking for
a modern PKCS#11 applet that works well with OpenSC then this should be your choice.

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
