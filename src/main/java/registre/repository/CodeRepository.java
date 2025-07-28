package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import registre.entity.CodeEntity;

public interface CodeRepository extends JpaRepository<CodeEntity, Long> {
}
