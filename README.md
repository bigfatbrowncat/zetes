Zetes
=========

Zetes is a crossplatform lightweight framework which allows the user to develop console or native-looking GUI programs, written in Java, that could run on a machine without any JVM installed.

The core of Zetes is built upon Avian Java machine implementation which would be embedded into every target binary thus your program <i>could run without any dependencies except the system API</i>. Imagine your program that has been written in Java and it even could run external jars, but it doesn't need any JRE or JDK installed at all. Really. It's just an exe file. And in addition it has a beautiful GUI (based upon SWT). 

The main feature of the library is that it makes a thin wrapper on SWT and basic system libraries that makes it possible to make an app which has a native look-and-feel under different platforms. For example, the "Exit" item is in system menu on OS X, but in "File" menu under Windows; about box has "OK" button in Windows, but doesn't have any on OS X and so on... 

The user of the library shouldn't think about such differences. That's why Zetes could be useful.

## Supported OSes

Operating systems supported at the moment:
<ul>
	<li>Windows (XP - 7) i686 (32 bit) or x86_64 (64 bit)</li>
	<li>OS X 10.8 Mountain Lion</li>
	<li>Ubuntu 12.10 or any compatible (x86_64)</li>
	<li>Raspbian on Raspberry Pi board (armv6l processor, a Debian-based linux)</li>
</ul>

Hopefully it should work under any Intel-based Linux, Windows or Mac platform and on everything compatible with Raspberry Pi.

## Building Zetes

At first you should prepare the environment. Although Zetes is a crossplatform library and its building process is the same on any supported platform, at first you need to prepare environment and this preparation is a different process on different platforms.

### Environment on Windows on i686 (32 bit) and x86_64 (64 bit)

Since Windows isn't a POSIX platform, the environment preparation isn't completely trivial. In Windows it would take about 10-15 minutes.

