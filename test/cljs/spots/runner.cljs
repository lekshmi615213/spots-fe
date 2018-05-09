(ns spots.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [spots.core-test]))

(doo-tests 'spots.core-test)
