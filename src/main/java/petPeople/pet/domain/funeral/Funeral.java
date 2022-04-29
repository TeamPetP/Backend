package petPeople.pet.domain.funeral;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class Funeral {

    @Id @GeneratedValue
    @Column(name = "funeral_id")
    private Long id;

    //가평군
    private String sigunNm;

    //병원이름
    private String funeralName;

    //지번 주소
    private String address;

    //우편 주소
    private String zipCode;

    //번호
    private String phone;

    //x
    private double x;

    //y
    private double y;

}
