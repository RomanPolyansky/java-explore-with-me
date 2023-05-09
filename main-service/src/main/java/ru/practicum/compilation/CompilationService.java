package ru.practicum.compilation;

import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {

    void deleteCompilation(long comId);

    Compilation changeCompilation(long comId, Compilation compilation);

    Compilation addCompilation(Compilation compilation);

    List<Compilation> getCompilations(Boolean pinned, int from, int size);

    Compilation getCompilationById(Long id);
}
