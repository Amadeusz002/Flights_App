package pl.edu.agh.ki.lab.to.yourflights.utils;

public enum UserRole {
    USER("[ROLE_USER]"),
    AIRLINE("[ROLE_AIRLINE]"),
    ADMIN("[ROLE_ADMIN]");

    public final String role;

    UserRole(String role) {
        this.role = role;
    }
}
