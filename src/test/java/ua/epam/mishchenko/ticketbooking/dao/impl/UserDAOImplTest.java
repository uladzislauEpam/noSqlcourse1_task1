package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;
import ua.epam.mishchenko.ticketbooking.postprocessor.FileReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:/test-applicationContext.xml"})
public class UserDAOImplTest {

    private UserDAOImpl userDAO;

    @Mock
    private Storage storage;

    @Autowired
    private FileReader fileReader;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, String> inMemoryStorage = fileReader.readPreparedDataFromFile();
        storage.setInMemoryStorage(inMemoryStorage);
        userDAO = new UserDAOImpl();
        userDAO.setStorage(storage);

        when(storage.getInMemoryStorage()).thenReturn(inMemoryStorage);
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
    public void updateWithExistsUserAndExistsEmailShouldBeOk() {
        String newEmail = "alan@gmail.com";
        User user = userDAO.getById(2L);
        user.setEmail(newEmail);
        DbException dbException = assertThrows(DbException.class,
                () -> userDAO.update(user));

        assertEquals("This email already exists", dbException.getMessage());
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