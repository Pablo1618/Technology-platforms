package org.example;
import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    public static void main(String[] args) {
        entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
        entityManager = entityManagerFactory.createEntityManager();

        initializeWithStartData();

        Scanner scanner = new Scanner(System.in);
        int wybor;

        do {
            System.out.println("Wybierz opcję:");
            System.out.println("1. Dodaj nowy wpis");
            System.out.println("2. Usuń wieżę");
            System.out.println("3. Usuń maga");
            System.out.println("4. Wyświetl wszystkie wpisy");
            System.out.println("5. Wyswietl magow pozywej danego poziomu");
            System.out.println("6. Wyswietl wieze pozywej danej wysokosci");
            System.out.println("7. Wyswietl magow powyzeje poziomu z danej wiezy");
            System.out.println("0. Wyjście");
            wybor = scanner.nextInt();

            switch (wybor) {
                case 1:
                    dodajNowyWpis();
                    break;
                case 2:
                    removeObject("Tower");
                    break;
                case 3:
                    removeObject("Mage");
                    break;
                case 4:
                    showAllEntries();
                    break;
                case 5:
                    System.out.println("Podaj poziom.");
                    int minLevel = scanner.nextInt();
                    showMagesWithLevelAbove(minLevel);
                    break;
                case 6:
                    System.out.println("Podaj wysokosc.");
                    int minHeight = scanner.nextInt();
                    showTowersWithHeightAbove(minHeight);
                    break;
                case 7:
                    System.out.println("Podaj ID wiezy:");
                    Long id = scanner.nextLong();
                    System.out.println("Podaj poziom.");
                    int level = scanner.nextInt();
                    showMagesWithLevelAboveFromTower(level,id);
                    break;
                case 0:
                    System.out.println("Koniec programu.");
                    break;
                default:
                    System.out.println("Nieprawidłowy wybór.");
            }
        } while (wybor != 0);

        entityManager.close();
        entityManagerFactory.close();
    }

    private static void initializeWithStartData() {
        entityManager.getTransaction().begin();

        Tower tower1 = new Tower("Potezna Wieza");
        Mage mage1 = new Mage("Jonathan", 69);
        Mage mage2 = new Mage("Michal", 2137);
        Mage mage3 = new Mage("Christopher", 420);
        tower1.addMage(mage1);
        tower1.addMage(mage2);
        tower1.addMage(mage3);
        entityManager.persist(tower1);

        Tower tower2 = new Tower("Magiczna Wieza");
        Mage mage4 = new Mage("Bob", 50);
        Mage mage5 = new Mage("Pawel", 34);
        tower2.addMage(mage4);
        tower2.addMage(mage5);
        entityManager.persist(tower2);

        entityManager.getTransaction().commit();
    }


    private static void dodajNowyWpis() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj nazwę dla Tower:");
        String towerName = scanner.nextLine();

        System.out.println("Podaj nazwę dla Mage:");
        String mageName = scanner.nextLine();

        System.out.println("Podaj level Maga:");
        int mageLevel = Integer.parseInt(scanner.nextLine());

        entityManager.getTransaction().begin();

        // Sprawdzenie czy istnieje wieza o takiej nazwie
        TypedQuery<Tower> query = entityManager.createQuery("SELECT t FROM Tower t WHERE t.name = :name", Tower.class);
        query.setParameter("name", towerName);
        List<Tower> wyniki = query.getResultList();
        Tower tower;

        if (wyniki.isEmpty()) {
            tower = new Tower(towerName);
            entityManager.persist(tower);
        } else {
            // Jesli istnieje wieza o takiej nazwie to jej uzywamy zamiast tworzyc nowa
            tower = wyniki.get(0);
        }

        Mage mage = new Mage(mageName, mageLevel);
        tower.addMage(mage);

        entityManager.getTransaction().commit();
        System.out.println("Nowy wpis dodany.");
    }

    private static void removeObject(String objectType) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj ID wpisu do usunięcia:");
        Long id = scanner.nextLong();

        entityManager.getTransaction().begin();

        // Usuwanie obiektu Tower
        if (objectType.equals("Tower")) {
            Tower tower = entityManager.find(Tower.class, id);
            if (tower != null) {
                entityManager.remove(tower);
                entityManager.getTransaction().commit();
                System.out.println("Wpis został usunięty.");
            } else {
                entityManager.getTransaction().rollback();
                System.out.println("Nie znaleziono wieży o podanym ID.");
            }
        }
        // Usuwanie obiektu Mage
        else if (objectType.equals("Mage")) {
            entityManager.flush();
            entityManager.clear();
            Mage mage = entityManager.find(Mage.class, id);
            if (mage != null) {
                entityManager.remove(mage);
                entityManager.getTransaction().commit();
                System.out.println("Mage został usunięty.");
            } else {
                entityManager.getTransaction().rollback();
                System.out.println("Nie znaleziono maga o podanym ID.");
            }
        }
        else {
            System.out.println("Nieprawidłowy typ obiektu do usunięcia.");
        }
    }

    private static void showAllEntries() {
        TypedQuery<Tower> query = entityManager.createQuery("SELECT t FROM Tower t", Tower.class);
        List<Tower> wyniki = query.getResultList();

        for (Tower tower : wyniki) {

            System.out.println("\u001B[1m\uD83C\uDFF0 TOWER:\u001B[0m " + tower.getName() + " | Wysokosc: " + tower.getHeight() + " ID: " + tower.getId());

            for (Mage mage : tower.getMages()) {
                System.out.println(" -  MAGE: \uD83E\uDDD9 " + mage.getName() + " | Level: " + mage.getLevel() + " ID: " + mage.getId());
            }
            System.out.println();
        }
    }

    private static void showMagesWithLevelAboveFromTower(int level, Long towerID) {
        TypedQuery<Mage> query = entityManager.createQuery(
                "SELECT m FROM Mage m JOIN m.tower t WHERE m.level > :minimalLevel AND t.id = :towerId",
                Mage.class
        );
        query.setParameter("minimalLevel", level);
        query.setParameter("towerId", towerID);
        List<Mage> mages = query.getResultList();

        if (mages.isEmpty()) {
            System.out.println("Brak magów z poziomem większym niż " + level + " związanych z wieżą o ID " + towerID);
        } else {
            System.out.println("Magowie z poziomem większym niż " + level + " związani z wieżą o ID " + towerID + ":");
            for (Mage mage : mages) {
                System.out.println("- " + mage.getName() + ", Poziom: " + mage.getLevel());
            }
        }
    }

    private static void showMagesWithLevelAbove(int level) {

        TypedQuery<Mage> query = entityManager.createQuery("SELECT m FROM Mage m WHERE m.level > :minimalLevel", Mage.class);
        query.setParameter("minimalLevel", level);
        List<Mage> mages = query.getResultList();

        if (mages.isEmpty()) {
            System.out.println("Brak magów z poziomem większym niż " + level);
        } else {
            System.out.println("Magowie z poziomem większym niż " + level + ":");
            for (Mage mage : mages) {
                System.out.println("- " + mage.getName() + ", Poziom: " + mage.getLevel());
            }
        }
    }

    private static void showTowersWithHeightAbove(int height) {

        TypedQuery<Tower> query = entityManager.createQuery("SELECT t FROM Tower t WHERE t.height > :minimalHeight", Tower.class);
        query.setParameter("minimalHeight", height);
        List<Tower> towers = query.getResultList();

        if (towers.isEmpty()) {
            System.out.println("Brak wiez wyzszych od " + height);
        } else {
            System.out.println("Oto wieze wyzsze od " + height + ":");
            for (Tower tower : towers) {
                System.out.println("- " + tower.getName() + ", Wysokosc: " + tower.getHeight());
            }
        }
    }


}
