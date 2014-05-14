(ns khroma.extension
  (:require
    [cljs.core.async :as async]
    [khroma.util :as util])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn send-request "Depricated function" [extension-id request]
  (util/with-callback
    #(.sendRequest js/chrome.extension extension-id request %)))

(defn get-url [path]
  (.getURL js/chrome.extension path))

(defn get-views [fetchProperties]
  (.getViews js/chrome.extension fetchProperties))

(defn get-background-page []
  (.getBackgroundPage js/chrome.extension))

(defn get-extension-tabs [windowId]
  (.getExtensionTabs js/chrome.extension))

(defn allowed-incognito-access? []
  (util/with-callback
    #(.isAllowedIncognitoAccess js/chrome.extension %)))

(defn allowed-file-scheme-access? []
  (util/with-callback
    #(.isAllowedFileSchemeAccess js/chrome.extension %)))

(defn set-update-url-data [data]
  (.setUpdateUrlData js/chrome.extension data))