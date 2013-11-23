(define print
    (lambda (x) x))

(print (print 100))

(print (print 500))

(print (print (print (print 1234))))