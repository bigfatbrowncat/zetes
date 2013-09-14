all: zetes gltest tinyviewer

zetes: zetesfeet zeteswings

zetesfeet:
	$(MAKE) all -C zetesfeet
	
zeteswings: zetesfeet
	$(MAKE) all -C zeteswings

gltest: zeteswings
	$(MAKE) all -C gltest

tinyviewer: zeteswings
	$(MAKE) all -C tinyviewer

clean:
	$(MAKE) clean -C zetesfeet
	$(MAKE) clean -C zeteswings
	$(MAKE) clean -C gltest
	$(MAKE) clean -C tinyviewer
	
.PHONY: all zetes zeteswings zetesfeet gltest tinyviewer
.SILENT: