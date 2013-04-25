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
  CLASSPATH_DELIM=:
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
  CLASSPATH_DELIM=:
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
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  EXE_EXT=.exe
  JNILIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
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

CROSSBASE_JNI_LIBS = $(shell cd $(CROSSBASE_PATH)/bin/$(PLATFORM_TAG); find . -name \*$(JNILIB_EXT) | awk '{ sub(/.\//,"") }; 1')
CROSSBASE_JNI_LIBS_TARGET = $(addprefix $(BINARY_PATH)/,$(addsuffix $(JNILIB_EXT),$(basename $(CROSSBASE_JNI_LIBS))))

ifeq ($(UNAME), Darwin)	# OS X
all: $(BINARY_PATH)/$(APPLICATION_NAME).app

$(BINARY_PATH)/$(APPLICATION_NAME).app: $(CROSSBASE_PATH)/osx-bundle/Contents/Info.plist $(BINARY_PATH)/$(BINARY_NAME) $(CROSSBASE_JNI_LIBS_TARGET)
	@echo Building OS X bundle...
	mkdir -p $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/MacOS
	cp -r $(CROSSBASE_PATH)/osx-bundle/* $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app
	cp $(BINARY_PATH)/$(BINARY_NAME) $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/MacOS
	cp $(CROSSBASE_JNI_LIBS_TARGET) $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/MacOS
	@echo Creating DMG image...
	hdiutil create $(BINARY_PATH)/$(BINARY_NAME)-darwin-universal.dmg -srcfolder $(BINARY_PATH)/$(APPLICATION_NAME) -ov

else
all: $(BINARY_PATH)/$(BINARY_NAME) $(CROSSBASE_JNI_LIBS_TARGET)
endif

$(CROSSBASE_JNI_LIBS_TARGET) : $(BINARY_PATH)/% : $(CROSSBASE_PATH)/bin/$(PLATFORM_TAG)/%
	@echo Copying $<...
	cp -f $< $@

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java $(CROSSBASE_PATH)/bin/java/crossbase.jar
	@echo Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)$(CLASSPATH_DELIM)$(CROSSBASE_PATH)/bin/java/crossbase.jar" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp
	@echo Compiling $<...
	mkdir -p $(OBJECTS_PATH)
	g++ $(DEBUG_OPTIMIZE) -D_JNI_IMPLEMENTATION_ -c $(PLATFORM_GENERAL_INCLUDES) $< -o $@

$(BINARY_PATH)/$(BINARY_NAME): $(BIN)/java/boot.jar $(CPP_OBJECTS)
	@echo Linking $@...
	mkdir -p $(BINARY_PATH);

	# Extracting libavian objects
	( \
	    cd $(OBJ); \
	    mkdir -p libcrossbase; \
	    cd libcrossbase; \
	    ar x ../../$(CROSSBASE_PATH)/bin/$(PLATFORM_TAG)/libcrossbase.a; \
	)

	mkdir -p $(BIN)/java

	# Making an object file from the java class library
	$(CROSSBASE_PATH)/tools/$(PLATFORM_TAG)/binaryToObject $(BIN)/java/boot.jar $(OBJECTS_PATH)/boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM_ARCH); \
	g++ $(RDYNAMIC) $(DEBUG_OPTIMIZE) -Llib/$(PLATFORM_TAG) $(OBJECTS_PATH)/boot.jar.o $(CPP_OBJECTS) $(OBJ)/libcrossbase/*.o $(PLATFORM_GENERAL_LINKER_OPTIONS) $(PLATFORM_CONSOLE_OPTION) -lm -lz -o $@
	strip -o $@$(EXE_EXT).tmp $(STRIP_OPTIONS) $@$(EXE_EXT) && mv $@$(EXE_EXT).tmp $@$(EXE_EXT) 

$(BIN)/java/boot.jar: $(CROSSBASE_PATH)/bin/java/crossbase.jar $(JAVA_CLASSES)
	@echo Constructing $@...
	mkdir -p $(BIN)/java;

	# Making the java class library
	cp -f $(CROSSBASE_PATH)/bin/java/crossbase.jar $(BIN)/java/boot.jar; \
	( \
	    cd $(BIN)/java; \
	    "$(JAVA_HOME)/bin/jar" u0f boot.jar -C ../java/classes .; \
	    "$(JAVA_HOME)/bin/jar" u0f boot.jar -C ../../src/res .; \
	)

clean:
	@echo Cleaning all...
	rm -rf $(OBJ)
	rm -rf $(BIN)

.PHONY: all
.SILENT:
