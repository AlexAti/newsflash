(ns newsflash.core
  (:require [net.cgrand.enlive-html :as html]))

(defn -main []
  (println (html/select (html/html-resource (java.net.URL. "http://google.com")) [:title])))
