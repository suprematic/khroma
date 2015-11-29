(ns khroma-tests.rigging.button-handler
  (:require [khroma.log :as console]
            [khroma.browser-action :as browser]
            [khroma.tabs :as tabs]
            [khroma.extension :as ext]
            [khroma.windows :as windows]
            [cljs.core.async :refer [>! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn init []
  (let [conns (browser/on-clicked)]
    (go (while true
          (let [clicked (<! conns)
                ;; Get the URL for the local installation
                ext-url (str (ext/get-url "/") "new-tab.html")
                window  (<! (windows/get-current))]
            (console/log "Clicked while on" clicked)
            (console/log "Button clicked from window" window)
            (tabs/create {:url ext-url})
            )))))
