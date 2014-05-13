(ns khroma.extension
	(:require
		[cljs.core.async :as async])

	(:require-macros
		[cljs.core.async.macros :refer [go]])
)

(defn send-request "Depricated function" [extension-id request]
	(with-callback
		#(.send-request js/chrome.extension extension-id request %)))

(defn get-url [path]
	(.getURL js/chrome.extension path))

(defn get-views [fetchProperties]
	(.getViews js/chrome.extension fetchProperties))

(defn get-background-page 
	(.getBackgroundPage js/chrome.extension))

(defn get-extension-tabs [windowId]
	(.getExtensionTabs js/chrome.extension))

(defn allowed-incognito-access?
	(with-callback
		#(.isAllowedIncognitoAccess js/chrome.extension %)))

(defn allowed-file-scheme-access?
	(with-callback
		#(.isAllowedFileSchemeAccess js/chrome.extension %)))

(def set-update-url-data [data]
	(.setUpdateUrlData js/chrome.extension data))

(defn- with-callback [f]
	(let [ch (async/chan)]
		(f (make-handler ch)) ch))

(defn- make-handler [ch]
	(fn [data]
		(go
			(>! ch data)
			(async/close ch))))