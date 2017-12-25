(ns oeconomica-api.store.transactions
  (:require [monger.collection :as mc]
            [oeconomica-api.store.connections :refer [db]]
            [oeconomica-api.store.users :as user-store])
  (:import org.bson.types.ObjectId))


(defn register-new-purchase! [purchase-data]
  (let [purchase (assoc purchase-data :_id (ObjectId.))]
    (do
      (dorun (map user-store/insert-pending-validation!
                  (:receivers purchase) (repeat (:_id purchase))))
      (user-store/insert-pending-transaction! (:spender purchase)
                                              (:_id purchase))
      (mc/insert db "pending" purchase)
      :purchase-registered)))
