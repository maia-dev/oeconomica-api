(ns oeconomica-api.helpers)

(defn contains-many?
  " Checks if a map(m) contains all the keys in (ks)"
  [m & ks]
  (every? #(contains? m %) ks))

(defn create-and-check
  " Receives (f-create) a function that creates something and
             (f-check) a function that returns an int wich is incremented
                       when created
     Returns true if ok, false if check fails"
  [f-create f-check]
  (let [count (f-check)]
    (f-create)
    (if (= (+ count 1) (f-check)) true false)))
