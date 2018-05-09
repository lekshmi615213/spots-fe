(ns spots.ui.forms.user-info
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.ui :refer [sub>]]
            [clojure.string :as str]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.ui.components.inputs :refer [controlled-input -input-group -label -button -button-rounded radiobtn cupcake-ammount]]
            [spots.ui.components.modals :refer [more-info]]
            [spots.ui.components.decorators :refer [-title]]
            [spots.domain.form-helpers :refer [submit-button]]
            ))

(defelement -typography-pack
  :tag :p
  :class [:h2 :center :c-cyan :uppercase :m0]
  :style [{:font-weight "900"}])

(defelement -wrap-pack
  :tag :div
  :class [:flex :flex-column :items-center :justify-center :mx2])

(def intro-text
  [:div
   [:p.h1.center.c-cyan.uppercase "Hey, Cupcake! We're so excited to get you SPOTTED!"]
   [:p.center.h3 "We have a 48-hour turnaround time for local orders (Manhattan) and a 72-hour turn around time for shipped orders (National). 
Depending on availability, we can rush your order (+$15)."]
   [:div.flex.flex-row.justify-center.my2
    [-wrap-pack
     [-typography-pack "12 Packs"]
     [-typography-pack "$25/Each"]
     [:p.center.h5 "1 dozen minimum"]]
    [-wrap-pack
     [-typography-pack "4 Packs"]
     [-typography-pack "$10/Each"]
     [:p.center.h5 "Minimum of 6 packs per order"]]]
   [:p.center.h3 
    "Price includes 4 different photos, logos or messages. Additional customization is an extra $1/image."
    [:br] 
    "All cupcakes are branded with an edible photo, logo or message."]
   [:p.center.mb0 "Local Hand Delivery: $16"]
   [:p.center.mt0-5 "National Shipping: $18"]
   [:p.center.h5 "*price may vary depending on quantity"]])

(defn render [ctx]
  (let [form-props [:user-info :form]
        form-state @(forms-helpers/form-state ctx form-props) 
        helpers (forms-helpers/make-component-helpers ctx form-props)
        {:keys [submit]} helpers
        ss-errors (get-in form-state [:state :cause])]
    [:form {:on-submit submit}
     [:div
      intro-text
      [-input-group
       [-label "Full Name"]
       [controlled-input {:form-state form-state :placeholder "First Name Last Name" :helpers helpers :attr :fullName}]]
      [-input-group
       [-label "Your Email Address"]
       [controlled-input {:form-state form-state :placeholder "Your Email Address" :helpers helpers :attr :email}]]
      [-input-group
       [-label "Your Phone Number"]
       [controlled-input {:form-state form-state :placeholder "Your Phone Number" :helpers helpers :attr :phoneNumber}]]
      [:div.flex.justify-center [submit-button form-state "Next Step"]]]]))


(def component (ui/constructor {:renderer render
                                :subscription-deps [:form-state]
                                :topic forms-core/id-key}))


