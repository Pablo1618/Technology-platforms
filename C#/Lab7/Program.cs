using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

class Program
{
    static void Main(string[] args)
    {
        if (args.Length == 0)
        {
            Console.WriteLine("Nie podano ścieżki do katalogu.");
            return;
        }

        string directoryPath = args[0];

        if (!Directory.Exists(directoryPath))
        {
            Console.WriteLine("Podany katalog nie istnieje.");
            return;
        }

        DirectoryInfo directory = new DirectoryInfo(directoryPath);
        DisplayDirectoryContents(directory, 0);



        // Data najstarszego pliku
        var oldestFile = directory.GetOldestDateRecursive();
        Console.WriteLine($"\nNajstarszy plik: {oldestFile}\n");



        // Pliki
        FileInfo[] files = directory.GetFiles();
        // Foldery
        DirectoryInfo[] directories = directory.GetDirectories();

        // Posortowana kolekcja, klucz - nazwa (string), wartość - rozmiar (long)
        SortedList<string, long> sortedCollection = new SortedList<string, long>(new CustomStringComparer());

        foreach (FileInfo file in files)
        {
            sortedCollection.Add(file.Name, file.Length);
        }
        foreach (DirectoryInfo subdirectory in directories)
        {
            int fileCount = subdirectory.GetFiles().Length; // Liczba plikow
            int subdirectoryCount = subdirectory.GetDirectories().Length; // Liczba podkatalogow (folderow)
            sortedCollection.Add(subdirectory.Name, fileCount + subdirectoryCount); // Liczba plikow + liczba folderow
        }


        // Serializacja kolekcji
        SerializeData(sortedCollection);

        // Deserializacja i wyswietlenie kolekcji
        SortedList<string, long> deserializedCollection = DeserializeData();
        foreach (var pair in deserializedCollection)
        {
            Console.WriteLine($"{pair.Key} -> {pair.Value}");
        }

    }


    // Serializacja kolekcji
    static void SerializeData(SortedList<string, long> collection)
    {
        using (FileStream fileStream = new FileStream("collection.bin", FileMode.Create))
        {
            BinaryFormatter binaryFormatter = new BinaryFormatter();
            binaryFormatter.Serialize(fileStream, collection);
        }
    }

    // Deserializacja kolekcji
    static SortedList<string, long> DeserializeData()
    {
        using (FileStream fileStream = new FileStream("collection.bin", FileMode.Open))
        {
            BinaryFormatter binaryFormatter = new BinaryFormatter();
            return (SortedList<string, long>)binaryFormatter.Deserialize(fileStream);
        }
    }


    static void DisplayDirectoryContents(DirectoryInfo directory, int indentLevel)
    {
        string indentation = new string(' ', indentLevel * 2);

        // Pliki
        FileInfo[] files = directory.GetFiles();
        // Foldery
        DirectoryInfo[] directories = directory.GetDirectories();

        foreach (FileInfo file in files)
        {
            Console.WriteLine($"{indentation}> {file.Name} {file.Length} bajtow {file.GetDosAttributes()}");
        }

        foreach (DirectoryInfo subdirectory in directories)
        {
            int fileCount = subdirectory.GetFiles().Length;
            int subdirectoryCount = subdirectory.GetDirectories().Length;
            Console.WriteLine($"{indentation}[] {subdirectory.Name} ({fileCount + subdirectoryCount}) {subdirectory.GetDosAttributes()}");
            // Rekurencyjnie wywolujemy funkcje dla folderu
            DisplayDirectoryContents(subdirectory, indentLevel + 1);
        }
    }

}