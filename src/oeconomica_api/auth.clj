(ns oeconomica-api.auth
  (:require [buddy.hashers :as hs]
            [buddy.sign.jwt :as jwt]
            [buddy.core.keys :as ks]
            [clj-time.core :as t]
            [clojure.java.io :as io]
            [oeconomica-api.store :as store]))


(defn register-user! [ds user]
  (if-not (or (nil? user) (nil? ds))
    (store/add-user! ds (update-in user [:password] #(hs/encrypt %)))
    :bad-data))

(defn auth-user [ds credentials]
  (let [user (store/find-user ds (:name credentials))
        unauthed [false "Invalid username or password"]]
    (if user
      (if (hs/check (:password credentials) (:password user))
        [true {:user (dissoc user :password :_id)}]
        unauthed)
      unauthed)))

(defn- pkey [auth-conf]
  (ks/private-key
   (io/resource (:privkey auth-conf))
   (:passphrase auth-conf)))

(defn- pubkey [auth-conf]
  (ks/public-key
   (io/resource (:pubkey auth-conf))))

(defn create-auth-token [ds auth-conf credentials]
  (let [[ok? res] (auth-user ds credentials)
        exp (-> (t/plus (t/now) (t/days 1)))]
    (if ok?
      [true {:token (jwt/sign res (pkey auth-conf) {:alg :rs256 :exp exp})}]
      [false res])))

(defn is-token-valid [token auth-conf]
  (try
    (:name (:user (jwt/unsign token
                              (pubkey {:pubkey "auth_pubkey.pem"})
                              {:alg :rs256})))
    (catch Exception e
      nil)))
