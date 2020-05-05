;; Copyright ⓒ the conexp-clj developers; all rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns conexp.fca.smeasure-test
  (:use conexp.fca.contexts conexp.fca.smeasure conexp.base)
  (:use clojure.test))


(deftest test-remove-attributes 
  (let [ctx (rand-context (range 6) 0.5)
        sm (make-id-smeasure ctx)
        removed (remove-attributes-sm sm #{1 2})]
    (is (= (attributes (scale removed)) #{0 3 4 5}))
    (is (smeasure? removed))))

(deftest test-rename
  (let [ctx (rand-context (range 6) 0.5)
        sm (make-id-smeasure ctx)
        renamed (rename-scale-attributes 
                 (rename-scale-objects sm 1 "test1" 2 "test2")
                 inc)]
    (is (smeasure? renamed))))

(deftest test-clusterings
  (let [ctx (rand-context (range 5) 0.5)
        sm (make-id-smeasure ctx)
        candidates (rest(subsets (range 5)))
        valid-attr-cl-all (map #(cluster-attributes-all sm %) candidates)
        valid-attr-cl-ex (map #(cluster-attributes-ex sm %) candidates)
        valid-obj-cl-all (map #(cluster-objects-all sm %) candidates)
        valid-obj-cl-ex (map #(cluster-objects-ex sm %) candidates)]
    (is (every? valid-scale-measure? (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-attr-cl-all)))
    (is (<= (+ 1 (count (attributes ctx))) (count (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-attr-cl-all))))
    (is (every? valid-scale-measure? (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-attr-cl-ex)))
    (is (<= (+ 1 (count (attributes ctx))) (count (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-attr-cl-ex))))
    (is (every? valid-scale-measure? (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-obj-cl-all)))
    (is (<= (+ 1 (count (objects ctx))) (count (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-obj-cl-all))))
    (is (every? valid-scale-measure? (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-obj-cl-ex)))
    (is (<= (+ 1 (count (objects ctx))) (count (filter #(instance? conexp.fca.smeasure.ScaleMeasure %) valid-obj-cl-ex))))))
