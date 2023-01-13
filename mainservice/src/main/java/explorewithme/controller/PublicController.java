package explorewithme.controller;

import explorewithme.model.category.CategoryDto;
import explorewithme.model.compilation.CompilationDto;
import explorewithme.model.event.EventFullDto;
import explorewithme.model.event.EventShortDto;
import explorewithme.service.CategoryService;
import explorewithme.service.CompilationService;
import explorewithme.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class PublicController {
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CategoryService categoryService;

    @Autowired
    public PublicController(EventService eventService,
                            CompilationService compilationService,
                            CategoryService categoryService) {
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.categoryService = categoryService;
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@RequestParam String text,
                                         @RequestParam Long[] categories,
                                         @RequestParam Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                                            onlyAvailable, sort, from, size, ip, uri);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable("eventId") long eventId, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        return eventService.getEvent(eventId, ip, uri);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable("compId") long compId) {
        return compilationService.getCompilation(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable("catId") long catId) {
        return categoryService.getCategory(catId);
    }
}
