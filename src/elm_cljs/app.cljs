(ns elm-cljs.app
  (:require [elm-cljs.core :refer [main]]
            [elm-cljs.effects :refer [->Random ->Fetch]]))

(enable-console-print!)

;; --- Model ------------------------------------------------

(def model {:counter 0
            :loading false
            :text "World"})

;; --- Messages ------------------------------------------------

(defrecord Increment [])
(defrecord Decrement [])
(defrecord SetCounter [value])
(defrecord GenerateRandom [])
(defrecord FetchData [])
(defrecord DataFetched [value])
(defrecord SetText [text])

;; --- Update ------------------------------------------------

(defmulti update-model (fn [model message] (type message)))

(defmethod update-model Increment [model message]
  [(update model :counter inc) nil])

(defmethod update-model Decrement [model message]
  [(update model :counter dec) nil])

(defmethod update-model SetCounter [model message]
  [(assoc model :counter (:value message)) nil])

(defmethod update-model GenerateRandom [model message]
  [model (->Random 50 ->SetCounter)])

(defmethod update-model FetchData [model message]
  [(assoc model :loading true)
   (->Fetch "http://some.url.com" ->DataFetched)])

(defmethod update-model DataFetched [model message]
  [(assoc model :loading false :text (:value message)) nil])

(defmethod update-model SetText [model message]
  [(assoc model :text (:text message)) nil])

(defmethod update-model :default [model message]
  [model nil])

;; --- View ------------------------------------------------

(defn view [model]
  [:div {}
    [:p {}
      [:button {:onClick ->Decrement} "-"]
      (:counter model)
      [:button {:onClick ->Increment} "+"]]
    [:p {}
      [:button {:onClick ->GenerateRandom} "Random"]]
    [:h2 {} "Hello " (:text model)]
    [:p {}
      [:input {:onChange (fn [e] (->SetText (-> e .-target .-value)))
               :value (:text model)}]]
    [:p {}
      (if (:loading model)
        "Loading..."
        [:button {:onClick ->FetchData} "Fetch Data"])]])

;; --- Main ------------------------------------------------

(def root (js/document.getElementById "app"))

(main model view update-model root)
