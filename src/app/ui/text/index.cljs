(ns app.ui.text.index
  (:require
   [app.ui.nativebase :as nbase]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [re-frame.core :as re-frame]
   [app.handler.gesture :as gesture]
   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-svg" :as svg]))

(defn text-input [atomic params]
  (let [{:keys [name props]} params
        [theme-props text-props] (nbase/theme-props name props)
        t-props text-props
        padding-value (:padding t-props)
        text-props (assoc (select-keys text-props [:fontSize :color]) :fontFamily "MongolianBaiZheng")

        info @(re-frame/subscribe [:editor-info])
        etext @(re-frame/subscribe [:editor-text])
        line-height @(re-frame/subscribe [:editor-line-height])
        [x y] @(re-frame/subscribe [:editor-selection-xy])]
    (when (false? (:flag @atomic))
      (swap! atomic assoc :flag true)
      (re-frame/dispatch
       [:init-editor {:text (:text @atomic) :text-props text-props :padding padding-value}]))
    [gesture/tap-gesture-handler
     {:onHandlerStateChange #(do
                               (swap! atomic assoc :focus true)
                               (if (gesture/tap-state-end (j/get % :nativeEvent))
                                 (re-frame/dispatch [:cursor-location (j/get % :nativeEvent)])))}
     [gesture/pan-gesture-handler {:onGestureEvent #(re-frame/dispatch [:cursor-location (j/get % :nativeEvent)])}
      [nbase/box (merge theme-props (if (:focus @atomic) (:_focus theme-props)))
       [nbase/zstack
        [nbase/measured-text text-props etext info]
        (if (:focus @atomic)
          [nbase/box {:style {:margin-top y :margin-left x}}
           [:> blinkview {"useNativeDriver" false}
            [:> svg/Svg {:width line-height :height 2}
             [:> svg/Rect {:x "0" :y "0" :width line-height :height 2 :fill "blue"}]]]])]]]]))