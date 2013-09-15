ZETES_FEET_PATH = ../zetesfeet
ZETES_HANDS_PATH = ../zeteshands

APPLICATION_NAME = BellardPI
BINARY_NAME = bellardpi

UNAME := $(shell uname)

ifeq ($(UNAME), Darwin)	# OS X
  CUSTOM_INCLUDES = 
  CUSTOM_LIBS = 
else ifeq ($(UNAME) $(ARCH), Linux x86_64)	# linux on PC
  CUSTOM_LIBS =  
else ifeq ($(OS), Windows_NT)	# Windows
  CUSTOM_LIBS = 
endif

all: package

include $(ZETES_HANDS_PATH)/User.mk