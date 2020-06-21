/* 
 * @authors
 * Phanindra Pydisetty
 * Sahith Reddy
 * Karttik Yellu
 * Bharath Rudra
 */
package pxp180031;

import rbk.Graph.Vertex;
import rbk.Graph;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;

import pxp180031.BinaryHeap.Index;
import pxp180031.BinaryHeap.IndexedHeap;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.File;

public class MST extends GraphAlgorithm<MST.MSTVertex> {
	String algorithm;
	public long wmst;
	List<Edge> mst;

	MST(Graph g) {
		super(g, new MSTVertex((Vertex) null));
	}

	public static class MSTVertex implements Index, Comparable<MSTVertex>, Factory {
		boolean seen; // set to true if vertex is already seen else false
		Vertex parent; // Indicates Parent of the vertex
		int rank; // Rank of the vertex to union the sets
		int d; // Minimum distance to the vertex from it's adjacent vertices present in MST set
		Vertex vertex;
		int index; // Used in Index heap to keep track of the vertex's index

		MSTVertex(Vertex u) { // constructor for initializing the values of the required variables
			seen = false;
			parent = u;
			rank = 0;
			vertex = u;
			d = 0;
		}

		public MSTVertex make(Vertex u) {
			return new MSTVertex(u);
		}

		/**
		 * Sets the index of the given Vertex in the Priority Queue
		 * 
		 * @param index - To put the vertex in the given Index
		 */
		public void putIndex(int index) {
			this.index = index;
		}

