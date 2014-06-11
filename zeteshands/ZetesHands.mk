# This makefile should be included by every user of the ZetesFeet library

# Attention! You should set ZETES_FEET_PATH to the zetesfeet library path and 
# ZETES_HANDS_PATH to the zeteshands library path

SRC = src
INCLUDE = include
BIN = bin
OBJ = obj
LIB = lib
RESOURCES = resources

DEBUG_OPTIMIZE = -O0  -g

ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_ARCH = darwin x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -framework Cocoa $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  SH_LIB_EXT=.so
  STRIP_OPTIONS=-S -x
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  PLATFORM_ARCH = linux x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  SH_LIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(UNAME) $(ARCH), Linux armv6l)	# linux on Raspberry Pi
  PLATFORM_ARCH = linux arm
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  SH_LIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(OS) $(ARCH), Windows_NT i686)	# Windows 32-bit
  PLATFORM_ARCH = windows i386
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -lshlwapi $(CUSTOM_LIBS) -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  SH_LIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64-bit
  PLATFORM_ARCH = windows x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -lshlwapi $(CUSTOM_LIBS) -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  SH_LIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
endif

ZETES_FEET_INCLUDE = -I$(ZETES_FEET_PATH)/include
ZETES_HANDS_INCLUDE = -I$(ZETES_HANDS_PATH)/include
ZETES_INCLUDE = $(ZETES_FEET_INCLUDE) $(ZETES_HANDS_INCLUDE)

# Java platform agnostic
JAVA_SOURCE_PATH = $(SRC)/java
RES_PATH = $(SRC)/res
JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

# Java platform specific
JAVA_PLATFORM_SPECIFIC_SOURCE_PATH = $(SRC)/$(PLATFORM_TAG)/java
JAVA_PLATFORM_SPECIFIC_FILES = $(shell if [ -d "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" ]; then cd $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1'; fi)
JAVA_PLATFORM_SPECIFIC_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_PLATFORM_SPECIFIC_FILES))))

# C++ Platform agnostic
CPP_SOURCE_PATH = $(SRC)/cpp
CPP_FILES = $(shell if [ -d "$(CPP_SOURCE_PATH)" ]; then cd $(CPP_SOURCE_PATH); find . -type f -name \*.cpp | awk '{ sub(/.\//,"") }; 1'; fi)
CPP_HEADER_FILES = $(addprefix $(CPP_SOURCE_PATH)/,$(shell if [ -d "$(CPP_SOURCE_PATH)" ]; then cd $(CPP_SOURCE_PATH); find . -type f -name \*.h | awk '{ sub(/.\//,"") }; 1'; fi))
CPP_OBJECTS = $(addprefix $(OBJECTS_PATH)/,$(addsuffix .o,$(basename $(CPP_FILES))))

# Target paths
BINARY_PATH = $(TARGET)/$(BIN)/$(PLATFORM_TAG)
LIBRARY_PATH = $(TARGET)/$(LIB)/$(PLATFORM_TAG)
OBJECTS_PATH = $(TARGET)/$(OBJ)/$(PLATFORM_TAG)
JAVA_BINARY_PATH = $(TARGET)/$(BIN)/java
JAVA_LIBRARY_PATH = $(TARGET)/$(LIB)/java
JAVA_OBJECTS_PATH = $(TARGET)/$(OBJ)/java
JAVA_CLASSPATH = $(JAVA_OBJECTS_PATH)/classes
JAVA_CLASSPATH_EXTERNAL = $(JAVA_OBJECTS_PATH)/classes_ext

CUSTOM_JARS =  $(shell if [ -d "lib/java" ]; then find lib/java -name \*.jar; fi)
BUILD_CLASSPATHS = $(shell echo "$(JAVA_CLASSPATH)$(CLASSPATH_DELIM)$(ZETES_HANDS_PATH)/$(LIB)/java/$(JAVA_ZETES_HANDS_LIBRARY)$(CLASSPATH_DELIM)$(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY)$(CLASSPATH_DELIM)$(CUSTOM_JARS)" | awk 'gsub(/ +/, "$(CLASSPATH_DELIM)"); 1';)

ZETES_JNI_LIBS = \
    $(shell \
        if [ -d $(ZETES_HANDS_PATH)/$(BIN)/$(PLATFORM_TAG) ]; \
        then \
            cd $(ZETES_HANDS_PATH)/$(BIN)/$(PLATFORM_TAG)/; \
            find . -type f -name \*$(JNILIB_EXT) | awk '{ sub(/.\//,"") }; 1'; \
        fi \
    )
    
ZETES_JNI_LIBS_TARGET = $(addprefix $(BINARY_PATH)/,$(addsuffix $(JNILIB_EXT),$(basename $(ZETES_JNI_LIBS))))

