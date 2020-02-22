(ns state-backend.core
  (:require
   [state-backend.response :refer [create-response]]
   [clojure.data.json :as json]))

;; Read the initial state from the db
;; This is where we store the state of the user
;; The user id should come from a verified token
(defn initial-state
  "Create initial state"
  []
  {:title "Lorem Ipsum"
   :description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vel scelerisque ligula. Aliquam dapibus scelerisque ipsum sed blandit. Suspendisse consectetur commodo ipsum, at rhoncus nibh aliquet non. Vestibulum sit amet mi neque. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Aliquam nibh libero, molestie sit amet suscipit sit amet, gravida at purus. Sed ac luctus ligula, et volutpat mauris. Curabitur a mattis erat. Aenean vehicula sollicitudin libero, id tristique augue efficitur et. Maecenas ut dignissim ante."
   :form {}})

(def state (atom (initial-state)))

(defn set-state-key
  ""
  [key]
  (fn [payload]
    (swap!
     state
     (fn [next-state]
       (assoc next-state key payload)))))

(defn update-state-key
  ""
  [key]
  (fn [payload]
    (swap!
     state
     (fn [next-state]
       (assoc next-state key (merge (key next-state) payload))))))

(defn delete-state-key
  ""
  [key]
  (fn []
    (swap!
     state
     (fn [next-state]
       (dissoc next-state key)))))

;; Defines a mapping between an action type and
;; a function. No undefined action types should be allowed
(def action-map {"SET_DESCRIPTION" (set-state-key :description)
                 "UPDATE_FORM" (update-state-key :form)
                 "DELETE_TITLE" (delete-state-key :title)})

;; Dispatcher
(defn action-switch
  "Dispatch an action on the state"
  [action]
  ;; Handle all incoming dispatches
  (->
   action-map
   (get (:type action))
   (apply [(:payload action)])
   (create-response 200)))

(defn dispatch
  ""
  [request]
  (-> request
      (:body)
      (slurp)
      (json/read-str :key-fn keyword)
      (action-switch)))

(defn get-state
  "Get the current state"
  []
  (create-response @state 200))
