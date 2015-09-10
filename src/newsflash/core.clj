(ns newsflash.core
  (:require [clojure.java.shell :refer [sh]]
            [net.cgrand.enlive-html :as html]))

(def ^:dynamic *sources* [["https://news.ycombinator.com" [:td.title :a] "EN"]])

(def ^:dynamic *readers* [["Mac OS X" "ES" ["say" "-v" "Monica"]]
                          ["Mac OS X" "EN" ["say" "-v" "Samantha"]]])

(defn grab-headlines [[url path language]]
  "Grab headlines from a single source [url path language]"
  (into [] (map (comp first :content) (html/select (html/html-resource (java.net.URL. url)) path))))

(defn read-aloud-headline [headline os language]
  (let [goodreader? (fn [[o l c]] (and (= o os) (= l language)))
        [o l command] (first (filter goodreader? *readers*))]
    (println (str "Reading " headline "..."))
    (apply sh (concat command [:in headline]))
    (sh "sleep" "1")))

(defn -main []
  (let [os (System/getProperty "os.name")]
    (doseq [hd (grab-headlines (first *sources*))]
      (read-aloud-headline hd os "EN"))))
