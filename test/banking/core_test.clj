(ns banking.core-test
  (:require [clojure.test :refer :all]
            [banking.core :refer :all]))

(def checking (make-account 1))
(def savings (make-account 2))

(defn my-fixture [f]
  (dosync
    (ref-set checking {:id 1 :money 100})
    (ref-set savings {:id 2 :money 100})
  (f)))

(use-fixtures :each my-fixture)

(deftest test-balance
  (is (= 100 (balance checking))))

(deftest test-credit
  (credit checking 60)
  (is (= 160 (balance checking))))

(deftest test-debit
  (debit checking 100)
  (is (= 0 (balance checking))))

(deftest test-overdraw-exception
  (is (thrown? Exception
        (debit checking 101))))

;; (deftest test-concurrent-credits-debits
;;   (doall (pmap #(do
;;                   (credit checking (+ % 1))
;;                   (debit checking %))
;;                (take 2 (repeat 5))))
;;   (is (= 200 (balance checking))))

(deftest test-transfer
  (transfer savings checking 25)
  (is (= 75 (balance savings)))
  (is (= 125  (balance checking))))

(deftest test-no-transfer-if-overdrawn
  (transfer checking savings 101)
  (is (= 100 (balance savings)))
  (is (= 100 (balance checking))))

;; this is never terminating!
;; (deftest test-concurrent-transfers
;;   "do one hundred transfers in and out of each account using the same amount."
;;   (doall (pmap #(do
;;            (transfer checking savings %)
;;            (transfer savings checking %))
;;         ;; make a lazy sequence of 100 transactions to perform
;;         (take 100 (repeatedly #(rand-int 25)))))
;;   (is (= 200 (+ (balance savings) (balance checking)))))

(deftest test-prints-balances
  (let [act1 (make-account "act1")]
    (credit act1 100)
    (is (= '("act1 has 100")
           (account-balances [act1])))))
