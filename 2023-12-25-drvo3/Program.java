import java.util.*;
import java.io.*;

class Imenik {
  private TreeMap<String, String> imena;
  private Map<String, String> brojevi;

  Imenik() {
    this.imena = new TreeMap<String, String>();
    this.brojevi = new HashMap<String, String>();
  }

  public boolean put(String osoba, String tel) {
    if (imena.containsKey(osoba) || brojevi.containsKey(tel)) {
      return false;
    }

    imena.put(osoba, tel);
    brojevi.put(tel, osoba);

    return true;
  }

  public String getTel(String osoba) {
    return imena.getOrDefault(osoba, null);
  }

  public String getOsoba(String tel) {
    return brojevi.getOrDefault(tel, null);
  }

  public Set<String> getBrojevi() {
    return brojevi.keySet();
  }

  public List<String> getOsobe() {
    // Ja se nadam da je ovo sortirano :)
    return new ArrayList<String>(imena.keySet());
  }

}

class Program {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java Program <filename>");
      return;
    }

    Imenik imenik = new Imenik();

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
        String[] lineSplit = line.split(";");
        imenik.put(lineSplit[0], lineSplit[1]);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<String> imena = imenik.getOsobe();

    for (int i = 0; i < imena.size() - 1; ++i) {
      if (imena.get(i).compareTo(imena.get(i+1)) > 0) {
        System.out.println("Nije sortirano :("); // Nije se ispisalo :)
      }
    }
  }
}
