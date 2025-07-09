package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import registre.entity.CodesListEntity;

import java.util.UUID;

public interface CodesListRepository extends JpaRepository<CodesListEntity, UUID> {
}
