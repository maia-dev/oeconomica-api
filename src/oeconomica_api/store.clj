(ns oeconomica-api.store
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(defn find-user [ds name]
  (let [conn (mg/connect {:host (:host ds) :port (:port ds) })
        db (mg/get-db conn (:db ds))]
    (mc/find-one-as-map db "users" {:name name})))

(defn add-user! [ds userData]
    (let [conn (mg/connect {:host (:host ds) :port (:port ds) })
          db (mg/get-db conn (:db ds))]
      (if-not (find-user ds (:name userData))
        (if-not (nil? (mc/insert db "users" userData))
          :user-created
          :error-inserting-user)
        :user-exists)))
