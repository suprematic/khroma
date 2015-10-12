(ns khroma.commands
  (:require
    [cljs.core.async :as async]
    [khroma.util :as util])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn get-all
  " Returns all registered commands for this extension.

  See https://developer.chrome.com/extensions/commands#method-getAll"
  []
  (util/with-callback
    #(.getAll js/chrome.commands %)))