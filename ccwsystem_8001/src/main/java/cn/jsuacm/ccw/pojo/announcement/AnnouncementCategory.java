package cn.jsuacm.ccw.pojo.announcement;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName AnnouncementCategory
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/21 21:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "announcement_category")
@ApiModel("公告类型分类")
public class AnnouncementCategory implements Serializable{


    /**
     * 公告类型的分类id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "公告分类的id", dataType = "int")
    private int id;


    /**
     * 分类的文章的权限，分别为：任何人、正在实验室的成员、管理员、超级管理员
     */
    @TableField(value = "role", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "分类的文章的权限，分别为：任何人、正在实验室的成员、管理员、超级管理员", dataType = "enum")
    private Role  role;

    /**
     * 权限分类的枚举类
     */
    public enum Role {
        // 任何人、正在实验室的成员、管理员、超级管理员
        ALL, ROLE_ING, ROLE_ADMIN, ROLE_ADMINISTRATOR;


        public static boolean hasRole(String role){
            if (ROLE_ING.toString().equals(role)){
                return true;
            }
            if (ROLE_ADMIN.toString().equals(role)){
                return true;
            }
            if (ROLE_ADMINISTRATOR.toString().equals(role)){
                return true;
            }
            return false;
        }
    }


    /**
     * 分类板块的名称
     */
    @TableField(value = "name", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "分类板块的名称", dataType = "string")
    private String name;
}
