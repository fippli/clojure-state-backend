(ns state-backend.main
  (:require
   [ring.adapter.jetty :refer [run-jetty]]
   [state-backend.routes :refer [routes]]))

(defn request-handler
  "Handle all requests"
  [requests]
  (println ">" (:remote-addr requests) (:request-method requests) (:uri requests))
  (routes requests))

(defn -main
  ""
  [& args]
  (run-jetty request-handler {:port 80}))
