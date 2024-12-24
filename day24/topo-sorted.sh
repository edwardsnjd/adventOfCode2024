#! /usr/bin/env bash

awk '
  BEGIN { IFS = " " }
  />/ {
   print $1, $5
   print $3, $5
  }
' | tsort
