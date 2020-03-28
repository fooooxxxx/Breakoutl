import java.sql.*;

/**
 * 方便连接数据库的类
 */
public class BoSql {
    Connection mysqlConn = null;
    Statement stmt = null;
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    /** 默认连接本地数据库,使用root用户 */
    BoSql() {
        this("localhost", "serverTimezone=UTC", "root", "a8053688");

    }

    /**
     * 新建mysql数据库连接
     *
     * @param url      连接目标
     * @param extra    额外信息,一般为空
     * @param userName 登录用户名
     * @param password 登录密码
     */
    BoSql(String url, String extra, String userName, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//加载驱动
            mysqlConn = DriverManager.getConnection("jdbc:mysql://" + url + "?" + extra, userName, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
