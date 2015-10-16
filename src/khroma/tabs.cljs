(ns khroma.tabs
  (:require [khroma.log :as console]
            [khroma.util :as kutil]
            [clojure.walk :as walk]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


;;;;------------------------------
;;;; Functions
;;;;------------------------------


(declare update)

(defn activate
  "Updates a tab to be both highlighted and active."
  [tab-id]
  (update tab-id {:highlighted true :active true}))


(defn create
  "Creates a new tab with the specified properties"
  ([]
   (create {} nil))
  ([props]
   (create props nil))
  ([props callback]
   (.create js/chrome.tabs (clj->js props) callback)))


(defn get-tab
  "Returns a channel where we'll put a tab's information from its id"
  [tab-id]
  (let [ch (async/chan)]
    (.get js/chrome.tabs tab-id
          (fn [tab]
            (async/put! ch (walk/keywordize-keys (js->clj tab))))) ch))


(defn get-active-tab
  "Returns a channel where we'll put the information for the current tab. Uses
  `query`.

  See https://developer.chrome.com/extensions/tabs#method-query"
  []
  (let [ch (async/chan)]
    (.query js/chrome.tabs #js {:active true :currentWindow true}
            (fn [result]
              (when-let [tab (first result)]
                (async/put! ch (walk/keywordize-keys (js->clj tab)))))) ch))


(defn query
  "Returns a channel where we'll put the information for the tabs matching the query.
  Receives an optional map with the query information.

  See https://developer.chrome.com/extensions/tabs#method-query"
  ([]
   (query {}))
  ([query-info]
   (let [ch (async/chan)]
     (.query js/chrome.tabs (clj->js query-info)
             (fn [result]
               (async/put! ch (walk/keywordize-keys (js->clj result)))))
     ch)))


(defn remove
  "Removes a tab by its ID (or a list of tabs, if it receives a list of IDs instead of an integer)"
  ([to-remove]
   (remove to-remove nil))
  ([to-remove callback]
   (.remove js/chrome.tabs to-remove callback)))


(defn update
  "Updates the properties for a tab.

  See https://developer.chrome.com/extensions/tabs#method-update"
  ([tab-id properties]
   (update tab-id properties nil))
  ([tab-id properties callback]
   (.update js/chrome.tabs tab-id (clj->js properties) callback)))



;;;;------------------------------
;;;; Event handlers
;;;;------------------------------


(defn on-created
  "Receives events when a tab is created."
  []
  (kutil/add-listener js/chrome.tabs.onCreated :tab))

(defn on-updated
  "Receives events when a tab is updated. This will include changing the URL,
  title or any content, not only creation. It will not fire when a tab is
  removed."
  []
  (kutil/add-listener js/chrome.tabs.onUpdated :tabId :changeInfo :tab))

(defn on-removed
  "Receives events when a tab is removed."
  []
  (kutil/add-listener js/chrome.tabs.onRemoved :tabId :removeInfo))

(defn on-replaced
  "Receives events when a tab is replaced with another tab. The notification
  will include the id for the tabs `:added` and `:removed`.  Chrome does not
  send any information other than both ids.

  See https://developer.chrome.com/extensions/tabs#event-onReplaced"
  []
  (kutil/add-listener js/chrome.tabs.onReplaced :added :removed))


(defn tab-created-events "DEPRECATED" []
  (kutil/deprecated on-created "tabs/on-created"))

(defn tab-updated-events "DEPRECATED" []
  (kutil/deprecated on-updated "tabs/on-updated"))

(defn tab-removed-events "DEPRECATED" []
  (kutil/deprecated on-removed "tabs/on-removed"))

(defn tab-replaced-events "DEPRECATED" []
  (kutil/deprecated on-replaced "tabs/on-replaced"))





