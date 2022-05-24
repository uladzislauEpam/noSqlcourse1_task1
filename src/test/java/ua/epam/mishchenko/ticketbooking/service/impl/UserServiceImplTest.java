package ua.epam.mishchenko.ticketbooking.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.epam.mishchenko.ticketbooking.dao.impl.UserDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private UserDAOImpl userDAO;

    @Before
    public void setUp() {
        userService = new UserServiceImpl();
        userService.setUserDAO(userDAO);
    }


    @Test
    public void getUserByIdWithExistsIdShouldBeOk() {
        User expectedUser = new UserImpl(3, "Max", "max@gmail.com");

        when(userDAO.getById(anyLong())).thenReturn(expectedUser);

        User actualUser = userService.getUserById(3L);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getUserByIdWithExceptionShouldReturnNull() {
        when(userDAO.getById(anyLong())).thenThrow(DbException.class);

        User actualUser = userService.getUserById(10L);

        assertNull(actualUser);
    }

    @Test
    public void getUserByEmailWithExistsEmailShouldBeOk() {
        User expectedUser = new UserImpl(3, "Max", "max@gmail.com");

        when(userDAO.getByEmail(anyString())).thenReturn(expectedUser);

        User actualUser = userService.getUserByEmail("max@gmail.com");

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getUserByEmailWithExceptionShouldReturnNull() {
        when(userDAO.getByEmail(anyString())).thenThrow(DbException.class);

        User actualUser = userService.getUserByEmail("notexists@gmail.com");

        assertNull(actualUser);
    }

    @Test
    public void getUserByEmailWithEmptyEmailShouldReturnNull() {
        User actualUserByEmail = userService.getUserByEmail("");

        assertNull(actualUserByEmail);
    }

    @Test
    public void getUserByNameWithExistsNameShouldBeOk() {
        List<User> expectedUsersByName = Arrays.asList(
                new UserImpl(3L, "Max", "max@gmail.com"),
                new UserImpl(4L, "Max", "max123@gmail.com")
        );

        when(userDAO.getByName(anyString(), anyInt(), anyInt())).thenReturn(expectedUsersByName);

        List<User> actualUsersByName = userService.getUsersByName("Max", 2, 1);

        assertEquals(expectedUsersByName, actualUsersByName);
    }

    @Test
    public void getUserByNameWithExceptionShouldReturnNull() {
        when(userDAO.getByName(anyString(), anyInt(), anyInt())).thenThrow(DbException.class);

        List<User> actualListOfUsers = userService.getUsersByName("Not exists", 1, 1);

        assertNull(actualListOfUsers);
    }

    @Test
    public void getUserByNameWithEmptyNameShouldReturnNull() {
        List<User> actualUsersByName = userService.getUsersByName("", 1, 1);

        assertNull(actualUsersByName);
    }

    @Test
    public void createUserWithUserShouldBeOk() {
        User expectedUser = new UserImpl(1L, "Test User", "testuser@gmail.com");

        when(userDAO.insert(any())).thenReturn(expectedUser);

        User actualUser = userService.createUser(expectedUser);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void createUserWithExceptionShouldReturnNull() {
        when(userDAO.insert(any())).thenThrow(DbException.class);

        User actualUser = userService.createUser(new UserImpl("Max", "max@gmail.com"));

        assertNull(actualUser);
    }

    @Test
    public void updateUserWithExistsUserShouldBeOk() {
        User expectedUser = new UserImpl(1L, "Test User", "testuser@gmail.com");

        when(userDAO.update(any())).thenReturn(expectedUser);

        User actualUser = userService.updateUser(expectedUser);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void updateUserWithExceptionShouldThrowException() {
        when(userDAO.update(any())).thenThrow(DbException.class);

        User actualUser = userService.updateUser(new UserImpl());

        assertNull(actualUser);
    }

    @Test
    public void deleteUserExistsUserShouldReturnTrue() {
        when(userDAO.delete(anyLong())).thenReturn(true);

        boolean actualIsDeleted = userService.deleteUser(2);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void deleteUserWhichNotExistsShouldReturnFalse() {
        when(userDAO.delete(anyLong())).thenThrow(DbException.class);

        boolean isRemoved = userService.deleteUser(10L);

        assertFalse(isRemoved);
    }

}