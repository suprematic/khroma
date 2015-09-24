(ns khroma.util
  (:require [cljs.core.async :as async]
            [clojure.walk :as walk])
  (:require-macros
    [cljs.core.async.macros :refer [go alt!]]))

(def nil-marker
  (gensym))

(defn escape-nil [value]
  (if (nil? value) nil-marker value))

(defn unescape-nil [value]
  (if (= nil-marker value) nil value))

(defn- make-handler [ch]
  (fn [& data]
    (go
      (if data (async/>! ch (js->clj (first data))))
      (async/close! ch))))

(defn with-callback [f]
  (let [ch (async/chan)]
    (f (make-handler ch)) ch))


(defn options->jsparams [options]
  (clj->js
    (map clj->js (filter (complement nil?) options))))

(defn add-listener
  "Returns a channel that's hooked to an event listener. When we receive an
  event, it'll put a message on the channel with the event-specific parameters
  as a map."
  [instance & key-args]
  (let [ch (async/chan)]
    (.addListener instance
                  (fn [& val-args]
                    (async/put! ch (walk/keywordize-keys (js->clj (zipmap key-args val-args)))))) ch))
