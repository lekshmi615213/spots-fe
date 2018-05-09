(ns spots.stylesheets.core
  (:require [garden-basscss.core :as core]
            [garden-basscss.vars :refer [vars]]
            [spots.stylesheets.colors :as colors]
            [spots.stylesheets.btn :as btn]
            [spots.stylesheets.datepicker :as datepicker]
            [garden.core :as garden]
            [spots.stylesheets.grid :as grid]
            [garden.units :refer [em rem px px-]]
            [garden.stylesheet :refer [at-media]]
            [spots.stylesheets.colors :refer [colors-with-variations]]
            [spots.stylesheets.typography :as typography]
            [keechma.toolbox.css.core :as toolbox-css]
            [clojure.string :refer [split]])
  (:require-macros [garden.def :refer [defkeyframes]]))

(def min-width-for-full-layout (px 1400))

(def breakpoints (:breakpoints @vars))

(defkeyframes spin
  [:100% {:transform "rotate(-360deg)"}])

(swap! vars assoc-in [:typography :bold-font-weight] 700)
(swap! vars assoc-in [:typography :caps-letter-spacing] (em 0.1))

(swap! vars assoc :spaces {0 0
                           1 (rem 1)
                           2 (rem 2)
                           3 (rem 3)
                           4 (rem 4)
                           5 (rem 5)
                           6 (rem 6)
                           "0-5" (rem 0.5)
                           "1-5" (rem 1.5)
                           "2-5" (rem 2.5)
                           "3-5" (rem 3.5)
                           "4-5" (rem 4.5)
                           "5-5" (rem 5.5)
                           "6-5" (rem 6.5)})

(def aspect-ratios {:nine-ten "9:10"
                    :one-one "1:1"
                    :two-one "2:1"})

(defn gen-aspect-ratios [ratios]
  (map (fn [[aspect-ratio-name value]] 
         (let [aspect-ratio-name (name aspect-ratio-name)
               [width height] (split value #":")]
           [[(str ".ratio-" aspect-ratio-name)
             [:& {:position "relative"
                  :overflow "hidden"}
              [:&:before {:display "block"
                          :content "''"
                          :width "100%"
                          :padding-bottom (str (* (/ height width) 100) "%")}]
              [:>.content {:position "absolute"
                           :top 0
                           :bottom 0
                           :left 0
                           :right 0}]
              [:>img {:position "absolute"
                      :top 0
                      :bottom 0
                      :left 0
                      :right 0
                      :margin "auto"
                      :width "100%"
                      :height "100%"}]]]])) ratios))

(defn stylesheet []
  [[:* {:box-sizing 'border-box}]
   [:html {:font-size "100%"
           :color "#808181"}]
   [:body {:margin 0
           :height "100%"
           :font-family "-apple-system, BlinkMacSystemFont, Roboto, Oxygen, Ubuntu, Cantarell, “Fira Sans”, “Droid Sans”, “Helvetica Neue”, Arial, sans-serif"
           :text-rendering "optimizeLegibility"
           :-webkit-font-smoothing "antialiased"
           :-moz-osx-font-smoothing "grayscale"}]
   [:form {:width "100%"}]
   [:img {:max-width "100%"}]
   [:svg {:max-width "100%"}]
   [:table {:width "100%"
            :border-spacing 0}]
   [:.cursor-pointer {:cursor 'pointer}] 
   [:.pill {:border-radius "999em"}]
   [:.w-100 {:width "100%"}]
   [:.pac-container {:z-index 999999999}]
   [:a {:color "#86D6F6"}]
   [:input::placeholder {:color "#bbbbbb"}]
   (core/stylesheet)
   (btn/stylesheet)
   (colors/stylesheet)
   (typography/stylesheet)
   (grid/stylesheet)
   (datepicker/stylesheet)
   
   (gen-aspect-ratios aspect-ratios)
   
   @toolbox-css/component-styles
   spin
   [:.spin {:animation [[spin "1.5s" :linear :infinite]]}]])
