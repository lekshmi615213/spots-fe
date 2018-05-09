(ns spots.datasources
  (:require [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]
            [keechma.toolbox.dataloader.core :as dataloader]
            [spots.util.gql-api :as ga]
            [spots.gql :as gql]
            [promesa.core :as p]))


(defn gql-processor [key]
  (fn [data]
    (get-in data (flatten [key]))))


(defn current-route-data [route]
  (first (filter #(= (get-in route [:page]) (:page %)) (:routes route))))


(def cupcake-datasource
  {:target [:kv :cupcake-types]
   :loader ga/gql-loader
   :processor (gql-processor :cupcakeTypes) 
   :params (fn [_ route _]
             (when (or (= "confirmation" (:page route)) (= "personalization" (:page route)))
               {:query-fn gql/cupcake-types-q}))})

(def clipart-datasource
  {:target [:edb/collection :clipart/list]
   :loader ga/gql-loader
   :processor (gql-processor :clipart) 
   :params (fn [_ route _]
             (when (or (= "confirmation" (:page route)) (= "personalization" (:page route)))
               {:query-fn gql/clipart-q}))})

(def font-datasource
  {:target [:edb/collection :font/list]
   :loader ga/gql-loader
   :processor (gql-processor :fonts) 
   :params (fn [_ route _]
             (when (or (= "confirmation" (:page route)) (= "personalization" (:page route)))
               {:query-fn gql/fonts-q}))})

(def color-datasource
  {:target [:edb/collection :colors/list]
   :loader ga/gql-loader
   :processor (gql-processor :colors) 
   :params (fn [_ route _]
             (when (or (= "confirmation" (:page route)) (= "personalization" (:page route)))
               {:query-fn gql/colors-q}))})

(def building-datasource
  {:target [:edb/collection :buildings/list]
   :loader ga/gql-loader
   :processor (gql-processor :buildingTypes) 
   :params (fn [_ route _]
             {:query-fn gql/building-types-q})})

(def industry-datasource
  {:target [:edb/collection :industry/list]
   :loader ga/gql-loader
   :processor (gql-processor :industries) 
   :params (fn [_ route _]
             {:query-fn gql/industries-q})})


(def datasources {:cupcake-types cupcake-datasource
                  :clipart       clipart-datasource
                  :font          font-datasource
                  :colors        color-datasource
                  :buildings     building-datasource
                  :industries    industry-datasource})
