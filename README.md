crossbase: a monolith program written in Java with Avian JVM integrated
=======================================================================

Size of the binary, produced from this small demo project is lower than 1.5 megabytes.

## Building

* Download and install Oracle JDK 7 (it isn't needed to _run_ the program, but it's used to _build_ it.
* Open a unix terminal window (use MinGW MSYS terminal under Windows)  
* Set `JAVA_HOME` variable (for ex. `export JAVA_HOME="/Library/Java/Home"` under OS X)
* Clone the repo to a folder, enter this folder in the terminal window and input `make`
* To run the compiled app use `bin/crossbase`. You can tell this app any command line arguments - it will print them back.
