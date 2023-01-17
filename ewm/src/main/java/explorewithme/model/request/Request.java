package explorewithme.model.request;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDateTime created;
    @Column(name = "event_id", nullable = false)
    private long event;
    @Column(name = "user_id", nullable = false)
    private long requester;
    @Column
    private String status;
}
