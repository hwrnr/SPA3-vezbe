import java.io.*;
import java.util.*;

import javax.sound.midi.SysexMessage;

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

class Razliciti {

  Set<Integer> cvorovi;

  Razliciti(Graph g) {
    this.cvorovi = new HashSet<Integer>();
    for (int i = 0; i < g.numberOfVertices; i += 2) {
      Collection<Integer> susedi = g.getNeighborhood(i);
      boolean ispunjavaUslov = true;
      for (int cvor : susedi) {
        if (cvor % 2 == 0) {
          ispunjavaUslov = false;
          break;
        }
      }
      if (ispunjavaUslov) {
        cvorovi.add(i);
      }
    }
  }

  Set<Integer> vratiCvorove() {
    return cvorovi;
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

    Razliciti razliciti = new Razliciti(graph);
    System.out.println(razliciti.vratiCvorove());

  }
}
