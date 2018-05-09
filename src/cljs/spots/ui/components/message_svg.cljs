(ns spots.ui.components.message-svg)

(defn curved-text [first-line second-line third-line font-family color]
  [:svg
   {:version "1", :height "250", :width "250"}
   [:path
    {:d
     "M125 5c32.1 0 62.2 12.5 84.9 35.1S245 92.9 245 125s-12.5 62.2-35.1 84.9S157.1 245 125 245s-62.2-12.5-84.9-35.1S5 157.1 5 125s12.5-62.2 35.1-84.9S92.9 5 125 5m0-5C56 0 0 56 0 125s56 125 125 125 125-56 125-125S194 0 125 0z"}]
   [:path#a {:d "M0 135.8h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "32", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#a", :startOffset "50%"} second-line]]
   [:path#b
    {:d    "M29.1 125c0 53 43 95.9 95.9 95.9 53 0 95.9-43 95.9-95.9",
     :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "32", :font-family font-family, :fill color, :letter-spacing "4"}
    [:textPath {:xlinkHref "#b", :startOffset "50%"} third-line]]
   [:path#c
    {:d    "M46.4 125c0-43.4 35.2-78.6 78.6-78.6s78.6 35.2 78.6 78.6",
     :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "32", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#c", :startOffset "50%"} first-line]]])


(defn flat-text [first-line second-line third-line font-family color]
  [:svg
   {:version "1", :height "250", :width "250"}
   [:path
    {:d
     "M125 5c32.1 0 62.2 12.5 84.9 35.1S245 92.9 245 125s-12.5 62.2-35.1 84.9S157.1 245 125 245s-62.2-12.5-84.9-35.1S5 157.1 5 125s12.5-62.2 35.1-84.9S92.9 5 125 5m0-5C56 0 0 56 0 125s56 125 125 125 125-56 125-125S194 0 125 0z"}]
   [:path#a {:d "M0 135.8h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "32", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#a", :startOffset "50%"} second-line]]
   [:path#b {:d "M0 73.8h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "32", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#b", :startOffset "50%"} first-line]]
   [:path#c {:d "M0 193.8h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "32", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#c", :startOffset "50%"} third-line]]])

(defn single-line-s [line font-family color]
  [:svg
   {:version "1", :height "250", :width "250", :data-size "s"}
   [:path
    {:d
     "M125 5c32.1 0 62.2 12.5 84.9 35.1S245 92.9 245 125s-12.5 62.2-35.1 84.9S157.1 245 125 245s-62.2-12.5-84.9-35.1S5 157.1 5 125s12.5-62.2 35.1-84.9S92.9 5 125 5m0-5C56 0 0 56 0 125s56 125 125 125 125-56 125-125S194 0 125 0z"}]
   [:path#a {:d "M0 140.8h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "42", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#a", :startOffset "50%"} line]]])


(defn single-line-m [line font-family color]
  [:svg
   {:version "1", :height "250", :width "250", :data-size "m"}
   [:path
    {:d
     "M125 5c32.1 0 62.2 12.5 84.9 35.1S245 92.9 245 125s-12.5 62.2-35.1 84.9S157.1 245 125 245s-62.2-12.5-84.9-35.1S5 157.1 5 125s12.5-62.2 35.1-84.9S92.9 5 125 5m0-5C56 0 0 56 0 125s56 125 125 125 125-56 125-125S194 0 125 0z"}]
   [:path#a {:d "M0 157.5h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "90", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#a", :startOffset "50%"} line]]])


(defn single-line-l [line font-family color]
  [:svg
   {:version "1", :height "250", :width "250", :data-size "l"}
   [:path
    {:d
     "M125 5c32.1 0 62.2 12.5 84.9 35.1S245 92.9 245 125s-12.5 62.2-35.1 84.9S157.1 245 125 245s-62.2-12.5-84.9-35.1S5 157.1 5 125s12.5-62.2 35.1-84.9S92.9 5 125 5m0-5C56 0 0 56 0 125s56 125 125 125 125-56 125-125S194 0 125 0z"}]
   [:path#a {:d "M0 162.5h250", :fill "none"}]
   [:text
    {:text-anchor "middle", :font-size "120", :font-family font-family, :fill color}
    [:textPath {:xlinkHref "#a", :startOffset "50%"} line]]])







