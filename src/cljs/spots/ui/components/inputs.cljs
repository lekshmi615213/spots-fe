(ns spots.ui.components.inputs
  (:require [keechma.toolbox.css.core :refer-macros [defelement]]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.forms.validators :as validators]
            [reagent.core :as r]
            [garden.stylesheet :refer [at-media]]
            [keechma.toolbox.ui :refer [<cmd]]
            [keechma.toolbox.util :refer [class-names]]
            [spots.stylesheets.core :refer [breakpoints]]
            [spots.stylesheets.colors :refer [colors]]
            [oops.core :refer [oget ocall]]
            [forms.util :refer [key-to-path]]))

(defelement -input-group
  :class [:mt2])

(defelement -label
  :class [:mb1])

(defelement -input
  :tag :input
  :class [:p1 :mb1 :spots-text :inline-block :w-100 :a-text :c-gray]
  :style [{:border (str "3px solid " (:light-gray colors-with-variations))
           :outline "none"}
          [:&:focus {:border (str "3px solid " (:cyan colors-with-variations))}]])

(defelement -form-input-title
  :tag :label
  :class [:spots-h3 :c-red-gray :block :mb1 :mt2])

(defelement -form-inline-label
  :tag :label
  :class [:spots-small :c-red-gray :ml0-5 :mt0 :mb0])

(defelement -form-add-new-label
  :tag :a
  :class [:c-red :spots-small :text-decoration-none])

(defelement -textarea
  :tag :textarea
  :class [:p1 :mb1 :spots-text :block :w-100]
  :style [{:border (str "3px solid " (:light-gray colors-with-variations))
           :outline "none"
           :max-width "100%"}
          [:&:focus {:border (str "3px solid " (:cyan colors-with-variations))}]])

(defelement -button
  :tag :button
  :class [:bg-cyan :bg-h-gray :a-text :btn :border-none :uppercase :c-white :px3 :justify-center :items-center :flex]
  :style [{:align-self "center"
           :font-weight "700" 
           :height "50px"}])

(defelement -button-warning
  :tag :button
  :class [:bg-red :bg-h-red :a-text :px1 :btn :border-none :uppercase :c-white]
  :style [{:font-weight "700" :height "58px"}])


(defelement -button-rounded
  :tag :button
  :class [:bg-cyan :bg-h-gray :a-text :mr1 :px2 :pt1 :pb2 :my1 :btn :border-none :uppercase :center :c-white :pill]
  :style [{:font-weight "700" :height "30px" :max-width "250px"}])

(defn render-errors
  ([attr-errors] (render-errors attr-errors []))
  ([attr-errors server-side-errors]
   (let [errors (get-in attr-errors [:$errors$ :failed])]
     (when (or errors server-side-errors) 
       (into [:div]
             (concat
              (doall (map (fn [e]
                            [:div.a-small.c-red (validators/get-validator-message e)])
                          errors))
              (doall (map (fn [e]
                            [:div.a-small.c-red e])
                          server-side-errors))))))))

(defn get-server-side-errors [form-state attr]
  (let [path (key-to-path attr)
        state (:state form-state)]
    (when (= :submit-failed (:type state))
      (let [errors (oget (:cause state) "data")]
        (get-in errors path)))))

(defn controlled-input [{:keys [form-state helpers placeholder label attr]}]
  (let [{:keys [on-change on-blur]} helpers]
    [:div.mb0-5
     [-input {:placeholder placeholder
              :on-change (on-change attr)
              :on-blur (on-blur attr)
              :value (forms-helpers/attr-get-in form-state attr)}]
     (render-errors (forms-helpers/attr-errors form-state attr)
                    (get-server-side-errors form-state attr))]))

(defn controlled-textarea [{:keys [form-state helpers placeholder label attr]}]
  (let [{:keys [on-change on-blur]} helpers]
    [:div.mb1-5
     [-textarea {:placeholder placeholder
                 :on-change (on-change attr)
                 :on-blur (on-blur attr)
                 :value (forms-helpers/attr-get-in form-state attr)}]
     (render-errors (forms-helpers/attr-errors form-state attr)
                    (get-server-side-errors form-state attr))]))


