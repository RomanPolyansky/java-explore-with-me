package ru.practicum.compilation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.QCompilation;
import ru.practicum.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Compilation addCompilation(Compilation compilation) {
        Optional<Compilation> compilationOfTitle = compilationRepository.findByTitle(compilation.getTitle());
        if (compilationOfTitle.isPresent()) {
            throw new DataIntegrityViolationException(String.format("Compilation of title %s already exists", compilation.getTitle()));
        }
        Compilation newCompilation = compilationRepository.save(new Compilation(compilation.getTitle(), compilation.getPinned()));
        compilation.setId(newCompilation.getId());
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("CompilationRepository saved: {}", savedCompilation);
        return savedCompilation;
    }


    @Override
    public List<Compilation> getCategories(Boolean pinned, int from, int size) {
        BooleanExpression inPinned = pinned == null ? Expressions.asBoolean(true).isTrue() :
                QCompilation.compilation.pinned.eq(pinned);

        List<Compilation> foundCategories = IterableUtils.toList(jpaQueryFactory.selectFrom(QCompilation.compilation)
                .where(inPinned)
                .orderBy(QCompilation.compilation.id.asc())
                .offset(from)
                .limit(size)
                .fetch());
        log.info("CompilationRepository returned: {}", foundCategories);
        return foundCategories;
    }

    @Override
    public Compilation getCompilationById(Long comId) {
        return compilationRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Compilation with id %s does not exist", comId))
        );
    }

    @Override
    public Compilation changeCompilation(long comId, Compilation compilation) {
        Compilation compilationInRepo = compilationRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Compilation with id %s does not exist", comId))
        );
        compilation.setId(comId);
        Compilation changedCompilation = compilationRepository.save(compilationInRepo.merge(compilation));
        log.info("CompilationRepository changed: {}; to {}", compilationInRepo, changedCompilation);
        return changedCompilation;
    }

    @Override
    public void deleteCompilation(long comId) {
        Compilation compilationInRepo = compilationRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Compilation with id %s does not exist", comId))
        );
        log.info("CompilationRepository deletes: {}", compilationInRepo);
        compilationRepository.deleteById(comId);
    }
}
