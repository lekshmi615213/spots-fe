(ns spots.domain.cupcake-design
  (:require [keechma.toolbox.forms.core :refer [id-key]]))

(def form-data-path [:kv id-key :states [:personalization :form] :data])

(defn get-cupcake-idx [cupcakes cupcake-design-id]
  (loop [idx 0
         cs cupcakes]
    (let [current (first cs)]
      (if (= cupcake-design-id (str (:id current)))
        idx
        (let [rest-cs (rest cs)]
          (if (seq rest-cs)
            (recur (inc idx) rest-cs)
            nil))))))


(defn set-design-type [app-db idx type]
  (let [cupcake-path (concat form-data-path [:cupcakes idx :type])
        cupcake-design (get-in app-db cupcake-path)]
    (assoc-in app-db cupcake-path type)))

(defn clear-designs [app-db idx]
  (let [cupcake-path (concat form-data-path [:cupcakes idx])
        cupcake-design (get-in app-db cupcake-path)]
    (assoc-in app-db cupcake-path
              (dissoc cupcake-design
                      :encodedImage
                      :clipartId
                      :curved
                      :fontId
                      :firstLine
                      :secondLine
                      :thirdLine
                      :threeLine))))

(defn clear-message [app-db idx]
  (let [cupcake-path (concat form-data-path [:cupcakes idx])
        cupcake-design (get-in app-db cupcake-path)]
    (assoc-in app-db cupcake-path
              (dissoc cupcake-design 
                      :curved
                      :colorId
                      :fontId
                      :firstLine
                      :secondLine
                      :thirdLine
                      :textSize
                      :threeLine))))

(defn clear-image-and-clipart [app-db idx]
  (let [cupcake-path (concat form-data-path [:cupcakes idx])
        cupcake-design (get-in app-db cupcake-path)]
    (assoc-in app-db cupcake-path
              (dissoc cupcake-design
                      :encodedImage
                      :clipartId))))

(defn remove-cupcake-ids [data]
  (let [cupcakes (:cupcakes data)]
    (assoc data :cupcakes (map #(dissoc % :id) cupcakes))))

(defn prepare-flavors [data]
  (let [flavors (:flavorIds data)]
    (assoc data :flavorIds (map (fn [k]
                                  (str (key k))) flavors))))

(defn strip-local-data [data]
  (let [cupcakes (:cupcakes data)
        flavor-ids (:flavorIds data)]
    (-> data
        (assoc :flavorIds (map #(str (key %)) flavor-ids))
        (assoc :cupcakes (map #(dissoc % :id :threeLine) cupcakes))
        (dissoc :cupcakeCount))))
