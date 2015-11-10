(ns ^{:author "Ricardo J. Mendez"
      :doc    "Functions to access Chrome's alarm functionality.
      You will need to specify the alarms permission on your manifest."} khroma.alarms
  (:require [khroma.log :as console]
            [khroma.util :as kutil]
            [clojure.walk :as walk]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


;;;;------------------------------
;;;; Functions
;;;;------------------------------

(defn create
  "Creates a new alarm with the specified properties.  See Chrome's
  documentation for how to create one-off or recurrent alarms.

  See https://developer.chrome.com/extensions/alarms#method-create"
  [name alarm-info]
  (.create js/chrome.alarms name (clj->js alarm-info)))


(defn clear
  "Clears an alarm by name

  See https://developer.chrome.com/extensions/alarms#method-clear"
  [name]
  (.clear js/chrome.alarms name))


(defn clear-all
  "Clears all alarms

  See https://developer.chrome.com/extensions/alarms#method-clearAll"
  []
  (.clearAll js/chrome.alarms))


(defn get
  "Returns a channel where we'll put an alarm's data from its name.

  See https://developer.chrome.com/extensions/alarms#method-get"
  [name]
  (let [ch (async/chan)]
    (.get js/chrome.alarms name
          (fn [alarm]
            (async/put! ch (walk/keywordize-keys (js->clj alarm)))))
    ch))


(defn get-all
  "Returns a channel where we'll put an array for all alarms

  See https://developer.chrome.com/extensions/alarms#method-getAll"
  []
  (let [ch (async/chan)]
    (.getAll js/chrome.alarms
             (fn [alarms]
               (async/put! ch (walk/keywordize-keys (js->clj alarms)))))
    ch))

;;;;------------------------------
;;;; Event handlers
;;;;------------------------------

(defn on-alarm
  "Receives events when an alarm is triggered.

  See https://developer.chrome.com/extensions/alarms#event-onAlarm"
  []
  (kutil/add-listener js/chrome.alarms.onAlarm :alarm))