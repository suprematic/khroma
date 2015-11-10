(ns khroma.context-menus
  (:require [khroma.util :as kutil]
            [clojure.walk :as walk]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


;;;;------------------------------
;;;; Functions
;;;;------------------------------


(defn create
  "Creates a new context menu with the specified properties.  Notice that if
  a keyword is used for the menu ID, it will be turned into a string before
  being sent to Chrome. Any notifications from Chrome will use the string
  ID.

  Doesn't support a callback, since Chrome is encouraging developers to move
  to event pages, where callbacks aren't supported.

  See https://developer.chrome.com/extensions/contextMenus#method-create"
  [props]
  (.create js/chrome.contextMenus (clj->js props) nil))


(defn remove
  "Removed an existing context menu by its id.

  See https://developer.chrome.com/extensions/contextMenus#method-remove"
  [id]
  (.remove js/chrome.contextMenus (clj->js id) nil))

(defn remove-all
  "Removes all context menus.

  See https://developer.chrome.com/extensions/contextMenus#method-removeAll"
  []
  (.removeAll js/chrome.contextMenus nil))

(defn update
  "Updates an existing context menu with the specified properties.

  See https://developer.chrome.com/extensions/contextMenus#method-update"
  [id props]
  (.update js/chrome.contextMenus (clj->js id) (clj->js props) nil))


;;;;------------------------------
;;;; Event handlers
;;;;------------------------------


(defn on-clicked
  "Returns a channel which will receive events when a context menu item is clicked.

  See: https://developer.chrome.com/extensions/contextMenus#event-onClicked"
  []
  (kutil/add-listener js/chrome.contextMenus.onClicked :info :tab))