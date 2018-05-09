(ns spots.ui.components.design-modals
  (:require [keechma.ui-component :as ui] 
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [clojure.string :as str]
            [reagent.core :as r]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [spots.ui.components.inputs :refer [controlled-input -button-rounded -button dropdown-input -button -button-rounded radiobtn]]
            [spots.ui.components.decorators :refer [-title cupcake-design cupcake-design-empty -dashed-divider]]
            [spots.ui.components.modals :refer [form-modal]]
            [oops.core :refer [ocall oget]]
            [spots.ui.components.image-upload :as img-upload]))

(defn render [ctx {:keys [form-props form-state helpers]}]
  (let [current-url (route> ctx)
        popup       (:popup current-url)
        popup-type  (:type popup)]
    (case popup-type
      "upload"  [form-modal ctx {:modal-title   ""
                                 :modal-content [(ui/component ctx :image-upload)]}]
      "clipart" [form-modal ctx {:modal-title   ""
                                 :modal-content [(ui/component ctx :clipart)]}]
      "message" [form-modal ctx {:modal-title   "Write A Message"
                                 :modal-content [(ui/component ctx :message) form-props]}]
      nil)))

(def component (ui/constructor {:renderer render
                                :topic :message
                                :component-deps [:image-upload :clipart :message]}))
