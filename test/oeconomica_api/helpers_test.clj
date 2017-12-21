(ns oeconomica-api.helpers-test
  (:require [oeconomica-api.helpers :as sut]
            [clojure.test :refer [deftest testing is]]))

(deftest contains-many-test
  (testing "Contains many"
    (let [m {:a 1 :b 1 :c 2}]
      (is (sut/contains-many? m :a))
      (is (sut/contains-many? m :a :b))
      (is (sut/contains-many? m :a :b :c))
      (is (not (sut/contains-many? m :a :d)))
      (is (not (sut/contains-many? m :a :b :d))))))
