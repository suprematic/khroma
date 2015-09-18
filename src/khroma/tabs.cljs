(ns khroma.tabs
  (:require
    [khroma.log :as log]
    [clojure.walk :as walk]
    [cljs.core.async :as async])

  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(defn get-tab [tab-id]
  (let [ch (async/chan)]
    (.get js/chrome.tabs tab-id
      (fn [tab]
        (async/put! ch (walk/keywordize-keys (js->clj {:tab tab}))))) ch))

(defn get-active-tab []
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

(defn- tab-action-events [instance & key-args]
  (let [ch (async/chan)]
      (.addListener instance
        (fn [& val-args]
          (async/put! ch (walk/keywordize-keys (js->clj (zipmap key-args val-args)))))) ch))

(defn tab-updated-events []
  (tab-action-events js/chrome.tabs.onUpdated :tabId :changeInfo :tab))

(defn tab-removed-events []
  (tab-action-events js/chrome.tabs.onRemoved :tabId :removeInfo))

(defn tab-replaced-events []
  (tab-action-events js/chrome.tabs.onReplaced :added :removed))