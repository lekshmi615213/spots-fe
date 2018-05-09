(ns spots.domain.form-helpers
  (:require [spots.ui.components.inputs :refer [-button]]
            [clojure.string :as str]
            [spots.ui.components.spinner :refer [small-white-spinner]]))

(defn submit-button [form-state label]
  (let [submitting? (= :submitting (get-in form-state [:state :type]))]
    [-button
     label
     (when submitting?
       [small-white-spinner])]))

(defn is-sunday? [date-str] 
  (if (= "0" (str (.getDay date-str)))
    true
    false))

(defn is-saturday? [date-str] 
  (if (= "6" (str (.getDay date-str)))
    true
    false))

(def format-phone
  ^{:format-chars #{"-"}}
  (fn [value _]
    (let [clean-value (subs (str/replace (or value "") #"[^0-9]" "") 0 10)
          clean-value-length (count clean-value)]
      (if (>= 3 clean-value-length)
        clean-value
        (if (>= 6 clean-value-length)
          (str (subs clean-value 0 3) "-" (subs clean-value 3))
          (str (subs clean-value 0 3) "-" (subs clean-value 3 (min 6 clean-value-length)) "-" (subs clean-value 6)))))))
