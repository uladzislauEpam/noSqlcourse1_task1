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
public class EventsControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showEventByIdWithExistingEventIdShouldReturnPageWithEvent() throws Exception {
        this.mockMvc.perform(get("/events/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("First event")));
    }

    @Test
    public void showEventByIdWithNotExistingEventIdShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/events/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to get an event by id: 0")));
    }

    @Test
    public void showEventsByTitleWithExistingEventTitleShouldReturnPageWithListOfEvents() throws Exception {
        this.mockMvc.perform(get("/events/title/Third event?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Third event")));
    }

    @Test
    public void showEventsByTitleWithNotExistingEventTitleShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/events/title/Not Existing Title?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to get events by title: Not Existing Title")));
    }

    @Test
    public void showEventsByTitleWithExistingEventTitleAndWrongParametersShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/events/title/Third event?pageSize=2&pageNum=-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to get events by title: Third event")));
    }

    @Test
    public void showEventsForDayWithExistingEventDayShouldReturnPageWithListOfEvents() throws Exception {
        this.mockMvc.perform(get("/events/day/2022-05-15 21:00?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Second event")))
                .andExpect(content().string(containsString("Fourth event")));
    }

    @Test
    public void showEventsForDayWithNotExistingEventDayShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/events/day/15-05-2015 21:00?pageSize=2&pageNum=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to get events for day: 15-05-2015 21:00")));
    }

    @Test
    public void showEventsForDayWithExistingEventDayAndWrongParametersShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(get("/events/day/15-05-2022 21:00?pageSize=2&pageNum=-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to get events for day: 15-05-2022 21:00")));
    }

    @Test
    public void createEventWithCorrectParametersShouldReturnPageWithEvent() throws Exception {
        this.mockMvc.perform(post("/events?title=Test Title&day=2015-05-15 21:00&price=250.00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test Title")));
    }

    @Test
    public void createEventWithWrongDateFormatParameterShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(post("/events?title=Test Title&day=15-05.2022 21:00&price=250"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to parse string 15-05.2022 21:00 to date object")));
    }

    @Test
    public void createEventWithExistingEventShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(post("/events?title=First event&day=2022-05-18 15:30&price=250"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to create an event")));
    }

    @Test
    public void updateEventWithCorrectParametersShouldReturnPageWithEvent() throws Exception {
        this.mockMvc.perform(put("/events?id=3&title=Test Title&day=2022-05-15 21:00&price=250"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test Title")));
    }

    @Test
    public void updateEventWithWrongDateFormatParameterShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(put("/events?id=2&title=Test Title&day=15-05.2022 21:00&price=250"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to parse string 15-05.2022 21:00 to date object")));
    }

    @Test
    public void updateEventWithExistingEventShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(put("/events?id=0&title=First event&day=2022-05-18 15:30&price=250"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Can not to update an event with id: 0")));
    }

    @Test
    public void deleteEventWithCorrectParametersShouldReturnPageWithEvent() throws Exception {
        this.mockMvc.perform(delete("/events/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The event with id 3 successfully deleted")));
    }

    @Test
    public void deleteEventWithWrongDateFormatParameterShouldReturnPageWithMessage() throws Exception {
        this.mockMvc.perform(delete("/events/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The event with id 0 not deleted")));
    }
}
