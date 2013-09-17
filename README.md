Zetes
=========

Zetes is a crossplatform lightweight framework which allows the user to develop console or native-looking GUI programs, written in Java, that could run on a machine without any JVM installed.

The core of Zetes is built upon Avian Java machine implementation which would be embedded into every target binary thus your program <i>could run without any dependencies except the system API</i>. Imagine your program that has been written in Java and it even could run external jars, but it doesn't need any JRE or JDK installed at all. Really. It's just an exe file. And in addition it has a beautiful GUI.

## Supported OS

Fully supported at the moment:
* Windows (XP - 7) i686 (32 bit) or x86_64 (64 bit)
* OS X 10.8 Mountain Lion

Partially supported (only console applications, no GUI yet):
* Ubuntu 12.10 or any compatible (x86_64)
* Raspbian on Raspberry Pi board (armv6l processor, a Debian-based linux)

Hopefully it should work under any Intel-based Linux, Windows or Mac platform and on everything compatible with Raspberry Pi.

## Building on Windows

At first you should prepare the environment. In Windows it would take about 10-15 minutes

### Environment

Download and install Oracle JDK 7 (it isn't needed to _run_ the target binaries, but it's used to _build_ them and the framework itself, cause it doesn't contain any Java compiler tool). You can get it from the official <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">download site</a>.

This framework is made crossplatform and it needs Unix compilers and tools to work, sounder Windows you need <code>mingw-w64</code> to build (and use) it. If you have a 32-bit OS, don't worry &#151; <b>mingw-w64</b> doesn't have only 64-bit tools, but 32-bit versions too.

Go to http://mingw-w64.sourceforge.net site, find <b>Mingw-builds</b> title there. Under this title you will find a list of implementations for different windows platforms and a small link to the universal installer (the <a href="http://sourceforge.net/projects/mingwbuilds/files/mingw-builds-install/mingw-builds-install.exe/download">direct link</a> to the installer).
Download this installer, run it and install any mingw toolchain, suitable for your system. Avoid installing it to a default "Program Files" folder cause you could have problems with configuration. I'd recommend c:\mingw\mingw-builds\<i>name_of_toolchain</i>\

Now you have gcc compiler, but don't have a proper unix environment for it. You should take it from the same place. It's called MSYS and located in the same sourceforge repo as mingw-w64.

Open http://sourceforge.net/projects/mingwbuilds/files/external-binary-packages/ and download the latest <b>msys+7za+wget+svn+git+mercurial+cvs-rev??.7z</b> version. 
The direct link to the current version is http://sourceforge.net/projects/mingwbuilds/files/external-binary-packages/msys%2B7za%2Bwget%2Bsvn%2Bgit%2Bmercurial%2Bcvs-rev13.7z/download

Extract the archive contents to some folder near your mingw compiler toolchain (I recommend c:\mingw\mingw-builds\msys).

Enter the subdirectory <code>etc</code> under the newly created directory <code>msys</code>. 
There you'll find a file named <code>fstab</code>. Open it with your favorite plain-text editor (notepad or notepad++ or something...) and insert there
a line
	
	c:/mingw/mingw-builds/<name_of_toolchain>/mingw<XX>		/mingw
	
where the path should be changed to your path and <i>XX</i> is "32" on i686 system and "64" on x86_64. The entered path should exist and contain the compiler toolchain. for example, on my machine this line looks like this:

	c:/mingw/mingw-builds/x64-4.8.1-posix-seh-rev3/mingw64		/mingw

And the last preparation. You should set a couple of variables for your MSYS profile to connect it to your JDK.

In the same directory as <code>fstab</code> there is a file named <code>profile</code>. Open it. It contains many commands used to initialize MSYS environment. You should add a one or two lines to its end.

On 32-bit system write one line:

	export JAVA_HOME="<directory_where_jdk_installed>"
	
On 64-bit system you should write two lines

	export JAVA_HOME="<directory_where_jdk_installed>"
	export ARCH=x86_64

For example, on my machine it looks like 

	export JAVA_HOME="C:\Program Files\Java\jdk1.7.0_17"
	export ARCH=x86_64
	
Now let's check the configuration. Make sure you saved all the files you've edited recently. Go to your <code>msys</code> directory and run <code>msys.bat</code> script there. It will open a terminal window for you. This terminal isn't a simple one. It's an MSYS terminal. You will use it to work with Zetes framework and your projects dependent on it. 

Let's do two tests to ensure everything's configured fine:

* Enter

	gcc
	
If you see something like

	gcc.exe: fatal error: no input files
	
then <code>gcc</code> compiler (and all the toolchain) is installed properly. If the response is like "command not found", something went wrong.

* Enter

	"$JAVA_HOME/bin/javac"
	
Notice this quote marks. They are necessary. As a response you should see a long "usage" instruction for <code>javac</code> Java Compiler tool. If not, something went wrong.

Congratulations! You have configured your environment successfully.

### Building Zetes

This part is much easier then the previous one

Open the MSYS terminal (with msys.bat). Enter your Projects folder. Now you should get Zetes source.

	git clone https://github.com/bigfatbrowncat/zetes.git
	
Wait till it ends downloading the repo, enter newly created <code>zetes</code> folder and
write

	make all
	
Zetes will be built with all demo projects.
