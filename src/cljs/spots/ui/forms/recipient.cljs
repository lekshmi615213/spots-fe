(ns spots.ui.forms.recipient
 (:require [keechma.ui-component :as ui]
           [keechma.toolbox.ui :refer [sub>]]
           [keechma.toolbox.forms.core :as forms-core]
           [keechma.toolbox.css.core :refer-macros [defelement]]
           [reagent.core :as r]
           [keechma.toolbox.forms.helpers :as forms-helpers]
           [spots.ui.components.inputs :refer [controlled-input -input-group -label -button -button-rounded radiobtn cupcake-ammount dropdown-input controlled-textarea]]
           [clojure.string :as str]
           [spots.ui.components.decorators :refer [-title]]
           [spots.ui.components.modals :refer [more-info caption-info link-info]]
           [spots.ui.components.address-autocomplete :as aa]
           [spots.ui.components.datepicker :as dp]
           [spots.util.helpers :refer [get-property-name]]
           [spots.domain.form-helpers :refer [submit-button]]
           [spots.domain.modal-content :as mc]
           [spots.ui.components.delivery-form :as delivery-form]
           [spots.ui.components.shipping-form :as shipping-form]
           [spots.ui.components.pickup-form :as pickup-form]))


(defelement -anchor-wrap
  :tag :a
  :class [:right :underline :inline-block]
  :style [{:font-size "1rem"}])

(defn render [ctx]
  (let [form-props [:recipient :form]
        form-state @(forms-helpers/form-state ctx form-props)
        order-form (sub> ctx :order-form)
        helpers    (forms-helpers/make-component-helpers ctx form-props)
        industries (sub> ctx :industries)
        buildings (sub> ctx :buildings)
        industry-list (map (fn [f] [(:id f) (:name f)]) industries)
        industry-id (forms-helpers/attr-get-in form-state :industryId)
        industry-name (get-property-name industry-id industries)
        gift-note (forms-helpers/attr-get-in form-state :giftMessage)
        {:keys [submit]} helpers]
    [:form {:on-submit submit}
     [:div
      (when-let [sm (:shippingMethod order-form)]
        (case sm
          "manhattan_delivery" [delivery-form/render ctx {:form-props form-props
                                              :form-state form-state
                                              :helpers helpers
                                              :order-form order-form
                                              :buildings buildings}]
          "shipping" [shipping-form/render ctx {:form-props form-props
                                                :form-state form-state
                                                :helpers helpers
                                                :order-form order-form
                                                :buildings buildings}]
          "pickup" [pickup-form/render ctx {:form-props form-props
                                            :form-state form-state
                                            :helpers helpers}]
          nil))                   
         [-title "Please select what type of order this is:"]                                                                                                   
         
      [:div.inline-block.mr3 [radiobtn {:label "Personal" :attr :orderType :helpers helpers :form-state form-state :name "orderType" :value "personal"}]]   
      [:div.inline-block [radiobtn {:label "Corporate" :attr :orderType :helpers helpers :form-state form-state :name "orderType" :value "corporate"}]]                                                                                            
         
         (when (= (forms-helpers/attr-get-in form-state :orderType) "corporate")
           [:div
            [-title "Company name:"]                                                                                                           
            [controlled-input {:form-state form-state :placeholder "Enter here" :helpers helpers :attr :company}]
            ;; [-title "Industry:"]
            ;; [dropdown-input ctx form-state form-props {:attr :industryId :label "Select" :values industry-list}]
            ;; [:span "(Optional)"]
            ])
      
         #_(when (= (forms-helpers/attr-get-in form-state :orderType) "personal"))
      [:div 
       [-title "Add a gift note to your order:"]                                                                                                               
       [controlled-textarea {:form-state form-state :placeholder "" :helpers helpers :attr :giftMessage}]                                                      
       [:span "+$1 (Optional) - " (count (or gift-note "")) " / 160"]]                                                                                                                                
         [:div.flex.justify-center [submit-button form-state "Next Step"]]]]))

(def component (ui/constructor {:renderer          render
                                :subscription-deps [:form-state :order-form :buildings :industries]
                                :topic             forms-core/id-key}))
