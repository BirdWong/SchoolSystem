import cn.jsuacm.ccw.pojo.projects.Commit;
import cn.jsuacm.ccw.service.projects.CommitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @ClassName AtomTest
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/31 20:18
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = SpringBootApplicationTest.class)
public class AtomTest {

    @Autowired
    CommitService commitService;


    @Test
    public void test(){
        commitService.getByGitHub("https://api.github.com/repos/birdwong/schoolsystem/commits", "RocWong");
    }
}
