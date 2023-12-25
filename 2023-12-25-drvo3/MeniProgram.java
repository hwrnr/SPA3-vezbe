import java.util.*;
import java.io.*;

class Node {
  Map<Character, Node> nodes;
  int counter;
  String description;
  String akcija;

  Node() {
    this.counter = 0;
    nodes = new HashMap<Character, Node>();
    this.description = "";
  }

  void increaseCounter() {
    counter++;
  }

  int getCounter() {
    return counter;
  }

  void setDescription(String description) {
    this.description = description;
  }

  void setAkcija(String akcija) {
    this.akcija = akcija;
  }
}

class Trie {
  Node root;

  Set<String> najcesciKljucevi;
  int najcesciCounter;

  public Trie() {
    root = new Node();
    root.setAkcija("");
    najcesciKljucevi = new HashSet<String>();
    najcesciCounter = 0;
  }

  public void insert(String kljuc, String description, String akcija) {
    this.insert(kljuc, 0, root, description, akcija);
  }

  private void insert(String kljuc, int index, Node currentNode, String description, String akcija) {
    if (kljuc.length() == index) {
      currentNode.increaseCounter();
      currentNode.setDescription(description);
      currentNode.setAkcija(akcija);
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

    this.insert(kljuc, index + 1, currentNode.nodes.get(kar), description, akcija);
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

  public Node get(String string) {
    return get(string, root, 0);
  }

  private Node get(String string, Node currentNode, int index) {
    if (string.length() == index)
      return currentNode;
    return get(string, currentNode.nodes.get(string.charAt(index)), index + 1);
  }
}

class MeniProgram {
  public static void main(String[] args) {
    Trie trie = new Trie();

    String podaci =
"(f) File \n" +
"- (n) new -> novi fajl\n" +
"(e) Edit\n" +
"- (c) copy -> kopiram\n" +
"- (x) cut -> secem\n" +
"- (v) paste -> lepim\n" +
"- (i) indent\n" +
"-- (m) indent more -> uvlacim\n" +
"-- (l) indent less -> privlacim\n" +
"-- (a) auto indent -> automatizujem\n" +
"-- (t) tabs or spaces\n" +
"--- (s) spaces -> spaces!\n" +
"--- (t) tabs -> tabs!\n" +
"- (d) delete\n" +
"-- (l) lines -> brisem redove\n" +
"-- (b) to beggining -> brisem do pocetka\n" +
"-- (e) to end -> brisem do kraja\n" +
"(p) Play\n" +
"- (n) next -> sledeca\n" +
"- (p) prev -> prethodna\n" +
"- (m) mode\n" +
"-- (s) shuffle\n" +
"--- (s) song -> pesma\n" +
"--- (a) all -> svi\n" +
"--- (b) album -> album\n" +
"(z) Zemlja\n" +
"- (e) Evropa\n" +
"-- (n) Nemacka\n" +
"--- (b) Berlin\n" +
"---- (o) Opera restaurant\n" +
"----- (w) Becka snicla\n" +
"------ (p) Pomfrit -> pravim pomfrit\n" +
"------ (l) limun\n" +
"------- (s) semenka -> grickam semenke\n";

    StringBuilder builder = new StringBuilder();

    String[] lines = podaci.split("\n");

    for (String line : lines) {
      char slovo = line.charAt(line.indexOf("(") + 1);
      int brojUvlacenja = line.split(" ")[0].length();
      if (line.charAt(0) == '(') {
        brojUvlacenja = 0;
      }
      builder.delete(brojUvlacenja, builder.length());
      builder.append(slovo);

      String opis = line.split("\\)")[1];
      String akcija = "";
      boolean imaAkciju = line.contains("->");
      if (imaAkciju) {
        String[] temp = opis.split("->");
        opis = temp[0];
        akcija = temp[1];
      }

      trie.insert(builder.toString(), opis, akcija);
      // System.out.println(trie.get(builder.toString()).description);
    }

    StringBuilder userInput = new StringBuilder("");
    Scanner scanner = new Scanner(System.in);
    String akcija = null;
    while (true) {
      Node currentNode = trie.get(userInput.toString()); // Bilo bi dobro da umesto Node vraća neku bar read-only strukturu, ako ne isflitriranu šta sme da se vidi van Trie klase

      System.out.println("\033[H\033[2J"); // Clear screen
      if (akcija != null) {
        System.out.println(akcija);
        System.out.println("Press enter to go back to main menu...");
        scanner.nextLine();
        System.out.println("\033[H\033[2J"); // Clear screen
        akcija = null;
      }

      if (currentNode == null) {
        System.out.println("Odabrali ste nepostojeću opciju");
        userInput.deleteCharAt(userInput.length() - 1);
        continue;
      }

      if (!"".equals(currentNode.akcija)) {
        akcija = currentNode.akcija;
        userInput.delete(0, userInput.length());
        continue;
      }

      for (char slovo : currentNode.nodes.keySet()) {
        System.out.println("(" + slovo + ") " + currentNode.nodes.get(slovo).description);
      }

      char novoSlovo = scanner.nextLine().charAt(0);
      if (novoSlovo == '.') break;
      if (novoSlovo == ',') {
        userInput.deleteCharAt(userInput.length() - 1);
      }
      else {
        userInput.append(novoSlovo);
      }

    }
    scanner.close();
  }
}
