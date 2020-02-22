(ns state-backend.core
  (:require
   [state-backend.response :refer [create-response]]
   [clojure.data.json :as json]
   [clojure.string]))

;; Read the initial state from the db
;; This is where we store the state of the user
;; The user id should come from a verified token
(defn initial-state
  "Create initial state"
  []
  {:initial-state-message " This is the initial state message to see that all is ok!"})

(def state (atom (initial-state)))

(defn set-state-key!
  ""
  [key payload]
  (swap!
   state
   (fn [next-state]
     (assoc next-state key payload))))

(defn update-state-key!
  ""
  [key payload]
  (swap!
   state
   (fn [next-state]
     (assoc next-state key (merge (key next-state) payload)))))

(defn delete-state-key!
  ""
  [key]
  (swap!
   state
   (fn [next-state]
     (dissoc next-state key))))

;; Defines a mapping between an action type and
;; a function. No undefined action types should be allowed
(defn action-map
  ""
  [func-key key payload]
  (case func-key
    :set (set-state-key! key payload)
    :update (update-state-key! key payload)
    :delete (delete-state-key! key)
    @state))

(defn parse-action
  ""
  [action]
  (as-> action $
    (:type $)
    (clojure.string/split $ #"\_")
    (action-map
     ;; fn-key
     (keyword (clojure.string/lower-case (first $)))
     ;; key
     (keyword (clojure.string/lower-case (second $)))
     ;; payload
     (:payload action))))


(defn dispatch
  ""
  [request]
  (-> request
      (:body)
      (slurp)
      (json/read-str :key-fn keyword)
      (parse-action)
      (create-response 200)))

(defn get-state
  "Get the current state"
  []
  (create-response @state 200))
