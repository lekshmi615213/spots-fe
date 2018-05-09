(ns spots.subscriptions
  (:require [keechma.toolbox.dataloader.subscriptions :as dataloader]
            [spots.edb :refer [edb-schema] :as edb]
            [spots.datasources  :refer [datasources]]
            [keechma.toolbox.forms.helpers :as forms-helpers])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn get-kv [key]
  (fn [app-db-atom]
    (reaction
     (get-in @app-db-atom (flatten [:kv key])))))

(defn clipart-url [app-db-atom id]
  (reaction
   (let [clipart (edb/get-item-by-id @app-db-atom :clipart id)]
     (:imageURL clipart))))

(defn clipart-filtered [app-db-atom]
  (reaction
   (let [app-db @app-db-atom
         clipart-filter (get-in (get-in app-db [:route :data]) [:popup :filter])
         clipart-list (edb/get-collection app-db :clipart :list)
         clipart-filtered (into [] (filter #(= clipart-filter (:category %)) clipart-list))]  
     (if (or (= "All" clipart-filter) (nil? clipart-filter))
       clipart-list
       clipart-filtered))))

(def subscriptions
  (merge (dataloader/make-subscriptions datasources edb-schema)
         {:form-state                 forms-helpers/form-state-sub
          :order-form                 (get-kv :order-form)
          :image-upload-preview       (get-kv :image-upload-preview)
          :cupcake-designs            (get-kv :cupcake-designs)
          :clipart-filtered           clipart-filtered
          :clipart-url                clipart-url
          :order-pricing              (get-kv :order-pricing)
          :show-paid-modal            (get-kv :show-paid-modal)
          :form-progress              (get-kv :form-progress)
          :processing-selected-image? (get-kv :processing-selected-image?)}))
