all: zetes

zetes: zetesfeet zeteswings zeteshands

zetesfeet:
	$(MAKE) all -C zetesfeet
	
zeteswings: zetesfeet
	$(MAKE) ZETES_FEET_PATH=../zetesfeet/target all -C zeteswings

zeteshands: zetesfeet
	$(MAKE) all -C zeteshands

clean:
	$(MAKE) clean -C zetesfeet
	$(MAKE) clean -C zeteswings
	$(MAKE) clean -C zeteshands

ideconf-eclipse:
	$(MAKE) ideconf-eclipse -C zetesfeet
	$(MAKE) ideconf-eclipse -C zeteswings
	$(MAKE) ideconf-eclipse -C zeteshands
	

.PHONY: all zetes zeteswings zetesfeet zeteshands
.SILENT: