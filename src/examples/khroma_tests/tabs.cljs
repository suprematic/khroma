(ns khroma-tests.tabs
  (:require
    [devcards.core :as devcards]
    [cljs.core.async :refer [>! <! close!]]
    [cljs.test :as t :refer [report] :include-macros true]
    [khroma.extension :as ext]
    [khroma.tabs :as tabs]
    [khroma-tests.utils :as utils])
  (:require-macros
    [devcards.core :as dc :refer [defcard]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))

(enable-console-print!)

(defonce on-activated-atom (utils/atom-channel-loop tabs/on-activated))
(defonce on-created-atom (utils/atom-channel-loop tabs/on-created))
(defonce on-updated-atom (utils/atom-channel-loop tabs/on-updated))
(defonce on-removed-atom (utils/atom-channel-loop tabs/on-removed))
(defonce on-replaced-atom (utils/atom-channel-loop tabs/on-replaced))

(defcard
  "#khroma.tab tests"
  "You will see some tab flickering as this page opens and closes tabs.

  There's handler demos for all of the events at the bottom, using observed
  atoms.")

(defcard
  get-active
  (dc/tests
    "Call `tabs/get-active` and evaluate the result"
    (async done
      (go
        (let [result (<! (tabs/get-active))]
          (is (some? result))
          (is (= (str (ext/get-url "/new-tab.html#!/khroma_tests.tabs"))
                 (:url result)))
          (is (integer? (:id result))))
        (done)))
    ))

(defcard
  get
  (dc/tests
    "Get the current tab, then call get-tab on its id and confirm we get the same value.
    Testing it this way since we can't really ensure any other tab ids will be available"
    (async done
      (go
        (let [current (<! (tabs/get-active))]
          (is (= current (<! (tabs/get (:id current))))))
        (done)))
    ))

(defcard
  create-activate-update-remove-cycle
  (dc/tests
    "Creates a tab pointing to startpage, then ensure we get an event when it's
    created.
    We will then activate our current tab again and confirm.
    We confirm that after re-activating our initial tab we got an on-updated
    event.
    Finally, we remove the new tab - we hardly knew ye - and check the on-removed
    event that we should get."
    (async done
      (go
        (let [initial-tab   (<! (tabs/get-active))
              on-created    (tabs/on-created)
              on-updated    (tabs/on-updated)
              on-removed    (tabs/on-removed)
              _             (tabs/create {:url "https://startpage.com/"})
              startpage-msg (<! on-created)]
          (is (= "https://startpage.com/" (get-in startpage-msg [:tab :url]))
              "We created a tab pointing to startpage")
          (is (not= initial-tab (<! (tabs/get-active)))
              "Which means our initial tab got deactivated")
          (is (= (get-in startpage-msg [:tab :id])
                 (:id (<! (tabs/get-active))))
              "IDs between startpage and the active tab match")
          (is (not= startpage-msg (<! (tabs/get-active)))
              "... but the full tab data does not")
          (is (false? (get-in startpage-msg [:tab :active]))
              "... because the tab was inactive when we got the initial creation notification")
          (tabs/activate (:id initial-tab))
          (is (= initial-tab (<! (tabs/get-active))) "We just re-activated our initial tab")
          (is (= (get-in startpage-msg [:tab :id])
                 (get-in (<! on-updated) [:tab :id]))
              "The startpage tab should have been updated when we switch away from it.")
          (is (nil? (tabs/remove (get-in startpage-msg [:tab :id])))
              "Let's remove the StartPage tab")
          (is (= (get-in startpage-msg [:tab :id])
                 (:tabId (<! on-removed)))
              "Confirm we got the right event")
          (close! on-created)
          (close! on-updated)
          (close! on-removed))
        (done)
        )))
  )


(defcard
  "##query example - all active tabs

  If you want all tabs, just pass an empty map to `query`, or call it without
  any parameters at all.

  In this case, we'll pass `{:active true}` to get all tabs that are active in
  their window, which will include ours."
  (utils/atom-channel #(tabs/query {:active true}))
  {}
  {:history false})


(defcard
  "##query example - only https tabs

  In this case, we pass a `{:url \"https://*\"}` parameter.
  See [this page](https://developer.chrome.com/extensions/match_patterns) for
  sample match patterns. This will exclude our own extension tab."
  (utils/atom-channel #(tabs/query {:url "https://*/*"}))
  {}
  {:history false})

(defcard
  "##on-activated

  Let's monitor `tabs/on-activated` events, on which we get the tab and
  window ids for activated tabs."
  on-activated-atom)


(defcard
  "##on-created

  We can also monitor `tabs/on-created` events. You will get an item listed
  here when our tests open a new tab (or when you do it)."
  on-created-atom)

(defcard
  "##on-removed

  We can also monitor `tabs/on-removed` events. You will get an item listed
  here when you close a tab."
  on-removed-atom)


(defcard
  "##on-replaced

  We can also monitor `tabs/on-replaced` events. You will get an item listed
  here when a tab is replaced. I can't really say when Chrome does that, seems
  to be when we have a page loaded immediately from cache. Tabs that are just
  loaded from the web go through `on-updated`."
  on-replaced-atom)


(defcard
  "##on-updated

  We can also monitor `tabs/on-updated` events. You will get an item listed
  here when a tab is updated."
  on-updated-atom)




