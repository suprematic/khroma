(defproject
  khroma "0.3.0-SNAPSHOT"
  :description "ClojureScript interface to Google Chrome Extension API"
  :url "https://github.com/suprematic/khroma"

  :license { :name "EPL" :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths  ["src/main"]


  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
   )
  :plugins [[lein-cljsbuild "1.1.0"]]

  :cljsbuild {:builds
              {:main
               {:source-paths ["src/main"]
                :compiler     {:output-to    "target/khroma.js"
                               :output-dir   "target/js"
                               :pretty-print true}}}}                               :pretty-print true}}}}
