all: zetes gltest tinyviewer bellardpi

zetes: zetesfeet zeteswings zeteshands

zetesfeet:
	$(MAKE) all -C zetesfeet
	
zeteswings: zetesfeet
	$(MAKE) all -C zeteswings

zeteshands: zetesfeet
	$(MAKE) all -C zeteshands

gltest: zeteswings
	$(MAKE) app -C gltest

tinyviewer: zeteswings
	$(MAKE) app -C tinyviewer

bellardpi: zeteshands
	$(MAKE) app -C bellardpi

clean:
	$(MAKE) clean -C zetesfeet
	$(MAKE) clean -C zeteswings
	$(MAKE) clean -C zeteshands
	$(MAKE) clean -C gltest
	$(MAKE) clean -C tinyviewer
	$(MAKE) clean -C bellardpi

package: gltest-pack tinyviewer-pack

gltest-pack:
	$(MAKE) package -C gltest

tinyviewer-pack:
	$(MAKE) package -C tinyviewer


.PHONY: all zetes zeteswings zetesfeet zeteshands gltest tinyviewer bellardpi
.SILENT: