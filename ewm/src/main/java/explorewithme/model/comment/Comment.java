package explorewithme.model.comment;

import explorewithme.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_text", nullable = false)
    private String text;
    @Enumerated(EnumType.STRING)
    private CommentState state;
    @Column
    private int version;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    @Column
    private LocalDateTime created;
}
