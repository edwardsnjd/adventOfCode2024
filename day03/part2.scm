#! /usr/bin/env -S guile \\
-e main -s
!#

(use-modules (ice-9 rdelim) (ice-9 match) (ice-9 regex) (srfi srfi-1))

;; IO

(define (read-lines port)
  (let ((line (read-line port)))
    (if (eof-object? line) '() (cons line (read-lines port)))))

;; Utilities

(define (print msg) (display msg) (newline))

;; Parsing

(define (operations line)
  (map operation
       (list-matches "(mul)\\(([0-9]{1,3}),([0-9]{1,3})\\)|(do)\\(\\)|(don't)\\(\\)" line)))

(define (operation match)
  (cond
    ((equal? (match:substring match 1) "mul")
     (let ((lhs (string->number (match:substring match 2)))
           (rhs (string->number (match:substring match 3))))
       (list 'mul lhs rhs)))
    ((equal? (match:substring match 4) "do") (list 'do))
    ((equal? (match:substring match 5) "don't") (list 'dont))
    (else (error "Oh no"))))

;; Puzzle

(define (part1 ops)
  (let loop ((todo ops) (go #t) (total 0))
    (if (null? todo)
      total
      (let ((op (car todo)) (rest (cdr todo)))
        (match op
               (('mul a b) (loop rest go (+ total (if go (* a b) 0))))
               (('do)      (loop rest #t total))
               (('dont)    (loop rest #f total)))))))

(define (main _)
  (let* ((input (read-lines (current-input-port)))
         (grid (append-map operations input)))
    (print (part1 grid))))
