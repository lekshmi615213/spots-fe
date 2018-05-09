(ns spots.domain.modal-content
  (:require [spots.ui.components.modals :refer [link-info]]))

(def delivery-content
  [:div
   [:p.mb0 "Local hand delivery for orders of 24 dozen or less: $16"]
   [:p.mt0 "For orders over 24 dozen, local hand delivery: $18"]
   [:p "Delivery Windows:"
     [:br.mt0-5]
     "9:30AM-12:30PM (Monday-Saturday)"
     [:br]
     "12:30PM-3:30PM (Monday-Saturday)"]
   [:p "If you need your order delivered at a specific time, please email or call us and we will do our best to accommodate you. "]
   [:p.mb0 "T: " [:a {:href "tel:646-360-3109"} "(646)360-3109"]]
   [:p.mt0-5 "E: " [:a {:href "mailto:order@spotsnyc.com"} "order@spotsync.com"]]])

(def shipping-content
  [:div
   [:p.mb0 "For national shipments of orders 24 dozen or less: $18"]
   [:p.mt0 "For orders over 24 dozen, national shipping: $50"]
   [:p "If you need your shipment rushed, please email or call us and we will do our best to accommodate you. "]
   [:p.mb0 "T: " [:a {:href "tel:646-360-3109"} "(646)360-3109"]]
   [:p.mt0-5 "E: " [:a {:href "mailto:order@spotsnyc.com"} "order@spotsync.com"]]])

(def pickup-content
  [:div
   [:p.col-8 "Pickups are accepted Monday-Friday, for no additional fee. Pickup hours are between 9:30AM-5:30PM. If you have a conflict, please call or email us and we will do our best to accommodate you."]
   [:p.col-8 "We do not accept pickups on Saturday or Sunday."]
   [:p.col-8 "Pickup address: "
    [:a {:href "https://goo.gl/maps/Wt8XjTM9AwN2"
         :target "_blank"} "174 5th Avenue (between 22nd and 23rd) – Suite 405 – New York, NY 10010"]]
   [:p.mb0 "T: " [:a {:href "tel:646-360-3109"} "(646)360-3109"]]
   [:p.mt0-5 "E: " [:a {:href "mailto:order@spotsnyc.com"} "order@spotsync.com"]]])

(def pickup
  [:p.col-12 "Pickup "
    [:a.c-light-gray {:href "https://goo.gl/maps/Wt8XjTM9AwN2"
                      :target "_blank"} "174 5TH AVENUE"]])

(def multiple-content
  [:div 
   [:p "Sending multiple orders to multiple recipients? No problem!"
    [:br]
    "These types of orders require a little bit more TLC, please email or call us so we can accommodate your multi-recipient order."]
   [:p.mb0 "T: " [:a {:href "tel:646-360-3109"} "(646)360-3109"]]
   [:p.mt0-5 "E: " [:a {:href "mailto:order@spotsnyc.com"} "order@spotsync.com"]]])

(def multiple-recipients
  [link-info {:label "Multiple recipients"
              :modal-title "Multiple recipients" 
              :modal-content [:div 
                              [:p "Sending multiple orders to multiple recipients? No problem!"
                               [:br]
                               "These types of orders require a little bit more TLC, please email or call us so we can accommodate your multi-recipient order."]
                              [:p.mb0 "T: " [:a {:href "tel:646-360-3109"} "(646)360-3109"]]
                              [:p.mt0-5 "E: " [:a {:href "mailto:order@spotsnyc.com"} "order@spotsync.com"]]]
              :modal-footer "Close"}])

(def shipping-dates
  [:div
    [:p "We do not deliver shipments on Sunday or Monday. If you need an order shipped for Saturday delivery please give us a call -"
      [:a {:href "tel:646-360-3109"} "(646)360-3109"]
      ". An additional fee may apply."]])

(def delivery-dates
  [:div
    [:p "Delivery Windows:"
      [:br]
      "9:30-12:30"
      [:br]
      "12:30-3:30"]
    [:p "If your preferred delivery date is not available, please email us at "
      [:a {:href "mailto:order@spotsnyc.com"} "order@spotsnyc.com"]
      "We rarely say no to making the world a sweeter place and will do whatever we can to get you SPOTTED!"]
    [:p "Please note that we tend to run ahead of schedule and your order may be delivered earlier. In the rare case that we are running behind schedule, please be patient. Unfortunately we can not be held accountable for weather and traffic related delivery delays"]])

(def by-the-dozen
  [:div
   [:p "For every 12 cupcakes, you can choose up to 4 customized designs."
    [:br]
    "There is a $1 charge for each additional design."
    [:br]
    "If you order multiple packs of 12, each pack can have its own unique designs."]])
 
(def four-packs
  [:div
   [:p "Every 4-pack of cupcakes includes up to 4 customized designs."
    [:br]
    "If you order multiple packs of 4, each pack must have the same designs."]])

(def image-crop
  [:div
   [:p "To resize an image, click and drag the circle to reach the size you want. You can also click and drag the sides the preview image to select your desired area."]])
