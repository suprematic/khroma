(ns khroma.browser
  (:require
    [cljs.core.async :refer [chan >!]]
    [clojure.walk :as walk])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))


(defn on-click
  "Returns a channel where we'll put a tab when we get an onClicked event"
  []
  (let [ch (chan)]
    (.addListener js/chrome.browserAction.onClicked
                  (fn [tab]
                    (go
                      (>! ch (walk/keywordize-keys (js->clj tab))))))
    ch))

