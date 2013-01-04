(ns clj-paymill.net
  (:require [clj-http.client :as http]
            [clj-http.util :refer [url-decode]]
            [clojure.data.json :refer [read-str]]
            [clojure.string :refer [split join]]))

(def ^:dynamic *key-url* "https://app.paymill.com/en-gb/default/iframe/key")
(def ^:dynamic *api-root-url* "https://api.paymill.com/v2/")
(def ^:dynamic *insecure?* false)

(defn generate-test-key []
  (-> (http/get *key-url*)
      (get-in [:cookies "tk" :value])
      (url-decode)
      (split #":")
      (second)))

(defn resource-url [resource]
  (str *api-root-url* (if (vector? resource) (join "/" resource) resource)))

(defn parse-value [key value]
  (condp = key
    :cancel_at_period_end (Boolean. value)
    value))

(defn parse-body [body]
  (let [json (read-str body :key-fn keyword :value-fn parse-value)]
   (if-let [data (:data json)]
     data
     json)))

(defn paymill-request [key method resource & [params]]
  (parse-body (try (:body (http/request {:url (resource-url resource)
                                         :method method
                                         :insecure? *insecure?*
                                         :accept :json
                                         :basic-auth [key ""]
                                         :query-params (if (= :get method) params)
                                         :form-params params}))
                   (catch Exception ex
                     (-> (ex-data ex) :object :body)))))