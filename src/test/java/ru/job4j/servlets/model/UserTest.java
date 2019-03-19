package ru.job4j.servlets.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * User model test.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 19/03/2019
 */
public class UserTest {
    private User user;

    @Before
    public void init() {
        this.user = new User(
          "1", "login", "user@mail.net", "password", "Russia", "Moscow", "admin"
        );
    }

    @Test
    public void whenGetIdThenTrue() {
        assertThat(this.user.getId(), is(1));
    }

    @Test
    public void whenSetIdThenTrue() {
        this.user.setId(2);
        assertThat(this.user.getId(), is(2));
    }

    @Test
    public void whenGetLoginThenTrue() {
        assertThat(this.user.getLogin(), is("login"));
    }

    @Test
    public void whenSetLoginThenTrue() {
        this.user.setLogin("Tom");
        assertThat(this.user.getLogin(), is("Tom"));
    }

    @Test
    public void whenGetEmailThenTrue() {
        assertThat(this.user.getEmail(), is("user@mail.net"));
    }

    @Test
    public void whenSetEmailThenTrue() {
        this.user.setEmail("admin@mail.net");
        assertThat(this.user.getEmail(), is("admin@mail.net"));
    }

    @Test
    public void whenGetPasswordThenTrue() {
        assertThat(this.user.getPassword(), is("password"));
    }

    @Test
    public void whenSetPasswordThenTrue() {
        this.user.setPassword("new password");
        assertThat(this.user.getPassword(), is("new password"));
    }

    @Test
    public void whenGetCountryThenTrue() {
        assertThat(this.user.getCountry(), is("Russia"));
    }

    @Test
    public void whenSetCountryThenTrue() {
        this.user.setCountry("China");
        assertThat(this.user.getCountry(), is("China"));
    }

    @Test
    public void whenGetCityThenTrue() {
        assertThat(this.user.getCity(), is("Moscow"));
    }

    @Test
    public void whenSetCityThenTrue() {
        this.user.setCity("Pekin");
        assertThat(this.user.getCity(), is("Pekin"));
    }

    @Test
    public void whenGetRoleThenTrue() {
        assertThat(this.user.getRole(), is("admin"));
    }

    @Test
    public void whenSetRoleThenTrue() {
        this.user.setRole("user");
        assertThat(this.user.getRole(), is("user"));
    }

    @Test
    public void whenUsersAreEqualsThenTrue1() {
        User sameUser = new User(
                "2", "login", "user@mail.net", "password", "Russia", "Moscow", "admin"
        );
        assertThat(this.user.equals(sameUser), is(true));
    }

    @Test
    public void whenUsersAreEqualsThenTrue2() {
        User sameUser = this.user;
        assertThat(this.user.equals(sameUser), is(true));
    }

    @Test
    public void whenUsersAreNotEqualsThenFalse1() {
        User sameUser = new User(
                "2", "Tom", "user@mail.net", "password", "Russia", "Moscow", "admin"
        );
        assertThat(this.user.equals(sameUser), is(false));
    }

    @Test
    public void whenUsersAreNotEqualsThenFalse2() {
        User sameUser = new User(
                "2", "login", "root@mail.net", "password", "Russia", "Moscow", "admin"
        );
        assertThat(this.user.equals(sameUser), is(false));
    }

    @Test
    public void whenUsersAreNotEqualsThenFalse3() {
        User sameUser = new User(
                "2", "login", "user@mail.net", "pass", "Russia", "Moscow", "admin"
        );
        assertThat(this.user.equals(sameUser), is(false));
    }

    @Test
    public void whenUsersAreNotEqualsThenFalse4() {
        User sameUser = new User(
                "2", "login", "user@mail.net", "password", "China", "Moscow", "admin"
        );
        assertThat(this.user.equals(sameUser), is(false));
    }

    @Test
    public void whenUsersAreNotEqualsThenFalse5() {
        User sameUser = new User(
                "2", "login", "user@mail.net", "password", "Russia", "Pekin", "admin"
        );
        assertThat(this.user.equals(sameUser), is(false));
    }

    @Test
    public void whenUsersAreNotEqualsThenFalse6() {
        User sameUser = new User(
                "2", "login", "user@mail.net", "password", "Russia", "Moscow", "user"
        );
        assertThat(this.user.equals(sameUser), is(false));
    }

    @Test
    public void whenAnotherUserIsNullThenFalse() {
        User another = null;
        assertThat(this.user.equals(another), is(false));
    }

    @Test
    public void whenRightFormatToStringThenTrue() {
        String expected = "User {id='1', login='login', email='user@mail.net', country='Russia', city='Moscow', role='admin'";
        assertThat(this.user.toString(), is(expected));
    }
}