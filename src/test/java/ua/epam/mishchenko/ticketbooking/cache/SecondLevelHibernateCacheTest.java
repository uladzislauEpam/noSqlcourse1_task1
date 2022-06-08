package ua.epam.mishchenko.ticketbooking.cache;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.service.impl.EventServiceImpl;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecondLevelHibernateCacheTest {

    @Autowired
    EventServiceImpl eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void getEventByIdWithExistsIdShouldCacheShouldBeWork() {
        assertEquals(sessionFactory.getStatistics().getSecondLevelCacheHitCount(), 0);
        assertEquals(sessionFactory.getStatistics().getSecondLevelCacheMissCount(), 0);

        Session session = sessionFactory.openSession();
        session.find(Event.class, 1L);

        assertEquals(sessionFactory.getStatistics().getSecondLevelCacheHitCount(), 0);
        assertEquals(sessionFactory.getStatistics().getSecondLevelCacheMissCount(), 1);

        session = sessionFactory.openSession();
        session.find(Event.class, 1L);

        assertEquals(sessionFactory.getStatistics().getSecondLevelCacheHitCount(), 1);
        assertEquals(sessionFactory.getStatistics().getSecondLevelCacheMissCount(), 1);
    }
}
