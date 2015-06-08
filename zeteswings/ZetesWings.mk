# This makefile should be included by every user of the ZetesWings library

# Attention! You should set ZETES_FEET_PATH to the zetesfeet library path and 
# ZETES_WINGS_PATH to the zeteswings library path

SRC = src
INCLUDE = include
BIN = bin
OBJ = obj
LIB = lib
RESOURCES = resources

DEBUG_OPTIMIZE = -O0  -g

ifeq ($(UNAME), Darwin)	# OS X
  JAVA_HOME = $(shell /usr/libexec/java_home)
  PLATFORM_ARCH = darwin x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -framework Cocoa -framework WebKit -framework CoreServices -framework JavaScriptCore -framework Security -framework SecurityInterface $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  SH_LIB_EXT=.so
  STRIP_OPTIONS=-S -x
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)/bundle/$(APPLICATION_NAME).app/Contents/Resources
else ifeq ($(UNAME), Linux)

  AVIAN_ARCH=$(ARCH)
ifeq ($(AVIAN_ARCH), armv6l)   # Raspberry Pi
    AVIAN_ARCH=arm
else ifeq ($(AVIAN_ARCH), armv7l)
    AVIAN_ARCH=arm
else ifeq ($(AVIAN_ARCH), i686)
    AVIAN_ARCH=i386
endif

  PLATFORM_ARCH = linux $(AVIAN_ARCH)
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl -lXtst -lGL -lX11 $(shell pkg-config --libs gtk+-2.0) $(CUSTOM_LIBS)
  PLATFORM_CONSOLE_OPTION = 
  SH_LIB_EXT=.so
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
  CLASSPATH_DELIM=:
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(OS) $(ARCH), Windows_NT i686)	# Windows 32-bit
  PLATFORM_ARCH = windows i386
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" $(CUSTOM_INCLUDES)
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -lshlwapi $(CUSTOM_LIBS) -mwindows -static-libgcc -static-libstdc++ $(OBJECTS_PATH)/win.res
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  SH_LIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64-bit
  PLATFORM_ARCH = windows x86_64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" $(CUSTOM_INCLUDES)
  
  # Basic Windows libraries
  WGUILIBS := -lkernel32 -ladvapi32 -luser32 -lgdi32 -lcomdlg32 -lwinspool
  WOLELIBS := -lole32 -luuid -loleaut32
  # Windows libraries used by SWT
  SWTLIBS := -lcomctl32 -lshell32 -limm32 -loleacc -lusp10 -lwininet -lcrypt32 -lshlwapi -lgdiplus -lopengl32

  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -lshlwapi $(WGUILIBS) $(WOLELIBS) $(SWTLIBS) $(CUSTOM_LIBS) -mwindows -static-libgcc -static-libstdc++ $(OBJECTS_PATH)/win.res
  PLATFORM_CONSOLE_OPTION = -mconsole     # <-- Uncomment this for console app
  SH_LIB_EXT=.dll
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
  CLASSPATH_DELIM=;
  RESOURCE_FILES_TARGET_PATH = $(BINARY_PATH)
endif

APP_ID = $(BINARY_NAME)

ZETES_FEET_INCLUDE = -I$(ZETES_FEET_PATH)/include
ZETES_WINGS_INCLUDE = -I$(ZETES_WINGS_PATH)/include
ZETES_INCLUDE = $(ZETES_FEET_INCLUDE) $(ZETES_WINGS_INCLUDE)

# Java platform agnostic
JAVA_SOURCE_PATH = $(SRC)/java
JAVA_RES_PATH = $(SRC)/res
JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))
JAVA_RES_FILES = $(shell if [ -d "$(JAVA_RES_PATH)" ]; then cd $(JAVA_RES_PATH); find . -type f -name \* | awk '{ sub(/.\//,"") }; 1'; fi)
JAVA_RES_FILES_TARGET = $(addprefix $(JAVA_CLASSPATH)/, $(JAVA_RES_FILES))

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
BUILD_CLASSPATHS = $(shell echo "$(JAVA_CLASSPATH)$(CLASSPATH_DELIM)$(ZETES_WINGS_PATH)/$(LIB)/java/$(JAVA_ZETES_WINGS_LIBRARY)$(CLASSPATH_DELIM)$(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY)$(CLASSPATH_DELIM)$(CUSTOM_JARS)" | awk 'gsub(/ +/, "$(CLASSPATH_DELIM)"); 1';)

