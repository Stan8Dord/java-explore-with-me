package explorewithme.repository;

import explorewithme.model.event.Event;
import explorewithme.model.event.EventState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e " +
            "where e.state = explorewithme.model.event.EventState.PUBLISHED " +
            "and (lower(e.annotation) like lower(concat('%', ?1, '%')) " +
            "or lower(e.description) like lower(concat('%', ?1, '%'))) " +
            "and e.category.id in ?2 " +
            "and e.paid = ?3 " +
            "and (e.eventDate between ?4 and ?5) " +
            "and ((?6 = false) or (e.participantLimit = 0) " +
            "or (?6 = true and e.confirmedRequests < e.participantLimit))")
    Page<Event> getEventsCustom(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                              LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable page);

    @Query("select e from Event e " +
            "where e.initiator.id = ?1")
    List<Event> findByUser(long userId, Pageable page);

    @Query("select e from Event e " +
            "where e.initiator.id in ?1 " +
            "and e.state in ?2 " +
            "and e.category.id in ?3 " +
            "and (e.eventDate between ?4 and ?5)")
    Page<Event> getEventsByAdmin(Long[] users, List<EventState> states, Long[] categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Pageable page);
}
