package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.mapper.CompilationMapper;
import ru.practicum.compilation.model.CompilationResponseDto;
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
    public CompilationResponseDto addCompilation(@RequestBody @Validated(Create.class) CompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.convertToEntity(compilationDto);
        log.info("POST /admin/compilations of: {}", compilation);
        return CompilationMapper.convertToDto(compilationService.addCompilation(compilation));
    }

    @PatchMapping("/admin/compilations/{comId}")
    public CompilationResponseDto addCompilation(@RequestBody @Validated(Update.class) CompilationDto compilationDto,
                                                 @PathVariable(value = "comId") long comId) {
        Compilation compilation = CompilationMapper.convertToEntity(compilationDto);
        log.info("PATCH /admin/compilations of: {}; to {}", comId, compilation);
        return CompilationMapper.convertToDto(compilationService.changeCompilation(comId, compilation));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/compilations/{comId}")
    public void deleteCompilation(@PathVariable(value = "comId") long comId) {
        log.info("DELETE of: {}", comId);
        compilationService.deleteCompilation(comId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/compilations")
    public List<CompilationResponseDto> getCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                        @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                        @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GET /admin/compilations pinned:{}; from: {}; size: {}", pinned, from, size);
        List<Compilation> compilationsList = compilationService.getCategories(pinned, from, size);
        return compilationsList.stream()
                .map(CompilationMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/compilations/{comId}")
    public CompilationResponseDto getCompilation(@PathVariable(value = "comId") Long id) {
        log.info("GET /compilations id: {}", id);
        return CompilationMapper.convertToDto(compilationService.getCompilationById(id));
    }
}

























































