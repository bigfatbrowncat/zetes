APPLICATION_NAME = Zetes

include common-scripts/globals.mk

PACKAGE_NAME = zetes-$(PLATFORM_TAG)-$(CLASSPATH)-`git describe`

all: zetes package

zetes: zetesfeet zeteswings zeteshands

zetesfeet:
	$(MAKE) all -C zetesfeet
	
ifeq ($(BUILD_WINGS), 1)
zeteswings: zetesfeet
	$(MAKE) all -C zeteswings
endif

zeteshands: zetesfeet
	$(MAKE) all -C zeteshands

clean:
	$(MAKE) clean -C zetesfeet
ifeq ($(BUILD_WINGS), 1)                 
	$(MAKE) clean -C zeteswings
endif
	$(MAKE) clean -C zeteshands
	rm -f $(PACKAGE_NAME).tar.bz2
	
package: zetes
	@echo [$(APPLICATION_NAME)] Copying all built files to a temporary folder...
	mkdir -p $(PACKAGE_NAME)/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)/
ifeq ($(BUILD_WINGS), 1)                 
	mkdir -p $(PACKAGE_NAME)/zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH)/
endif
	mkdir -p $(PACKAGE_NAME)/zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH)/
	mkdir -p $(PACKAGE_NAME)/common-scripts
	cp -rf zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH) $(PACKAGE_NAME)/zetesfeet/
ifeq ($(BUILD_WINGS), 1)                 
	cp -rf zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH) $(PACKAGE_NAME)/zeteswings/
endif
	cp -rf zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH) $(PACKAGE_NAME)/zeteshands/
	cp -rf common-scripts/* $(PACKAGE_NAME)/common-scripts/
	@echo [$(APPLICATION_NAME)] Removing unnecessary files from the temporary folder...
	rm -rf $(PACKAGE_NAME)/zetesfeet/target-$(PLATFORM_TAG)-$(CLASSPATH)/obj
ifeq ($(BUILD_WINGS), 1)                 
	rm -rf $(PACKAGE_NAME)/zeteswings/target-$(PLATFORM_TAG)-$(CLASSPATH)/obj
endif
	rm -rf $(PACKAGE_NAME)/zeteshands/target-$(PLATFORM_TAG)-$(CLASSPATH)/obj
ifeq ($(OS), Windows_NT)	# Windows 32-bit
	@echo [$(APPLICATION_NAME)] Archiving the package $(PACKAGE_NAME).zip...
	( \
	    cd $(PACKAGE_NAME); \
	    zip -r ../$(PACKAGE_NAME).zip *;\
	)
else
	@echo [$(APPLICATION_NAME)] Archiving the package $(PACKAGE_NAME).tar.bz2...
	( \
	    cd $(PACKAGE_NAME); \
	    tar -cjf ../$(PACKAGE_NAME).tar.bz2 *;\
	)
endif
	rm -rf $(PACKAGE_NAME)
	
.PHONY: all zetes zeteswings zetesfeet zeteshands package clean
.SILENT:
