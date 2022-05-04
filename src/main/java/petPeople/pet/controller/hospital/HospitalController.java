package petPeople.pet.controller.hospital;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petPeople.pet.domain.data.hospital.Hospital;
import petPeople.pet.domain.data.hospital.HospitalRepository;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/hospitals")
public class HospitalController {

    private final HospitalRepository hospitalRepository;

    @PostMapping("/hospitals")
    public ResponseEntity saveHospital(@RequestBody List<HospitalSaveDto> hospitalSaveDtoList) {
        for (HospitalSaveDto hospitalSaveDto : hospitalSaveDtoList) {
            if (hospitalSaveDto.getBSN_STATE_NM() != null && hospitalSaveDto.getBSN_STATE_NM().equals("정상")) {
                hospitalRepository.save(createHospital(hospitalSaveDto));
            }
        }
        return ResponseEntity.noContent().build();
    }

    private Hospital createHospital(HospitalSaveDto hospitalSaveDto) {
        Hospital hospital = Hospital.builder()
                .sigunNm(hospitalSaveDto.getSIGUN_NM())
                .hospitalName(hospitalSaveDto.getBIZPLC_NM())
                .phone(hospitalSaveDto.getLOCPLC_FACLT_TELNO_DTLS())
                .address(hospitalSaveDto.getREFINE_LOTNO_ADDR())
                .zipCode(hospitalSaveDto.getROADNM_ZIPNO())
                .x(hospitalSaveDto.getREFINE_WGS84_LAT())
                .y(hospitalSaveDto.getREFINE_WGS84_LOGT())
                .build();
        return hospital;
    }

}
