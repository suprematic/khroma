(ns khroma-tests.commands
  (:require
    [cljs.core.async :refer [>! <! close!]]
    [devcards.core :as core]
    [khroma.commands :as commands]
    [khroma-tests.utils :as utils]
    [khroma.log :as console])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-doc mkdn-pprint-source]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))


(defcard
  "#khroma.commands examples"
  "We can't tests some of these functions automatically, since they are
  about reacting to user input, but we can demonstrate their use when
  activated.")


(defcard
  get-all
  (dc/tests
    "Get the list of defined commands and review that we have the one we defined,
    as well as the one for the button."
    (async done
      (go
        (let [all-commands (sort-by :description (<! (commands/get-all)))]
          (is (= 2 (count all-commands)))
          (is (= "_execute_browser_action" (:name (first all-commands))))
          (is (= "say-hi" (:name (second all-commands))))
          (done)))
      )))


(defonce on-command-atom (utils/atom-channel-loop commands/on-command))
(defcard
  "##on-command

  We can monitor `commands/on-command` events here. We only have one.

  [Go to Chrome Extension configuration](chrome://extensions/configureCommands),
  set up a shortcut for `khroma-tests` _Send a sample command_, and then
  come back here and press that key combination to see an event logged."
  on-command-atom)

