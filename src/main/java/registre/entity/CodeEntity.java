package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "code")
public class CodeEntity {

    @Id
    private String id;

    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codes_list_id", nullable = false)
    private CodesListEntity codesList;
}
