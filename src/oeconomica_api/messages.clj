(ns oeconomica-api.messages)

(def messages {:created {:status 201 :body "Created"}
               :user-created {:status 201 :body "User Created"}
               :error-inserting-user {:status 500 :body "Error Inserting user"}
               :user-exists {:status 400 :body "User exists"}
               :bad-data {:status 400 :body "Invalid data"}
               :bad-request {:status 400 :body "Bad request"}
               :invalid-name-password {:status 401
                                       :body "Invalid name or password"}
               :invalid-token {:status 403 :body "Invalid token"}})

