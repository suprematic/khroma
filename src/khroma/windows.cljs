(ns khroma.windows
  (:require
    [cljs.core.async :refer [chan >!]]
    [clojure.walk :as walk])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))


(defn get-all
  "Returns an array containing information for all open windows.

  See https://developer.chrome.com/extensions/windows#method-getAll"
  ([]
   (get-all nil))
  ([get-info]
   (let [ch (chan)]
     (.getAll js/chrome.windows (clj->js get-info)
              (fn [windows]
                (go
                  (>! ch (walk/keywordize-keys (js->clj windows))))))
     ch)))

(defn get-current
  "Returns a channel where we'll put the current window information.

  See https://developer.chrome.com/extensions/windows#method-getCurrent"
  ([]
   (get-current {:populate true}))
  ([get-info]
   (let [ch (chan)]
     (.getCurrent js/chrome.windows (clj->js get-info)
                  (fn [window]
                    (go
                      (>! ch (walk/keywordize-keys (js->clj window))))))
     ch)))

