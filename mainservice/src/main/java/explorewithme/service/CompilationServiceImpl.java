package explorewithme.service;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.NotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = checkCompilation(compId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    public Compilation checkCompilation(long compId) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(compId);

        if (compilationOptional.isPresent())
            return compilationOptional.get();
        else
            throw new NotFoundException("Not found");
    }

    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null)
            throw new BadRequestException("Bad bad bad request");
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        return CompilationMapper.toCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events)));
    }

    @Override
    public void addEventToCompilation(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found"));
        compilation.getEvents().add(event);

        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilation(long compId) {
        compilationRepository.pinCompilation(compId);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found"));
        compilation.getEvents().remove(event);

        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilation(long compId) {
        compilationRepository.unpinCompilation(compId);
    }
}
