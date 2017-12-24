(ns oeconomica-api.testhelpers
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [buddy.core.keys :as ks]
            [buddy.sign.jwt :as jwt]
            [buddy.hashers :as hs]))


(def ds-data {:host (env :ds-host)
              :port (Integer. (env :ds-port))
              :db (env :ds-db)})
(def test-user {:name "test user"
                :balance 0
                :password (hs/encrypt "1234")})
(def conn
  (mg/connect {:host (:host ds-data)
               :port (:port ds-data)}))
(def test-db
  (mg/get-db conn (:db ds-data)))

;;---- USERS RELATED
(defn create-test-user []
  (mc/insert test-db "users" test-user))

(defn clear-users []
  (mc/remove test-db "users"))

;;---- TOKEN RELATED
(def key-data {:privkey (env :privkey)
               :pubkey (env :pubkey)
               :passphrase (env :key-passphrase)})

(defn- privkey []
  (ks/private-key
   (io/resource (:privkey key-data))
   (:passphrase key-data)))

(defn- pubkey []
  (ks/public-key
   (io/resource (:pubkey key-data))))

(defn sign-test-token []
  (let [exp (-> (t/plus (t/now) (t/seconds 30)))]
    (jwt/sign {:user "test"} (privkey) {:alg :rs256 :exp exp})))

(defn unsign-test-token [token]
  (jwt/unsign token (pubkey) {:alg :rs256}))
