(ns khroma-tests.extension
  (:require
    [devcards.core :as devcards]
    [cljs.core.async :refer [>! <!]]
    [cljs.test :as t :refer [report] :include-macros true]
    [khroma.extension :as ext]
    [khroma.log :as console])

  (:require-macros
    [devcards.core :as dc :refer [defcard]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))

(enable-console-print!)

(defcard
  "#khroma.extension examples"
  "Evaluating the result of the khroma.extension functions.

  We can't tests these functions per se, since we can' rely on them returning
  a specific value, but we can demonstrate their use.")

(defcard
  "##allowed-incognito-access?"
  "Is our extension allowed incognito access? We assume not
   for the test extension.

   Notice that we get a channel back from the function, since Chrome's response
   arrives asynchronously."
  )


(defcard
  allowed-incognito-access?
  (dc/tests
    (async done
      (go
        (is (false? (<! (ext/allowed-incognito-access?)))
            "You didn't enable inconito access for a test extension, did you?")
        (done)))))


(defcard
  "##allowed-file-scheme-access?"
  "Is our extension file scheme access?

   Notice that we get a channel back from the function, since Chrome's response
   arrives asynchronously."
  )

(defcard
  allowed-file-scheme-access?
  (dc/tests
    (async done
      (go
        (is (true? (<! (ext/allowed-file-scheme-access?)))
            "Looks like this is on by default")
        (done)))))

(defcard
  "##get-background-page

  This is the object for our background page, as a window"
  (ext/get-background-page))

(defcard
  "##get-url

  This is where we'd put an extension index page... if we had one."
  (ext/get-url "/index.html"))

(defcard
  "##get-views"

  "Returns, by default, all views and windows for the extension.

  ```
  (ext/get-views nil)
  ```

  You can also call it to obtain just those for a specific type.

  ```
  (ext/get-views {:type \"tab\"})
  (ext/get-views {:type \"notification\"})
  (ext/get-views {:type \"popup\"})
  ```
  "
  )

(defcard
  get-views
  (dc/tests
    (is (nil? (ext/get-views {:type "notification"})))
    (is (= 2 (count (ext/get-views nil))))
    (is (= 1 (count (ext/get-views {:type "tab"}))))
    ))


(defcard
  "##set-update-url-data"

  "Can't come up with a meaningful example for this function, but if you have
  one, by all means send it over!")

