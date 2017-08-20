(ns elm-cljs.render
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>!]]
            [clojure.string :as str]
            [elm-cljs.channels :refer [messages]]
            [react :refer [createElement]]
            [react-dom :as react-dom]))

(defn- callback? [key]
  (str/starts-with? (name key) "on"))

(defn- wrap-callback [callback]
  (fn [e]
    (let [message (callback e)]
      (go (>! messages message)))))

(defn- to-prop [key value]
  (if (callback? key)
    (wrap-callback value)
    value))

(defn- props-reducer [acc [key value]]
  (aset acc (name key) (to-prop key value))
  acc)

(defn- to-props [props]
  (reduce props-reducer (js-obj) props))

(declare to-react)

(defn- create-element [[tag props & children]]
  (apply createElement (name tag) (to-props props) (map to-react children)))

(defn- to-react [view]
  (if (vector? view)
    (create-element view)
    view))

(defn render [view el]
  (react-dom/render (to-react view) el))
