(ns spots.ui.forms.order-info
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [keechma.toolbox.forms.core :as forms-core]
            [reagent.core :as r]
            [clojure.string :as str]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.ui.components.inputs :refer [controlled-input -input-group -label -button -button-rounded radiobtn cupcake-ammount dropdown-input controlled-textarea]]
            [keechma.toolbox.util :refer [class-names]]
            [spots.ui.components.decorators :refer [-title]]
            [spots.ui.components.modals :refer [more-info caption-info link-info]]
            [spots.ui.components.address-autocomplete :as aa]
            [spots.domain.form-helpers :refer [submit-button]]
            [spots.domain.modal-content :as mc]
            [oops.core :refer [ocall]]))

(defn render-aa [ctx form-props form-state helpers] 
  [:div
   [:div.clearfix
    [:div.sm-col.sm-col-8.pr1
     [aa/render ctx form-props                                                                                     
      {:form-state form-state :placeholder "Street Address" :helpers helpers :attr [:address :street]}]]
    [:div.sm-col.sm-col-4
     [controlled-input {:form-state form-state :placeholder "Apartment / Suite" :helpers helpers :attr :apartmentNumber}]]]
   [:div.clearfix
    [:div.sm-col.sm-col-8.pr1
     [controlled-input {:form-state form-state :placeholder "City" :helpers helpers :attr [:address :city]}]]
    [:div.sm-col.sm-col-1
     [controlled-input {:form-state form-state :placeholder "State" :helpers helpers :attr [:address :state]}]]
    [:div.sm-col.sm-col-3.pl1
     [controlled-input {:form-state form-state :placeholder "Zip Code" :helpers helpers :attr [:address :zipcode]}]]] ])

(defn render [ctx]
  (let [form-props [:order-info :form]
        form-state @(forms-helpers/form-state ctx form-props)
        helpers (forms-helpers/make-component-helpers ctx form-props)
        address (forms-helpers/attr-get-in form-state :address)
        address-done? (get-in form-state [:data :internal :address-done?])
        address-valid? (every? nil? [(forms-helpers/attr-errors form-state :address.city)
                                     (forms-helpers/attr-errors form-state :address.street)
                                     (forms-helpers/attr-errors form-state :address.state)
                                     (forms-helpers/attr-errors form-state :address.zipcode)])
        {:keys [submit set-value]} helpers]
    [:div
     [:form {:on-submit submit}   
      [:div 
       [:div.inline-block
        [radiobtn {:label "Manhattan Delivery" 
                   :attr :shippingMethod 
                   :helpers helpers 
                   :form-state form-state
                   :name "shippingMethod"
                   :value "manhattan_delivery"}]]
       [caption-info {:label "Delivery Policy + Rates" 
                      :modal-title "Delivery Policy + Rates" 
                      :modal-content mc/delivery-content 
                      :modal-footer "Close"}]]
      (when (= (forms-helpers/attr-get-in form-state :shippingMethod) "manhattan_delivery")
        (render-aa ctx form-props form-state helpers))
       
      [:div 
       [:div.inline-block
        [radiobtn {:label "Shipping (Outside of Manhattan)"
                   :attr :shippingMethod
                   :helpers helpers
                   :form-state form-state
                   :name "shippingMethod"
                   :value "shipping"}]]
       [caption-info {:label "Shipping Rates"
                      :modal-title "Shipping Rates"
                      :modal-content mc/shipping-content
                      :modal-footer "Close"}]]    
      (when (= (forms-helpers/attr-get-in form-state :shippingMethod) "shipping")
        (render-aa ctx form-props form-state helpers))
      [:div
       [:div.inline-block
        [radiobtn {:label mc/pickup
                   :attr :shippingMethod
                   :helpers helpers
                   :form-state form-state
                   :name "shippingMethod"
                   :value "pickup"}]]
       [caption-info {:label "Pickup Policy"
                      :modal-title "Pickup Policy"
                      :modal-content mc/pickup-content
                      :modal-footer "Close"}]] 
       
      [:div.inline-block
       [radiobtn {:label mc/multiple-recipients
                  :attr :shippingMethod
                  :helpers helpers
                  :form-state form-state
                  :name "shippingMethod"
                  :value "modalTrigger"}]]
      (when (= (forms-helpers/attr-get-in form-state :shippingMethod) "modalTrigger")
        [caption-info {:label "Multiple recipients policy"
                       :modal-title "Multiple recipients" 
                       :modal-content mc/multiple-content 
                       :modal-footer "Close"}])
      [:div.flex.justify-center [submit-button form-state "Next Step"]]]]))

(def component (ui/constructor {:renderer render
                                :subscription-deps [:form-state]
                                :topic forms-core/id-key}))
