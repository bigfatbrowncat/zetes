UNAME := $(shell uname)
ARCH := $(shell uname -m)

SRC = src
BIN = bin
OBJ = obj

JAVA_SOURCE_PATH = $(SRC)/java
JAVA_CLASSPATH = $(BIN)/java
CPP_SOURCE_PATH = $(SRC)/cpp
OBJECTS = $(OBJ)


DEBUG_OPTIMIZE = -O3 #-O0 -g

ifeq ($(UNAME), Darwin)	# OS X
  PLATFORM_ARCH = darwin x86_64
  PLATFORM_LIBS = osx
  PLATFORM_GENERAL_INCLUDES = -I/System/Library/Frameworks/JavaVM.framework/Headers
  PLATFORM_GENERAL_LINKER_OPTIONS = -framework Carbon
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  STRIP_OPTIONS=-S -x
  RDYNAMIC=-rdynamic
else ifeq ($(UNAME) $(ARCH), Linux armv6l)	# linux on raspberry pi
  PLATFORM_ARCH = linux arm
  PLATFORM_LIBS = linux-arm
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
  PLATFORM_GENERAL_LINKER_OPTIONS = -lpthread -ldl
  PLATFORM_CONSOLE_OPTION = 
  EXE_EXT=
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=-rdynamic
else ifeq ($(OS), Windows_NT)	# Windows
  PLATFORM_ARCH = windows x86_64
  PLATFORM_LIBS = win
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
  PLATFORM_GENERAL_LINKER_OPTIONS = -static -lmingw32 -lmingwthrd -lws2_32 -mwindows -static-libgcc -static-libstdc++
  PLATFORM_CONSOLE_OPTION = -mconsole
  EXE_EXT=.exe
  STRIP_OPTIONS=--strip-all
  RDYNAMIC=
endif

JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES := $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

CPP_FILES = $(shell cd $(CPP_SOURCE_PATH); find . -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
CPP_OBJECTS := $(addprefix $(OBJECTS)/,$(addsuffix .o,$(basename $(CPP_FILES))))

all: $(BIN)/crossbase

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java
	@echo $(PLATFORM_GENERAL_INCLUDES)
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -sourcepath "$(JAVA_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)" -d "$(JAVA_CLASSPATH)" $<

$(OBJ)/%.o: $(SRC)/cpp/%.cpp
	@echo $(PLATFORM_GENERAL_INCLUDES)
	mkdir -p $(OBJ)
	g++ $(DEBUG_OPTIMIZE) -D_JNI_IMPLEMENTATION_ -c $(PLATFORM_GENERAL_INCLUDES) $< -o $@

$(BIN)/crossbase: $(JAVA_CLASSES) $(CPP_OBJECTS)
	mkdir -p $(BIN);
	@echo $(PLATFORM_GENERAL_INCLUDES)

	# Extracting libavian objects
	( \
	    cd $(OBJ); \
	    mkdir -p libavian; \
	    cd libavian; \
	    ar x ../../lib/$(PLATFORM_LIBS)/libavian.a; \
	)

	# Making the java class library
	cp lib/java/classpath.jar $(BIN)/boot.jar; \
	( \
	    cd $(BIN); \
	    "$(JAVA_HOME)/bin/jar" u0f boot.jar -C java .; \
	)

	# Making an object file from the java class library
	tools/$(PLATFORM_LIBS)/binaryToObject $(BIN)/boot.jar $(OBJ)/boot.jar.o _binary_boot_jar_start _binary_boot_jar_end $(PLATFORM_ARCH); \
	g++ $(RDYNAMIC) $(DEBUG_OPTIMIZE) -Llib/$(PLATFORM_LIBS) $(OBJ)/boot.jar.o $(CPP_OBJECTS) $(OBJ)/libavian/*.o $(PLATFORM_GENERAL_LINKER_OPTIONS) $(PLATFORM_CONSOLE_OPTION) -lm -lz -o $@
	strip $(STRIP_OPTIONS) $@$(EXE_EXT)


clean:
	rm -rf $(OBJ)
	rm -rf $(BIN)

.PHONY: all
