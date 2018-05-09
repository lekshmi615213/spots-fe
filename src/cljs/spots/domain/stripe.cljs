(ns spots.domain.stripe
  (:require [oops.core :refer [oget ocall]]))

(def Stripe (oget js/window "Stripe"))

(def stripe-client (Stripe "pk_test_10XG1kA381lKP4snJ9OGm8S8"))

(def stripe-elements (ocall stripe-client "elements"))
