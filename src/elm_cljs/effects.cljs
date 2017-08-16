(ns elm-cljs.effects
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <!]]
            [elm-cljs.channels :refer [messages effects]]))

(defprotocol Effect
  (run [this]))

(defrecord Random [max callback]
  Effect
  (run [this]
    ((:callback this) (rand-int 100))))

(go
  (while true
    (let [effect (<! effects)
          message (run effect)]
      (if-not (nil? message) (>! messages message)))))
