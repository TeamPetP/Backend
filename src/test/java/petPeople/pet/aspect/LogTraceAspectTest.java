package petPeople.pet.aspect;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import petPeople.pet.controller.member.MemberController;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class LogTraceAspectTest {

    @Autowired
    MemberController memberController;

    @Test
    void test() {
        Assertions.assertThat(AopUtils.isAopProxy(memberController)).isTrue();
    }

}