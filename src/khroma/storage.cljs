(ns khroma.storage
  (:require
    [cljs.core.async :refer [chan >!]]
    [khroma.util :as util]
    [clojure.walk :as walk])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))


(def local js/chrome.storage.local)
(def sync js/chrome.storage.sync)

(defn set
  "Receives and object which gives each key/value pair to update storage with
  and saves it locally. It can optionally receive a function that gets called
  back when the value has been set.

  When getting, we will keywordize the values on the results, so you should
  only use as keys values that can be turned to keywords.

  See https://developer.chrome.com/extensions/storage#type-StorageArea"
  ([items]
   (set items nil))
  ([items on-complete]
   (.set js/chrome.storage.sync (clj->js items) on-complete)))


(defn get
  "Retrieves values from the extension storage. Returns a channel where we'll
  put the results.

  See https://developer.chrome.com/extensions/storage#type-StorageArea"
  ([]
   (get nil))
  ([keys]
   (let [ch (chan)]
     (.get js/chrome.storage.sync (clj->js keys)
           (fn [result]
             (go
               (>! ch (walk/keywordize-keys (js->clj result))))))
     ch))
  )


(defn bytes-in-use
  "Retrieves the total bytes in use."
  ([]
   (bytes-in-use nil))
  ([keys]
   (let [ch (chan)]
     (.getBytesInUse js/chrome.storage.sync (clj->js keys)
                     (fn [result]
                       (go
                         (>! ch result))))
     ch))
  )


(defn remove
  "Removes values from the extension's storage. Can receive an optional callback
  function that gets invoked when the removal is complete."
  ([keys]
   (remove keys nil))
  ([keys on-complete]
   (.remove js/chrome.storage.sync (clj->js keys) on-complete)))


(defn clear
  "Clears all extension storage."
  ([]
   (clear nil))
  ([on-complete]
   (.clear js/chrome.storage.sync on-complete)))

(defn on-changed
  "Raised when a storage change is detected.

  See https://developer.chrome.com/extensions/storage#event-onChanged
  "
  []
  (util/add-listener js/chrome.storage.onChanged :changes :areaName))