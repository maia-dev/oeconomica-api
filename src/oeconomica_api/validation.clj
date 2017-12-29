(ns oeconomica-api.validation
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [oeconomica-api.store.users :as user-store]
            [oeconomica-api.helpers :as h]))

;;--- see clojure spec explain to send better error messages

(defn sanitize-new-user [user-data]
  "Sanitizes the request and appends the server generated data.
     Returns: valid user data if all ok
              the message related to the error if error"
    (if (and (h/contains-many? user-data :name :password)
             (string? (:name user-data))
             (string? (:password user-data)))
      (if-not (boolean (user-store/find-user (:name user-data)))
        {:name (:name user-data)
         :password (:password user-data)
         :balance 0}
        :user-exists)
      :bad-data))


;TODO:this is leting a purchase to oneself pass, needs more validations
(defn sanitize-new-purchase [purchase-data]
  (if (and (h/contains-many? purchase-data
                             :spender :value :receivers :category)
           (string? (:spender purchase-data))
           (string? (:category purchase-data))
           (number? (:value purchase-data))
           (and (boolean (vector? (:receivers purchase-data)))
                (boolean (not-empty (:receivers purchase-data)))
                (not-any? nil? (map string? (:receivers purchase-data))))
           (or (string? (:description purchase-data))
               (nil? (:description purchase-data))))
    (if (not-any? nil? (map user-store/find-user (:receivers purchase-data)))
      {:spender (:spender purchase-data)
       :value (:value purchase-data)
       :receivers (into #{} (:receivers purchase-data))
       :category (:category purchase-data)
       :description (:description purchase-data)
       :date (c/to-string (t/now))
       :validations (map #(hash-map :name % :validated false)
                         (:receivers purchase-data))
       }
      :bad-receivers)
    :bad-data))
