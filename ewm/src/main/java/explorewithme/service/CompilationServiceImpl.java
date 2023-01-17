package explorewithme.service;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.NotFoundCompilationException;
import explorewithme.exceptions.NotFoundEventException;
import explorewithme.model.compilation.Compilation;
import explorewithme.model.compilation.CompilationDto;
import explorewithme.model.compilation.CompilationMapper;
import explorewithme.model.compilation.NewCompilationDto;
import explorewithme.model.event.Event;
import explorewithme.repository.CompilationRepository;
import explorewithme.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CompilationServiceImpl implements  CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int fromNum, int size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Compilation> compilations;

        if (pinned == null)
            compilations = compilationRepository.findAll(page);
        else
            compilations = compilationRepository.findByPinned(pinned, page);

        if (compilations == null)
            compilations = Page.empty();

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationDtoList.add(CompilationMapper.toCompilationDto(compilation));
        }

        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilation(Long compId, HttpServletRequest request) {
        Compilation compilation = checkCompilation(compId, request);

        return CompilationMapper.toCompilationDto(compilation);
    }

    public Compilation checkCompilation(long compId, HttpServletRequest request) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundCompilationException(compId, request));
    }

    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto, HttpServletRequest request) {
        if (newCompilationDto.getTitle() == null)
            throw new BadRequestException(request.getParameterMap().toString());
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        return CompilationMapper.toCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events)));
    }

    @Override
    public void addEventToCompilation(long compId, long eventId, HttpServletRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundCompilationException(compId, request));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundEventException(eventId, request));
        compilation.getEvents().add(event);

        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(long compId) {
        compilationRepository.changeCompilationPin(compId, true);
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(long compId, long eventId, HttpServletRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundCompilationException(compId, request));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundEventException(eventId, request));
        compilation.getEvents().remove(event);

        compilationRepository.save(compilation);
    }

    @Override
    public void unpinCompilation(long compId) {
        compilationRepository.changeCompilationPin(compId, false);
    }
}
