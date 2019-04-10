package ru.job4j.servlets.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.servlets.Constants;
import ru.job4j.servlets.Validate;
import ru.job4j.servlets.ValidateService;
import ru.job4j.servlets.ValidateServiceStub;
import ru.job4j.servlets.dao.exception.AlreadyExistsModelWithSuchLoginException;
import ru.job4j.servlets.dao.exception.DaoSystemException;
import ru.job4j.servlets.dao.exception.NoSuchIdException;
import ru.job4j.servlets.dao.exception.NullArgumentException;
import ru.job4j.servlets.dao.impl.UserDaoMemory;
import ru.job4j.servlets.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Controllers' test. Uses mockito to create request, response, session mocks; and powermock to replace static
 * DB storage to memory storage.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 05/04/2019
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ValidateService.class)
@PowerMockIgnore({"org.apache.logging.log4j.LogManager", "org.apache.logging.log4j.Logger"})
public class ControllersTest {
    private Validate validate;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @Before
    public void init() {
        this.validate = new ValidateServiceStub();
        mockStatic(ValidateService.class);
        when(ValidateService.getSingletonValidateServiceInstance()).thenReturn(this.validate);
        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
        this.session = mock(HttpSession.class);
        when(this.request.getRequestDispatcher(Constants.PAGE_JSP_CREATE)).thenReturn(mock(RequestDispatcher.class));
        when(this.request.getSession()).thenReturn(this.session);
    }

    private void addUserPrepare(User user) {
        when(this.request.getParameter(User.PARAM_LOGIN)).thenReturn(user.getLogin());
        when(this.request.getParameter(User.PARAM_EMAIL)).thenReturn(user.getEmail());
        when(this.request.getParameter(User.PARAM_PASSWORD)).thenReturn(user.getPassword());
        when(this.request.getParameter(User.PARAM_CITY)).thenReturn(user.getCity());
        when(this.request.getParameter(User.PARAM_COUNTRY)).thenReturn(user.getCountry());
        when(this.request.getParameter(User.PARAM_ROLE)).thenReturn(Constants.ROLE_USER);
        when(this.session.getAttribute(Constants.ATTR_LOGIN)).thenReturn(user.getLogin());
    }

    @Test
    public void whenAddUserThenStoreIt() throws ServletException, IOException, DaoSystemException {
        UserCreateController createUser = new UserCreateController();
        User vasya = new User("Vasya", "vasya@mail.ru", "vasya", "RF", "Nsk");
        this.addUserPrepare(vasya);
        createUser.doPost(this.request, this.response);
        List<User> users = new LinkedList<User>(UserDaoMemory.getSingletonMemoryInstance().findAll());
        assertThat(users, hasItem(vasya));
    }

    private void deleteUserPrepare(User user) {
        when(this.request.getParameter(User.PARAM_ID)).thenReturn(String.valueOf(user.getId()));
        when(this.session.getAttribute(Constants.ATTR_CURRENT_USER)).thenReturn(user);
        when(this.request.getRequestDispatcher(Constants.PAGE_JSP_LIST)).thenReturn(mock(RequestDispatcher.class));
    }

    @Test
    public void whenDeleteUserThenNoUserInStorage() throws ServletException, IOException {
        UserCreateController createUser = new UserCreateController();
        UsersListController usersListController = new UsersListController();
        User vasya = new User("Vasya", "vasya@mail.ru", "vasya", "RF", "Nsk");
        User misha = new User("Misha", "misha@mail.ru", "misha", "RF", "Nsk");
        this.addUserPrepare(vasya);
        createUser.doPost(this.request, this.response);
        this.addUserPrepare(misha);
        createUser.doPost(this.request, this.response);
        vasya.setId((UserDaoMemory.getSingletonMemoryInstance().findByLogin(vasya.getLogin())).getId());
        misha.setId((UserDaoMemory.getSingletonMemoryInstance().findByLogin(misha.getLogin())).getId());
        List<User> users = new LinkedList<User>(UserDaoMemory.getSingletonMemoryInstance().findAll());
        assertThat(users, hasItem(vasya));
        assertThat(users, hasItem(misha));
        this.deleteUserPrepare(vasya);
        usersListController.doPost(this.request, this.response);
        users = new LinkedList<User>(UserDaoMemory.getSingletonMemoryInstance().findAll());
        assertThat(users.contains(misha), is(true));
        assertThat(users.contains(vasya), is(false));
    }

    private void updatePrepare(User user) {
        when(this.request.getParameter(User.PARAM_ID)).thenReturn(String.format("%d", user.getId()));
        this.addUserPrepare(user);
        when(this.request.getRequestDispatcher(Constants.PAGE_JSP_UPDATE)).thenReturn(mock(RequestDispatcher.class));
    }

    @Test
    public void whenUpdateUserThenUpdatedUserInTheStorage() throws ServletException, IOException {
        UserCreateController createUser = new UserCreateController();
        UserUpdateController updateUser = new UserUpdateController();
        User vasya = new User("Vasya", "vasya@mail.ru", "vasya", "RF", "Nsk");
        this.addUserPrepare(vasya);
        createUser.doPost(this.request, this.response);
        vasya.setId((UserDaoMemory.getSingletonMemoryInstance().findByLogin(vasya.getLogin())).getId());
        vasya.setPassword("new_vasya@yandex.ru");
        vasya.setPassword("god");
        this.updatePrepare(vasya);
        updateUser.doPost(this.request, this.response);
        User requestedUser = UserDaoMemory.getSingletonMemoryInstance().findById(vasya.getId());
        assertThat(requestedUser.equals(vasya), is(true));
    }

    @After
    public void clearMemory() throws DaoSystemException, NullArgumentException, NoSuchIdException, AlreadyExistsModelWithSuchLoginException {
        List<User> users = new LinkedList<User>(this.validate.findAll());
        for (User user : users) {
            this.validate.delete(user);
        }
        this.validate.add(new User(0, "root", "root@mail.net", "root", "Russia", "Moscow", "admin"));
    }
}