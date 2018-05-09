(ns spots.controllers.image-upload
  (:require [keechma.toolbox.pipeline.controller :as pp-controller]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]] 
            [keechma.toolbox.forms.core :as forms-core]
            [oops.core :refer [oget ocall]]
            [medley.core :refer [dissoc-in]]
            [promesa.core :as p]
            [keechma.toolbox.forms.core :refer [id-key]]
            [spots.domain.cupcake-design :refer [get-cupcake-idx form-data-path clear-designs set-design-type]]))


(defn read-image-from-file [file]
  (p/promise
   (fn [resolve reject]
     (let [fr (js/FileReader.)
           handler-fn (fn handler-fn []
                        (ocall fr "removeEventListener" "load" handler-fn false)
                        (resolve (oget fr "result")))]
       (ocall fr "addEventListener" "load" handler-fn false)
       (ocall fr "readAsDataURL" file)))))

(defn commit-image-to-form [app-db cropped-image]
  (let [cupcake-design-id (get-in app-db [:route :data :popup :id])
        cupcakes (get-in app-db (conj form-data-path :cupcakes))
        cupcake-idx (get-cupcake-idx cupcakes cupcake-design-id)]
    (if cupcake-idx
      (-> app-db
          (dissoc-in [:kv :image-upload-preview])
          (clear-designs cupcake-idx)
          (assoc-in (concat form-data-path [:cupcakes cupcake-idx :encodedImage]) cropped-image)
          (set-design-type cupcake-idx "image"))
      app-db)))

(def controller
  (pp-controller/constructor
   (fn [route-data]
     (when (= "upload" (get-in route-data [:data :popup :type]))
       true))
   {:start (pipeline! [value app-db]
             (pp/commit! (assoc-in app-db [:kv :processing-selected-image?] false)))
    :stop (pipeline! [value app-db]
            (pp/commit! (dissoc-in app-db [:kv :image-upload-preview])))
    :upload (pipeline! [value app-db]
              (when value
                (pipeline! [value app-db]
                  (pp/commit! (assoc-in app-db [:kv :processing-selected-image?] true))
                  (read-image-from-file value)
                  (pp/commit! (-> app-db
                                  (assoc-in [:kv :processing-selected-image?] false)
                                  (assoc-in [:kv :image-upload-preview] value))))))
    :crop (pipeline! [value app-db]
            (when value
              (pipeline! [value app-db]
                (pp/commit! (assoc-in app-db [:kv :image-upload-preview] value)))))
    :remove (pipeline! [value app-db]
              (pp/commit! (assoc-in app-db [:kv :image-upload-preview] nil)))
    :commit (pipeline! [value app-db]
              (when value
                (pipeline! [value app-db]
                  (pp/commit! (commit-image-to-form app-db value))
                  (pp/redirect! (dissoc (get-in app-db [:route :data]) :popup)))))}))
