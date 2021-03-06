(defproject spots "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.8.0-alpha2" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/create-react-class cljsjs/react-dom-server]]
                 [keechma "0.3.1" :exclusions [cljsjs/react-with-addons]]
                 [keechma/toolbox "0.1.5"]
                 [keechma/entitydb "0.1.4"]
                 [org.clojars.mihaelkonjevic/garden-basscss "0.2.1"]
                 [garden "1.3.2"]
                 [cljsjs/moment "2.17.1-1"]
                 [cljsjs/pikaday "1.5.1-2"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                 [floatingpointio/graphql-builder "0.1.4"]
                 [binaryage/oops "0.5.6"]
                 [reagent-utils "0.2.1"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]}



  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:dev
   {:dependencies [
                   [figwheel-sidecar "0.5.10"]
                   [com.cemerick/piggieback "0.2.1"]
                   [binaryage/devtools "0.8.2"]]

    :plugins      [[lein-figwheel "0.5.14"]
                   [lein-doo "0.1.7"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "spots.core/reload"}
     :compiler     {:main                 spots.core
                    :optimizations        :none
                    :output-to            "resources/public/js/app.js"
                    :output-dir           "resources/public/js/dev"
                    :asset-path           "js/dev"
                    :source-map-timestamp true
                    :install-deps true
                    :npm-deps {:react "15.6.2"
                               :react-dom "15.6.2"
                               :create-react-class "15.6.2"
                               :react-avatar-edit "0.5.9"
                               :cropperjs "1.2.2"}
                    :preloads             [devtools.preload]
                    :external-config
                    {:devtools/config
                     {:features-to-install    [:formatters :hints]
                      :fn-symbol              "F"
                      :print-config-overrides true}}}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            spots.core
                    :optimizations   :simple
                    :output-to       "resources/public/js/app.js"
                    :output-dir      "resources/public/js/min"
                    :elide-asserts   true
                    :install-deps true
                    :npm-deps {:react "15.6.2"
                               :react-dom "15.6.2"
                               :create-react-class "15.6.2"
                               :react-avatar-edit "0.5.9"
                               :cropperjs "1.2.2"}
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/test.js"
                    :output-dir    "resources/public/js/test"
                    :main          spots.runner
                    :optimizations :none}}
    ]})
