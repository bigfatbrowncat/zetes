UNAME := $(shell uname)

ZETES_FEET_PATH = $(ZETES_PATH)/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_HANDS_PATH = $(ZETES_PATH)/zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH)
ZETES_WINGS_PATH = $(ZETES_PATH)/zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH)

ifndef CLASSPATH
  CLASSPATH := android
endif

ifndef ARCH
  ARCH := $(shell uname -m)
endif

ifeq ($(UNAME), Darwin)	# OS X
  JAVA_HOME=$(shell /usr/libexec/java_home)
  PLATFORM_TAG = darwin-x86_64
  JNILIB_EXT=.jnilib
  EXE_EXT=
  PIC=
else ifeq ($(UNAME) $(ARCH), Linux x86_64)		# linux on PC
  PLATFORM_TAG = linux-x86_64
  JNILIB_EXT=.so
  EXE_EXT=
  PIC=-fPIC
else ifeq ($(UNAME) $(ARCH), Linux armv6l)		# linux on Raspberry Pi
  PLATFORM_TAG = linux-armv6l
  JNILIB_EXT=.so
  EXE_EXT=
  PIC=-fPIC
else ifeq ($(OS) $(ARCH), Windows_NT i686)		# Windows 32
  PLATFORM_TAG = windows-i386
  JNILIB_EXT=.dll
  EXE_EXT=.exe
  PIC=
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64
  PLATFORM_TAG = windows-x86_64
  JNILIB_EXT=.dll
  EXE_EXT=.exe
  PIC=
endif

ifndef TARGET
  TARGET = target-$(PLATFORM_TAG)-$(CLASSPATH)
endif
