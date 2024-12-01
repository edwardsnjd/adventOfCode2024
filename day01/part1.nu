#! /usr/bin/env nu

def main [path = "input.txt"] {
  let input = getInput $path
  let col1 = $input | select first | sort
  let col2 = $input | select second | sort

  ($col1 | merge $col2)
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
