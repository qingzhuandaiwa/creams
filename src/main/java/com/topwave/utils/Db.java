package com.topwave.utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作工具类
 */
public class Db {

    private static Logger logger = Logger.getLogger(Db.class);
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    private static DruidDataSource dataSource;
    private static QueryRunner queryRunner;

    public static synchronized void init(String url, String username, String password) {
        if (null == dataSource) {
            dataSource = new DruidDataSource();
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            //dataSource.setMaxWait(1000 * 24);
            dataSource.setInitialSize(2);
            dataSource.setMinIdle(1);
            dataSource.setMaxActive(3);
            dataSource.setRemoveAbandoned(false);

            queryRunner = new QueryRunner(dataSource);
        }
    }

    public static Connection getConnection() throws Exception {
        Connection conn = threadLocal.get();
        if (null != conn) {
            return conn;
        }

        threadLocal.set(dataSource.getConnection());
        return threadLocal.get();
    }

    public static void closeConnection(Connection connection) throws Exception {
        if (null != connection) {
            connection.close();
            threadLocal.remove();
        }
    }

    /**
     * 获取数据库表结构信息
     * @return
     * @throws Exception
     */
    public static Map<String, List<String>> getMeta() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        Connection connection = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getTables(null, "%", "%" , new String[]{"TABLE"});

            while(rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                List<String> columnList = new ArrayList<String>();

                ResultSet colRet = dbm.getColumns(null,"%", tableName,"%");
                while(colRet.next()) {
                    String columnName = colRet.getString("COLUMN_NAME");
                    columnList.add(columnName);
                }
                map.put(tableName, columnList);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != connection) {
                    closeConnection(connection);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;
    }


    /**
     * 查询id集合.
     * @param sql
     * @return
     */
    public static List<Integer> find(String sql) {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement pst = null;
        List<Integer> integers = new ArrayList<Integer>();

        try {
            connection = getConnection();
            pst = connection.prepareStatement(sql);
            resultSet = pst.executeQuery();

            while (resultSet.next()) {
                integers.add(resultSet.getInt(1));
            }

            return integers;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != resultSet) {
                    resultSet.close();
                }

                if (null != pst) {
                    pst.close();
                }

                if (null != connection) {
                    closeConnection(connection);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return integers;
    }

    /**
     * 更新数据
     * @param sql
     * @throws Exception
     */
    public static void update(String sql) throws Exception {
        queryRunner.update(sql);
    }

    /**
     * 新增数据
     * @param tableName
     * @param map
     * @throws Exception
     */
    public static void insert(String tableName, Map<String, Object> map) {
        tableName = tableName.trim();
        List<Object> paras = new ArrayList();

        StringBuilder sql = new StringBuilder();

        sql.append("insert into `");
        sql.append(tableName).append("`(");
        StringBuilder temp = new StringBuilder();
        temp.append(") values(");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (paras.size() > 0) {
                sql.append(", ");
                temp.append(", ");
            }
            sql.append('`').append(entry.getKey()).append('`');
            temp.append('?');
            paras.add(entry.getValue());
        }
        sql.append(temp.toString()).append(')');

        Connection connection = null;
        PreparedStatement ps = null;


        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            fillStatement(ps, paras);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ps) {
                    ps.close();
                }
                if (null != connection) {
                    closeConnection(connection);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static <T> List<T> query(Connection conn, String sql) throws Exception {
		List result = new ArrayList();
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		int colAmount = rs.getMetaData().getColumnCount();
		if (colAmount > 1) {
			while (rs.next()) {
				Object[] temp = new Object[colAmount];
				for (int i=0; i<colAmount; i++) {
					temp[i] = rs.getObject(i + 1);
				}
				result.add(temp);
			}
		}
		else if(colAmount == 1) {
			while (rs.next()) {
				result.add(rs.getObject(1));
			}
		}
		rs.close();
        pst.close();
		return result;
	}
    
    public static int update(Connection conn, String sql) throws Exception {
    	PreparedStatement pst = conn.prepareStatement(sql);
		int result = pst.executeUpdate();
		pst.close();

		return result;
    }

    private static void fillStatement(PreparedStatement pst, List<Object> paras) throws SQLException {
        for (int i = 0, size = paras.size(); i < size; i++) {
            pst.setObject(i + 1, paras.get(i));
        }
    }
}
