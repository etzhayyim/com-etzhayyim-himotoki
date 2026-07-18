(ns himotoki.registry-test
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :refer [deftest is]]))

(def registry (edn/read-string (slurp "registry/targets.seed.edn")))
(def targets (get registry "targets"))

(deftest registry-is-non-empty
  (is (map? registry))
  (is (seq targets)))

(deftest organizations-are-unique
  (let [ids (mapv #(get % "organization") targets)]
    (is (every? seq ids))
    (is (= (count ids) (count (set ids))))))

(deftest seed-is-fail-closed
  (doseq [target targets]
    (is (= "unverified-seed" (get target "verificationStatus"))
        (get target "organization"))))

(deftest provenance-and-verification-time-are-present
  (doseq [target targets]
    (is (str/starts-with? (get target "provenance" "") "https://")
        (get target "organization"))
    (is (re-matches #"\d{4}-\d{2}-\d{2}T.*" (get target "lastVerified" ""))
        (get target "organization"))))

(deftest worldwide-jurisdiction-coverage
  (is (every? #(seq (get % "jurisdiction")) targets))
  (is (<= 12 (count (set (map #(get % "jurisdiction") targets))))))

(deftest constitutional-boundary-is-explicit
  (is (every? #(not (str/blank? (get % "notes" ""))) targets))
  (let [corpus (str/lower-case (pr-str registry))]
    (is (str/includes? corpus "own-data-only"))
    (is (str/includes? corpus "unverified-seed"))
    (is (or (str/includes? corpus "lawful-channel")
            (str/includes? corpus "consent-gated")))))

(deftest freshness-window-is-positive-integer
  (let [days (get registry "freshnessWindowDays")]
    (is (integer? days))
    (is (pos? days))))
