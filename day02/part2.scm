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
(define (without lst ind) (append (take lst ind) (drop lst (+ ind 1))))

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

(define (safe-line? line)
  (safe? (find-diffs line)))

(define (safe-line-variation? line)
  (define candidates
    (cons line
      (map
        (lambda (i) (without line i))
        (iota (length line)))))
  (any safe-line? candidates))

(define (part2 grid)
  (count safe-line-variation? grid))

(define (main _)
  (let* ((input (read-lines (current-input-port)))
         (grid (map nums input)))
    (print (part2 grid))))
