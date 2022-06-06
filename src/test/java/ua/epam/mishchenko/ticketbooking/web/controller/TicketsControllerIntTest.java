package ua.epam.mishchenko.ticketbooking.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class TicketsControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void bookTicketWithNewTicketsParametersShouldReturnPageWithTicket() throws Exception {
        this.mockMvc.perform(post("/tickets?userId=6&eventId=1&place=20&category=BAR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to book a ticket")));
    }

    @Test
    public void bookTicketWithExistingUserIdAndEventIdButNewPlaceShouldReturnPageWithTicket() throws Exception {
        this.mockMvc.perform(post("/tickets?userId=1&eventId=1&place=20&category=BAR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("BAR")));
    }

    @Test
    public void bookTicketWithBookedTicketParametersShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(post("/tickets?userId=1&eventId=1&place=10&category=BAR"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to book a ticket")));
    }

    @Test
    public void showTicketsByUserWithExistingUserIdShouldReturnPageWithListOfTickets() throws Exception {
        this.mockMvc.perform(get("/tickets/user/5?pageSize=1&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PREMIUM")));
    }

    @Test
    public void showTicketsByUserWithNotExistingUserIdShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/tickets/user/0?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find a user by id: 0")));
    }

    @Test
    public void showTicketsByUserWithExistingUserIdAndWrongParametersShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/tickets/user/1?pageSize=2&pageNum=-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find the tickets by user with id: 1")));
    }

    @Test
    public void showTicketsByEventWithExistingEventIdShouldReturnPageWithListOfTickets() throws Exception {
        this.mockMvc.perform(get("/tickets/event/3?pageSize=1&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PREMIUM")));
    }

    @Test
    public void showTicketsByEventWithNotExistingEventIdShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/tickets/event/0?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find an event by id: 0")));
    }

    @Test
    public void showTicketsByEventWithExistingEventIdAndWrongParametersShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/tickets/event/1?pageSize=2&pageNum=-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to find the tickets by event with id: 1")));
    }

    @Test
    public void cancelTicketWithCorrectParametersShouldReturnPageWithUser() throws Exception {
        this.mockMvc.perform(delete("/tickets/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The ticket with id: 3 successfully canceled")));
    }

    @Test
    public void cancelTicketWithWrongDateFormatParameterShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(delete("/tickets/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The ticket with id: 0 not canceled")));
    }
}
