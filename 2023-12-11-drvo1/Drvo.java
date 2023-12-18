import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.*;
import java.lang.*;

class Cvor<K extends Comparable, V> {
  private K kljuc;
  private V vrednost;

  Cvor<K, V> levi, desni;

  Cvor(K kljuc, V vrednost) {
    this.kljuc = kljuc;
    this.vrednost = vrednost;
  }

  public K getKljuc() {
    return kljuc;
  }

  public V getVrednost() {
    return vrednost;
  }

  public void setVrednost(V vrednost) {
    this.vrednost = vrednost;
  }

  public void setKljuc(K kljuc) {
    this.kljuc = kljuc;
  }
}

public class Drvo<K extends Comparable<K>, V> {
  private Comparator<K> comparator;
  private Cvor<K, V> koren;

  public Drvo(Comparator<K> c) {
    this.comparator = Comparator.nullsFirst(c);
  }

  public Drvo() {
    this(Comparator.naturalOrder());
  }

  public void put(K key, V value) {
    if (koren == null) {
      koren = new Cvor<K, V>(key, value);
    }

    Cvor<K, V> trenutni = koren;
    Cvor<K, V> prosli = koren;

    while (trenutni != null) {
      if (comparator.compare(trenutni.getKljuc(), key) > 0) {
        prosli = trenutni;
        trenutni = trenutni.levi;
      } else if (comparator.compare(trenutni.getKljuc(), key) < 0) {
        prosli = trenutni;
        trenutni = trenutni.desni;
      } else {
        trenutni.setVrednost(value);
        return;
      }
    }

    Cvor<K, V> noviCvor = new Cvor<K, V>(key, value);

    if (comparator.compare(prosli.getKljuc(), key) > 0) {
      prosli.levi = noviCvor;
    } else {
      prosli.desni = noviCvor;
    }
  }

  public boolean containsKey(K key) {
    Cvor<K, V> trenutni = koren;
    while (trenutni != null) {
      if (comparator.compare(trenutni.getKljuc(), key) > 0) {
        trenutni = trenutni.levi;
      } else if (comparator.compare(trenutni.getKljuc(), key) < 0) {
        trenutni = trenutni.desni;
      } else {
        return true;
      }
    }
    return false;
  }

  public V get(K key) {
    Cvor<K, V> trenutni = koren;
    while (trenutni != null) {
      if (comparator.compare(trenutni.getKljuc(), key) > 0) {
        trenutni = trenutni.levi;
      } else if (comparator.compare(trenutni.getKljuc(), key) < 0) {
        trenutni = trenutni.desni;
      } else {
        return trenutni.getVrednost();
      }
    }
    return null;
  }

  public K minKey() {
    if (koren == null)
      return null;
    Cvor<K, V> trenutni = koren;
    while (trenutni.levi != null) {
      trenutni = trenutni.levi;
    }
    return trenutni.getKljuc();
  }

  public K maxKey() {
    if (koren == null)
      return null;
    Cvor<K, V> trenutni = koren;
    while (trenutni.desni != null) {
      trenutni = trenutni.desni;
    }
    return trenutni.getKljuc();
  }

  private Cvor<K, V> minCvor(Cvor<K, V> cvor) {
    Cvor<K, V> trenutni = cvor;
    while (trenutni.levi != null) {
      trenutni = trenutni.levi;
    }
    return trenutni;
  }

  private Cvor<K, V> maxCvor(Cvor<K, V> cvor) {
    Cvor<K, V> trenutni = cvor;
    while (trenutni.levi != null) {
      trenutni = trenutni.levi;
    }
    return trenutni;
  }

  private int height(Cvor<K, V> cvor) {
    if (cvor == null)
      return 0;
    return 1 + Math.max(height(cvor.levi), height(cvor.desni));
  }

  public int height() {
    return height(koren);
  }

  private void keysInRangeHelper(K a, K b, List<K> lista, Cvor<K, V> cvor) {
    if (cvor == null) {
      return;
    }

    if (comparator.compare(a, cvor.getKljuc()) <= 0) {
      keysInRangeHelper(a, b, lista, cvor.levi);
      if (comparator.compare(b, cvor.getKljuc()) >= 0) {
        lista.add(cvor.getKljuc());
      }
    }
    if (comparator.compare(b, cvor.getKljuc()) >= 0) {
      if (comparator.compare(a, cvor.getKljuc()) <= 0) {
        lista.add(cvor.getKljuc());
      }
      keysInRangeHelper(a, b, lista, cvor.desni);
    }
  }

  public List<K> keysInRange(K a, K b) {
    List<K> lista = new ArrayList<K>();
    keysInRangeHelper(a, b, lista, koren);
    return lista;
  }

  private void kradiSaLeva(Cvor<K, V> cvor, Cvor<K, V> roditelj) throws Exception {
    if (cvor == null || cvor.levi == null)
      throw new Exception("Nay :(");
    Cvor<K, V> levi = cvor.levi;
    Cvor<K, V> leviD = levi.desni;
    levi.desni = cvor;
    cvor.levi = leviD;
    if (roditelj.levi == cvor) {
      roditelj.levi = levi;
    } else if (roditelj.desni == cvor) {
      roditelj.desni = levi;
    } else {
      System.out.println("Veliki error levi");
    }
  }

  private void kradiSaDesna(Cvor<K, V> cvor, Cvor<K, V> roditelj) throws Exception {
    if (cvor == null || cvor.desni == null)
      throw new Exception("Nay :(");
    Cvor<K, V> desni = cvor.desni;
    Cvor<K, V> desniD = desni.levi;
    desni.levi = cvor;
    cvor.desni = desniD;
    if (roditelj.levi == cvor) {
      roditelj.levi = desni;
    } else if (roditelj.desni == cvor) {
      roditelj.desni = desni;
    } else {
      System.out.println("Veliki error desni");
    }
  }

  private Cvor<K, V> balansirajCvor(Cvor<K, V> cvor, Cvor<K, V> parent) {
    if (cvor == null) {
      return null;
    }

    cvor = balansirajCvor(cvor.levi, cvor);
    cvor = balansirajCvor(cvor.desni, cvor);

    if (cvor == null) return null;

    int levaVisina = this.height(cvor.levi);
    int desnaVisina = this.height(cvor.desni);
    int n = Math.abs(levaVisina - desnaVisina);

    while (n > 3) {

      if (levaVisina > desnaVisina) {
        try {
          cvor = kradiSaLeva(cvor, parent);
        } catch (Exception e) {
          if (!e.getMessage().equals("Nay :("))
            System.out.println(e.getMessage());
          break;
        }
      } else {
        try {
          cvor = kradiSaDesna(cvor, parent);
        } catch (Exception e) {
          if (!e.getMessage().equals("Nay :("))
            System.out.println(e.getMessage());
          break;
        }
      }
      levaVisina = this.height(cvor.levi);
      desnaVisina = this.height(cvor.desni);
      n = Math.abs(levaVisina - desnaVisina);
    }
    return cvor;
  }

  // Ovo ne radi, mrzi me da popravljam
  public void balanceIfNeeded() {
    int levaVisina = this.height(koren.levi);
    int desnaVisina = this.height(koren.desni);

    System.out.println("Početne visine");
    System.out.println("leva : " + levaVisina);
    System.out.println("desna: " + desnaVisina);

    balansirajCvor(koren.levi, koren);
    balansirajCvor(koren.desni, koren);

    levaVisina = this.height(koren.levi);
    desnaVisina = this.height(koren.desni);

    System.out.println("Krajnje visine");
    System.out.println("leva : " + levaVisina);
    System.out.println("desna: " + desnaVisina);
  }
}
