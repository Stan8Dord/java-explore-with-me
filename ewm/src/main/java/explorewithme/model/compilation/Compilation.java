package explorewithme.model.compilation;

import explorewithme.model.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "compilations", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private boolean pinned = false;
    @Column(nullable = false)
    private String title;
    @ManyToMany
    @JoinTable(
            name = "events_compilations",
            joinColumns = { @JoinColumn(name = "compilation_id") },
            inverseJoinColumns = { @JoinColumn(name = "event_id")}
    )
    private List<Event> events;
}
