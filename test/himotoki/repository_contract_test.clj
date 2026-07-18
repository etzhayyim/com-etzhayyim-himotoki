(ns himotoki.repository-contract-test
  (:require [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]))

(deftest canonical-repository-shape
  (doseq [path ["manifest.edn" "identity.edn" "dependencies.edn"
                "repository-contracts.edn" "kotoba.app.edn"
                "registry/targets.seed.edn"
                "data/persisted/himotoki-request-drafts.kotoba.edn"]]
    (is (some? (edn/read-string (slurp path))) path))
  (is (= 5 (count (filter #(and (.isFile %) (.endsWith (.getName %) ".edn"))
                          (file-seq (io/file "lex"))))))
  (is (= 65 (count (get (edn/read-string (slurp "registry/targets.seed.edn")) "targets"))))
  (is (not (.exists (io/file "manifest.jsonld"))))
  (is (not (.exists (io/file "run_tests.sh")))))

(deftest wire-projections-match-canonical-edn
  (is (= (edn/read-string (slurp "registry/targets.seed.edn"))
         (json/parse-string (slurp "wire/registry/targets.seed.json"))))
  (doseq [name ["appealRecord" "disclosureRequest" "disclosureResponse"
                "disclosureTarget" "requestDispatch"]]
    (is (= (edn/read-string (slurp (str "lex/" name ".edn")))
           (json/parse-string (slurp (str "wire/lex/" name ".json")))))))
