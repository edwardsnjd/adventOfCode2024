# Sanity
# From: https://tech.davis-hansson.com/p/make/

SHELL := bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DELETE_ON_ERROR:
MAKEFLAGS += --warn-undefined-variables
MAKEFLAGS += --no-builtin-rules

.PHONY: all
all: input.svg

.PHONY: clean
clean:
	rm input.dot
	rm input.svg

input.dot: input.txt to_dot
	./to_dot < $< > $@

input.svg: input.dot
	dot -Tsvg < $< > $@
