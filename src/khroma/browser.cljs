(ns khroma.browser
  (:require
    [khroma.util :refer [add-listener]])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))


(defn on-clicked
  "Returns a channel where we'll put a tab when we get an onClicked event

  https://developer.chrome.com/extensions/browserAction#event-onClicked"
  []
  (add-listener js/chrome.browserAction.onClicked :tab))

