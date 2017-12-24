(ns oeconomica-api.store.transactions
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [clj-time.core :as t]
            [monger.operators :refer :all]
            [environ.core :refer [env]]
            [oeconomica-api.store.connections :refer [db]]
            [oeconomica-api.store.users :as user-store])
  (:import org.bson.types.ObjectId))


(defn flush-new-purchase! [data]
  (do
    (dorun
     (map user-store/insert-pending-transaction!
          (:receivers data) (repeat (:_id data))))
    (mc/insert db "pending" data)
    :purchase-registered)
  )

(defn register-new-purchase [purchase-data]
  "registers the new purchase in the pending transactions collection
     and updates the involved users with the pending transaction id"
  (let [purchase (assoc purchase-data :_id (ObjectId.))
        users (map user-store/find-user (:receivers purchase-data))]
    (if (not-any? nil? users)
      (flush-new-purchase! purchase)
      :bad-receivers)))
