(ns khroma-tests.browser-action
  (:require
    [devcards.core :as core]
    [khroma-tests.rigging.button-handler :as button-handler])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-doc mkdn-pprint-source]]))


(defcard
  "#khroma.browser-action examples"
  "We can't tests these functions per se, since they are all about reacting
  to user input, but we can demonstrate their use.")

(defcard-doc
  "##on-clicked

  As you can see below from the included `khroma-tests.rigging.button-handler`
  namespace, which is used to plug in to the `on-clicked` function, we receive
  a channel from `on-clicked` which we can then poll for events and react to them."

  (dc/mkdn-pprint-source button-handler/init)

  "The sample above also logs some information before opening the tab that
  you use to see these examples.")
