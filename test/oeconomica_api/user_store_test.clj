(ns oeconomica-api.user-store-test
  (:require [oeconomica-api.store.users :as sut]
            [buddy.hashers :as hs]
            [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]
            [clojure.test :refer [deftest is testing use-fixtures]]))


(let [conn (mg/connect {:host (:ds-host env)
                        :port (Integer. (:ds-port env))})
      db (mg/get-db conn (:ds-db env))]

  (use-fixtures :each
    (fn [f]
      (mc/remove db "users")
      (f)
      (mc/remove db "users")))

  (deftest add-user-test
    (testing "Adding user to users db"
      (let [coll "users"
            doc {:name "Test"}]
        (is (= :user-created (sut/add-user! doc)))
        (is (= 1 (mc/count db coll))))))

  (deftest get-user-names-test
    (testing "Get all user names from db"
      (let [coll "users"]
        (is (= 0 (count (sut/get-user-names))))
        (mc/insert db coll {:name "t1"})
        (is (= 1 (count (sut/get-user-names))))
        (mc/insert db coll {:name "t2"})
        (is (= 2 (count (sut/get-user-names)))))))

  (deftest find-user
    (testing "Find user in users db"
      (let [coll "users"
            doc {:name "Test"}]
        (is (nil? (sut/find-user (:name doc))))
        (mc/insert db coll doc)
        (is (instance? clojure.lang.PersistentArrayMap
                       (sut/find-user (:name doc)))))))

  (deftest insert-pending-transaction))
