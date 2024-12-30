#! /usr/bin/env -S guile \\
-e main -s
!#

(use-srfis '(1)) ; List utils
(use-modules (ice-9 rdelim)) ; IO

;; IO

(define (read-lines port)
  (let ((line (read-line port)))
    (if (eof-object? line) '() (cons line (read-lines port)))))

;; Utilities

(define (positive? x) (< 0 x))
(define negative? (negate positive?))
(define (between? min max) (lambda (x) (<= min x max)))
(define (peek xs) (print xs) xs)
(define (print msg) (display msg) (newline))
(define (flow . fns) (apply compose (reverse fns)))

(define nums
  (flow
    (lambda (x) (string-split x #\space))
    (lambda (x) (filter (negate string-null?) x))
    (lambda (x) (map string->number x))))

;; Puzzle

(define (find-diffs line)
  (map (lambda (a b) (- b a)) line (drop line 1)))

(define (safe? diffs)
  (and
    (or
      (every positive? diffs)
      (every negative? diffs))
    (every (between? 1 3) (map abs diffs))))

(define part1
  (flow
    (lambda (x) (map find-diffs x))
    (lambda (x) (filter safe? x))
    length))

(define (main _)
  (let* ((input (read-lines (current-input-port)))
         (grid (map nums input)))
    (print (part1 grid))))
