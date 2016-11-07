
(ns debounced-input-demo.comp.widget
  (:require [respo.alias :refer [create-comp div input span]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]
            [cljs.core.async :refer [chan <! >! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn update-state [state] state)

(defn init-state [& args] nil)

(defn render []
  (fn [state mutate!]
    (div {} (input {:style ui/input}) (comp-space 16 nil) (comp-text state nil))))

(def comp-widget (create-comp :widget init-state update-state render))
