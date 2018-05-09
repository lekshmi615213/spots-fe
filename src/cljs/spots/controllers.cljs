(ns spots.controllers
  (:require [spots.forms :as forms]
            [spots.controllers.image-upload :as image-upload]
            [spots.controllers.clipart :as clipart]
            [keechma.toolbox.forms.controller :as forms-controller]
            [keechma.toolbox.forms.mount-controller :as forms-mount-controller]
            [spots.controllers.modal :as modal]
            [spots.controllers.init :as init]
            [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [spots.datasources :refer [datasources]]
            [spots.edb :refer [edb-schema]]))

(def controllers
  (-> {:image-upload image-upload/controller
       :clipart clipart/controller
       :modal modal/controller
       :init init/controller}
      (forms-controller/register forms/forms)
      (forms-mount-controller/register forms/forms-params)
      (dataloader-controller/register datasources edb-schema)))
