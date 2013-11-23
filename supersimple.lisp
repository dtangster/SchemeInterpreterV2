(define printOne
    (lambda () 1))

(define print
    (lambda (x) x))

(define x 1)

(define y x)

(define z y)

(print x)

(print z)

(print (print 100))

(print (print (print (print 1234))))

(printOne)

(print (printOne))