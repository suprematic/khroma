(ns khroma.tabs
  (:require [khroma.log :as log]
            [khroma.util :as kutil]
            [clojure.walk :as walk]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn get-tab
  "Returns a channel where we'll put a tab' information from its id"
  [tab-id]
  (let [ch (async/chan)]
    (.get js/chrome.tabs tab-id
      (fn [tab]
        (async/put! ch (walk/keywordize-keys (js->clj {:tab tab}))))) ch))

(defn get-active-tab
  "Returns a channel where we'll put the information for the current tab"
  []
  (let [ch (async/chan)]
    (.query js/chrome.tabs #js {:active true :currentWindow true}
      (fn [result]
        (when-let [tab (first result)]
          (async/put! ch (walk/keywordize-keys (js->clj {:tab tab})))))) ch))

(defn create
  "Creates a new tab with the specified properties"
  ([]
   (create {} nil))
  ([props]
   (create props nil))
  ([props callback]
   (.create js/chrome.tabs (clj->js props) callback)))


(defn tab-created-events
  "Receives events when a tab is created."
  []
  (kutil/add-listener js/chrome.tabs.onCreated :tab))

(defn tab-updated-events
  "Receives events when a tab is updated. This will include changing the URL,
  title or any content, not only creation. It will not fire when a tab is
  removed."
  []
  (kutil/add-listener js/chrome.tabs.onUpdated :tabId :changeInfo :tab))

(defn tab-removed-events
  "Receives events when a tab is removed."
  []
  (kutil/add-listener js/chrome.tabs.onRemoved :tabId :removeInfo))

(defn tab-replaced-events
  "Receives events when a tab is replaced with another tab."
  []
  (kutil/add-listener js/chrome.tabs.onReplaced :added :removed))
