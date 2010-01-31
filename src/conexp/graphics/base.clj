(ns conexp.graphics.base
  (:use [conexp.util :only (update-ns-meta!)]
	[conexp.base :only (defvar-)]
	[conexp.layout.util :only (edges-of-points)]
	[clojure.contrib.ns-utils :only (immigrate)])
  (:import [javax.swing JFrame JButton JPanel JLabel]
	   [java.awt Dimension BorderLayout Color]
	   [no.geosoft.cc.graphics GWindow GScene GStyle]))

(immigrate 'conexp.graphics.util
	   'conexp.graphics.nodes-and-connections)

(update-ns-meta! conexp.graphics.base
  :doc "Basic namespace for drawing lattice.")


;;; draw nodes with coordinates and connections on a scene

(defvar- *default-scene-style* (doto (GStyle.)
				 (.setBackgroundColor Color/WHITE)
				 (.setAntialiased true))
  "Default GScene style.")

(defn draw-on-scene
  "Draws given layout on a GScene and returns it."
  [[points-to-coordinates point-connections]]
  (let [wnd (GWindow. Color/WHITE)
	scn (GScene. wnd),
	[x_min y_min x_max y_max] (edges-of-points (vals points-to-coordinates))]
    (doto scn
      (.setWorldExtent (double (- x_min 0.1)) (double (- y_min 0.1))
		       (double (+ x_max 0.1)) (double (+ y_max 0.1)))
      (.shouldZoomOnResize false)
      (.shouldWorldExtentFitViewport false)
      (.setStyle *default-scene-style*)
      (add-nodes-with-connections points-to-coordinates point-connections))
    (doto wnd
      (.startInteraction (move-interaction)))
    scn))


;;; get layout and diagram from scene

(defn get-diagram-from-scene
  "Returns nodes and lines of a scene."
  [scene]
  (seq (.getChildren scene)))

(defn get-layout-from-scene
  "Returns layout from a scene."
  [scn]
  (let [[nodes connections] (loop [things (get-diagram-from-scene scn),
				   nodes [],
				   connections []]
			      (if (empty? things)
				[nodes connections]
				(let [thing (first things)]
				  (cond
				   (node? thing)
				   (recur (rest things) (conj nodes thing) connections),
				   (connection? thing)
				   (recur (rest things) nodes (conj connections thing)),
				   :else
				   (throw (IllegalStateException. "Invalid item in lattice diagram."))))))]
    [(reduce (fn [hash node]
	       (assoc hash (get-name node) (position node)))
	     {}
	     nodes),
     (map #(vector (get-name (lower-node %))
		   (get-name (upper-node %)))
	  connections)]))

(defn set-layout-of-scene
  "Sets given layout as current layout of scene."
  [#^GScene scene, layout]
  (doto scene
    (.removeAll)
    (add-nodes-with-connections (first layout) (second layout))
    (.refresh)
    (.unzoom))) ; ? updates the scene and draw it's new content...


;;; node and line iterators

(defmacro donodes
  "Do whatever with every node on the scene."
  [[node scene] & body]
  `(doseq [~node (filter node? (get-diagram-from-scene ~scene))]
     ~@body))

(defmacro dolines
  "Do whatever with every connection on the scene."
  [[line scene] & body]
  `(doseq [~line (filter connection? (get-diagram-from-scene ~scene))]
     ~@body))


;;; some testing

(defn- make-some-scene
  "Testscene for lattice diagrams."
  []
  (draw-on-scene [0.0 0.0] [100.0 100.0]
		 [{:x [100 100], :y [50 50], :z [0 0], :a [50 0]}
		  [[:z :y], [:y :x], [:a :y]]]))


;;;

nil
