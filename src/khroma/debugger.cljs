(ns khroma.debugger
  (:require
    [cljs.core.async :as async]
    [khroma.util :as util])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn attach [target required-version]
  (util/with-callback
    #(.attach js/chrome.debugger target required-version %)))

(defn detach [target]
  (util/with-callback
    #(.detach js/chrome.debugger target %)))

(defn send-command [target method command-params]
  (util/with-callback
    #(.sendCommand js/chrome.debugger target method command-params %)))

(defn get-targets []
  (util/with-callback
    #(.getTargets js/chrome.debugger %)))