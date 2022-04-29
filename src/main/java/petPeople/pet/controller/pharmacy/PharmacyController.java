package petPeople.pet.controller.pharmacy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petPeople.pet.domain.pharmacy.Pharmacy;
import petPeople.pet.domain.pharmacy.PharmacyRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pharmacies")
public class PharmacyController {

    private final PharmacyRepository pharmacyRepository;

    @PostMapping("")
    public ResponseEntity savePharmacy(@RequestBody List<PharmacySaveDto> pharmacySaveDtoList) {

        for (PharmacySaveDto pharmacySaveDto : pharmacySaveDtoList) {
            if (pharmacySaveDto.getBSN_STATE_NM() != null && pharmacySaveDto.getBSN_STATE_NM().equals("정상")) {
                pharmacyRepository.save(createPharmacy(pharmacySaveDto));
            }
        }

        return ResponseEntity.noContent().build();
    }

    private Pharmacy createPharmacy(PharmacySaveDto pharmacySaveDto) {
        return Pharmacy.builder()
                .sigunNm(pharmacySaveDto.getSIGUN_NM())
                .pharmacyName(pharmacySaveDto.getBIZPLC_NM())
                .phone(pharmacySaveDto.getLOCPLC_FACLT_TELNO_DTLS())
                .address(pharmacySaveDto.getREFINE_LOTNO_ADDR())
                .zipCode(pharmacySaveDto.getROADNM_ZIPNO())
                .x(pharmacySaveDto.getREFINE_WGS84_LAT())
                .y(pharmacySaveDto.getREFINE_WGS84_LOGT())
                .build();
    }

}
