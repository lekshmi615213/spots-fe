(ns spots.controllers.clipart
  (:require [keechma.toolbox.pipeline.controller :as pp-controller]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]] 
            [keechma.toolbox.forms.core :as forms-core]
            [oops.core :refer [oget ocall]]
            [medley.core :refer [dissoc-in]]
            [promesa.core :as p]
            [spots.domain.cupcake-design :refer [get-cupcake-idx form-data-path clear-designs set-design-type]]))

(defn set-cupcake-clipart [app-db clipart-id]
   (let [cupcake-design-id (get-in app-db [:route :data :popup :id])
         cupcakes (get-in app-db (conj form-data-path :cupcakes))
         cupcake-idx (get-cupcake-idx cupcakes cupcake-design-id)]
     (if cupcake-idx
       (-> app-db
           (clear-designs cupcake-idx)
           (assoc-in (concat form-data-path [:cupcakes cupcake-idx :clipartId]) clipart-id)
           (set-design-type cupcake-idx "clipart"))
       app-db)))

(def controller
  (pp-controller/constructor
   (fn [route-data]
     (when (= "clipart" (get-in route-data [:data :popup :type]))
       true))
   {:set (pipeline! [value app-db]
           (pp/commit! (set-cupcake-clipart app-db value))
           (pp/redirect! (dissoc (get-in app-db [:route :data]) :popup)))}))
