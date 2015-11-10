(ns khroma.identity
	(:require
		[cljs.core.async :as async]
		[khroma.util :as util])
	(:require-macros
		[cljs.core.async.macros :refer [go]]))

(defn get-accounts
	"Developer-only API

	See https://developer.chrome.com/apps/identity#method-getAccounts
	"
	[]
	(util/with-callback
		#(.getAccounts js/chrome.identity %)))

(defn get-auth-token
	"Gets an OAuth2 access token using the client ID and scopes specified
	in the oauth2 section of manifest.json.

	See https://developer.chrome.com/apps/identity#method-getAuthToken"
	[details]
	(util/with-callback
		#(.getAuthToken js/chrome.identity details %)))

(defn remove-cached-auth-token
	"Removes an OAuth2 access token from the application cache.

	See https://developer.chrome.com/apps/identity#method-removeCachedAuthToken"
	[details]
	(util/with-callback
		#(.removeCachedAuthToken js/chrome.identity details %)))

(defn
	"Starts a web auth flow with a URL.

	See https://developer.chrome.com/apps/identity#method-launchWebAuthFlow
	"
	launch-web-auth-flow [details]
	(util/with-callback
		#(.launchWebAuthFlow js/chrome.identity details %)))

(defn
	"Gets a redirect URL for a web auth flow.

	See https://developer.chrome.com/apps/identity#method-getRedirectURL"
	get-redirect-url [path]
	(util/with-callback
		#(.getRedirectURL js/chrome.identity path %)))