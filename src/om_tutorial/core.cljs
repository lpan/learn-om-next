(ns om-tutorial.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

; It is surprising to see a JS-object-literal-like defui
(defui HelloWorld
  Object
  (render
    [this]
    (dom/div nil (get (om/props this) :title))))

; IMO om/factory is sort of like React.createElement
; it converts a 'UI' into a 'Component' that you can pass props/children in
; Note: dom/div and hello are 'Component', HelloWorld is an UI
(def hello (om/factory HelloWorld))

; Render one child
; (js/ReactDOM.render (hello {:title "LMAO"}) (gdom/getElement "app"))

; Render multile children
; (js/ReactDOM.render
;   (apply dom/div nil
;          (map #(hello {:react-key %
;                        :title (str "Hello" %)})
;               (range 5)))
;   (gdom/getElement "app"))


; Adding state
; The naive approach
(def app-state (atom {:count 0}))

(defui Counter
  Object
  (render
    [this]
    (let [{:keys [count]} (om/props this)]
      (dom/div
        nil
        (dom/span
          nil (str "Count: " count))
        (dom/button
          #js {:onClick
               (fn [e] (swap! app-state update-in [:count] inc))}
          "Click me!")))))

; reconciler reminds me combineReducer from Redux. We need to put the app-state
; atom into a reconciler so that om can do its magic.
(def reconciler
  (om/reconciler {:state app-state}))

; so for add-root! it seems that we do not need to transform our 'UI' into
; 'Component'. This also reminds me the `connect` HOC from react-redux as we are
; able to access `app-state` from Counter's props
; Another thing that is worth noting is that reconciler automatically deref the
; atom when pass its value as props to Counter
(om/add-root!
  reconciler
  Counter (gdom/getElement "app"))
