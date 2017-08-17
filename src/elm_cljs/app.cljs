(ns elm-cljs.app
  (:require [elm-cljs.core :refer [main submit]]
            [elm-cljs.effects :refer [->Random]]
            [quiescent.dom :as d]))

(enable-console-print!)

;; --- Model ------------------------------------------------

(def model {:counter 0
            :loading false
            :text "???"})

;; --- Actions ------------------------------------------------

(defrecord Increment [])

(defrecord Decrement [])

(defrecord SetCounter [value])

(defrecord GenerateRandom [])

(defrecord Type [text])

;; --- Update ------------------------------------------------

(defmulti update-state (fn [model message] (type message)))

(defmethod update-state Increment [model message]
  [(update model :counter inc) nil])

(defmethod update-state Decrement [model message]
  [(update model :counter dec) nil])

(defmethod update-state SetCounter [model message]
  [(assoc model :counter (:value message)) nil])

(defmethod update-state GenerateRandom [model message]
  [model (->Random 50 ->SetCounter)])

(defmethod update-state Type [model message]
  [(assoc model :text (:text message)) nil])

(defmethod update-state :default [model message]
  [model nil])

;; --- View ------------------------------------------------

(defn view [model]
  (d/div {}
         (d/p {}
              (d/button {:onClick (fn [e] (submit (->Decrement)))} "-")
              (:counter model)
              (d/button {:onClick (fn [e] (submit (->Increment)))} "+"))
         (d/p {} (d/button {:onClick (fn [e] (submit (->GenerateRandom)))} "Random"))
         (d/p {}
              (d/input {:onChange (fn [e] (let [value (-> e (.-target) (.-value))]
                                            (submit (->Type value))))
                        :value    (:text model)}))))

;; --- Main ------------------------------------------------

(def root (.getElementById js/document "app"))

(main model view update-state root)
