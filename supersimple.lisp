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

; The line below doesn't work
; (car (cdr '(a b c)))

; The line below doesn't work
; (cdr (car '((a b c) d)))