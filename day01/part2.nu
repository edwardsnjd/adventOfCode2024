#! /usr/bin/env nu

def main [path = "input.txt"] {
  let input = getInput $path
  let col1 = $input | get first | sort
  let col2 = $input | get second | sort | group-by
  let countOf = { |v| $col2 | get --ignore-errors $"($v)" | default [] | length }

  $col1
  | each {|f| $f * (do $countOf $f)}
  | math sum
}

def getInput [path] {
  open $path
  | lines
  | split column --regex " +" first second
  | update first { $in | into int }
  | update second { $in | into int }
}
