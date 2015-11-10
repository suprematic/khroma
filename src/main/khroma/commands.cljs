(ns khroma.commands
  (:require
    [khroma.util :as util])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn get-all
  "Returns all registered commands for this extension.

  See https://developer.chrome.com/extensions/commands#method-getAll"
  []
  (util/with-callback
    #(.getAll js/chrome.commands %)))


(defn on-command
  "Returns a channel which receives events when a registered command
  is activated.

  See https://developer.chrome.com/extensions/commands#event-onCommand"
  []
  (util/add-listener js/chrome.commands.onCommand :command))