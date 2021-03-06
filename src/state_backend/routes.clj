(ns state-backend.routes
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes
                           GET
                           POST]]
   [state-backend.core :refer [dispatch
                               get-state
                               create-user-state]]))

(defroutes routes
  (POST "/dispatch" request (dispatch request))

  (GET "/state" [] (get-state))

  (POST "/state" request (create-user-state request))

  (GET "/health" [] "Authentication service health ok!")

  (route/not-found "<h1>Not found</h1>"))
