# Environment:
# 	APPLICATION_NAME	- the application's name
# 	TARGET				- target path
# 	JUST_COPY_FILES		- list of the files to copy

JUST_COPY_FILES_TARGET = $(addprefix $(TARGET)/,$(JUST_COPY_FILES))

$(JUST_COPY_FILES_TARGET): $(TARGET)/%: %
	@echo [$(APPLICATION_NAME)] Copying $@...
	mkdir -p $(dir $@)
	cp -f $< $(TARGET)/$<