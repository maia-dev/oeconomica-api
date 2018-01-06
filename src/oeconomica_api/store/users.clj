(ns oeconomica-api.store.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [environ.core :refer [env]]
            [oeconomica-api.helpers :refer [create-and-check]]
            [oeconomica-api.store.connections :refer [db]]))

(defn find-user
  " Returns a user in the database if exists, nil if not found"
  [name]
  (mc/find-one-as-map db "users" {:name name}))

(defn get-user-names
  " Returns the name of all registered users in a list"
  []
  (map :name (mc/find-maps db "users")))

(defn get-user-info-from-key
  " Returns the values from a key from a given user name"
  [key user]
  (apply key (mc/find-maps db "users" {:name user})))


(defn add-user!
  " Adds a new user to the db. returns :user-created"
  [user-data]
  (if (create-and-check #(mc/insert db "users" user-data)
                        #(mc/count db "users"))
    :user-created
    :error-creating-user))

(defn insert-pending-validation!
  [user t-id]
  (mc/update db "users" (find-user user)
             {$push {:pending-validations t-id}}))

(defn insert-pending-transaction!
  [user t-id]
  (mc/update db "users" (find-user user)
             {$push {:pending-transactions t-id}}))
