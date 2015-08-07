(ns banking.core)

(defn make-account [acct-id]
  (ref {:id acct-id :money 0}))

(defn account-id [account]
  (:id @account))

(defn balance [account]
  (:money @account))

(defn credit [account amount]
  (dosync
   (alter account
          (fn [a m] (assoc a :money (+ (:money a) amount))) amount)))

(defn debit [account amount]
  (dosync
   (when (> amount (balance account))
     (throw (Exception. "Insufficient Funds")))
     (credit account (- amount))))

(defn transfer [from to amount]
  (dosync
   (when (>= (:money from) amount)
     (Thread/sleep 100)
     (debit from amount)
     (credit to amount))))

(defn account-balances [accts]
  (map
   (fn [x] (format "%s has %d" (account-id x) (balance x))) accts))
