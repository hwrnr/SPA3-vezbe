import java.util.*;
import java.io.*;

class Node {
  Map<Character, Node> nodes;
  int counter;

  Node() {
    this.counter = 0;
    nodes = new HashMap<Character, Node>();
  }

  void increaseCounter() {
    counter++;
  }

  int getCounter() {
    return counter;
  }
}

class Trie {
  Node root;

  Set<String> najcesciKljucevi;
  int najcesciCounter;

  public Trie() {
    root = new Node();
    najcesciKljucevi = new HashSet();
    najcesciCounter = 0;
  }

  public void insert(String kljuc) {
    this.insert(kljuc, 0, root);
  }

  private void insert(String kljuc, int index, Node currentNode) {
    if (kljuc.length() == index) {
      currentNode.increaseCounter();
      if (currentNode.counter > najcesciCounter) {
        najcesciCounter = currentNode.counter;
        najcesciKljucevi.clear();
      }

      if (currentNode.counter == najcesciCounter) {
        najcesciKljucevi.add(kljuc);
      }

      return;
    }

    char kar = kljuc.charAt(index);

    if (!currentNode.nodes.containsKey(kar)) {
      currentNode.nodes.put(kar, new Node());
    }

    this.insert(kljuc, index + 1, currentNode.nodes.get(kar));
  }

  int getCount(String kljuc) {
    return getCount(kljuc, 0, root);
  }

  private int getCount(String kljuc, int index, Node currentNode) {
    if (kljuc.length() == index) {
      return currentNode.counter;
    }

    char kar = kljuc.charAt(index);

    if (!currentNode.nodes.containsKey(kar)) {
      return 0;
    }

    return this.getCount(kljuc, index + 1, currentNode.nodes.get(kar));
  }

  void deleteAndSlomiNajscesce(String kljuc) {
    delete(kljuc, 0, root);
  }

  private void delete(String kljuc, int index, Node currentNode) {
    if (kljuc.length() == index) {
      currentNode.counter = 0;
      return;
    }

    char kar = kljuc.charAt(index);

    if (!currentNode.nodes.containsKey(kar)) {
      return;
    }

    this.delete(kljuc, index + 1, currentNode.nodes.get(kar));
  }

  public Map<String, Integer> asMap() {
    Map<String, Integer> mapa = new HashMap<String, Integer>();
    this.asMap(new StringBuilder(), root, mapa);
    return mapa;
  }

  private void asMap(StringBuilder trenutniKljuc, Node currentNode, Map<String, Integer> mapa) {

    if (currentNode.counter != 0) {
      mapa.put(trenutniKljuc.toString(), currentNode.counter);
    }

    for (char kar : currentNode.nodes.keySet()) {
      Node n = currentNode.nodes.get(kar);
      trenutniKljuc.append(kar);
      asMap(trenutniKljuc, n, mapa);
      trenutniKljuc.deleteCharAt(trenutniKljuc.length() - 1);
    }
  }

  public Collection<String> getWithPrefix(String prefix) {
    Map<String, Integer> kolekcija = new HashMap<String, Integer>();
    this.getWithPrefix(prefix, 0, root, kolekcija);
    return kolekcija.keySet();
  }

  private void getWithPrefix(String prefix, int index, Node currentNode, Map<String, Integer> kolekcija) {
    if (prefix.length() == index) {
      asMap(new StringBuilder(prefix), currentNode, kolekcija);
      return;
    }

    char kar = prefix.charAt(index);

    if (currentNode.nodes.containsKey(kar)) {
      this.getWithPrefix(prefix, index + 1, currentNode.nodes.get(kar), kolekcija);
    }
  }

  public Set<String> getNajcesciKljucevi() {
    return najcesciKljucevi;
  }
}

class Program {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java Program <filename>");
      return;
    }
    Trie drvo = new Trie();

    BufferedReader reader;
    try {
      try {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return;
      }

      String line;
      while ((line = reader.readLine()) != null) {
        drvo.insert(line);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    drvo.insert("");
    drvo.insert("");
    drvo.insert("");
    drvo.insert("");
    System.out.println(drvo.getCount(""));

    System.out.println(drvo.asMap());

    System.out.println(drvo.getNajcesciKljucevi());

    System.out.println(drvo.getWithPrefix("Avramov"));

    drvo.deleteAndSlomiNajscesce("Avramov");

    System.out.println(drvo.getWithPrefix("Avramov"));
  }
}
