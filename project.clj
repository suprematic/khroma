 (defproject khroma "0.0.2-SNAPSHOT"
  :description "ClojureScript interface to Google Chrome Extension API"
  :url "https://github.com/suprematic/khroma"
  
  :license { :name "Eclipse"
    :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :source-paths  ["src"]
  
  :dependencies [[org.clojure/clojure "1.5.1"]
        [org.clojure/clojurescript "0.0-2234"]
        [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]
  
  :plugins [[lein-cljsbuild "1.0.2"]]
  
  :cljsbuild {
    :builds[{:id "dev"
    :source-paths ["src"]
    :compiler {:output-dir "extension/js/compiled"
      :output-to "extension/js/compiled/ascent.js"
      :source-map "extension/js/compiled/ascent.js.map"
      :optimizations :none
      :pretty-print true}}]})