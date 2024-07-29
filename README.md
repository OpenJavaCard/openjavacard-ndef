## OpenJavaCard NDEF

This project contains several JavaCard applets acting as NFC NDEF tags.

It is intended as a reusable library covering most usecases for NDEF
on smartcards. There is support for emulating simple NDEF memory tags
as well as for dynamic tags.

[![Build Status](https://travis-ci.org/OpenJavaCard/openjavacard-ndef.svg?branch=master)](https://travis-ci.org/OpenJavaCard/openjavacard-ndef)

### Project

For more information about this overall project, see our [website](https://openjavacard.org/).

You can follow us on [Twitter](https://twitter.com/openjavacardorg) and chat with us
on [Gitter](https://gitter.im/openjavacard/general).

### Build

The project includes [ph4r05/javacard-gradle-plugin](https://github.com/ph4r05/javacard-gradle-plugin) Gradle plugin for
building and installing the applets.

Build the applets:

```shell
# clone the submodules
git submodule update --init --recursive
# build the applets
gradlew buildJavaCard
```

install an applet:

1. set your card reader name in `local.properties`
2. change the installation paramaters in `build.gradle` in the applet subproject to update the ndef payload
3. ```gradlew :$variant_subproject:installJavaCard```

### Documentation

* [Applet Variants](doc/variants.md)
* [Compatibility List](doc/compatibility.md)
* [Installation Guide](doc/install.md)
* [Protocol Reference](doc/protocol.md)

### Variants

| Name     | Description                                  | Status     |
|----------|----------------------------------------------|------------|
| full     | Full features (configured on install)        | Stable     |
| tiny     | Tiny feature set (read-only, static content) | Stable     |
| advanced | Full plus GlobalPlatform features            | Experiment |
| stub     | Stub backed by another service               | Experiment |

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

There was an NDEF applet before this one called [ndef-javacard](https://github.com/slomo/ndef-javacard).

### Legal

Copyright 2015-2020 Ingo Albrecht

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
