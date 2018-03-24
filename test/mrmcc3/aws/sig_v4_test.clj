(ns mrmcc3.aws.sig-v4-test
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str])
  (:import (java.io File)))

(defn parse-req [req]
  (let [[head body] (str/split req #"\n\n")
        [req & headers] (str/split head #"\n")
        headers (mapv #(str/split % #":" 2) headers)
        [req] (str/split req #" HTTP/1.1")
        [method uri] (str/split req #" " 2)]
    {:method method :uri uri :headers headers :body body}))

(defn collect-test-data [data ^File f]
  (let [[key ext] (str/split (.getName f) #"\.")
        path [key (keyword ext)]]
    (case ext
      "req" (assoc-in data path (parse-req (slurp f)))
      "creq" (assoc-in data path (slurp f))
      "sts" (assoc-in data path (slurp f))
      "authz" (assoc-in data path (slurp f))
      data)))

(defmacro test-data [src]
  (reduce collect-test-data {} (file-seq (io/file src))))
