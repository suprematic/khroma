(ns khroma.tabs
  (:require 
    [khroma.log :as log]
    [cljs.core.async :as async])
  
  (:require-macros 
    [cljs.core.async.macros :refer [go go-loop]]))

;(defn- options->jsparams [options]
;  (clj->js
;    (map clj->js (filter (complement nil?) options))))

(defn get-tab [tab-id]
  (let [ch (async/chan)]
    (.get js/chrome.tabs tab-id
      (fn [tab]
        (go 
          (>! ch (js->clj tab))
          (async/close ch)))) ch))

(defn query [query-info]
  (let [ch (async/chan)]
    (.query js/chrome.tabs (clj->js query-info)
            (fn [tabs]
              (go 
                (>! ch (js->clj tabs))
                (async/close! ch)))) ch))

(defn send-message [tab-id message]
  (let [ch (async/chan 1)]
    (js/chrome.tabs.sendMessage tab-id (clj->js message)
                                (fn [res] 
                                  (when res 
                                    (go (async/>! ch (js->clj res))
                                        (async/close! ch)))))
    ch))

(defn- message-event [message sender response-fn]
  {:message (js->clj message) :sender (js->clj sender) :response-fn response-fn}) 
 
(defn messages []
  (let [ch (async/chan)]    
    (.addListener js/chrome.runtime.onMessage 
      (fn [message sender reply-fn]
        (log/log "A tab message" message)
        (go
          (>! ch (message-event message sender reply-fn)))
        true))
    ch))
