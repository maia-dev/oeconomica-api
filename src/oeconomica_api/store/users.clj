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
  " adds a new user to the db. returns :user-created"
  (do (mc/insert db "users" user-data)
    :user-created))

(defn insert-pending-validation! [user t-id]
  (mc/update db "users" (find-user user)
             {$push {:pending-validations t-id}}))

(defn insert-pending-transaction! [user t-id]
  (mc/update db "users" (find-user user)
             {$push {:pending-transactions t-id}}))
