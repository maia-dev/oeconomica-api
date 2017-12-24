(ns oeconomica-api.store.connections
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]))

(def ds {:host (env :ds-host)
         :port (Integer. (env :ds-port))
         :db (env :ds-db)})
(def conn (mg/connect {:host (:host ds) :port (:port ds)}))
(def db (mg/get-db conn (:db ds)))
