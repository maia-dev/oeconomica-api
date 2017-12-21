(ns oeconomica-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer (response)]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [oeconomica-api.auth :as auth]
            [oeconomica-api.config :as config]
            [oeconomica-api.store :as store]
            [oeconomica-api.validation :as validate]
            [oeconomica-api.messages :refer [messages]]))

;;----------------Open-Routes
;;NOTE: see multimethods to implement messages
(defn login [req]
  (let [[ok? res] (auth/create-auth-token (:ds req)
                                          (:auth-conf req)
                                          (:body req))]
    (if ok?
      {:status 201 :body res}
      {:status 401 :body res})))

;;------------------Closed-Routes
(defn signup! [req]
  (messages
   (auth/register-user! (:ds req)
                        (validate/sanitize-new-user-data (:body req)))))

(defn home-controler [req] "Welcome to Oeconomica API!")

;;-----------------Middlewares
(defn wrap-config [handler]
  (fn [req]
    (handler (assoc req :auth-conf config/auth))))

(defn wrap-datastore [handler]
  (fn [req]
    (handler (assoc req :ds config/datastore))))

(defn wrap-auth [handler]
  (fn [req]
    (let [user (auth/is-token-valid (:token (:body req)) config/auth)]
      (if (nil? user)
        (messages :invalid-token)
        (handler (assoc req :identity user))))))

;;-----------------Interface
(defroutes closed-routes
  (GET "/" [] home-controler)
  (POST "/signup" [] signup!))

(defroutes app-routes
  (POST "/login" [] login)
  (wrap-routes closed-routes wrap-auth)
  (route/resources "/resources")
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-datastore)
      (wrap-config)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
