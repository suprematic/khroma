(ns khroma.context-menus
  (:require [cljs.core.async :as async]
            [khroma.log :as console]
            [khroma.util :refer [options->jsparams]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn create [& {:as properties}]
  (let [ch (async/chan 1)]
    (.create js/chrome.contextMenus (clj->js properties) (fn []
                                                           (go (>! ch true)
                                                               (async/close! ch))))
    ch))

(defn add-listener []
  (let [ch (async/chan 1)]
    (.addListener js/chrome.contextMenus.onClicked (fn [args]
                                                     (go (>! ch args))))
    ch))

