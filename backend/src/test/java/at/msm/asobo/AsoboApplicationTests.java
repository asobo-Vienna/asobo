package at.msm.asobo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = AsoboApplication.class)
@ActiveProfiles("test")
class AsoboApplicationTests {

    @Test
    void contextLoads() {
    }

}
