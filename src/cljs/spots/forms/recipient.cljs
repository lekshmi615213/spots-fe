(ns spots.forms.recipient
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.edb :as edb]
            [forms.validator :as v]
            [spots.forms.validators :as vs]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [spots.gql :refer [validate-order-information-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [cljsjs.moment]
            [clojure.string :as str]
            [spots.domain.cupcake-design :refer [strip-local-data]]
            [spots.domain.form-helpers :refer [format-phone]]
            [oops.core :refer [ocall oget]]
            [spots.util.helpers :refer [get-doorman-status]]))

(defn moment
  ([date-str] (moment date-str nil))
  ([date-str fmt]
   (js/moment date-str fmt)))

(defn address-part-not-empty? [value form-data path]
  (let [shipping-method (:shippingMethod form-data)]
    (if (not= "pickup" shipping-method)
      (vs/not-empty? value form-data path)
      true)))

(defn is-delivery? [value form-data path]
  (let [shipping-method (:shippingMethod form-data)]
    (if (= "manhattan_delivery" shipping-method)
      (vs/not-empty? value form-data path)
      true)))

(defn pickup-fields-not-empty? [value form-data path]
  (let [shipping-method (:shippingMethod form-data)]
    (if (= "pickup" shipping-method)
      (vs/not-empty? value form-data path)
      true)))

(defn phone-part-not-empty? [value form-data path]
  (let [shipping-method (:shippingMethod form-data)]
    (if (not= "pickup" shipping-method)
      (and (vs/not-empty? value form-data path) (vs/phone? value form-data path))
      true)))

(defn doorman-part-not-empty? [value form-data path]
  (let [shipping-method (:shippingMethod form-data)
        building_type (:buildingTypeId form-data)]
    (if (= "manhattan_delivery" shipping-method)
      (vs/not-empty? value form-data path)
      true)))


(def validator
  (v/validator {:shippingDate [[:not-empty vs/not-empty?]]
                :orderType [[:not-empty vs/not-empty?]]
                :deliveryWindow [[:not-empty is-delivery?]]
                :recipientName [[:not-empty address-part-not-empty?]]
                :recipientPhoneNumber [[:phone phone-part-not-empty?]]
                ;;:doorman [[:not-empty doorman-part-not-empty?]]
                :buildingTypeId [[:not-empty address-part-not-empty?]]
                :pickupTime [[:not-empty pickup-fields-not-empty?]]}))

(def date-format "YYYY-MM-DD HH:mm")

(defn format-date [date time format]
  (let [date-time (moment (str date " " time) "MM-DD-YYYY HH:mm")]
    (ocall date-time "format" format)))

(defn format-date-without-time [date format]
  (let [date-time (moment date "MM-DD-YYYY HH:mm")]
    (ocall date-time "format" format)))

(defn set-pickup-time [data date time]
  (let [time (subs time 0 (- (count time) 2))] 
    (-> data
        (assoc :pickupTime (format-date date time date-format))
        (dissoc :shippingDate)
        (dissoc :deliveryWindow)
        (dissoc :deliveryStartingAt)
        (dissoc :deliveryEndingAt))))

(defn set-delivery-times [data date time]
  (let [start-time (if (= "9:30AM - 12:30PM" time) "09:30" "12:30")
        end-time (if (= "9:30AM - 12:30PM" time) "12:30" "15:30")]
    (-> data
        (assoc :deliveryStartingAt (format-date date start-time date-format))
        (assoc :deliveryEndingAt (format-date date end-time date-format))
        (dissoc :shippingDate)
        (dissoc :deliveryWindow)
        (dissoc :pickupTime))))

(defn set-shipping-times [data date] 
  (-> data
      (assoc :deliveryStartingAt (format-date-without-time date date-format))
      (assoc :deliveryEndingAt (format-date-without-time date date-format))
      (dissoc :shippingDate)
      (dissoc :deliveryWindow)
      (dissoc :pickupTime)))

(defn prepare-shipping-method [data]
  (let [shipping-date (:shippingDate data)
        pickup-time (:pickupTime data)
        delivery-window (:deliveryWindow data)
        shipping-method (:shippingMethod data)]
    (case shipping-method
      "pickup" (set-pickup-time data shipping-date pickup-time)
      "shipping" (set-shipping-times data shipping-date)
      (set-delivery-times data shipping-date delivery-window))))

(defn gift-message-processor [app-db form-props form-state path value]
  (let [trimmed-value (or value "")]
    (assoc-in form-state (concat [:data] path)
              (subs trimmed-value 0 (min (count trimmed-value) 160)))))

(defrecord RecipientForm [validator])

(defmethod forms-core/get-data RecipientForm [this app-db form-props] 
  (-> (get-in app-db [:kv :order-form])
      (assoc :doorman false)))

(defmethod forms-core/format-attr-with RecipientForm [this path]
  (case path
    [:recipientPhoneNumber] format-phone
    nil))

(defmethod forms-core/process-attr-with RecipientForm [this path]
  (case path
    [:giftMessage] gift-message-processor 
    nil))



(defmethod forms-core/submit-data RecipientForm [_ app-db _ data]
  (let [] 
    (pipeline! [value app-db] 
      (wrap-validation (gql-req validate-order-information-m (wrap-request (-> data
                                                                               (strip-local-data)
                                                                               (prepare-shipping-method))))))))

(defmethod forms-core/on-submit-success RecipientForm [this app-db form-props data]
  (let [res (:validate data)
        data (:data (get-form-state app-db form-props))]
    (pipeline! [value app-db]
      (when (:valid res)
        (pipeline! [value app-db]
          (pp/commit! (assoc-in app-db [:kv :order-form] (prepare-shipping-method data)))
          (pp/send-command! [forms-core/id-key :mount-form] [:personalization :form])
          (pp/commit! (assoc-in app-db [:kv :form-progress :personalization] true))
          (pp/redirect! {:page "personalization"}))))))

(defn constructor []
  (->RecipientForm validator))
