crossbase
=========

Crossbase is an example of a monolith program written in Java with Avian JVM integrated.
"Monolith" here means that it has no external dependencies except the ones provided by the OS.
Size of the binary, produced from this small demo project is lower than 1.5 megabytes. Not bad for a "native" app!

## Supported OS

The example is tested under:
* Windows 7 (64-bit x86_64 compilation with MinGW-w64, Oracle JDK7)
* OS X 10.8 Mountain Lion (x86_64 build using Oracle JDK7)
* Ubuntu 12.10 (x86_64 build, a Debian-based linux, OpenJDK 7)
* Raspbian on Raspberry Pi board (armv6l processor, a Debian-based linux, OpenJDK7)

Hopefully it should work under any Intel-based Linux, Windows or Mac platform with JDK compatible to Oracle JDK 6 or 7 installed 
and on anything compatible with Raspberry Pi.

## Building

* Download and install Oracle JDK 7 or OpenJDK (it isn't needed to _run_ the program, but it's used to _build_ it)
* Open a unix terminal window (use MinGW MSYS terminal under Windows)  
* Set the `JAVA_HOME` variable that should point to the installed JDK base folder (something like `export JAVA_HOME="/c/Program\ files/Java/jdk7_1.2.3"`)
* Clone the repo to a folder, enter this folder in the terminal window and input `make`
* To run the compiled app use `bin/crossbase`. You can tell this app any command line arguments -- it will print them back.
