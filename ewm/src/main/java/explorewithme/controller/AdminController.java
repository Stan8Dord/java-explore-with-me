package explorewithme.controller;

import explorewithme.model.category.CategoryDto;
import explorewithme.model.category.NewCategoryDto;
import explorewithme.model.compilation.CompilationDto;
import explorewithme.model.compilation.NewCompilationDto;
import explorewithme.model.event.AdminUpdateEventRequest;
import explorewithme.model.event.EventFullDto;
import explorewithme.model.user.NewUserRequest;
import explorewithme.model.user.User;
import explorewithme.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/admin")
public class AdminController {
    private final EventService eventService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CompilationService compilationService;

    @Autowired
    public AdminController(EventService eventService, CategoryService categoryService, UserService userService,
                            CompilationService compilationService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.compilationService = compilationService;
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam Long[] users,
                                        @RequestParam(required = false) String[] states,
                                        @RequestParam Long[] categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("events/{eventId}")
    public EventFullDto modifyEventByAdmin(@PathVariable("eventId") long eventId,
                                           @RequestBody AdminUpdateEventRequest modEvent,
                                           HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.modifyEventByAdmin(eventId, modEvent, request);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable("eventId") long eventId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.publishEvent(eventId, request);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable("eventId") long eventId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return eventService.rejectEvent(eventId, request);
    }

    @PostMapping("/categories")
    public CategoryDto addNewCategory(@RequestBody NewCategoryDto categoryDto, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return categoryService.addNewCategory(categoryDto, request);
    }

    @PatchMapping("/categories")
    public CategoryDto modifyCategory(@RequestBody CategoryDto categoryDto, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return categoryService.modifyCategory(categoryDto, request);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable("catId") long catId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        categoryService.deleteCategory(catId);
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestParam(required = false) Long[] ids,
                               @RequestParam(defaultValue = "0") int from,
                               @RequestParam(defaultValue = "10") int size,
                               HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public User addNewUser(@RequestBody NewUserRequest newUser, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return userService.addNewUser(newUser, request);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("userId") long userId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        userService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto addNewCompilation(@RequestBody NewCompilationDto newCompilationDto,
                                            HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return compilationService.addNewCompilation(newCompilationDto, request);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable("compId") long compId,
                                      @PathVariable("eventId") long eventId,
                                      HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        compilationService.addEventToCompilation(compId, eventId, request);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable("compId") long compId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        compilationService.pinCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable("compId") long compId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable("compId") long compId,
                                           @PathVariable("eventId") long eventId,
                                           HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        compilationService.deleteEventFromCompilation(compId, eventId, request);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void unpinCompilation(@PathVariable("compId") long compId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        compilationService.unpinCompilation(compId);
    }
}
