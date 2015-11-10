(ns khroma-tests.web-navigation
  (:require
    [devcards.core :as devcards]
    [cljs.core.async :refer [>! <! close!]]
    [khroma.web-navigation :as navigation]
    [khroma-tests.utils :as utils])
  (:require-macros
    [devcards.core :as dc :refer [defcard]]
    [cljs.core.async.macros :refer [go go-loop]]))


(defcard "#khroma.web-navigation examples"
         "We can't test the `web-navigation` event handlers, but we can monitor them.
         We'll take advantage of devcards' atom history to avoid ending up with some
         huge lists.")

(defonce on-before-navigate-atom (utils/atom-channel-loop navigation/on-before-navigate))

(defcard
  "##on-before-navigate"
  on-before-navigate-atom)


(defonce on-completed-atom (utils/atom-channel-loop navigation/on-completed))
(defcard
  "##on-completed"
  on-completed-atom)


