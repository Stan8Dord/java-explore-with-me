package explorewithme.model.compilation;

import explorewithme.model.event.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    @NotNull
    private long id;
    private List<EventShortDto> events;
    @NotNull
    private boolean pinned;
    @NotNull
    private String title;
}
