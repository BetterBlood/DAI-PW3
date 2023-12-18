package ch.heigvd.server;

import lombok.Getter;
import lombok.Synchronized;

public class TowerDefense {
    @Getter(onMethod_={@Synchronized})
    private int hp;
    private int protection;

    TowerDefense(int baseHP, int baseProtection) {
        this.hp = baseHP;
        this.protection = baseProtection;
    }

    /**
     * @param damage damage to take
     * @return new tower hp
     */
    public synchronized int takeDamage(int damage) {
        return (hp -= (int) (damage * (1 - protection / 100.0)));
    }

    /**
     * @param heal hp to add
     * @return new tower hp
     */
    public synchronized int heal(int heal) {
        return (hp += heal);
    }

    /**
     * @param protection protection to add
     */
    public synchronized void addProtection(int protection) {
        this.protection += protection;
    }

    @Override
    public String toString() {
        return "TowerDefense{" +
                "hp=" + hp +
                ", protection=" + protection +
                '}';
    }
}
