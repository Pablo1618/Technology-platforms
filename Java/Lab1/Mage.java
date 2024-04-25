package org.example;
import java.util.*;

/**
 * <h1>Mage</h1>
 */

public class Mage implements Comparable<Mage>{
    private String name;
    private int level;
    private double power;
    private Set<Mage> apprentices;

    public Mage(String name, int level, double power, Set<Mage> apprentices) {
        this.name = name;
        this.level = level;
        this.power = power;
        this.apprentices = apprentices;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setPower(double power) {
        this.power = power;
    }
    public void setApprentices(Set<Mage> apprentices) {
        this.apprentices = apprentices;
    }
    public String getName() {
        return name;
    }
    public int getLevel() {
        return level;
    }
    public double getPower() {
        return power;
    }
    public Set<Mage> getApprentices() {
        return apprentices;
    }

    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }else if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        // rzutowanie na obiekt klasy Mag
        Mage mage = (Mage) obj;
        return level == mage.level && Double.compare(mage.power, power) == 0 && Objects.equals(name, mage.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, power);
    }

    @Override
    public int compareTo(Mage otherMage) {
        // Imię maga
        int nameComparison = this.name.compareTo(otherMage.name);
        if (nameComparison != 0) {
            return nameComparison;
        }
        // Poziom
        if (this.level != otherMage.level) {
            return Integer.compare(this.level, otherMage.level);
        }
        // Moc
        return Double.compare(this.power, otherMage.power);
    }

    @Override
    public String toString() {
        return "Mage{name='" + name + "', level=" + level + ", power=" + power + "}";
    }

    public static void printMageRecursive(Mage mage, Set<Mage> printedMages, int depth) {
        if (printedMages.contains(mage)) {
            return; // Jeśli mag już został wyświetlony, przerwij
        }
        // Wyświetl maga
        System.out.println("-".repeat(depth) + mage);

        // Dodaj maga do zbioru wyświetlonych magów
        printedMages.add(mage);

        // Wywołaj rekurencyjnie dla uczniów maga
        if (mage.getApprentices() != null) {
            for (Mage apprentice : mage.getApprentices()) {
                printMageRecursive(apprentice, printedMages, depth + 1);
            }
        }
    }

    public static void printAllMagesWithStatistics(Set<Mage> mages, boolean useTreeMap) { // ewentualnie Collection<Mage> mages - bardziej uniwersalne
        Map<Mage, Integer> mageStudentsAmount = new HashMap<>();
        for (Mage mage : mages) {
            countStudents(mage, mageStudentsAmount);
        }

        if (useTreeMap) { // sortowanie
            mageStudentsAmount = new TreeMap<>(mageStudentsAmount);
        }

        for (Map.Entry<Mage, Integer> entry : mageStudentsAmount.entrySet()) {
            System.out.println(entry.getKey() + " - Uczniowie: " + entry.getValue());
        }
    }

    private static int countStudents(Mage mage, Map<Mage, Integer> mageStudentsAmount) {
        if (mageStudentsAmount.containsKey(mage)) {
            // W razie, gdyby magowie uczyli siebie nawzajem to byłaby nieskończona pętla
            // więc robimy wtedy return, aby temu zapobiec
            return mageStudentsAmount.get(mage);
        }

        int students = 0;
        if (mage.getApprentices() != null) {
            for (Mage apprentice : mage.getApprentices()) {
                students += 1 + countStudents(apprentice, mageStudentsAmount);
            }
        }

        mageStudentsAmount.put(mage, students);
        return students;
    }

}

