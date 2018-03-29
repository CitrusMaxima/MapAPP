package com.baidumap;

public class OfflineMapCityBean
{
	private String cityName;
	private int cityCode;
	/**
	 * ���صĽ���
	 */
	private int progress;
	
	private Flag flag = Flag.NO_STATUS;
	/**
	 * ���ص�״̬����״̬����ͣ����������
	 * @author zhy
	 *
	 */
	public enum Flag
	{
		NO_STATUS,PAUSE,DOWNLOADING
	}

	public Flag getFlag()
	{
		return flag;
	}

	public void setFlag(Flag flag)
	{
		this.flag = flag;
	}

	public OfflineMapCityBean()
	{
	}

	public OfflineMapCityBean(String cityName, int cityCode, int progress)
	{
		this.cityName = cityName;
		this.cityCode = cityCode;
		this.progress = progress;
	}

	public String getCityName()
	{
		return cityName;
	}

	public void setCityName(String cityName)
	{
		this.cityName = cityName;
	}

	public int getCityCode()
	{
		return cityCode;
	}

	public void setCityCode(int cityCode)
	{
		this.cityCode = cityCode;
	}

	public int getProgress()
	{
		return progress;
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
	}

}
