(ns clj-paymill.test.client
  (:use clojure.test
        clojure.data
        clj-paymill.net
        clj-paymill.client))

(def test-payment-token "098f6bcd4621d373cade4e832627b4f6")

(deftest creates-clients?
  (let [client (create-client! (generate-test-key) "example@example.org" "Test Testerson")]
    (is (not (nil? (:id client))))
    (is (= "example@example.org" (:email client)))
    (is (= "Test Testerson" (:description client)))))

(deftest lists-clients?
  (let [key (generate-test-key)]
    (doseq [i (range 0 5)]
      (create-client! key (str "example-" i "@example.org") "Test Testerson"))
    (let [clients (list-clients key)]
      (is (= 5 (count clients)))
      (is (= "example-0@example.org" (:email (first clients)))))))

(deftest gets-clients?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")]
    (is (= client (get-client key (:id client))))))

(deftest updates-clients?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")
        update (update-client! key (:id client) "example-changed@example.org" "Changed Testerson")
        updated-client (get-client key (:id client))]
    (is (= "example-changed@example.org" (:email updated-client)))
    (is (= "Changed Testerson" (:description updated-client)))))

(deftest filters-lists?
  (let [key (generate-test-key)]
    (doseq [i (range 0 5)]
      (create-client! key (str "example-" i "@example.org") "Test Testerson"))
    (let [clients (list-clients key {:email "example-0@example.org"})]
      (is (= 1 (count clients)))
      (is (= "example-0@example.org" (:email (first clients)))))))

(deftest creates-payments?
  (let [payment (create-payment! (generate-test-key) test-payment-token)]
    (is (nil? (:client payment)))
    (is (= "1111" (:last4 payment)))))

(deftest creates-client-payments?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")
        payment (create-payment! key test-payment-token client)]
    (is (= (:id client) (:client payment)))
    (is (= "1111" (:last4 payment)))))

(deftest creates-offers?
  (let [offer (create-offer! (generate-test-key) "Test offer" 5 "GBP" "month")]
    (is (= "Test offer") (:name offer))))

(deftest lists-offers?
  (let [key (generate-test-key)]
    (doseq [i (range 1 6)]
      (create-offer! key (str "Test offer " i) (* i 10) "GBP" "month"))
    (let [offers (list-offers key)]
      (is (= 5 (count offers)))
      (is (= 50 (:amount (last offers)))))))

(deftest creates-subscriptions?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")
        payment (create-payment! key test-payment-token client)
        offer (create-offer! key "Test offer" 10 "GBP" "month")
        subscription (subscribe! key client payment offer)]
    (is (= (:id offer) (-> subscription :offer :id)))
    (is (= (:id client) (-> subscription :client :id)))
    (is (= (:id payment (-> subscription :client :payment :id))))))

(deftest details-subscriptions?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")
        payment (create-payment! key test-payment-token client)
        offer (create-offer! key "Test offer" 10 "GBP" "month")
        subscription (subscribe! key client payment offer)
        details (subscription-details key (:id subscription))]
    (is (nil? (first (diff subscription details))))))

(deftest cancels-subscriptions-immediately?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")
        payment (create-payment! key test-payment-token client)
        offer (create-offer! key "Test offer" 10 "GBP" "month")
        subscription (subscribe! key client payment offer)
        cancellation (cancel-subscription! key (:id subscription))]
    (is (= false (:cancel_at_period_end cancellation)))
    (is (not (nil? (:canceled_at cancellation))))))

(deftest cancels-subscriptions-at-period-end?
  (let [key (generate-test-key)
        client (create-client! key "example@example.org" "Test Testerson")
        payment (create-payment! key test-payment-token client)
        offer (create-offer! key "Test offer" 10 "GBP" "month")
        subscription (subscribe! key client payment offer)
        cancellation (cancel-subscription! key (:id subscription) true)]
    (is (= true (:cancel_at_period_end cancellation)))))