(ns khroma.runtime
  (:require
            [khroma.log :as log]
            [khroma.messaging :as messaging]
            [khroma.util :as kutil]
            [cljs.core.async :as async])
  (:require-macros
            [cljs.core.async.macros :refer [go go-loop]]))


(def available?
  (not (nil? js/chrome.runtime)))

(def manifest
  (delay
    (js->clj
      (.getManifest js/chrome.runtime))))

(defn connect
  "Attempts to connect to connect listeners within an extension/app.

  See https://developer.chrome.com/extensions/runtime#method-connect"
  [& options]
  (kmessaging/channel-from-port
    (let [{:keys [extensionId connectInfo]} (apply hash-map options)]
      (.apply
        js/chrome.runtime.connect js/chrome.runtime
        (kutil/options->jsparams [extensionId connectInfo])))))

(defn connections
  "Fired when a connection is made from either an extension process
  or a content script.

  See https://developer.chrome.com/extensions/runtime#event-onConnect"
  []
  (let [c (messaging/chan)]
    (.addListener js/chrome.runtime.onConnect
      (fn [port]
        (go
          (async/>! c (messaging/channel-from-port port)))))
    c))


(defn- message-event [message sender response-fn]
  {:message (js->clj message) :sender (js->clj sender) :response-fn response-fn})

(defn messages []
  (let [ch (messaging/chan)]
    (.addListener js/chrome.runtime.onMessage
      (fn [message sender reply-fn]
        (go
          (async/>! ch (message-event message sender reply-fn)))))
    ch))

(defn send-message [message & options]
  (let [{:keys [extensionId options responseCallback]} (apply hash-map options)]
    (.apply
      js/chrome.runtime.sendMessage js/chrome.runtime
        (kutil/options->jsparams
          [extensionId message options responseCallback]))))
