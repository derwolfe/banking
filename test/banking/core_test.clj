(ns banking.core-test
  (:require [clojure.test :refer :all]
            [banking.core :refer :all]))

(deftest test-balance
  (let [c (make-account! 1 100)]
    (is (= 100 (balance c)))))

(deftest test-credit
  (let [c (make-account! 1 100)]
    (credit! c 60)
    (is (= 160 (balance c)))))

(deftest test-debit
  (let [c (make-account! 1 100)]
    (debit! c 100)
    (is (= 0 (balance c)))))

(deftest test-overdraw-exception
  (let [c (make-account! 1 100)]
    (is (thrown? Exception
                 (debit! c 101)))))

(deftest test-concurrent-credits-debits
  (let [a (make-account! 1 100)]
    (doall
     (pmap
      #(do
         (Thread/sleep 100)
         (credit! a (+ % 1))
         (debit! a %))
      (take 100 (repeatedly #(rand-int 25)))))
    (is (= 200 (balance a)))))

(deftest test-transfer
  (let [a (make-account! 1 100)
        b (make-account! 2 100)]
    (do
      (transfer! b a 25)
      (is (= 75 (balance b)))
      (is (= 125  (balance a))))))

(deftest test-no-transfer-if-overdrawn
  (let [a (make-account! 1 1)
        b (make-account! 2 1)]
    (transfer! a b 101)
    (is (= 1 (balance a)))
    (is (= 1 (balance b)))))

(deftest test-concurrent-transfers
  (let [a (make-account! 1 100)
        b (make-account! 2 100)]
    (doall
     (pmap
      #(do
         (transfer! a b %)
         (transfer! b a %))
      (take 30 (repeatedly #(rand-int 25)))))
    ;; shouldn't the result of these transfers end up with EACH accont
    ;; still equalling the initial 100?
    (is (= 100 (balance a)))
    (is (= 100 (balance b)))))

(deftest test-prints-balances
  (let [act1 (make-account! "act1" 100)]
    (is (= '("act1 has 100")
           (account-balances [act1])))))
