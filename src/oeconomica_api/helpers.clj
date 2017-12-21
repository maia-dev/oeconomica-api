(ns oeconomica-api.helpers)

(defn contains-many? [m & ks]
  " Checks if a map(m) contains all the keys in (ks)"
  (every? #(contains? m %) ks))
