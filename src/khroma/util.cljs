(ns khroma.util
  (:require [cljs.core.async :as async])
  (:require-macros 
    [cljs.core.async.macros :refer [go alt!]]))

(def nil-marker
  (gensym))

(defn escape-nil [value]
  (if (nil? value) nil-marker value))

(defn unescape-nil [value]
  (if (= nil-marker value) nil value))

(defn with-callback [f]
  (let [ch (async/chan)]
    (f (make-handler ch)) ch))

(defn- make-handler [ch]
  (fn [& data]
    (go
      (if-not (nil? data) (>! ch (js->clj data)))
      (async/close ch))))