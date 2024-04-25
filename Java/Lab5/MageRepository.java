package org.example;
import java.util.Collection;
import java.util.Optional;

public class MageRepository {
    private Collection<Mage> collection;

    public MageRepository(Collection<Mage> collection) {
        this.collection = collection;
    }

    public Optional<Mage> find(String name) {
        for (Mage mage : collection) {
            if (mage.getName().equals(name)) {
                return Optional.of(mage);
            }
        }
        return Optional.empty();
    }

    public void delete(String name) {
        if (find(name).isEmpty()) {
            throw new IllegalArgumentException("Mage: " + name + " does not exist");
        }
        collection.removeIf(mage -> mage.getName().equals(name));
    }

    public void save(Mage mage) {
        if (find(mage.getName()).isPresent()) {
            throw new IllegalArgumentException("Mage: " + mage.getName() + " already exists");
        }
        collection.add(mage);
    }
}
