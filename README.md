# cljs-aws-signature

A pure ClojureScript implementation of the
[AWS Signature Version 4 Signing Process](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)

## Usage

See `test/mrmcc3/aws/sig_v4_usage.cljs` for an example. Run with `cljs.main` via nodejs

```
$ clj -Atest -m cljs.main -re node test/mrmcc3/aws/sig_v4_usage.cljs
```

Example request map.
```clojure
{:path "/thepath/",
 :service "lambda",
 :date #inst "2018-03-24T06:17:56.256-00:00",
 :payload "the request body",
 :method "GET",
 :secret "aws-secret-access-key",
 :headers {:Host "hw.com", :X-Amz-Date "20180324T061756Z"},
 :region "ap-southeast-2",
 :access "aws-access-key-id", 
 :query {:Hello "World"}}
```

Output of `sign-req`
```clojure
{:path "/thepath/",
 :service "lambda",
 :alg "AWS4-HMAC-SHA256",
 :date #inst "2018-03-24T06:17:56.256-00:00",
 :payload "the request body",
 :method "GET",
 :signature "8398f0b217578062ace2204ca36a0911d3840fe36268c0c8c6f15d04857859ad",
 :signed-headers "host;x-amz-date",
 :scope "20180324/ap-southeast-2/lambda/aws4_request",
 :secret "aws-secret-access-key",
 :headers {:Host "hw.com", :X-Amz-Date "20180324T061756Z"},
 :region "ap-southeast-2",
 :sts "AWS4-HMAC-SHA256\n20180324T061756Z\n20180324/ap-southeast-2/lambda/aws4_request\n440933f30a0f8431a994cf72e1ab5fffe4594746fda23a47cf12572f8e58e64c",
 :creq "GET\n/thepath/\nHello=World\nhost:hw.com\nx-amz-date:20180324T061756Z\n\nhost;x-amz-date\n6b5eacc80f13368f01e2107935c6adaccd58cda3a709cc2faebe29c016ab8962",
 :access "aws-access-key-id",
 :authz "AWS4-HMAC-SHA256 Credential=aws-access-key-id/20180324/ap-southeast-2/lambda/aws4_request, SignedHeaders=host;x-amz-date, Signature=8398f0b217578062ace2204ca36a0911d3840fe36268c0c8c6f15d04857859ad",
 :query {:Hello "World"}}
```

## Tests

Includes all tests from the [AWS Test Suite](https://docs.aws.amazon.com/general/latest/gr/signature-v4-test-suite.html)
except for `get-header-value-multiline`

```
$ clj -Atest -m cljs.main test/mrmcc3/aws/sig_v4_test.cljs
$ clj -Atest -m cljs.main -re node test/mrmcc3/aws/sig_v4_test.cljs
$ clj -Atest -m cljs.main -O advanced -c mrmcc3.aws.sig_v4_test
$ xdg-open test/index.html
```
