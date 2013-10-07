all: zetes

zetes: zetesfeet zeteswings zeteshands

zetesfeet:
	$(MAKE) TARGET=../target all -C zetesfeet
	
zeteswings: zetesfeet
	$(MAKE) TARGET=../target ZETES_FEET_PATH=../target all -C zeteswings

zeteshands: zetesfeet
	$(MAKE) TARGET=../target all -C zeteshands

clean:
	$(MAKE) TARGET=../target clean -C zetesfeet
	$(MAKE) TARGET=../target clean -C zeteswings
	$(MAKE) TARGET=../target clean -C zeteshands

ideconf-eclipse:
	$(MAKE) ideconf-eclipse -C zetesfeet
	$(MAKE) ideconf-eclipse -C zeteswings
	$(MAKE) ideconf-eclipse -C zeteshands
	$(MAKE) ideconf-eclipse -C bellardpi
	

.PHONY: all zetes zeteswings zetesfeet zeteshands
.SILENT: