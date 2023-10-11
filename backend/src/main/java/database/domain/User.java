package database.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * 用户自增的主键，无实际意义
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户的id，为学生老师的学工号
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Integer uid;

    /**
     * 用户的密码，经过两次sha256加密
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @Getter(onMethod = @__(@JsonIgnore))
    @Setter(onMethod = @__(@JsonProperty))
    private String password;

    /**
     * 用户的姓名
     */
    private String name;

    /**
     * 用户的邮箱
     */
    private String mail;

    /**
     * 用户的过期时间
     */
    private Date expire;

    /**
     * 用户的禁用标识
     */
    private Integer disabled;

    /**
     * 用户的路由信息
     */
    @TableField(exist = false)
    private String menu;

    @TableField(exist = false)
    private Integer role;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
                && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getMail() == null ? other.getMail() == null : this.getMail().equals(other.getMail()))
                && (this.getExpire() == null ? other.getExpire() == null : this.getExpire().equals(other.getExpire()))
                && (this.getDisabled() == null ? other.getDisabled() == null : this.getDisabled().equals(other.getDisabled()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getMail() == null) ? 0 : getMail().hashCode());
        result = prime * result + ((getExpire() == null) ? 0 : getExpire().hashCode());
        result = prime * result + ((getDisabled() == null) ? 0 : getDisabled().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uid=").append(uid);
        sb.append(", password=").append(password);
        sb.append(", name=").append(name);
        sb.append(", mail=").append(mail);
        sb.append(", expire=").append(expire);
        sb.append(", disabled=").append(disabled);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}