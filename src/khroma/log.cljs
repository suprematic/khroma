(ns khroma.log
  (:refer-clojure :exclude [count]))

(defn console-fn [f]
  (fn [& args]
    (.apply f js/console (clj->js (map clj->js args)))))

(def assert 
  (console-fn js/console.assert))

(def clear 
  (console-fn js/console.clear))

(def count 
  (console-fn js/console.count))

(def debug 
  (console-fn js/console.debug))

(def dir 
  (console-fn js/console.dir))

(def dirxml 
  (console-fn js/console.dirxml))

(def error 
  (console-fn js/console.error))

(def group
  (console-fn js/console.group))

(def group-collapsed 
  (console-fn js/console.groupCollapsed))

(def group-end 
  (console-fn js/console.groupEnd))

(def info 
  (console-fn js/console.info))

(def log 
  (console-fn js/console.log))

(def profile 
  (console-fn js/console.profile))

(def profile-end
  (console-fn js/console.profileEnd))

(def time 
  (console-fn js/console.time))

(def time-end 
  (console-fn js/console.timeEnd))

(def time-stamp 
  (console-fn js/console.timeStamp))

(def trace 
  (console-fn js/console.trace))

(def warn 
  (console-fn js/console.warn))




