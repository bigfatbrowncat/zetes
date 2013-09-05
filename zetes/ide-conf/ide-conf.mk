IDE_CONF=ide-conf

UNAME := $(shell uname)
ifndef ARCH
  ARCH := $(shell uname -m)
endif

ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_TAG = osx-x86_64
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  PLATFORM_TAG = linux-x86_64
else ifeq ($(UNAME) $(ARCH), Linux armv6l)	# linux on Raspberry Pi
  PLATFORM_TAG = linux-armv6l
else ifeq ($(OS) $(ARCH), Windows_NT i686)	# Windows
  PLATFORM_TAG = win-i386
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows
  PLATFORM_TAG = win-x86_64
endif

ide-conf-eclipse:
	@echo Extracting Eclipse project files for $(PLATFORM_TAG)...
	cp -f $(IDE_CONF)/$(PLATFORM_TAG)/eclipse/.project ./
	cp -f $(IDE_CONF)/$(PLATFORM_TAG)/eclipse/.cproject ./
	cp -f $(IDE_CONF)/$(PLATFORM_TAG)/eclipse/.classpath ./
	cp -rf $(IDE_CONF)/$(PLATFORM_TAG)/eclipse/.settings ./

.SILENT:
