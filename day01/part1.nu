#! /usr/bin/env nu

let realPath = "input.txt"
let examplePath = "example.txt"

def "main" [] { part1 $realPath }
def "main part1" [] { part1 $realPath }
def "main part1 example" [] { part1 $examplePath }
def "main part2" [] { part2 $realPath }
def "main part2 example" [] { part2 $examplePath }

def part1 [path] {
  let col1 = getInput $path | select first | sort
  let col2 = getInput $path | select second | sort

  ($col1 | merge $col2)
  | each {|f| $f.first - $f.second }
  | math abs
  | math sum
}

def part2 [path] {
  let col1 = getInput $path | get first | sort
  let col2 = getInput $path | get second | sort | group-by
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
