(ns khroma-tests.storage
  (:require
    [devcards.core :as devcards]
    [cljs.core.async :refer [>! <! close!]]
    [cljs.test :as t :refer [report] :include-macros true]
    [khroma.storage :as storage]
    [khroma-tests.utils :as utils])
  (:require-macros
    [devcards.core :as dc :refer [defcard]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))

(enable-console-print!)


(defcard
  "#khroma.storage tests and demo"
  "As you come in, we'll first clear both the local and sync storage for the
  extension.")

(storage/clear)
(storage/clear storage/sync)


(defonce
  on-storage-changed-atom
  (utils/atom-channel-loop-append storage/on-changed))


(defcard
  get-set
  (dc/tests
    "##get and set tests
    Let's get and set some values and evaluate the behavior.
    We will not use callbacks for the set tests, but you should
    see the `storage/get` and `storage/set` documentation.

    Since the keys are keywordized on return, you should only
    use as key strings that can be keywordized."
    (async done
      (go
        (is (empty? (<! (storage/get)))
            "We should have nothing stored, since we just cleared it")
        (is (nil? (storage/set {:first 1}))
            "Save a value")
        (is (= {:first 1} (<! (storage/get)))
            "After setting a value, we can retrieve the entire storage and see it")
        (is (nil? (storage/set {:other 2}))
            "Save a new one")
        (is (= {:first   1
                :other 2} (<! (storage/get)))
            "We can retrieve the entire storage...")
        (is (= {:other 2} (<! (storage/get [:other])))
            "...or just a single key")
        (is (nil? (storage/set {:final 3}))
            "Save a final one, just to do one more demo...")
        (is (nil? (storage/set {:first 4 :other 5}))
            "... which is that we can save only a few items...")
        (is (= {:other 5
                :final 3} (<! (storage/get [:other :final])))
            "... as well as selectively retrieve them.")
        (is (= {"other" 5
                "final" 3} (<! (storage/get [:other :final] storage/local :key-fn identity)))
            "... we can pass `identity` as the key-fn if we don't want the keys altered")
        (is (= {"other-key" 5
                "final-key" 3} (<! (storage/get [:other :final] storage/local :key-fn #(str % "-key"))))
            "... or pass any arbitrary function")
        (is (nil? (storage/set {:complex {:a 1 :b 2 :c {:d "delta" :e "epsilon"}}}))
            "Save a complex value")
        (is (= {:complex {:a 1 :b 2 :c {:d "delta" :e "epsilon"}}} (<! (storage/get [:complex])))
            "Retrieving a complex value works as expected")
        (is (= {"complex-key" {"a-key" 1 "b-key" 2 "c-key" {"d-key" "delta" "e-key" "epsilon"}}}
               (<! (storage/get [:complex] storage/local :key-fn #(str % "-key"))))
            "... even if we pass an arbitrary key-fn")
        (done)))
    ))


(defcard
  bytes-in-use
  (dc/tests
    "##bytes-in-use

    We can also retrieve the total bytes in use for a particular storage.
    We'll first clear any storage previously used by tests."
    (async done
      (go
        (storage/clear)
        (is (= 0 (<! (storage/bytes-in-use)))
            "We're not using any local storage")
        (is (= 0 (<! (storage/bytes-in-use nil storage/sync)))
            "... nor any sync storage")
        (is (nil?
              (storage/set {:library     "khroma"
                            :ns          :khroma.storage
                            :author      "Ricardo J. Mendez"
                            :description "kroma.storage provides Clojurrific access to Chrome's extension storage"}))
            "Let's save a few values.")
        (is (= 142 (<! (storage/bytes-in-use)))
            "We are using 142 bytes total")
        (is (= 84 (<! (storage/bytes-in-use [:description])))
            "84 bytes are for the description alone")
        (is (= 99 (<! (storage/bytes-in-use [:description :library])))
            "But we can also query for some keys specifically")

        (done)))
    ))


(defcard
  "##on-changed

  We could also monitor storage changes, in case we have multiple tabs/windows
  that can modify it and we want to keep in sync. This is done by polling the
  channel returned by calling `storage/on-changed`.

  We can't properly test it, however, since tests are run asynchronously and we
  will likely get notifications from one of the other tests. You will receive
  back a message containing keywords `:areaName`, specifying where the changes
  happened, and `:changes`, which lists the changed values.

  We can, however, observe the changes as they come in using an observed atom,
  and take advantage of devcards' amazing history control.

  ```
  (let [a (atom [])]\n    (go-loop\n      [channel (storage/on-changed)]\n      (swap! observed-atom conj (<! channel))\n      (recur channel))\n    a)
  ```

  Notice that we can get changes with only `:newValue` when the value did not
  previously exist, or only `:oldValue` when it's being removed (for example,
  when we clear the data)."
  on-storage-changed-atom)


(defcard
  remove
  (dc/tests
    "##remove

    Trivial test, but hey, who says that function can't break?"
    (async done
      (go
        (storage/clear)
        (is (nil?
              (storage/set {:library     "khroma"
                            :ns          :khroma.storage
                            :author      "Ricardo J. Mendez"
                            :depends-on  ["Oracle" "SQLServer"]
                            :description "kroma.storage provides Clojurrific access to Chrome's extension storage"}))
            "Let's save a few values.")
        (is (= ["Oracle" "SQLServer" (<! (storage/get [:depends-on]))])
            "We added some dependencies. Welp, they're not correct!")
        (is (nil? (storage/remove [:depends-on])))
        (is (empty? (<! (storage/get [:depends-on])))
            "Poof! They're gone.")
        (is (= {:library "khroma"} (<! (storage/get [:depends-on :library])))
            "If we include a removed key on a request, we only get back the existing items.")
        (done)))
    ))


