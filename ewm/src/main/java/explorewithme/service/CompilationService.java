package explorewithme.service;

import explorewithme.model.compilation.CompilationDto;
import explorewithme.model.compilation.NewCompilationDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compId, HttpServletRequest request);

    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto, HttpServletRequest request);

    void addEventToCompilation(long compId, long eventId, HttpServletRequest request);

    void pinCompilation(long compId);

    void deleteCompilation(long compId);

    void deleteEventFromCompilation(long compId, long eventId, HttpServletRequest request);

    void unpinCompilation(long compId);
}
