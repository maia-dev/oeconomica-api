(ns oeconomica-api.validation-test
  (:require [oeconomica-api.validation :as sut]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [buddy.hashers :as hs]
            [clojure.test :refer [deftest testing is run-tests use-fixtures]]))

(let [conn (mg/connect {:host (:ds-host env)
                        :port (Integer. (:ds-port env))})
      db (mg/get-db conn (:ds-db env))]

  (use-fixtures :each
    (fn [f]
      (mc/remove db "users")
      (mc/insert db "users" {:name "Initial"
                             :password (hs/encrypt "1234")
                             :balance 0})
      (f)
      (mc/remove db "users")))

(deftest sanitize-new-user-test
  (testing "Sanitize new user data"
    (let [userdata {:name "test" :password "test1234"}
          expected-data (assoc userdata
                               :balance 0)]
      (is (= expected-data
             (sut/sanitize-new-user userdata)))
      (is (= expected-data
             (sut/sanitize-new-user (assoc userdata :invalid-key "k"))))
      (is (= :user-exists
             (sut/sanitize-new-user {:name "Initial" :password "wtv"})))
      (is (= :bad-data
             (sut/sanitize-new-user (dissoc userdata :name))))
      (is (= :bad-data
             (sut/sanitize-new-user {:name 123 :password "aaa"})))
      (is (= :bad-data
             (sut/sanitize-new-user {:name "aa" :password 1234})))
      (is (= :bad-data
             (sut/sanitize-new-user {:name 123 :password 1234}))))))

(deftest sanitize-new-purchase-test
  (testing "Sanitize new purchase"
    (let [purchase-data {:spender "t"
                         :value 123
                         :receivers ["a" "b"]
                         :category "test"}
          expected-data (assoc purchase-data
                               :validations [{:name "a" :validated false}
                                             {:name "b" :validated false}]
                               :description nil)]
      (is (= expected-data
             (dissoc (sut/sanitize-new-purchase purchase-data) :date)))))))
