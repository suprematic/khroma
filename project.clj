(defproject
  khroma "0.3.0"
  :description "ClojureScript interface to Google Chrome Extension API"
  :url "https://github.com/suprematic/khroma"

  :license {:name "EPL" :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/main"]


  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[lein-cljsbuild "1.1.0"]]

  :clean-targets ^{:protect false} ["extension/khroma_tests.js" "target"]

  :cljsbuild {:builds
              {:main
               {:source-paths ["src/main"]
                :compiler     {:output-to    "target/khroma.js"
                               :output-dir   "target/js"
                               :pretty-print true}}}}

  :profiles {:khroma-tests
             {:dependencies [[devcards "0.2.0-5"]]
              :plugins      [[lein-cljsbuild "1.1.0"]]
              :cljsbuild    {:builds
                             {:main
                              {:source-paths ["src/main" "src/examples"]

                               :compiler     {:output-to     "extension/khroma_tests.js"
                                              :output-dir    "target/js"
                                              :optimizations :whitespace
                                              :main          "khroma-tests.main-tests"
                                              :devcards      true
                                              :pretty-print  true}}}}}})