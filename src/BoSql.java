import java.sql.*;
import java.util.ArrayList;

/**
 * 方便连接数据库的类
 */
public class BoSql {
    final String TABLE_NAME = "players";


    Connection mysqlConn = null;
    Statement stmt = null;
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    /** 默认连接本地数据库,使用root用户 */
    BoSql() {
        this("localhost:3306/break_out", "serverTimezone=UTC", "player", "a789456123");

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
            stmt = mysqlConn.createStatement();
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**更新玩家数据*/
    boolean updatePlayerInfo(String name,int score){
        String preSql = "UPDATE " +TABLE_NAME +" SET score=? WHERE player_name=?;";
        try {
            preStmt = mysqlConn.prepareStatement(preSql);
            preStmt.setInt(1,score);
            preStmt.setString(2,name);
            return preStmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    /**插入新玩家数据*/
    boolean insertPlayerInfo(String name,int score){
        String preSql = "INSERT INTO " +TABLE_NAME +" (player_name,score) VALUES(?,?);";
        try {
            System.out.println(score);
            preStmt = mysqlConn.prepareStatement(preSql);
            preStmt.setString(1,name);
            preStmt.setInt(2,score);
            return preStmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    /**从数据库中读取玩家信息
     * @param playerName 需要读取的特定玩家姓名
     * @param isAll 是否查询所有玩家,如果是,则无视上面参数,返回所有玩家的数据*/
    ArrayList<PlayerInfo> queryPlayerInfo(String playerName,boolean isAll){
        ArrayList<PlayerInfo> playerList = new ArrayList<>();
        String sql;
        if(isAll){
             sql = "SELECT player_name,score FROM players ORDER BY score DESC";
        }
        else{//查询特定玩家的
            sql = "SELECT player_name,score FROM players WHERE player_name= '" + playerName +"';";
        }
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                playerList.add(new PlayerInfo(rs.getString("player_name"),rs.getInt("score")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return playerList;
        }
        return playerList;
    }
}

/**数据存储类,存储玩家信息*/
class PlayerInfo{
    String playerName;//玩家姓名
    int score;//最高分

    PlayerInfo(String name,int score){
        this.playerName = name;
        this.score = score;
    }
}
