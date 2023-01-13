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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
                                        @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("events/{eventId}")
    public EventFullDto modifyEventByAdmin(@PathVariable("eventId") long eventId,
                                           @RequestBody AdminUpdateEventRequest modEvent) {
        return eventService.modifyEventByAdmin(eventId, modEvent);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable("eventId") long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable("eventId") long eventId) {
        return eventService.rejectEvent(eventId);
    }

    @PostMapping("/categories")
    public CategoryDto addNewCategory(@RequestBody NewCategoryDto categoryDto) {
        return categoryService.addNewCategory(categoryDto);
    }

    @PatchMapping("/categories")
    public CategoryDto modifyCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.modifyCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable("catId") long catId) {
        categoryService.deleteCategory(catId);
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestParam(required = false) Long[] ids,
                               @RequestParam(defaultValue = "0") int from,
                               @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public User addNewUser(@RequestBody NewUserRequest newUser) {
        return userService.addNewUser(newUser);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto addNewCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addNewCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable("compId") long compId,
                                      @PathVariable("eventId") long eventId) {
        compilationService.addEventToCompilation(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable("compId") long compId) {
        compilationService.pinCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable("compId") long compId) {
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable("compId") long compId,
                                           @PathVariable("eventId") long eventId) {
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void unpinCompilation(@PathVariable("compId") long compId) {
        compilationService.unpinCompilation(compId);
    }
}
