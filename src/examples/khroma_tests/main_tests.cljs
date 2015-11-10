(ns khroma-tests.main-tests
  (:require
    [devcards.core :as core]
    [khroma.log :as console]
    [khroma-tests.extension]
    [khroma-tests.identity]
    [khroma-tests.idle]
    [khroma-tests.tabs]
    [khroma-tests.runtime]
    [khroma-tests.storage]
    [khroma-tests.windows]
    ))


(defn ^:export main []
  (console/log "Initializing UI...")
  (core/start-devcard-ui!*))
