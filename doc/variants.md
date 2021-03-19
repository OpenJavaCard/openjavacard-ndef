## Variants

#### Advanced variant

#### Full variant

The FULL variant is a writable and configurable NDEF tag with optional
advanced features such as media-dependent access control and write-once
support. It can be configured during installation and at build time.
By default it will be a writable NDEF tag with 256 bytes of memory.
Its load file size ranges from about 1k to 2k bytes depending on selected
features.

#### Tiny variant

The TINY variant is a minimal read-only NDEF tag that can be initialized
by providing NDEF data as applet data during installation. This version
of the applet has a load file size less than 1k bytes and is recommended
for serving static content, such as a simple URL. Content size is limited
to allowable install data (~200 bytes).

#### Stub variant

The STUB variant is an applet that uses a service in another applet to
generate its contents. This can be used for creating dynamic NDEF tags
while keeping your actual application applet under its own proper AID.
Writing is not supported because it is not relevant and/or convenient
for any use-cases I could think of. Load file size is slightly above 1k
bytes. You will have to provide your own backend applet and generate
your own NDEF data.

#### Creating variants

If you need to create a new variant it is recommended to start with
the FULL variant as it contains every available feature except for
the service feature of the STUB variant.
