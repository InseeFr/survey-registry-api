package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import registre.entity.CodesListEntity;

public interface CodesListRepository extends JpaRepository<CodesListEntity, String> {
}