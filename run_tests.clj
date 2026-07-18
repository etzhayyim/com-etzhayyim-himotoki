(require '[babashka.classpath :as cp]
         '[babashka.fs :as fs]
         '[clojure.test :as t])

(let [root (fs/parent (fs/absolutize *file*))]
  (cp/add-classpath (str root "/src"))
  (cp/add-classpath (str root "/test")))

(def suites '[himotoki.methods.test-charter-gates
              himotoki.methods.test-deadline-status
              himotoki.methods.test-request
              himotoki.registry-test
              himotoki.social-test
              himotoki.murakumo-test
              himotoki.repository-contract-test])
(apply require suites)
(let [{:keys [fail error]} (apply t/run-tests suites)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
