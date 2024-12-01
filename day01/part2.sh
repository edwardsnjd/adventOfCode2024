#! /usr/bin/env bash

input="$(cat "$@")"
join \
  <(echo "$input" | cut -d' ' -f1 | sort) \
  <(echo "$input" | cut -d' ' -f4 | sort | uniq -c | awk '{ print $2, $1}') \
| awk '
  { total += $1 * $2 }
  END { print total }
'
