APPLICATION_NAME = Zetes
PACKAGE_NAME = zetes-$(CLASSPATH)-`git describe`

all: zetes package

zetes: zetesfeet zeteswings zeteshands

zetesfeet:
	$(MAKE) all -C zetesfeet
	
zeteswings: zetesfeet
	$(MAKE) ZETES_FEET_PATH=../zetesfeet/target-$(CLASSPATH) all -C zeteswings

zeteshands: zetesfeet
	$(MAKE) all -C zeteshands

clean:
	$(MAKE) clean -C zetesfeet
	$(MAKE) clean -C zeteswings
	$(MAKE) clean -C zeteshands
	rm -f $(PACKAGE_NAME).tar.bz2

ideconf-eclipse:
	$(MAKE) ideconf-eclipse -C zetesfeet
	$(MAKE) ideconf-eclipse -C zeteswings
	$(MAKE) ideconf-eclipse -C zeteshands
	
package: zetes
	
	@echo [$(APPLICATION_NAME)] Copying all built files to a temporary folder...
	mkdir -p $(PACKAGE_NAME)/zetesfeet
	mkdir -p $(PACKAGE_NAME)/zeteswings
	mkdir -p $(PACKAGE_NAME)/zeteshands
	cp -rf zetesfeet/target-$(CLASSPATH)/* $(PACKAGE_NAME)/zetesfeet/
	cp -rf zeteswings/target-$(CLASSPATH)/* $(PACKAGE_NAME)/zeteswings/
	cp -rf zeteshands/target-$(CLASSPATH)/* $(PACKAGE_NAME)/zeteshands/
	@echo [$(APPLICATION_NAME)] Removing unnecessary files from the temporary folder...
	rm -rf $(PACKAGE_NAME)/zetesfeet/obj
	rm -rf $(PACKAGE_NAME)/zeteswings/obj
	rm -rf $(PACKAGE_NAME)/zeteshands/obj
	@echo [$(APPLICATION_NAME)] Archiving the package $(PACKAGE_NAME).tar.bz2...
	( \
	    cd $(PACKAGE_NAME); \
	    tar -cvjf ../$(PACKAGE_NAME).tar.bz2 *;\
	)
	rm -rf $(PACKAGE_NAME)
	
.PHONY: all zetes zeteswings zetesfeet zeteshands 
.SILENT: