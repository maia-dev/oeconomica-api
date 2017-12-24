(ns oeconomica-api.store.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [environ.core :refer [env]]
            [oeconomica-api.store.connections :refer [db]]))

(defn find-user [name]
  " Returns a user in the database if exists, nil if not found"
  (mc/find-one-as-map db "users" {:name name}))

(defn add-user! [user-data]
  " tries to add a new user to the db.
                  returns :user-created if created
                          :error-inserting-user if unkown error
                          :user-exists if the user already exists"
  (if-not (find-user (:name user-data))
    (if-not (nil? (mc/insert db "users" user-data))
      :user-created
      :error-inserting-user)
    :user-exists))

(defn insert-pending-transaction! [user t-id]
  (mc/update db "users" (find-user user)
             {$push {:pending-transactions t-id}}))
