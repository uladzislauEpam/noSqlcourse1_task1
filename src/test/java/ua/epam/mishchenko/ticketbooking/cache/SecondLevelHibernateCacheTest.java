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
    public void getEventByIdWithExistsIdShouldCacheWorks() {
        assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheMissCount());

        Session session = sessionFactory.openSession();
        session.find(Event.class, 1L);

        assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        assertEquals(1, sessionFactory.getStatistics().getSecondLevelCacheMissCount());

        session = sessionFactory.openSession();
        session.find(Event.class, 1L);

        assertEquals(1, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        assertEquals(1, sessionFactory.getStatistics().getSecondLevelCacheMissCount());
    }

    @Test
    public void getEventsShouldQueryCacheWorks() {
        assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheMissCount());

        Session session = sessionFactory.openSession();
        session.createQuery("select e from Event e order by e.id desc")
                .setMaxResults(5)
                .setCacheable(true)
                .list();

        assertEquals(0, sessionFactory.getStatistics().getQueryCacheHitCount());
        assertEquals(1, sessionFactory.getStatistics().getQueryCacheMissCount());

        session = sessionFactory.openSession();
        session.createQuery("select e from Event e order by e.id desc")
                .setMaxResults(5)
                .setCacheable(true)
                .list();

        assertEquals(1, sessionFactory.getStatistics().getQueryCacheHitCount());
        assertEquals(1, sessionFactory.getStatistics().getQueryCacheMissCount());
        assertEquals(5, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
    }
}
