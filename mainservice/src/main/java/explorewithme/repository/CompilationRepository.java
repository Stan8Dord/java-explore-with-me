package explorewithme.repository;

import explorewithme.model.compilation.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long>  {
    Page<Compilation> findByPinned(Boolean pinned, Pageable page);

    @Modifying
    @Query("update Compilation c set c.pinned = TRUE where c.id = ?1")
    void pinCompilation(long compId);

    @Modifying
    @Query("update Compilation c set c.pinned = FALSE where c.id = ?1")
    void unpinCompilation(long compId);
}
