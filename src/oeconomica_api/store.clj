(ns oeconomica-api.store
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(defn find-user [ds name]
  " Returns a user in the database if exists, nil if not found"
  (let [conn (mg/connect {:host (:host ds) :port (:port ds) })
        db (mg/get-db conn (:db ds))]
    (mc/find-one-as-map db "users" {:name name})))

(defn add-user! [ds userData]
  " tries to add a new user to the db.
                  returns :user-created if created
                          :error-inserting-user if unkown error
                          :user-exists if the user already exists"
  (let [conn (mg/connect {:host (:host ds) :port (:port ds) })
        db (mg/get-db conn (:db ds))]
    (if-not (find-user ds (:name userData))
      (if-not (nil? (mc/insert db "users" userData))
        :user-created
        :error-inserting-user)
      :user-exists)))
