(ns elm-cljs.effects
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! timeout]]
            [elm-cljs.channels :refer [messages effects]]))

(defprotocol Effect
  (run [this messages effects]))

(defrecord Random [max callback]
  Effect
  (run [this messages effects]
    (go
      (>! messages ((:callback this) (rand-int 100))))))

(defn- rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

; Simulating HTTP call...
(defrecord Fetch [url callback]
  Effect
  (run [this messages effects]
    (go
      (<! (timeout 1000))
      (>! messages ((:callback this) (rand-str 8))))))

(go
  (while true
    (run (<! effects) messages effects)))
