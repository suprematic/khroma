(ns khroma-tests.idle
  (:require
    [devcards.core :as core]
    [khroma.idle :as idle]
    [khroma.log :as console]
    [cljs.core.async :refer [<!]]
    [khroma-tests.utils :as utils])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]
    [devcards.core :as dc :refer [defcard defcard-doc mkdn-pprint-source]]))


(defonce on-state-changed-atom (utils/atom-channel-loop idle/on-state-changed))

(defcard
  "#khroma.idle"
  "`khroma.idle` functions can't be easily tested, since they depend on
  user input, but you'll find some examples below.

  You'll need to have the `idle` permission in order to use these functions.")


(defcard
  "##on-state-changed"
  "Returns a channel that can be used to detect idle state changes.

  Suppose you were using [re-frame](https://github.com/Day8/re-frame)
  (because why wouldn't you?), and you had the following function:

  ```
  (defn dispatch-on-channel
    \"Dispatches msg when there's content received on the channel returned by
    function chan-f.\"
    [msg chan-f]
    (go-loop
       [channel (chan-f)]
       (dispatch [msg (<! channel)])
       (recur channel)))
  ```

  You could then send yourself the `:idle-state-change` whenever the
  state changes:

  ```
  (dispatch-on-channel :idle-state-change idle/on-state-changed)
  ```

  When the browser turns idle, or back to active, you'll receive a message
  including a `:newState` value.
  ")


(defcard
  query-state
  (dc/tests
    "##query-state

    Finally a function we can test!

    `query-state` returns a channel where we'll put the idle state.
    This should be `active`, seeing as how you just clicked to get here,
    but can also be `idle` or `locked`."
    (async done
      (go
        (is (= "active" (<! (idle/query-state 30))))
        (is (not= "idle" (<! (idle/query-state 30))))
        (is (not= "locked" (<! (idle/query-state 30))))
        (done)))))

(defcard
  "##set-detection-interval"
  "Call `set-detection-interval` to configure the threshold for the browser
  to be considered idle, in seconds.

  ```
  (idle/set-detection-interval 60)
  ```

  There will be a minimum value enforced by Chrome. When the state changes,
  you'll get a notification via the channel you can obtain with
  `on-state-changed`.")

(idle/set-detection-interval 15)

(defcard
  "##on-idle-state-changed

  We can also monitor `idle/on-state-changed` events. I've set the threshold
  for fifteen seconds so we can get more events here (Chrome won't let us go
  any lower)

  You'll need to let the computer idle to see a change, or lock it and
  unlock it.
  "
  on-state-changed-atom)