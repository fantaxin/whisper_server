package com.company;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataBase {
    static final String JDBC_driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static final String URL = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=Whisper";
    static final String userName = "sa";
    static final String userPassword = "sakura";

    private Connection conn = null;
    private Statement stmt = null;

    public DataBase() {
        conn = null;
        stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_driver);
            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(URL, userName, userPassword);
            // 执行查询
            System.out.println("实例化Statement对象...");
            stmt = conn.createStatement();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    }

    public SearchResult SearchRegister(String user_name) {
        String SQL_SEARCH_REGISTER =
                "SELECT id, password " +
                        "FROM dbo.register " +
                        "WHERE name = '" + user_name + "' ;";
        boolean success = false;
        try {
            ResultSet rs = stmt.executeQuery(SQL_SEARCH_REGISTER);  //导入的jar包为：import java.sql.ResultSet
            rs.next();//rs默认指向第一行的前一行，所以要后移一行
            int id = rs.getInt(1);
            String password = rs.getString(2);
            success = true;
            SearchResult result = new SearchResult(success, id, user_name, password);
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            success = false;
            return new SearchResult(success, 0, "false", "false");
        }
    }

    public void SearchChatfile()
    {
        String SQL_SEARCH_CHATFILE =
                "SELECT sender_id, send_time, message " +
                        "FROM chatfile ;" ;
        //"WHERE send_time ";
        try {
            ResultSet rs = stmt.executeQuery(SQL_SEARCH_CHATFILE);  //导入的jar包为：import java.sql.ResultSet
            //rs.afterLast();
            int num = 0;
            while(rs.next()){
                if(num++>10)
                {
                    break;
                }
                String sender_id = rs.getString(1);
                String send_time = rs.getString(2);
                String message = rs.getString(3);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void InsertRegister(String user_name, String user_password)
    {
        String SQL_INSERT_REGISTER =
                "INSERT INTO dbo.register(name, password) values('"+user_name+"', '"+user_password+"');";
        try {
            stmt.executeUpdate(SQL_INSERT_REGISTER);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean InsertOnline(int id, String ip, int port, String name)
    {
        String SQL_INSERT_ONLINE =
                "INSERT INTO dbo.online(id, ip, port, name) values( " + id + ", '"+ip+"', "+port+ ", '"+name+"' );";
        try {
            stmt.executeUpdate(SQL_INSERT_ONLINE);
            System.out.println("加入成功");
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("加入失败");
            return false;
        }
    }

    public void DeleteOnline(int id)
    {
        String SQL_DELETE_ONLINE =
                "DELETE FROM dbo.online WHERE id = " + id + " ;";
        try {
            stmt.executeUpdate(SQL_DELETE_ONLINE);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //success, id, ip, port
    public SearchResult SearchOnline(int id) {
        String SQL_SEARCH_ONLINE =
                "SELECT ip, port " +
                        "FROM dbo.online " +
                        "WHERE id = " + id + " ;";
        boolean success = false;
        try {
            ResultSet rs = stmt.executeQuery(SQL_SEARCH_ONLINE);  //导入的jar包为：import java.sql.ResultSet
            rs.next();//rs默认指向第一行的前一行，所以要后移一行
            String ip = rs.getString(1);
            int port = rs.getInt(2);
            System.out.println(ip+"||"+port);
            success = true;
            SearchResult result = new SearchResult(success, id, ip, port);
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            success = false;
            return new SearchResult(success, 0, "false", 0);
        }
    }

    public List<SearchResult> SerchOnline()
    {
        String SQL_SEARCH_ONLINE =
                "SELECT id, ip, port, name " +
                        "FROM dbo.online ;";
        boolean success = false;
        List<SearchResult> list = new ArrayList<SearchResult>();
        try {
            ResultSet rs = stmt.executeQuery(SQL_SEARCH_ONLINE);  //导入的jar包为：import java.sql.ResultSet
            while(rs.next())//rs默认指向第一行的前一行，所以要后移一行
            {
                int id = rs.getInt(1);
                String ip = rs.getString(2);
                int port = rs.getInt(3);
                String name = rs.getString(4);
                success = true;
                SearchResult result = new SearchResult(success, id, name, ip, port);
                list.add(result);
            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            success = false;
            SearchResult s = new SearchResult(success, 0, "name", "ip", 0);
            list.add(s);
            list.get(0).success = false;
            return list;
        }
    }

    public void UpdateOnline(int id, String ip)
    {
        String SQL_UPDATE_ONLINE =
                "UPDATE dbo.online " +
                        "SET ip = " + ip +" "+
                        "WHERE id = " + id + " ;";
        try {
            stmt.executeUpdate(SQL_UPDATE_ONLINE);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void InsertChatfile(int sender_id, String send_time, String message)
    {
        String SQL_INSERT_CHATFILE =
                "INSERT INTO chatfile values(sender_id, send_time, message);";
        try {
            stmt.executeUpdate(SQL_INSERT_CHATFILE);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void close() {
        // 关闭资源
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se2) {
        }// 什么都不做
        try {
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
