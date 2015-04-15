package com.zju.medicalcare.model;

public class BloodOxygen {
	public int pulserate;//����
	public int saturation;//���Ͷ�
	public int pulseintensity;//����ǿ��
	public byte[] waveform = new byte[125];//�������
	public byte[] oxygenmark = new byte[125];//Ѫ����־
}
