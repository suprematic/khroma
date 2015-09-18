(ns khroma.windows
  (:require
    [cljs.core.async :refer [chan >!]])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))


(defn get-current
  "Returns a channel where we'll put the current window information."
  ([]
   (get-current {:populate true}))
  ([get-info]
   (let [ch (chan)]
     (.getCurrent js/chrome.windows (clj->js get-info)
                  (fn [window]
                    (go
                      (>! ch window))))
     ch)))

