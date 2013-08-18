UNAME := $(shell uname)
ifndef ARCH
  ARCH := $(shell uname -m)
endif

SRC = src
BIN = bin
OBJ = obj

DEBUG_OPTIMIZE = -O3 #-O0 -g

ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_ARCH = darwin x86_64
  PLATFORM_TAG = osx-x86_64
  PLATFORM_GENERAL_INCLUDES = -I/System/Library/Frameworks/JavaVM.framework/Headers
  PLATFORM_GENERAL_LINKER_OPTIONS = -framework Carbon
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  JNILIB_EXT=.jnilib
  STRIP_OPTIONS=-S -x
  RDYNAMIC=-rdynamic
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  PLATFORM_ARCH = linux x86_64
  PLATFORM_TAG = linux-x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  JNILIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
else ifeq ($(UNAME) $(ARCH), Linux armv6l)	# linux on Raspberry Pi
  PLATFORM_ARCH = linux arm
  PLATFORM_TAG = linux-armv6l
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  JNILIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
else ifeq ($(OS), Windows_NT)	# Windows
  PLATFORM_ARCH = windows $(ARCH)
  PLATFORM_TAG = win-$(ARCH)
  
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  EXE_EXT=.exe
  JNILIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
endif

JAVA_SOURCE_PATH = $(SRC)/java
JAVA_PLATFORM_SPECIFIC_SOURCE_PATH = $(SRC)/$(PLATFORM_TAG)/java
JAVA_CLASSPATH = $(BIN)/java/classes
CPP_SOURCE_PATH = $(SRC)/cpp
BINARY_PATH = $(BIN)/$(PLATFORM_TAG)
OBJECTS_PATH = $(OBJ)/$(PLATFORM_TAG)

LIBRARY_NAME = libcrossbase.a

JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

JAVA_PLATFORM_SPECIFIC_FILES = $(shell if [ -d "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" ]; then cd $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1'; fi)
JAVA_PLATFORM_SPECIFIC_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_PLATFORM_SPECIFIC_FILES))))

CPP_FILES = $(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
CPP_HEADER_FILES = $(addprefix $(CPP_SOURCE_PATH)/,$(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.h | awk '{ sub(/.\//,"") }; 1'))

CPP_OBJECTS := $(addprefix $(OBJECTS_PATH)/,$(addsuffix .o,$(basename $(CPP_FILES))))

SWT_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(shell "$(JAVA_HOME)/bin/jar" -tf lib/$(PLATFORM_TAG)/swt.jar | grep .class))
SWT_LIBS := $(addprefix $(BINARY_PATH)/,$(shell "$(JAVA_HOME)/bin/jar" -tf lib/$(PLATFORM_TAG)/swt.jar | grep $(JNILIB_EXT)))

all: $(BINARY_PATH)/$(LIBRARY_NAME) $(BIN)/java/crossbase.jar

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

$(BIN)/java/crossbase.jar: lib/java/classpath.jar $(JAVA_CLASSES) $(JAVA_PLATFORM_SPECIFIC_CLASSES) $(SWT_CLASSES)
	@echo Constructing $@...
	mkdir -p $(BINARY_PATH);

	# Making the java class library
	cp -f lib/java/classpath.jar $(BIN)/java/crossbase.jar; \
	( \
	    cd $(BIN)/java; \
	    "$(JAVA_HOME)/bin/jar" u0f crossbase.jar -C ../java/classes .; \
	)

$(SWT_CLASSES) $(SWT_LIBS): %:
	@echo Extracting SWT library...
	mkdir -p $(BINARY_PATH);
	mkdir -p $(BIN)/java/swt;
	mkdir -p $(BIN)/java/classes;
	# Extracting swt
	( \
	    cd $(BIN)/java/swt; \
	    "$(JAVA_HOME)/bin/jar" xf ../../../lib/$(PLATFORM_TAG)/swt.jar; \
	    cp -rf org ../classes/; \
	    cp -rf *$(JNILIB_EXT) ../../$(PLATFORM_TAG)/; \
	)

clean:
	@echo Cleaning all...
	rm -rf $(OBJ)
	rm -rf $(BIN)

.PHONY: all
.SILENT:
