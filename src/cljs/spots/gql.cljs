(ns spots.gql
  (:require [graphql-builder.parser :refer-macros [defgraphql]]
            [graphql-builder.core :as core]))

(defn get-mutation [query-map name]
  (get-in query-map [:mutation name]))

(defn get-query [query-map name]
  (get-in query-map [:query name]))

(defgraphql queries "resources/graphql/queries.graphql")

(def q-map (try (core/query-map queries {:inline-fragments true}) (catch js/Object e (throw queries))))


;;;;;;;;;;;;;
;; QUERIES ;;
;;;;;;;;;;;;;

(def clipart-q (get-query q-map :clipart))
(def cupcake-types-q (get-query q-map :cupcake-types))
(def fonts-q (get-query q-map :fonts))
(def colors-q (get-query q-map :colors))
(def building-types-q (get-query q-map :building-types))
(def industries-q (get-query q-map :industries))

;;;;;;;;;;;;;;;
;; MUTATIONS ;;
;;;;;;;;;;;;;;;

(def calculate-order-prices-m (get-mutation q-map :calculate-order-price))
(def create-order-m (get-mutation q-map :create-order))
(def validate-order-m (get-mutation q-map :validate-order))
(def validate-order-personal-information-m (get-mutation q-map :validate-order-personal-information))
(def validate-order-information-m (get-mutation q-map :validate-order-information))
(def validate-order-personalization-m (get-mutation q-map :validate-order-personalization))
(def validate-order-overview-m (get-mutation q-map :validate-order-overview))
(def validate-order-payment-m (get-mutation q-map :validate-order-payment))
