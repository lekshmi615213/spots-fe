(ns spots.ui.components.cropper
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [clojure.string :as str]
            [reagent.core :as r] 
            [oops.core :refer [ocall oget]]
            [cropperjs]))

(defelement -cropped-image
  :tag :img
  :style [{:max-width "100%"}])

(defelement -wrap
  :style [{:height "500px"}
          [:.cropper-view-box {:border-radius "50%"}]
          [:.cropper-face {:border-radius "50%"}]])

(def Cropper (oget js/window "Cropper"))

(defn mount-cropper [wrap]
  (when wrap
    (when-let [image (oget wrap "firstChild")]
      (let [cropper (Cropper. image #js{:aspectRatio 1
                                        :viewMode 0
                                        :guides false
                                        :rotatable false
                                        :zoomable false})]
        cropper))))

(defn unmount-cropper [cropper-inst]
  (when cropper-inst
    (ocall cropper-inst "destroy")))

(defn render [{:keys [src cropper-inst-atom]}]
  (r/create-class
   {:component-did-mount (fn [c _]
                           (let [wrap (r/dom-node c)]
                             (unmount-cropper @cropper-inst-atom)
                             (reset! cropper-inst-atom (mount-cropper wrap))))
    :component-did-update (fn [c _]
                            (let [wrap (r/dom-node c)]
                              (unmount-cropper @cropper-inst-atom)
                              (reset! cropper-inst-atom (mount-cropper wrap))))
    :component-will-unmount #(unmount-cropper @cropper-inst-atom)

    :reagent-render (fn [{:keys [src]}]
                      [-wrap [-cropped-image {:src src}]])}))


