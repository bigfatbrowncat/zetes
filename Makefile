UNAME := $(shell uname)
ARCH := $(shell uname -m)

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
  PLATFORM_ARCH = windows x86_64
  PLATFORM_TAG = win-x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = #-mconsole     # <-- Uncomment this for console app
  EXE_EXT=.exe
  JNILIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
endif

JAVA_SOURCE_PATH = $(SRC)/java
JAVA_CLASSPATH = $(BIN)/java/classes
CPP_SOURCE_PATH = $(SRC)/cpp
BINARY_PATH = $(BIN)/$(PLATFORM_TAG)
OBJECTS_PATH = $(OBJ)/$(PLATFORM_TAG)

JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

CPP_FILES = $(shell cd $(CPP_SOURCE_PATH); find . -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
CPP_OBJECTS := $(addprefix $(OBJECTS_PATH)/,$(addsuffix .o,$(basename $(CPP_FILES))))

SWT_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(shell "$(JAVA_HOME)/bin/jar" -tf lib/$(PLATFORM_TAG)/swt.jar | grep .class))
SWT_LIBS := $(addprefix $(BINARY_PATH)/,$(shell "$(JAVA_HOME)/bin/jar" -tf lib/$(PLATFORM_TAG)/swt.jar | grep $(JNILIB_EXT)))

all: $(BINARY_PATH)/crossbase

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java $(SWT_CLASSES)
	@echo Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp
	@echo Compiling $<...
	mkdir -p $(OBJECTS_PATH)
	g++ $(DEBUG_OPTIMIZE) -D_JNI_IMPLEMENTATION_ -c $(PLATFORM_GENERAL_INCLUDES) $< -o $@

$(BINARY_PATH)/crossbase: $(BIN)/java/boot.jar $(CPP_OBJECTS)
	@echo Linking $@...
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
	tools/$(PLATFORM_TAG)/binaryToObject $(BIN)/java/boot.jar $(OBJECTS_PATH)/boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM_ARCH); \
	g++ $(RDYNAMIC) $(DEBUG_OPTIMIZE) -Llib/$(PLATFORM_TAG) $(OBJECTS_PATH)/boot.jar.o $(CPP_OBJECTS) $(OBJ)/libavian/*.o $(PLATFORM_GENERAL_LINKER_OPTIONS) $(PLATFORM_CONSOLE_OPTION) -lm -lz -o $@
	strip -o $@$(EXE_EXT).tmp $(STRIP_OPTIONS) $@$(EXE_EXT) && mv $@$(EXE_EXT).tmp $@$(EXE_EXT) 

$(BIN)/java/boot.jar: lib/java/classpath.jar $(JAVA_CLASSES) $(SWT_CLASSES)
	@echo Constructing $@...
	mkdir -p $(BINARY_PATH);

	# Making the java class library
	cp -f lib/java/classpath.jar $(BIN)/java/boot.jar; \
	( \
	    cd $(BIN)/java; \
	    "$(JAVA_HOME)/bin/jar" u0f boot.jar -C ../java/classes .; \
	    "$(JAVA_HOME)/bin/jar" u0f boot.jar -C ../../src/res .; \
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
#.SILENT:
