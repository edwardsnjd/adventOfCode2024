#! /usr/bin/env -S guile \\
-e main -s
!#

(use-modules (ice-9 rdelim) (ice-9 regex) (srfi srfi-1)) ; IO, match, lists

;; IO

(define (read-lines port)
  (let ((line (read-line port)))
    (if (eof-object? line) '() (cons line (read-lines port)))))

;; Utilities

(define (print msg) (display msg) (newline))
(define (flow . fns) (apply compose (reverse fns)))

;; Parsing

(define operations
  (flow
    (lambda (x) (list-matches "mul\\(([0-9]{1,3}),([0-9]{1,3})\\)" x))
    (lambda (x) (map operation x))))

(define (operation match)
  (let ((lhs (string->number (match:substring match 1)))
       (rhs (string->number (match:substring match 2))))
    (cons lhs rhs)))

;; Puzzle

(define (part1 ops) (fold + 0 (map eval-op ops)))

(define (eval-op op) (* (car op) (cdr op)))

(define (main _)
  (let* ((input (read-lines (current-input-port)))
         (grid (append-map operations input)))
    (print (part1 grid))))
