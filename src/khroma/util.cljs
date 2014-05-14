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

(defn- make-handler [ch]
  (fn [& data]
    (go
      (if data (>! ch (js->clj (first data))))
      (async/close ch))))

(defn with-callback [f]
  (let [ch (async/chan)]
    (f (make-handler ch)) ch))