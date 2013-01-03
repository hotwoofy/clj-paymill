(ns clj-paymill.test.net
  (:use clojure.test
        clj-paymill.net))

(deftest parses-errors?
  (is (= "Api_Exception_InvalidAuthentication" (:exception (paymill-request "invalidkey" :get "clients")))))

(deftest generates-valid-test-keys?
  (is (nil? (:exception (paymill-request (generate-test-key) :get "clients")))))

(deftest gets-from-api?
  (is (= [] (paymill-request (generate-test-key) :get "clients"))))

(deftest posts-to-api?
  (is (map? (paymill-request (generate-test-key) :post "clients"))))

(deftest gets-nested-resources?
  (is (= (str *api-root-url* "subscriptions/1234") (resource-url ["subscriptions" "1234"]))))