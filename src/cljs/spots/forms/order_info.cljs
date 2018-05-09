(ns spots.forms.order-info
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [forms.validator :as v]
            [clojure.string :as str]
            [spots.forms.validators :as validators]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]))

(defn address-part-not-empty? [value form-data path]
  (let [shipping-method (:shippingMethod form-data)]
    (if (not= "pickup" shipping-method)
      (validators/not-empty? value form-data path)
      true)))

(def validator
  (v/validator {:shippingMethod [[:not-empty validators/not-empty?]] 
                :address.street [[:not-empty address-part-not-empty?]]
                :address.city [[:not-empty address-part-not-empty?]]
                :address.state [[:not-empty address-part-not-empty?]]
                :address.zipcode [[:not-empty address-part-not-empty?]]}))

(defrecord OrderInfoForm [validator])

(defmethod forms-core/get-data OrderInfoForm [this app-db form-props]
  (get-in app-db [:kv :order-form]))

(defmethod forms-core/process-attr-with OrderInfoForm [this path]
  (when (= [:shippingMethod] path)
    (fn [app-db form-props form-state path value]
      (let [data (:data form-state)
            new-data (-> data
                         (assoc-in path value)
                         (dissoc :address)
                         (dissoc :apartmentNumber))]
        (assoc form-state :data new-data)))))

(defmethod forms-core/submit-data OrderInfoForm [_ app-db _ data]
  (let []
    data))

(defmethod forms-core/on-submit-success OrderInfoForm [this app-db form-props data]
  (let [form-data (:data (get-form-state app-db form-props))] 
    (pipeline! [value app-db] 
      (pp/commit! (assoc-in app-db [:kv :order-form] data))
      (pp/send-command! [forms-core/id-key :mount-form] [:recipient :form])
      (pp/commit! (assoc-in app-db [:kv :form-progress :recipient] true))
      (pp/redirect! {:page "recipient"}))))

(defn constructor []
  (->OrderInfoForm validator))