(defn controlled-password [{:keys [form-state helpers placeholder label attr]}]
  (let [{:keys [on-change on-blur]} helpers]
    [:div.mb1-5
     [-input {:placeholder placeholder
              :on-change (on-change attr)
              :on-blur (on-blur attr)
              :type "password"
              :value (forms-helpers/attr-get-in form-state attr)}]
     (render-errors (forms-helpers/attr-errors form-state attr)
                    (get-server-side-errors form-state attr))]))

(defelement -radio-wrap
  :tag :span
  :style [{:position "relative"}
          [:input {:display "none" 
                   :position "absolute"}
           [:&:checked ["~label" [:&:before {:background-color "#86D6F6" :width "10px" :height "10px" :left "-33px"}]]]
           [:&:checked ["~label" [:&:after {:border-color "#86D6F6"}]]]]
          [:label {:display "block" 
                   :position "relative" 
                   :cursor "pointer" 
                   :margin-left "40px"}
           [:&:after {:content "\"\"" 
                      :position "absolute" 
                      :left "-40px" 
                      :top "-3px"
                      :width "19px" 
                      :height "19px" 
                      :border-radius "16px" 
                      :transition "all ease-out .25s"
                      :border "3px solid #C7C7C7"}]
           [:&:before {:content "\"\"" 
                       :position "absolute" 
                       :left "-32px" 
                       :top "4px" 
                       :width "10px" 
                       :height "10px" 
                       :transition "all ease-out .25s"
                       :border-radius "100%"}]]])

(defn radiobtn [{:keys [label form-state attr helpers name value]}]
  (let [{:keys [on-change on-blur set-value]} helpers
        id (str (gensym "radio"))
        selected? (= value (forms-helpers/attr-get-in form-state attr))]
    
    [:div.mb1-5
     [-radio-wrap
      [:input {:type "radio"
               :value value
               :name name
               :checked selected?
               :on-change (fn [e]
                            (when (.. e -target -checked)
                              (set-value attr value)))
               :id id}]
      [:label.my1.c-light-gray.uppercase.bold {:for id}        
       label]]
     (render-errors (forms-helpers/attr-errors form-state attr))]))

(defelement -cupcake-wrap
  :tag :div
  :style [{}
          [:input {:width "100%" 
                   :height "40px"
                   :line-height "40px" 
                   :padding "0" 
                   :max-width "250px" 
                   :text-align "center"
                   :border-color (str (:cyan colors-with-variations))}]
          [:div {:height "40px" 
                 :width "100%" 
                 :max-width "250px" 
                 :top "0px" 
                 :line-height "40px" 
                 :vertical-align "top"}]])

(defn cupcake-ammount [{:keys [label form-state attr helpers placeholder]}]
  (let [{:keys [on-change on-blur]} helpers]
    
    [-cupcake-wrap
     [-input {:placeholder placeholder
              :on-change (on-change attr)
              :on-blur (on-blur attr)
              :value (forms-helpers/attr-get-in form-state attr)}]
     [:div.inline-block.bg-cyan.uppercase.c-white.bold.center
      label]
     (render-errors (forms-helpers/attr-errors form-state attr))]))


(defelement -dropdown-wrapper
  :tag :div
  :class [:spots-text :block :w-100 :a-text :c-white :cursor-pointer :mb2 :relative] 
  :style [{:outline "none"
           :clear "both"}])

(defelement -dropdown-button
  :tag :div
  :class [:p1 :relative]
  :style [{:position "relative"
           :background-color (str (:cyan colors-with-variations))
           :border (str "3px solid " (:cyan colors-with-variations))
           :z-index "9"}
          [:&:after {:content "\"\""
                     
                     :right "10px"
                     :top "17px"
                     :position "absolute"
                     :border-left "20px solid transparent"
                     :border-right "20px solid transparent"
                     :border-top (str "20px solid " (:white colors-with-variations))}]
          [:&:hover:after {:border-top (str "20px solid "(:white colors-with-variations))}]
          [:&.is-open:after {:top "-7px"
                             :border-top "20px solid transparent"
                             :border-left "20px solid transparent"
                             :border-right "20px solid transparent"
                             :border-bottom (str "20px solid " (:white colors-with-variations))}]])


