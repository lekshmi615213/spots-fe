(ns spots.ui.forms.confirmation
  (:require [keechma.ui-component :as ui]
            [clojure.string :as str]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.ui.components.inputs :refer [controlled-input -input-group -label -button controlled-textarea radiobtn dropdown-input]]
            [spots.ui.components.decorators :refer [-title flavor-image flavor-image-with-message -dashed-divider -total]]
            [spots.ui.components.address-autocomplete :as aa]
            [reagent.core :as r]
            [spots.util.gql-api :as ga]
            [spots.domain.stripe :refer [stripe-elements]]
            [oops.core :refer [ocall oget]]
            [spots.util.helpers :refer [get-message-params generate-resource-link]]
            [spots.domain.form-helpers :refer [submit-button]]))

(def blank-img "/img/blank-img.jpg")

(defn get-clipart-url [ctx id]
  (let [clipart (sub> ctx :clipart)
        url (:imageURL (first (filter #(= id (:id %)) clipart)))]
    url))


(defn get-design-img-src [ctx design]
  (cond
    (:encodedImage design) (:encodedImage design)
    (:clipartId design) (generate-resource-link (get-clipart-url ctx (:clipartId design)))
    :else blank-img))

(defn get-flavor [ctx id]
 (let [ct (sub> ctx :cupcake-types)
       flavor (first (filter (fn [c] 
                              (= (str id) (str (:id c)))) ct))] 
   flavor))


(defn render [ctx]
  (let [form-props [:confirmation :form]
        form-state @(forms-helpers/form-state ctx form-props)
        helpers (forms-helpers/make-component-helpers ctx form-props)
        cupcakes (or (forms-helpers/attr-get-in form-state :cupcakes) []) 
        cupcake-flavors (or (forms-helpers/attr-get-in form-state :flavorIds) [])
        {:keys [submit]} helpers]
    ;;Screen 27 and top half of 28
    [:div
     [:form {:on-submit submit} 
      [:h2.center "Please check your designs and confirm this is what you want."]
      ;;[:p "Reminder, if you need an exact delivery date, please contact us first."]
      [:div.flex.flex-wrap
       (when cupcakes
         (doall (map-indexed (fn [idx design]
                               (if (= "message" (:type design))
                                 [flavor-image-with-message (into {:key idx
                                                                   :label (str "Design " (inc idx))
                                                                   :ammount (:quantity design)} {:svg-params (get-message-params (sub> ctx :font) (sub> ctx :colors) design)})]
                                 [flavor-image (into {:key idx} {:image-src (get-design-img-src ctx design)
                                                                 :label (str "Design " (inc idx))
                                                                 :ammount (:quantity design)})])) cupcakes)))]
      
      (when cupcake-flavors
        [:div 
         [-title "Flavors:"]
         (doall (map-indexed (fn [idx flav-id]
                               (let [flavor (get-flavor ctx (key flav-id))]
                                 [:div.mb1 {:key idx}
                                  [:div.flex.items-center
                                   [:img.inline-block.circle.mr1 {:width 40
                                                                  :height 40
                                                                  :src (generate-resource-link (:imageURL flavor))}]
                                   [:p.inline-block.mr3 (str (:name flavor))]]
                                  [-dashed-divider]])) cupcake-flavors))])

      [-title "Additional order notes:"]    
      [controlled-input {:form-state form-state :placeholder "" :helpers helpers :attr :additionalNotes}]
      [:span "(Optional)"]

      [-title "Promo code:"]    
      [controlled-input {:form-state form-state :placeholder "" :helpers helpers :attr :promoCode}]
      [:span "(Optional)"]

      [-title "Can we post the images of your cupcakes on our social media?:"]
      [:div.inline-block.mr3 [radiobtn {:label "Yes" :attr :shareableImages :helpers helpers :form-state form-state  :name "shareableImages" :value true}]]
      [:div.inline-block [radiobtn {:label "No" :attr :shareableImages :helpers helpers :form-state form-state  :name "shareableImages" :value false}]]

      [:div.flex.justify-center.mt2 [submit-button form-state "Checkout"]]]
     [(ui/component ctx :forms-payment)]]))


(def component (ui/constructor {:renderer render
                                :component-deps [:forms-payment]
                                :subscription-deps [:form-state :cupcake-types :clipart-url :clipart :font :colors]
                                :topic forms-core/id-key}))
