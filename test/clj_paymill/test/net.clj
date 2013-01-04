(ns clj-paymill.test.net
  (:use clojure.test
        clj-paymill.net))

(defmacro catch-data [form]
  `(try (~@form)
        (catch Exception ex#
          (ex-data ex#))))

(deftest parses-errors?
  (is (= {:error "Access Denied", :exception "Api_Exception_InvalidAuthentication"}
         (catch-data (paymill-request "invalidkey" :get "clients")))))

(deftest parses-arbitrarily-different-error-formats?
  (is (= {:error {:messages {:required "Parameter is mandatory"}, :field "Identifier"}}
         (catch-data (paymill-request (generate-test-key) :delete ["subscriptions" nil])))))

(deftest gets-from-api?
  (is (= [] (paymill-request (generate-test-key) :get "clients"))))

(deftest posts-to-api?
  (is (map? (paymill-request (generate-test-key) :post "clients"))))

(deftest gets-nested-resources?
  (is (= (str *api-root-url* "subscriptions/1234") (resource-url ["subscriptions" "1234"]))))