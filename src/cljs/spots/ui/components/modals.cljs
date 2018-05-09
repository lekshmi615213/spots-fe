(ns spots.ui.components.modals
    (:require [keechma.ui-component :as ui] 
              [keechma.toolbox.css.core :refer-macros [defelement]]
              [keechma.toolbox.ui :refer [sub> route>]]
              [spots.stylesheets.colors :refer [colors-with-variations]]
              [spots.ui.components.inputs :refer [-button]]
              [garden.stylesheet :refer [at-media]]
              [reagent.core :as r]
              [keechma.toolbox.util :refer [class-names]]
              [spots.stylesheets.core :refer [breakpoints]]
              [spots.stylesheets.colors :refer [colors]]
              [oops.core :refer [ocall oget]]))

(defelement -modal-wrapper
    :tag :div
    :class [:fixed]
    :style [{:top 0 
             :left 0 
             :bottom 0 
             :right 0 
             :background-color "rgba(0,0,0,0.5)"
             :z-index "10"
             :overflow "auto"}])

(defelement -modal-body
    :tag :div 
    :class [:my4 :mx-auto :p2 :bg-white :max-width-4 :flex :flex-column :justify-between]
    :style [{:box-shadow "0 7px 5px rgba(0, 0, 0, 0.5)"
             :min-height "450px"
             :text-transform "none"
             :font-weight "400"
             :color (str (:gray colors-with-variations))}
            [:h2 {:border-bottom (str "2px solid" (:light-gray colors-with-variations))}]])

(defn modal [{:keys [title content footer close-fn]}]
    (fn []
        [-modal-wrapper
            {:on-click (fn [e]
                         (when (= (.-target e) (.-currentTarget e)) 
                           (close-fn))
                         (when (not= "A" (oget e "currentTarget.nodeName"))
                           (ocall e "preventDefault")))}
         [-modal-body 
          (if-not (= "" title) [:h2.regular.c-light-gray.caps.pb1 title] nil)
          [:div content] 
          [:footer.mt2.c-gray-l.center.z4.flex.justify-center
           [-button {:on-click (fn [e]
                                 (close-fn))} footer]]]]))

(defn modal-with-action [{:keys [title content footer close-fn submit-fn]}]
  (fn []
    [-modal-wrapper
     {:on-click (fn [e]
                  (when (= (.-target e) (.-currentTarget e)) 
                    (close-fn))
                  
                  )}
     [-modal-body 
      (if-not (= "" title) [:h2.regular.c-light-gray.caps.pb1 title] nil)
      [:div content]]]))

(defelement -question-wrapper
  :tag :div
  :style [{:display "inline-block"
           :width "100%"
           :max-width "25px"
           :height "100%"
           :max-height "25px"
           :line-height "25px"
           :margin-left "10px"
           :background-color (str (:light-gray-l colors-with-variations))
           :border-radius "100%"
           :opacity "1"}
          [:&:hover{:background-color (str (:white-d colors-with-variations))}]])
          
(defn more-info [{:keys [modal-title modal-content modal-footer]}]
  (let [open-tooltip-atom? (r/atom false)]
    (fn []
      [-question-wrapper
        [:img.cursor-pointer {:src "img/question-circle.svg"
                              :on-click #(swap! open-tooltip-atom? not)}]
        (when @open-tooltip-atom?
          [modal {:title modal-title :content modal-content :footer modal-footer :close-fn #(reset! open-tooltip-atom? false)}])])))
  
(defelement -modal-label
    :tag :p
    :style [{:text-transform "none"
             :color (str (:gray colors-with-variations))
             :font-weight "400"
             :font-size "14px"}])

(defn caption-info [{:keys [label modal-title modal-content modal-footer]}]
  (let [open-tooltip-atom? (r/atom false)]
    (fn []
        [:div.inline-block
            [:div.cursor-pointer.underline.ml3 {:on-click #(swap! open-tooltip-atom? not)} [-modal-label label]]
            (when @open-tooltip-atom?
                [modal {:title modal-title :content modal-content :footer modal-footer :close-fn #(reset! open-tooltip-atom? false)}])])))

(defn link-info [{:keys [label modal-title modal-content modal-footer]}]
  (let [open-tooltip-atom? (r/atom false)]
    (fn []
        [:div.inline-block
            [:div.cursor-pointer {:on-click #(swap! open-tooltip-atom? not)}  label]
            (when @open-tooltip-atom?
                [modal {:title modal-title :content modal-content :footer modal-footer :close-fn #(reset! open-tooltip-atom? false)}])])))


(defn form-modal [ctx {:keys [modal-title modal-content modal-footer submit-fn close-fn]}]
  (let [current-url (route> ctx)] 
    [:div.inline-block 
     (when (:popup current-url)
       [modal-with-action
        {:title     modal-title
         :content   modal-content
         :footer    modal-footer
         :submit-fn submit-fn
         :close-fn  (or close-fn #(ui/redirect ctx (dissoc current-url :popup)))}])]))
        
