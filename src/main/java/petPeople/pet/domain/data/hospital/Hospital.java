package petPeople.pet.domain.data.hospital;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Hospital {

    @Id @GeneratedValue
    @Column(name = "hospital_id")
    private Long id;

    //가평군
    private String sigunNm;

    //병원이름
    private String hospitalName;
    
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
