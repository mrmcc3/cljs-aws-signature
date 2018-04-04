# cljs-aws-signature

A pure ClojureScript implementation of the
[AWS Signature Version 4 Signing Process](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)

## Usage

[![Clojars Project](https://img.shields.io/clojars/v/mrmcc3/cljs-aws-signature.svg)](https://clojars.org/mrmcc3/cljs-aws-signature)

See `test/mrmcc3/aws/sig_v4_usage.cljs` for an example. Run with

```
$ clj -Atest:usage
```

Example request map.
```clojure
{:method  "GET"
 :path    "/thepath/"
 :query   {:Hello "World"}
 :headers {:Host "hw.com", :X-Amz-Date "20180403T075016Z"}
 :body    "the request body"

 :service "lambda"
 :region  "ap-southeast-2"
 :access  "aws-access-key-id"
 :secret  "aws-secret-access-key"}
```

Output of `sign-req`
```clojure
{:method    "GET"
 :path      "/thepath/"
 :query     {:Hello "World"}
 :headers   {:Host "hw.com", :X-Amz-Date "20180403T075016Z"}
 :body      "the request body"

 :service   "lambda"
 :region    "ap-southeast-2"
 :secret    "aws-secret-access-key"
 :access    "aws-access-key-id"

 :alg       "AWS4-HMAC-SHA256"
 :iso       "20180403T075016Z"
 :day       "20180403"
 :signed    "host;x-amz-date"
 :creq      "GET\n/thepath/\nHello=World\nhost:hw.com\nx-amz-date:20180403T075016Z\n\nhost;x-amz-date\n6b5eacc80f13368f01e2107935c6adaccd58cda3a709cc2faebe29c016ab8962"
 :scope     "20180403/ap-southeast-2/lambda/aws4_request"
 :sts       "AWS4-HMAC-SHA256\n20180403T075016Z\n20180403/ap-southeast-2/lambda/aws4_request\n00bb03ecf97c0674c08f0ac63f2f9c9ac04c447b2cdaced9418336ce92bb5837"
 :signature "05b9ee058cffb668b7bec341155a08c0a37211fbd64c63f584487fb349383fd8"
 :authz     "AWS4-HMAC-SHA256 Credential=aws-access-key-id/20180403/ap-southeast-2/lambda/aws4_request, SignedHeaders=host;x-amz-date, Signature=05b9ee058cffb668b7bec341155a08c0a37211fbd64c63f584487fb349383fd8"}
```

## Tests

Includes all tests from the [AWS Test Suite](https://docs.aws.amazon.com/general/latest/gr/signature-v4-test-suite.html)
except for `get-header-value-multiline`

```
$ clj -Atest:node
$ clj -Atest:adv
```
