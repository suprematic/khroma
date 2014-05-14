(ns khroma.debugger
  (:require
    [cljs.core.async :as async]
    [khroma.util :as util])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn get-all []
  (util/with-callback
    #(.getAll js/chrome.commands %)))