(ns spots.ui.components.clipart
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [clojure.string :as str]
            [reagent.core :as r]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [spots.ui.components.inputs :refer [controlled-input -button-rounded -button dropdown-input dropdown -button -button-rounded radiobtn]]
            [spots.ui.components.decorators :refer [-title cupcake-design cupcake-design-empty -dashed-divider]]
            [spots.ui.components.modals :refer [form-modal]]
            [spots.util.gql-api :as ga]
            [spots.util.helpers :refer [generate-resource-link]]
            [oops.core :refer [ocall oget]]))

(defelement -clipart-wrapper
  :tag :div
  :class [:flex :items-center :justify-around :items-center :center :sm-col :sm-col-6 :md-col-3 :mx2 :overflow-hidden]
  :style [{:height "250px"}])

(defn render [ctx]
  (let [clipart (sub> ctx :clipart)
        clipart-filtered (sub> ctx :clipart-filtered)
        current-route (route> ctx)
        filter (get-in current-route [:popup :filter])
        categories (into ["All"] (distinct (map :category clipart)))]
    
    [:div.flex.flex-column.justify-center
     [:div
      [:div.flex.items-baseline
       
       [:div.col.col-5.mr3 [dropdown {:label "Choose a category" 
                                      :values categories 
                                      :selected-value filter 
                                      :on-select #(ui/redirect ctx (assoc-in current-route [:popup :filter] %))}]]]
      [:div.clearfix
       (doall (map (fn [c]
                     [-clipart-wrapper {:key (:id c)
                                        :on-click (fn [e]
                                                    (<cmd ctx :set (:id c))
                                                    (ocall e "preventDefault"))}
                      [:img {:src (generate-resource-link (:imageURL c))}]]) clipart-filtered))]]
     [:footer.mt2.c-gray-l.center.z4.flex.justify-center
      [-button {:on-click (fn [e]
                            (ui/redirect ctx (dissoc current-route :popup))
                            (ocall e "preventDefault"))} "Close"]]]))

(def component (ui/constructor {:renderer render
                                :topic :clipart
                                :subscription-deps [:clipart :clipart-filtered]}))

