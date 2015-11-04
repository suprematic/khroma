(ns ^{:author "Ricardo J. Mendez"
      :doc    "Functions to access Chrome's storage from an extension.
      You will need to specify the storage permission on your manifest."} khroma.storage
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
  and saves it. It can optionally receive a function that gets called back when
  the value has been set.

  Use storage/local or storage/sync for the area. If no storage area is
  indicated, it defaults to the local storage.

  IMPORTANT: When getting, we will keywordize the values on the results, so you
  should only use as keys values that can be turned to keywords.\n\n

  See https://developer.chrome.com/extensions/storage#type-StorageArea"
  ([items]
   (set items local nil))
  ([items area]
   (set items area nil))
  ([items area on-complete]
   (.set area (clj->js items) on-complete)))


(defn get
  "Retrieves values from the extension storage. Returns a channel where we'll
  put the results.

  If no storage area is indicated, it defaults to the local storage.

  IMPORTANT: When getting, we  keywordize the values on the results, so you
  should only have used as keys values that can be turned to keywords.

  See https://developer.chrome.com/extensions/storage#type-StorageArea"
  ([]
   (get nil local))
  ([keys]
   (get keys local))
  ([keys area & opts]
   (let [ch     (chan)
         {:keys [key-fn]} opts
         walk-f (or key-fn keyword)]
     (.get area (clj->js keys)
           (fn [result]
             (go
               (>! ch (walk/postwalk (fn [x]
                                       (if (map? x)
                                         (into {} (map #(vector (walk-f (key %))
                                                                (val %))
                                                       x))
                                         x))
                                     (js->clj result))))))
     ch))
  )


(defn bytes-in-use
  "Retrieves the total bytes in use in an area.

  See https://developer.chrome.com/extensions/storage#type-StorageArea"
  ([]
   (bytes-in-use nil local))
  ([keys]
   (bytes-in-use keys local))
  ([keys area]
   (let [ch (chan)]
     (.getBytesInUse area (clj->js keys)
                     (fn [result]
                       (go
                         (>! ch result))))
     ch))
  )


(defn remove
  "Removes values from the extension's storage. Can receive an optional callback
  function that gets invoked when the removal is complete."
  ([keys]
   (remove keys local nil))
  ([keys area]
   (remove keys area nil))
  ([keys area on-complete]
   (.remove area (clj->js keys) on-complete)))


(defn clear
  "Clears all extension storage."
  ([]
   (clear local nil))
  ([area]
   (clear area nil))
  ([area on-complete]
   (.clear area on-complete)))

(defn on-changed
  "Raised when a storage change is detected.

  See https://developer.chrome.com/extensions/storage#event-onChanged
  "
  []
  (util/add-listener js/chrome.storage.onChanged :changes :areaName))