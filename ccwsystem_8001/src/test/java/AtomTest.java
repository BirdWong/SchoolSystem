import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Test;

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
public class AtomTest {



    @Test
    public void testRss(){
        String urlString = "https://code.aliyun.com/cdhuishuo/lq-wechat.atom";
        try {
            URL url = new URL(urlString);
            parseXml(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }
    }


    public void parseXml(URL url) throws IllegalArgumentException, FeedException {

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = null;
            URLConnection conn;
            conn = url.openConnection();
            String content_encoding = conn.getHeaderField("Content-Encoding");


            InputStream inputStream = conn.getInputStream();
            if (content_encoding != null && content_encoding.contains("gzip")) {
                System.out.println("conent encoding is gzip");
                GZIPInputStream gzin = new GZIPInputStream(inputStream);
                XmlReader reader = new XmlReader(gzin);
                feed = input.build(reader);
            } else {
                XmlReader xmlReader = new XmlReader(inputStream);
                feed = input.build(xmlReader);
            }

            List entries = feed.getEntries();//得到所有的标题<title></title>
            for(int i=0; i < entries.size(); i++) {
                SyndEntry entry = (SyndEntry)entries.get(i);
                System.out.println(entry.getTitle());
                System.out.println(entry.getAuthor());
                System.out.println(entry.getContents());
                System.out.println(entry.getCategories());
                System.out.println(entry.getLink());
                System.out.println(entry.getDescription());

            }
            System.out.println("feed size:" + feed.getEntries().size());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
