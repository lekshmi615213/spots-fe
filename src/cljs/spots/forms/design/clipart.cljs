(ns spots.forms.design.clipart
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [forms.validator :as v]
            [spots.forms.validators :as vs]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [spots.gql :refer [validate-order-personal-information-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [promesa.core :as p]))

(def validator
  (v/validator {}))

(defrecord DesignClipartForm [validator])

(defmethod forms-core/get-data DesignClipartForm [this app-db form-props]
  )



(defmethod forms-core/submit-data DesignClipartForm [_ app-db _ data]
  )

(defmethod forms-core/on-submit-error DesignClipartForm [this app-db form-props data error]
  )

(defmethod forms-core/on-submit-success DesignClipartForm [this app-db form-props data]
  )

(defn constructor []
  (->DesignClipartForm validator))
