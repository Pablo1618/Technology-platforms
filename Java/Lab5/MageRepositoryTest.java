package org.example;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

class MageRepositoryTest {

    private MageRepository repository;
    private Collection<Mage> collection;

    @BeforeEach
    void setUp() {
        collection = new ArrayList<>();
        repository = new MageRepository(collection);
    }

    @Test
    void testFindExistingMage() {

        Mage mage = new Mage("Pablo", 69);
        collection.add(mage);

        Optional<Mage> result = repository.find("Pablo");

        // Czy mag istnieje i czy jest to ten mag ktorego stworzylismy
        assertTrue(result.isPresent());
        assertEquals(mage, result.get());
    }

    @Test
    void testFindNonExistingMage() {

        Optional<Mage> result = repository.find("Pablo");
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteExistingMage() {

        Mage mage = new Mage("Pablo", 69);
        collection.add(mage);
        repository.delete("Pablo");
        assertFalse(collection.contains(mage));
    }

    @Test
    void testDeleteNonExistingMage() {
        // Sprawdzamy czy jest rzucany wyjatek gdy probujemy usunac nieistniejacego maga
        assertThrows(IllegalArgumentException.class, () -> {
            repository.delete("Pablo");
        });
    }

    @Test
    void testSaveNewMage() {

        Mage mage = new Mage("Pablo", 69);
        repository.save(mage);
        assertTrue(collection.contains(mage));
    }

    @Test
    void testSaveExistingMage() {

        Mage existingMage = new Mage("Pablo", 69);
        collection.add(existingMage);

        // Sprawdzamy czy zostanie wywolany wyjatek IllegalArgumentException
        // przy dodaniu maga o imieniu ktore juz istnieje
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(new Mage("Pablo", 100));
        });
    }
}
