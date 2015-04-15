package com.zju.medicalcare.command;

public class CommandResult {
	public byte command;//����
	public char length;//����
	public byte response;//��Ӧ���룬���ط�0Ϊ�������
	public byte reservedbit;//����λ
	public byte[] data;//������Ӧ��ݲ���
}
