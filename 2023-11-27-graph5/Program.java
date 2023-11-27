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
    for (int i = 0; i < numberOfVertices; i++) {
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

  public Collection<Edge> getEdges() {
    Collection<Edge> edges = new ArrayList<>();
    for (Set<Edge> set : this.edges) {
      edges.addAll(set);
    }
    return edges;
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

  public void addEdge(Edge e) {
    int u = e.getOne();
    int v = e.getOther(u);
    edges.get(u).add(e);
    edges.get(v).add(e);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append(numberOfVertices);
    builder.append("\n");

    for (Set<Edge> edges : this.edges) {
      builder.append(edges);
      builder.append("\n");
    }

    return builder.toString();
  }

}

class MST {
  private Graph g;
  private Set<Edge> edges;
  private PriorityQueue<Edge> moguceGrane;
  private boolean[] visited;

  public MST(Graph g) {
    this(g, null);
  }

  public MST(Graph g, Edge mustHaveEdge) {
    this.g = g;
    this.edges = new HashSet<Edge>(g.numberOfVertices);
    this.moguceGrane = new PriorityQueue<Edge>(g.numberOfEdges);
    this.visited = new boolean[g.numberOfVertices];

    int startingVertex = 0;
    if (mustHaveEdge != null) {
      this.edges.add(mustHaveEdge);
      int vertex = mustHaveEdge.getOne();
      visited[vertex] = true;
      visited[mustHaveEdge.getOther(vertex)] = true;
      startingVertex = vertex;
      moguceGrane.addAll(g.getNeighborhood(mustHaveEdge.getOther(vertex)));
    }

    moguceGrane.addAll(g.getNeighborhood(startingVertex));

    while (!moguceGrane.isEmpty()) {
      Edge currentEdge = moguceGrane.poll();

      int vertexOne = currentEdge.getOne();
      if (!visited[vertexOne]) {
        visited[vertexOne] = true;
        edges.add(currentEdge);
        moguceGrane.addAll(g.getNeighborhood(vertexOne));
      }

      int vertexOther = currentEdge.getOther(vertexOne);
      if (!visited[vertexOther]) {
        visited[vertexOther] = true;
        edges.add(currentEdge); // Pošto je Set, nije problem da ga dva puta ubacimo
        moguceGrane.addAll(g.getNeighborhood(vertexOther));
      }

      moguceGrane.removeIf(edge -> { // Možda je bolje bez ovoga, već u moguceGrane.addAll da proveravamo da ni ne ubacujemo višak grana
        int vertex = edge.getOne();
        return visited[vertex] && visited[edge.getOther(vertex)];
      });
    }

    this.g = new Graph(g.numberOfVertices, g.numberOfEdges);

    System.out.println(edges);

    double suma = 0.0;
    for (Edge edge : edges) {
      this.g.addEdge(edge);
      suma += edge.getWeight();
    }

    // System.out.println("Težina: " + suma);

  }

  public Graph getMST() {
    return this.g;
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

    Edge mustHaveEdge = null;

    for (Edge edge : graph.getNeighborhood(3)) {
      if (edge.getOther(3) == 4) {
        mustHaveEdge = edge; // Mrzi me da implementiram equals, nek ga poredi po referenci
        break;
      }
    }

    MST mst = new MST(graph, mustHaveEdge);

    System.out.println(mst.getMST());

  }
}
