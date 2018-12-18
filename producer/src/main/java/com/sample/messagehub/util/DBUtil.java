package com.sample.messagehub.util;

import com.sample.messagehub.core.GroupInfo;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtil {

    private static DataSource ds;
    private static Connection conn;

    public static Connection getConnection() throws NamingException, SQLException {

        if(conn == null) {

            if(ds == null) {

                InitialContext ctx = new InitialContext();
                ds = (DataSource) ctx.lookup("jdbc/mysqlDB");
            }

            conn = ds.getConnection();
        }

        return conn;
    }


    public static void insertSendLog(GroupInfo group) {

        Connection conn = null;
        PreparedStatement ps = null;

        try {

            conn = getConnection();
            ps = conn.prepareStatement("insert into msglog(M_PARTITION, M_OFFSET, GROUP_ID, ACTIVITY_ID, RETRY_COUNT, DEMO_COMMAND, STATUS, RV_SUBJECT, APP, APP_INST, CREATE_TIME, INSERT_TIME) "
                    + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp(), current_timestamp())");

            ps.setLong(1,  group.getPartitionId());
            ps.setLong(2, group.getOffset());
            ps.setInt(3, group.getGroupId());
            ps.setInt(4, group.getActId());
            ps.setInt(5,group.getRetry_count());
            ps.setString(6, "");
            ps.setString(7, group.getStatus());
            ps.setString(9, group.getApp());
            ps.setString(10,  group.getApp_inst());

            ps.execute();

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try {
                ps.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
