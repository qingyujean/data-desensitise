package com.DesensitiseTest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.DesensitiseTest.db.api.DBApi;
import com.DesensitiseTest.utils.DesensitizeUtils;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String ORIGINAL_DB_NAME = "zfsj_dyllry";//脱敏前某库
	private static String ORIGINAL_TABLE_NAME = "sta_party";//脱敏前某表
	private static String DESENSITIZED_COLUMNS_DB_NAME = "desensitize";//脱敏字段及规则配置数据库
	private static String DESENSITIZED_COLUMNS_TABLE_NAME = "fields";//脱敏字段及规则配置数据表
	private static String DESENSITIZED_RULES_TABLE_NAME = "rules";//脱敏规则说明表
	private static String DESENSITIZED_DB_NAME = "zfsj_dyllry1";//脱敏后目标库
	private static String DESENSITIZED_TABLE_NAME = "sta_party1";//脱敏后目标表
	
    public static void main( String[] args )
    {
    	DBApi api = new DBApi();
    	//1. 将数据迁移到新库
    	/**目前手动迁移，可使用命令行、脚本等
    	 * CREATE DATABASE zfsj_dyllry1;
    	 * CREATE TABLE sta_party1(DESENSITIZED_TABLE_NAME) SELECT FROM sta_party(ORIGINAL_TABLE_NAME);（会丢失主键、索引、extra等信息）
    	 * CREATE TABLE zfsj_dyllry1.sta_party1 LIKE zfsj_dyllry.sta_party;
		   INSERT INTO zfsj_dyllry1.sta_party1 SELECT * FROM zfsj_dyllry.sta_party;
    	 */
    	
    	//2. 创建脱敏字段配置表及脱敏规则说明表
    	/** 目前手动创建
    	 * 
    	 * CREATE DATABASE desensitize;
    	 * 
    	 * (1)fields表的创建：
    	 * CREATE TABLE fields (
			id INT NOT NULL AUTO_INCREMENT,
			field VARCHAR(50) NOT NULL COMMENT '字段名',
			field_comment VARCHAR(80) COMMENT '字段注释说明',
			desensitive_rule VARCHAR(100) COMMENT '脱敏规则',
			field_key VARCHAR(20) COMMENT '是否是主键',
			field_type VARCHAR(64) NOT NULL COMMENT '字段类型',
			table_name VARCHAR(50) NOT NULL COMMENT '表名',
			db_name VARCHAR(50) NOT NULL COMMENT '数据库名',
			PRIMARY KEY (id));
			
		 * (2)rules表的创建：
		 * 
    	 */
    	
    	//3. 获取待脱敏数据表的所有字段相关信息
    	//JSONArray fieldsInfo = api.getFieldInfoByDbNameAndTableName(DESENSITIZED_DB_NAME, DESENSITIZED_TABLE_NAME);
    	//4. 将3中获取的字段插入到字段规则配置表fields
    	//api.insert2fieldsTable(DESENSITIZED_COLUMNS_DB_NAME, DESENSITIZED_COLUMNS_TABLE_NAME, fieldsInfo);
    	//5. 人工审核字段，为敏感字段配置脱敏规则（暂时直接在库里配置规则）
    	
    	//6. 读取敏感字段即其相应规则
    	
    	JSONArray preDesensitiveFields = api.getPreDesensitizeField(DESENSITIZED_COLUMNS_DB_NAME, DESENSITIZED_COLUMNS_TABLE_NAME);
    	//Map<String, String> map_rule2field = new HashMap<String, String>();//规则->字段
    	Map<String, String> map_field2rule = new HashMap<String, String>();//字段->规则
    	for(Object field: preDesensitiveFields){
    		JSONObject f = JSONObject.fromObject(field);
    		//map_rule2field.put(f.get("desensitive_rule").toString(), f.get("field").toString());
    		map_field2rule.put(f.get("field").toString(), f.get("desensitive_rule").toString());
    	}
    	//System.out.println(map_rule2field);
    	System.out.println(map_field2rule);
    	
    	//7. 获取待脱敏数据(假设数据都已经迁移至新库新表，注意，所有字段类型换成string)
    	JSONArray preDesensitiveData =api.getPreDesensitizeData(DESENSITIZED_DB_NAME, DESENSITIZED_TABLE_NAME, preDesensitiveFields);
    	
    	//8. 根据敏感字段的规则进行脱敏
    	for(Object record: preDesensitiveData){
    		JSONObject r = JSONObject.fromObject(record);//{"id":"47866a2164204255b02bbf12acbd77b7","user_name":"曾庆金","card_id":"210104195910064955","birthday":"1959-10-06","Home_address":"沈阳市大东区天后宫路95号5-8-2","Contact_tel":"13504013738"}
    		Iterator iter = r.keys();
    		while(iter.hasNext()){
    			String field = (String) iter.next();
    			String value = r.get(field).toString();
    			String new_value = desensitize(field, value, map_field2rule.get(field));//按规则对字段进行脱敏
    			r.put(field, new_value);
    		}
    		System.out.println("脱敏后r="+r); 
    		//9. 将脱敏数据入库
        	api.updateDesensitizedData(DESENSITIZED_DB_NAME, DESENSITIZED_TABLE_NAME, r, preDesensitiveFields, map_field2rule);
    	}
    	
    }
    
    
    public static String desensitize(String field, String original_value, String rule){
    	String new_value = "";
    	switch(rule){
    	case "_IDNum": 
    		new_value = DesensitizeUtils.idCard(original_value);
    		//System.out.println(field+"的脱敏规则是"+rule);
    		break;
    	case "_Name":
    		new_value = DesensitizeUtils.account(original_value);
    		//System.out.println(field+"的脱敏规则是"+rule);
    		break;
    	case "_Birthday":
    		//new_value = DesensitizeUtils.birthday(original_value);
    		new_value = "new_value";
    		//System.out.println(field+"的脱敏规则是"+rule);
    		break;
    	case "_Address":
    		new_value = DesensitizeUtils.address(original_value);
    		//System.out.println(field+"的脱敏规则是"+rule);
    		break;
    	case "_PhoneNum":
    		new_value = DesensitizeUtils.mobilePhone(original_value);
    		//System.out.println(field+"的脱敏规则是"+rule);
    		break;
    	//case "_PrimaryKey":
    		//System.out.println(field+"的脱敏规则是"+rule);
    		//break;
    	default:
    		new_value = original_value;//是主键，不用脱敏
    		//System.out.println(field+"是主键，不用脱敏");	
    	}
    	return new_value;
    }
}
