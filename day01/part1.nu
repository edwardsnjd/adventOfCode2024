#! /usr/bin/env -S nu --stdin

def main [] {
  let input = $in | parseInput
  let col1 = $input | select first | sort
  let col2 = $input | select second | sort

  ($col1 | merge $col2)
  | each {|f| $f.first - $f.second }
  | math abs
  | math sum
}

def parseInput [] {
  lines
  | split column --regex " +" first second
  | update first { $in | into int }
  | update second { $in | into int }
}
