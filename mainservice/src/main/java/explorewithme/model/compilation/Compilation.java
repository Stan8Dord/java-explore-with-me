package explorewithme.model.compilation;

import explorewithme.model.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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
    List<Event> events;

    public Compilation(boolean pinned, String title, List<Event> events) {
        this.pinned = pinned;
        this.title = title;
        this.events = events;
    }
}
