(ns newsflash.core
  (:require [clojure.java.shell :refer [sh]]
            [net.cgrand.enlive-html :as html]))

(def ^:dynamic *sources* [["https://news.ycombinator.com" [:td.title :a] "EN"]
                          ["http://www.meneame.net" [ :h2 :a] "ES"]
                          ["http://www.elmundo.es" [:article.noticia :header :h1 :a] "ES"]
                          ["http://www.expansion.com" [:article.noticia :header :h1 :a] "ES"]
                          ])

(def ^:dynamic *readers* [["Mac OS X" "ES" ["say" "-v" "Monica"]]
                          ["Mac OS X" "EN" ["say" "-v" "Samantha"]]])

(def ^:dynamic *history* (atom []))

(defn grab-headlines [[url path language]]
  "Grab headlines from a single source [url path language]"
  (into [] (map (comp first :content) (html/select (html/html-resource (java.net.URL. url)) path))))

(defn new? [obj]
  (when (not (some #{obj} @*history*))
    (swap! *history* conj obj)))

(defn read-aloud-headline [headline os language]
  (let [goodreader? (fn [[o l c]] (and (= o os) (= l language)))
        [o l command] (first (filter goodreader? *readers*))]
    (println (str "Reading " headline "..."))
    (apply sh (concat command [:in headline]))
    (sh "sleep" "1")))

(defn -main []
  (let [os (System/getProperty "os.name")]
    (reset! *history* (flatten (map grab-headlines *sources*))) ; Initial history creation
    (while true
      (sh "sleep" "30")
      (println "Checking for news...")
      (doseq [sc *sources*]
        (doseq [hd (grab-headlines sc)]
          (when (new? hd)
            (read-aloud-headline hd os (nth sc 2))))))))
