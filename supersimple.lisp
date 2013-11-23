(define print
    (lambda (x) x))

(define a 10)

(define b a)

(define c b)

(define d c)

(print 20)

(print a)

(define c 25)

(print d)