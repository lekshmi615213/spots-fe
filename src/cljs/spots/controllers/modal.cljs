(ns spots.controllers.modal
  (:require [keechma.toolbox.pipeline.controller :as pp-controller]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]] 
            [keechma.toolbox.forms.core :as forms-core]
            [oops.core :refer [oget ocall]]
            [promesa.core :as p]))

(def controller
  (pp-controller/constructor
   (fn [route-data]
     true)
   {:close-paid-modal (pipeline! [value app-db]
                        (pp/commit! (assoc-in app-db [:kv :show-paid-modal] false)))}))
