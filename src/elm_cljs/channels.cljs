(ns elm-cljs.channels
  (:require [cljs.core.async :refer [chan]]))

(def messages (chan))

(def effects (chan))
