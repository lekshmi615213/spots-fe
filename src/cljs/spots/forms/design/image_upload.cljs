(ns spots.forms.design.image-upload
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [forms.validator :as v]
            [spots.forms.validators :as vs]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [spots.gql :refer [validate-order-personal-information-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [promesa.core :as p]
            [spots.domain.cupcake-design :refer [get-cupcake-idx form-data-path clear-designs clear-message clear-image-and-clipart set-design-type]]))

(def validator
  (v/validator {}))

(defrecord DesignImageUploadForm [validator])

(defmethod forms-core/get-data DesignImageUploadForm [this app-db form-props]
  )

(defmethod forms-core/submit-data DesignImageUploadForm [_ app-db _ data]
  )

(defmethod forms-core/on-submit-error DesignImageUploadForm [this app-db form-props data error]
  )

(defmethod forms-core/on-submit-success DesignImageUploadForm [this app-db form-props data]
  )

(defn constructor []
  (->DesignImageUploadForm validator))
