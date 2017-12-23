(ns oeconomica-api.validation-test
  (:require [oeconomica-api.validation :as sut]
            [clojure.test :refer [deftest testing is run-tests]]))

(deftest sanitize-new-user-data-test
  (testing "Sanitize new user data"
    (let [userdata {:name "test" :password "test1234"}]
      (is (= (assoc userdata :balance 0)
             (sut/sanitize-new-user-data userdata)))
      (is (= (assoc userdata :balance 0)
             (sut/sanitize-new-user-data (assoc userdata :invalid-key "k"))))
      (is (nil?
             (sut/sanitize-new-user-data (dissoc userdata :name))))
      (is (nil?
             (sut/sanitize-new-user-data {:name 123 :password "aaa"})))
      (is (nil?
             (sut/sanitize-new-user-data {:name "aa" :password 1234})))
      (is (nil?
             (sut/sanitize-new-user-data {:name 123 :password 1234}))))))

(deftest sanitize-new-purchase-test
  (testing "Sanitize new purchase"
    (let [purchase-data {:spender "t"
                         :value 123
                         :receivers ["a" "b"]
                         :category "test"}]
      (is (= (assoc purchase-data :description nil :validated [false false])
             (sut/sanitize-new-purchase purchase-data))))))

