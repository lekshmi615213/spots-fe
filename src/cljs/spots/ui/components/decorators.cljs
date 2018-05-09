(ns spots.ui.components.decorators
    (:require [keechma.toolbox.css.core :refer-macros [defelement]]
              [spots.stylesheets.colors :refer [colors-with-variations]]
              [garden.stylesheet :refer [at-media]]
              [keechma.toolbox.util :refer [class-names]]
              [spots.stylesheets.core :refer [breakpoints]]
              [spots.stylesheets.colors :refer [colors]]
              [spots.ui.components.svg-preview :as sp]))

(defelement -title
    :tag :h2
    :class [:c-light-gray :py1 :my2 :regular :uppercase :bd-light-gray]
    :style [{:border-bottom "3px solid"}])


(defelement -design-wrapper
    :tag :div
    :class [:overflow-hidden :circle :flex :justify-center :items-center]
    :style [{:border (str "5px dashed "(:light-gray-l colors-with-variations))
             :width "250px"
             :height "250px"} 
            [:div {:border (str "3px solid "(:light-gray-l colors-with-variations))
                   :width "100%"
                   :height "100%"}]])

(defelement -message-design-wrapper
    :tag :div
    :class [:overflow-hidden :circle :flex :justify-center :items-center]
    :style [{:border (str "5px dashed "(:light-gray-l colors-with-variations))
             :width "266px"
             :height "266px"} 
            [:div {:border (str "3px solid "(:light-gray-l colors-with-variations))
                   :width "100%"
                   :height "100%"}]])
             
(defn cupcake-message-design [{:keys [svg-params]}]
  [-message-design-wrapper
   [sp/render svg-params]])

(defn cupcake-design [{:keys [img-src]}]
    [-design-wrapper
     [:div {:style {:width "250px"
                    :height "250px"
                    :border 0
                    :background-position "center center"
                    :background-size "cover"
                    :background-repeat "no-repeat"
                    :background-image (str "url(" img-src ")")}}]])

(defn cupcake-design-empty []
    [-design-wrapper
        [:div.circle.flex.items-center.justify-around
            [:p.caps.c-light-gray "Preview"]]])

(defelement -dashed-divider
    :tag :div 
    :style [{:border-bottom (str "1px dashed " (:light-gray-l colors-with-variations))}])


(defelement -image-wrap
  :tag :div 
  :class [:p-0-5 :center :flex :col-sm :col-4 :items-center :justify-around]
  :style [[:div {:width "250px"}]
          [:div.svg-wrap {:width "250px"
                          :height "250px"}]
          [:div.img-wrap {:width "250px"
                          :height "250px"}]
          [:img {:border-radius "100%"}]])
    
(defn flavor-image-with-message [{:keys [label ammount svg-params]}]
  [-image-wrap
   [:div
    [:div.svg-wrap.flex.justify-center.items-center
     [sp/render svg-params]] 
    [:p.bold label]
    [:p.c-gray
     ammount
     [:br]
     "cupcakes"]]])  

      
(defn flavor-image [{:keys [image-src label ammount]}]
  [-image-wrap
   [:div
    [:div.img-wrap.flex.justify-center.items-center
     [:div.circle {:style {:width "250px"
                           :height "250px"
                           :background-position "center center"
                           :background-size "cover"
                           :background-image (str "url(" image-src ")")}}]]
    [:p.bold label]
    [:p.c-gray
     ammount
     [:br]
     "cupcakes"]]])        

(defelement -total
  :tag :div
  :class [:p1 :inline-block :bold]
  :style [{:border (str  "3px solid " (:light-gray colors-with-variations))
           :color (str (:gray colors-with-variations))}])
