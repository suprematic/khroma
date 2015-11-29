(ns khroma-tests.runtime
  (:require
    [devcards.core :as core]
    [cljs.core.async :refer [>! <!]]
    [khroma.runtime :as runtime]
    [khroma.log :as console]
    [khroma-tests.rigging.background :as background]
    [khroma-tests.rigging.content :as content]
    [khroma-tests.utils :as utils])
  (:require-macros
    [devcards.core :refer [defcard defcard-doc mkdn-pprint-source]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))

(enable-console-print!)

(defcard
  "#khroma.runtime tests"
  "Test for the currently surfaced `runtime` functions.")


(defcard
  "##available?

  Is the runtime available? I sure hope so, if you're reading this."
  runtime/available?)

(defcard-doc
  "##connect

  For a very basic `connect` example, see the `khroma-tests.rigging.content`
  namespace."
  (mkdn-pprint-source content/init))


(defcard
  "##manifest

  Stores the contents of the extension's manifest."
  runtime/manifest)


(defcard-doc
  "##on-connect

  For an `on-connect` example, see the `khroma-tests.rigging.background`
  namespace."
  (mkdn-pprint-source background/on-connect-listener)
  "This simple example listens to connection messages from tabs and content
  scripts, and sends a message back to them.")


(defcard-doc
  "##on-message

  For an `on-message` example, see the `khroma-tests.rigging.background`
  namespace."
  (mkdn-pprint-source background/message-listener)
  "This simple example listens to tab messages, will reply once we have
  surfaced `tabs/send-message` on 0.2.0.")


(go
  (runtime/send-message {:message "Hello background!"}))


(defcard-doc
  "##send-message

  When this page is loaded, we'll send a message to the background page
  containing our tab-id. The background page will then reply with an
  echo of the message, using `tabs/send-message`.")



(defonce on-suspend-atom (utils/atom-channel-loop runtime/on-suspend))
(defonce on-suspend-canceled-atom (utils/atom-channel-loop runtime/on-suspend-canceled))

(defcard
  "##on-suspend-cancel

  Monitors `runtime/on-suspend` events, which are sent when the background page
  is suspended. We are unlikely to get any while the page is running.

  It will be `nil` if we haven't received any."
  on-suspend-atom)

(defcard
  "##on-suspend-canceled

  Monitors `runtime/on-suspend-canceled` events, which are sent when a suspend
  is canceled midway through because the background page received an event.

  It will be `nil` if we haven't received any."
  on-suspend-canceled-atom)