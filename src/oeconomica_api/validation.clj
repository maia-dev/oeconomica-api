(ns oeconomica-api.validation
  (:require [oeconomica-api.helpers :as h]))

(defn sanitize-new-user-data [user-data]
  (if (and (h/contains-many? user-data :name :password)
           (string? (:name user-data))
           (string? (:password user-data)))
    {:name (:name user-data) :password (:password user-data) :balance 0}
    nil))

;;TODO:test for spender existance (probably in the store)
(defn sanitize-new-purchase [purchase-data]
  (if (and (h/contains-many? purchase-data
                             :spender :value :receivers :category)
           (string? (:spender purchase-data))
           (string? (:category purchase-data))
           (vector? (:receivers purchase-data))
           (number? (:value purchase-data))
           )
    {:spender (:spender purchase-data)
     :value (:value purchase-data)
     :receivers (:receivers purchase-data)
     :category (:category purchase-data)
     :description (:description purchase-data)
     ;;TODO: this needs to be redone
     :validated (vec (boolean-array (map nil? (:receivers purchase-data))))
     }
    nil))


;(println (vec (boolean-array (map nil? ["aaa" "bbb" "ccc"]) )))
