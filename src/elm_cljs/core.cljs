(ns elm-cljs.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan >! <!]]
            [quiescent.core :as q]
            [elm-cljs.channels :refer [messages effects]]))

(defn- print-update [action old-model new-model effect]
  (js/console.groupCollapsed action)
  (js/console.info old-model)
  (js/console.info new-model)
  (js/console.info effect)
  (js/console.groupEnd))

(defn submit [message]
  (go (>! messages message)))

(defn main [initial-model view update root]
  (go-loop [model initial-model]
           (q/render (view model) root)
           (let [message (<! messages)
                 [updated-model effect] (update model message)]
             (print-update message model updated-model effect)
             (if-not (nil? effect) (>! effects effect))
             (recur updated-model))))
