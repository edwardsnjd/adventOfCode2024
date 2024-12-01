#! /usr/bin/env nu

def main [path = "input.txt"] {
  let col1 = getInput $path | select first | sort
  let col2 = getInput $path | select second | sort
  let merged = $col1 | merge $col2

  $merged
  | each {|f| $f.first - $f.second }
  | math abs
  | math sum
}

def getInput [path] {
  open $path
  | lines
  | split column --regex " +" first second
  | update first { $in | into int }
  | update second { $in | into int }
}
