(ns oeconomica-api.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as cheshire]
            [oeconomica-api.testhelpers :as h ]
            [oeconomica-api.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Welcome to Oeconomica API!"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing "login route"
    (h/create-test-user)
    (let [valid-req (app (-> (mock/request :post "/login")
                             (mock/content-type "application/json")
                             (mock/body (cheshire/generate-string
                              {:name "test user"
                               :password "1234"}))))
          bad-password-req (app (-> (mock/request :post "/login")
                                    (mock/content-type "application/json")
                                    (mock/body (cheshire/generate-string
                                                {:name "test user"
                                                 :password "12"}))))
          bad-name-req (app (-> (mock/request :post "/login")
                                    (mock/content-type "application/json")
                                    (mock/body (cheshire/generate-string
                                                {:name "tttttttt"
                                                 :password "1234"}))))
          bad-req (app (-> (mock/request :post "/login")
                           (mock/content-type "application/json")
                           (mock/body (cheshire/generate-string
                                       {:name "test user"}))))]
      (is (boolean (re-find #"\btoken\b" (:body valid-req))))
      (is (= (:body bad-password-req) "Invalid name or password"))
      (is (= (:body bad-name-req) "Invalid name or password"))
      (is (= (:body bad-req) "Invalid name or password"))
      )
    (h/clear-users)
    )

  ;TODO: for signup testing I have to find a way to mock tokens
  (testing "signup route")
  )
