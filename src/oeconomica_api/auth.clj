(ns oeconomica-api.auth
  (:require [buddy.hashers :as hs]
            [buddy.sign.jwt :as jwt]
            [buddy.core.keys :as ks]
            [clj-time.core :as t]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [oeconomica-api.store.users :as user-store]))

(def auth-conf {:privkey (env :privkey)
                :pubkey (env :pubkey)
                :passphrase (env :key-passphrase)})

(defn- privkey []
  "Returns private key if found, else nil"
  (ks/private-key
   (io/resource (:privkey auth-conf))
   (:passphrase auth-conf)))

(defn- pubkey []
  "Returns public key if found, else nill"
  (ks/public-key
   (io/resource (:pubkey auth-conf))))

(defn register-user! [user-data]
  "Calls user-store/add-user! with the user data and the hashed password
     if the user data is valid, else returns :bad-data"
  (if-not (nil? user-data)
    (user-store/add-user! (update-in user-data [:password] #(hs/encrypt %)))
    :bad-data))

(defn auth-user [credentials]
  "Returns a {:user {userdata} if user exists and password
     is correct, else returns :Invalid-name-password"
  (let [user (user-store/find-user (:name credentials))
        unauthed :invalid-name-password]
    (if user
      (if (hs/check (:password credentials) (:password user))
        {:user (dissoc user :password :_id)}
        unauthed)
      unauthed)))

(defn create-auth-token [credentials]
  (let [res (auth-user credentials)
        exp (-> (t/plus (t/now) (t/days 1)))]
    (if (res :user)
      {:token (jwt/sign res (privkey) {:alg :rs256 :exp exp})}
      res)))

(defn is-token-valid [token]
  (try
    (:name (:user (jwt/unsign token (pubkey) {:alg :rs256})))
    (catch Exception e
      nil)))
