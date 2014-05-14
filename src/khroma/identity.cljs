(ns khroma.extension
	(:require
		[cljs.core.async :as async]
		[khroma.util :as util])
	(:require-macros
		[cljs.core.async.macros :refer [go]]))

(defn get-accounts
	(util/with-callback
		#(.getAccounts js/chrome.identity %)))

(defn get-auth-token [details]
	(util/with-callback
		#(.getAuthToken js/chrome.identity details %)))

(defn remove-cached-auth-token [details]
	(util/with-callback
		#(.removeCachedAuthToken js/chrome.identity details %)))

(defn launch-web-auth-flow [details]
	(util/with-callback
		#(.launchWebAuthFlow js/chrome.identity details %)))

(defn get-redirect-url [path]
	(util/with-callback
		#(.getRedirectURL js/chrome.identity path %)))