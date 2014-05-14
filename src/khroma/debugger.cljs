(ns khromia.debugger
  (:require
    [cljs.core.async :as async])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn attach [target required-version]
  (with-callback-without-params
    #(.attach js/chrome.debugger target required-version %)))

(defn detach [target]
  (with-callback-without-params
    #(.detach js/chrome.debugger target %)))

(defn send-command [target method command-params]
  (with-callback-with-params
    #(.sendCommand js/chrome.debugger target method command-params %)))

(defn get-targets
  (with-callback-with-params
    #(.getTargets js/chrome.debugger %)))

(defn- with-callback-without-params [f]
  (let [ch (async/chan)]
    (f (make-handler-without-params ch)) ch))

(defn- with-callback-with-params [f]
  (let [ch (async/chan)]
    (f (make-handler-with-params ch)) ch))

(defn- make-handler-without-params [ch]
  #(go (async/close ch)))

(defn- make-handler-with-params [ch]
  (fn [data]
    (go
      (>! ch data)
      (async/close ch))))