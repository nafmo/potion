none:
	@echo "To make a local binary for testing, type:"
	@echo "   make local"
	@echo "To make a C64 target binary, type:"
	@echo "   make c64"

all: local c64

clean:
	-rm potion potion.prg
	-rm -rf local target

local:
	make -f Makefile.linux

c64:
	make -f Makefile.c64

.PHONY: all none c64 local
