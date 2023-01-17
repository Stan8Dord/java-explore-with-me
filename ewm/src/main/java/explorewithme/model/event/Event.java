package explorewithme.model.event;

import explorewithme.model.category.Category;
import explorewithme.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String annotation;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "confirmed_requests")
    private long confirmedRequests;
    @Column(name = "created")
    private LocalDateTime createdOn;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User initiator;
    @Column(nullable = false)
    private float lat;
    @Column(nullable = false)
    private float lon;
    @Column(nullable = false)
    private boolean paid;
    @Column(name = "participation_limit")
    private int participantLimit = 0;
    @Column(name = "published_date", nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = "moderation")
    private boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(nullable = false)
    private String title;
    @Column
    private long views;
}
