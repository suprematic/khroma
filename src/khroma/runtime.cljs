(ns khroma.runtime
  (:require [khroma.messaging :as messaging]
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
  (messaging/channel-from-port
    (let [{:keys [extensionId connectInfo]} (apply hash-map options)]
      (.apply
        js/chrome.runtime.connect js/chrome.runtime
        (kutil/options->jsparams [extensionId connectInfo])))))


(defn connections "DEPRECATED" []
  (kutil/deprecated on-connect "runtime/on-connect"))


(defn messages "DEPRECATED" []
  (kutil/deprecated on-message "runtime/on-message"))

(defn send-message [message & options]
  (let [{:keys [extensionId options responseCallback]} (apply hash-map options)]
    (.apply
      js/chrome.runtime.sendMessage js/chrome.runtime
        (kutil/options->jsparams
          [extensionId message options responseCallback]))))



;;;;------------------------------
;;;; Event handlers
;;;;------------------------------

(defn- message-event [message sender response-fn]
  {:message (js->clj message) :sender (js->clj sender) :response-fn response-fn})

(defn on-connect
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


(defn on-connect-external
  "Fired when a connection is made from another extension.

  See https://developer.chrome.com/extensions/runtime#event-onConnectExternal"
  []
  (let [c (messaging/chan)]
    (.addListener js/chrome.runtime.onConnectExternal
                  (fn [port]
                    (go
                      (async/>! c (messaging/channel-from-port port)))))
    c))


(defn on-installed
  "Fired when the extension is installed

  See https://developer.chrome.com/extensions/runtime#event-onInstalled"
  []
  (kutil/add-listener js/chrome.runtime.onInstalled :details))


(defn on-message
  "Fired when a message is sent to the runtime.

  See https://developer.chrome.com/extensions/runtime#event-onMessage"
  []
  (let [ch (messaging/chan)]
    (.addListener js/chrome.runtime.onMessage
                  (fn [message sender reply-fn]
                    (go
                      (async/>! ch (message-event message sender reply-fn)))))
    ch))


(defn on-message-external
  "Fired when a message is sent to the runtime from another application

  See https://developer.chrome.com/extensions/runtime#event-onMessageExternal"
  []
  (let [ch (messaging/chan)]
    (.addListener js/chrome.runtime.onMessageExternal
                  (fn [message sender reply-fn]
                    (go
                      (async/>! ch (message-event message sender reply-fn)))))
    ch))


(defn on-restart-required
  "Fired when we need to restart.

  See https://developer.chrome.com/extensions/runtime#event-onRestartRequired"
  []
  (kutil/add-listener js/chrome.runtime.onRestartRequired :reason))


(defn on-startup
  "Fired when a profile using the extension starts up.

  This event doesn't get any parameters, so we'll just send a
  :startup keyword to the channel returned by this function.
  I expect it's good manners to close the channel then, seeing
  as how we're unlikely to get it more than once.

  See https://developer.chrome.com/extensions/runtime#event-onStartup"
  []
  (let [ch (messaging/chan)]
    (.addListener js/chrome.runtime.onStartup
                  (fn []
                    (go
                      (async/>! ch :startup))))
    ch))

(defn on-suspend
  "Fired when the background page is about to be suspended.
  This event doesn't get any parameters, so we'll just send a
  :suspend keyword to the channel returned by this function

  See https://developer.chrome.com/extensions/runtime#event-onSuspend"
  []
  (let [ch (messaging/chan)]
    (.addListener js/chrome.runtime.onSuspend
                  (fn []
                    (go
                      (async/>! ch :suspend))))
    ch))

(defn on-suspend-canceled
  "Fired when the background page's suspend operation was canceled.
  This event doesn't get any parameters, so we'll just send a
  :suspend-canceled keyword to the channel returned by this function.

  https://developer.chrome.com/extensions/runtime#event-onSuspendCanceled"
  []
  (let [ch (messaging/chan)]
    (.addListener js/chrome.runtime.onSuspendCanceled
                  (fn []
                    (go
                      (async/>! ch :suspend-canceled))))
    ch))


(defn on-update-available
  "Fired when the extension has an update available which isn't immediately
  installed.

  See https://developer.chrome.com/extensions/runtime#event-onUpdateAvailable"
  []
  (kutil/add-listener js/chrome.runtime.onUpdateAvailable :details))