(ns elm-cljs.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan >! <!]]
            [quiescent.core :as q]
            [quiescent.dom :as d]))

(enable-console-print!)

;; ******************************************************************

(def messages (chan))

(def effects (chan))

(defprotocol Effect
  (run-effect [this]))

(defrecord Random [callback]
  Effect
  (run-effect [this]
    ((:callback this) (rand-int 100))))

(defrecord Print []
  Effect
  (run-effect [this]
    (println "Printed!")))

(defn printaction [action old-model new-model effect]
  (js/console.groupCollapsed action)
  (js/console.info old-model)
  (js/console.info new-model)
  (js/console.info effect)
  (js/console.groupEnd))

(go
  (while true
    (let [effect (<! effects)
          message (run-effect effect)]
      (if-not (nil? message) (>! messages message)))))

(def root (.getElementById js/document "app"))

(defn submit [message]
  (go (>! messages message)))

(defn main [initial-model view update]
  (go-loop [model initial-model]
           (q/render (view model) root)
           (let [message (<! messages)
                 [updated-model effect] (update model message)]
             (printaction message model updated-model effect)
             (if-not (nil? effect) (>! effects effect))
             (recur updated-model))))

;; ******************************************************************

(def model {:counter 0 :text "???"})

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
    [model (->Random ->SetCounter)]))

(defrecord PrintSomething []
  Action
  (run [this model]
    [model (->Print)]))

(defrecord Type [text]
  Action
  (run [this model]
    [(assoc model :text (:text this)) nil]))

(defn view [model]
  (d/div {}
         (d/p {}
              (d/button {:onClick (fn [e] (submit (->Decrement)))} "-")
              (:counter model)
              (d/button {:onClick (fn [e] (submit (->Increment)))} "+"))
         (d/p {} (d/button {:onClick (fn [e] (submit (->GenerateRandom)))} "Random"))
         (d/p {} (d/button {:onClick (fn [e] (submit (->PrintSomething)))} "Print something"))
         (d/p {}
              (d/input {:onChange (fn [e] (let [value (-> e (.-target) (.-value))]
                                            (submit (->Type value))))
                        :value    (:text model)}))))

(defn update [model message]
  (run message model))

(main model view update)
