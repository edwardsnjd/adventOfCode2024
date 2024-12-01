#! /usr/bin/env bash

input="$(cat "$@")"
paste \
  <(echo "$input" | cut -d' ' -f1 | sort) \
  <(echo "$input" | cut -d' ' -f4 | sort) \
| awk '
  function abs(x) { return x < 0 ? x * -1 : x }
  { total += abs($1 - $2) }
  END { print total }
'
