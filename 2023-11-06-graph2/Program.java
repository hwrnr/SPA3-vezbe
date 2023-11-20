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

class IsBiPartitive {
  public boolean[] visited;
  public boolean isBiPartitive;
  public int[] skup;
  public Graph g;

  IsBiPartitive(Graph g) {
    this.visited = new boolean[g.getNumberOfVertices()];
    this.skup = new int[g.getNumberOfVertices()];
    this.isBiPartitive = true;
    this.g = g;

    for (int i = 0; i < g.getNumberOfVertices(); ++i) {
      if (visited[i] || skup[i] != 0) {
        continue;
      }
      this.skup[i] = 1;
      step(i);
    }
  }

  void step(int vertex) {
    if (visited[vertex]) {
      return;
    }
    visited[vertex] = true;
    for (int i : g.getNeighborhood(vertex)) {
      if (skup[i] != 0 && skup[i] == skup[vertex]) {
        isBiPartitive = false;
        return;
      } else {
        skup[i] = -skup[vertex];
        step(i);
      }
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

    IsBiPartitive isBiPartitive = new IsBiPartitive(graph);
    System.out.println(isBiPartitive.isBiPartitive);
  }
}
