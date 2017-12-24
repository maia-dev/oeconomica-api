(ns oeconomica-api.user-store-test
  (:require [oeconomica-api.store.users :as sut]
            [buddy.hashers :as hs]
            [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]
            [clojure.test :refer [deftest is testing use-fixtures]]))


(let [conn (mg/connect {:host (:ds-host env)
                        :port (Integer. (:ds-port env))})
      db (mg/connect (:db (:ds-db env)))]

  (defn purge-collections
    [f]
    (mc/remove db "users")
    (f)
    (mc/remove db "users"))
  (use-fixtures :each purge-collections)

  (deftest add-user-test
    (testing "Adding user to users db"
      (let [coll "users"
            doc {:name "Test"}]
        (is (= :user-created (sut/add-user! doc)))
        (is (= :user-exists (sut/add-user! doc)))
        (is (= 1 (mc/count db coll))))))

  (deftest find-user
    (testing "Find user in users db"
      (let [coll "users"
            doc {:name "Test"}]
        (is (nil? (sut/find-user (:name doc))))
        (mc/insert db coll doc)
        (is (instance? clojure.lang.PersistentArrayMap
                       (sut/find-user (:name doc)))))))

  (deftest insert-pending-transaction))
