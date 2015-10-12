(ns khroma.extension
  (:require
    [cljs.core.async :as async]
    [khroma.util :as util])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn send-request "Deprecated function" [extension-id request]
  (util/with-callback
    #(.sendRequest js/chrome.extension extension-id request %)))

(defn get-url
  "Returns a full url for a path inside the extension"
  [path]
  (.getURL js/chrome.extension path))

(defn get-views
  "Returns, by default, all views and windows running on the extension.

  See https://developer.chrome.com/extensions/extension#method-getViews"
  [fetchProperties]
  (.getViews js/chrome.extension fetchProperties))

(defn get-background-page
  "Returns the window for the background page"
  []
  (.getBackgroundPage js/chrome.extension))

(defn get-extension-tabs "Deprecated since Chrome 33, see get-views" [windowId]
  (.getExtensionTabs js/chrome.extension))

(defn allowed-incognito-access?
  "Is our extension allowed incognito access? Returns a channel."
  []
  (util/with-callback
    #(.isAllowedIncognitoAccess js/chrome.extension %)))

(defn allowed-file-scheme-access?
  "Returns a channel where we'll put the reply on if the extension is
  allowed file scheme access"
  []
  (util/with-callback
    #(.isAllowedFileSchemeAccess js/chrome.extension %)))

(defn set-update-url-data [data]
  (.setUpdateUrlData js/chrome.extension data))