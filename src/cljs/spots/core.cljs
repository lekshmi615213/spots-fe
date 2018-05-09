(ns spots.core 
  (:require [reagent.core :as reagent]
            [keechma.app-state :as app-state]
            [spots.controllers :refer [controllers]]
            [spots.ui :refer [ui]]
            [spots.subscriptions :refer [subscriptions]]
            [spots.stylesheets.core :refer [stylesheet]]
            [keechma.toolbox.css.core :refer [update-page-css]]))

(def app-definition
  {:components    ui
   :controllers   controllers
   :subscriptions subscriptions
   :html-element  (.getElementById js/document "app")
   :routes [["" {:page "user-info"}]
            ":page"]})

(defonce running-app (clojure.core/atom nil))

(defn start-app! []
  (reset! running-app (app-state/start! app-definition))
  (update-page-css (stylesheet)))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn reload []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(defn ^:export main []
  (dev-setup)
  (start-app!))
