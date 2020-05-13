## Installation Guide

#### All variants

It is possible to configure the various variants of the applet at install time.
To do so you will have to find out how to provide custom application data to your GlobalPlatform frontend.

For common open-source tools such as "gp.jar" and "gpj.jar" you can do it like this:

```
 user@host:~$ java -jar gp.jar \
        -applet D2760000850101 \
        -params 100BD101075402656E54657374 \
        -install build/javacard/javacard-ndef-tiny.cap
 (Install tiny variant with static text "Test")
```

```
 user@host:~$ java -jar gp.jar \
        -applet D2760000850101 \
        -params 3FABCDABCD \
        -install build/javacard/javacard-ndef-stub.cap
 (Install stub variant using backend in app ABCDABCD service 0x3F)
```

```
 user@host:~$ java -jar gp.jar \
        -applet D2760000850101 \
        -params 810200F182020800 \
        -install build/javacard/javacard-ndef-full.cap
 (Install full variant as a write-once tag with 2048 bytes of memory)
```
#### Full variant

The full variant can be configured during install time by providing a concatenation of TLV records as install data.
Content and access control properties of the applet can be configured.

The following TLV records are supported:

##### **DATA INITIAL [0x80 [byte len] [bytes data]]**

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

##### **DATA ACCESS [0x81 0x02 [byte read] [byte write]]**

   Provides access policies for NDEF data read and write.

   Standard values: 0x00 (open access), 0xFF (no access)
   Proprietary values: 0xF0 (contact-only), 0xF1 (write-once)

##### **DATA SIZE [0x82 0x02 [short size]]**

   Specifies the size of the NDEF data file. Up to
   32767 bytes may be requested. Installation will
   fail when sufficient memory is not available.

   Note that 2 bytes are required for the record size.

#### Tiny variant

The tiny variant requires an NDEF tag dataset as its install data, which will be used as the read-only content of the tag.
The data needs to be small enough to fit in install data (200+ bytes) and will not be verified by the applet.
You should not prepend the dataset with a length prefix as in the stored form.

#### Stub variant

This variant requires a backend service in another applet.

To use it you need to import it as a JavaCard library and implement the trivial NdefService interface, serving it as a shareable object.

TODO: Publish an example and some useful applications.

TODO: Document how to configure it. See install example above or source code.
