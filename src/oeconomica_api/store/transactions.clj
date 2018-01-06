(ns oeconomica-api.store.transactions
  (:require [monger.collection :as mc]
            [oeconomica-api.store.connections :refer [db]]
            [oeconomica-api.store.users :as user-store])
  (:import org.bson.types.ObjectId))

(defn get-my-pending
  "Returns all the pending (key) from a given user, (key) can be
    :pending-transactions or :pending-validations"
  [key user]
  (map #(mc/find-one-as-map db "pending" {:_id %})
       (user-store/get-user-info-from-key key user)))

(defn register-new-purchase! [purchase-data]
  (let [purchase (assoc purchase-data :_id (ObjectId.))]
    (do
      (dorun (map user-store/insert-pending-validation!
                  (:receivers purchase)
                  (repeat (:_id purchase))))
      (user-store/insert-pending-transaction! (:spender purchase)
                                              (:_id purchase))
      (mc/insert db "pending" purchase)
      :purchase-registered)))

