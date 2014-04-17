(ns khroma.extension
)

(defn get-url [path]
  (.getURL js/chrome.extension path))