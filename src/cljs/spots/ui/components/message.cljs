(ns spots.ui.components.message
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [clojure.string :as str]
            [reagent.core :as r]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [spots.ui.components.inputs :refer [controlled-input -button-rounded -button dropdown-input dropdown -button -button-rounded radiobtn]]
            [spots.ui.components.decorators :refer [-title cupcake-design cupcake-design-empty -dashed-divider -message-design-wrapper]]
            [spots.ui.components.modals :refer [form-modal]]
            [spots.domain.cupcake-design :refer [get-cupcake-idx]]
            [spots.util.gql-api :as ga]
            [oops.core :refer [ocall oget]]
            [spots.ui.components.svg-preview :as sp]
            [spots.util.helpers :refer [get-font-name get-color-hex]]))

(defn render-three-line-options [helpers form-state]
  [:div.col.col-12
   [:div.inline-block.mr3 [radiobtn {:label "Flat Text" :attr :curved :helpers helpers :form-state form-state :value false}]]
   [:div.inline-block [radiobtn {:label "Curved" :attr :curved :helpers helpers :form-state form-state :value true}]]
   [controlled-input {:form-state form-state :placeholder "First line" :helpers helpers :attr :firstLine}]
   [controlled-input {:form-state form-state :placeholder "Second line" :helpers helpers :attr :secondLine}]
   [controlled-input {:form-state form-state :placeholder "Third line" :helpers helpers :attr :thirdLine}]])

(defn render-single-line-options [helpers form-state]
  [:div.col.col-12
   [:div.inline-block.mr2 [radiobtn {:label "Small" :attr :textSize :helpers helpers :form-state form-state :value "small"}]]
   [:div.inline-block.mr2 [radiobtn {:label "Medium" :attr :textSize :helpers helpers :form-state form-state :value "medium"}]]
   [:div.inline-block [radiobtn {:label "Large" :attr :textSize :helpers helpers :form-state form-state :value "large"}]]
   [controlled-input {:form-state form-state :placeholder "Your text" :helpers helpers :attr :secondLine}]])

(defn render [ctx]
  (let [id (get-in (route> ctx) [:popup :id])
        form-props [:design-message id]
        form-state @(forms-helpers/form-state ctx form-props)
        helpers (forms-helpers/make-component-helpers ctx form-props)
        font (sub> ctx :font)
        colors (sub> ctx :colors)
        three-line? (forms-helpers/attr-get-in form-state :threeLine)
        first-line (forms-helpers/attr-get-in form-state :firstLine)
        second-line (forms-helpers/attr-get-in form-state :secondLine)
        third-line (forms-helpers/attr-get-in form-state :thirdLine)
        curved (forms-helpers/attr-get-in form-state :curved)
        text-size (forms-helpers/attr-get-in form-state :textSize)
        font-list (map (fn [f] [(:id f) (:name f)]) font)
        colors-list (map (fn [f] [(:id f) (:name f)]) colors) 
        font-id (forms-helpers/attr-get-in form-state :fontId)
        font-family (get-font-name font-id font)
        color-id (forms-helpers/attr-get-in form-state :colorId)
        color (get-color-hex color-id colors)
        {:keys [set-value submit]} helpers]
    [:form.flex.flex-column {:on-submit submit}
     [:div
      [:div.clearfix.flex.justify-between.items-center
       [:div.col.col-5
        [:h3.caps "Choose a style"]
        [:div.col.col-12
         [:div.inline-block.mr3 [radiobtn {:label "Three Line Text" :attr :threeLine :helpers helpers :form-state form-state :value true}]]
         [:div.inline-block [radiobtn {:label "Single Line Text" :attr :threeLine :helpers helpers :form-state form-state :value false}]]]
        (if three-line?
          (render-three-line-options helpers form-state)
          (render-single-line-options helpers form-state))
        [:div.clearfix.flex.justify-around
         [:div.col.col-5.inline-block.mr1
          [dropdown-input ctx form-state form-props {:attr :fontId :label "Font" :values font-list}]]
         [:div.col.col-7.inline-block
          [dropdown-input ctx form-state form-props {:attr :colorId :label "Color" :values colors-list}]]]]
       [:div.col.col-5.flex {:style {:flex-direction "row-reverse"}}
        [-message-design-wrapper
         [:div.circle.flex.items-center.justify-around
          [sp/render {:three-line? three-line?
                      :curved curved
                      :first-line first-line
                      :text-size text-size
                      :second-line second-line
                      :third-line third-line
                      :font-family font-family
                      :color color}]]]]]]
     [:footer.mt2.c-gray-l.center.z4.flex.justify-center
      [-button "Add Message"]]]))

(def component (ui/constructor {:renderer render
                                :topic forms-core/id-key
                                :subscription-deps [:form-state :font :colors]}))




