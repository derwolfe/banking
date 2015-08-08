(ns banking.core)

(defn seed-account [id seed]
  {:id id :money seed})

(defn make-account! [acct-id seed]
  (ref {:id acct-id :money seed}))

(defn account-id [account]
  (:id @account))

(defn balance [account]
  (:money @account))

(defn- credit [account amount]
  (assoc account :money (+ (:money account) amount)))

(defn- debit [account amount]
  (if (> amount (:money account))
    (throw (Exception. "Insufficient Funds"))
    (credit account (- amount))))

(defn credit! [account amount]
  (dosync
   (alter account credit amount)))

(defn debit! [account amount]
  (dosync
   (alter account debit amount)))

(defn transfer! [from to amount]
  (dosync
   (when (>= (balance from) amount)
     (Thread/sleep 100)
     (debit! from amount)
     (credit! to amount))))

(defn account-balances [accts]
  (map
   (fn [x] (format "%s has %d" (account-id x) (balance x))) accts))
