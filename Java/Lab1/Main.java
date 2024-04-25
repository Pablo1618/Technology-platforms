package org.example;
import java.util.*;
import org.example.Mage;
import org.example.MageLevelComparator;

public class Main {
    public static void main(String[] args) {

        // Dostępne opcje:
        // no_sorting
        // natural_sorting
        // alternative_sorting
        System.out.println("Sort mode is set to: " + args[0]);
        String sortingMode = args[0];

        Set<Mage> mages;

        if (sortingMode.equals("natural")) {
            // Użycie compareTo()
            mages = new TreeSet<>();
        } else if (sortingMode.equals("alternative")) {
            // Przekazanie instancji komparatora do konstruktora
            mages = new TreeSet<>(new MageLevelComparator());
        } else {
            // Bez sortowania
            mages = new HashSet<>();
        }


        // Poziom 1: Magowie niebędący uczniami
        Mage mage1 = new Mage("Jan", 50, 200.0, new HashSet<>());
        Mage mage2 = new Mage("Krzysztof", 60, 250.0, new HashSet<>());
        Mage mage3 = new Mage("Bob", 70, 300.0, new HashSet<>());

        // Poziom 2: Magowie, którzy są studentami i mają uczniów
        Mage apprentice1 = new Mage("Patrycja", 40, 180.0, new HashSet<>());
        Mage apprentice2 = new Mage("Dawid", 45, 190.0, new HashSet<>());
        Mage apprentice3 = new Mage("Grzegorz", 55, 220.0, new HashSet<>());

        mage1.getApprentices().add(apprentice1);
        mage1.getApprentices().add(apprentice2);
        mage2.getApprentices().add(apprentice3);

        // Poziom 3: Magowie, którzy są studentami i nie mają uczniów
        Mage student1 = new Mage("George", 30, 150.0, null);
        Mage student2 = new Mage("Marcel", 35, 160.0, null);
        Mage student3 = new Mage("Jonathan", 90, 150.0, null);

        apprentice1.getApprentices().add(student1);
        apprentice1.getApprentices().add(student3);
        apprentice2.getApprentices().add(student2);

        mages.add(mage1);
        mages.add(mage2);
        mages.add(mage3);
        mages.add(apprentice1);
        mages.add(apprentice2);
        mages.add(apprentice3);
        mages.add(student1);
        mages.add(student2);

        // Użycie metody equals
//        if (mage1.equals(mage1)) {
//            System.out.println("Mages are the same.");
//        } else {
//            System.out.println("Mages are NOT the same.");
//        }

        // Sortowanie za pomocą Comparator (alternatywne kryterium)
//        MageLevelComparator mageComparator = new MageLevelComparator();
//        Arrays.sort(mages, mageComparator);
//        // Przeniesione w miejsce, gdzie sprawdzany jest parametr natural/alternative

        // Wypisanie magów - w kolejności po sortowaniu natural/alternative/brak sortowania
//        System.out.println("All mages:");
//        for (Mage mage : mages) {
//            System.out.println(mage);
//        }

        // Rekurencyjne wypisanie magów
//        for (Mage mage : mages) {
//            // Zbiór wypisanych magów, aby zapobiec nieskończonej pętli, gdyby magowie uczyli siebie nawzajem
//            Set<Mage> printedMages = new HashSet<>();
//           // String beginningText = "1.";
//            Mage.printMageRecursive(mage, printedMages, 0); // 0 = poziom rekurencji
//        }

        // Wypisywanie magów ze statystyką liczby uczniów
        Mage.printAllMagesWithStatistics(mages,true);

    }
}