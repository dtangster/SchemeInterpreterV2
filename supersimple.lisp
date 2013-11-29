(define print
  (lambda (x) x))

(define double
  (lambda (a)
    (let ((result (+ a a)))
      result)))

(print (double 5))