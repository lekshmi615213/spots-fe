(ns spots.ui.components.datepicker
  (:require [oops.core :refer [oget ocall oset!]]
            [spots.ui.components.inputs :refer [-input controlled-input render-errors]]
            [reagent.core :as r]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [clojure.string :as str]
            [keechma.toolbox.ui :refer [<cmd]]
            [cljsjs.pikaday]
            [cljsjs.moment]
            [camel-snake-kebab.core :refer [->camelCaseString]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [oops.core :refer [ocall oget]]))

(defn mount-datepicker [ctx form-props datepicker-params el]
  (let [default-date (js/Date.)
        pikaday (js/Pikaday. (clj->js (merge {:field el
                                              :format "MM-DD-YYYY" 
                                              :minDate (ocall (ocall (js/moment) "add" 1 "days") "toDate") 
                                              :onSelect (fn [_]
                                                          (this-as this
                                                            (let [value (ocall this "toString")] 
                                                              (<cmd ctx :on-change [form-props :shippingDate nil value nil] ))))}
                                             datepicker-params)))]))

(defn render-input [ctx input-props]
  (r/create-class
   {:reagent-render 
    (fn [ctx input-props] 
      (let [{:keys [form-state helpers placeholder label form-props attr]} input-props
            {:keys [on-change on-blur]} helpers]
        [-input {:placeholder placeholder
                 :on-change (on-change attr)
                 :on-blur (on-blur attr)
                 :on-key-press (fn [e]
                                 (when (= 13 (oget e "which"))
                                   ;; enter key is pressed
                                   (ocall e "preventDefault")))
                 :value (forms-helpers/attr-get-in form-state attr)}]))
    :component-did-mount (fn [this]
                           (let [dom-node (r/dom-node this)
                                 {:keys [form-props datepicker-params]} input-props]
                             (mount-datepicker ctx form-props datepicker-params dom-node)))
    :component-will-update (fn [this [_ _ input-props]]
                            (let [dom-node (r/dom-node this)
                                  form-state (:form-state input-props)
                                  shipping-date (forms-helpers/attr-get-in form-state :shippingDate)]
                              (when (not= shipping-date (oget dom-node "value"))
                                (oset! dom-node "value" shipping-date))))}))

(defn render [ctx input-props]
  (let [{:keys [form-state attr]} input-props]
    [:div.mb0-5
     [render-input ctx input-props]
     (render-errors (forms-helpers/attr-errors form-state attr))]))
