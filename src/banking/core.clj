(ns banking.core)

(defn make-account [acct-number]
  (ref {:number acct-number :money 0}))

(defn balance [account]
  (:money @account))

(defn credit [account amount]
  (dosync
    (alter account
           (fn [acct]
             (let [new-balance (+ (:money acct) amount)
                   acct-number (:number acct)]
               {:number acct-number :money new-balance})))))

(defn debit [account amount]
  (dosync
   (when (> amount (balance account))
     (throw (Exception. "Insufficient Funds")))
     (credit account (- amount))))

(defn transfer [from to amount]
  (dosync
   (when (>= (balance from) amount)
     (Thread/sleep 10)
     (debit from amount)
     (credit to amount))))

(defn account-balances [accts]
  (loop [accounts accts]
    (let [acct (first accounts)
          balance-amount (balance acct)
          account-name (:number @acct)]
      (println "%s has %d" account-name balance-amount)
      (recur (rest accounts)))))
