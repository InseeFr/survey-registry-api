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
    @GeneratedValue
    private Long id;

    private String code;
    private String label;

    @ManyToOne
    @JoinColumn(name = "codes_list_id" , nullable = false)
    private CodesListEntity codesList;
}
