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
    @Query("update Compilation c set c.pinned = ?2 where c.id = ?1")
    void changeCompilationPin(long compId, boolean pinned);
}
