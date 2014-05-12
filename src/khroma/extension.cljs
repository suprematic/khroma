(ns khroma.extension
	(:require
		[cljs.core.async :as async])

	(:require-macros
		[cljs.core.async.macros :refer [go]])
)

;depricated
(defn send-request [extension-id request]
	(let [ch (async/chan)]
		(.sendRequest js/chrome.extension extension-id request
			(fn [response]
				(go
					(>! ch (js->clj response))
					(async/close ch)))) ch))

(defn get-url [path]
	(.getURL js/chrome.extension path))

(defn get-views [fetchProperties]
	(.getViews js/chrome.extension fetchProperties))

(defn get-background-page 
	(.getBackgroundPage js/chrome.extension))

(defn get-extension-tabs [windowId]
	(.getExtensionTabs js/chrome.extension))

(defn is-allowed-incognito-access
	(let [ch (async/chan)]
		(.isAllowedIncognitoAccess js/chrome.extension
			(fn [is-allowed-access]
				(go
					(>! ch (js->clj tab))
					(async/close ch)))) ch))

(defn is-allowed-file-scheme-access
	(let [ch (async/chan)]
		(.isAllowedFileSchemeAccess js/chrome.extension
			(fn [is-allowed-access]
				(go
					(>! ch (js->clj tab))
					(async/close ch)))) ch))

(def set-update-url-data [data]
	(.setUpdateUrlData js/chrome.extension data))