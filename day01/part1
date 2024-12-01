#!/usr/bin/env bash

inputPath="${1:-input.txt}"

paste \
   <(cut -d' ' -f1 "$inputPath" | sort) \
   <(cut -d' ' -f4 "$inputPath" | sort) \
| awk '
   function abs(x) { return x < 0 ? x * -1 : x }
   { total += abs($1 - $2) }
   END { print total }
'