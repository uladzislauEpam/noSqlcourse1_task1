package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDAOImplTest {

    private UserDAOImpl userDAO;

    @Mock
    private Storage storage;

    @Before
    public void setUp() {
        HashMap<String, String> inMemoryStorage = initInMemoryStorage();
        storage.setInMemoryStorage(inMemoryStorage);
        userDAO = new UserDAOImpl();
        userDAO.setStorage(storage);

        when(storage.getInMemoryStorage()).thenReturn(inMemoryStorage);
    }

    private HashMap<String, String> initInMemoryStorage() {
        HashMap<String, String> memoryStorage = new HashMap<>();
        memoryStorage.put("user:1", "'id' : 1, 'name' : 'Alan', 'email' : 'alan@gmail.com'");
        memoryStorage.put("user:2", "'id' : 2, 'name' : 'Kate', 'email' : 'kate@gmail.com'");
        memoryStorage.put("user:3", "'id' : 3, 'name' : 'Max', 'email' : 'max@gmail.com'");
        memoryStorage.put("user:4", "'id' : 4, 'name' : 'Sara', 'email' : 'sara@gmail.com'");
        memoryStorage.put("user:5", "'id' : 5, 'name' : 'Alex', 'email' : 'alex@gmail.com'");
        memoryStorage.put("user:6", "'id' : 6, 'name' : 'Alex', 'email' : 'anotheralex@gmail.com'");
        memoryStorage.put("ticket:1", "'id' : 1, 'userId' : 1, 'eventId' : 1, 'place' : 10, 'category' : 'BAR'");
        memoryStorage.put("ticket:2", "'id' : 2, 'userId' : 4, 'eventId' : 3, 'place' : 2, 'category' : 'PREMIUM'");
        memoryStorage.put("ticket:3", "'id' : 3, 'userId' : 2, 'eventId' : 2, 'place' : 4, 'category' : 'STANDARD'");
        memoryStorage.put("ticket:4", "'id' : 4, 'userId' : 1, 'eventId' : 4, 'place' : 20, 'category' : 'BAR'");
        memoryStorage.put("ticket:5", "'id' : 5, 'userId' : 5, 'eventId' : 1, 'place' : 11, 'category' : 'PREMIUM'");
        memoryStorage.put("ticket:6", "'id' : 6, 'userId' : 3, 'eventId' : 5, 'place' : 1, 'category' : 'STANDARD'");
        memoryStorage.put("event:1", "'id' : 1, 'title' : 'First event', 'date' : '18-05-2022 15:30'");
        memoryStorage.put("event:2", "'id' : 2, 'title' : 'Second event', 'date' : '15-05-2022 21:00'");
        memoryStorage.put("event:3", "'id' : 3, 'title' : 'Third event', 'date' : '16-05-2022 12:00'");
        memoryStorage.put("event:4", "'id' : 4, 'title' : 'Fourth event', 'date' : '15-05-2022 21:00'");
        memoryStorage.put("event:5", "'id' : 5, 'title' : 'Third event', 'date' : '25-05-2022 9:10'");
        memoryStorage.put("event:6", "'id' : 6, 'title' : 'Fifth event', 'date' : '1-06-2022 14:20'");
        return memoryStorage;
    }

    @Test
    public void getByIdWithExistsIdShouldBeOk() {
        User expectedUser = new UserImpl(3, "Max", "max@gmail.com");
        User actualUser = userDAO.getById(3L);

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getByIdWithNotExistsIdShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.getById(10L));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("Can not to find a user by id: 10", dbException.getMessage());
    }

    @Test
    public void getByEmailWithExistsEmailShouldBeOk() {
        User expectedUser = new UserImpl(3, "Max", "max@gmail.com");
        User actualUser = userDAO.getByEmail("max@gmail.com");

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getByEmailWithNotExistsEmailShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.getByEmail("notexists@gmail.com"));

        assertEquals("Can not to find a user by email: notexists@gmail.com", dbException.getMessage());
    }

    @Test
    public void getByNameWithExistsNameShouldBeOk() {
        int expectedSize = 2;
        List<User> actualUsersList = userDAO.getByName("Alex", 2, 1);

        assertEquals(expectedSize, actualUsersList.size());
    }

    @Test
    public void getByNameWithNotExistsNameShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.getByName("Not exists", 1, 1));

        assertEquals("List of all users by name 'Not exists' is empty", dbException.getMessage());
    }

    @Test
    public void getByNameWithExistsNameAndWrongPageSizeShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.getByName("Alex", -1, 1));

        assertEquals("The page size and page num must be greater than 0", dbException.getMessage());
    }

    @Test
    public void getAllShouldBeOk() {
        List<User> expectedListOfUsers = Arrays.asList(
                new UserImpl(1L, "Alan", "alan@gmail.com"),
                new UserImpl(4L, "Sara", "sara@gmail.com"),
                new UserImpl(5L, "Alex", "alex@gmail.com"),
                new UserImpl(6L, "Alex", "anotheralex@gmail.com")
        );
        List<User> actualListOfUsers = userDAO.getAll();

        assertTrue(actualListOfUsers.containsAll(expectedListOfUsers));
    }

    @Test
    public void insertWithUserShouldBeOk() {
        User expectedUser = new UserImpl("Test User", "testuser@gmail.com");
        User actualUser = userDAO.insert(expectedUser);
        expectedUser.setId(7L);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void insertWithNullUserShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.insert(null));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The user can not equal a null", dbException.getMessage());
    }

    @Test
    public void insertWithExistsEmailShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.insert(new UserImpl("Max", "max@gmail.com")));

        assertEquals("This email already exists", dbException.getMessage());
    }

    @Test
    public void updateWithExistsUserShouldBeOk() {
        String expectedName = "Test name";
        User expectedUser = userDAO.getById(1L);
        expectedUser.setName(expectedName);
        User actualUser = userDAO.update(expectedUser);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void updateWithNullUserShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.update(null));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The user can not equal a null", dbException.getMessage());
    }

    @Test
    public void updateUserWhichNotExistsShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.update(new UserImpl(10L, "Test User", "testuser@gmail.com")));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("The user with id 10 does not exist", dbException.getMessage());
    }

    @Test
    public void deleteExistsUserShouldBeOk() {
        boolean actualIsDeleted = userDAO.delete(2L);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void deleteUserWhichNotExistsShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.delete(10L));

        assertEquals("The user with id 10 does not exist", dbException.getMessage());
    }
}