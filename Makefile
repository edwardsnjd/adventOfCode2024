SHELL := bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DELETE_ON_ERROR:

.PHONY: loc.png
loc.png:
	@fd 'part' --exec wc -l \
	| awk '{print $$1, $$2}' \
	| sort -k2,2 \
	| awk '{print $$2, $$1}' \
	| sed -E 's|^./||' \
	| feedgnuplot \
		--term 'png small size 1024,600' \
		--title 'LOC for AoC Parts' \
		--dataid \
		--exit \
		--ymin 0 \
		--autolegend \
		--set 'key outside' \
		--unset grid \
		--set 'tics out' \
		--set 'border behind' \
	> $@

