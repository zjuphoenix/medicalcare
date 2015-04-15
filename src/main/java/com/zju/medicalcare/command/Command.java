package com.zju.medicalcare.command;

public class Command {
	/*
	 * ��ʼ�����ĵ��������
	 * ͨ�����֡ͷ��(���1byte,����2byte)��ͷ��(ģ������1byte,����2byte)�����(ͷ��(����1byte,����2byte,����λ2byte),���(��))
	 * ���Ϊ1��ʾ�ĵ�ģ��,ģ������Ϊ3��ʾģ������,�����ͷ���е�����2��ʾSTART
	 */
	public static final byte[] STARTECG = new byte[]{1,0,8,3,0,0,2,0,0,0,0};
	/*
	 * ֹͣ�����ĵ��������
	 * ͨ�����֡ͷ��(���1byte,����2byte)��ͷ��(ģ������1byte,����2byte)�����(ͷ��(����1byte,����2byte,����λ2byte),���(��))
	 * ���Ϊ1��ʾ�ĵ�ģ��,ģ������Ϊ3��ʾģ������,�����ͷ���е�����3��ʾSTOP
	 */
	public static final byte[] STOPECG = new byte[]{1,0,8,3,0,0,3,0,0,0,0};
	
	/*
	 * ��ʼ����Ѫѹ�������
	 * ͨ�����֡ͷ��(���1byte,����2byte)��ͷ��(ģ������1byte,����2byte)�����(ͷ��(����1byte,����2byte,����λ2byte),���(��))
	 * ���Ϊ3��ʾѪѹģ��,ģ������Ϊ3��ʾģ������,�����ͷ���е�����2��ʾSTART
	 */
	public static final byte[] STARTBloodPressure = new byte[]{3,0,8,3,0,0,2,0,0,0,0};
	/*
	 * ֹͣ����Ѫѹ�������
	 * ͨ�����֡ͷ��(���1byte,����2byte)��ͷ��(ģ������1byte,����2byte)�����(ͷ��(����1byte,����2byte,����λ2byte),���(��))
	 * ���Ϊ3��ʾѪѹģ��,ģ������Ϊ3��ʾģ������,�����ͷ���е�����3��ʾSTOP
	 */
	public static final byte[] STOPBloodPressure = new byte[]{3,0,8,3,0,0,3,0,0,0,0};
	
	/*
	 * ��ʼ����Ѫ���������
	 * ͨ�����֡ͷ��(���1byte,����2byte)��ͷ��(ģ������1byte,����2byte)�����(ͷ��(����1byte,����2byte,����λ2byte),���(��))
	 * ���Ϊ2��ʾѪ��ģ��,ģ������Ϊ3��ʾģ������,�����ͷ���е�����2��ʾSTART
	 */
	public static final byte[] STARTBloodOxygen = new byte[]{2,0,8,3,0,0,2,0,0,0,0};
	/*
	 * ֹͣ����Ѫ���������
	 * ͨ�����֡ͷ��(���1byte,����2byte)��ͷ��(ģ������1byte,����2byte)�����(ͷ��(����1byte,����2byte,����λ2byte),���(��))
	 * ���Ϊ2��ʾѪ��ģ��,ģ������Ϊ3��ʾģ������,�����ͷ���е�����3��ʾSTOP
	 */
	public static final byte[] STOPBloodOxygen = new byte[]{2,0,8,3,0,0,3,0,0,0,0};
	
	/*
	 * ����Э��������Ϣ����
	 * ͨ�����֡ͷ��(���1byte,����2byte),Э��汾1byte,����λ2byte
	 * ���Ϊ0��ʾЭ������,ģ������Ϊ3��ʾģ������,�����ͷ���е�����2��ʾSTART
	 */
	public static final byte[] PROTOCOLDESCRIPTION = new byte[]{0,0,3,0,0,0};
}
