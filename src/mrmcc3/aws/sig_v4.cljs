(ns mrmcc3.aws.sig-v4
  (:require
    [clojure.string :as str]
    [goog.crypt :as crypt])
  (:import
    (goog.crypt Hmac Sha256)
    (goog.date UtcDateTime)
    (goog.Uri QueryData)
    (goog Uri)))

(defn sha256 [s]
  (let [hasher (Sha256.)]
    (.update hasher (crypt/stringToByteArray s))
    (crypt/byteArrayToHex (.digest hasher))))

(defn hmac [k msg]
  (-> (Sha256.) (Hmac. k) (.getHmac msg)))

(defn date->iso [date]
  (.toIsoString (UtcDateTime. date) false true))

(defn process-query [query]
  (reduce
    (fn [q [k v]] (.add q (name k) v))
    (QueryData.)
    (sort query)))

(defn trim-header [acc [header value]]
  (let [h (-> header name str/lower-case str/trim)
        v (-> value (str/replace #"\s+" " ") str/trim)]
    (update acc h (fnil conj []) v)))

(defn header-string [[h vals]]
  (str h ":" (str/join "," vals)))

(defn process-headers [headers]
  (let [trimmed   (reduce trim-header {} headers)
        sorted    (sort-by key trimmed)
        canonical (map header-string sorted)
        signed    (map first sorted)]
    [(str/join "\n" canonical)
     (str/join ";" signed)
     (get-in trimmed ["x-amz-date" 0])]))

(defn path->uri [path]
  (-> (.removeDotSegments Uri (or path "/"))
      (str/replace-all #"//" "/")
      (Uri.)))

(defn canonical-request [{:keys [method path query headers payload body] :as req}]
  (let [[canonical signed iso] (process-headers headers)
        creq (str/join "\n" [method (path->uri path) (process-query query)
                             canonical "" signed (sha256 (or payload body ""))])]
    (assoc req :creq creq :signed signed :iso iso :day (subs iso 0 8))))

(defn string-to-sign [{:keys [region service creq iso day] :as req}]
  (let [alg   "AWS4-HMAC-SHA256"
        scope (str/join "/" [day region service "aws4_request"])
        hash  (sha256 creq)
        sts   (str/join "\n" [alg iso scope hash])]
    (assoc req :sts sts :alg alg :scope scope)))

(defn signature [{:keys [day region service secret sts] :as req}]
  (-> (str "AWS4" secret)
      (crypt/stringToByteArray)
      (hmac day)
      (hmac region)
      (hmac service)
      (hmac "aws4_request")
      (hmac sts)
      (crypt/byteArrayToHex)
      (->> (assoc req :signature))))

(defn auth-header [{:keys [alg access scope signed signature] :as req}]
  (assoc req :authz (str alg " Credential=" access "/" scope ", SignedHeaders="
                         signed ", Signature=" signature)))

(defn sign-req [req]
  (-> req canonical-request string-to-sign signature auth-header))

(defn sign [req]
  (-> req sign-req :authz))

