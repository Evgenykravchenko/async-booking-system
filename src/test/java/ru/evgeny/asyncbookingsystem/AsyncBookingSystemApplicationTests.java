package ru.evgeny.asyncbookingsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.evgeny.asyncbookingsystem.repository.BookingRepository;
import ru.evgeny.asyncbookingsystem.repository.BookingRequestRepository;
import ru.evgeny.asyncbookingsystem.repository.ResourceRepository;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration,"
                + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
})
class AsyncBookingSystemApplicationTests {

    @MockitoBean
    private ResourceRepository resourceRepository;

    @MockitoBean
    private BookingRepository bookingRepository;

    @MockitoBean
    private BookingRequestRepository bookingRequestRepository;

    @Test
    void contextLoads() {
    }

}