(defelement -dropdown-content
  :tag :div
  :class [:absolute]
  :style [{:width "100%"
           :max-height "200px"
           :overflow "auto"
           :z-index 100
           :box-shadow "0 0 5px 0 rgba(0,0,0,0.1)"
           :border (str "3px solid " (:cyan colors-with-variations))}])  

(defn get-dropdown-label [values value]
  (let [current-value (first
                        (filter
                         (fn [v]
                           (if (string? v)
                             (= value v)
                             (= (str (first v)) (str value))))
                         values))]
    (if (string? current-value)
      current-value
      (get current-value 1))))


(defn dropdown-input [ctx form-state form-props {:keys [label attr values]}]
  (let [dropdown-selected (r/atom label)
        dropdown-open? (r/atom false)]
    (fn [ctx form-state]
      (let [value (forms-helpers/attr-get-in form-state attr)]
        [-dropdown-wrapper {:class (class-names {:is-open @dropdown-open?})}
         [-dropdown-button  {:on-click #(swap! dropdown-open? not)} (if value (get-dropdown-label values value) @dropdown-selected)]
         (when @dropdown-open?
           [-dropdown-content
            (map (fn [v]
                   (let [[key val] (if (string? v) [v v] v)]
                     [:div.p1.bg-h-cyan.c-h-white.bg-white.c-gray
                      {:key key
                       :on-click (fn []
                                   (<cmd ctx :on-change [form-props attr nil key nil] )
                                   (reset! dropdown-selected val)
                                   (reset! dropdown-open? false))}
                      val])) values)])
         [:div.mt1.c-red.a-small
          (render-errors (forms-helpers/attr-errors form-state attr)
                         (get-server-side-errors form-state attr))]]))))


(defn dropdown [{:keys [label selected-value values on-select]}]
  (let [dropdown-open? (r/atom false)]
    (fn [{:keys [label selected-value values on-select]}]
      [-dropdown-wrapper {:class (class-names {:is-open @dropdown-open?})} 
       [-dropdown-button  {:on-click #(swap! dropdown-open? not)} (or selected-value label)]
       (when @dropdown-open?
         [-dropdown-content
          (map (fn [v]
                 [:div.p1.bg-h-cyan.c-h-white.bg-white.c-gray
                  {:key v
                   :on-click (fn [e]
                               (on-select v)
                               (ocall e "preventDefault")
                               (reset! dropdown-open? false))}
                  v]) values)])])))

(defelement -checkbox-wrap
  :tag :div 
  :class [:p1 :center :clearfix]
  :style [{:display "inline-block"
           :max-width "250px"}
          [:input {:display "none"}
           [:&:checked ["~label" [:div {:border-color (str (:cyan colors-with-variations))}]]]]
          [:div {:padding "1rem"
                 :border (str "3px solid " (:white colors-with-variations))}]
          [:img {:border-radius "100%"}]])



(defn image-checkbox [{:keys [label form-state helpers image-src attr name details-url]}]
  (let [{:keys [on-change on-blur set-value]} helpers
        id (str (gensym "checkbox"))
        value (forms-helpers/attr-get-in form-state attr)]
    [-checkbox-wrap 
     [:input {:type "checkbox" 
              :name name 
              :id id
              :value true
              :checked (boolean value)
              :on-change #(set-value attr (not value))}]
     [:label {:for id}
      [:div.flex.flex-column.justify-center
       [:div.circle.border {:style {:width "180px"
                                    :height "180px"
                                    :border "none"
                                    :background-position "center center"
                                    :background-size "cover"
                                    :background-image (str "url(" image-src ")")}}]
       [:p.bold label]
       ;;[:a.c-gray {:href details-url} "View Details"]
       ]]
     (render-errors (forms-helpers/attr-errors form-state attr)
                    (get-server-side-errors form-state attr))])) 
