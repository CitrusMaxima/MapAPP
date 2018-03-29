package com.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
//import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidumap.OfflineMapCityBean.Flag;

public class OfflineMapActivity extends Activity
{

	protected static final String TAG = "OfflineMapActivity";
	/**
	 * ���ߵ�ͼ����
	 */
	private MKOfflineMap mOfflineMap;
	private ListView mListView;
	/**
	 * ���ߵ�ͼ������
	 */
	private List<OfflineMapCityBean> mDatas = new ArrayList<OfflineMapCityBean>();
	/**
	 * ������
	 */
	private MyOfflineCityBeanAdapter mAdapter;
	private LayoutInflater mInflater;
	//private Context context;
	/**
	 * Ŀǰ�������ض��еĳ���
	 */
	private List<Integer> mCityCodes = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlinemap);

		//context = this;
		mInflater = LayoutInflater.from(this);
		/**
		 * ��ʼ�����ߵ�ͼ
		 */
		initOfflineMap();
		/**
		 * ��ʼ��ListView����
		 */
		initData();
		/**
		 * ��ʼ��ListView
		 */
		initListView();

	}

	private void initListView()
	{
		mListView = (ListView) findViewById(R.id.id_offline_map_lv);
		mAdapter = new MyOfflineCityBeanAdapter();
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				int cityId = mDatas.get(position).getCityCode();
				if (mCityCodes.contains(cityId))
				{
					removeTaskFromQueue(position, cityId);
				} else
				{
					addToDownloadQueue(position, cityId);
				}

			}
		});
	}

	/**
	 * �������Ƴ����ض���
	 * 
	 * @param pos
	 * @param cityId
	 */
	public void removeTaskFromQueue(int pos, int cityId)
	{
		mOfflineMap.pause(cityId);
		mDatas.get(pos).setFlag(Flag.NO_STATUS);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * ������������������ض���
	 * 
	 * @param pos
	 * @param cityId
	 */
	public void addToDownloadQueue(int pos, int cityId)
	{
		mCityCodes.add(cityId);
		mOfflineMap.start(cityId);
		mDatas.get(pos).setFlag(Flag.PAUSE);
		mAdapter.notifyDataSetChanged();
	}

	private void initData()
	{

		// ����������ų���
		ArrayList<MKOLSearchRecord> offlineCityList = mOfflineMap
				.getHotCityList();

		// ��������Ѿ����صĳ����б�
		ArrayList<MKOLUpdateElement> allUpdateInfo = mOfflineMap
				.getAllUpdateInfo();
		// �����������ݵ�״̬
		for (MKOLSearchRecord record : offlineCityList)
		{
			OfflineMapCityBean cityBean = new OfflineMapCityBean();
			cityBean.setCityName(record.cityName);
			cityBean.setCityCode(record.cityID);

			if (allUpdateInfo != null)//û���κ����ؼ�¼������null,Ϊɶ�����ؿ��б�~~
			{
				for (MKOLUpdateElement ele : allUpdateInfo)
				{
					if (ele.cityID == record.cityID)
					{
						cityBean.setProgress(ele.ratio);
					}
				}

			}
			mDatas.add(cityBean);
		}

	}

	/**
	 * ��ʼ�����ߵ�ͼ
	 */
	private void initOfflineMap()
	{
		mOfflineMap = new MKOfflineMap();
		// ���ü���
		mOfflineMap.init(new MKOfflineMapListener()
		{
			@Override
			public void onGetOfflineMapState(int type, int state)
			{
				switch (type)
				{
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
					// ���ߵ�ͼ���ظ����¼�����
					MKOLUpdateElement update = mOfflineMap.getUpdateInfo(state);
					Log.e(TAG, update.cityName + " ," + update.ratio);
					for (OfflineMapCityBean bean : mDatas)
					{
						if (bean.getCityCode() == state)
						{
							bean.setProgress(update.ratio);
							bean.setFlag(Flag.DOWNLOADING);
							break;
						}
					}
					mAdapter.notifyDataSetChanged();
					Log.e(TAG, "TYPE_DOWNLOAD_UPDATE");
					break;
				case MKOfflineMap.TYPE_NEW_OFFLINE:
					// �������ߵ�ͼ��װ
					Log.e(TAG, "TYPE_NEW_OFFLINE");
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					// �汾������ʾ
					break;
				}

			}
		});
	}

	@Override
	protected void onDestroy()
	{
		mOfflineMap.destroy();
		super.onDestroy();
	}

	/**
	 * ���ų��е�ͼ�б��Adapter
	 * 
	 * @author zhy
	 * 
	 */
	class MyOfflineCityBeanAdapter extends BaseAdapter
	{

		@Override
		public boolean isEnabled(int position)
		{
			if (mDatas.get(position).getProgress() == 100)
			{
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public int getCount()
		{
			return mDatas.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			OfflineMapCityBean bean = mDatas.get(position);
			ViewHolder holder = null;
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.offlinemap_item,
						parent, false);
				holder.cityName = (TextView) convertView
						.findViewById(R.id.id_cityname);
				holder.progress = (TextView) convertView
						.findViewById(R.id.id_progress);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.cityName.setText(bean.getCityName());
			int progress = bean.getProgress();
			String progressMsg = "";
			// ���ݽ��������������ʾ
			if (progress == 0)
			{
				progressMsg = "δ����";
			} else if (progress == 100)
			{
				bean.setFlag(Flag.NO_STATUS);
				progressMsg = "������";
			} else
			{
				progressMsg = progress + "%";
			}
			// ���ݵ�ǰ״̬��������ʾ
			switch (bean.getFlag())
			{
			case PAUSE:
				progressMsg += "���ȴ����ء�";
				break;
			case DOWNLOADING:
				progressMsg += "���������ء�";
				break;
			default:
				break;
			}
			holder.progress.setText(progressMsg);
			return convertView;
		}

		private class ViewHolder
		{
			TextView cityName;
			TextView progress;

		}
	}
}
