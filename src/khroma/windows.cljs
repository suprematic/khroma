(ns khroma.windows
  (:require
    [cljs.core.async :refer [chan >!]]
    [clojure.walk :as walk]
    [khroma.util :as kutil])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))


;; While it would be ideal to define this as
;;
;;   (def none (.-WINDOW-ID-NONE js/chrome.windows))
;;
;; this causes a situation on content scripts, since khroma.windows
;; might be refernced on the js for the application as a whole, but
;; chrome.windows is _not_ accessible from content scripts.
(def none -1)

(defn get-all
  "Returns an array containing information for all open windows.

  See https://developer.chrome.com/extensions/windows#method-getAll"
  ([]
   (get-all nil))
  ([get-info]
   (let [ch (chan)]
     (.getAll js/chrome.windows (clj->js get-info)
              (fn [windows]
                (go
                  (>! ch (walk/keywordize-keys (js->clj windows))))))
     ch)))

(defn get-current
  "Returns a channel where we'll put the current window information.
  That's the window that the function is being called from, not necessarily
  the focused one.

  See https://developer.chrome.com/extensions/windows#method-getCurrent"
  ([]
   (get-current {:populate true}))
  ([get-info]
   (let [ch (chan)]
     (.getCurrent js/chrome.windows (clj->js get-info)
                  (fn [window]
                    (go
                      (>! ch (walk/keywordize-keys (js->clj window))))))
     ch)))

(defn get-last-focused
  "Returns a channel where we'll put the information for the last focused window

  See https://developer.chrome.com/extensions/windows#method-getLastFocused"
  ([]
   (get-last-focused {:populate true}))
  ([get-info]
   (let [ch (chan)]
     (.getLastFocused js/chrome.windows (clj->js get-info)
                  (fn [window]
                    (go
                      (>! ch (walk/keywordize-keys (js->clj window))))))
     ch)))


(defn on-created
  "Receives events when a window is created.

  See https://developer.chrome.com/extensions/windows#event-onCreated"
  []
  (kutil/add-listener js/chrome.windows.onCreated :windowId))

(defn on-focus-changed
  "Receives events when a window gains or loses focus.

  See https://developer.chrome.com/extensions/windows#event-onFocusChanged"
  []
  (kutil/add-listener js/chrome.windows.onFocusChanged :windowId))

(defn on-removed
  "Receives events when a window is removed

  See https://developer.chrome.com/extensions/windows#event-onRemoved"
  []
  (kutil/add-listener js/chrome.windows.onRemoved :windowId))