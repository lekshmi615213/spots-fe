(ns spots.ui.components.delivery-form
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.ui.components.decorators :refer [-title]]
            [spots.ui.components.inputs :refer [controlled-input radiobtn dropdown-input controlled-textarea]]
            [spots.ui.components.modals :refer [more-info]]
            [spots.ui.components.datepicker :as datepicker]
            [spots.util.helpers :refer [get-property-name get-doorman-status]]
            [spots.domain.modal-content :as mc]
            [spots.domain.form-helpers :refer [is-sunday?]]
            [clojure.string :as str]))

(defelement -anchor-wrap
  :tag :a
  :class [:right :underline :inline-block]
  :style [{:font-size "1rem"}])

(defn render [ctx {:keys [form-props helpers form-state order-form buildings]}]
  (let [building-list (map (fn [f] [(:id f) (:name f) (:askForDoorman f)]) buildings)
        building-id (forms-helpers/attr-get-in form-state :buildingTypeId)
        building-name (get-property-name building-id buildings)
        building-has-doorman? (get-doorman-status building-id buildings)]
    [:div
     [:div
      [:div 
       [-title "Recipient Address: "
        [-anchor-wrap {:href (ui/url ctx {:page "order-info"})} "Change delivery method"]]]
      [:div
       [:a {:href (ui/url ctx {:page "order-info"})} (str/join "," (vals (:address order-form)))]]
      [-title "Recipient name:"]
      [controlled-input {:form-state form-state :placeholder "First Name Last Name" :helpers helpers :attr :recipientName}]
      
      [-title "Recipient phone number:"]
      [controlled-input {:form-state form-state :placeholder "Recipient Phone Number" :helpers helpers :attr :recipientPhoneNumber}]
      [-title "Building type:"]
      [:div
       [dropdown-input ctx form-state form-props {:attr :buildingTypeId :label "Select" :values building-list}]]

      (when building-has-doorman?
        [:div
         [-title "Is there a doorman?:"]
         [:div.inline-block.mr3 [radiobtn {:label "Yes" :attr :doorman :helpers helpers :form-state form-state  :name "doorman" :value true}]]
         [:div.inline-block [radiobtn {:label "No" :attr :doorman :helpers helpers :form-state form-state  :name "doorman" :value false}]]])       
      
      [-title "Other delivery comments:"]
      [controlled-input {:form-state form-state :placeholder "Comment" :helpers helpers :attr :deliveryComment}]
      [:span "(Optional)"]
      [:div
       [-title "Select a delivery window (Please note we do not deliver on Sundays):"
        [more-info {:modal-title "Delivery date" :modal-content mc/delivery-content :modal-footer "Close"}]]
       [:div
        [:div.sm-col.sm-col-8.pr1
         [datepicker/render ctx
          {:form-props form-props
           :form-state form-state
           :helpers helpers
           :placeholder "Delivery Date"
           :datepicker-params {:disableDayFn #(is-sunday? %)}
           :attr :shippingDate}]]
        [:div.sm-col.sm-col-4.pr1
         [dropdown-input ctx form-state form-props {:attr :deliveryWindow 
                                                    :label "Delivery window" 
                                                    :values ["9:30AM - 12:30PM" "12:30PM - 3:30PM"]}]]]]]]))
