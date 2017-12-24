(ns oeconomica-api.auth-test
  (:require [oeconomica-api.auth :as sut]
            [oeconomica-api.testhelpers :as h]
            [clojure.test :refer [deftest is testing]]))

(deftest register-user-test
  (testing "Register user"
    (is (= :bad-data (sut/register-user! nil)))
    (is (not (= :bad-data (sut/register-user! h/test-user))))
    (h/clear-users)))

(deftest auth-user-test
  (testing "Authenticate user"
    (h/create-test-user)
    (is (= (sut/auth-user {:name "test user" :password "1234"})
           {:user {:name "test user" :balance 0}}))
    (is (= :invalid-name-password
           (sut/auth-user {:name "t" :password "1234"})))
    (is (= :invalid-name-password
           (sut/auth-user {:name "test user" :password "12"})))
    (h/clear-users)))

(deftest create-auth-token-test
  (testing "Create auth token"
    (h/create-test-user)
    (is (= :invalid-name-password
           (sut/create-auth-token {:name "t" :password "1234"})))
    (is (= :invalid-name-password
           (sut/create-auth-token {:name "test user" :password "1"})))
    (is (= :invalid-name-password
           (sut/create-auth-token {:name "test user"})))
    (is (boolean(:token (sut/create-auth-token {:name "test user" :password "1234"}))))
    (h/clear-users)))

;TODO: not testing token related stuff, need looking into