ZETES_JNI_LIBS = \
    $(shell \
        if [ -d $(ZETES_WINGS_PATH)/$(BIN)/$(PLATFORM_TAG) ]; \
        then \
            cd $(ZETES_WINGS_PATH)/$(BIN)/$(PLATFORM_TAG)/; \
            find . -type f -name \*$(JNILIB_EXT) | awk '{ sub(/.\//,"") }; 1'; \
        fi \
    )
    
ZETES_JNI_LIBS_TARGET = $(addprefix $(BINARY_PATH)/,$(addsuffix $(JNILIB_EXT),$(basename $(ZETES_JNI_LIBS))))

# Copying shared and JNI libraries
SHARED_LIB_PATH = $(BIN)/$(PLATFORM_TAG)
SHARED_LIBS = $(shell if [ -d "$(SHARED_LIB_PATH)" ]; then cd $(SHARED_LIB_PATH); find . -type f -name \*$(SHLIB_EXT) | awk '{ sub(/.\//,"") }; 1'; fi)
JNI_LIBS = $(shell if [ -d "$(SHARED_LIB_PATH)" ]; then cd $(SHARED_LIB_PATH); find . -type f -name \*$(JNILIB_EXT) | awk '{ sub(/.\//,"") }; 1'; fi)
JUST_COPY_FILES = $(addprefix $(SHARED_LIB_PATH)/, $(SHARED_LIBS)) $(addprefix $(SHARED_LIB_PATH)/, $(JNI_LIBS))
include $(ZETES_PATH)/common-scripts/just_copy.mk

RESOURCE_FILES = $(shell if [ -d "$(RESOURCES)" ]; then cd $(RESOURCES); find . -type f -name \* | awk '{ sub(/.\//,"") }; 1'; fi)
RESOURCE_FILES_TARGET = $(addprefix $(RESOURCE_FILES_TARGET_PATH)/, $(RESOURCE_FILES))

JAVA_ZETES_WINGS_LIBRARY = zeteswings.jar
JAVA_ZETES_FEET_LIBRARY = zetesfeet.jar

ZETES_WINGS_LIBRARY = libzeteswings.a
ZETES_FEET_LIBRARY = libzetesfeet.a

