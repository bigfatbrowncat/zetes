IDE_CONF=ideconf

UNAME := $(shell uname)
ifndef ARCH
  ARCH := $(shell uname -m)
endif

ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_SHORT = osx
  PLATFORM_TAG = osx-x86_64
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  PLATFORM_SHORT = linux
  PLATFORM_TAG = linux-x86_64
else ifeq ($(UNAME) $(ARCH), Linux armv6l)	# linux on Raspberry Pi
  PLATFORM_SHORT = linux
  PLATFORM_TAG = linux-armv6l
else ifeq ($(OS) $(ARCH), Windows_NT i686)	# Windows
  PLATFORM_SHORT = win
  PLATFORM_TAG = win-i386
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows
  PLATFORM_SHORT = win
  PLATFORM_TAG = win-x86_64
endif

VARS_SUBST='s|<ARCH>|$(ARCH)|g;s|<PLATFORM_SHORT>|$(PLATFORM_SHORT)|g;s|<JAVA_HOME>|$(JAVA_HOME)|g'
IDE_CONF_SRC=$(IDE_CONF)/$(PLATFORM_TAG)

ideconf-eclipse:
	@echo Extracting Eclipse project files for $(PLATFORM_TAG)...
	mkdir -p .settings
	rm -f .settings/org.eclipse.cdt.core.prefs
	sed $(VARS_SUBST) < $(IDE_CONF)/eclipse/settings/org.eclipse.cdt.core.prefs.template > .settings/org.eclipse.cdt.core.prefs
	sed $(VARS_SUBST) < $(IDE_CONF)/eclipse/classpath.template > .classpath
	sed $(VARS_SUBST) < $(IDE_CONF)/eclipse/project.template > .project
	sed $(VARS_SUBST) < $(IDE_CONF)/eclipse/$(PLATFORM_SHORT)/cproject.template > .cproject


#.SILENT:
