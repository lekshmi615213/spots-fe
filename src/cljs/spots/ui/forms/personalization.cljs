(ns spots.ui.forms.personalization
  (:require [cljs-uuid-utils.core :as uuid]
            [clojure.string :as str]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [keechma.ui-component :as ui]
            [oops.core :refer [ocall oget]]
            [reagent.core :as r]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [spots.ui.components.address-autocomplete :as aa]
            [spots.ui.components.decorators :refer [-title cupcake-design cupcake-design-empty cupcake-message-design -dashed-divider]]
            [spots.ui.components.design-modals :as dm]
            [spots.ui.components.inputs :refer [controlled-input -input-group -label -button -button-warning -button-rounded radiobtn cupcake-ammount dropdown-input controlled-textarea image-checkbox]]
            [spots.ui.components.modals :refer [more-info caption-info link-info form-modal]]
            [spots.util.gql-api :as ga]
            [spots.util.helpers :refer [get-message-params generate-resource-link]]
            [spots.domain.form-helpers :refer [submit-button]]
            [spots.domain.modal-content :as mc]))

(def blank-img "/img/blank-img.jpg")

(def cupcake-size "/img/cupcake-size.jpg")

(def cupcake-total
  [:div
   [:p "For adults we recommend 3-4 cupcakes per person."
    [:br]
    "For children we recommend 2-3 cupcakes per person."] 
   [:div.center [:img {:src cupcake-size}]]])

(defn get-design-img-src [ctx design]
  (cond
    (:encodedImage design) (:encodedImage design)
    (:clipartId design) (generate-resource-link (sub> ctx :clipart-url (:clipartId design)))
    :else blank-img))

(defn add-cupcake [cupcakes cupcakes-diff]
  (conj cupcakes {:quantity cupcakes-diff
                  :type nil
                  :id (uuid/make-random-uuid)}))

(defn cupcakes-total [cupcakes]
  (reduce (fn [acc v]
            (+ acc (or (:quantity v) 0))) 0 cupcakes))

(defn remove-by-index [coll remove-idx]
  (loop [items coll
         cleared-items []
         idx 0] 
    (if (not (seq items))
      cleared-items
      (let [next (first items)]
        (if (not= idx remove-idx)
          (recur (rest items) (conj cleared-items next) (inc idx))
          (recur (rest items) cleared-items (inc idx)))))))

