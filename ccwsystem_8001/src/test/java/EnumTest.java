import cn.jsuacm.ccw.pojo.announcement.Announcement;
import cn.jsuacm.ccw.pojo.announcement.AnnouncementCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName EnumTest
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/21 22:20
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = SpringBootApplicationTest.class)
public class EnumTest {


    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate template;

    private String url = "http://JSUCCW-ZUUL-GATEWAY/authentication/getAuthenticationByUid/";

    @Test
    public void test(){
        AnnouncementCategory.Role role1 = AnnouncementCategory.Role.ROLE_ING;
        AnnouncementCategory.Role role2 = AnnouncementCategory.Role.ROLE_ADMIN;
        AnnouncementCategory.Role role3 = AnnouncementCategory.Role.ROLE_ADMINISTRATOR;

        List<String> roleList = getRole(1);
        for (String r : roleList) {
            if (!AnnouncementCategory.Role.hasRole(r)){
                r = "ALL";
            }

            if(AnnouncementCategory.Role.valueOf(r).compareTo(role1) >= 0){
                System.out.println(role1.name()+"\t"+r);
            }


        }

        for (String r : roleList) {
            if (!AnnouncementCategory.Role.hasRole(r)){
                r = "ALL";
            }
            if(AnnouncementCategory.Role.valueOf(r).compareTo(role2) >= 0){
                System.out.println(role2.name()+"\t"+r);
            }
        }

        for (String r : roleList) {
            if (!AnnouncementCategory.Role.hasRole(r)){
                r = "ALL";
            }
            if(AnnouncementCategory.Role.valueOf(r).compareTo(role3) >= 0){
                System.out.println(role3.name()+"\t"+r);
            }
        }

    }


    private List<String> getRole(int uid){
        HttpHeaders handler = new HttpHeaders();

        handler.add("Uid", String.valueOf(uid));
        HttpEntity<String> entity = new HttpEntity<>(null, handler);
        ResponseEntity<List> responseEntity = template.exchange(url + uid, HttpMethod.GET,entity,List.class);
        List body = responseEntity.getBody();
        ArrayList<String> roleList = new ArrayList<>();
        if (body == null || body.size() == 0){
            return roleList;
        }

        for (Object obj : body){

            HashMap<String, Object> values = (HashMap<String, Object>) obj;
            Object role = values.get("role");
            if (role != null){
                roleList.add(String.valueOf(role));
            }
        }

        return roleList;
    }









}
