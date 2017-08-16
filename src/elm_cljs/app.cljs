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

(defprotocol Action
  (run [this model]))

(defrecord Increment []
  Action
  (run [this model]
    [(update model :counter inc) nil]))

(defrecord Decrement []
  Action
  (run [this model]
    [(update model :counter dec) nil]))

(defrecord SetCounter [value]
  Action
  (run [this model]
    [(assoc model :counter value) nil]))

(defrecord GenerateRandom []
  Action
  (run [this model]
    [model (->Random 50 ->SetCounter)]))

(defrecord Type [text]
  Action
  (run [this model]
    [(assoc model :text (:text this)) nil]))

(defn update [model message]
  (run message model))

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

(main model view update root)
