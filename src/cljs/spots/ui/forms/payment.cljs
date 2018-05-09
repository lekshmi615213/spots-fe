(ns spots.ui.forms.payment
  (:require [keechma.ui-component :as ui]
            [clojure.string :as str]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [spots.ui.components.inputs :refer [controlled-input -input-group -label -button controlled-textarea radiobtn dropdown-input]]
            [spots.ui.components.decorators :refer [-title flavor-image flavor-image-with-message -dashed-divider -total]]
            [spots.ui.components.address-autocomplete :as aa]
            [reagent.core :as r]
            [spots.util.gql-api :as ga]
            [spots.domain.stripe :refer [stripe-elements]]
            [oops.core :refer [ocall oget]]
            [reagent.format :refer [currency-format]]
            [spots.ui.components.modals :refer [modal]]
            [spots.domain.form-helpers :refer [submit-button]]))

(def cc-styles
  (clj->js {}))

(defn stripe-cc [ctx form-props form-state helpers]
  (let [{:keys [set-value set-value-without-immediate-validation]} helpers
        cc-element-id (gensym "stripe-cc")
        card-instance-atom (atom nil)
        change-handler (fn [e]
                         (let [error-message (oget e "?error.?message")]
                           (set-value :cc-errors error-message)))]
    (r/create-class
     {:reagent-render (fn [ctx form-props form-state helpers]
                        (let [errors (forms-helpers/attr-get-in form-state :cc-errors)]
                          [:div
                           [:div.p1.mb1.border.bd-gray.c-gray
                            [:div {:id cc-element-id}]]
                           (when errors
                             [:div.mb1.c-red errors])]))
      :component-did-mount (fn [c _]
                             (let [el (ocall js/window "document.getElementById" cc-element-id)
                                   card (ocall stripe-elements "create" "card" cc-styles)]
                               (ocall card "addEventListener" "change" change-handler)
                               (ocall card "mount" el)
                               (reset! card-instance-atom card)
                               (js/setTimeout
                                #(set-value-without-immediate-validation :stripe-card card)
                                1)))
      :component-will-unmount (fn [_ _]
                                (when-let [card @card-instance-atom]
                                  (ocall card "destroy")
                                  (ocall card "removeEventListener" "change" change-handler)))})))

(def order-completed-content
  [:div 
   [:p "Your payment has been processed! Expect confirmation email soon!"]])


(defn render [ctx]
  (let [form-props       [:payment :form]
        form-state       @(forms-helpers/form-state ctx form-props)
        helpers          (forms-helpers/make-component-helpers ctx form-props)
        order-pricing    (sub> ctx :order-pricing)
        {:keys [submit]} helpers]
    (when form-state
      [:form {:on-submit submit} 
       [:div
        [-title "Bill"]
        (when order-pricing
          [:div
           [:div.col.col-5
            [:p (str "Cupcakes + Design: " (currency-format (:units order-pricing)))]
            [-dashed-divider]
            [:p (str "Additional Design: " (currency-format (:cupcakeDesigns order-pricing)))]
            [-dashed-divider]
            [:p (str "Gift Message: " (currency-format (:giftMessage order-pricing)))]
            [-dashed-divider]
            [:p (str "Rush Order: " (currency-format (:rushOrder order-pricing)))]
            [-dashed-divider]
            [:p (str "Shipping: " (currency-format (:shipping order-pricing)))]]
           
           [-total "Total: " (currency-format (:total order-pricing))]])

        [-title "Payment info"]
        [-input-group
         [-label "Cardholders Name:"]
         [controlled-input {:form-state form-state :placeholder "" :helpers helpers :attr :cardHoldersName}]]     
        
        [:div
         [stripe-cc ctx form-props form-state helpers]]
        [:div.flex.justify-center.mt2 [submit-button form-state "Complete Purchase"]] 
        (when (sub> ctx :show-paid-modal) 
          [modal {:title "Order completed" 
                  :content order-completed-content
                  :footer "Close"
                  :close-fn #(<cmd ctx [:modal :close-paid-modal] nil)}])]])))


(def component (ui/constructor {:renderer          render
                                :subscription-deps [:form-state :cupcake-types :clipart-url :clipart :order-pricing :show-paid-modal]
                                :topic             forms-core/id-key}))
