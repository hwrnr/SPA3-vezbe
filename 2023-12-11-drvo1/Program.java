import java.io.*;
import java.util.List;

public class Program {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java Program <filename>");
      return;
    }

    Drvo<String, String> drvo = new Drvo<String, String>();

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
        String[] lineNiz = line.split(";");
        String tablica = lineNiz[0];
        String covek = lineNiz[1];
        drvo.put(tablica, covek);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(drvo.get("NS 459 IU"));
    System.out.println("Min key: " + drvo.minKey());
    System.out.println("Max key: " + drvo.maxKey());
    System.out.println("Height:  " + drvo.height());

    String a = "BG 398 DD";
    String b = "NS 249 YK";
    List<String> kljuceviIzmedju = drvo.keysInRange(a, b);
    System.out.println("Keys in range:  " + kljuceviIzmedju);

    for (int i = 0; i < kljuceviIzmedju.size(); ++i) {
      if (kljuceviIzmedju.get(i).compareTo(a) < 0) {
        System.out.println("error   " + kljuceviIzmedju.get(i));
      }
      if (kljuceviIzmedju.get(i).compareTo(b) > 0) {
        System.out.println("error 1 " + kljuceviIzmedju.get(i));
      }
    }

    for (int i = 0; i < kljuceviIzmedju.size() - 1; ++i) {
      if (kljuceviIzmedju.get(i).compareTo(kljuceviIzmedju.get(i + 1)) > 0) {
        System.out.println("Nisu sortirani");
      }
    }

    drvo.balanceIfNeeded();
    kljuceviIzmedju = drvo.keysInRange(a, b);
    System.out.println("Keys in range:  " + kljuceviIzmedju);
  }
}
