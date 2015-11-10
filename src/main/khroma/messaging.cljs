(ns khroma.messaging
  (:require [cljs.core.async :as async]
            [cljs.core.async.impl.protocols :as p])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defprotocol IChromePort
  (port-name [this]))

(def chan
  (partial async/chan 100)) 

(defn channel-from-port [port]
  (let [in (chan) out (chan)]
    (go-loop [message (async/<! out)]
      (when message
        (.postMessage port (clj->js message)) 
        (recur (async/<! out))))    
    
    (.addListener (.-onMessage port) 
      (fn [message sender response-fn]
        (go (async/>! in (js->clj message)))))
    
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


