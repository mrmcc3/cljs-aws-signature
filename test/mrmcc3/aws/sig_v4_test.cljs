(ns mrmcc3.aws.sig-v4-test
  (:require-macros
    [mrmcc3.aws.sig-v4-test :refer [test-data]])
  (:require
    [mrmcc3.aws.sig-v4 :as v4]
    [clojure.string :as str]
    [clojure.pprint :refer [pprint]])
  (:import
    (goog Uri)
    (goog.date UtcDateTime)))

(defn query-map [uri]
  (let [qd (.getQueryData (Uri. uri))]
    (map vector (.getKeys qd) (.getValues qd))))

(defn iso->date [iso]
  (.-date (.fromIsoString UtcDateTime iso)))

(defn req->map [{:keys [method uri headers body]}]
  (let [[path] (str/split uri #"\?" 2)]
    {:method  method
     :path    path
     :query   (query-map uri)
     :headers headers
     :region  "us-east-1"
     :service "service"
     :date    (iso->date (get (into {} headers) "X-Amz-Date"))
     :body    body
     :access  "AKIDEXAMPLE"
     :secret  "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY"}))

(doseq [[k data] (test-data "test/suite-v4")]
  (let [result  (-> data :req req->map v4/sign-req)
        keys    [:creq :sts :authz]
        success (= (select-keys data keys) (select-keys result keys))]
    (when success
      (println "testing" k "... OK"))
    (when-not success
      (println "testing" k "... FAILED")
      (println (:creq data))
      (println (:creq result)))))

