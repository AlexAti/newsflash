(ns newsflash.core
  (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *sources* [["https://news.ycombinator.com" [:td.title :a] "EN"]])

(defn grab-headlines [[url path language]]
  "Grab headlines from a single source [url path language]"
  (into [] (map (comp first :content) (html/select (html/html-resource (java.net.URL. url)) path))))

(defn -main []
  (println (grab-headlines (first *sources*))))
