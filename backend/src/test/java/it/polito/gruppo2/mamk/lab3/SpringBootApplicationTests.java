package it.polito.gruppo2.mamk.lab3;

import it.polito.gruppo2.mamk.lab3.controllers.ArchiveController;
import it.polito.gruppo2.mamk.lab3.controllers.TransactionController;
import it.polito.gruppo2.mamk.lab3.controllers.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SpringBootApplicationTests {

    @Autowired
    private ArchiveController archiveController;
    @Autowired
    private TransactionController transactionController;
    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() throws Exception {

        TestUtils.print("+++ Testing application context in a non mocked web environment +++");
        TestUtils.print("Starting up application context..");
        assertThat(archiveController).isNotNull();
        assertThat(transactionController).isNotNull();
        assertThat(userController).isNotNull();
        TestUtils.print("+++ Application context tests completed! +++\n\n");
    }

}
