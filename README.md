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

### Features

 * CAP size of about 4 kBytes
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
  * Will minimize size when content is preloaded

### Install-time configuration

It is possible to configure the applet at install time. To do
so you will have to find out how to provide custom application
data to your GlobalPlatform frontend. For common opensource
tools such as "gp.jar" and "gpj.jar" you can do it like this:

```
 user@host:~$ java -jar gp.jar \
        -params 100BD101075402656E54657374 \
        -install javacard-ndef.cap
 (This example will preload the tag with the text "Test")
```

```
 user@host:~$ java -jar gp.jar \
        -params 110200F112020800 \
        -install javacard-ndef.cap
 (This will make the tag write-once with 2048 bytes of memory)
```

The following TLV records are supported:

**DATA INITIAL [0x10 [byte len] [bytes data]]**

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

**DATA ACCESS [0x11 0x02 [byte read] [byte write]]**

   Provides access policies for NDEF data read and write.

   Standard values: 0x00 (open access), 0xFF (no access)
   Proprietary values: 0xF0 (contact-only), 0xF1 (write-once)

**DATA SIZE [0x12 0x02 [short size]]**

   Specifies the size of the NDEF data file. Up to
   32767 bytes may be requested. Installation will
   fail when sufficient memory is not available.

### Related Projects

I use [GlobalPlatformPro](https://github.com/martinpaljak/GlobalPlatformPro) by [Martin Paljak](https://github.com/martinpaljak/)
for managing my cards during development. It works well with my NXP and Gemalto cards.

JavaCard compilation and conversion is done using [ant-javacard](https://github.com/martinpaljak/ant-javacard) in this project.
It is simple but complete and therefore highly recommended for new JavaCard projects.

This project contains some code from the excellent [IsoApplet](https://github.com/philipWendland/IsoApplet) by Philip Wendland.
If you are looking for a modern PKCS#11 applet that works well with OpenSC then this should be your choice.

### Recommended Android Apps

I can recommend [NFC TagInfo](https://play.google.com/store/apps/details?id=com.nxp.taginfolite)
and [NFC TagWriter](https://play.google.com/store/apps/details?id=com.nxp.nfc.tagwriter) by NXP
for interacting with this applet, even though they are proprietary. Both apps are quite
professional and work well. TagInfo is great for debugging because it shows a lot of detail.

### Legal

Copyright 2015 Ingo Albrecht

This software is licensed under the GNU GPL Version 3.
See the file LICENSE in the source tree for further information.

This software contains TLV parsing functions developed
and published by Philip Wendland as part of IsoApplet.

### Standards

The applet is intended to comply with the following standards, where applicable:
 * ISO 7816-4 Organization, security and commands for interchange (Release 2013)
 * GlobalPlatform Card Specification (Version 2.1)
 * NFC Forum Type 4 Tag Operation Specification (Version 2.0)
