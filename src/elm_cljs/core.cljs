(ns elm-cljs.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan >! <!]]
            [quiescent.core :as q]
            [quiescent.dom :as d]))

(enable-console-print!)

;; ******************************************************************

(def messages (chan))

(def root (.getElementById js/document "app"))

(defn submit [message]
  (go (>! messages message)))

(defn main [initial-model view update]
  (go-loop [model initial-model]
           (println model)
           (q/render (view model) root)
           (recur (update model (<! messages)))))

;; ******************************************************************

(def model {:counter 0 :text "???"})

(defprotocol Action
  (run [this model]))

(defrecord Increment []
  Action
  (run [this model]
    (update model :counter inc)))

(defrecord Decrement []
  Action
  (run [this model]
    (update model :counter dec)))

(defrecord GenerateRandom []
  Action
  (run [this model] model))

(defrecord Type [text]
  Action
  (run [this model]
    (assoc model :text (:text this))))

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

(defn update [model message]
  (run message model))

(main model view update)
