package cn.jsuacm.ccw.pojo.recruit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName NewMenberInfo
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/05 20:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "报名用户实体类")
public class NewMenberInfo implements Serializable{


    @ApiModelProperty(value = "报名者姓名", required = true, dataType = "string")
    private String name;


    @ApiModelProperty(value = "报名者邮箱", required = true, dataType = "string")
    private String email;

    @ApiModelProperty(value = "报名者qq", required = true, dataType = "string")
    private String qq;

    @ApiModelProperty(value = "报名者照片Base64字符串", required = true, dataType = "string")
    private String pictrue;

    @ApiModelProperty(value = "报名者性别, 直接生成好表单选项，为男或者女", required = true, dataType = "string")
    private String sex;

    @ApiModelProperty(value = "报名者班级， 表单设置成选择项， 管理员添加表单的时候这个组件自动存在，但是不需要指定属性名，但是需要添加班级的选项", required = true, dataType = "int")
    private int from;

    @ApiModelProperty(value = "报名者自我介绍", required = true, dataType = "string")
    private String content;

    @ApiModelProperty(value = "报名者年龄", required = true, dataType = "int")
    private int age;


    @ApiModelProperty(value = "报名者最终得分", required = false, dataType = "map")
    private Map<String, BigDecimal> scores;


    @ApiModelProperty(value = "报名者其他信息", required = true, dataType = "string")
    private String obj;



    /**
     * 检查填写的信息是否正确
     * @param newMenberInfo
     * @return
     */
    public static boolean checkInfo(NewMenberInfo newMenberInfo){


        String exrp = "^([\\w-_]+(?:\\.[\\w-_]+)*)@((?:[a-z0-9]+(?:-[a-zA-Z0-9]+)*)+\\.[a-z]{2,6})$";
        Pattern p = Pattern.compile(exrp);

        Pattern pattern = Pattern.compile("[0-9]*");
        // 判断年龄  不得小于15 不得大于30
        if (newMenberInfo.getAge() < 15 || newMenberInfo.getAge() > 30){
            return false;
        }

        // 用户个人评价不得为空， 也不能大于1000字
        if (newMenberInfo.getContent() == null || "".equals(newMenberInfo.getContent()) || newMenberInfo.getContent().length() > 1000){
            return false;
        }


        // 用户的邮箱验证
        if (newMenberInfo.getEmail() == null || "".equals(newMenberInfo.getEmail())){
            return false;
        }
        Matcher matcher=p.matcher(newMenberInfo.getEmail());
        if(!matcher.matches()){
            return false;
        }


        // 用户的班级不能不填写
        if (newMenberInfo.getFrom() == 0){
            return false;
        }

        // 用户的姓名验证
        if (newMenberInfo.getName() == null || "".equals(newMenberInfo.getName()) || newMenberInfo.getName().length() < 2){
            return false;
        }

        // 用户的照片验证
        if (newMenberInfo.getPictrue() == null || newMenberInfo.getPictrue().length() == 0){
            return false;
        }


        // 用户的qq验证
        if (newMenberInfo.getQq() == null || newMenberInfo.getQq().length() == 0 || !pattern.matcher(newMenberInfo.getQq()).matches()){
            return false;
        }

        // 用户的性别验证
        if (newMenberInfo.getSex() == null || "".equals(newMenberInfo.getSex()) || !("男".equals(newMenberInfo.getSex()) || "女".equals(newMenberInfo.getSex()))){
            return false;
        }


        return true;
    }
}
