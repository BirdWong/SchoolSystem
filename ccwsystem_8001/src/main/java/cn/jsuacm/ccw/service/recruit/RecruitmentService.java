package cn.jsuacm.ccw.service.recruit;


import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.recruit.NewMenberInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @ClassName RecruitmentService
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/04 20:46
 */
public interface RecruitmentService {



    /**
     * redis中存储报名信息的key
     */
    public static final String RECRUIT = "recruit_key";

    /**
     * 添加一个报名信息
     * @param newMenberInfo
     * @return
     */
    public MessageResult add(NewMenberInfo newMenberInfo);


    /**
     * 获取所有的报名信息，并且按照班级分类
     * @return
     */
    public TreeMap<Integer, List<NewMenberInfo>> getToClass();

    /**
     * 按照输入的班级返回报名列表
     * @param num 班级
     * @return
     */
    public List<NewMenberInfo> getByClass(int num);


    /**
     * 获取报名的班级。 可以通过拥有的班级然后跟别获取每个班级的报名信息
     * @return
     */
    public List<Integer> getClassNumbers();


    /**
     * 修改报名信息
     * @param menberInfo
     * @return
     */
    public MessageResult updateInfo(NewMenberInfo menberInfo);


    /**
     * 通过邮箱获取一个报名信息
     * @param email
     * @return
     */
    public NewMenberInfo getByEmail(String email);


    /**
     * 按照平均分下载面试分数排名
     */
    public void download(HttpServletResponse response);


    /**
     * 面试打分
     * @param email
     * @param uid
     * @param score
     * @return
     */
    public MessageResult setScore(String email, String uid, double score);


    /**
     * 上传图片转化为base64
     * @return
     */
    public MessageResult uploadPictrue(MultipartFile multipartFile);


    /**
     * 发送邮件给所有的面试者通知面试时间
     * @param date 面试时间
     * @param address 面试地点
     * @param msg 其他提示性信息
     * @return
     */
    public MessageResult sendEmail(Date date, String address, String msg);






    /**
     * 判断是否有报名表
     * @return
     */
    public boolean hasRecruitment();





}
