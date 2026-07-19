(ns himotoki.methods.request-host
  "Babashka host adapter for reading himotoki's JSON target registry."
  (:require [cheshire.core :as json]
            [himotoki.methods.request :as request]))

(defn load-registry
  "Read and decode a registry path, then hand data to the portable indexer."
  [path]
  (request/index-registry (json/parse-string (slurp (str path)))))
