(ns spots.stylesheets.typography
  (:require [spots.stylesheets.colors :refer [colors]]
            [garden-basscss.vars :refer [vars]]
            [garden.stylesheet :refer [at-media]]))

(def breakpoints (:breakpoints @vars))

(defn stylesheet []
  [[:body {:font-family "\"Open Sans\", sans-serif"}]
   [:.a-small {:font-size "1rem"}]
   [:.a-text {:font-size "1.125rem"}]])
