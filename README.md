
Debounced input demo
----

A demo in `core.async` of debounced input in a Respo component.

Demo http://repo.respo.site/debounced-input-demo

This is the code briefly showing how it works:

```clojure
; initial states and update function, notice `chan` is nil at first
(defn init-state [& args] {:chan nil, :text ""})
(defn update-state [state new-state] (merge state new-state))

(def time-gap 500)

; this function is called by `on-input` to create the channel
(defn setup-chan! [text mutate!]
  ; initial channel created
  (let [the-chan (chan)
        timeout-ch (timeout time-gap)]
    ; Respo function to set component state, this is a mutation
    (mutate! {:chan the-chan})
    ; anonymous recursion, see `recur` below
    (go-loop
     ; pass the text and timeout channel as parameters
     [current-text text time-ch timeout-ch]
     ; now pick data from channels and check
     (let [[v c] (alts! [the-chan time-ch])]
       (if (= c time-ch)
         (do
          ; so timeout now, get rid of the channel, set text
          (close! the-chan)
          (mutate! {:chan nil, :text current-text}))
         (if (nil? v)
          ; a closing channel may send nil, but doesn't matter here
          nil
          ; if typing it fast, continue with the recusion
          (if (= v current-text)
              (recur v time-ch)
              (recur v (timeout time-gap)))))))
    ; pass it to `on-input`
    the-chan))

(defn on-input [state mutate!]
  (fn [e dispatch!]
    (let [text (:text state)
          input-chan (:chan state)
          ; by default, there is no channel, create one
          the-chan (if (some? input-chan)
                       input-chan
                       (setup-chan! text mutate!))]
      ; and put `input.value` into the channel
      (go (>! the-chan (:value e))))))
```

### Develop

Workflow https://github.com/mvc-works/stack-workflow

### License

MIT
