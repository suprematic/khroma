(ns khroma.runtime
  (:require 
            [khroma.log :as log]
            [cljs.core.async :as async]
            [cljs.core.async.impl.protocols :as p])
  
  (:require-macros 
            [cljs.core.async.macros :refer [go go-loop]]))


(def available?
  (not (nil? js/chrome.runtime)))

(def manifest
  (delay 
    (js->clj 
      (.getManifest js/chrome.runtime))))

(defn- options->jsparams [options]
  (clj->js
    (map clj->js (filter (complement nil?) options))))

(def chan
  (partial async/chan 100)) 

(defprotocol IChromePort
  (port-name [this]))

(defn channel-from-port [port]
  (let [in (chan) out (chan)]
    (go-loop [message (<! out)]
      (when message
        (.postMessage port (clj->js message)) 
        (recur (<! out))))    
    
    (.addListener (.-onMessage port) 
      (fn [message sender response-fn]
        (go (>! in (js->clj message)))))
    
    (.addListener (.-onDisconnect port)
      (fn []
        (async/close! in)
        (async/close! out)))
       
    (reify
      p/ReadPort
      (take! [_ handler]
        (p/take! in handler))

      p/WritePort
      (put! [_ message handler]
        (p/put! out message handler))

      p/Channel
      (close! [_]
        (p/close! in)
        (p/close! out)
        (.disconnect port))
      
      IChromePort
      (port-name [_]
        (.-name port)))))

(defn connect [& options]
  (channel-from-port
    (let [{:keys [extensionId connectInfo]} (apply hash-map options)]
      (.apply     
        js/chrome.runtime.connect js/chrome.runtime
          (options->jsparams [extensionId connectInfo])))))

(defn connections []
  (let [c (chan)]
    (.addListener js/chrome.runtime.onConnect
      (fn [port]
        (go
          (>! c (channel-from-port port)))))
    c))


(defn- message-event [message sender response-fn]
  {:message (js->clj message) :sender (js->clj sender) :response-fn response-fn}) 
 
(defn messages []
  (let [ch (chan)]    
    (.addListener js/chrome.runtime.onMessage 
      (fn [message sender reply-fn]
        (go
          (>! ch (message-event message sender reply-fn)))))
    ch))

(defn send-message [message & options]
  (let [{:keys [extensionId options responseCallback]} (apply hash-map options)]
    (.apply 
      js/chrome.runtime.sendMessage js/chrome.runtime 
        (options->jsparams
          [extensionId message options responseCallback]))))







      
      
    
    
