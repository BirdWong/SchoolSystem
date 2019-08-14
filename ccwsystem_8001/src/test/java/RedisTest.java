import cn.jsuacm.ccw.pojo.enity.BookEsEmpty;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.book.EsBookRepository;
import cn.jsuacm.ccw.service.book.EsBookService;
import cn.jsuacm.ccw.service.recruit.RecruitmentService;
import cn.jsuacm.ccw.util.EmailUtil;
import cn.jsuacm.ccw.util.RedisUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.management.StringValueExp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * @ClassName RedisTest
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/02 21:18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootApplicationTest.class)
public class RedisTest {


    @Autowired
    private RedisUtil redisUtil;

    @Before
    public void before(){

    }


    @Test
    public void test1() {

    }


    @Test
    public void test2(){
    }
    @Test
    public void test() throws IOException {

        File file = new File("/home/h4795/Desktop/1.png");


        String data = encodeImgageToBase64(file);
        System.out.println("*******************");
        System.out.println(data);
        System.out.println("**********************");
        decodeBase64ToImage(data, "/home/h4795/", "1.png");
    }


    public static String encodeImgageToBase64(URL imageUrl) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
    }



    public static String encodeImgageToBase64(File imageFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String name = imageFile.getName();
        String[] split = name.split("\\.");
        System.out.println(name +"  "+split.length);
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, split[split.length - 1], outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
    }

    /**
     * 将Base64位编码的图片进行解码，并保存到指定目录
     *
     * @param base64 base64编码的图片信息
     * @return
     */
    public static void decodeBase64ToImage(String base64, String path,
                                           String imgName) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            FileOutputStream write = new FileOutputStream(new File(path
                    + imgName));
            byte[] decoderBytes = decoder.decodeBuffer(base64);
            write.write(decoderBytes);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    @Test
    public void hmtest(){

        Object hget = redisUtil.hget("haha", "hehe");
        if (hget != null ){
            System.out.println(Integer.valueOf(String.valueOf(hget)));
        }

        hget = redisUtil.hget("haha", "h");
        if (hget != null ){
            System.out.println(Integer.valueOf(String.valueOf(hget)));
        }else {
            System.out.println("h null");
        }
        redisUtil.hdel("haha", "hehe");
        redisUtil.del("haha");
        redisUtil.hasKey("haha");
    }








    @Autowired
    EsBookService esBookService;

    @Autowired
    EsBookRepository esBookRepository;


    @Test
    public void test3(){

        int[] ids = new int[]{48,40,44,42,43,51,46,39,49,45,50,47};
        for (int id : ids){
            Optional<BookEsEmpty> boo = esBookRepository.findById(id);
            for(int i = 0; i < boo.get().getHasUse(); i++){
                esBookService.changeUse(id, -1);
            }
        }
    }



    @Test
    public void test4(){
        MessageResult messageResult = EmailUtil.sendEmail("912719483@qq.com", "4795", new Date(), "2110", "带笔，带纸");
        System.out.println(messageResult.getMsg());
    }
}






