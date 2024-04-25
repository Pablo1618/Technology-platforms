package org.example;
import java.util.ArrayList;
import java.util.*;
import java.util.Scanner;

class ResultCollector {
    private List<Integer> results = new ArrayList<>();

    public synchronized void addResult(int result) {
        results.add(result);
    }

    public synchronized List<Integer> getResults() {
        return new ArrayList<>(results); // zwracamy kopię listy, aby uniknąć niekontrolowanego dostępu do danych
    }
}

class TaskManager {
    public Queue<Integer> numbersToCheck = new LinkedList<>();
    public boolean waitingForNewNumber = true;
    public boolean isNumberPrime = true;
    public LinkedList<Integer> tasks = new LinkedList<>();
    private ResultCollector resultCollector = new ResultCollector(); // dodajemy obiekt ResultCollector
    public boolean endTaskManager = false;

    public synchronized void checkNewNumber(){
        waitingForNewNumber = false;
        isNumberPrime = true;
        int number = numbersToCheck.peek();
        for(int i=2;i<number;i++){
            tasks.add(i);
            notify();
        }
    }

//    public synchronized void addTask(int task) {
//        tasks.add(task);
//        notify();
//    }

    public synchronized int getTask() throws InterruptedException {
        while (tasks.isEmpty()) {
            wait();
        }
        return tasks.remove();
    }

    public ResultCollector getResultCollector() {
        return resultCollector;
    }
}

class TaskConsumer implements Runnable {

//    private boolean isPrime(int number) {
//        if (number <= 1) {
//            return false;
//        }
//        for (int i = 2; i * i <= number; i++) {
//            if (number % i == 0) {
//                return false;
//            }
//        }
//        return true;
//    }
    private TaskManager taskManager;

    public TaskConsumer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    //public void run() {
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int number = taskManager.getTask();
                Thread.sleep(100); // symulacja przetwarzania zadania
                if(taskManager.numbersToCheck.peek() % number == 0){
                    taskManager.isNumberPrime = false;
                }
                if(taskManager.numbersToCheck.peek()-1 == number ){
                    int nmb = taskManager.numbersToCheck.peek();
                    if(taskManager.isNumberPrime == false){
                        System.out.println(nmb + " nie jest pierwsza.");
                    }else{
                        System.out.println(nmb + " jest pierwsza.");
                        taskManager.getResultCollector().addResult(number+1);
                    }
                    taskManager.numbersToCheck.remove();
                    taskManager.waitingForNewNumber = true;
                }

            } catch (InterruptedException e) {
                // Obsługa przerwania wątku
                System.out.println("Wątek konsumenta przerwany.");
                Thread.currentThread().interrupt(); // Ponowne ustawienie flagi przerwania
            }

            if (taskManager.tasks.isEmpty() && taskManager.endTaskManager) {
                System.out.println("Przerwanie wątku konsumenta.");
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        int numThreads = Integer.parseInt(args[0]);
        List<Thread> consumerThreads = new ArrayList<>();
        Thread consumerThread = null;
        for (int i = 0; i < numThreads; i++) {
            consumerThread = new Thread(new TaskConsumer(taskManager));
            consumerThread.start();
            consumerThreads.add(consumerThread);
        }

        Thread checkingThread = new Thread(() -> {
            while (true) {
                if (taskManager.waitingForNewNumber && !taskManager.numbersToCheck.isEmpty()) {
                    System.out.println(taskManager.numbersToCheck);
                    taskManager.checkNewNumber();
                }
                try {
                    Thread.sleep(1000); // Poczekaj 1 sekundę przed kolejną iteracją
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        checkingThread.start();

        // Wprowadzanie zadań przez użytkownika
        System.out.print("Wprowadź liczbę: \n");
        Scanner scanner = new Scanner(System.in);
        while (true) {

            String input = scanner.nextLine();
            if (input.equals("q")) {
                // Przerwanie działania wszystkich wątków konsumentów
                taskManager.endTaskManager = true;
//                for (Thread thread : consumerThreads) {
//                    //thread.interrupt();
//                    //taskManager.endTaskManager = true;
//                }
                //break;
            }
            else if(input.equals("w")) {
                System.out.println("Znalezione liczby pierwsze: " + taskManager.getResultCollector().getResults());
            }
            else {
                try {
                    int number = Integer.parseInt(input);
                    taskManager.numbersToCheck.add(number);
                    //taskManager.waitingForNewNumber = false;
                    //taskManager.checkNewNumber();
                    //taskManager.addTask(number); // Dodanie liczby do listy zadań
                } catch (NumberFormatException e) {
                    System.out.println("Nieprawidłowy format liczby.");
                }
            }
        }
    }
}
