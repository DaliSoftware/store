package com.dali.app.base.common;

public class Resource {
	/**
	 * ����˵�ַ
	 */
	public static final String urlService = "http://192.168.1.18:8080/quanminJieshang/";
	
	/**
	 * ���ỷ��
	 */
//	public static final String urlService = "http://192.168.1.109:8080/quanminJieshang/";
	public static final String urlUploadFile = urlService + "doc/upload";
	
	/**
	 * ����url
	 */
	public static final String urlLogin = urlService + "login";
	
	/**
	 * ��ȡ�ֻ���֤��url
	 */
	public static final String urlGetPhoneVerifyCode = urlService + "verifyCode/sendRegisterSecurityCode/";
	
	/**
	 * ��֤������֤��url
	 */
	public static final String urlCheckoutPhoneVerifyCode = urlService + "verifyCode/checkoutPhoneVerifyCode/";
	
	/**
	 * ��ȡͼƬ��֤��url
	 */
	public static final String urlUpdateImageVerify = urlService + "verifyCode/updateImageVerify?width=280&height=110";
	
	/**
	 * ��֤ͼƬ��֤����ȷ��url
	 */
	public static final String urlCheckoutImageVerify = urlService + "verifyCode/checkoutImageVerify/";
	
	/**
	 * ������ע���̻�����Ϣurl
	 */
	public static final String urlRegister = urlService +"user/saveNewAccount";
}
