(ns spots.stylesheets.btn
  (:require [garden.stylesheet :refer [at-media]]
            [garden-basscss.vars :refer [vars]]))

(def breakpoints (:breakpoints @vars))

(defn stylesheet []
  [:.btn {:-moz-user-select "none"
          :-ms-touch-action "manipulation"
          :-ms-user-select "none"
          :-webkit-user-select "none"
          :-webkit-appearance "none"   
          :background-image "none"
          :cursor "pointer"
          :font-family "inherit"
          :font-size "1.125rem"
          :font-weight 400
          :height "58px"
          :letter-spacing ".03em"
          :line-height "18px"
          :touch-action "manipulation"
          :transition "all .2s ease-in-out"
          :user-select "none"
          :vertical-align "middle"
          :white-space "nowrap"}
   (at-media (:xs breakpoints)
             [:& {:width "100%"
                  :padding-left "0.5rem"
                  :padding-right "0.5rem"}])
   [:&.sz-xs {:min-width "140px"}]
   [:&.sz-small {:min-width "227px"}]
   [:&.sz-normal {:min-width "335px"}]
   [:&.sz-large {:min-width "400px"}]
   [:&.sz-100 {:width "100%"}]
   [:&:focus {:outline 'none}]
   [:&:active {:outline 'none}]])
