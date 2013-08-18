UNAME := $(shell uname)
ifndef ARCH
  ARCH := $(shell uname -m)
endif

SRC = src
INCLUDE = include
BIN = bin
OBJ = obj
RESOURCES = resources

PWD = $(shell pwd)

DEBUG_OPTIMIZE = -O0  -g



ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_ARCH = darwin x86_64
  PLATFORM_TAG = osx-x86_64
  PLATFORM_GENERAL_INCLUDES = -I/System/Library/Frameworks/JavaVM.framework/Headers $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -framework Carbon $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  JNILIB_EXT=.jnilib
  STRIP_OPTIONS=-S -x
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/Resources
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  PLATFORM_ARCH = linux x86_64
  PLATFORM_TAG = linux-x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  JNILIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(UNAME) $(ARCH), Linux armv6l)	# linux on Raspberry Pi
  PLATFORM_ARCH = linux arm
  PLATFORM_TAG = linux-armv6l
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  JNILIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(OS) $(ARCH), Windows_NT i686)	# Windows 32-bit
  PLATFORM_ARCH = windows i386
  PLATFORM_TAG = win-i386
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 $(CUSTOM_LIBS) -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  EXE_EXT=.exe
  JNILIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64-bit
  PLATFORM_ARCH = windows x86_64
  PLATFORM_TAG = win-x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 $(CUSTOM_LIBS) -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  EXE_EXT=.exe
  JNILIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
endif

JAVA_SOURCE_PATH = $(SRC)/java
JAVA_PLATFORM_SPECIFIC_SOURCE_PATH = $(SRC)/$(PLATFORM_TAG)/java
JAVA_CLASSPATH = $(BIN)/java/classes
CPP_SOURCE_PATH = $(SRC)/cpp
BINARY_PATH = $(BIN)/$(PLATFORM_TAG)
OBJECTS_PATH = $(OBJ)/$(PLATFORM_TAG)

JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

JAVA_PLATFORM_SPECIFIC_FILES = $(shell if [ -d "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" ]; then cd $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1'; fi)
JAVA_PLATFORM_SPECIFIC_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_PLATFORM_SPECIFIC_FILES))))

CUSTOM_JARS =  $(shell if [ -d "lib/java" ]; then find lib/java -name \*.jar; fi)
BUILD_CLASSPATHS = $(shell echo "$(JAVA_CLASSPATH)$(CLASSPATH_DELIM)$(CROSSBASE_PATH)/bin/java/crossbase.jar$(CLASSPATH_DELIM)$(CUSTOM_JARS)" | awk 'gsub(/ +/, "$(CLASSPATH_DELIM)"); 1';)

CPP_FILES = $(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
CPP_HEADER_FILES = $(addprefix $(CPP_SOURCE_PATH)/,$(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.h | awk '{ sub(/.\//,"") }; 1'))
CPP_OBJECTS := $(addprefix $(OBJECTS_PATH)/,$(addsuffix .o,$(basename $(CPP_FILES))))

CROSSBASE_JNI_LIBS = $(shell cd $(CROSSBASE_PATH)/bin/$(PLATFORM_TAG); find . -type f -name \*$(JNILIB_EXT) | awk '{ sub(/.\//,"") }; 1')
CROSSBASE_JNI_LIBS_TARGET = $(addprefix $(BINARY_PATH)/,$(addsuffix $(JNILIB_EXT),$(basename $(CROSSBASE_JNI_LIBS))))

RESOURCE_FILES = $(shell if [ -d "$(RESOURCES)" ]; then cd $(RESOURCES); find . -type f -name \* | awk '{ sub(/.\//,"") }; 1'; fi)
RESOURCE_FILES_TARGET = $(addprefix $(RESOURCE_FILES_TARGET_PATH)/, $(RESOURCE_FILES))

CROSSBASE_INCLUDE = $(CROSSBASE_PATH)/include


ifeq ($(UNAME), Darwin)	# OS X
package: $(BINARY_PATH)/$(APPLICATION_NAME).app

$(BINARY_PATH)/$(APPLICATION_NAME).app: osx-bundle/Contents/Info.plist $(BINARY_PATH)/$(BINARY_NAME) $(CROSSBASE_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET)
	@echo Building OS X bundle...
	mkdir -p $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/MacOS
	mkdir -p $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/Resources
	cp -r osx-bundle/* $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app
	cp $(BINARY_PATH)/$(BINARY_NAME) $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/MacOS
	cp $(CROSSBASE_JNI_LIBS_TARGET) $(BINARY_PATH)/$(APPLICATION_NAME)/$(APPLICATION_NAME).app/Contents/MacOS
	@echo Creating DMG image...
	hdiutil create $(BINARY_PATH)/$(BINARY_NAME)-darwin-universal.dmg -srcfolder $(BINARY_PATH)/$(APPLICATION_NAME) -ov

else
package: $(BINARY_PATH)/$(BINARY_NAME) $(CROSSBASE_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET)
endif

$(CROSSBASE_JNI_LIBS_TARGET) : $(BINARY_PATH)/% : $(CROSSBASE_PATH)/bin/$(PLATFORM_TAG)/%
	@echo Copying crossbase library $<...
	cp -f $< $@

$(RESOURCE_FILES_TARGET) : $(RESOURCE_FILES_TARGET_PATH)/% : $(RESOURCES)/%
	@echo Copying resource file $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	cp -f $< $@

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java $(CROSSBASE_PATH)/bin/java/crossbase.jar
	@echo Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_SOURCE_PATH)$(CLASSPATH_DELIM)$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(BUILD_CLASSPATHS)" -d "$(JAVA_CLASSPATH)" $<

$(JAVA_CLASSPATH)/%.class: $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)/%.java $(CROSSBASE_PATH)/bin/java/crossbase.jar
	@echo Compiling platform specific $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_SOURCE_PATH)$(CLASSPATH_DELIM)$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(BUILD_CLASSPATHS)" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp $(CPP_HEADER_FILES)
	@echo Compiling $<...
	mkdir -p $(dir $@)
	g++ $(DEBUG_OPTIMIZE) -D_JNI_IMPLEMENTATION_ -c $(PLATFORM_GENERAL_INCLUDES) -I$(INCLUDE) -I$(CROSSBASE_INCLUDE) $< -o $@

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

$(BIN)/java/boot.jar: $(CROSSBASE_PATH)/bin/java/crossbase.jar $(JAVA_CLASSES) $(JAVA_PLATFORM_SPECIFIC_CLASSES) $(CUSTOM_JARS)
	@echo Constructing $@...
	mkdir -p $(BIN)/java/classes;

	# Extracting custom jars
	for cust_jar in $(CUSTOM_JARS); do \
	    echo Extracting $$cust_jar...; \
	    (cd $(BIN)/java/classes; "$(JAVA_HOME)/bin/jar" xvf $(PWD)/$$cust_jar ); \
	done
	
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

.PHONY: package
.SILENT:
