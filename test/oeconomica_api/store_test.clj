(ns oeconomica-api.store-test
  (:require [oeconomica-api.store :as sut]
            [oeconomica-api.testhelpers :as h ]
            [clojure.test :refer [deftest is testing]]))


(deftest add-user-test
  (testing "Adding user to users db"
    (h/clear-users)
    (is (= :user-created (sut/add-user! h/test-user)))
    (is (= :user-exists (sut/add-user! h/test-user)))
    (h/clear-users)))

(deftest find-user
   (testing "Find user in users db"
     (h/clear-users)
     (is (nil? (sut/find-user (:name h/test-user))))
     (h/create-test-user)
     (is (instance? clojure.lang.PersistentArrayMap
                      (sut/find-user (:name h/test-user))))
     (h/clear-users)))
