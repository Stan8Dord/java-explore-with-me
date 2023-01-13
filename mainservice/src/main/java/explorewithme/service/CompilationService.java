package explorewithme.service;

import explorewithme.model.compilation.CompilationDto;
import explorewithme.model.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compId);

    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto);

    void addEventToCompilation(long compId, long eventId);

    void pinCompilation(long compId);

    void deleteCompilation(long compId);

    void deleteEventFromCompilation(long compId, long eventId);

    void unpinCompilation(long compId);
}
