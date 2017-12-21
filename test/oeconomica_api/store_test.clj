(ns oeconomica-api.store-test
  (:require [oeconomica-api.store :as sut]
            [oeconomica-api.config :as config]
            [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.test :as t]))

(def ds-data config/test-datastore)

(def test-user {:name "TESTER"
                :balance 0
                :password "1234"})

(def conn-mongo
  (mg/connect {:host (:host ds-data)
               :port (:port ds-data)}))
(def test-db
  (mg/get-db conn-mongo (:db ds-data)))

(t/deftest add-user-test
  (t/testing "Adding user to users db"
    (mc/remove test-db "users")
    (t/is (= :user-created (sut/add-user! ds-data test-user)))
    (t/is (= :user-exists (sut/add-user! ds-data test-user)))
    (mc/remove test-db "users")))

(t/deftest find-user
   (t/testing "Find user in users db"
     (mc/remove test-db "users")
     (t/is (nil? (sut/find-user ds-data (:name test-user))))
     (mc/insert test-db "users" test-user)
     (t/is (instance? clojure.lang.PersistentArrayMap
                      (sut/find-user ds-data (:name test-user))))
     (mc/remove test-db "users")))


(t/run-tests)
