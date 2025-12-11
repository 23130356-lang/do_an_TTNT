public class Skill {
    String name;
    int energyCost;
    int baseDamage;
    String iconPath;

    public Skill(String name, int energyCost, int baseDamage, String iconPath) {
        this.name = name;
        this.energyCost = energyCost;
        this.baseDamage = baseDamage;
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public String getIconPath() {
        return iconPath;
    }
}