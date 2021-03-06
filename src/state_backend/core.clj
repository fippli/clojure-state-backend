(ns state-backend.core
  (:require
   [state-backend.response :refer [create-response]]
   [clojure.data.json :as json]
   [clojure.string]
   [buddy.sign.jwt :as jwt]))

;; Read the initial state from the db
;; This is where we store the state of the user
;; The user id should come from a verified token
(defn initial-state
  "Create initial state"
  []
  {:initial-state-message "This is the initial state message to see that all is ok!"})

(def state (atom {}))

(defn create-user-state
  ""
  ([user-id data]
   (swap! state (fn [next-state]
                  (assoc next-state (keyword user-id) data))))
  ([user-id]
   (swap! state (fn [next-state]
                  (assoc next-state (keyword user-id) {})))))

(defn set-state-key!
  ""
  [key payload user-id]
  (user-id (swap!
            state
            (fn [next-state]
              (assoc-in next-state [user-id key] payload)))))

(defn update-state-key!
  ""
  [key payload user-id]
  (user-id (swap!
            state
            (fn [next-state]
              (assoc-in next-state [user-id key] (merge (key next-state) payload))))))

(defn delete-state-key!
  ""
  [key user-id]
  (user-id (swap!
            state
            (fn [next-state]
              (update-in next-state [user-id] dissoc key)))))

;; Defines a mapping between an action type and
;; a function. No undefined action types should be allowed
(defn action-map
  ""
  [func-key key payload user-id]
  (case func-key
    :set (set-state-key! key payload user-id)
    :update (update-state-key! key payload user-id)
    :delete (delete-state-key! key user-id)
    @state))

(defn parse-action
  ""
  [action user-id]
  (as-> action $
    (:type $)
    (clojure.string/split $ #"\_") ; SET_FIELD -> [ SET FIELD ]
    (action-map
     ;; fn-key
     (keyword (clojure.string/lower-case (first $))) ; -> :set
     ;; key
     (keyword (clojure.string/lower-case (second $))) ; -> :field
     ;; payload
     (:payload action)
     ;; user id
     user-id)))

(defn authorize-token
  ""
  [request secret]
  (->
   request
   (:headers)
   (get "authorization")
   (clojure.string/split #" ")
   (nth 1)
   (jwt/unsign secret)))

(defn get-user-id!
  "Get the user id from the request token"
  [request]
  ;; temp solution for testing
  (->
   request
   (authorize-token (System/getenv "TOKEN_SECRET"))
   (:user-id)
   (keyword)))

(defn dispatch
  ""
  [request]
  (as-> (slurp (:body request)) $
    (json/read-str $ :key-fn keyword)
    (parse-action $ (get-user-id! request))
    (create-response $ 200)))

(defn get-state
  "Get the current state"
  []
  (create-response @state 200))
