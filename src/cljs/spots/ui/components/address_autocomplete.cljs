(ns spots.ui.components.address-autocomplete
  (:require [oops.core :refer [oget ocall oset!]]
            [spots.ui.components.inputs :refer [-input controlled-input render-errors]]
            [reagent.core :as r]
            [spots.forms.validators :as validators]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [clojure.string :as str]
            [keechma.toolbox.ui :refer [<cmd]]))

(defn place->address [place]
  (let [components (:address_components place)]
    (reduce (fn [acc c]
              (let [types (set (:types c))] 
                (cond
                  (contains? types "street_number") (assoc acc :street (conj (:street acc) (:long_name c)))
                  (contains? types "route") (assoc acc :street (conj (:street acc) (:long_name c)))
                  (contains? types "locality") (assoc acc :city (conj (:city acc) (:long_name c)))
                  (contains? types "administrative_area_level_2") (assoc acc :city (conj (:city acc) (:long_name c)))
                  (contains? types "administrative_area_level_1") (assoc acc :state (:short_name c))
                  (contains? types "postal_code") (assoc acc :zipcode (conj (:zipcode acc) (:long_name c)))
                  :else acc)))
            {:street []
             :city []
             :state ""
             :zipcode []} components)))

(defn join-address-parts [a]
  (assoc a
         :street (str/join ", " (:street a))
         :city (str/join ", " (:city a))
         :zipcode (str/join " - " (:zipcode a))))


(def Autocomplete (oget js/window "google.maps.places.Autocomplete"))

(defn mount-autocomplete [ctx form-props el]
  (let [autocomplete (Autocomplete. el #js {:componentRestrictions #js {:country "US"}})]
    (ocall autocomplete "addListener" "place_changed"
           (fn []
            (this-as this
              (let [place (js->clj (ocall this "getPlace") :keywordize-keys true)
                    formatted-address (join-address-parts (place->address place))]
                (oset! el "value" (:street formatted-address))
                (doseq [[k v] formatted-address]
                  (<cmd ctx :on-change [form-props [:address k] nil v nil]))))))))

(defn render-input [ctx form-props input-props]
  (r/create-class
   {:reagent-render 
    (fn [ctx form-props input-props] 
      (let [{:keys [form-state helpers placeholder label attr]} input-props
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
                           (let [dom-node (r/dom-node this)]
                             (mount-autocomplete ctx form-props dom-node)))
    :component-will-update (fn [this [_ _ _ input-props]]
                            (let [dom-node (r/dom-node this)
                                  form-state (:form-state input-props)
                                  street (forms-helpers/attr-get-in form-state [:address :street])]
                              (when (not= street (oget dom-node "value"))
                                (oset! dom-node "value" street))))}))

(defn render [ctx form-props input-props]
  (let [{:keys [form-state attr]} input-props]
    [:div.mb0-5
     [render-input ctx form-props input-props]
     (render-errors (forms-helpers/attr-errors form-state attr))]))
