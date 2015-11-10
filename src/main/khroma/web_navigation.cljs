(ns ^{:author "Ricardo J. Mendez"
      :doc    "Functions to access Chrome's webNavigation functionality.
      You will need to specify the webNavigation permission on
      your manifest."} khroma.web-navigation
  (:require [khroma.util :as kutil])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


;;;;------------------------------
;;;; Event handlers
;;;;------------------------------

(defn on-before-navigate
  "Returns a channel which receives events when a navigation is about to occur.

  See https://developer.chrome.com/extensions/webNavigation#event-onBeforeNavigate"
  []
  (kutil/add-listener js/chrome.webNavigation.onBeforeNavigate :details))


(defn on-completed
  "Returns a channel which receives events when a document has been fully loaded.

  See https://developer.chrome.com/extensions/webNavigation#event-onCompleted"
  []
  (kutil/add-listener js/chrome.webNavigation.onCompleted :details))