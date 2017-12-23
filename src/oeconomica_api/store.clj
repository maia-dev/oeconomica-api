(ns oeconomica-api.store
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]))

(def ds {:host (env :ds-host)
         :port (Integer. (env :ds-port))
         :db (env :ds-db)})

(def conn (mg/connect {:host (:host ds) :port (:port ds)}))
(def db (mg/get-db conn (:db ds)))

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
