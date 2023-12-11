import java.io.*;
import java.util.*;

class Edge implements Comparable<Edge> {
  private int v1, v2;
  double weight;

  Edge(int v1, int v2, double weight) {
    this.v1 = v1;
    this.v2 = v2;
    this.weight = weight;
  }

  public int getOne() {
    return v1;
  }

  public int getOther(int v) {
    if (v == v1)
      return v2;
    return v1;
  }

  public double getWeight() {
    return weight;
  }

  public String toString() {
    return "Edge(" + v1 + ", " + v2 + ", " + weight + ")";
  }

  @Override
  public int compareTo(Edge other) {
    return Double.compare(weight, other.weight);
  }
}

class Graph {
  int numberOfVertices;
  int numberOfEdges;
  List<Set<Edge>> edges;

  public Graph(int numberOfVertices, int numberOfEdges) {
    this.numberOfVertices = numberOfVertices;
    this.numberOfEdges = numberOfEdges;
    edges = new ArrayList<Set<Edge>>(numberOfVertices);
    for (int i = 0; i < numberOfEdges; i++) {
      edges.add(new HashSet<Edge>());
    }
  }

  public Graph(BufferedReader in) {
    try {
      this.numberOfVertices = Integer.parseInt(in.readLine());
      this.numberOfEdges = Integer.parseInt(in.readLine());

      edges = new ArrayList<Set<Edge>>(numberOfVertices);
      for (int i = 0; i < numberOfVertices; i++) {
        edges.add(new HashSet<Edge>());
      }

      String line;
      while ((line = in.readLine()) != null) {
        String[] tokens = line.split(" ");
        int u = Integer.parseInt(tokens[0]);
        int v = Integer.parseInt(tokens[1]);
        double weight = Double.parseDouble(tokens[2]);
        Edge edge = new Edge(u, v, weight);
        edges.get(u).add(edge);
        edges.get(v).add(edge);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public int getNumberOfVertices() {
    return this.numberOfVertices;
  }

  public int getNumberOfEdges() {
    return this.numberOfEdges;
  }

  public int neighborhoodSize(int vertex) {
    return edges.get(vertex).size();
  }

  public Collection<Edge> getNeighborhood(int vertex) {
    return edges.get(vertex);
  }
}

class HighestAndLowestVertices {

  private int minV = -1;
  private int maxV = -1;

  private double currentMin = Double.POSITIVE_INFINITY;
  private double currentMax = Double.NEGATIVE_INFINITY;

  HighestAndLowestVertices(Graph graph) {
    for (int i = 0; i < graph.numberOfVertices; ++i) {
      double current = 0;
      for (Edge e : graph.getNeighborhood(i)) {
        current += e.getWeight();
      }

      if (current < currentMin) {
        currentMin = current;
        minV = i;
      }
      if (current > currentMax) {
        currentMax = current;
        maxV = i;
      }
    }
  }

  int getLowest() {
    return minV;
  }

  int getHighest() {
    return maxV;
  }

}

class PossiblyDijkstra {

  double[] duzinePuteva;
  Edge[] granaDolaska;
  Graph g;
  boolean[] visited;

  PriorityQueue<Edge> queue = new PriorityQueue<Edge>();

  PossiblyDijkstra(Graph g, int startingVertex) {
    this.g = g;

    this.duzinePuteva = new double[g.getNumberOfVertices()];
    this.granaDolaska = new Edge[g.getNumberOfVertices()];
    this.visited = new boolean[g.getNumberOfVertices()];

    for (int i = 0; i < g.getNumberOfVertices(); ++i) {
      this.duzinePuteva[i] = Double.POSITIVE_INFINITY;
      this.visited[i] = false;
    }

    queue.addAll(g.getNeighborhood(0));
    visited[0] = true;

    duzinePuteva[startingVertex] = 0.0;

    while (!queue.isEmpty()) {
      Edge edge = queue.poll();
      int vertex = edge.getOne();
      int otherVertex = edge.getOther(vertex);

      if (duzinePuteva[otherVertex] + edge.getWeight() < duzinePuteva[vertex]) {
        duzinePuteva[vertex] = duzinePuteva[otherVertex] + edge.getWeight();
        granaDolaska[vertex] = edge;
      }
      else if (duzinePuteva[vertex] + edge.getWeight() < duzinePuteva[otherVertex]) {
        duzinePuteva[otherVertex] = duzinePuteva[vertex] + edge.getWeight();
        granaDolaska[otherVertex] = edge;
      }

      for (Edge e : g.getNeighborhood(vertex)) {
        if (!visited[e.getOther(vertex)]) {
          queue.add(e);
        }
      }
      for (Edge e : g.getNeighborhood(otherVertex)) {
        if (!visited[e.getOther(otherVertex)]) {
          queue.add(e);
        }
      }
      visited[vertex] = true;
      visited[otherVertex] = true;
    }
  }
}

public class Program {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java Program <filename>");
      return;
    }

    BufferedReader reader;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }
    Graph graph = new Graph(reader);

    HighestAndLowestVertices highestAndLowestVertices = new HighestAndLowestVertices(graph);

    System.out.println("Max: " + highestAndLowestVertices.getHighest());
    System.out.println("Min: " + highestAndLowestVertices.getLowest());

    PossiblyDijkstra d = new PossiblyDijkstra(graph, 0);
    System.out.println("Dijkstra");
    System.out.println();
    for (int i = 0; i < graph.getNumberOfVertices(); ++i) {
      System.out.println(i + ": " + d.granaDolaska[i]);
    }

  }
}
