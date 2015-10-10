(ns ^{:author "Ricardo J. Mendez"
      :doc    "Functions to access the browser's idle state.
      You will need to specify the idle permission on your manifest."}
  khroma.idle

  (:require [khroma.util :as kutil]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


(defn query-state
  "Returns a channel where we'll put the current idle state.

  https://developer.chrome.com/extensions/idle#method-queryState"
  [interval-seconds]
  (let [ch (async/chan)]
    (.queryState js/chrome.idle interval-seconds
                 (fn [state]
                   (async/put! ch (js->clj state)))) ch))


(defn set-detection-interval
  "Sets the state change detection interval for on-stage-changed.

  See https://developer.chrome.com/extensions/idle#method-setDetectionInterval"
  [interval-seconds]
  (.setDetectionInterval js/chrome.idle interval-seconds))


(defn on-state-changed
  "Returns a channel which will receive events when the idle state changes.

  See https://developer.chrome.com/extensions/idle#event-onStateChanged"
  []
  (kutil/add-listener js/chrome.idle.onStateChanged :newState))
