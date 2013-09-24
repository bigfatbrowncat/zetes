all: zetes #gltest tinyviewer bellardpi

zetes: zetesfeet zeteswings zeteshands

zetesfeet:
	$(MAKE) TARGET=../target all -C zetesfeet
	
zeteswings: zetesfeet
	$(MAKE) TARGET=../target ZETES_FEET_PATH=../target all -C zeteswings

zeteshands: zetesfeet
	$(MAKE) TARGET=../target all -C zeteshands

gltest: zeteswings
#	$(MAKE) app -C gltest

#tinyviewer: zeteswings
#	$(MAKE) app -C tinyviewer

#bellardpi: zeteshands
#	$(MAKE) app -C bellardpi

clean:
	$(MAKE) TARGET=../target clean -C zetesfeet
	$(MAKE) TARGET=../target clean -C zeteswings
	$(MAKE) TARGET=../target clean -C zeteshands
#	$(MAKE) clean -C gltest
#	$(MAKE) clean -C tinyviewer
#	$(MAKE) clean -C bellardpi

#package: gltest-pack tinyviewer-pack

#gltest-pack:
#	$(MAKE) package -C gltest

#tinyviewer-pack:
#	$(MAKE) package -C tinyviewer

ideconf-eclipse:
	$(MAKE) ideconf-eclipse -C zetesfeet
	$(MAKE) ideconf-eclipse -C zeteswings
	$(MAKE) ideconf-eclipse -C zeteshands
	$(MAKE) ideconf-eclipse -C bellardpi
	

.PHONY: all zetes zeteswings zetesfeet zeteshands gltest tinyviewer bellardpi
.SILENT: