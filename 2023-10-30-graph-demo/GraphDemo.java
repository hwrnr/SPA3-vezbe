import java.io.*;
import java.util.*;

class Graph {
  int numberOfVertices;
  int numberOfEdges;
  List<Set<Integer>> edges;

  public Graph(int numberOfVertices, int numberOfEdges) {
    this.numberOfVertices = numberOfVertices;
    this.numberOfEdges = numberOfEdges;
    edges = new ArrayList<Set<Integer>>(numberOfVertices);
    for (int i = 0; i < numberOfEdges; i++) {
      edges.add(new HashSet<Integer>());
    }
  }

  public Graph(BufferedReader in) {
    try {
      this.numberOfVertices = Integer.parseInt(in.readLine());
      this.numberOfEdges = Integer.parseInt(in.readLine());

      edges = new ArrayList<Set<Integer>>(numberOfVertices);
      for (int i = 0; i < numberOfVertices; i++) {
        edges.add(new HashSet<Integer>());
      }

      String line;
      while ((line = in.readLine()) != null) {
        String[] tokens = line.split(" ");
        int u = Integer.parseInt(tokens[0]);
        int v = Integer.parseInt(tokens[1]);
        edges.get(u).add(v);
        edges.get(v).add(u);
      }

      for (int i = 0; i < numberOfVertices; i++) {
        System.out.println(edges.get(i));
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

  public Collection<Integer> getNeighborhood(int vertex) {
    return edges.get(vertex);
  }
}

class FindComponent {
  Set<Integer> visitedVertexes = new HashSet<Integer>();
  Graph graph;

  FindComponent(Graph graph, int vertex) {
    this.graph = graph;
    step(vertex);
  }

  private void step(int vertex) {
    if (visitedVertexes.contains(vertex))
      return;

    visitedVertexes.add(vertex);
    for (int newVertex : graph.getNeighborhood(vertex)) {
      step(newVertex);
    }
  }

  Collection<Integer> getComponent() {
    return this.visitedVertexes;
  }
}

class FindAllComponents {
  // Moglo je da se reuse FindComponent, ali je dovoljno kratko da je
  // jednostavnije ovako

  Graph graph;

  boolean[] visitedVertexes;

  List<Set<Integer>> components;

  FindAllComponents(Graph graph) {
    this.graph = graph;
    this.visitedVertexes = new boolean[graph.getNumberOfVertices()];
    this.components = new ArrayList<Set<Integer>>();

    int componentIndex = 0;
    for (int i = 0; i < graph.getNumberOfVertices(); ++i) {
      if (!visitedVertexes[i]) {
        components.add(componentIndex, new HashSet<Integer>());
        step(i, componentIndex);
        componentIndex++;
      }
    }
  }

  private void step(int vertex, int componentIndex) {
    if (visitedVertexes[vertex])
      return;

    visitedVertexes[vertex] = true;
    components.get(componentIndex).add(vertex);
    for (int newVertex : graph.getNeighborhood(vertex)) {
      step(newVertex, componentIndex);
    }
  }

  Collection<Set<Integer>> getComponents() {
    return this.components;
  }

}

class FindPath {

  Graph graph;
  LinkedList<Integer> path;
  boolean[] visitedVertexes;

  boolean pathExists = false;

  public FindPath(Graph graph, int startVertex, int endVertex) {
    this.graph = graph;
    this.visitedVertexes = new boolean[graph.getNumberOfVertices()];
    this.path = new LinkedList<>();

    try {
      // Lakše mi da obrnem start i end, nego da okrećem path
      path.addFirst(endVertex);
      step(endVertex, startVertex);
      this.path = null;
    } catch (Exception e) {
      this.pathExists = true;
    }
  }

  private void step(int vertex, int endVertex) throws Exception {
    if (visitedVertexes[vertex])
      return;

    if (vertex == endVertex)
      throw new Exception("Found path");

    visitedVertexes[vertex] = true;
    for (int newVertex : graph.getNeighborhood(vertex)) {
      path.addFirst(newVertex);
      step(newVertex, endVertex);
      path.removeFirst();
    }
  }

  boolean getPathExists() {
    return this.pathExists;
  }

  Collection<Integer> getPath() {
    return this.path;
  }
}

class VertexDistances {
  Graph graph;
  boolean[] visitedVertexes;

  LinkedList<Integer> queue = new LinkedList<>();

  int[] distances;

  VertexDistances(Graph graph, int vertex) {
    this.graph = graph;
    this.visitedVertexes = new boolean[graph.numberOfVertices];
    this.distances = new int[graph.getNumberOfVertices()];

    for (int i = 0; i < this.distances.length; ++i) {
      this.distances[i] = -1;
    }

    queue.addLast(vertex);
    visitedVertexes[vertex] = true;
    distances[vertex] = 0;

    int currentDistance;

    while (queue.size() > 0) {
      int currentVertex = queue.getFirst();
      queue.removeFirst();
      currentDistance = distances[currentVertex];

      for (int i : graph.getNeighborhood(currentVertex)) {
        if (!visitedVertexes[i]) {
          queue.addLast(i);
          distances[i] = currentDistance + 1;
          visitedVertexes[i] = true;
        }
      }
    }
  }

  int[] getDistances() {
    return this.distances;
  }
}

class FindContour {
  Graph graph;

  boolean[] visitedVertexes;
  boolean hasContour = false;

  LinkedList<Integer> currentPath;

  FindContour(Graph graph) {
    this.graph = graph;
    this.currentPath = new LinkedList<Integer>();
    this.visitedVertexes = new boolean[graph.getNumberOfVertices()];
    for (int i = 0; i < graph.getNumberOfVertices(); ++i) {
      visitedVertexes[i] = true;
      step(i, i);
    }
  }

  private void step(int currentVertex, int lastVertex) {
    for (int vertex : graph.getNeighborhood(currentVertex)) {
      if (!visitedVertexes[vertex]) {
        this.currentPath.addFirst(vertex);
        this.visitedVertexes[vertex] = true;
        step(vertex, currentVertex);
        this.currentPath.removeFirst();
      }
      else {
        if (vertex != lastVertex && this.currentPath.contains(vertex)) {
          hasContour = true;
        }
      }
    }
  }

  boolean getHasContour() {
    return hasContour;
  }
}

public class GraphDemo {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java GraphDemo <filename>");
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

    System.out.println("Komponenta iz čvora 1");
    FindComponent findComponent = new FindComponent(graph, 1);
    for (int vertex : findComponent.getComponent()) {
      System.out.print(vertex + ", ");
    }
    System.out.println();

    System.out.println("Komponenta iz čvora 12");
    FindComponent findComponent2 = new FindComponent(graph, 12);
    for (int vertex : findComponent2.getComponent()) {
      System.out.print(vertex + ", ");
    }
    System.out.println();

    System.out.println("Sve Komponente");
    FindAllComponents findAllComponents = new FindAllComponents(graph);
    for (Collection<Integer> i : findAllComponents.getComponents()) {
      System.out.println(i);
    }

    System.out.println("Put između 0 i 4");
    FindPath findPath = new FindPath(graph, 0, 4);
    System.out.println(findPath.getPath());

    System.out.println("Put između 0 i 12");
    FindPath findPath1 = new FindPath(graph, 0, 12);
    System.out.println(findPath1.getPath());

    System.out.println("Rastojanja od nule");
    VertexDistances vertexDistances = new VertexDistances(graph, 0);
    int[] distances = vertexDistances.getDistances();
    for (int i = 0; i < distances.length; ++i) {
      System.out.println(i + ": " + distances[i]);
    }

    System.out.println("Imal' konturu");
    FindContour findContour = new FindContour(graph);
    System.out.println(findContour.getHasContour());
  }
}
