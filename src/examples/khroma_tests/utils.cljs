(ns khroma-tests.utils
  (:require
    [cljs.core.async :refer [>! <! close!]])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(defn atom-channel
  "Receives a channel-returning function, and returns an atom that will
  be swapped with a channel's return value."
  [chan-f]
  (let [a (atom [])]
    (go
      (let [channel (chan-f)]
        (reset! a (<! channel))
        (close! channel)))
    a))

(defn atom-channel-loop
  "Receives a channel-returning function, and returns an atom that new
  messages from the channel will be swapped with (including a timestamp)."
  [chan-f]
  (let [a (atom {})]
    (go-loop
      [channel (chan-f)]
      (reset! a {:date (js/Date.)
                 :message (<! channel)})
      (recur channel))
    a))

(defn atom-channel-loop-append
  "Receives a channel-returning function, and returns an atom with a vector
  that new messages from the channel will be conjoined to."
  [chan-f]
  (let [a (atom [])]
    (go-loop
      [channel (chan-f)]
      (swap! a conj (<! channel))
      (recur channel))
    a))