
(ns debounced-input-demo.comp.widget
  (:require [respo.alias :refer [create-comp div input span a]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]
            [respo-ui.style :as ui]
            [cljs.core.async :refer [chan <! >! timeout close! alts!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def time-gap 500)

(defn setup-chan! [text mutate!]
  (let [the-chan (chan), timeout-ch (timeout time-gap)]
    (println "created chan.")
    (mutate! {:chan the-chan})
    (go-loop
     [current-text text time-ch timeout-ch]
     (println "waiting for a value")
     (let [[v c] (alts! [the-chan time-ch])]
       (if (= c time-ch)
         (do
          (println "timeout, destrying all")
          (close! the-chan)
          (mutate! {:chan nil, :text current-text}))
         (if (nil? v)
           (do (println "closed, nil") nil)
           (do
            (println "new value" (pr-str v) (pr-str current-text))
            (if (= v current-text) (recur v time-ch) (recur v (timeout time-gap))))))))
    the-chan))

(defn on-input [state mutate!]
  (println "render event")
  (fn [e dispatch!]
    (let [text (:text state)
          input-chan (:chan state)
          the-chan (if (some? input-chan) input-chan (setup-chan! text mutate!))]
      (go (println "putting new value" (pr-str (:value e))) (>! the-chan (:value e))))))

(defn update-state [state new-state] (merge state new-state))

(defn init-state [& args] {:chan nil, :text ""})

(defn render []
  (fn [state mutate!]
    (div
     {}
     (div
      {}
      (comp-text "This is a demo of a debounced input box." nil)
      (comp-space 8 nil)
      (a
       {:attrs {:inner-text "Find more on GitHub",
                :target "_black",
                :href "https://github.com/Respo/debounced-input-demo"}}))
     (div
      {}
      (input
       {:style (merge ui/input {:width 400}),
        :event {:input (on-input state mutate!)},
        :attrs {:placeholder "Type here..."}})
      (comp-space 16 nil)
      (if (some? (:chan state)) (comp-text "Typing. The gap is 500ms." nil)))
     (div {} (comp-text (:text state) nil)))))

(def comp-widget (create-comp :widget init-state update-state render))
