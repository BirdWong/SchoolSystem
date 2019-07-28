import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName SpringBootApplicationTest
 * @Description 测试
 * @Author h4795
 * @Date 2019/06/20 20:10
 */
@SpringBootApplication
@ComponentScan("cn.jsuacm.ccw")
public class SpringBootApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplicationTest.class, args);
    }
}
