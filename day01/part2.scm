#! /usr/bin/env -S guile \\
-e main -s
!#

(use-srfis '(1)) ; List utils
(use-modules (ice-9 rdelim)) ; IO

;; IO

(define (read-lines port)
  (let ((line (read-line port)))
    (if (eof-object? line)
        '()
        (cons line (read-lines port)))))

;; Input parsing

(define (nums line)
  (let* ((all-tokens (string-split line #\space))
         (tokens (filter not-string-null? all-tokens)))
    (map string->number tokens)))

;; Utilities

(define (first list) (list-ref list 0))
(define (second list) (list-ref list 1))
(define (sum xs) (fold + 0 xs))
(define (print msg) (display msg) (newline))
(define (count-eq target list)
  (length (filter (lambda (x) (equal? x target)) list)))
(define (abs-diff a b) (abs (- a b)))
(define (not-string-null? x) (not (string-null? x)))

;; Puzzle

(define (part2 listA listB)
  (sum
    (map
      (lambda (a) (* a (count-eq a listB)))
      listA)))

(define (main args)
  (let* ((input (read-lines (current-input-port)))
         (pairs (map nums input))
         (listA (map first pairs))
         (listB (map second pairs)))
    (print (part2 listA listB))))