ifeq ($(UNAME), Darwin)	# OS X
package: app
	@echo [$(APPLICATION_NAME)] Creating image $(TARGET)/package/$(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).dmg...
	mkdir -p $(TARGET)/package/$(APPLICATION_NAME)
	cp -rf $(BINARY_PATH)/* $(TARGET)/package/$(APPLICATION_NAME)
	hdiutil create $(TARGET)/package/$(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).dmg -srcfolder $(TARGET)/package/$(APPLICATION_NAME) -ov

app: $(BINARY_PATH)/$(APPLICATION_NAME).app

$(BINARY_PATH)/$(APPLICATION_NAME).app: osx-bundle/Contents/Info.plist $(BINARY_PATH)/$(BINARY_NAME) $(ZETES_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET) $(JUST_COPY_FILES_TARGET)
	@echo [$(APPLICATION_NAME)] Creating OS X bundle...
	mkdir -p $(BINARY_PATH)/bundle/$(APPLICATION_NAME).app/Contents/MacOS
	mkdir -p $(BINARY_PATH)/bundle/$(APPLICATION_NAME).app/Contents/Resources
	cp -r osx-bundle/* $(BINARY_PATH)/bundle/$(APPLICATION_NAME).app
	cp $(BINARY_PATH)/$(BINARY_NAME) $(BINARY_PATH)/bundle/$(APPLICATION_NAME).app/Contents/MacOS

else ifeq ($(OS), Windows_NT)	# Windows 

package: app
	@echo [$(APPLICATION_NAME)] Packaging archive $(TARGET)/package/$(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).zip...
	mkdir -p $(TARGET)/package/$(APPLICATION_NAME)
	cp -rf $(BINARY_PATH)/* $(TARGET)/package/$(APPLICATION_NAME)
	( \
	    cd $(TARGET)/package; \
	    zip -r $(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).zip $(APPLICATION_NAME); \
	)
		
app: $(BINARY_PATH)/$(BINARY_NAME) $(ZETES_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET) $(JUST_COPY_FILES_TARGET)

else

package: app
	@echo [$(APPLICATION_NAME)] Packaging archive $(TARGET)/package/$(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).tar.bz2...
	mkdir -p $(TARGET)/package/$(APPLICATION_NAME)
	cp -rf $(BINARY_PATH)/* $(TARGET)/package/$(APPLICATION_NAME)
	( \
	    cd $(TARGET)/package; \
		tar -cjf $(BINARY_NAME)-$(PLATFORM_TAG)-$(CLASSPATH).tar.bz2 $(APPLICATION_NAME); \
	)

app: $(BINARY_PATH)/$(BINARY_NAME) $(ZETES_JNI_LIBS_TARGET) $(RESOURCE_FILES_TARGET) $(JUST_COPY_FILES_TARGET)

endif

$(ZETES_JNI_LIBS_TARGET) : $(BINARY_PATH)/% : $(ZETES_WINGS_PATH)/$(BIN)/$(PLATFORM_TAG)/%
	@echo [$(APPLICATION_NAME)] Copying library $<...
	cp -f $< "$@"

$(RESOURCE_FILES_TARGET) : $(RESOURCE_FILES_TARGET_PATH)/% : $(RESOURCES)/%
	@echo [$(APPLICATION_NAME)] Copying resource file $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	cp -f $< "$@"

$(JAVA_RES_FILES_TARGET) : $(JAVA_CLASSPATH)/% : $(JAVA_RES_PATH)/%
	@echo [$(APPLICATION_NAME)] Copying Java resources $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	cp -f $< "$@"

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java $(ZETES_WINGS_PATH)/$(LIB)/java/$(JAVA_ZETES_WINGS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY)
	@echo [$(APPLICATION_NAME)] Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -encoding utf8 -sourcepath "$(JAVA_SOURCE_PATH)$(CLASSPATH_DELIM)$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(BUILD_CLASSPATHS)" -d "$(JAVA_CLASSPATH)" $<

$(JAVA_CLASSPATH)/%.class: $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)/%.java $(ZETES_WINGS_PATH)/$(LIB)/java/$(JAVA_ZETES_WINGS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY)
	@echo [$(APPLICATION_NAME)] Compiling platform specific $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -encoding utf8 -sourcepath "$(JAVA_SOURCE_PATH)$(CLASSPATH_DELIM)$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(BUILD_CLASSPATHS)" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp $(CPP_HEADER_FILES)
	@echo [$(APPLICATION_NAME)] Compiling $<...
	mkdir -p $(dir $@)
	g++ -std=c++0x $(DEBUG_OPTIMIZE) $(PIC) -D_JNI_IMPLEMENTATION_ -c $(PLATFORM_GENERAL_INCLUDES) -I$(INCLUDE) $(ZETES_INCLUDE) $< -o $@

$(BINARY_PATH)/$(BINARY_NAME): native-deps $(JAVA_OBJECTS_PATH)/boot.jar $(ZETES_WINGS_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_WINGS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_FEET_LIBRARY) $(CPP_OBJECTS)
	@echo [$(APPLICATION_NAME)] Linking $@...
	mkdir -p $(BINARY_PATH);
	mkdir -p $(OBJECTS_PATH);
	mkdir -p $(JAVA_OBJECTS_PATH)

	# Making an object file from the java class library
	$(ZETES_FEET_PATH)/tools/$(PLATFORM_TAG)/binaryToObject $(JAVA_OBJECTS_PATH)/boot.jar $(OBJECTS_PATH)/boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM_ARCH);
	
	# Making an object file from the entry point class name string
	echo $(ENTRY_CLASS) > $(OBJECTS_PATH)/entry.str
	$(ZETES_FEET_PATH)/tools/$(PLATFORM_TAG)/binaryToObject $(OBJECTS_PATH)/entry.str $(OBJECTS_PATH)/entry.str.o _boot_class_name_start _boot_class_name_end $(PLATFORM_ARCH);


	# Making an object file from the unique app id string
	echo $(APP_ID) > $(OBJECTS_PATH)/app_id.str
	$(ZETES_FEET_PATH)/tools/$(PLATFORM_TAG)/binaryToObject $(OBJECTS_PATH)/app_id.str $(OBJECTS_PATH)/app_id.str.o _app_id_start _app_id_end $(PLATFORM_ARCH);

	# Extracting libzetesfeet objects
	( \
	    set -e; \
	    cd $(OBJECTS_PATH); \
	    mkdir -p libzetesfeet; \
	    cd libzetesfeet; \
	    ar x $(CURDIR)/$(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_FEET_LIBRARY); \
	    ar t $(CURDIR)/$(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_FEET_LIBRARY) | awk '/.*\.o/ {print "$(OBJECTS_PATH)/libzetesfeet/"$$0}' > liblistpath.txt; \
	)

	# Extracting libzeteswings objects
	( \
	    set -e; \
	    cd $(OBJECTS_PATH); \
	    mkdir -p libzeteswings; \
	    cd libzeteswings; \
	    ar x $(CURDIR)/$(ZETES_WINGS_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_WINGS_LIBRARY); \
	    ar t $(CURDIR)/$(ZETES_WINGS_PATH)/$(LIB)/$(PLATFORM_TAG)/$(ZETES_WINGS_LIBRARY) | awk '/.*\.o/ {print "$(OBJECTS_PATH)/libzeteswings/"$$0}' > liblistpath.txt; \
	)

	# Prepending path
	awk '/.+/ {print "$(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/"$$0}' $(ZETES_FEET_PATH)/$(LIB)/$(PLATFORM_TAG)/liblist.txt > $(OBJECTS_PATH)/liblistpath.txt

ifeq ($(OS), Windows_NT)	# Windows 32-bit
	# Compiling resources
	windres win-res/win.rc -O coff -o $(OBJECTS_PATH)/win.res
endif
	
	# Linking the target
	g++ $(RDYNAMIC) $(DEBUG_OPTIMIZE) -Llib/$(PLATFORM_TAG) $(CPP_OBJECTS) \
	           @$(OBJECTS_PATH)/libzetesfeet/liblistpath.txt \
	           @$(OBJECTS_PATH)/libzeteswings/liblistpath.txt \
	           @$(OBJECTS_PATH)/liblistpath.txt \
	           $(OBJECTS_PATH)/boot.jar.o \
	           $(OBJECTS_PATH)/entry.str.o \
	           $(OBJECTS_PATH)/app_id.str.o \
			   $(PLATFORM_GENERAL_LINKER_OPTIONS) $(PLATFORM_CONSOLE_OPTION) \
			   -lm -lz -o $@
	strip -o $@$(EXE_EXT).tmp $(STRIP_OPTIONS) $@$(EXE_EXT) && mv $@$(EXE_EXT).tmp $@$(EXE_EXT) 

	#g++ -mdll $(DEBUG_OPTIMIZE) -Llib/$(PLATFORM_TAG) $(CPP_OBJECTS) \
	#           @$(OBJECTS_PATH)/libzetesfeet/liblistpath.txt \
	#           @$(OBJECTS_PATH)/libzeteswings/liblistpath.txt \
	#           @$(OBJECTS_PATH)/liblistpath.txt \
	#           $(OBJECTS_PATH)/boot.jar.o \
	#           $(OBJECTS_PATH)/entry.str.o \
	#           $(OBJECTS_PATH)/app_id.str.o \
	#		   $(PLATFORM_GENERAL_LINKER_OPTIONS) $(PLATFORM_CONSOLE_OPTION) \
	#		   -lm -lz -o $@.dll
	#strip -o $@.dll.tmp $(STRIP_OPTIONS) $@.dll && mv $@.dll.tmp $@.dll 

$(JAVA_OBJECTS_PATH)/classpath.jar: $(ZETES_WINGS_PATH)/$(LIB)/java/$(JAVA_ZETES_WINGS_LIBRARY) $(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY) $(CUSTOM_JARS)
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
	    "$(JAVA_HOME)/bin/jar" xf $(CURDIR)/$(ZETES_WINGS_PATH)/$(LIB)/java/$(JAVA_ZETES_WINGS_LIBRARY); \
	    "$(JAVA_HOME)/bin/jar" xf $(CURDIR)/$(ZETES_FEET_PATH)/$(LIB)/java/$(JAVA_ZETES_FEET_LIBRARY); \
	    cd $(CURDIR)/$(JAVA_OBJECTS_PATH); \
	    "$(JAVA_HOME)/bin/jar" cf classpath.jar -C $(CURDIR)/$(JAVA_CLASSPATH_EXTERNAL) .; \
	)
	
$(JAVA_OBJECTS_PATH)/boot.jar: $(JAVA_OBJECTS_PATH)/classpath.jar $(JAVA_CLASSES) $(JAVA_PLATFORM_SPECIFIC_CLASSES) $(JAVA_RES_FILES_TARGET)
	@echo [$(APPLICATION_NAME)] Constructing $@...
	mkdir -p $(JAVA_CLASSPATH);

	# Making the java class library
	( \
	    set -e; \
	    cd $(CURDIR)/$(JAVA_OBJECTS_PATH); \
		cp -f classpath.jar boot.jar; \
	    "$(JAVA_HOME)/bin/jar" uf boot.jar -C $(CURDIR)/$(JAVA_CLASSPATH) .; \
	)

clean:
	@echo [$(APPLICATION_NAME)] Cleaning all...
	rm -rf $(TARGET)

native-deps::
	@echo [$(APPLICATION_NAME)] Building custom native dependencies...

.PHONY: package clean native-deps
#.SILENT:
