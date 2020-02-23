(defproject state-backend "0.1.0-SNAPSHOT"
  :description "State backend for web apps"

  :url "https://github.com/fippli/clojure-state-backend"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring "1.8.0"]
                 [compojure "1.6.1"]
                 [org.clojure/data.json "0.2.6"]
                 [buddy/buddy-sign "3.1.0"]]

  :main ^:skip-aot state-backend.main

  :target-path "target/%s"

  :plugins [[lein-ring "0.12.5"]]

  :ring {:handler state-backend.main/request-handler}

  :profiles {:uberjar {:aot :all}})
