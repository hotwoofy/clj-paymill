clj-paymill
===========

Clojure library for the Paymill API.

Installation
------------

`clj-paymill` is up on [Clojars](http://clojars.org/clj-paymill):

```clojure
[clj-paymill "0.1.1"]
```

Testing
-------

If you haven't got a Paymill account set up but want to experiment, you can peruse the tests, wherein we somewhat filthily scrape the Paymill site to retrieve a test key. This obviously isn't guaranteed to work forever, and leaves some crud on their server, so don't overdo it.

Usage
-----

Only very limited functionality is offered at this stage (i.e. just what I need). There are two namespaces, `clj-paymill.net` which is a thin wrapper around `clj-http` to talk to the Paymill REST API and spit JSON back out, and `clj-paymill.client` which is in turn a thin wrapper around that.

```clojure
(ns paymill-test
  (:require [clj-paymill.net :refer :all]
            [clj-paymill.client :refer :all]))
        
;; get a private key for testing 
(def key (generate-test-key))

;; create a client
(create-client! key "example@example.org" "Test Testerson")

;; find them again
(list-clients key {:email "example@example.org"})

;; create a payment (token taken from Paymill Bridge)
(create-payment! key token client)

;; create an offer
(create-offer! key "Test offer" 10 "GBP" "month")

;; list offers
(list-offers key)

;; create a subscription
(subscribe! key client payment offer)

;; check its current status
(subscription-details key (:id subscription))

;; cancel it
(cancel-subscription! key (:id subscription) true)

;; talk to other parts of the API
(paymill-request key :post "preauthorizations" {...})
(paymill-request key :get ["preauthorizations" (:id preauthorization)])
```
