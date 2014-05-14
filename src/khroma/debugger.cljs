(ns khromia.debugger
  (:require
    [cljs.core.async :as async])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn attach [target required-version]
  (with-callback
    #(.attach js/chrome.debugger target required-version %)))

(defn detach [target]
  (with-callback
    #(.detach js/chrome.debugger target %)))

(defn send-command [target method command-params]
  (with-callback
    #(.sendCommand js/chrome.debugger target method command-params %)))

(defn get-targets
  (with-callback
    #(.getTargets js/chrome.debugger %)))

(defn- with-callback [f]
  (let [ch (async/chan)]
    (f (make-handler ch)) ch))

(defn- make-handler [ch]
  (fn [& data]
    (go
      (if-not (nil? data) (>! ch (js->clj data)))
      (async/close ch))))