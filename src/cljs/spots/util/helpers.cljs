(ns spots.util.helpers
  (:require [spots.util.gql-api :as ga]
            [clojure.string :as str]))

(defn get-font-name [font-id font]
  (if font-id
    (->> font
         (filter #(= (:id %) font-id))
         first
         :name)
    "Verdana"))

(defn get-property-name [property-id properties]
  (if property-id
    (->> properties
         (filter #(= (:id %) property-id))
         first
         :name)
    "None"))

(defn get-doorman-status [property-id properties]
  (if property-id
    (->> properties
         (filter #(= (:id %) property-id))
         first
         :askForDoorman)
    false))


(defn get-color-hex [color-id colors]
  (if color-id
    (->> colors
         (filter #(= (:id %) color-id))
         first
         :hexValue)
    "#000000"))

(defn get-message-params [font-coll color-coll design]
  (let [font-id (:fontId design)
        color-id (:colorId design)
        text-size (:textSize design)
        three-line? (:threeLine design)
        font-family (get-font-name font-id font-coll)
        color (get-color-hex color-id color-coll)]
    {:first-line (:firstLine design)
     :three-line? three-line?
     :second-line (:secondLine design)
     :third-line (:thirdLine design)
     :curved (:curved design)
     :font-family font-family
     :text-size text-size
     :color color}))

(def generate-resource-link identity)


(def constants
  {:shipping-methods {:pickup "pickup"
                     :manhattan_delivery "manhattan_delivery"
                     :shipping "shipping"}
   :order-count_by {:the-dozen "the_dozen"
                    :packs-of-four "packs_of_four"}
   :order-type {:personal "personal"
                :corporate "corporate"}
   :cupcake-medium-type {:image "image"
                         :clipart "clipart"
                         :message "message"}
   :text-size {:small "small"
               :medium "medium"
               :large "large"}})

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))
