(ns spots.forms.confirmation
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [spots.forms.validators :as validators]
            [forms.validator :as v]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [promesa.core :as p]
            [spots.domain.stripe :refer [stripe-client]]
            [spots.gql :refer [validate-order-overview-m calculate-order-prices-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [spots.domain.cupcake-design :refer [remove-cupcake-ids prepare-flavors]]
            [spots.domain.cupcake-design :refer [strip-local-data]]
            [oops.core :refer [oget ocall]]))

(def validator
  (v/validator {:shareableImages [[:not-empty validators/not-empty?]]}))

(def mock-data 
  )

(defn init-data [original-data]
  #_(merge original-data
         mock-data)
  original-data)

(defrecord ConfirmationForm [validator])

(defmethod forms-core/get-data ConfirmationForm [this app-db form-props]
  (-> (get-in app-db [:kv :order-form])
      identity
      init-data)
  (get-in app-db [:kv :order-form])
  )

(defmethod forms-core/submit-data ConfirmationForm [_ app-db _ data]
  (let [form-data data]
    (pipeline! [value app-db]
      (wrap-validation
       (gql-req validate-order-overview-m (wrap-request (strip-local-data data))))
      (gql-req calculate-order-prices-m (wrap-request (strip-local-data data))))))

(defmethod forms-core/on-submit-error ConfirmationForm [this app-db form-props data error])

(defmethod forms-core/on-submit-success ConfirmationForm [this app-db form-props data]
  (let [res data
        form-data (:data (get-form-state app-db form-props))] 
    (pipeline! [value app-db]
      (pp/commit! (assoc-in app-db [:kv :order-pricing] (:calculateOrderPrice res)))
      (pp/commit! (assoc-in app-db [:kv :order-form] form-data))
      (pp/send-command! [forms-core/id-key :mount-form] [:payment :form]))))

(defn constructor []
  (->ConfirmationForm validator))
