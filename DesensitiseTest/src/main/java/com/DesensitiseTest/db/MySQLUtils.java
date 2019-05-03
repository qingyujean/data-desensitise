package com.DesensitiseTest.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class MySQLUtils {
	private static Logger log = Logger.getLogger("mysqldb");
	private static Connection coon = null;
	
	
	public static boolean connect2db(){
		try{
			//1.加载对应数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			
			
			//2.获取数据库连接
			String url = "jdbc:mysql://localhost:3306/information_schema?characterEncoding=UTF-8&autoReconnect=true&useSSL=false&useOldAliasMetadataBehavior=true";
			String username = "root";
			String password = "123456";
			
			/*
			//2.获取数据库连接
			String url = "jdbc:mysql://10.10.169.31:4000/information_schema?characterEncoding=UTF-8&autoReconnect=true&useSSL=false&useOldAliasMetadataBehavior=true";
			String username = "root";
			String password = "ucas1234";
			*/
			
			
			coon = DriverManager.getConnection(url, username, password);
			log.info("数据库连接成功！");
			return true;
		}catch(Exception e){
			log.severe("数据库连接失败！");
			e.printStackTrace();
			return false;
		}

	}
	
	
	
	public static String query2db(String sql, boolean isReturnMany ){
		PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
	    ResultSet rs = null;// 创建一个结果集对象
	    try{
	    	//3.数据库操作-增删改查
			pre = coon.prepareStatement(sql);
			rs = pre.executeQuery();
			
			ResultSetMetaData meta = rs.getMetaData();
			int colCnt = meta.getColumnCount();
			
			if(isReturnMany == false){
				JSONObject row = new JSONObject();
				if(rs.next()){
					String colName = "";
					for(int i = 1; i<=colCnt; i++){
						colName = meta.getColumnName(i);
						String value = rs.getString(colName);
						if(value==null ){
							row.put(colName, "");
						}else{
							row.put(colName, value);
						}
						
					}
				}
				return row.toString();
			}else{
				JSONArray rows = new JSONArray();
				String colName = "";
				while(rs.next()){
					JSONObject row = new JSONObject();
					for(int i = 1; i<=colCnt; i++){
						colName = meta.getColumnName(i);
						//System.out.println("colName="+colName);
						Object value = rs.getObject(i);
						if(value==null){
							row.put(colName, "");
						}else{
							row.put(colName, value);
						}
						
					}
					rows.add(row);
				}
				return rows.toString();
			}

			
			
	    }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			//4.释放资源
			try{
				if(rs!=null){
					rs.close();
				}
			
				
				if(pre!=null){
					pre.close();
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	//3.数据库操作-增删改
	public static boolean updateDb(String sql){
		PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
		boolean state = true;
	    try{
		    //3.数据库操作-增删改查
			pre = coon.prepareStatement(sql);
			pre.executeUpdate(sql);//返回执行受到影响的行数。
			log.info("数据表更新成功！");
		}catch(Exception e){
	    	e.printStackTrace();
	    	state = false;
		    log.info("数据表更新失败！");
		}finally{
			//4.释放资源
			try{
					
				if(pre!=null){
					pre.close();
				}
			}catch(Exception e){
				e.printStackTrace();
				state = false;
				log.info("关闭pre失败！");
			}
		}
	    return state;
	}
	
	public static void closedbconnect(){
		if(coon!=null){
			try {
				coon.close();
				System.out.println("数据库连接已关闭！");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
