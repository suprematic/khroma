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