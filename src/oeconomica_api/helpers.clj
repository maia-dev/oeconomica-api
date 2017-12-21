(ns oeconomica-api.helpers)

(defn contains-many? [m & ks]
  (every? #(contains? m %) ks))
