package cn.jsuacm.ccw.service.recruit.impl;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.recruit.NewMenberInfo;
import cn.jsuacm.ccw.service.recruit.LimitTimerService;
import cn.jsuacm.ccw.service.recruit.RecruitmentService;
import cn.jsuacm.ccw.util.EmailUtil;
import cn.jsuacm.ccw.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName RecruitmentServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/04 20:49
 */
@Service
public class RecruitmentServiceImpl implements RecruitmentService {


    @Autowired
    private LimitTimerService limitTimerService;


    @Autowired
    private RedisUtil redisUtil;


    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/getUserById/";

    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate template;





    /**
     * 添加一个报名信息
     *
     * @param newMenberInfo
     * @return
     */
    @Override
    public MessageResult add(NewMenberInfo newMenberInfo) {
        // 检验表单是否填写正确
        if (!NewMenberInfo.checkInfo(newMenberInfo)){
            return new MessageResult(false, "表单信息填写错误");
        }
        // 1. 如果redis中存在时间的键， 说明已经开始报名
        if (limitTimerService.hasTime()){
            // 判断这个用户是否已经存在， 键值是已email为键
            boolean hasUser = redisUtil.hHasKey(RecruitmentService.RECRUIT, newMenberInfo.getEmail());
            if (hasUser){
                // 如果已经存在则不允许再报名
                return new MessageResult(false, "用户已经报名成功， 请勿重复报名");
            }
            // 添加打分对象
            newMenberInfo.setScores(new HashMap<String, BigDecimal>());
            // 保存到数据库中
            redisUtil.hset(RecruitmentService.RECRUIT, newMenberInfo.getEmail(), newMenberInfo);
            return new MessageResult(true, "报名成功");
        }
        return new MessageResult(false, "报名未开始/报名结束");
    }


    /**
     * 获取所有的报名信息，并且按照班级分类
     * @return
     */
    @Override
    public TreeMap<Integer, List<NewMenberInfo>> getToClass() {
        TreeMap<Integer, List<NewMenberInfo>> newMenberInfoMap = new TreeMap<>(Comparator.comparingInt(integer -> integer));
        if (hasRecruitment()){
            Map<Object, Object> hmget = redisUtil.hmget(RecruitmentService.RECRUIT);
            for (Object key : hmget.keySet()){
                NewMenberInfo newMenberInfo = (NewMenberInfo)hmget.get(key);
                List<NewMenberInfo> infos = newMenberInfoMap.getOrDefault(newMenberInfo.getFrom(), new ArrayList<>());
                infos.add(newMenberInfo);
                newMenberInfoMap.put(newMenberInfo.getFrom(), infos);
            }
            return newMenberInfoMap;
        }else {
            return null;
        }


    }


    /**
     * 按照输入的班级返回报名列表
     * @param num 班级
     * @return
     */
    @Override
    public List<NewMenberInfo> getByClass(int num) {
        List<NewMenberInfo> list = new ArrayList<>();
        if (hasRecruitment()){
            Map<Object, Object> hmget = redisUtil.hmget(RecruitmentService.RECRUIT);
            for (Object key : hmget.keySet()) {
                NewMenberInfo newMenberInfo = (NewMenberInfo) hmget.get(key);
                if (newMenberInfo.getFrom() == num){
                    list.add(newMenberInfo);
                }
            }
            return list;
        }
        return null;
    }

    /**
     * 获取报名的班级。 可以通过拥有的班级然后跟别获取每个班级的报名信息
     *
     * @return
     */
    @Override
    public List<Integer> getClassNumbers() {
        Set<Integer> classNumbers = new HashSet<>();
        if (hasRecruitment()) {
            Map<Object, Object> hmget = redisUtil.hmget(RecruitmentService.RECRUIT);
            for (Object key : hmget.keySet()) {
                NewMenberInfo newMenberInfo = (NewMenberInfo) hmget.get(key);
                classNumbers.add(newMenberInfo.getFrom());
            }
            ArrayList<Integer> list = new ArrayList<>(classNumbers);
            Collections.sort(list, Comparator.comparingInt(integer -> integer));
            return list;
        }
        return null;
    }

    /**
     * 修改报名信息
     *
     * @param menberInfo
     * @return
     */
    @Override
    public MessageResult updateInfo(NewMenberInfo menberInfo) {
        if (NewMenberInfo.checkInfo(menberInfo)){
            // 1. 如果redis中存在用户信息并且存在这个用户信息
            if (hasRecruitment() && redisUtil.hHasKey(RecruitmentService.RECRUIT, menberInfo.getEmail())) {
                redisUtil.hset(RecruitmentService.RECRUIT, menberInfo.getEmail(), menberInfo);
                return new MessageResult(true, "修改成功");
            }else {
                return  new MessageResult(false, "请确认是否用户存在");
            }
        }else {
            return new MessageResult(false, "表单填写错误");
        }
    }

