(ns oeconomica-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer (response)]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [oeconomica-api.auth :as auth]
            [oeconomica-api.validation :as refiner]
            [oeconomica-api.store.transactions :as transaction-store]
            [oeconomica-api.messages :refer [messages]]))

;TODO: validate db insertions
;;----------------Open-Routes
(defn home [req] "Welcome to Oeconomica API!")

(defn login [req]
  (let [res (auth/create-auth-token (:body req))]
    (or (messages res)
        {:status 201 :body res})))

;;------------------Closed-Routes
(defn signup! [req]
  (let [user-data (refiner/sanitize-new-user (:body req))]
    (if (keyword? user-data)
      (messages user-data)
      (messages (auth/register-user! user-data)))))

(defn new-purchase [req]
  (let [purchase-data (refiner/sanitize-new-purchase
                       (assoc (:body req)
                              :spender (:identity req)))]
    (if (keyword? purchase-data)
      (messages purchase-data)
      (messages (transaction-store/register-new-purchase! purchase-data)))))

;;-----------------Middlewares
(defn wrap-auth [handler]
  (fn [req]
    (let [user (auth/is-token-valid (:token (:body req)))]
      (if (nil? user)
        (messages :invalid-token)
        (handler (assoc (update-in req [:body]
                                   dissoc :token) :identity user))))))

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
      (wrap-cors :access-control-allow-origin [#"http://127.0.0.1:3000"]
                 :access-control-allow-methods [:get :post])
      (wrap-json-body {:keywords? true})
      wrap-json-response))
