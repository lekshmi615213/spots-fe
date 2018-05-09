(ns spots.stylesheets.datepicker
  (:require [garden.stylesheet :refer [at-media]]
            [garden-basscss.vars :refer [vars]]))

(def breakpoints (:breakpoints @vars))

(defn stylesheet []
  [[:.pika-single {:z-index 999
                    :display "block"
                    :position "relative"
                    :color "#333"
                    :background-color "#fff"
                    :border-width "1px"
                    :border-style "solid"
                    :border-color "#ccc"
                    :*zoom 1}
     [:&:before {:content "''"
                 :display "table"}]
     [:&:after {:content "''"
                :display "table"
                :clear "both"}]
     [:&.is-hidden {:display "none"}]
     [:&.is-bound {:position "absolute"
                   :box-shadow "0 5px 15px -5px rgba(0,0,0,.5)"}]]
   [:.pika-lendar {:float "left"
                   :width "240px"
                   :margin "8px"}]
   [:.pika-title {:position "relative"
                  :text-align "center"}
    [:select [:cursor "pointer"
              :position "absolute"
              :z-index 998
              :margin 0
              :left 0
              :top "5px"
              :opacity 0]]]
   [:.pika-label {:display "inline-block"
                  :*display "inline"
                  :position "relative"
                  :z-index 999
                  :overflow "hidden"
                  :margin 0
                  :padding "5px 3px"
                  :line-height "20px"
                  :font-size "14px"
                  :font-weight "500"
                  :background-color "#fff"}]
   [:.pika-prev :.pika-next {:display "block"
                             :cursor "pointer"
                             :position "relative"
                             :outline "none"
                             :border 0
                             :padding 0
                             :width "20px"
                             :height "30px"
                             :text-indent "20px"
                             :white-space "nowrap"
                             :overflow "hidden"
                             :background-color "transparent"
                             :background-position "center center"
                             :background-repeat "no-repeat"
                             :background-size "75% 75%"
                             :opacity 0.5
                             :*position "absolute"
                             :*top 0}
    [:&:hover {:opacity 1}]]
   [:.pika-prev {:float "left"
                 :background-image "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAeCAYAAAAsEj5rAAAAUklEQVR42u3VMQoAIBADQf8Pgj+OD9hG2CtONJB2ymQkKe0HbwAP0xucDiQWARITIDEBEnMgMQ8S8+AqBIl6kKgHiXqQqAeJepBo/z38J/U0uAHlaBkBl9I4GwAAAABJRU5ErkJggg==')"
                 :*left 0}]
   [:.is-rtl [:.pika-next {:float "left"
                           :background-image "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAeCAYAAAAsEj5rAAAAUklEQVR42u3VMQoAIBADQf8Pgj+OD9hG2CtONJB2ymQkKe0HbwAP0xucDiQWARITIDEBEnMgMQ8S8+AqBIl6kKgHiXqQqAeJepBo/z38J/U0uAHlaBkBl9I4GwAAAABJRU5ErkJggg==')"
                           :*left 0}]]
   [:.pika-next {:float "right"
                 :background-image "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAeCAYAAAAsEj5rAAAAU0lEQVR42u3VOwoAMAgE0dwfAnNjU26bYkBCFGwfiL9VVWoO+BJ4Gf3gtsEKKoFBNTCoCAYVwaAiGNQGMUHMkjGbgjk2mIONuXo0nC8XnCf1JXgArVIZAQh5TKYAAAAASUVORK5CYII=')"
                :*right 0}]
   [:.is-rtl [:.pika-prev {:float "right"
                           :background-image "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAeCAYAAAAsEj5rAAAAU0lEQVR42u3VOwoAMAgE0dwfAnNjU26bYkBCFGwfiL9VVWoO+BJ4Gf3gtsEKKoFBNTCoCAYVwaAiGNQGMUHMkjGbgjk2mIONuXo0nC8XnCf1JXgArVIZAQh5TKYAAAAASUVORK5CYII=')"
                         :*right 0}]]
   [:.pika-prev [:&.is-disabled {:cursor "default"
                                 :opacity 0.2}]]
   [:.pika-next [:&.is-disabled {:cursor "default"
                                 :opacity 0.2}]]
   [:.pika-select {:display "inline-block"
                   :*display "inline"}]
   [:.pika-table {:width "100%"
                  :border-collapse "collapse"
                  :border-spacing 0
                  :border 0}
    [:th :td {:width "14.285714285714286%"
              :padding 0}]
    [:th {:color "#999"
          :font-size "12px"
          :line-height "25px"
          :font-weight "500"
          :text-align "center"}]]
   [:.pika-button {:cursor "pointer"
                   :display "block"
                   :box-sizing "border-box"
                   :-moz-box-sizing "border-box"
                   :outline "none"
                   :border 0
                   :margin 0
                   :width "100%"
                   :padding "5px"
                   :color "#666"
                   :font-size "12px"
                   :line-height "15px"
                   :text-align "right"
                   :background "#f5f5f5"}]
   [:.pika-weak {:font-size 11
                 :color "#999"}]
   [:.is-today [:&.pika-button {:color "#33aaff"
                                :font-weight "500"}]]
   [:.is-selected [:&.pika-button {:color "#fff"
                                   :font-weigth "500"
                                   :background-color "#33aaff"
                                   :box-shadow "inset 0 1px 3px #178fe5"
                                   :border-radius "3px"}]]
   [:.has-event [:&.pika-button {:color "#fff"
                                :font-weigth "500"
                                :background-color "#005da9"
                                :box-shadow "inset 0 1px 3px #0076c9"
                                :border-radius "3px"}]]
   [:.is-disabled [:&.pika-button {:background-color "#D5E9F7"
                                   :pointer-events "none"
                                   :cursor "default"
                                   :color "#999"
                                   :opacity 0.3}]]
   [:.is-inrange [:&.pika-button {:background-color "#D5E9F7"}]]
   [:.is-startrange [:&.pika-button {:color "#fff"
                                     :background-color "#6CB31D"
                                     :box-shadow "none"
                                     :border-radius "3px"}]]
   [:.is-endrange [:&.pika-button {:color "#fff"
                                   :background-color "#33aaff"
                                   :box-shadow "none"
                                   :border-radius "3px"}]]
   [:.is-outside-current-month [:&.pika-button {:color "#999"
                                                :opacity 0.3}]]
   [:.is-selection-disabled {:pointer-events "none"
                             :cursor "default"}]
   [:pika-button [:&:hover {:color "#fff"
                            :background-color "#ff800"
                            :box-shadow "none"
                            :border-radius "3px"}]]
   [:pika-row [:&.pick-whole-week
               [:& :hover 
                [:&.pika-button {:color "#fff"
                                 :background-color "#ff800"
                                 :box-shadow "none"
                                 :border-radius "3px"}]]]]
   [:pika-table [:&abbr {:border-bottom "none"
                         :cursor "help"}]]])


