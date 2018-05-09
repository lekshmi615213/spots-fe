(ns spots.util.gql-api
  (:require [promesa.core :as p]
            [keechma.toolbox.ajax :refer [POST]]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [oops.core :refer [oget ocall]]))

(defn wrap-request [data]
  {:input {:order data}})

(def gql-endpoint
  (if js/goog.DEBUG 
    "http://0.0.0.0:3000/graphql"
    ;;"https://spotsnyc-order-staging.herokuapp.com/graphql"
    "https://spotsnyc-order-staging.herokuapp.com/graphql"))

(defn add-authentication-header [headers token]
  (if token
    (assoc headers :Authorization (str "jwt " token))
    headers))

(defn extract-gql-error [error]
  (.-data (get-in error [:payload])))

(defn gql-results-handler [unpack]
  (fn [{:keys [data errors]}]
    (if errors
      (throw (ex-info "GraphQLError" errors))
      (unpack data))))

(defn gql-req
  ([query-fn] (gql-req query-fn {} nil))
  ([query-fn variables] (gql-req query-fn variables nil))
  ([query-fn variables token]
   (let [{:keys [graphql unpack]} (query-fn variables)]
     (->> (POST gql-endpoint
                {:format :json
                 :params graphql
                 :response-format :json
                 :keywords? true
                 :headers (-> {}
                              (add-authentication-header token))})
          (p/map (gql-results-handler unpack))))))

(defn gql-loader [reqs]
  (map (fn [req]
         (when-let [params (:params req)]
           (let [{:keys [query-fn variables token]} params]
             (gql-req query-fn (or variables {}) token)))) 
       reqs))



(def ss-e {:data {:validate {:errors [{:index nil,
                                       :key "cupcakes",
                                       :messages nil,
                                       :suberrors [{:index 0,
                                                    :key nil,
                                                    :messages nil,
                                                    :suberrors [{:index nil,
                                                                 :key "colorId",
                                                                 :messages ["is missing"
                                                                            "no such color exists"],
                                                                 :suberrors nil}
                                                                {:index nil,
                                                                 :key "fontId",
                                                                 :messages ["is missing"
                                                                            "no such font exists"],
                                                                 :suberrors nil}
                                                                {:index nil,
                                                                 :key "firstLine",
                                                                 :messages ["is missing"],
                                                                 :suberrors nil}
                                                                {:index nil,
                                                                 :key "secondLine",
                                                                 :messages ["is missing"],
                                                                 :suberrors nil}
                                                                {:index nil,
                                                                 :key "thirdLine",
                                                                 :messages ["is missing"],
                                                                 :suberrors nil}]}]}],
                             :valid false}}})

(defn process-server-side-errors
  ([errors] (process-server-side-errors errors [] {}))
  ([errors path] (process-server-side-errors errors path {}))
  ([errors path formatted-errors]
   (if (seq errors)
     (reduce (fn [acc e]
               (let [path-part (or (:index e) (:key e))
                     full-path (conj path (if (string? path-part) (keyword path-part) path-part))]
                (process-server-side-errors (:suberrors e) full-path (assoc-in acc full-path (:messages e)))))
             formatted-errors errors)
     formatted-errors)))


(defn wrap-validation [req]
  (->> req
       (p/map (fn [res]
                (let [valid? (get-in res [:validate :valid])]
                  (if valid?
                    res
                    (throw (ex-info "Validation Failed" (process-server-side-errors (get-in res [:validate :errors]))))))))))
