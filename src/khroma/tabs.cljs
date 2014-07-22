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
        (go 
          (>! ch (js->clj tab))
          (async/close ch)))) ch))

(defn tab-updated-events []
  (let [ch (async/chan)]
    (.addListener js/chrome.tabs.onUpdated
      (fn [id info tab]
        (async/put! ch (walk/keywordize-keys (js->clj {:tabId id :changeInfo info :tab tab}))))) ch))

(defn tab-removed-events []
  (let [ch (async/chan)]
    (.addListener js/chrome.tabs.onRemoved
      (fn [id info]
        (async/put! ch (walk/keywordize-keys (js->clj {:tabId id :removeInfo info}))))) ch))

(defn tab-replaced-events []
  (let [ch (async/chan)]
    (.addListener js/chrome.tabs.onReplaced
      (fn [added removed]
        (async/put! ch (walk/keywordize-keys (js->clj {:added added :removed removed}))))) ch))