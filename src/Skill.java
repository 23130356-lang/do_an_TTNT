public class Skill {
    String name;
    int energyCost;
    int baseDamage;
    String iconPath; // <-- 1. Thêm biến này

    // 2. Sửa hàm khởi tạo (constructor) để nhận 4 tham số
    public Skill(String name, int energyCost, int baseDamage, String iconPath) {
        this.name = name;
        this.energyCost = energyCost;
        this.baseDamage = baseDamage;
        this.iconPath = iconPath; // <-- 3. Gán giá trị cho biến mới
    }

    // (Giữ nguyên các hàm cũ)
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