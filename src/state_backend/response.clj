(ns state-backend.response
  (:require
   [clojure.data.json :as json]))

(defn create-response
  [data status]
  (case status
    200 {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/write-str data)}))
