package petPeople.pet.controller.pharmacy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PharmacySaveDto {

    //가평군
    @JsonProperty("SIGUN_NM")
    private String SIGUN_NM;

    //약국이름
    @JsonProperty("BIZPLC_NM")
    private String BIZPLC_NM;

    @JsonProperty("LOCPLC_FACLT_TELNO_DTLS")
    private String LOCPLC_FACLT_TELNO_DTLS;

    //영업 상태
    @JsonProperty("BSN_STATE_NM")
    private String BSN_STATE_NM;

    //지번 주소
    @JsonProperty("REFINE_LOTNO_ADDR")
    private String REFINE_LOTNO_ADDR;

    //우편번호
    @JsonProperty("ROADNM_ZIPNO")
    private String ROADNM_ZIPNO;

    //x
    @JsonProperty("REFINE_WGS84_LAT")
    private double REFINE_WGS84_LAT;

    //y
    @JsonProperty("REFINE_WGS84_LOGT")
    private double REFINE_WGS84_LOGT;

}
