(ns khroma-tests.windows
  (:require
    [devcards.core :as devcards]
    [cljs.core.async :refer [>! <! chan]]
    [cljs.test :as t :refer [report] :include-macros true]
    [khroma.alarms :as alarms]
    [khroma.windows :as windows]
    [khroma-tests.utils :as utils]
    )
  (:require-macros
    [devcards.core :as dc :refer [defcard]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))

(enable-console-print!)


(defcard "#khroma.windows tests"
         "Test for the currently surfaced windows functions.")


(defcard
  get-all
  (dc/tests
    "Call `windows/get-all` without requesting tab information and evaluate the result"
    (async done
      (go
        (let [result (<! (windows/get-all))]
          (is (some? result) "We got a window list")
          (is (coll? result) "The windows are returned as a list")
          (doseq [window result]
            (is (integer? (:id window)) "Each window should include an id")
            (is (= 0 (count (:tabs window))) "... but we don't get tabs, since we did not ask for them"))
          )
        (done)))
    )
  )

(defcard
  get-all-populated
  (dc/tests
    "Call `windows/get-all` with `{:populate true}` and evaluate the result"
    (async done
      (go
        (doseq [window (<! (windows/get-all {:populate true}))]
          (is (integer? (:id window)) "Each window should include an id")
          (is (< 0 (count (:tabs window))) "Each window should have tab information on them")
          (doseq [tab (:tabs window)]
            (is (some? (:id tab)) (str "... " (:title tab)))
            ))
        (done)))
    ))


(defcard
  get-current
  (dc/tests
    "Call `windows/get-current` and evaluate the result"
    (async done
      (go
        (let [result (<! (windows/get-current))]
          (is (some? result) "We got a window!")
          (is (integer? (:id result)) "... which should include an id")
          (is (< 0 (count (:tabs result))) "... and have at least one tab - the one for the extension")
          (is (:focused result) ".. but of which we cannot make any other assumptions beyond it being in focus")
          )
        (done)))
    ))

(defn window-query
  "Queries periodically for the current focused window state using an alarm.
  Returns an atom that the state will be reset to."
  []
  (alarms/create "check-windows" {:periodInMinutes 1})
  (let [a (atom {})]
    (go-loop
      [channel (alarms/on-alarm)]
      (when (= "check-windows" (get-in (<! channel) [:alarm :name]))
        (reset! a {:current-window (<! (windows/get-current {:populate false}))
                   :last-focused   (<! (windows/get-last-focused {:populate false}))}))
      (recur channel))
    a))

(defonce on-created-atom (utils/atom-channel-loop-append windows/on-created))
(defonce on-removed-atom (utils/atom-channel-loop-append windows/on-removed))
(defonce on-focus-atom (utils/atom-channel-loop windows/on-focus-changed))
(defonce alarm-focus-atom (window-query))


(defcard
  "##on-created

  We can monitor `windows/on-created` events. You will get an item listed
  whenever someone creates a new window."
  on-created-atom)


(defcard
  "##on-removed

  We can also monitor `windows/on-removed` events. You will get an item listed
  whenever a window is removed."
  on-removed-atom)


(defcard
  "##on-focus-changed

  We can also monitor `windows/on-focus-changed` events, where we get the id
  of the currently-focused window. The window listed can be
  `chrome.windows.WINDOW_ID_NONE` (exposed as `windows/none`) if all windows
  have lost focus.

  Notice that Chrome will sometimes not send us `on-focus-changed` events after
  we click away from a window and back to it, with the last item being a -1.

  To explore what happens in those cases, whenever we get one of these events
  I'll list both the id of the window that got the focus, and the result of
  querying both `windows/get-current` and `windows/get-last-focused`, so we
  can evaluate the state."
  on-focus-atom)

(defcard
  "##Focus alarm

  Since we can't rely on Chrome to always send `on-focus-changed` messages,
  we can query for them periodically with an alarm. You may see that even
  though Chrome missed sending us an event, the window object will still
  have its `focused` property set to `true`."
  alarm-focus-atom
  )