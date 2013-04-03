crossbase
=========

Crossbase is an example of a monolith program written in Java with Avian JVM integrated.
"Monolith" here means that it has no external dependencies except the ones provided by the OS.
Size of the binary, produced from this small demo project is lower than 1.5 megabytes. Not bad for a "native" app!

## Supported OS

The example is tested under:
* Windows 7 (32-bit x86 compilation with MinGW32)
* OS X 10.8 Mountain Lion (x86_64 build using the provided OS X JDK)
* Ubuntu 12.10 (a Debian-based linux, OpenJDK, x86_64 build)

Hopefully it should work under any Intel-based Linux, Windows or Mac platform with JDK compatible to Oracle JDK 6 or 7 installed.

## Building

* Download and install Oracle JDK 7, Apple JDK for OS X or OpenJDK (it isn't needed to _run_ the program, but it's used to _build_ it)
* Open a unix terminal window (use MinGW MSYS terminal under Windows)  
* Set the `JAVA_HOME` variable that should point to the installed JDK base folder (for ex. `export JAVA_HOME="/Library/Java/Home"` under OS X)
* Clone the repo to a folder, enter this folder in the terminal window and input `make`
* To run the compiled app use `bin/crossbase`. You can tell this app any command line arguments - it will print them back.
