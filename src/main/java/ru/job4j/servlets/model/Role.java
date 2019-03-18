package ru.job4j.servlets.model;

import java.util.List;
import java.util.Objects;

/**
 * Role model description.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 26/02/2019
 */
public class Role {
    private String role;

    public Role(String role) {
        this.role = role;
    }

    public Role(String role, List<User> users) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (o == null || getClass() != o.getClass()) {
            result = false;
        } else {
            Role role1 = (Role) o;
            result = Objects.equals(this.role, role1.role);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.role);
    }

    @Override
    public String toString() {
        return String.format("Role {role='%s'", this.role);
    }
}