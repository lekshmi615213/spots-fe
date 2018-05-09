(ns spots.ui.components.pickup-form
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.ui.components.decorators :refer [-title]]
            [spots.ui.components.inputs :refer [controlled-input radiobtn dropdown-input controlled-textarea]]
            [spots.ui.components.modals :refer [more-info]]
            [spots.ui.components.datepicker :as datepicker]
            [spots.util.helpers :refer [get-property-name]]
            [spots.domain.modal-content :as mc]
            [spots.domain.form-helpers :refer [is-sunday?]]
            [clojure.string :as str]))

(defelement -anchor-wrap
  :tag :a
  :class [:right :underline :inline-block]
  :style [{:font-size "1rem"}])

(def pickup-times
  ["9:30AM"
   "10:00AM"
   "10:30AM"
   "11:00AM"
   "11:30AM"
   "12:00PM"
   "12:30PM"
   "1:00PM"
   "1:30PM"
   "2:00PM"
   "2:30PM"
   "3:00PM"
   "3:30PM"
   "4:00PM"
   "4:30PM"
   "5:00PM"
   "5:30PM"])

(defn render [ctx {:keys [form-props helpers form-state]}]
  (let []
    [:div 
     [:div 
      [-title "Delivery method: PICKUP"
       [-anchor-wrap {:href (ui/url ctx {:page "order-info"})} "Change delivery method"]]]
     [:div
      [:p "Pickup location: "]
      [:a.c-light-gray {:href "https://goo.gl/maps/Wt8XjTM9AwN2"
                        :target "_blank"} "174 5TH AVENUE"]]
     [:div
      [-title "Select a pickup time: "
       [more-info {:modal-title "Pickup time"
                   :modal-content mc/pickup-content
                   :modal-footer "Close"}]]
      [:div
       [:div.sm-col.sm-col-8.pr1
        [datepicker/render ctx
         {:form-props form-props
          :form-state form-state  
          :helpers helpers
          :placeholder "Pickup date"
          :datepicker-params {:disableWeekends true}
          :attr :shippingDate}]]
       [:div.sm-col.sm-col-4
        [dropdown-input ctx form-state form-props {:attr :pickupTime 
                                                   :label "Pickup time" 
                                                   :values pickup-times}]]]]]))
