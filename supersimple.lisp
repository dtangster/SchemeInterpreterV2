(let ((x 5))
      x)

(pair? '(1))

(pair? '())

(pair? '(1 2 3))

(null? '())

(null? '(2))

(null? '(3 (1)))

(null? (cdr '(3)))

(define print
    (lambda (x) x))

(print 10)

(+ 5 6)

(print (+ 1 2))

(print (+ 1 2 3 4 5 6))

(print (* 1 2 3))

(print (* 1 2 3 (+ 2 4)))

(print (* (+ 1 1 1) (+ 2 2 2) (+ 3 3)))

(print (+ (* 1 2) (* 2 3) (* 3 4) (* 1 4 5)))

(car '((a b c) d))

(car '(a b c))

(cdr '(a b c))

(cdr '(a b (c)))

(car (cdr '(a b c)))

(cdr (car '((a b c) d)))

(+ (car '(1 2)) (car '(3 4)))

(+ (car '(1 2)) (car (cdr '(3 4))))

(* (+ (car '(1 2)) (car '(3 4))) (+ (car '(1 2)) (car (cdr '(3 4)))))

(define z 100)

(define double
  (lambda (a)
    (let ((result (+ a a)))
      result)))

(define printNonsense
  (lambda (x)
      (let ((y 9)) (double (+ x y z)))))

(printNonsense 3)