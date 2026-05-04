package oily.top.model;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;
/**
 * 
 * @author 奥利顶<oily.top>
 */
public class Admin extends Model<Admin> {
    private static final long serialVersionUID = 1L;
    public static final Admin dao = new Admin();

    public Admin findByUsername(String username) {
        return findFirst("SELECT * FROM t_admin WHERE username = ?", username);
    }

    public boolean validateLogin(String username, String password) {
        Admin admin = findByUsername(username);
        return admin != null && admin.getStr("password").equals(password);
    }
}
