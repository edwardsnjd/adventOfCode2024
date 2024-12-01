#!/usr/bin/env bash

inputPath="${1:-input.txt}"

join \
   <(cut -d' ' -f1 "$inputPath" | sort) \
   <(cut -d' ' -f4 "$inputPath" | sort | uniq -c | awk '{ print $2, $1}') \
| awk '
    { total += $1 * $2 }
    END { print total }
'