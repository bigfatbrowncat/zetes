ifndef IDE_CONF_PATH
  IDE_CONF_PATH=ideconf
endif

VARS_SUBST='s|<ARCH>|$(ARCH)|g;s|<PLATFORM_SHORT>|$(PLATFORM_SHORT)|g;s|<JAVA_HOME>|$(JAVA_HOME)|g'
IDE_CONF_SRC=$(IDE_CONF_PATH)/$(PLATFORM_TAG)

ideconf-eclipse:
	@echo Extracting Eclipse project files for $(PLATFORM_TAG)...
	mkdir -p .settings
	rm -f .settings/org.eclipse.cdt.core.prefs
	sed $(VARS_SUBST) < $(IDE_CONF_PATH)/eclipse/settings/org.eclipse.cdt.core.prefs.template > .settings/org.eclipse.cdt.core.prefs
	sed $(VARS_SUBST) < $(IDE_CONF_PATH)/eclipse/classpath.template > .classpath
	sed $(VARS_SUBST) < $(IDE_CONF_PATH)/eclipse/project.template > .project
	sed $(VARS_SUBST) < $(IDE_CONF_PATH)/eclipse/$(PLATFORM_SHORT)/cproject.template > .cproject

#.SILENT:
