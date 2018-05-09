(ns spots.ui.components.svg-preview
  (:require [spots.ui.components.message-svg :refer [flat-text curved-text single-line-m single-line-s single-line-l]]))

(def svg-default-params 
  {:curved false
   :first-line ""
   :second-line ""
   :third-line ""
   :font-family "Verdana"
   :color "#000000"})

(defn render-one-line-message [params]
  (let [{:keys [text-size second-line font-family color]} params]
    (case text-size
          "small" [single-line-s second-line font-family color]
          "medium" [single-line-m second-line font-family color]
          "large" [single-line-l second-line font-family color])))

(defn render-three-line-message [params]
  (let [{:keys [curved first-line second-line third-line font-family color]} params]
    (if curved
      [curved-text first-line second-line third-line font-family color]
      [flat-text first-line second-line third-line font-family color])))

(defn render [svg-params]
  (let [new-params (merge svg-default-params svg-params)
        {:keys [three-line?]} new-params]
    (if three-line?
      (render-three-line-message new-params)
      (render-one-line-message new-params))))
