(ns khroma-tests.alarms
  (:require
    [devcards.core :as core]
    [khroma.alarms :as alarms]
    [khroma.log :as console]
    [cljs.core.async :refer [<!]]
    [khroma-tests.utils :as utils])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]
    [devcards.core :as dc :refer [defcard defcard-doc mkdn-pprint-source]]))


(defn create-one-off [name minutes]
  (alarms/create name {:delayInMinutes minutes}))

(defn create-recurrent [name delay period]
  (alarms/create name {:delayInMinutes  delay
                       :periodInMinutes period}))

(defn create-sample-alarms []
  (create-one-off "one-off" 2.5)
  (create-recurrent "recurrent" 1.5 1))

(defcard-doc
  "##One-off alarms
  
  We can create a one-off alarm by just adding a delay in minutes."

  (mkdn-pprint-source create-one-off))


(defcard-doc
  "##Recurrent alarms

  We can also create recurrent alarms by sending a period in minutes,
  which can be combined with a delay before we start."

  (mkdn-pprint-source create-recurrent))


(defcard-doc
  "##Sample alarms

  Up next, we'll create two sample alarms and track them."

  (mkdn-pprint-source create-sample-alarms)
  "We'll only get one message for one-off, but should get a steady
  stream for the recurrent one. Notice they will all arrive on a single
  on-alarm channel")


(create-sample-alarms)
(defonce on-alarm-atom (utils/atom-channel-loop alarms/on-alarm))


(defcard
  "##on-alarm

  Let's monitor the channel returned by `alarms/on-alarm`. We'll show
  the events as they come in, instead of appending to a list, since it
  can get large.

  The timing may not exactly match that we requested, according to Chrome's docs.
  "
  on-alarm-atom)


(defcard
  get-tests
  (dc/tests
    "##get and get-all tests

    Let's test the result of getting our alarms, which we bind using `get`.

    ```Clojure
    (let [one-off   (<! (alarms/get \"one-off\"))
          recurrent (<! (alarms/get \"recurrent\"))]
          #_ (do-tests-here))
    ```
    "
    (async done
      (go
        (let [one-off   (<! (alarms/get "one-off"))
              recurrent (<! (alarms/get "recurrent"))]
          (is (< 0 (:scheduledTime one-off)) "One-off alarm has a scheduled time set")
          (is (< 0 (:scheduledTime recurrent)) "Recurrent alarm has a scheduled time set")
          (is (= 1 (:periodInMinutes recurrent)) "... but also the period")
          (is (<= 2 (count (<! (alarms/get-all)))) "And we have at least two alarms (other tabs may have created their own)"))
        (done)))))