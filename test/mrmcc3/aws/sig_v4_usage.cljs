(ns mrmcc3.aws.sig-v4-usage
  (:require
    [mrmcc3.aws.sig-v4 :as v4]
    [cljs.pprint :refer [pprint]]))

(def request
  (let [date (js/Date.)]
    {:method  "GET"
     :path    "/thepath/"
     :query   {:Hello "World"}
     :headers {:Host "hw.com" :X-Amz-Date (v4/date->iso date)}
     :region  "ap-southeast-2"
     :service "lambda"
     :body    "the request body"
     :access  "aws-access-key-id"
     :secret  "aws-secret-access-key"}))

(println "example request")
(pprint request)
(println)

(println "example result")
(pprint (v4/sign-req request))
(println)

(println "example auth header")
(pprint (v4/sign request))
(println)
