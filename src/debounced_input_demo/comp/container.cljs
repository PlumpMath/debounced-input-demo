
(ns debounced-input-demo.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [debounced-input-demo.comp.widget :refer [comp-widget]]))

(defn render [store ssr-stages]
  (fn [state mutate!] (div {:style (merge ui/global {:padding 16})} (comp-widget))))

(def comp-container (create-comp :container render))