Download and install Oracle JDK 7 (it isn't needed to _run_ the target binaries, but it's used to _build_ them and the framework itself, cause it doesn't contain any Java compiler tool). You can get it from the official <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">download site</a>.

This framework is made crossplatform and it needs Unix compilers and tools to work, so under Windows you need <code>mingw-w64</code> to build (and use) it. If you have a 32-bit OS, don't worry – <b>mingw-w64</b> is equipped with 32-bit tools as well as 64-bit ones.

Go to http://mingw-w64.sourceforge.net site, find <b>Mingw-builds</b> title there. Under this title you will find a list of implementations for different Windows platforms and a small link to the universal installer (the <a href="http://sourceforge.net/projects/mingwbuilds/files/mingw-builds-install/mingw-builds-install.exe/download">direct link</a> to the installer).
Download this installer, run it and install any mingw toolchain suitable for your system. Avoid installing it to a default "Program Files" folder cause you could have problems with configuration (caused by spaces in $PATH). I'd recommend c:\mingw\mingw-builds\<i>name_of_toolchain</i>\

Now you have gcc compiler, but don't have a proper unix environment for it. You should take it from the same place. It's called MSYS and located in the same sourceforge repo as mingw-w64.

Open http://sourceforge.net/projects/mingwbuilds/files/external-binary-packages/ and download the latest <b>msys+7za+wget+svn+git+mercurial+cvs-rev??.7z</b> version. 
The direct link to the current version is <a href="http://sourceforge.net/projects/mingwbuilds/files/external-binary-packages/msys%2B7za%2Bwget%2Bsvn%2Bgit%2Bmercurial%2Bcvs-rev13.7z/download">msys+7za+wget+svn+git+mercurial+cvs-rev13.7z</a>

Extract the archive contents to some folder next to your mingw compiler toolchain (I'd recommend c:\mingw\mingw-builds\msys).

Enter the subdirectory <code>etc</code> under the newly created directory <code>msys</code>. 
There you'll find a file named <code>fstab</code>. Open it with your favorite plain-text editor (notepad++ or something else supporting Unix EOLNs) and insert there
a line
	
	c:/mingw/mingw-builds/<name_of_toolchain>/mingw<XX>		/mingw
	
where the path should be changed to your path and <i>XX</i> is "32" on i686 system and "64" on x86_64. The entered path should exist and contain the compiler toolchain. for example, on my machine this line looks like this:

	c:/mingw/mingw-builds/x64-4.8.1-posix-seh-rev3/mingw64		/mingw

And the last preparation. You should set a couple of variables for your MSYS profile to connect it to your JDK.

In the same directory as <code>fstab</code> there is a file named <code>profile</code>. Open it. It contains many commands used to initialize MSYS environment. You should add a one or two lines to its end.

On 32-bit system write one line:

	export JAVA_HOME="<directory_where_jdk_is_installed>"
	
On 64-bit system you should write two lines

	export JAVA_HOME="<directory_where_jdk_is_installed>"
	export ARCH=x86_64

For example, on my machine it looks like 

	export JAVA_HOME="C:\Program Files\Java\jdk1.7.0_17"
	export ARCH=x86_64

The last thing. The default Git configuration on Windows includes an option to change all line endings to CRLF (Windoes). But the native line ending for MSYS utils and text processors is LF (Unix). So if Git change the line endings on checkout, some files will be broken - that influences installation process. So we are turning the conversion option off.

	git config --global core.autocrlf input
	
This is a global option. If you need to turn it back (for other tasks), just type <code>git config --global core.autocrlf true</code>.

Now let's check the configuration. Make sure you've saved all the files you've edited recently. Go to your <code>msys</code> directory and run <code>msys.bat</code> script there. It will open a terminal window for you. This terminal isn't a simple one. It's an MSYS terminal. You will use it to work with Zetes framework and your projects dependent on it. 

Let's do two tests to ensure everything's configured fine:

#### Test 1 
Enter this command in MSYS terminal:

	gcc
	
If you see something like

	gcc.exe: fatal error: no input files
	
then <code>gcc</code> compiler (and all the toolchain) is installed properly. If the response is like "command not found", something's gone wrong.

#### Test 2
Enter this command to MSYS terminal:

	"$JAVA_HOME/bin/javac"
	
Notice this quote marks. They are necessary. As a response you should see a long "usage" instruction for <code>javac</code> Java Compiler tool. If not, something's gone wrong.

Congratulations! You have configured your environment successfully.

### Environment on Ubuntu-based Linux distro

There are many deb-packaged distros out there and the preparation of the most part of them to build Zetes should be alike. I'm using Mint 16. First of all let's install all the dependencies:

	sudo apt-get install git openjdk-7-jdk libglu-dev libz-dev g++
	
Here we are installing OpenJDK for Java, libGLU and libGL for graphic API (used in some example programs), zlib development package and GNU C++ compiler for building native bindings.

Next step - setting Java Home location:

	export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/

### Environment on OS X (tested on 10.9)

On OS X you should install Command Line Tools for Developers. Go to https://developer.apple.com/opensource/, click "Command Line Tools" there. Login with your Apple ID, select the latest version of the tools for your OS version (I selected April 2014 for Mavericks). When the image file is downloaded, mount it and install the package inside.

Then go to the Oracle site for downloading JDK. Open http://www.oracle.com/technetwork/java/javase/downloads/index.html, select JDK 7 there, download it and install.

#### Test 1 
Enter this command in a terminal window:

	gcc
	
You should see:

	clang: error: no input files
	
Yeah, that's Clang pretending being GCC. If the response is like "command not found", something's gone wrong - you need to check your Command Line Tools.

#### Test 2
Enter this command:

	/usr/libexec/java_home
	
As a response you should receive something like 

	/Library/Java/JavaVirtualMachines/jdk1.7.0_17.jdk/Contents/Home
	
That's your JDK path. If its name looks suspicious, you could check its version by typing

	`/usr/libexec/java_home`/bin/java -version
	
The answer should contain a line

	java version "1.7.<something>"
	
If the version is 1.6 - you downloaded a wrong Java version, or install Apple Java instead of Oracle Java.

If everything's ok, let's proceed.

### Building Zetes

This part is much easier then the previous one.

On Windows open the MSYS terminal (with msys.bat). On other platforms a simple terminal window would be sufficient. 

Enter your Projects folder. Now you should get Zetes source (you may already have got one with "Zetes Examples" source). Let's check for its existence:

	ls
	
If you see <code>zetes</code> folder, it's already here, so skip the next command. Otherwise input the following:

	git clone https://github.com/bigfatbrowncat/zetes.git

Wait till it ends downloading the repo and enter the newly created <code>zetes</code> folder.

Now a very important step - we should choose which version of Java classpath we are using - Avian or Android. Avian classpath isn't complete, it lacks many useful features, but it's designed specially to work with Avian and it has a GPL-compatible BSD license. On the other hand, Android classpath is almost full and compatible with the original Oracle classpath. But it's not native for Avian and especially for Windows. Additionally, it's quite heavy - there is a high overhead (about 20 Megabytes of extra resulting binary size).

If you are building the demo applications, Avian classpath would be enough (and recommended).

For Avian classpath type:

	export CLASSPATH=avian
	
For Android classpath type:

	export CLASSPATH=android

Then finally execute

	make all
	
Zetes will be built completely (if no errors occured during the building process).

If you are building zetes-examples, proceed with the instruction published [here](https://github.com/bigfatbrowncat/zetes-examples#how-to-build). 
