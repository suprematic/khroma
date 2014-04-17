(ns khroma.tabs
  (:require 
    [khroma.log :as log]
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