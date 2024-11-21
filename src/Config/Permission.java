package Config;

public enum Permission {
    READ(1),
    EDIT(2),
    ADMIN(3);

    private final int value;

    Permission(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean greatherThan(Permission permission) {
        return this.value > permission.value;
    }

    public boolean lessThan(Permission permission) {
        return this.value < permission.value;
    }
}
