package com.DesensitiseTest.utils;

import org.apache.commons.lang3.StringUtils;

public class DesensitizeUtils {
	/** 中文名：只显示第一个汉字，其他隐藏为（名字长度-1）个星号
	 *  示例：李小四 -> 李**
	 */
	public static String chineseName(String fullname) {
		if(StringUtils.isBlank(fullname)) {
			return "";
		}
		String name = StringUtils.left(fullname, 1);//得到字符串str从左边数len长度的子串。 
		return StringUtils.rightPad(name, fullname.length(), "*");
	}
	
	
	
	/** 身份证号：只显示前6位和后4位，其他隐藏为星号
	 *  示例：420528199104300327 -> 420528******0327
	 */
	public static String idCard(String idStr) {
		if(StringUtils.isBlank(idStr)) {
			return "";
		}
		String idLeft = StringUtils.left(idStr, 6);
		String idRight = StringUtils.right(idStr, 4);//得到字符串str从右边数len长度的子串
		return StringUtils.rightPad(idLeft, 18-4, "*").concat(idRight);
	}
	
	
	
	/** 手机号：只显示前3位和后4位，其他隐藏为星号
	 *  示例：18601344276 -> 186****4276
	 */
	public static String mobilePhone(String phoneNum) {
		if(StringUtils.isBlank(phoneNum)) {
			return "";
		}
		String phoneLeft = StringUtils.left(phoneNum, 3);
		String phoneRight = StringUtils.right(phoneNum, 4);
		return StringUtils.rightPad(phoneLeft, 11-4, "*").concat(phoneRight);
	}
	
	
	
	/** 地址：只显示省、市、区，其他隐藏为星号
	 *  示例：北京市海淀区北清路156号龙芯1号楼 -> 北京市海淀区****
	 */
	public static String address(String address) {
		if (StringUtils.isBlank(address)) {
            return "";
        }
		//indexOf: 返回字符searchChar在字符串str中第一次出现的位置
		String addressLeft = StringUtils.left(address, address.indexOf("区")+1);
		return addressLeft.concat("****");
	}
	
	
	
	/** 电子邮箱：只显示前缀第一个字母+域名，其他隐藏为星号
	 *  示例：xijian@ict.ac.cn -> x****@ict.ac.cn
	 */
	public static String email(String email) {
		if(StringUtils.isBlank(email)) {
			return "";
		}
		int idx = email.indexOf("@");
		if(idx<=1)
			return email;
		else {
			String emailLeft = StringUtils.left(email, 1);
			//substring(idx)：从idx到end
			return StringUtils.rightPad(emailLeft, idx, "*").concat(email.substring(idx)); 
		}
		
		
	}
	
	
	
	/** 银行卡号：只显示前6位和后4位，其他隐藏为星号
	 *  示例：621559 0286325378482 -> 621559 **********482
	 */
	public static String bankCard(String bankCardNum) {
		if(StringUtils.isBlank(bankCardNum)) {
			return "";
		}
		String bankLeft = StringUtils.left(bankCardNum, 6);
		String bankRight = StringUtils.right(bankCardNum, 4);
		return StringUtils.rightPad(bankLeft, bankCardNum.length()-4, "*").concat(bankRight);
	}
	
	
	/** 账户名：只显示前1个字母，其他隐藏为星号
	 *  示例：Jane -> J***
	 */
	public static String account(String account) {
		if(StringUtils.isBlank(account)) {
			return "";
		}
		String accountLeft = StringUtils.left(account, 1);
		return StringUtils.rightPad(accountLeft, account.length(), "*");
	}
	
	
	/** 账户密码：全部显示为星号
	 *  示例：123456 -> ******
	 */
	public static String password(String passwd) {
		if(StringUtils.isBlank(passwd)) {
			return "";
		}
		return StringUtils.rightPad("", passwd.length(), "*");
	}
}
