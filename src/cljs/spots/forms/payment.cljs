(ns spots.forms.payment
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [forms.validator :as v]
            [spots.forms.validators :as vs]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [promesa.core :as p]
            [spots.domain.stripe :refer [stripe-client]]
            [spots.gql :refer [validate-order-payment-m create-order-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [spots.domain.cupcake-design :refer [strip-local-data]]
            [oops.core :refer [oget ocall]]))

(def validator
  (v/validator {}))

(defn assoc-stripe-token [card form-data]
  (p/promise
   (fn [resolve reject]
     (ocall (ocall stripe-client "createToken" card) "then"
            (fn [result]
              (let [error-message (oget result "?error.?message")]
                (if error-message
                  (reject (ex-info error-message {}))
                  (resolve (assoc form-data :stripe-token (:id (js->clj (oget result "token") :keywordize-keys true)))))))))))

(defrecord PaymentForm [validator])

(defmethod forms-core/get-data PaymentForm [this app-db form-props]
  (get-in app-db [:kv :order-form]))

(defn validate-and-create-order [data total-price]
  (let [clean-data (-> data
                       (strip-local-data)
                       (dissoc :cardHoldersName)
                       (dissoc :stripe-card)
                       (dissoc :cc-errors))]
    (->> (wrap-validation
          (gql-req validate-order-payment-m (wrap-request clean-data)))
         (p/map (fn [_]
                  (gql-req create-order-m (wrap-request clean-data)))))))

(defmethod forms-core/submit-data PaymentForm [_ app-db _ data]
  (let [total-price (get-in app-db [:kv :order-pricing :total])]
    (pipeline! [value app-db]
      (assoc-stripe-token (:stripe-card data) data)
      (validate-and-create-order value total-price)
      (rescue! [error]
        nil))))

(defmethod forms-core/on-submit-error PaymentForm [this app-db form-props data error]
  ;;(println "ERROR:" error)
  )

(defmethod forms-core/on-submit-success PaymentForm [this app-db form-props data]
  (let [res (:createOrder data)
        form-data (:data (get-form-state app-db form-props))]
    (pipeline! [value app-db]
      (when (:valid res)
        (pipeline! [value app-db] 
          (pp/commit! (assoc-in app-db [:kv :show-paid-modal] true)))))))

(defn constructor []
  (->PaymentForm validator))