RESOURCE_FILES = $(shell if [ -d "$(RESOURCES)" ]; then cd $(RESOURCES); find . -type f -name \* | awk '{ sub(/.\//,"") }; 1'; fi)
RESOURCE_FILES_TARGET = $(addprefix $(RESOURCE_FILES_TARGET_PATH)/, $(RESOURCE_FILES))

JAVA_ZETES_HANDS_LIBRARY = zeteshands.jar
JAVA_ZETES_FEET_LIBRARY = zetesfeet.jar

ZETES_HANDS_LIBRARY = libzeteshands.a
ZETES_FEET_LIBRARY = libzetesfeet.a

ifeq ($(OS), Windows_NT)	# Windows 

package: app
	@echo [$(APPLICATION_NAME)] Packaging zip archive for Windows...
	mkdir -p $(TARGET)/package/$(APPLICATION_NAME)
	cp -rf $(BINARY_PATH)/* $(TARGET)/package/$(APPLICATION_NAME)
	( \
	    cd $(TARGET)/package; \
	    zip -r $(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).zip $(APPLICATION_NAME); \
	)
		
app: $(BINARY_PATH)/$(BINARY_NAME) $(ZETES_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET)

else

package: app
	@echo [$(APPLICATION_NAME)] Packaging tar.bz2 archive...
	mkdir -p $(TARGET)/package/$(APPLICATION_NAME)
	cp -rf $(BINARY_PATH)/* $(TARGET)/package/$(APPLICATION_NAME)
	( \
	    cd $(TARGET)/package; \
		tar -cjf $(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).tar.bz2 $(APPLICATION_NAME); \
	)

app: $(BINARY_PATH)/$(BINARY_NAME) $(ZETES_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET)

endif

app: $(BINARY_PATH)/$(BINARY_NAME) $(ZETES_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET)

$(ZETES_JNI_LIBS_TARGET) : $(BINARY_PATH)/% : $(ZETES_HANDS_PATH)/$(BIN)/$(PLATFORM_TAG)/%
	@echo [$(APPLICATION_NAME)] Copying library $<...
	cp -f $< "$@"

$(RESOURCE_FILES_TARGET) : $(RESOURCE_FILES_TARGET_PATH)/% : $(RESOURCES)/%
	@echo [$(APPLICATION_NAME)] Copying resource file $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	cp -f $< $@

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java $(ZETES_HANDS_PATH)/$(LIB)/java/$(JAVA_ZETES_HANDS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY)
	@echo [$(APPLICATION_NAME)] Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -encoding utf8 -sourcepath "$(JAVA_SOURCE_PATH)$(CLASSPATH_DELIM)$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(BUILD_CLASSPATHS)" -d "$(JAVA_CLASSPATH)" $<

$(JAVA_CLASSPATH)/%.class: $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)/%.java $(ZETES_HANDS_PATH)/$(LIB)/java/$(JAVA_ZETES_HANDS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY)
	@echo [$(APPLICATION_NAME)] Compiling platform specific $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -encoding utf8 -sourcepath "$(JAVA_SOURCE_PATH)$(CLASSPATH_DELIM)$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(BUILD_CLASSPATHS)" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp $(CPP_HEADER_FILES)
	@echo [$(APPLICATION_NAME)] Compiling $<...
	mkdir -p $(dir $@)
	g++ $(DEBUG_OPTIMIZE) $(PIC) -D_JNI_IMPLEMENTATION_ -c $(PLATFORM_GENERAL_INCLUDES) -I$(INCLUDE) $(ZETES_INCLUDE) $< -o $@

$(BINARY_PATH)/$(BINARY_NAME): $(JAVA_OBJECTS_PATH)/boot.jar $(ZETES_HANDS_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_HANDS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_FEET_LIBRARY) $(CPP_OBJECTS)
	@echo [$(APPLICATION_NAME)] Linking $@...
	mkdir -p $(BINARY_PATH);
	mkdir -p $(OBJECTS_PATH);
	mkdir -p $(JAVA_OBJECTS_PATH)

	# Making an object file from the java class library
	$(ZETES_FEET_PATH)/tools/$(PLATFORM_TAG)/binaryToObject $(JAVA_OBJECTS_PATH)/boot.jar $(OBJECTS_PATH)/boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM_ARCH);
	
	# Making an object file from the entry point class name string
	echo $(ENTRY_CLASS) > $(OBJECTS_PATH)/entry.str
	$(ZETES_FEET_PATH)/tools/$(PLATFORM_TAG)/binaryToObject $(OBJECTS_PATH)/entry.str $(OBJECTS_PATH)/entry.str.o _boot_class_name_start _boot_class_name_end $(PLATFORM_ARCH);

	# Extracting libzetesfeet objects
	( \
	    set -e; \
	    cd $(OBJECTS_PATH); \
	    mkdir -p libzetesfeet; \
	    cd libzetesfeet; \
	    ar x $(CURDIR)/$(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_FEET_LIBRARY); \
	    ar t $(CURDIR)/$(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_FEET_LIBRARY) | awk '/.*\.o/ {print "$(OBJECTS_PATH)/libzetesfeet/"$$0}' > liblistpath.txt; \
	)
	
	# Extracting libzeteshands objects
	( \
	    set -e; \
	    cd $(OBJECTS_PATH); \
	    mkdir -p libzeteshands; \
	    cd libzeteshands; \
	    ar x $(CURDIR)/$(ZETES_HANDS_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_HANDS_LIBRARY); \
	    ar t $(CURDIR)/$(ZETES_HANDS_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_HANDS_LIBRARY) | awk '/.*\.o/ {print "$(OBJECTS_PATH)/libzeteshands/"$$0}' > liblistpath.txt; \
	)

	# Prepending path
	awk '/.+/ {print "$(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/"$$0}' $(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/liblist.txt > $(OBJECTS_PATH)/liblistpath.txt
	
	# Linking the target
	g++ $(RDYNAMIC) $(DEBUG_OPTIMIZE) -Llib/$(PLATFORM_TAG) $(CPP_OBJECTS) \
	           @$(OBJECTS_PATH)/libzetesfeet/liblistpath.txt \
	           @$(OBJECTS_PATH)/libzeteshands/liblistpath.txt \
	           @$(OBJECTS_PATH)/liblistpath.txt \
	           $(OBJECTS_PATH)/boot.jar.o \
	           $(OBJECTS_PATH)/entry.str.o \
	           $(PLATFORM_GENERAL_LINKER_OPTIONS) $(PLATFORM_CONSOLE_OPTION) -lm -lz -o $@
	strip -o $@$(EXE_EXT).tmp $(STRIP_OPTIONS) $@$(EXE_EXT) && mv $@$(EXE_EXT).tmp $@$(EXE_EXT) 

$(JAVA_OBJECTS_PATH)/classpath.jar: $(ZETES_HANDS_PATH)/$(LIB)/java/$(JAVA_ZETES_HANDS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY) $(CUSTOM_JARS)
	@echo [$(APPLICATION_NAME)] Constructing $@...
	mkdir -p $(JAVA_CLASSPATH_EXTERNAL);

	# Extracting custom jars
	for cust_jar in $(CUSTOM_JARS); do \
	    echo [$(APPLICATION_NAME)] Extracting $$cust_jar...; \
	    (cd $(JAVA_CLASSPATH_EXTERNAL); "$(JAVA_HOME)/bin/jar" xvf $(CURDIR)/$$cust_jar ); \
	done
	
	# Making the java class library
	( \
	    set -e; \
	    cd $(CURDIR)/$(JAVA_CLASSPATH_EXTERNAL); \
	    "$(JAVA_HOME)/bin/jar" xf $(CURDIR)/$(ZETES_HANDS_PATH)/$(LIB)/java/$(JAVA_ZETES_HANDS_LIBRARY); \
	    "$(JAVA_HOME)/bin/jar" xf $(CURDIR)/$(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY); \
	    cd $(CURDIR)/$(JAVA_OBJECTS_PATH); \
	    "$(JAVA_HOME)/bin/jar" cf classpath.jar -C $(CURDIR)/$(JAVA_CLASSPATH_EXTERNAL) .; \
	)
	
$(JAVA_OBJECTS_PATH)/boot.jar: $(JAVA_OBJECTS_PATH)/classpath.jar $(JAVA_CLASSES) $(JAVA_PLATFORM_SPECIFIC_CLASSES)
	@echo [$(APPLICATION_NAME)] Constructing $@...
	mkdir -p $(JAVA_CLASSPATH);

	# Making the java class library
	( \
	    set -e; \
	    cd $(CURDIR)/$(JAVA_OBJECTS_PATH); \
		cp -f classpath.jar boot.jar; \
	    "$(JAVA_HOME)/bin/jar" uf boot.jar -C $(CURDIR)/$(JAVA_CLASSPATH) .; \
	    "$(JAVA_HOME)/bin/jar" uf boot.jar -C $(CURDIR)/$(RES_PATH) .; \
	)

clean:
	@echo [$(APPLICATION_NAME)] Cleaning all...
	rm -rf $(TARGET)

.PHONY: package clean
.SILENT:
