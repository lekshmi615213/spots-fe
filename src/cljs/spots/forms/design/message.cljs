(ns spots.forms.design.message
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.controller :refer [get-form-state]]
            [forms.validator :as v]
            [spots.forms.validators :as vs]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [spots.gql :refer [validate-order-personal-information-m]]
            [spots.util.gql-api :refer [gql-req wrap-request wrap-validation]]
            [promesa.core :as p]
            [spots.domain.cupcake-design :refer [get-cupcake-idx form-data-path clear-designs clear-message clear-image-and-clipart set-design-type]]
            [oops.core :refer [ocall oget oset!]]
            [spots.edb :as edb]
            [garden.core :refer [css]]  
            [garden.stylesheet :refer [at-font-face]]
            [spots.util.helpers :refer [dissoc-in]]))

(def validator
  (v/validator {:curved  [[:not-empty vs/not-empty?]
                          [:bool vs/bool?]]
                :fontId  [[:not-empty vs/not-empty?]]
                :colorId [[:not-empty vs/not-empty?]]}))

(defn set-text-size [data]
  (if (:threeLine data)
    data
    (update data :textSize #(or % "small"))))

(defrecord DesignMessageForm [validator])

(defmethod forms-core/get-data DesignMessageForm [this app-db form-props]
  (let [cupcake-design-id (get-in app-db [:route :data :popup :id])
        cupcakes          (get-in app-db (conj form-data-path :cupcakes))
        cupcake-idx       (get-cupcake-idx cupcakes cupcake-design-id)
        data              (get-in app-db (concat form-data-path [:cupcakes cupcake-idx]))] 
    (-> data
        (update :curved boolean)
        (update :threeLine #(if (nil? %) true %))
        (assoc :textSize nil)
        set-text-size
        (assoc :type "message")
        (select-keys [:firstLine :secondLine :thirdLine :curved :colorId :fontId :type :fontSize :threeLine :textSize]))))

(defn set-message-form-data [app-db data]
  (let [cupcake-design-id   (get-in app-db [:route :data :popup :id])
        cupcakes            (get-in app-db (conj form-data-path :cupcakes))
        cupcake-idx         (get-cupcake-idx cupcakes cupcake-design-id)
        cupcake-design-path (concat form-data-path [:cupcakes cupcake-idx])
        cupcake-design-data (get-in app-db cupcake-design-path)]
    (-> app-db
        (clear-designs cupcake-idx)
        (update-in cupcake-design-path #(merge % data)))))

(defmethod forms-core/on-submit-success DesignMessageForm [this app-db form-props data]
  (pipeline! [value app-db]
    (pp/commit! (set-message-form-data app-db data))
    (pp/redirect! (dissoc (get-in app-db [:route :data]) :popup))))

(defn font-stylesheet-id [font-id]
  (str "font-" font-id))

(defn create-font-stylesheet [font]
  (let [css   (str
               "@font-face {"
               "\n"
               "  font-family: '" (:name font) "';"
               "\n"
               "  src: url(" (:fileURL font) ");"
               "\n"
               "}")
        style (ocall js/window "document.createElement" "style")
        head  (oget js/window "document.head")]  

    (oset! style "id" (gensym (font-stylesheet-id (:id font))))
    (ocall head "appendChild" style)
    (when-let [sheet (oget style "sheet")]
      (ocall sheet "insertRule" css 0)) ))

(defn set-font-id [app-db form-props form-state path value]
  (let [stylesheet (ocall js/window "document.getElementById" (font-stylesheet-id value))]
    (when (nil? stylesheet)
      (create-font-stylesheet (edb/get-item-by-id app-db :font value)))
    (assoc-in form-state (concat [:data] path) value)))



(defn process-svg-preview [app-db form-props form-state path value]
  (let [new-state (assoc-in form-state (concat [:data] path) value)]
   (if value
     (assoc-in new-state [:data :textSize] nil)
     (-> new-state
         (assoc-in [:data :textSize] "small")
         (dissoc-in [:data :firstLine])
         (dissoc-in [:data :thirdLine])))))

(defmethod forms-core/process-attr-with DesignMessageForm [this path] 
  (cond
    (= path [:fontId]) set-font-id
    (= path [:threeLine]) process-svg-preview
    :else              nil))

(defn constructor []
  (->DesignMessageForm validator))
