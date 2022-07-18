(ns app.ui.question.detail
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.text.message :refer [labels]]
    [app.ui.question.comment :as comment]
    [app.text.message :refer [labels]]

    [steroid.rn.core :as srn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-vector-icons/Ionicons" :default Ionicons]))


(def comments [{:user_name "john" :content "hello"}
               {:user_name "sara" :content "hello, John"}
               {:user_name "peter" :content "what a nice day"}
               {:user_name "Lily" :content "so, what is the best there?"}])

(defn comment-view [h item]
  [nbase/vstack {:flex 1 :ml 2 :mt 1}
   [nbase/box {:justifyContent "flex-start" :alignItems "flex-start"}
    [nbase/box {:bg (theme/color "gray.300" "gray.500") :borderRadius "md" :p 6}]]
   [nbase/hstack {:flex 1 :mt 2}
    [nbase/vstack {:mr 2}
     [nbase/box  {:mb 2 :justifyContent "center" :alignItems "center"}
      [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (item :user_name)]]
     [nbase/box  {:justifyContent "center" :alignItems "center"}
      [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]
    [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (item :content)]]])

(defn answer-buttons [h model]
  [srn/view {:style {:height @h :margin-top 5 :margin-horizontal 20}}
   ; [srn/view {:style {:margin-bottom 20 :borderRadius 10 :backgroundColor "#eff6ff" :justifyContent "center" :alignItems "center"}}]
   [nbase/vstack {:mb 4 :borderRadius "full" :bg (theme/color "blue.50" "dark.300") :justifyContent "center" :alignItems "center"}
    [nbase/icon-button {:justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "caret-up-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]
    [text/measured-text {:fontSize 12 :color (theme/color "#2563eb" "#1e40af")} (str (get-in labels [:question :vote]) "  " (:agree_count model))]
    [nbase/icon-button {:justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "caret-down-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]]
   [nbase/vstack {:mt 2 :borderRadius "full" :bg (theme/color "blue.50" "dark.300") :justifyContent "center" :alignItems "center"}
   ; [srn/view {:style {:margin-top 10 :borderRadius 10 :backgroundColor "#eff6ff" :justifyContent "center" :alignItems "center"}}
    [nbase/icon-button {:mb 4 :justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "heart-outline"  :size "5" :color (theme/color "blue.600" "blue.800")}])}]
    [nbase/icon-button {:mb 4 :justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "star-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]
    [nbase/icon-button {:justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "chatbubble-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]]])

(defn detail-view []
  (let [h (reagent/atom 0)
        modal-open (reagent/atom false)
        is-open (reagent/atom false)]
    (fn []
      (let [question @(re-frame/subscribe [:question])
            answers @(re-frame/subscribe [:answers])]
        [ui/safe-area-consumer
         [nbase/box {:flex 1
                     :flex-direction "row"
                     :bg (theme/color "gray" "dark.100")
                     :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (reset! h height))}
          ; [nbase/box {:flex 1 :flex-direction "row" :style {:width "100%" :height @h}}]
          [nbase/scroll-view {:flex 1 :_contentContainerStyle {:flexGrow 1}
                              :horizontal true
                              :nestedScrollEnabled false
                              :scrollEventThrottle 16}
           [nbase/vstack {:m 1 :ml 2 :justifyContent "flex-start" :alignItems "flex-start"}
            [nbase/icon {:as Ionicons :name "help-circle"
                         :size "6" :color "indigo.500" :mb 6}]
            [srn/touchable-highlight {:underlayColor "#cccccc"
                                      :onPress (fn []
                                                 (re-frame/dispatch [:navigate-to :question-detail2]))}
             [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:question_content question)]]]
           (if-not (empty? (:question_detail question))
             [nbase/box {:ml 1 :mt 8}
              [editor/simple-view
               ;opts
               {:type :text}
               ; (:content question)
               (fn [] (:question_detail question))
               (fn [] nil)]])
             ;
           [nbase/divider {:orientation "vertical" :mx 2}]
           [nbase/flex {:m 1 :flex-direction "row" :bg (theme/color "white" "dark.100")}
            [nbase/vstack
             [nbase/box {:bg (theme/color "gray.300" "gray.500")
                         :borderRadius "md"
                         :p 4
                         :alignSelf "center"}]
             [nbase/box {:alignSelf "center"
                         :justifyContent "center"
                         :mt 4}
              [nbase/hstack {:bg (theme/color "white" "dark.100")}
               [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48) } (:user_name question)]
               [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]]
            ; [nbase/box {:m 1 :ml 2 :mt 12
            ;             :bg (theme/color "white" "dark.100")}
            ;  ;; width 4 + 4 + 4   ()  *  4  = 48
            ;  [text/multi-line-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (:question_detail question)]]
            [nbase/box {:m 1 :ml 2 :mt 8
                        :bg (theme/color "white" "dark.100")}
             ;; width 4 + 4 + 4   ()  *  4  = 48
             [editor/simple-view
              ;opts
              {:type :text}
              ; (:content question)
              (fn [] (:question_detail question))
                ; (str "<h2>abc</h2><p></p>" (:question_detail question)))
              ; "<p>abc</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>xxxx</p>"
              ;tap-fn
              (fn [] (js/console.log "text on tap >>> question-detail"))]]]
           [answer-buttons h question]

           (if (< 0 (count comments))
             [comment-view h (first comments)])
           (if (< 1 (count comments))
             [comment-view h (second comments)])

           ; [srn/touchable-opacity {:on-press (fn [] (reset! modal-open true))}]
           [srn/touchable-opacity {:on-press (fn [] (j/call @modal-open :open)
                                                    (reset! is-open true))
                                   :style {:justifyContent "center" :marginHorizontal 10 :paddingBottom 20}}
            [text/measured-text {:fontSize 14 :color "#60a5fa"} (get-in labels [:question :all-answer-comments])]]
           [nbase/divider {:orientation "vertical" :mx 2}]]
          ;; in zstack flow next answer button
          [comment/list-view modal-open is-open]
          [nbase/box {:right 4
                      :bottom 2
                      :position "absolute"}
           [nbase/icon-button {:w 10 :h 10 :borderRadius "full" :variant "outline" :colorScheme "coolGray"
                               :justifyContent "center" :alignSelf "center" :alignItems "center"
                               :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward-outline"}])
                               :onPress (fn [e]
                                          (js/console.log "icon-button on press"))}]]]]))))

(defn detail-view2 []
  (let [h (reagent/atom 0)]
    (fn []
      [ui/safe-area-consumer
       [nbase/flex {:flex 1
                    :m 1
                    :flex-direction "row"
                    :bg "gray"
                    :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                  (reset! h height))}

        [nbase/flex {:flex-direction "row" :bg "white"}
         [nbase/vstack
          [nbase/box {:bg "gray.300"
                      :borderRadius "md"
                      :p 6
                      :alignSelf "center"}]
          [nbase/box {:alignSelf "center"
                      :justifyContent "center"
                      :mt 4}
           [nbase/hstack
            ; [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 68)} (:user_name question)]
            [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]]]
         ; [nbase/hstack {:mt 16 :ml 1}
         ;  [text/measured-text {:fontSize 22 :color "#002851" :width (- @h 68)} (question :question_content)]
         ;  [nbase/box {:ml 1}
         ;   [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 68)} (question :question_detail)]]]]
        [nbase/divider {:orientation "vertical" :mx 2}]
        [nbase/flex {:flex-direction "row" :bg "white" :ml 2}
         [nbase/box {:p 5 :bg "indigo.300"}]]]])))


(def question-detail
  {:name       :question-detail
   :component  detail-view
   :options
   {:title ""
    :headerShown true}})

(def question-detail2
  {:name       :question-detail2
   :component  detail-view2
   :options
   {:title ""
    :headerShown true}})