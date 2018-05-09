(ns spots.ui
  (:require [spots.ui.main :as main]
            [spots.ui.forms.user-info :as forms-user-info]
            [spots.ui.forms.order-info :as forms-order-info]
            [spots.ui.forms.personalization :as forms-personalization]
            [spots.ui.forms.confirmation :as forms-confirmation]
            [spots.ui.forms.recipient :as forms-recipient]
            [spots.ui.forms.payment :as forms-payment]
            [spots.ui.components.image-upload :as image-upload]
            [spots.ui.components.clipart :as clipart]
            [spots.ui.components.message :as message]
            [spots.ui.components.design-modals :as design-modals]))

(def ui
  {:main                  main/component
   :forms-user-info       forms-user-info/component
   :forms-order-info      forms-order-info/component
   :forms-personalization forms-personalization/component
   :forms-confirmation    forms-confirmation/component
   :forms-recipient       forms-recipient/component
   :forms-payment         forms-payment/component
   :image-upload          image-upload/component
   :clipart               clipart/component
   :message               message/component
   :design-modals         design-modals/component})
