(ns oeconomica-api.validation
  (:require [oeconomica-api.store :as store]
            [oeconomica-api.helpers :as h]))

(defn sanitize-new-user-data [userData]
  (if (and (h/contains-many? userData :name :password)
           (string? (:name userData))
           (string? (:password userData)))
    {:name (:name userData) :password (:password userData)}
    nil))


