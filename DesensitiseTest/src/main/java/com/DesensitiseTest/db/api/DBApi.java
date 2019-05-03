package com.DesensitiseTest.db.api;

import java.util.Map;
import java.util.logging.Logger;

import com.DesensitiseTest.db.MySQLUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DBApi {
	private static Logger log = Logger.getLogger("DBApi");
	boolean connDbState = false;
	
	public DBApi(){
		connDbState = MySQLUtils.connect2db();
	}
	
	public void closeDb(){
		MySQLUtils.closedbconnect();
	}
	
	
	//获取某个库中某个表的所有数据字段的相关信息
	public JSONArray getFieldInfoByDbNameAndTableName(String db_name, String table_name){
		JSONArray res = null;
		String sql = "SELECT column_name, column_comment, column_key, data_type, "
					+ "table_name, table_schema FROM columns "
					+ "WHERE table_schema='"+db_name+"' AND table_name='"+table_name+"'";
		System.out.println("sql="+sql);
		res = getRows(sql);
		System.out.println("res="+res);
		return res;
	}
	
	//将获取的字段插入到fileds表里
	public boolean insert2fieldsTable(String db_name, String table_name, JSONArray fieldsInfo){
		String sql = "";
		boolean insertState = true;
		for(Object fieldInfo: fieldsInfo){
			JSONObject f_info = JSONObject.fromObject(fieldInfo);
			sql = "INSERT INTO "+db_name+"."+table_name+" (FIELD, FIELD_COMMENT, FIELD_KEY, FIELD_TYPE, "
				+ "TABLE_NAME, DB_NAME) VALUES ("
				+ "'"+f_info.get("column_name")+"', "
				+ "'"+f_info.get("column_comment")+"', "
				+ "'"+f_info.get("column_key")+"', "
				+ "'"+f_info.get("data_type")+"', "
				+ "'"+f_info.get("table_name")+"', "
				+ "'"+f_info.get("table_schema")+"')";
			System.out.println("sql="+sql);
			insertState = MySQLUtils.updateDb(sql);
		}
		return insertState;
	}
	
	
	//获取预脱敏的字段
	public JSONArray getPreDesensitizeField(String db_name, String table_name){
		JSONArray res = null;
		String sql = "SELECT field, desensitive_rule FROM "+db_name+"."+table_name
				+" WHERE desensitive_rule IS NOT NULL";
		System.out.println("sql="+sql);
		res = getRows(sql);
		System.out.println("res="+res);
		return res;
	}
	
	//获取预脱敏的数据（备份/迁移后的数据）
	public JSONArray getPreDesensitizeData(String db_name, String table_name, JSONArray preDesensitiveFields){
		JSONArray res = null;
		String sql = "SELECT ";
    	boolean firstField = true;
    	for(Object field: preDesensitiveFields){
    		JSONObject f = JSONObject.fromObject(field);
    		if(firstField){
    			sql += f.get("field");
    			firstField = false;
    		}else
    			sql += ", "+f.get("field");
    			
    	}
    	sql += " FROM "+db_name+"."+table_name;
    	System.out.println("sql="+sql);
		res = getRows(sql);
		System.out.println("res="+res);
		return res;
	}
	
	
	//更新脱敏数据
	/**
	 * 暂时update时没考虑数据库字段类型与string不匹配的情况
	 * @param db_name
	 * @param table_name
	 * @param record
	 * @param preDesensitiveFields
	 * @param map_field2rule
	 * @return
	 */
	public boolean updateDesensitizedData(String db_name, String table_name, JSONObject record, JSONArray preDesensitiveFields, Map<String, String> map_field2rule){
		boolean updateState = true;
		String sql = "UPDATE "+db_name+"."+table_name+" SET ";
		boolean firstField = true;
		String pri = "";
    	for(Object field: preDesensitiveFields){
    		JSONObject f = JSONObject.fromObject(field);
    		
    		if(map_field2rule.get(f.get("field")).equals("_PrimaryKey")){//是主键
    			pri = f.get("field").toString();
    		}else{
    			//System.out.println("主键="+map_field2rule.get(f.get("field")));
    			if(firstField){
        			sql += f.get("field")+"='"+record.get(f.get("field"))+"'";
        			firstField = false;
        		}else
        			sql += ", "+f.get("field")+"='"+record.get(f.get("field"))+"'";
    		}
    		
    			
    	}
    	sql += " WHERE "+pri+"='"+record.get(pri)+"'";
    	System.out.println("sql="+sql);
		updateState = MySQLUtils.updateDb(sql);
		return updateState;
	}
	

	
	
	//该sql仅会返回一行数据
	public JSONObject getRow(String sql) {
		if(!connDbState){
	    	log.info("数据库未连接！");
	    	System.exit(0);
	    }
			
		JSONObject row;
		//String sql = "SELECT COUNT(*) AS total_table_num FROM tables";
		String res = MySQLUtils.query2db(sql, false);
		//System.out.println("res="+res);
		if(res==null){
			return new JSONObject();
		}else{
			row = JSONObject.fromObject(res);
		}
		//System.out.println("row="+row);
		return row;
	}
	
	//该sql会返回多行数据
	public JSONArray getRows(String sql) {
		if(!connDbState){
	    	log.info("数据库未连接！");
	    	System.exit(0);
	   	}
			
		JSONArray rows;
		String res = MySQLUtils.query2db(sql, true);
		//System.out.println("res="+res);
		if(res==null){
			return new JSONArray();
		}else{
			rows = JSONArray.fromObject(res);
		}
		//System.out.println("row="+row);
		return rows;
	}
}