    /**
     * 通过邮箱获取一个报名信息
     *
     * @param email
     * @return
     */
    @Override
    public NewMenberInfo getByEmail(String email) {
        if (!hasRecruitment()){
            return null;
        }
        boolean key = redisUtil.hHasKey(RecruitmentService.RECRUIT, email);
        if (key){
            NewMenberInfo newMenberInfo = (NewMenberInfo) redisUtil.hget(RecruitmentService.RECRUIT, email);
            return newMenberInfo;
        }else {
            return null;
        }
    }

    /**
     * 按照平均分下载面试分数排名
     */
    @Override
    public void download(HttpServletResponse response) {
        if (hasRecruitment()){

            // 设置下载头
            File file = new File("报名面试排名表.csv");



            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

            ServletOutputStream outputStream = null;




            // 所有的报名用户表
            Map<Object, Object> infos = redisUtil.hmget(RecruitmentService.RECRUIT);
            // 评分者的id和用户名
            TreeMap<String, String> nameMap = new TreeMap<>(String::compareTo);
            // 每个报名者的平均分
            TreeMap<BigDecimal, String> avgMap = new TreeMap<>(Comparator.reverseOrder());



            int scoringSize = 0;
            // 遍历每个报名者信息
            for (Object key : infos.keySet()){
                // 获取报名者信息
                NewMenberInfo newMenberInfo = (NewMenberInfo) infos.get(key);
                // 获取这个报名者的打分信息
                Map<String, BigDecimal> scores = newMenberInfo.getScores();
                // 统计有多少人打分
                int memberSize = 0;
                // 计算得分总计
                BigDecimal sumScores = BigDecimal.ZERO;

                //  遍历打分列表， 获取所有打分的用户id，并且统计每个报名者的平均分
                for (String uid : scores.keySet()){
                    // 判断是否已经有这个用户的信息， 确认是否知道这个用户uid的名称
                    if (!nameMap.containsKey(uid)){
                        // 尝试获取这个用户id的名称
                        try {
                            // 获取用户信息
                            ResponseEntity<Map> entity = template.getForEntity(url + uid, Map.class);
                            Map userMap = entity.getBody();
                            // 获取用户名
                            Object username = userMap.get("username");
                            if (username != null) {
                                nameMap.put(uid, String.valueOf(username));
                            }else {
                                nameMap.put(uid, "未知用户");
                            }
                        }catch (Exception e){
                            nameMap.put(uid, "未知用户");
                        }
                    }
                    // 统计总分
                    sumScores = sumScores.add(scores.get(uid));
                    // 确认打分人数
                    memberSize++;
                }
                // 设置总共的打分人
                scoringSize = scoringSize > memberSize ? scoringSize : memberSize;
                // 获取两位数的平均分
                BigDecimal ave = sumScores.divide(new BigDecimal(memberSize), 2, BigDecimal.ROUND_HALF_UP);
                /*
                 * 将平均分和对应的email存入map
                 *  - 如果平均分重复，则添加一个微小的分数，避免重复
                 */
                while (avgMap.containsKey(ave)){
                    ave = ave.add(BigDecimal.valueOf(0.0000001));
                }
                avgMap.put(ave, String.valueOf(key));
            }


            // 设置字段属性
            List<CellProcessor> cellProcessorList = new ArrayList<>();
            for (int i = 0; i < scoringSize+3; i++){
                cellProcessorList.add(new NotNull());
            }

            // 设置字段头
            List<String> headerList = new ArrayList<>();
            headerList.add("姓名");
            headerList.add("QQ");
            headerList.addAll(nameMap.values());
            headerList.add("平均分");

            ICsvListWriter listWriter = null;
            try {
                // 设置输出流
                outputStream = response.getOutputStream();
                listWriter = new CsvListWriter(new OutputStreamWriter(outputStream), CsvPreference.STANDARD_PREFERENCE);

                // 设置csv头
                listWriter.writeHeader(headerList.toArray(new String[headerList.size()]));


                // 循环最高分
                for (BigDecimal key : avgMap.keySet()){

                    List<String> user = new ArrayList<>();

                    // 获取此报名者邮箱
                    String email = avgMap.get(key);

                    // 获取这个报名者的信息
                    NewMenberInfo newMenberInfo = (NewMenberInfo) infos.get(email);

                    // 设置报名者的名称
                    user.add(newMenberInfo.getName());
                    // 设置报名者的qq
                    user.add(newMenberInfo.getQq());

                    // 获取这个报名者的分数
                    Map<String, BigDecimal> scores = newMenberInfo.getScores();

                    // 去掉避免重复的尾缀
                    BigDecimal ave = key.setScale(2, BigDecimal.ROUND_HALF_UP);


                    // 遍历评分人
                    for (String uid : nameMap.keySet()){
                        if (scores.containsKey(uid)){
                            user.add(scores.get(uid).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        }else {
                            user.add("none");
                        }
                    }

                    user.add(ave.toString());
                    listWriter.write(user, headerList.toArray(new String[headerList.size()]));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if( listWriter != null ) {
                    try {
                        listWriter.flush();
                        listWriter.close();
                        outputStream.close();
                    } catch (IOException e) {
                        System.out.println("关闭出错");
                    }
                }
            }


        }
    }

    /**
     * 面试打分
     *
     * @param email
     * @param uid
     * @param score
     * @return
     */
    @Override
    public MessageResult setScore(String email, String uid, double score) {
        if (Double.compare(score, 10) <= 0 || Double.compare(score, 100) > 0){
            return new MessageResult(false, "分数是百分制");
        }
        if (hasRecruitment()){

            boolean hasKey = redisUtil.hHasKey(RecruitmentService.RECRUIT, email);
            if (hasKey){

                NewMenberInfo newMenberInfo = (NewMenberInfo) redisUtil.hget(RecruitmentService.RECRUIT, email);
                if (newMenberInfo != null){
                    Map<String, BigDecimal> scores = newMenberInfo.getScores();
                    BigDecimal bigDecimal = BigDecimal.valueOf(score);
                    if (scores.containsKey(uid)){
                        scores.put(uid, bigDecimal);
                        redisUtil.hset(RecruitmentService.RECRUIT, email, newMenberInfo);
                        return new MessageResult(true, "已经覆盖之前打分");
                    }else {
                        scores.put(uid, bigDecimal);
                        redisUtil.hset(RecruitmentService.RECRUIT, email, newMenberInfo);
                        return new MessageResult(true, "打分成功");
                    }
                }else {
                    return new MessageResult(false, "没有这个报名表");
                }

            }else {
                return new MessageResult(false, "没有这个报名表");
            }

        }

        return new MessageResult(false, "没有任何报名表");
    }

    /**
     * 上传图片转化为base64
     *
     * @param multipartFile
     * @return
     */
    @Override
    public MessageResult uploadPictrue(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || StringUtils.isBlank(multipartFile.getOriginalFilename())) {
            return new MessageResult(false, "上传失败");
        }

        String contentType = multipartFile.getContentType();
        if (contentType == null || contentType.contains("")) {
            return new MessageResult(false, "上传失败");
        }


        // 切割文件名称， 获取文件格式
        String fileName = multipartFile.getOriginalFilename();
        String[] fileSplit = fileName.split("\\.");
        if (fileSplit.length < 2){
            return new MessageResult(false, "文件名称错误");
        }

        // 获取文件格式
        String type = fileSplit[fileSplit.length - 1];

        String prefix;

        // 确认文件格式
        switch (type){
            case "png": prefix = "data:image/png;base64,";
            break;
            case "jpg":
            case "jpeg": prefix = "data:image/jpeg;base64,";
            break;
            case "gif": prefix = "data:image/gif;base64,";
            break;
            default: return new MessageResult(false, "文件格式错误，只允许 png/jpg/jpeg/gif文件");
        }
        try {
            // 设置文件输入流
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            // 转化成图片字节
            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 将字节写入输出流
            ImageIO.write(bufferedImage, type, outputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            // 将输出流中的字节转成base64
            String date = prefix + encoder.encode(outputStream.toByteArray());
            return new MessageResult(true, date);
        } catch (IOException e) {
            return new MessageResult(false, "转换失败，请重新尝试");
        }

    }



    /**
     * 发送邮件给所有的面试者通知面试时间
     * @param date 面试时间
     * @param address 面试地点
     * @param msg 其他提示性信息
     * @return
     */
    @Override
    public MessageResult sendEmail(Date date, String address, String msg) {
        boolean hasTime = limitTimerService.hasTime();
        if (hasTime){
            return new MessageResult(false, "报名未结束， 不允许提前你发送面试信息");
        }
        if (hasRecruitment()){
            if (msg == null){
                msg = "";
            }
            StringBuilder errorMsg = new StringBuilder("");
            Map<Object, Object> hmget = redisUtil.hmget(RecruitmentService.RECRUIT);
            for (Object key : hmget.keySet()){
                NewMenberInfo newMenberInfo = (NewMenberInfo)hmget.get(key);
                String email = String.valueOf(key);
                MessageResult messageResult = EmailUtil.sendEmail(email, newMenberInfo.getName(), date, address, msg);
                if (!messageResult.isStatus()){
                    if (errorMsg.length() != 0){
                        errorMsg.append("、");
                    }
                    errorMsg.append(newMenberInfo.getName()).append(":").append(newMenberInfo.getQq());
                }
            }
            if (errorMsg.length() != 0){
                errorMsg.insert(0, "以下用户面试邮件发送错误：");
                return new MessageResult(true, errorMsg.toString());
            }else {
                return new MessageResult(true, "全部发送成功");
            }
        }
        return new MessageResult(false, "没有报名信息");
    }

    /**
     * 判断是否有报名表
     *
     * @return
     */
    @Override
    public boolean hasRecruitment() {
        return redisUtil.hasKey(RecruitmentService.RECRUIT);
    }



}
