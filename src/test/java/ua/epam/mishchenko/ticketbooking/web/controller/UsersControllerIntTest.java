package ua.epam.mishchenko.ticketbooking.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@Sql(value = {"classpath:sql/clear-database.sql", "classpath:sql/insert-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:sql/clear-database.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsersControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showUserByIdWithExistingUserIdShouldReturnPageWithUser() throws Exception {
        this.mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alan")));
    }

    @Test
    public void showUserByIdWithNotExistingUserIdShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/users/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find user by id: 0")));
    }

    @Test
    public void showUsersByNameWithExistingUserNameShouldReturnPageWithListOfUsers() throws Exception {
        this.mockMvc.perform(get("/users/name/Alex?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("alex@gmail.com")))
                .andExpect(content().string(containsString("anotheralex@gmail.com")));
    }

    @Test
    public void showUsersByNameWithNotExistingUserNameShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/users/name/Not Existing Name?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find users by name: Not Existing Name")));
    }

    @Test
    public void showUsersByNameWithExistingUserNameAndWrongParametersShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/users/name/Alan?pageSize=2&pageNum=-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find users by name: Alan")));
    }

    @Test
    public void showUserByEmailWithNotExistingUserEmailShouldReturnPageWithUser() throws Exception {
        this.mockMvc.perform(get("/users/email/alan@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alan")));
    }

    @Test
    public void showUserByEmailWithNotExistingUserEmailShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/users/email/notexisting@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find user by email: notexisting@gmail.com")));
    }

    @Test
    public void createUserWithNotExistingNameAndEmailShouldReturnPageWithUser() throws Exception {
        this.mockMvc.perform(post("/users?name=User&email=user@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("user@gmail.com")));
    }

    @Test
    public void createUserWithExistingNameAndEmailShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(post("/users?name=Alan&email=alan@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to create user with name - Alan and email - alan@gmail.com")));
    }

    @Test
    public void updateUserWithWrongDateFormatParameterShouldReturnPageWithUser() throws Exception {
        this.mockMvc.perform(put("/users?id=2&name=Test&email=test@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test@gmail.com")));
    }

    @Test
    public void updateUserWithExistingUserShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(put("/users?id=0&name=Test&email=test@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to update user with id: 0")));
    }

    @Test
    public void deleteUserWithCorrectParametersShouldReturnPageWithUser() throws Exception {
        this.mockMvc.perform(delete("/users/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The user with id: 3 successfully removed")));
    }

    @Test
    public void deleteUserWithWrongDateFormatParameterShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(delete("/users/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The user with id: 0 not removed")));
    }
}
