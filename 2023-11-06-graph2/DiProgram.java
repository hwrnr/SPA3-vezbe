import java.io.*;
import java.util.*;

class DiGraph {
  int numberOfVertices;
  int numberOfEdges;
  List<Set<Integer>> outEdges;
  List<Set<Integer>> inEdges;

  public DiGraph(BufferedReader in) {
    try {
      this.numberOfVertices = Integer.parseInt(in.readLine());
      this.numberOfEdges = Integer.parseInt(in.readLine());

      outEdges = new ArrayList<Set<Integer>>(numberOfVertices);
      inEdges = new ArrayList<Set<Integer>>(numberOfVertices);
      for (int i = 0; i < numberOfVertices; i++) {
        outEdges.add(new HashSet<Integer>());
        inEdges.add(new HashSet<Integer>());
      }

      String line;
      while ((line = in.readLine()) != null) {
        String[] tokens = line.split(" ");
        int u = Integer.parseInt(tokens[0]);
        int v = Integer.parseInt(tokens[1]);
        outEdges.get(u).add(v);
        inEdges.get(v).add(u);
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
    return outEdges.get(vertex).size();
  }

  public Collection<Integer> getOutNeighborhood(int vertex) {
    return outEdges.get(vertex);
  }

  public Collection<Integer> getInNeighborhood(int vertex) {
    return inEdges.get(vertex);
  }

  public Collection<Integer> getNeighborhood(int vertex) {
    Collection<Integer> outNeighborhood = getOutNeighborhood(vertex);
    Collection<Integer> inNeighborhood = getInNeighborhood(vertex);
    Set<Integer> neighborhood = new HashSet<Integer>(inNeighborhood.size() + outNeighborhood.size());
    for (int i : inNeighborhood) {
      neighborhood.add(i);
    }
    for (int i : outNeighborhood) {
      neighborhood.add(i);
    }

    return neighborhood;
  }
}

class FindAllComponents {

  DiGraph graph;

  boolean[] visitedVertexes;

  List<Set<Integer>> components;

  FindAllComponents(DiGraph graph) {
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

class FindContour {
  boolean[] visitedVertexes;
  DiGraph graph;

  LinkedList<Integer> currentPath;

  FindContour(DiGraph graph) {
    this.graph = graph;
    this.visitedVertexes = new boolean[graph.getNumberOfVertices()];

    try {
      for (int i = 0; i < graph.getNumberOfVertices(); ++i) {
        this.currentPath = new LinkedList<Integer>();
        if (!visitedVertexes[i]) {
          currentPath.add(i);
          step(i, i);
        }
      }
    } catch (Exception e) {
      LinkedList<Integer> lastPath = this.currentPath;
      this.currentPath = new LinkedList<Integer>();
      int lastVertex = lastPath.getLast();
      lastPath.removeLast();

      int currentVertex = lastPath.getLast();
      lastPath.removeLast();
      while (currentVertex != lastVertex) {
        currentPath.addFirst(currentVertex);
        currentVertex = lastPath.getLast();
        lastPath.removeLast();
      }
      currentPath.addFirst(currentVertex);
    }
  }

  void step(int vertex, int lastVertex) throws Exception {
    if (visitedVertexes[vertex])
      return;

    visitedVertexes[vertex] = true;

    for (int i : graph.getOutNeighborhood(vertex)) {
      if (i != lastVertex && currentPath.contains(i)) {
        currentPath.add(i);
        throw new Exception("Cycle detected");
      } else {
        currentPath.add(i);
        step(i, vertex);
        currentPath.removeLast();
      }
    }
  }
}

class TopologicalSort {

  DiGraph graph;
  boolean[] visited;

  List<Integer> sorted;

  TopologicalSort(DiGraph graph) {
    this.graph = graph;
    this.sorted = new ArrayList<Integer>(graph.getNumberOfVertices());
    this.visited = new boolean[graph.getNumberOfVertices()];

    for (int i = 0; i < graph.getNumberOfVertices(); ++i) {
      if (!visited[i]) {
        step(i);
      }
    }

    for (int i = 0; i < this.sorted.size() / 2; ++i) {
      int temp = this.sorted.get(i);
      this.sorted.set(i, this.sorted.get(this.sorted.size() - i - 1));
      this.sorted.set(this.sorted.size() - i - 1, temp);
    }

  }

  void step(int vertex) {
    if (visited[vertex])
      return;

    visited[vertex] = true;
    for (int i : graph.getOutNeighborhood(vertex)) {
      step(i);
    }
    sorted.add(vertex);
  }

}

public class DiProgram {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java DiProgram <filename>");
      return;
    }

    BufferedReader reader;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }
    DiGraph graph = new DiGraph(reader);

    FindAllComponents components = new FindAllComponents(graph);
    System.out.println(components.getComponents());

    FindContour contour = new FindContour(graph);
    System.out.println(contour.currentPath);

    TopologicalSort sort = new TopologicalSort(graph);
    System.out.println(sort.sorted);
  }
}
