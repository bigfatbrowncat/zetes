UNAME := $(shell uname)
ifndef ARCH
  ARCH := $(shell uname -m)
endif

SRC = src
BIN = bin
OBJ = obj

DEBUG_OPTIMIZE = -O3 #-O0 -g

ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_TAG = osx-x86_64
  PLATFORM_GENERAL_INCLUDES = -I/System/Library/Frameworks/JavaVM.framework/Headers
else ifeq ($(UNAME) $(ARCH), Linux x86_64)		# linux on PC
  PLATFORM_TAG = linux-x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
else ifeq ($(UNAME) $(ARCH), Linux armv6l)		# linux on Raspberry Pi
  PLATFORM_TAG = linux-armv6l
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
else ifeq ($(OS) $(ARCH), Windows_NT i686)		# Windows 32
  PLATFORM_TAG = win-i386
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64
  PLATFORM_TAG = win-x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
endif

# Java platform agnostic
JAVA_SOURCE_PATH = $(SRC)/java
JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

# Java platform specific
JAVA_PLATFORM_SPECIFIC_SOURCE_PATH = $(SRC)/$(PLATFORM_TAG)/java
JAVA_PLATFORM_SPECIFIC_FILES = $(shell if [ -d "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" ]; then cd $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1'; fi)
JAVA_PLATFORM_SPECIFIC_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_PLATFORM_SPECIFIC_FILES))))

# C++ Platform agnostic
CPP_SOURCE_PATH = $(SRC)/cpp
CPP_FILES = $(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
CPP_HEADER_FILES = $(addprefix $(CPP_SOURCE_PATH)/,$(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.h | awk '{ sub(/.\//,"") }; 1'))
CPP_OBJECTS = $(addprefix $(OBJECTS_PATH)/,$(addsuffix .o,$(basename $(CPP_FILES))))

# Target paths
BINARY_PATH = $(BIN)/$(PLATFORM_TAG)
OBJECTS_PATH = $(OBJ)/$(PLATFORM_TAG)
JAVA_CLASSPATH = $(BIN)/java/classes

# Target names
LIBRARY_NAME = libzetesfeet.a
JAVA_LIBRARY_NAME = zetesfeet.jar

help:
	@echo "*** This is Zetes build utility: This part builds feet of Zetes ***"
	@echo
	@echo 1. Set up your build environment
	@echo Zetes library needs Java 7 compiler, so JAVA_HOME is to be set correctly. 
	@echo
	@echo Current value: JAVA_HOME=$(JAVA_HOME)
	@echo
	@echo To change it you can type:
	@echo
	@echo "  > export JAVA_HOME=<path>"
	@echo
	@echo Make sure that the current value ARCH=$(ARCH) is a correct architecture of the
	@echo target that you are going to build. If it\'s not, type
	@echo
	@echo "  > export ARCH=<arch>"
	@echo
	@echo "where <arch> is one of: i386, x86_64"
	@echo
	@echo 2. Build Zetes or config IDE 
	@echo To build the library right now from the command line, type 
	@echo
	@echo "  > make all"
	@echo
	@echo To setup project files for Eclipse CDT IDE, type
	@echo
	@echo "  > make ideconf-eclipse"
	@echo
	@echo To clean compiled binary files, type 
	@echo
	@echo "  > make clean"
	@echo
	

all: $(BINARY_PATH)/$(LIBRARY_NAME) $(BIN)/java/$(JAVA_LIBRARY_NAME)
	@echo "*** Building process completed successfully. ***"
	@echo You can find the result in folders:
	@echo
	@echo "  $(BINARY_PATH)"
	@echo "  $(BIN)/java"
	@echo

include ideconf/ideconf.mk

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java $(SWT_CLASSES)
	@echo Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)" -d "$(JAVA_CLASSPATH)" $<

$(JAVA_CLASSPATH)/%.class: $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)/%.java $(SWT_CLASSES)
	@echo Compiling platform specific $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp $(CPP_HEADER_FILES)
	@echo Compiling $<...
	mkdir -p $(OBJECTS_PATH)
	g++ $(DEBUG_OPTIMIZE) -D_JNI_IMPLEMENTATION_ -c -Iinclude $(PLATFORM_GENERAL_INCLUDES) $< -o $@

$(BINARY_PATH)/$(LIBRARY_NAME): $(CPP_OBJECTS)
	@echo Constructing $@...
	mkdir -p $(BINARY_PATH);

	# Extracting libavian objects
	( \
	    cd $(OBJ); \
	    mkdir -p libavian; \
	    cd libavian; \
	    ar x ../../lib/$(PLATFORM_TAG)/libavian.a; \
	)

	mkdir -p $(BIN)/java

	# Making an object file from the java class library
	
	ar rvs $@ $(CPP_OBJECTS) $(OBJ)/libavian/*.o

$(BIN)/java/$(JAVA_LIBRARY_NAME): lib/java/classpath.jar $(JAVA_CLASSES) $(JAVA_PLATFORM_SPECIFIC_CLASSES) $(SWT_CLASSES)
	@echo Constructing $@...
	mkdir -p $(BINARY_PATH);

	# Making the java class library
	cp -f lib/java/classpath.jar $(BIN)/java/$(JAVA_LIBRARY_NAME); \
	( \
	    cd $(BIN)/java; \
	    "$(JAVA_HOME)/bin/jar" u0f $(JAVA_LIBRARY_NAME) -C ../java/classes .; \
	)

clean:
	@echo Cleaning all...
	rm -rf $(OBJ)
	rm -rf $(BIN)

.PHONY: all
.SILENT:
