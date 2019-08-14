import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ExecTest
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/10 20:00
 */
public class ExecTest {

    @Test
    public void test(){
        String exrp = "^([\\w-_]+(?:\\.[\\w-_]+)*)@((?:[a-z0-9]+(?:-[a-zA-Z0-9]+)*)+\\.[a-z]{2,6})$";
        Pattern p = Pattern.compile(exrp);
        Matcher matcher=p.matcher("hack_5211314_hp@qq.com");
        System.out.println(matcher.matches());
    }

}

