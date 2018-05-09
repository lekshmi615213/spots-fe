(ns spots.forms
  (:require [spots.forms.user-info :as user-info]
            [spots.forms.order-info :as order-info]
            [spots.forms.personalization :as personalization]
            [spots.forms.confirmation :as confirmation]
            [spots.forms.recipient :as recipient]
            [spots.forms.payment :as payment]
            [spots.forms.design.message :as design-message]))

(def forms
  {:user-info (user-info/constructor)
   :order-info (order-info/constructor)
   :personalization (personalization/constructor)
   :confirmation (confirmation/constructor)
   :recipient (recipient/constructor)
   :payment (payment/constructor)
   :design-message (design-message/constructor)})


(defn make-page-matches? [matching-page]
  (fn [{:keys [page]}]
    (when (= page matching-page)
      :form)))

(def forms-params
  {:user-info       (make-page-matches? "user-info")
   :order-info      (make-page-matches? "order-info")
   :recipient       (make-page-matches? "recipient") 
   :personalization (make-page-matches? "personalization")
   :confirmation    (make-page-matches? "confirmation")
   :design-message  (fn [route]
                      (when (= "message" (get-in route [:popup :type]))
                        (get-in route [:popup :id])))})

