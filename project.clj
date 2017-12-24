(defproject oeconomica-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [environ "1.1.0"]
                 [com.novemberain/monger "3.1.0"]
                 [org.slf4j/slf4j-nop "1.7.12"]
                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [cheshire "5.8.0"]
                 [ring/ring-mock "0.3.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 ]
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]]
  :ring {:handler oeconomica-api.handler/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}
             :test {}
             :default [:base :system :user :provided :dev :local-dev]
             }
  :aliases {"test" ["with-profile" "+local-test" "test"]})
