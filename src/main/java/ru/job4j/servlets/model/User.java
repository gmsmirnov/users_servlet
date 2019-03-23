package ru.job4j.servlets.model;

import ru.job4j.servlets.Constants;

import java.util.Objects;

/**
 * User model description.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.3
 * @since 07/02/2019
 */
public class User {
    /**
     * Param "id" in in the POST request.
     */
    public static final String PARAM_ID = "id";

    /**
     * Param "login" in in the POST request.
     */
    public static final String PARAM_LOGIN = "login";

    /**
     * Param "email" in in the POST request.
     */
    public static final String PARAM_EMAIL = "email";

    /**
     * Param "password" in in the POST request.
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * Param "country" in in the POST request.
     */
    public static final String PARAM_COUNTRY = "country";

    /**
     * Param "city" in in the POST request.
     */
    public static final String PARAM_CITY = "city";

    /**
     * Param "role" in in the POST request.
     */
    public static final String PARAM_ROLE = "role";

    /**
     * User's id.
     */
    private int id;

    /**
     * User's login.
     */
    private String login;

    /**
     * User's e-mail.
     */
    private String email;

    /**
     * User's password.
     */
    private String password;

    /**
     * User's country.
     */
    private String country;

    /**
     * User's city.
     */
    private String city;

    /**
     * The role defined to this user.
     */
    private String role;

    /**
     * Creates a new user with the specified params.
     *
     * @param id - the specified user's id.
     * @param login - the specified user's login.
     * @param email - the specified user's email.
     * @param password - the specified user's password.
     * @param country - the specified user's country.
     * @param city - the specified user's city.
     */
    public User(int id, String login, String email, String password, String country, String city) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.role = Constants.ROLE_USER;
    }

    /**
     * Creates a new user with the specified params.
     *
     * @param login - the specified user's login.
     * @param email - the specified user's email.
     * @param password - the specified user's password.
     * @param country - the specified user's country.
     * @param city - the specified user's city.
     */
    public User(String login, String email, String password, String country, String city) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.role = Constants.ROLE_USER;
    }

    /**
     * Creates a new user with the specified params, including role.
     *
     * @param id - the specified user's id.
     * @param login - the specified user's login.
     * @param email - the specified user's email.
     * @param password - the specified user's password.
     * @param country - the specified user's country.
     * @param city - the specified user's city.
     * @param role - the specified role.
     */
    public User(int id, String login, String email, String password, String country, String city, String role) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.role = role;
    }

    /**
     * Creates a new user with the specified params, including role, without id.
     *
     * @param login - the specified user's login.
     * @param email - the specified user's email.
     * @param password - the specified user's password.
     * @param country - the specified user's country.
     * @param city - the specified user's city.
     * @param role - the specified role.
     */
    public User(String login, String email, String password, String country, String city, String role) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.role = role;
    }

    /**
     * Gets user's id.
     *
     * @return user's id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the specified user's id.
     *
     * @param id - the specified new user's id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets user's login.
     *
     * @return user's login.
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Sets the specified user's login.
     *
     * @param login - the specified user's login.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets user's e-mail.
     *
     * @return user's e-mail.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the specified user's e-mail.
     *
     * @param email - the specified user's e-mail.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return the user's password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the specified user's password.
     *
     * @param password - the specified user's password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets user's country.
     *
     * @return user's country.
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * Sets the specified user's county.
     *
     * @param country - the specified user's county.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets user's city.
     *
     * @return user's city.
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Sets the specified user's city.
     *
     * @param city - the specified user's city.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the role defined to this user.
     *
     * @return - the role specified to this user.
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Sets the specified role to this user.
     *
     * @param role - the specified role.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Checks this user equivalence to the specified user.
     *
     * @param o - the specified user.
     * @return true if users are equals.
     */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (o == null || getClass() != o.getClass()) {
            result = false;
        } else {
            User user = (User) o;
            result = Objects.equals(this.login, user.login)
                    && Objects.equals(this.email, user.email)
                    && Objects.equals(this.password, user.password)
                    && Objects.equals(this.country, user.country)
                    && Objects.equals(this.city, user.city)
                    && Objects.equals(this.role, user.role);
        }
        return result;
    }

    /**
     * Calculates this user's hash code.
     *
     * @return this user's hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.login, this.email, this.password, this.country, this.city, this.role);
    }

    /**
     * Presents this user in a String-view.
     *
     * @return this user's String-view.
     */
    @Override
    public String toString() {
        return String.format("User {id=%d, login='%s', email='%s', country='%s', city='%s', role='%s'}",
                this.id, this.login, this.email, this.country, this.city, this.role);
    }
}