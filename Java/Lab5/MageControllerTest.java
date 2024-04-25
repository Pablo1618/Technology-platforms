package org.example;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

class MageControllerTest {

    private MageRepository repository;
    private MageController controller;

    @BeforeEach
    void setUp() {
        // Mock obiektu repozytorium, bo jego nie testujemy (jest testowany w MageRepositoryTest)
        repository = mock(MageRepository.class);
        controller = new MageController(repository);
    }

    @Test
    void testFindExistingMage() {

        Mage mage = new Mage("Pablo", 10);
        when(repository.find("Pablo")).thenReturn(Optional.of(mage));

        String result = controller.find("Pablo");

        assertEquals("Mage name: Pablo, Level: 10", result);
    }

    @Test
    void testFindNonExistingMage() {

        when(repository.find("Pablo")).thenReturn(Optional.empty());

        String result = controller.find("Pablo");

        assertEquals("Mage not found", result);
    }

    @Test
    void testDeleteExistingMage() {

        when(repository.find("Pablo")).thenReturn(Optional.of(new Mage("Pablo", 20)));

        String result = controller.delete("Pablo");

        assertEquals("done", result);
        verify(repository, times(1)).delete("Pablo");
    }

    @Test
    void testDeleteNonExistingMage() {

        when(repository.find("Pablo")).thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException()).when(repository).delete("Pablo");

        String result = controller.delete("Pablo");

        assertEquals("not found", result);
        verify(repository, times(1)).delete("Pablo");
    }

    @Test
    void testSaveNewMage() {
        when(repository.find("Pablo")).thenReturn(Optional.empty());

        String result = controller.save("Pablo", 10);

        assertEquals("saved", result);
        verify(repository, times(1)).save(any(Mage.class));
    }


    @Test
    void testSaveExistingMage() {

        doThrow(new IllegalArgumentException()).when(repository).save(any(Mage.class));

        String result = controller.save("Pablo", 10);

        assertEquals("mage already exists", result);
        verify(repository).save(any(Mage.class));
    }

}
