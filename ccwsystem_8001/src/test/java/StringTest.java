import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

/**
 * @ClassName StringTest
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/02 16:44
 */
public class StringTest {

    @Test
    public void test(){
        String url = "b/a";
        String[] split = url.split("/");
        for (String s : split){
            System.out.println(s);
        }
        System.out.println(split.length);
    }



    @Test
    public void test1(){

        TreeMap<BigDecimal, String> aveMap = new TreeMap<>(Comparator.reverseOrder());
        BigDecimal num1 = BigDecimal.ONE;

        num1 = num1.divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_HALF_UP);
        aveMap.put(num1, "1");




        BigDecimal num2 = BigDecimal.valueOf(3);

        num2 = num2.divide(BigDecimal.valueOf(9), 2, BigDecimal.ROUND_HALF_UP);
        num2 = num2.setScale(10, BigDecimal.ROUND_HALF_UP);

        while (aveMap.containsKey(num2)){

            num2 = num2.add(new BigDecimal("0.0000001"));
            System.out.println(num2.toString());
        }

        aveMap.put(num2, "2");



        for (BigDecimal key : aveMap.keySet()){
            String s = aveMap.get(key);
            key = key.setScale(2, BigDecimal.ROUND_HALF_UP);
            System.out.println(key.toString() + ":"+ s);
        }


    }




    @Test
    public void test2(){
        System.out.println(new Date().toString());
    }






    @Test
    public void test3(){
        System.out.println((Double.compare(99, 10)));
    }
}
