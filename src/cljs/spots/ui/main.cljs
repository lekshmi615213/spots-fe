(ns spots.ui.main
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd route>]]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.util :refer [class-names]]))

(def pages
  [{:page "user-info" :title "Your Info"}
   {:page "order-info" :title "Order Info"}
   {:page "personalization" :title "Personalization"}
   {:page "confirmation" :title "Confirmation"}])

(defelement -wrap
  :class [:mx-auto :my4]
  :style [])

(defelement -content
  :class [:mx-auto :mt4 :max-width-4])

(defelement -tab
  :tag :li
  :class [:center]
  :style [{:width         "100%"
           :height        "50px"
           :position      "relative" 
           :border-top    "none" 
           :border-bottom "none" 
           :border-left   "3px solid white"}
          [:a {:height "50px"}]
          [[:&:first-child {:border-left "none"}]]
          [[:&:last-child {}]
           [:&.current [:&:after {:content      "\"\"" 
                                  :position     "absolute" 
                                  :left         "calc(50% - 5px)" 
                                  :width        0 
                                  :height       0 
                                  :border-left  "10px solid transparent" 
                                  :border-right "10px solid transparent" 
                                  :border-top   "10px solid #86D6F7"}]]]])

(defn render-tabs [ctx]
  (let [current-route (route> ctx)
        current-page  (:page current-route)
        form-progress (sub> ctx :form-progress)]
    [:div.bg-gray.clearfix
     [:ul.list-style-none.m0.p0.max-width-4.mx-auto.justify-center.flex
      (map (fn [{:keys [page title]}]
             ^{:key page}
             [-tab {:class (class-names {[:bg-cyan :current] (or (= page current-page)
                                                                 (and (= "payment" current-page) (= "confirmation" page))
                                                                 (and (= "recipient" current-page) (= "order-info" page) ))})}
              (if ((keyword page) form-progress)
                [:a.block.py1.text-decoration-none.c-white.uppercase {:href (ui/url ctx {:page page})} title]
                [:p.block.py0.text-decoration-none.c-white.uppercase title])]) pages)]]))

(defn progress-allows-route? [target cp fp]
  (when fp
    (and (= target cp) ((keyword cp) fp))))

(defn last-completed-step [fp]
  (when fp
    (->> fp
        (filter #(true? (val %)))
        last
        key
        name)))

(defn render [ctx]
  (let [current-route (route> ctx)
        form-progress (sub> ctx :form-progress)
        current-page  (:page current-route)]
    [-wrap
     [:div.center
      [:h1.c-gray-l "ORDER MINI CUPCAKES"]]
     [render-tabs ctx]
     [-content
      (cond
        (progress-allows-route? "user-info" current-page form-progress)       [(ui/component ctx :forms-user-info)]
        (progress-allows-route? "order-info" current-page form-progress)      [(ui/component ctx :forms-order-info)]
        (progress-allows-route? "personalization" current-page form-progress) [(ui/component ctx :forms-personalization)]
        (progress-allows-route? "confirmation" current-page form-progress)    [(ui/component ctx :forms-confirmation)]
        (progress-allows-route? "recipient" current-page form-progress)       [(ui/component ctx :forms-recipient)]
        :else (ui/redirect ctx {:page (str (last-completed-step form-progress))}))]]))

(def component (ui/constructor {:renderer          render
                                :subscription-deps [:order-form :form-progress]
                                :component-deps    [:forms-user-info
                                                    :forms-order-info
                                                    :forms-personalization
                                                    :forms-confirmation
                                                    :forms-recipient]}))
