(ns khroma-tests.rigging.background
  (:require [khroma.log :as console]
            [khroma.extension :as ext]
            [khroma.tabs :as tabs]
            [khroma.runtime :as runtime]
            [cljs.core.async :refer [>! <!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn message-listener
  "Listens for messages from a tab and echoes them back"
  []
  (go-loop
    [channel (runtime/on-message)]
    (let [received (<! channel)
          id       (get-in received [:sender :tab :id])]
      (console/log "Received message from tab" id received))
      ;; TODO: Reply once we have exposed the tabs/send-message)
    (recur channel)))

(defn on-connect-listener
  "Listens to on-connect messages"
  []
  (go-loop
    [channel (runtime/on-connect)]
    (let [content (<! channel)]
      (console/log "on-connect message: " (<! content))
      (>! content :fml-i-am-the-background-script)
      (recur channel))
    ))

(defn init []
  (console/log "Background page initialized")
  (console/log "Extension url:" (ext/get-url "/"))
  (message-listener)
  (on-connect-listener))
