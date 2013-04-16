APPNAME=crossbase
BINARIES_PATH=../bin/osx-x86_64
TARGET_PATH=../bin/osx-x86_64-bundle

all:
	mkdir -p $(TARGET_PATH)/$(APPNAME)
	cp -rf src/$(APPNAME)/* $(TARGET_PATH)/$(APPNAME)/$(APPNAME).app/
	
	mkdir -p $(TARGET_PATH)/$(APPNAME)/$(APPNAME).app/Contents/Resources/
	mkdir -p $(TARGET_PATH)/$(APPNAME)/$(APPNAME).app/Contents/MacOS/
	cp -rf $(BINARIES_PATH)/* $(TARGET_PATH)/$(APPNAME)/$(APPNAME).app/Contents/MacOS/
	hdiutil create $(TARGET_PATH)/$(APPNAME)-darwin-universal.dmg -srcfolder $(TARGET_PATH)/$(APPNAME) -ov

clean:
	rm -rf out