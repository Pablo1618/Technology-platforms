package org.example;
import org.example.Mage;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
class Tower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int height;


    @OneToMany(mappedBy = "tower", cascade = CascadeType.ALL)
    private List<Mage> mages = new ArrayList<>();

    public Tower() {}

    public Tower(String name) {
        this.name = name;
        this.height = 0;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public int getHeight() {
        return height;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setHeight(int newHeight) {
        this.height = newHeight;
    }
    public List<Mage> getMages() {
        return mages;
    }
    public void setMages(List<Mage> mages) {
        this.mages = mages;
    }
    public void addMage(Mage mage) {
        mage.setTower(this);
        mages.add(mage);
        setHeight(getHeight()+1);
    }
}