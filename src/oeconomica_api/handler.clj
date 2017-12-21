(ns oeconomica-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [oeconomica-api.auth :as auth]
            [oeconomica-api.config :as config]
            [oeconomica-api.store :as store]
            [oeconomica-api.validation :as validate]
            [oeconomica-api.messages :refer [messages]]))

;TODO: add authorization
;https://rundis.github.io/blog/2015/buddy_auth_part2.html
;;-----------------Routes
(defn signup! [req]
  (messages (auth/register-user! (:ds req)
                            (validate/sanitize-new-user-data (:body req)))))

;;NOTE: see multimethods to implement messages
(defn create-auth-token [req]
  (let [[ok? res] (auth/create-auth-token (:ds req)
                                          (:auth-conf req)
                                          (:body req))]
    (if ok?
      {:status 201 :body res}
      {:status 401 :body res})))

;;-----------------Middlewares
(defn wrap-config [handler]
  (fn [req]
    (handler (assoc req :auth-conf config/auth))))

(defn wrap-datastore [handler]
  (fn [req]
    (handler (assoc req :ds config/datastore))))

;;-----------------Interface
(defroutes app-routes
  (GET "/" [] "Welcome to Oeconomica API!")
  (POST "/signup" [] signup!)
  (POST "/create-auth-token" [] create-auth-token)
  (route/resources "/resources")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (wrap-datastore)
      (wrap-config)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
