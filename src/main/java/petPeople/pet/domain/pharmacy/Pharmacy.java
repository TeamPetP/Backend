package petPeople.pet.domain.pharmacy;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pharmacy {

    @Id @GeneratedValue
    @Column(name = "pharmacy_id")
    Long id;

    //가평군
    private String sigunNm;

    //병원이름
    private String pharmacyName;

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
