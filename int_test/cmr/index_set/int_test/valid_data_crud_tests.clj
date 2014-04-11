(ns cmr.index-set.int-test.valid-data-crud-tests
  "Contains integration tests to verify index-set crud operations with good data."
  (:require [clojure.test :refer :all]
            [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [clojure.walk :as walk]
            [clojurewerkz.elastisch.rest.index :as esi]
            [cmr.index-set.int-test.utility :as util]))

;;; fixtures
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn setup [] (util/flush-elastic))
(defn teardown [] (util/reset))

(defn each-fixture [f]
  (setup)
  (f)
  (teardown))

(use-fixtures :each each-fixture)

;;; tests
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Verify index-set creation is successful.
;; use elastisch to verify all indices of index-set exist and the index-set doc has been indexed
;; in elastic
(deftest create-index-set-test
  (testing "create index-set"
    (let [index-set util/sample-index-set
          {:keys [status]} (util/submit-create-index-set-req index-set)]
      (is (= status 201))))
  (testing "indices existence"
    (let [index-set util/sample-index-set
          index-set-id (-> index-set :index-set :id)
          _ (util/flush-elastic)
          index-names (util/get-index-names index-set)]
      (for [idx-name index-names]
        (is (esi/exists? idx-name)))))
  (testing "index-set doc existence"
    (let [index-set util/sample-index-set
          index-set-id (-> index-set :index-set :id)
          {:keys [status]} (util/get-index-set index-set-id)]
      (is (= status 200)))))

;; Verify index-set fetch is successful.
;; First create a index-set, fetch the index-set using an id successfully and then
;; assert one of the expected index by name in index-set is created in elastic.
(deftest get-index-set-test
  (testing "index-set fetch by id"
    (let [index-set util/sample-index-set
          suffix-idx-name "C99-Collections"
          mod-index-set (-> index-set
                            (assoc-in [:index-set :collection :index-names] (vec (list suffix-idx-name)))
                            (assoc-in [:index-set :id] 77))
          index-set-id (-> mod-index-set :index-set :id)
          expected-idx-name (util/gen-valid-index-name index-set-id suffix-idx-name)
          {:keys [status]} (util/submit-create-index-set-req mod-index-set)
          _ (util/flush-elastic)
          body (-> (util/get-index-set index-set-id) :response :body)
          fetched-index-set (cheshire.core/decode body true)
          actual-idx-name (get-in fetched-index-set [:index-set :concepts :collection (keyword suffix-idx-name)])]
      (is (= status 201))
      (is (= expected-idx-name actual-idx-name)))))

;; Verify index-set delete is successful.
;; First create a index-set, verify a specified index in index-set is created, delete index-set
;; and verify specified index is not present now to ensure delete is successful
(deftest delete-index-set-test
  (testing "create index-set"
    (let [index-set util/sample-index-set
          suffix-idx-name "C99-Collections"
          mod-index-set (-> index-set
                            (assoc-in [:index-set :collection :index-names] (vec (list suffix-idx-name))))
          index-set-id (-> mod-index-set :index-set :id)
          expected-idx-name (util/gen-valid-index-name index-set-id suffix-idx-name)
          {:keys [status]} (util/submit-create-index-set-req mod-index-set)]
      (is (= status 201))
      (is (esi/exists? expected-idx-name))))
  (testing "delete index-set"
    (let [index-set util/sample-index-set
          index-set-id (-> index-set :index-set :id)
          suffix-idx-name "C99-Collections"
          expected-idx-name (util/gen-valid-index-name index-set-id suffix-idx-name)
          {:keys [status]} (util/submit-delete-index-set-req index-set-id)]
      (is (= status 200))
      (is (not (esi/exists? expected-idx-name))))))

;; Verify get index-sets fetches all index-sets in elastic.
;; Create 2 index-sets with different ids but with same number of concepts and indices associated
;; with each concept. Remember total number of indices in index-sets. Fetch all index-sets
;; from elastic to count indices. Count should match and all of the indices listed in index-sets
;; should be present in elastic
(deftest get-index-sets-test
  (testing "fetch all index-sets"
    (let [index-set util/sample-index-set
          _ (util/submit-create-index-set-req index-set)
          _ (util/submit-create-index-set-req (assoc-in index-set [:index-set :id] 77))
          indices-cnt (reduce (fn [cnt concept] (+ cnt
                                                   (count (get-in util/sample-index-set
                                                                  [:index-set concept :index-names])))) 0 util/cmr-concepts)
          expected-idx-cnt (* 2 indices-cnt)
          _ (util/flush-elastic)
          body (-> (util/get-index-sets) :response :body (cheshire.core/decode true))
          ;; actual-indices-map (apply merge (util/list-es-indices body))
          actual-es-indices (util/list-es-indices body)]
      (for [es-idx-name actual-es-indices]
        (is (esi/exists? es-idx-name)))
      (is (= expected-idx-cnt (count actual-es-indices))))))


;; Verify creating same index-set twice will result in 409
(deftest create-index-set-twice-test
  (testing "create index-set"
    (let [index-set util/sample-index-set
          {:keys [status]} (util/submit-create-index-set-req index-set)]
      (is (= status 201))))
  (testing "create same index-set"
    (let [index-set util/sample-index-set
          index-set-id (-> index-set :index-set :id)
          {:keys [status errors-str]} (util/submit-create-index-set-req index-set)]
      (is (= status 409))
      (is (re-find #"already exists" errors-str)))))

;; Verify reset deletes all of the indices assoc with index-sets and index-set docs
(deftest reset-index-sets-test
  (testing "reset index-set app"
    (let [{:keys [status]} (util/reset)]
      (is (= status 200)))))





