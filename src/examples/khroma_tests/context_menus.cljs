(ns khroma-tests.context-menus
  (:require
    [cljs.core.async :refer [>! <! close!]]
    [devcards.core :as core]
    [khroma.context-menus :as menus]
    [khroma-tests.utils :as utils]
    [khroma.log :as console])
  (:require-macros
    [devcards.core :as dc :refer [defcard defcard-doc mkdn-pprint-source]]
    [cljs.core.async.macros :refer [go go-loop]]
    [cljs.test :refer [async is testing]]))


(defn create-page-menu []
  (menus/create {:id    "khroma-menu"
                 :title "Khroma - Page menu"})
  (menus/create {:id       "khroma-first"
                 :title    "Khroma - First item"
                 :parentId "khroma-menu"})
  (menus/create {:id       "khroma-second"
                 :title    "Khroma - Second item"
                 :parentId "khroma-menu"}))

(defn create-browser-menu []
  (menus/create {:id       "khroma-browser"
                 :title    "Khroma - Browser"
                 :contexts ["browser_action"]}))

(menus/remove-all)
(create-page-menu)
(create-browser-menu)


(defcard
  "#khroma.context-menus examples"
  "Let's see how to use the context-menus namespace.")


(defcard-doc
  "##Creating page menus"
  (mkdn-pprint-source create-page-menu)
  "This example creates a parent menu, then adds two child menu items to it
  based on its id.")


(defcard-doc
  "##Creating browser action menus"
  (mkdn-pprint-source create-browser-menu)
  "A browser action menu will appear on our extension's button on the browser bar.")


(defonce on-clicked-atom (utils/atom-channel-loop menus/on-clicked))
(defcard
  "##on-clicked

  We monitor `context-menus/on-clicked` events here.

  We have two sample context menues, one which will appear if you
  right-click a page, and one that will show up if you right-click our
  extension's button.  Pop them up and click them so you can see the event
  history being updated below.
  "
  on-clicked-atom)

