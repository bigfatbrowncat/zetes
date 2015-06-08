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

AVIAN_ARCH=$(ARCH)
ifeq ($(AVIAN_ARCH), armv6l)   # Raspberry Pi
  AVIAN_ARCH=arm
else ifeq ($(AVIAN_ARCH), armv7l)
  AVIAN_ARCH=arm
else ifeq ($(AVIAN_ARCH), i686)
  AVIAN_ARCH=i386
  ARCH=i386
endif

ifeq ($(UNAME), Darwin)	# OS X
  JAVA_HOME?=$(shell /usr/libexec/java_home)
  PLATFORM=darwin
  PLATFORM_TAG = darwin-x86_64
  AVIAN_PLATFORM_TAG_PART = macosx-x86_64
  CLASSPATH_DELIM=:
  JNILIB_EXT=.jnilib
  SHLIB_EXT=.dylib
  EXE_EXT=
  PIC=
else ifeq ($(UNAME), Linux)
  JAVA_HOME?=$(shell readlink -f `which javac` | sed "s:bin/javac::")
  PLATFORM=linux
  PLATFORM_TAG = linux-$(ARCH)
  AVIAN_PLATFORM_TAG_PART = linux-$(AVIAN_ARCH)
  CLASSPATH_DELIM=:
  JNILIB_EXT=.so
  SHLIB_EXT=.so
  EXE_EXT=
  PIC=-fPIC
else ifeq ($(OS) $(ARCH), Windows_NT i686)		# Windows 32
  PLATFORM=windows
  PLATFORM_TAG = windows-i386
  AVIAN_PLATFORM_TAG_PART = windows-i386
  CLASSPATH_DELIM=;
  JNILIB_EXT=.dll
  SHLIB_EXT=.dll
  EXE_EXT=.exe
  PIC=
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64
  PLATFORM=windows
  PLATFORM_TAG = windows-x86_64
  AVIAN_PLATFORM_TAG_PART = windows-x86_64
  CLASSPATH_DELIM=;
  JNILIB_EXT=.dll
  SHLIB_EXT=.dll
  EXE_EXT=.exe
  PIC=
endif

ifeq ($(CLASSPATH), android)
  AVIAN_PLATFORM_SUFFIX = -android
else
  AVIAN_PLATFORM_SUFFIX =
endif

# In Avian Darwin is now macosx or ios. We use macosx
AVIAN_PLATFORM_TAG := $(AVIAN_PLATFORM_TAG_PART)$(AVIAN_PLATFORM_SUFFIX)

ifndef TARGET
  TARGET = target-$(PLATFORM_TAG)-$(CLASSPATH)
endif
