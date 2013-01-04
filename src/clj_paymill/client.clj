(ns clj-paymill.client
  (:use clj-paymill.net))

(defn create-client! [key email name]
  (paymill-request key :post "clients" {:email email :description name}))

(defn list-clients [key & [filters]]
  (paymill-request key :get "clients" filters))

(defn get-client [key id]
  (paymill-request key :get ["clients" id]))

(defn update-client! [key id email name]
  (paymill-request key :put ["clients" id] {:email email :description name}))

(defn create-payment!
  ([key token]
     (paymill-request key :post "payments" {:token token}))
  ([key token client]
     (paymill-request key :post "payments" {:token token :client (:id client)})))

(defn create-offer! [key name amount currency interval]
  (paymill-request key :post "offers" {:name name
                                       :amount amount
                                       :currency currency
                                       :interval interval}))

(defn list-offers [key & [filters]]
  (paymill-request key :get "offers" filters))

(defn subscribe! [key client payment offer]
  (paymill-request key :post "subscriptions" {:client (:id client)
                                              :offer (:id offer)
                                              :payment (:id payment)}))

(defn subscription-details [key id]
  (paymill-request key :get ["subscriptions" id]))

(defn cancel-subscription! [key id & [at-period-end?]]
  (if at-period-end?
    (paymill-request key :put ["subscriptions" id] {:cancel_at_period_end (boolean at-period-end?)})
    (paymill-request key :delete ["subscriptions" id])))