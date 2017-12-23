(ns oeconomica-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer (response)]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [oeconomica-api.auth :as auth]
            [oeconomica-api.validation :as validate]
            [oeconomica-api.messages :refer [messages]]))

;;----------------Open-Routes
(defn home [req] "Welcome to Oeconomica API!")

(defn login [req]
  (let [res (auth/create-auth-token (:body req))]
    (or (messages res)
        {:status 201 :body res})))

;;------------------Closed-Routes
(defn signup! [req]
  (messages
   (auth/register-user! (validate/sanitize-new-user-data (:body req)))))


(defn new-purchase [req]
  (println (validate/sanitize-new-purchase (:body req))))

;;-----------------Middlewares
(defn wrap-auth [handler]
  (fn [req]
    (let [user (auth/is-token-valid (:token (:body req)))]
      (if (nil? user)
        (messages :invalid-token)
        (handler (assoc (update-in req [:body] dissoc :token) :identity user))))))

;;-----------------Interface
(defroutes closed-routes
  (POST "/signup" [] signup!)
  (POST "/new-purchase" [] new-purchase)
  (POST "/new-payment" [] "PAYMENT"))

(defroutes app-routes
  (GET "/" [] home)
  (POST "/login" [] login)
  (wrap-routes closed-routes wrap-auth)
  (route/resources "/resources")
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
