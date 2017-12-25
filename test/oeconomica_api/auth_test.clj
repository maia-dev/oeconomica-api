(ns oeconomica-api.auth-test
  (:require [oeconomica-api.auth :as sut]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [buddy.hashers :as hs]
            [clojure.test :refer [deftest is testing use-fixtures]]))

;TODO: not testing token related stuff, need looking into

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


  (deftest auth-user-test
    (testing "Authenticate user"
      (let [coll "users"
            doc {:name "Initial" :password "1234"}]
        (is (= (sut/auth-user doc)
               {:user {:name "Initial" :balance 0}}))
        (is (= :invalid-name-password
               (sut/auth-user {:name "t" :password "1234"})))
        (is (= :invalid-name-password
               (sut/auth-user {:name "test user" :password "12"}))))))

  (deftest create-auth-token-test
    (testing "Create auth token"
      (is (= :invalid-name-password
             (sut/create-auth-token {:name "t" :password "1234"})))
      (is (= :invalid-name-password
             (sut/create-auth-token {:name "Initial" :password "1"})))
      (is (= :invalid-name-password
             (sut/create-auth-token {:name "Initial"})))
      (is (boolean(:token (sut/create-auth-token
                           {:name "Initial" :password "1234"})))))))
