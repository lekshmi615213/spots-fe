(ns spots.controllers.init
  (:require [keechma.toolbox.pipeline.controller :as pp-controller]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]] ))

(def controller
  (pp-controller/constructor
   (fn [_]
     true)
   {:start (pipeline! [value app-db]
           (pp/commit! (assoc-in app-db [:kv :form-progress] {:user-info true})))}))
