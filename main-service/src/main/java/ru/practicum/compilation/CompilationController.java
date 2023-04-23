package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.constraint.Create;
import ru.practicum.constraint.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@RestControllerAdvice
public class CompilationController {

    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/compilations")
    public CompilationDto addCompilation(@RequestBody @Validated(Create.class) CompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.convertToEntity(compilationDto);
        log.info("POST /admin/compilations of: {}", compilation);
        return CompilationMapper.convertToDto(compilationService.addCompilation(compilation));
    }

    
    @PatchMapping("/admin/compilations/{catId}") //TODO remake so Only set fields get replaced
    public CompilationDto addCompilation(@RequestBody @Validated(Update.class) CompilationDto compilationDto,
                                   @PathVariable(value = "catId") long catId) {
        Compilation compilation = CompilationMapper.convertToEntity(compilationDto);
        log.info("PATCH /admin/compilations of: {}; to {}", catId, compilation);
        return CompilationMapper.convertToDto(compilationService.changeCompilation(catId, compilation));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/compilations/{catId}")
    public void deleteCompilation(@PathVariable(value = "catId") long catId) {
        log.info("DELETE of: {}", catId);
        compilationService.deleteCompilation(catId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/compilations")
    public List<CompilationDto> getCategories(@PositiveOrZero @RequestParam (value = "from", defaultValue = "0") int from,
                                           @Positive @RequestParam (value = "size", defaultValue = "10") int size) {
        log.info("GET /admin/compilations from: {}; size: {}", from, size);
        List<Compilation> compilationsList = compilationService.getCategories(from, size);
        return compilationsList.stream()
                .map(CompilationMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/compilations/{catId}")
    public CompilationDto getCompilation(@PathVariable (value = "catId") Long id) {
        log.info("GET /compilations id: {}", id);
        return CompilationMapper.convertToDto(compilationService.getCompilationById(id));
    }
}

























































