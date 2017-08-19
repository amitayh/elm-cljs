(ns elm-cljs.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [>! <!]]
            [elm-cljs.channels :refer [messages effects]]
            [elm-cljs.render :refer [render]]))

(defn- print-update [action old-model new-model effect]
  (js/console.groupCollapsed action)
  (js/console.info old-model)
  (js/console.info new-model)
  (js/console.info effect)
  (js/console.groupEnd))

(defn main [initial-model view update root]
  (render (view initial-model) root)
  (go-loop [model initial-model]
    (let [message (<! messages)
          [updated-model effect] (update model message)]
      ;(print-update message model updated-model effect)
      (if-not (= model updated-model) (render (view updated-model) root))
      (if-not (nil? effect) (>! effects effect))
      (recur updated-model))))
