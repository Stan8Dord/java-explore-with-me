package explorewithme.model.compilation;

import explorewithme.model.compilation.dto.CompilationDto;
import explorewithme.model.compilation.dto.NewCompilationDto;
import explorewithme.model.event.Event;
import explorewithme.model.event.EventMapper;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static Compilation toCompilation(CompilationDto dto, List<Event> events) {
        return new Compilation(
                dto.getId(),
                dto.isPinned(),
                dto.getTitle(),
                events);
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList()),
                compilation.isPinned(),
                compilation.getTitle());
    }

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        return new Compilation(
                null,
                dto.isPinned(),
                dto.getTitle(),
                events);
    }
}
