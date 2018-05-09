(ns spots.ui.components.image-upload
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.css.core :refer-macros [defelement]]
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [clojure.string :as str]
            [reagent.core :as r]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [spots.ui.components.inputs :refer [controlled-input -button-rounded -button dropdown-input -button -button-rounded radiobtn]]
            [spots.ui.components.decorators :refer [-title cupcake-design cupcake-design-empty -dashed-divider]]
            [spots.ui.components.modals :refer [form-modal]]
            [spots.ui.components.cropper :as cropper]
            [cropperjs]
            [oops.core :refer [ocall oget]]
            [react-avatar-edit :refer [reactAvatar]]))

(def blank-img "/img/blank-img.jpg")

(def avatar (r/adapt-react-class (.-default reactAvatar)))

(defelement -dropzone-wrap
  :tag :label
  :class [:overflow-hidden :absolute :top-0 :left-0 :right-0 :bottom-0 :flex :justify-center :items-center]
  :style [])

(defelement -dropzone-input
  :tag :input
  :class [:block :absolute :top-0 :right-0 :left-0 :bottom-0 ]
  :style [{:min-width "100%"
           :opacity 0
           :text-align "right"
           :min-height "100%"
           :font-size "999px"}])

(defn render [ctx {:keys [idx form-props form-state helpers]}]
  (let [img-crop-preview (r/atom nil)
        cropper-inst-atom (atom nil)]
    (fn []
      (let [image-upload-preview (sub> ctx :image-upload-preview)
            processing-selected-image? (sub> ctx :processing-selected-image?)]
        [:div.flex.flex-column.justify-center
         [:div.relative {:style {:min-height "500px"
                                 :border (str "2px solid " (:light-gray-l colors-with-variations))}}
          (if image-upload-preview
            [cropper/render {:src image-upload-preview
                             :cropper-inst-atom cropper-inst-atom}] 
            (if processing-selected-image?
              [-dropzone-wrap
               [:p.h2.caps.regular.c-light-gray.center "Processing image..."]]
              [-dropzone-wrap
               [-dropzone-input
                {:type "file"
                 :accept "image/jpeg, image/png"
                 :on-change #(<cmd ctx :upload (oget % "target.?files.?0"))}]
               [:p.h2.caps.regular.c-light-gray.center "Drag an image here or "
                [:br]
                [:a.bold {:href "#"} "Browse"]
                " for an image to upload"]]))]
         [:footer.mt0-5.c-gray-l.center.z4.flex.justify-center.flex-column
          (when image-upload-preview
            [:p.mb1-5 "To resize an image, click and drag the circle to reach the size you want." [:br] "You can also click and drag the sides the preview image to select your desired area."])
          [-button {:on-click (fn [e]
                                (when-let [cropper-inst @cropper-inst-atom]
                                  (let [canvas (ocall cropper-inst "getCroppedCanvas" (clj->js {:fillColor "#ffffff"}))] 
                                    (<cmd ctx :commit (ocall canvas "toDataURL" "image/jpeg" 1.0))))
                                (.preventDefault e))} "Upload Art"]]]))))



(def component (ui/constructor {:renderer render
                                :subscription-deps [:image-upload-preview :processing-selected-image?]
                                :topic :image-upload}))
