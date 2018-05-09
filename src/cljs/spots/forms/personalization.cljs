(ns spots.forms.personalization
  (:require [keechma.toolbox.forms.core :as forms-core]
            [clojure.string :as str]
            [forms.validator :as v]
            [clojure.walk :as walk]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [spots.forms.validators :as validators]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [spots.gql :refer [validate-order-personalization-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [spots.domain.cupcake-design :refer [strip-local-data]]
            [cljs-uuid-utils.core :as uuid]))

(defn larger-than-minimum-pack-amount? [value form-data path]
  (let [count-cupcakes-by (:countCupcakesBy form-data)]
    (if (not= "the_dozen" count-cupcakes-by)
      (and (validators/number>=6? value form-data path) (validators/not-empty? value form-data path))
      true)))

(def validator
  (v/validator {:cupcakeCount [[:>=6 larger-than-minimum-pack-amount?]
                               [:not-empty validators/not-empty?]]}))
(defonce cupcake-id (uuid/make-random-uuid))


(def mock-data 
  {:email "tibor.kranjcec@maddev.com"
   :shareableImages true
   :shippingMethod "pickup"
   :pickupTime "2018-01-20 09:30"
   :orderType "personal"
   :countCupcakesBy "the_dozen"
   :flavorIds ["1" "2"]
   :fullName "Tibor Kranjcec"
   :cupcakes [{:colorId "1"
               :curved true
               :firstLine "asdasd"
               :fontId "2"
               :quantity 144
               :secondLine "sadads"
               :thirdLine "sadasd"
               :type "message"}
              {:colorId "2"
               :curved false
               :textSize "small"
               :firstLine "asdasd"
               :fontId "2"
               :quantity 144
               :secondLine "sadads"
               :type "message"}
              {:clipartId "1"
               :quantity 144 
               :type "clipart"}]
   :phoneNumber "111-222-3333"})

(defn init-data [original-data]
  (merge original-data
         mock-data))

(defn cupcakes-total [cupcakes]
  (reduce (fn [acc v]
            (+ acc (or (:quantity v) 0))) 0 cupcakes))

(defn cupcakes-max [form-data]
  (let [ccb (:countCupcakesBy form-data)
        cc (:cupcakeCount form-data)]
    (if (= "the_dozen" ccb) (* 12 cc) (* 4 cc))))

(defn cupcake-qty-processor [app-db form-props form-state path value]
  (let [int-value (js/parseInt value)
        real-int-value (if (js/isNaN int-value) 0 int-value)
        form-data (:data form-state)
        potential-form-data (assoc-in form-data path real-int-value)
        potential-cupcakes-diff (- (cupcakes-max potential-form-data) (cupcakes-total (:cupcakes potential-form-data)))]
    (assoc form-state :data
           (assoc-in form-data path (if (neg? potential-cupcakes-diff) (+ real-int-value potential-cupcakes-diff) real-int-value)))))

(defn update-cupcake-qty [form-state cupcakes-group-count val-qty val-mod]
  (let [qtys (update (vec (repeat cupcakes-group-count val-qty)) 0 #(+ val-mod %))]
    (if (pos? cupcakes-group-count)
      (reduce-kv (fn [m k v]
                   (assoc-in m [:data :cupcakes k :quantity] v)) form-state qtys)
      form-state)))

(defn cupcake-max-processor [app-db form-props form-state path value]
  (let [int-value (js/parseInt value)
        real-int-value (if (js/isNaN int-value) 0 int-value)
        form-data (:data form-state)
        cupcakes-group-count (count (:cupcakes form-data))
        cupcakes-max-count (cupcakes-max (assoc form-data :cupcakeCount real-int-value)) 
        new-val-mod (mod cupcakes-max-count cupcakes-group-count)
        new-val-qty (quot cupcakes-max-count cupcakes-group-count)]
    (-> form-state
        (update-cupcake-qty cupcakes-group-count new-val-qty new-val-mod)
        (assoc-in (concat [:data] path) real-int-value))))

(defn count-cupcakes-by-processor [app-db form-props form-state path value]
  (let [new-state (assoc-in form-state (concat [:data] path) value)]
    (if (= "the_dozen" value)
      new-state
      (try
        (let [cupcakes (get-in form-state [:data :cupcakes])
              new-cupcakes (vec (if (<= 4 (count cupcakes))
                                  (subvec cupcakes 0 4)
                                  (concat cupcakes (repeatedly (- 4 (count cupcakes)) (fn [] {:quantity 0
                                                                                               :type nil
                                                                                               :id (uuid/make-random-uuid)})))))] 
          (cupcake-max-processor app-db
                                 form-props
                                 (assoc-in new-state [:data :cupcakes] new-cupcakes)
                                 [:cupcakeCount]
                                 (or (get-in form-state [:data :cupcakeCount]) 0)))
        (catch :default e
          #_(println e))))))


(defrecord PersonalizationForm [validator])

(defmethod forms-core/get-data PersonalizationForm [this app-db form-props]  
  (-> (get-in app-db [:kv :order-form])
      identity
      init-data)
  (get-in app-db [:kv :order-form]))



(defmethod forms-core/process-attr-with PersonalizationForm [this path]
  (let [[k-first _ k-last] path]
    (cond
      (= :countCupcakesBy k-first)                     count-cupcakes-by-processor
      (= :cupcakeCount k-first)                        cupcake-max-processor
      (and (= :cupcakes k-first) (= :quantity k-last)) cupcake-qty-processor
      :else                                            nil)))

(defmethod forms-core/submit-data PersonalizationForm [_ app-db _ data]
  (let [] 
    (wrap-validation
     (gql-req validate-order-personalization-m (wrap-request (strip-local-data data))))))

(defmethod forms-core/on-submit-success PersonalizationForm [this app-db form-props data]
  (let [res (:validate data)
        data (:data (get-form-state app-db form-props))]
    (pipeline! [value app-db] 
      (when (:valid res)
        (pipeline! [value app-db]
          (pp/commit! (assoc-in app-db [:kv :order-form] data))
          (pp/send-command! [forms-core/id-key :mount-form] [:confirmation :form])
          (pp/commit! (assoc-in app-db [:kv :form-progress :confirmation] true))
          (pp/redirect! {:page "confirmation"}))))))

(defn constructor []
  (->PersonalizationForm validator))
