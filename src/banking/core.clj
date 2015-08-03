(ns banking.core)

(defn make-account []
  (ref 0))

(defn balance [account]
  @account)

(defn credit [account amount]
  (dosync
    (alter account #(+ % amount))))

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
          account-name (name 'acct)]
      (printf "%s has %d\n" account-name balance-amount)
      (recur (rest accounts)))))