(defn render [ctx]
  (let [form-props [:personalization :form]
        form-state @(forms-helpers/form-state ctx form-props)
        helpers (forms-helpers/make-component-helpers ctx form-props)
        ccb (forms-helpers/attr-get-in form-state :countCupcakesBy)
        cc (forms-helpers/attr-get-in form-state :cupcakeCount)
        cupcakes (or (forms-helpers/attr-get-in form-state :cupcakes) [])
        flavors (forms-helpers/attr-get-in form-state :flavorIds)
        cupcake-types (sub> ctx :cupcake-types)
        cupcake-sum (if (= "the_dozen" ccb) (* 12 cc) (* 4 cc))
        pack-sum (when (and (>= cc 6) (= "packs_of_four" ccb)) true)
        customization-wording (if (= "the_dozen" ccb) "Design" "Cupcake")
        current-url (route> ctx)
        {:keys [submit set-value]} helpers]
    [:div
     [(ui/component ctx :design-modals) {:helpers helpers :form-props form-props :form-state form-state}]
     [:form {:on-submit submit}
      [-title "Cupcake Total:"
       [more-info {:modal-title "Recommended order size" :modal-content cupcake-total :modal-footer "Close"}]]
      [:div.flex.items-center.flex-row
       [:div.inline-block.mr3
        [:div.flex.items-center
         [radiobtn {:label [:span "By the Dozen" [:span.c-gray.ml1 "$25/Dozen"]] 
                    :attr :countCupcakesBy 
                    :helpers helpers 
                    :form-state form-state 
                    :name "orderType" 
                    :value "the_dozen"}]
         [:div.mb1-5
          [more-info {:modal-title "Dozen Cupcake Designs"
                      :modal-content mc/by-the-dozen
                      :modal-footer "Close"}]]]]
       
       [:div.inline-block
        [:div.flex.items-center
         [radiobtn {:label [:span "Packs of 4 (Minimum of 6 packs)" [:span.c-gray.ml1 "$10/Pack"]] 
                    :attr :countCupcakesBy 
                    :helpers helpers 
                    :form-state form-state 
                    :name "orderType" 
                    :value "packs_of_four"}]
         [:div.mb1-5
          [more-info {:modal-title "4-Pack Designs"
                      :modal-content mc/four-packs
                      :modal-footer "Close"}]]]]]
      
      (when (= (forms-helpers/attr-get-in form-state :countCupcakesBy) "the_dozen")
        [:div [cupcake-ammount {:label "Dozen" :attr :cupcakeCount :placeholder "Amount" :helpers helpers :form-state form-state}]
         [:div.inline.ml2.bold.h2  (str cupcake-sum " Cupcakes")]])
      
      (when (= (forms-helpers/attr-get-in form-state :countCupcakesBy) "packs_of_four")
        [:div [cupcake-ammount {:label "Packs of 4" :attr :cupcakeCount :placeholder "Amount" :helpers helpers :form-state form-state}]
         [:div.inline.ml2.bold.h2 (str cupcake-sum " Cupcakes")]])
      
      (when (= (forms-helpers/attr-get-in form-state :countCupcakesBy) "packs_of_four")
        [:div 
         [-title "Wraparound Label Message:"]                                                                                                               
         [controlled-input {:form-state form-state :placeholder "Message" :helpers helpers :attr :wrapAroundMessage}]
         [:span "+$1/Pack (Optional)"]])
      (when cupcake-types
        [:div.clearfix.mb2
         [-title "Click your flavor selections:"]
         [:div.clearfix
          (doall (map (fn [ct] 
                        [image-checkbox {:key (:id ct) 
                                         :label (:name ct) 
                                         :image-src (generate-resource-link (:imageURL ct)) 
                                         :attr [:flavorIds (:id ct)] 
                                         :name "flavorId" 
                                         :details-url "#"
                                         :form-state form-state
                                         :helpers helpers}]) cupcake-types))]])
      (when (and (< 0 cupcake-sum) (some true? (vals flavors)))
        [:div
         [-title "Add cupcake designs"]
         (when cupcakes
           (doall (map-indexed (fn [idx design]
                                 (let [attr-name (keyword (str "design-" idx))
                                       id (:id design)]
                                   [:div {:key id}  
                                    [:h3.caps (str customization-wording " " (inc idx))]
                                    [:p "Customize your Cupcakes"]
                                    [:div.clearfix.flex
                                     [:div.col.col-7q
                                      [-button-rounded {:on-click (fn [e]
                                                                    (ui/redirect ctx (assoc current-url :popup {:type  "upload" :id id}))
                                                                    (ocall e "preventDefault"))} "Upload My Art"] 
                                      [-button-rounded {:on-click (fn [e]
                                                                    (ui/redirect ctx (assoc current-url :popup {:type  "clipart" :id id}))
                                                                    (ocall e "preventDefault"))} "Choose Clip-Art"] 
                                      [-button-rounded {:on-click (fn [e]
                                                                    (ui/redirect ctx (assoc current-url :popup {:type "message" :id id}))
                                                                    (ocall e "preventDefault"))} "Write Message"]
                                      (when (= "the_dozen" ccb)
                                        [:div
                                         [:p.caps.mt4  "Quantity of cupcakes" [:br] "with design " (inc idx) ": " ]
                                         [:div.flex.flex-row.items-start
                                          [:div.inline-block.col-2
                                           [controlled-input {:form-state form-state :placeholder "Qty" :helpers helpers :attr [:cupcakes idx :quantity]}]]
                                          [:div.inline-block.col-2.ml1
                                           [-button-warning {:on-click (fn [e]
                                                                         (set-value :cupcakes (remove-by-index cupcakes idx))
                                                                         (.preventDefault e))} "Delete"]]]])]
                                     [:div.col.col-3
                                      (if (= "message" (:type design))
                                        [cupcake-message-design {:svg-params (get-message-params (sub> ctx :font) (sub> ctx :colors) design)}]
                                        [cupcake-design {:img-src (get-design-img-src ctx design)}])]]])) cupcakes)))
         [:div.flex.justify-center.py2
          (let [cupcakes-added (cupcakes-total cupcakes)
                cupcakes-available? (< cupcakes-added cupcake-sum)]
            (if cupcakes-available?
              [-button {:on-click (fn [e]
                                    (set-value :cupcakes (add-cupcake cupcakes (- cupcake-sum cupcakes-added)))
                                    (ocall e "preventDefault"))}
               "Add Design"]
              (when (= "the_dozen" ccb)
                [:div.c-red.h3 "If you want to add additional customization, please adjust the cupcake quantities."])))]
         

         [-dashed-divider]
         [:div.flex.justify-center.mt2 [submit-button form-state "Next Step"]]])]]))


(def component (ui/constructor {:renderer render
                                :component-deps [:design-modals]
                                :subscription-deps [:form-state :cupcake-types :image-upload-preview :clipart-url :font :colors]
                                :topic forms-core/id-key}))
