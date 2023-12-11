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

  // Ako je digraf, getOne je cvor iz kog izlazi
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

class DiGraph {
  int numberOfVertices;
  int numberOfEdges;
  List<Set<Edge>> edges;

  public DiGraph(int numberOfVertices, int numberOfEdges) {
    this.numberOfVertices = numberOfVertices;
    this.numberOfEdges = numberOfEdges;
    edges = new ArrayList<Set<Edge>>(numberOfVertices);
    for (int i = 0; i < numberOfVertices; i++) {
      edges.add(new HashSet<Edge>());
    }
  }

  public DiGraph(BufferedReader in) {
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
        // edges.get(v).add(edge);
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

  public Collection<Edge> getOutNeighborhood(int vertex) {
    return edges.get(vertex);
  }
}

// Helper klasa da mi olakša System.out.println();
class Put {
  int startingVertex;
  List<Edge> put;
  double duzina;

  Put(int startingVertex, List<Edge> put, double duzina) {
    this.startingVertex = startingVertex;
    this.put = put;
    if (put != null) {
      this.duzina = duzina;
    } else {
      this.duzina = -1;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(startingVertex);
    sb.append(": ");

    if (put == null) {
      sb.append("-1 ");
    } else {
      sb.append(this.duzina);
    }
    sb.append(" -- ");

    if (this.put != null) {
      for (Edge e : this.put) {
        int startVertex = e.getOne();
        int endVertex = e.getOther(startVertex);
        double tezina = e.getWeight();
        sb.append(" ");
        sb.append(endVertex);
        sb.append(" (");
        sb.append(tezina);
        sb.append(")");
      }
    }

    return sb.toString();
  }
}

class Parnost {

  double[] duzinePuteva;
  Edge[] granaDolaska;
  DiGraph g;
  boolean[] visited;

  List<Put> putevi;

  // Nema potrebe da držimo PriorityQueue pošto prolazimo kroz svaku granu svakako (tačno 1)
  // Queue<Edge> queue = new PriorityQueue<Edge>();
  Queue<Edge> queue = new LinkedList<Edge>();

  Parnost(DiGraph g, int startingVertex) {
    this.g = g;

    this.duzinePuteva = new double[g.getNumberOfVertices()];
    this.granaDolaska = new Edge[g.getNumberOfVertices()];

    // Cvorovi cije smo grane smo vec ubacili
    // visited[i * g.getNumberOfVertices() + j] je grana iz i u j
    // Morala se održati mala složenost provere :), a da radi sa grafovima koji sadrže ciklus. Mada neće raditi za grafove koji imaju multigrane
    this.visited = new boolean[g.getNumberOfVertices() * g.getNumberOfVertices()];

    for (int i = 0; i < g.getNumberOfVertices(); ++i) {
      this.duzinePuteva[i] = Double.POSITIVE_INFINITY;
    }

    for (Edge e : g.getOutNeighborhood(startingVertex)) {
      int startVertex = e.getOne();
      int endVertex = e.getOther(startVertex);
      // Ubacujemo samo one grane koje imaju čvorove različite parnosti
      if (startVertex % 2 != endVertex % 2) {
        queue.add(e);
        this.visited[startVertex * g.getNumberOfVertices() + endVertex] = true;
      }
    }
    visited[startingVertex] = true;
    duzinePuteva[startingVertex] = 0.0;

    // Složenost O(E), jer ćemo svaku granu (koja ispunjava uslov) proći tačno jednom
    while (!queue.isEmpty()) {
      Edge edge = queue.poll();
      int vertex = edge.getOne();
      int otherVertex = edge.getOther(vertex);

      if (duzinePuteva[vertex] + edge.getWeight() < duzinePuteva[otherVertex]) {
        duzinePuteva[otherVertex] = duzinePuteva[vertex] + edge.getWeight();
        granaDolaska[otherVertex] = edge;
      }

      // Ukoliko je čvor povezan sa svim ostalim, ovde će biti složenost V, odnosno ovo je O(V) u najgorem slučaju. Ako ovo važi za sve čvorove (najgori slučaj), onda je složenost ukupno O(E*V) u najgorem slučaju.
      for (Edge e : g.getOutNeighborhood(otherVertex)) {
        int startVertex = e.getOne();
        int endVertex = e.getOther(startVertex);
      // Ubacujemo samo one grane koje imaju čvorove različite parnosti i nismo išli tom granom
        if (startVertex % 2 != endVertex % 2 && !this.visited[startVertex * g.getNumberOfVertices() + endVertex]) {
          queue.add(e);
          this.visited[startVertex * g.getNumberOfVertices() + endVertex] = true;
        }
      }
      visited[vertex] = true;
    }

    // Ovaj poziv ne utiče na složenost, jer je složenost (uglavnom) manja od prethodne složenosti, a i da je ista, onda bi složenst bila O(2 * E*V) što je opet O(E*V)
    // Ovo se poziva samo da bi se ulepšao rezultat svakako, i lepše ispisao. Putevi su u ovom trenutku već nađeni
    this.napraviPuteve();
  }

  private void napraviPuteve() {
    this.putevi = new ArrayList<Put>(g.numberOfVertices);
    // Ovo nam daje O(V)
    for (int i = 0; i < g.numberOfVertices; i++) {
      LinkedList<Edge> put = new LinkedList();
      Edge grana = granaDolaska[i];
      double zbirTezina = 0;
      if (duzinePuteva[i] == Double.POSITIVE_INFINITY) {
        this.putevi.add(new Put(i, null, zbirTezina));
      }
      else {
        // Ovo je manje od O(E), osim ako je put između dva krajnja čvora kada je graf put dužine E
        // Ukoliko je ovaj slučaj, onda je svaki naredni put kraći, pa ukupna složenost ne može biti O(E) nego mora biti manja
        while (grana != null) {
          put.addFirst(grana);
          zbirTezina += grana.getWeight();
          int startVertex = grana.getOne();
          grana = granaDolaska[startVertex];
        }
        this.putevi.add(new Put(i, put, zbirTezina));
      }
    }
  }

  public List<Put> vratiPuteve() {
    return this.putevi;
  }
}

public class Program2 {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java Program2 <filename>");
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

    Parnost d = new Parnost(graph, 0);
    for (Put p : d.vratiPuteve()) {
      System.out.println(p);
    }

  }
}