		/**
		 * Gets the Index of the Specific Vertex in the Priority Queue returns the index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Custom Comparator used to compare the distances of the MSTVertex
		 */
		public int compareTo(MSTVertex other) {
			if (this.d < other.d) {
				return -1;
			} else if (this.d > other.d) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	/**
	 * method that find a given vertex in the graph and returns its parent
	 * 
	 * @param u
	 * @return
	 */
	public Vertex find(Vertex u) {
		if (get(u).parent.getName() != u.getName()) {
			get(u).parent = find(get(u).parent);
		}
		return get(u).parent;
	}

	/**
	 * method that combines two components based on rank
	 * 
	 * @param ru
	 * @param rv
	 */
	public void union(Vertex ru, Vertex rv) { // one component with greater rank becomes the parent of other
		if (get(ru).rank > get(rv).rank) {
			get(rv).parent = ru;
		} else if (get(ru).rank < get(rv).rank) {
			get(ru).parent = rv;
		} else {
			get(ru).rank++;
			get(rv).parent = ru;
		}
	}

	/**
	 * method that runs the kruskals algorithm on a graph
	 * 
	 * @return minimum spanning tree weight
	 */
	public long kruskal() {
		algorithm = "Kruskal";
		Edge[] edgeArray = g.getEdgeArray(); // array that stores the edges of the graph
		mst = new LinkedList<>();
		wmst = 0;

		Arrays.sort(edgeArray);
		for (Edge e : edgeArray) {
			Vertex u = e.fromVertex();
			Vertex v = e.otherEnd(u);

			Vertex ru = find(u);
			Vertex rv = find(v);
			if (!ru.equals(rv)) {
				mst.add(e);
				wmst += e.getWeight();
				union(ru, rv);
			}
		}
		return wmst;
	}

	/**
	 * Finds the Minimum Spanning tree Prim 3 Algorithm
	 * 
	 * @param s - Vertex s to indicate the source vertex to start the algorithm from
	 * @return minimum spanning tree weight
	 */
	public long prim3(Vertex s) { // Finds the Minimum Spanning tree using Indexed Heap
		algorithm = "Indexed Heap";
//uses indexed heap for storing the vertices with minimum distance based on their weights

		for (Vertex u : g) {
			get(u).seen = false;
			get(u).parent = null;
			get(u).d = Integer.MAX_VALUE;
		}

		get(s).d = 0;
		wmst = 0; // stores the weight of the mst
		IndexedHeap<MSTVertex> q = new IndexedHeap<>(g.size()); // Indexed Heap to keep track of vertex that has minimum
		                                                        // distance

		for (Vertex u : g) {
			q.add(get(u)); // adds all the vertices to the queue to iterate through
		}

		while (!q.isEmpty()) {
			MSTVertex uMSTVertex = q.remove(); // removes each element from queue and checks for their weights with neighbours
			Vertex u = uMSTVertex.vertex;
			if (!get(u).seen) {
				get(u).seen = true;
				wmst = wmst + get(u).d; // updates the weights with least weights
				for (Edge e : g.incident(u)) {
					Vertex v = e.otherEnd(u);
					// Updates the vertex distance if not seen and has lower distance than the
					// current distance
					if (!get(v).seen && e.getWeight() < get(v).d) {
						get(v).d = e.getWeight();
						get(v).parent = u;
						q.decreaseKey(get(v)); // Updates the distance of vertex by percolating up the Vertex with the updated
						                       // distance
					}
				}
			}
		}
		return wmst;
	}

	/**
	 * Finds the Minimum Spanning tree Prim 2 Algorithm
	 * 
	 * @param s - Vertex s to indicate the source vertex to start the algorithm from
	 * @return minimum spanning tree weight
	 */
	public long prim2(Vertex s) { // Algorithm finds the minimum spanning tree using Priority Queue vertices of
	                              // the graph
		algorithm = "PriorityQueue<Vertex>";
		for (Vertex u : g) { // Initialization of vertices in the graph
			get(u).seen = false;
			get(u).parent = null;
			get(u).d = Integer.MAX_VALUE;
		}
		get(s).d = 0;
		wmst = 0;
		PriorityQueue<MSTVertex> q = new PriorityQueue<>(); // Priority Queue to keep track of vertex that has minimum
		                                                    // distance
		q.add(get(s));

		while (!q.isEmpty()) {
			Vertex u = q.remove().vertex;
			if (!get(u).seen) {
				get(u).seen = true;
				wmst = wmst + get(u).d;

				for (Edge e : g.incident(u)) {
					Vertex v = e.otherEnd(u);
					// Updates the vertex distance if not seen and has lower distance than the
					// current distance
					if (!get(v).seen && e.getWeight() < get(v).d) {
						get(v).d = e.getWeight();
						get(v).parent = u;
						q.remove(get(v));
						q.add(get(v));
					}
				}
			}
		}
		return wmst;
	}

	/**
	 * 
	 * @param s - Vertex s to indicate the source vertex to start the algorithm from
	 * @return minimum spanning tree weight
	 * 
	 */
	public long prim1(Vertex s) { // Finds the Minimum Spanning tree using Priority Queue of Edges
		algorithm = "PriorityQueue<Edge>";

		for (Vertex u : g) {
			get(u).seen = false;
			get(u).parent = null;
		}
		get(s).seen = true;
		wmst = 0;
		PriorityQueue<Edge> q = new PriorityQueue<>(); // Priority Queue to keep track of Edge that has minimum distance

		for (Edge e : g.incident(s)) {
			q.add(e);
		}

		while (!q.isEmpty()) {
			Edge e = q.remove();
			Vertex u = e.fromVertex();
			Vertex v = (get(u).seen) ? e.otherEnd(u) : u;
			if (get(v).seen) {
				continue;
			}

			get(v).seen = true;
			get(v).parent = u;
			wmst = wmst + e.getWeight(); // updates the wmst with minimum weights in the graph

			for (Edge e2 : g.incident(v)) {
				if (!get(e2.otherEnd(v)).seen) {
					q.add(e2); // adds the vertices to the queue for checking each edge minimum weight
				}
			}
		}
		return wmst;
	}

	public static MST mst(Graph g, Vertex s, int choice) {
		MST m = new MST(g);
		switch (choice) {
		case 0:
			m.kruskal();
			break;
		case 1:
			m.prim1(s);
			break;
		case 2:
			m.prim2(s);
			break;
		default:
			m.prim3(s);
			break;
		}
		return m;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		int choice = 3;
		if (args.length == 0 || args[0].equals("-")) {
			in = new Scanner(System.in);
		} else {
			File inputFile = new File(args[0]);
			System.out.println(inputFile.getName());
			in = new Scanner(inputFile);
		}

		if (args.length > 1) {
			choice = Integer.parseInt(args[1]);
		}

		Graph g = Graph.readGraph(in);
		Vertex s = g.getVertex(1);

		Timer timer = new Timer();
		MST m = mst(g, s, choice);
		System.out.println(m.algorithm + "\n" + m.wmst);
		System.out.println(timer.end());
	}
}
