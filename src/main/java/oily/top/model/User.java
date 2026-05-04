package oily.top.model;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.kit.HashKit;

/**
 * 用户模型
 * @author 奥利顶<oily.top>
 */
public class User extends Model<User> {
    public static final User dao = new User();
    
    /**
     * 用户登录验证
     */
    public User login(String username, String password) {
        String md5Password = HashKit.md5(password);
        return User.dao.findFirst(
            "SELECT * FROM user WHERE username=? AND password=?",
            username, md5Password
        );
    }
    
    /**
     * 修改密码
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = User.dao.findById(userId);
        if (user != null && user.get("password").equals(HashKit.md5(oldPassword))) {
            user.set("password", HashKit.md5(newPassword));
            return user.update();
        }
        return false;
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateProfile(int userId, String nickname, String email, String username ) {
        User user = User.dao.findById(userId);
        if (user != null) {
            user.set("NICKNAME", nickname);
            user.set("EMAIL", email);
            user.set("USERNAME", username );
            return user.update();
        }
        return false;
    }
    
    /**
     * 获取用户信息（不含敏感字段）
     */
    public User getSafeUser(int id) {
        return User.dao.findFirst(
            "SELECT id,username,nickname,email,avatar,role,create_time FROM user WHERE id=?", id
        );
    }
}