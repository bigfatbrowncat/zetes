ZETES_FEET_PATH = ../target
ZETES_WINGS_PATH = ../target

APPLICATION_NAME = GLDemo
BINARY_NAME = gltest

TARGET = target

UNAME := $(shell uname)

ifeq ($(UNAME), Darwin)	# OS X
  CUSTOM_INCLUDES = -I/System/Library/Frameworks/OpenGL.framework/Headers
  CUSTOM_LIBS = -framework OpenGL lib/$(PLATFORM_TAG)/libpng16.a
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_LIBS = -lopengl32 -lglu32 
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_LIBS = -lopengl32 -lglu32 lib/$(PLATFORM_TAG)/libpng16.a
endif

all: package

include $(ZETES_WINGS_PATH)/ZetesWings.mk