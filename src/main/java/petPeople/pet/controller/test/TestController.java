package petPeople.pet.controller.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //TEST
    @GetMapping("/test")
    public String test() {
        return "okk";
    }

    @RequestMapping(value = "/test", method = RequestMethod.OPTIONS)
    public String test1() {
        return "okk";
    }

}
