(define print
    (lambda (x) x))

(define print10
    (lambda () 10))

(define a 10)

(define b a)

(define c b)

(define d c)

(print10)

(print 20)