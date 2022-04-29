package petPeople.pet.controller.funeral;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import petPeople.pet.domain.funeral.Funeral;
import petPeople.pet.domain.funeral.FuneralRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funerals")
public class FuneralController {

    private final FuneralRepository funeralRepository;

    @PostMapping("")
    public ResponseEntity saveFuneral(@RequestBody List<FuneralSaveDto> funeralSaveDtoList) {

        for (FuneralSaveDto funeralSaveDto : funeralSaveDtoList) {
            funeralRepository.save(createFuneral(funeralSaveDto));
        }

        return ResponseEntity.noContent().build();
    }


    private Funeral createFuneral(FuneralSaveDto funeralSaveDto) {
        return Funeral.builder()
                .sigunNm(funeralSaveDto.getSIGUN_NM())
                .funeralName(funeralSaveDto.getBIZPLC_NM())
                .phone(funeralSaveDto.getLOCPLC_FACLT_TELNO_DTLS())
                .address(funeralSaveDto.getREFINE_LOTNO_ADDR())
                .zipCode(funeralSaveDto.getROADNM_ZIPNO())
                .x(funeralSaveDto.getREFINE_WGS84_LAT())
                .y(funeralSaveDto.getREFINE_WGS84_LOGT())
                .build();
    }
}
