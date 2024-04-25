package org.example;
import java.util.Comparator;

public class MageLevelComparator implements Comparator<Mage> {
    @Override
    public int compare(Mage mage1, Mage mage2) {

        if (mage1.getLevel() != mage2.getLevel()) {
            return Integer.compare(mage1.getLevel(), mage2.getLevel());
        } else {
            return mage1.getName().compareTo(mage2.getName());
        }
    }
}
