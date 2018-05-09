(ns spots.forms.user-info
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [forms.validator :as v]
            [spots.forms.validators :as vs]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [spots.gql :refer [validate-order-personal-information-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [spots.domain.cupcake-design :refer [strip-local-data]]
            [promesa.core :as p]
            [spots.domain.form-helpers :refer [format-phone]]
            [clojure.string :as str]))

(def validator
  (v/validator {:fullName    [[:not-empty vs/not-empty?]]
                :email       [[:not-empty vs/not-empty?]
                              [:email vs/email?]]
                :phoneNumber [[:not-empty vs/not-empty?]
                              [:phone vs/phone?]]}))

(defrecord UserInfoForm [validator])

(defmethod forms-core/format-attr-with UserInfoForm [this path]
  (case path
    [:phoneNumber] format-phone
    nil))

(defmethod forms-core/get-data UserInfoForm [this app-db form-props]
  (get-in app-db [:kv :order-form]))

(defmethod forms-core/submit-data UserInfoForm [_ app-db _ data]
  (let []
    (wrap-validation (gql-req validate-order-personal-information-m (wrap-request (strip-local-data data))))))

(defmethod forms-core/on-submit-error UserInfoForm [this app-db form-props data error]
  ;;(println "ERROR" error)
  )

(defmethod forms-core/on-submit-success UserInfoForm [this app-db form-props data]
  (let [res       (:validate data)
        form-data (:data (get-form-state app-db form-props))]
    (pipeline! [value app-db]
      (when (:valid res)
        (pipeline! [value app-db]
          (pp/commit! (assoc-in app-db [:kv :order-form] form-data))
          (pp/send-command! [forms-core/id-key :mount-form] [:order-info :form])
          (pp/commit! (assoc-in app-db [:kv :form-progress :order-info] true))
          (pp/redirect! {:page "order-info"}))))))

(defn constructor []
  (->UserInfoForm validator))
