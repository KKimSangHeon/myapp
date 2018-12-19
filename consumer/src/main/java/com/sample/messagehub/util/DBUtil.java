package com.sample.messagehub.util;

import com.sample.messagehub.core.GroupInfo;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {

    private static final Logger logger = Logger.getLogger(DBUtil.class);

    private static DataSource ds;
    private static Connection conn;

    public static Connection getConnection() throws NamingException, SQLException {

        if (conn == null) {

            if (ds == null) {

                InitialContext ctx = new InitialContext();
                ds = (DataSource) ctx.lookup("jdbc/mysqlDB");
            }

            conn = ds.getConnection();
        }

        return conn;
    }


    public static void insertGroupActivity(GroupInfo group) {

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {

            conn = getConnection();

            ps = conn.prepareStatement("select M_OFFSET, RETRY_COUNT from msglog where M_PARTITION = ? and M_OFFSET = ? and GROUP_ID = ? and ACTIVITY_ID = ? and APP = ?");
            ps.setLong(1, group.getPartitionId());
            ps.setLong(2,  group.getOffset());
            ps.setInt(3, group.getGroupId());
            ps.setInt(4, group.getActId());
            ps.setString(5, group.getApp());

            rs = ps.executeQuery();

            if(rs.next()) {

                int retryCnt = rs.getInt("RETRY_COUNT");

                ps2 = conn.prepareStatement("update msglog set RETRY_COUNT = RETRY_COUNT + 1 where M_PARTITION = ? and M_OFFSET = ? and GROUP_ID = ? and ACTIVITY_ID = ? and APP = ?");
                ps2.setLong(1, group.getPartitionId());
                ps2.setLong(2, group.getOffset());
                ps2.setInt(3, group.getGroupId());
                ps2.setInt(4, group.getActId());
                ps2.setString(5,  group.getApp());

                ps2.execute();

            }else {

                ps2 = conn.prepareStatement("insert into msglog(M_PARTITION, M_OFFSET, GROUP_ID, ACTIVITY_ID, RETRY_COUNT, DEMO_COMMAND, STATUS,  APP, APP_INST, INSERT_TIME) "
                        + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp())");

                ps2.setLong(1,  group.getPartitionId());
                ps2.setLong(2, group.getOffset());
                ps2.setInt(3, group.getGroupId());
                ps2.setInt(4, group.getActId());
                ps2.setInt(5,group.getRetry_count());
                ps2.setString(6, "");
                ps2.setString(7, group.getStatus());
                ps2.setString(8, group.getApp());
                ps2.setString(9,  group.getApp_inst());

                ps2.execute();
            }

        }catch(Exception e) {
            try{
                conn = getConnection();
                insertGroupActivity(group);

            }catch(Exception ex){}

        }finally {
            try { if(ps != null) ps.close();	} catch (SQLException e) {}
            try { if(ps2 != null) ps2.close();	} catch (SQLException e) {}
            try { if(rs != null) rs.close();	} catch (SQLException e) {}
        }



    }


    public static void updateGroupStatus(GroupInfo group) {

        logger.info("Update GROUP Status : " + group.getStatus());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement("update msglog set status = ? where M_PARTITION = ? and M_OFFSET = ? and GROUP_ID = ? and ACTIVITY_ID = ? and APP = ?");
            ps.setString(1, group.getStatus());
            ps.setLong(2, group.getPartitionId());
            ps.setLong(3, group.getOffset());
            ps.setInt(4, group.getGroupId());
            ps.setInt(5, group.getActId());
            ps.setString(6,  group.getApp());

            ps.execute();

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try { if(ps != null) ps.close();	} catch (SQLException e) {}
            try { if(rs != null) rs.close();	} catch (SQLException e) {}
        }

    }

    public static boolean touchDB() {

        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean result = false;

        try {
            ps = getConnection().prepareStatement("select 1 from dual");
            rs = ps.executeQuery();

            if(rs.next()) {
                result = true;
            }
        }catch(Exception e) {
            try { if(ps != null) ps.close();	} catch (SQLException e1) {}
            try { if(rs != null) rs.close();	} catch (SQLException e1) {}
        }

        return result;
    }
}
