(ns himotoki.social-test
  (:require [clojure.test :refer [deftest is]]
            [himotoki.methods.social :as social]
            [himotoki.cells.social-post.state-machine :as machine]))

(deftest social-projection-is-own-data-only
  (let [post (social/draft-filing-post
              {"id" "window-1" "window" "2026-Q3" "dsar_count" 1 "foia_count" 2}
              ["cid:statute" "cid:registry"])]
    (is (= ":dry-run" (get post ":post/status")))
    (is (true? (get post ":post/own-data-only")))
    (is (= ":pending-operator-transport"
           (get (social/emit post) "external_relay")))))

(deftest publication-state-machine-is-dry-run-only
  (is (= machine/phase-drafted
         (get-in (machine/transition-to-drafted
                  {"subject" "disclosure" "sources" ["cid:a" "cid:b"]})
                 ["cell_state" "phase"])))
  (is (= machine/phase-refused
         (get-in (machine/transition-to-drafted
                  {"subject" "disclosure" "sources" ["cid:a" "cid:b"]
                   "requested_status" "published"})
                 ["cell_state" "phase"]))))
