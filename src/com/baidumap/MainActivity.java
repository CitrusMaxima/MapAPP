package com.baidumap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidumap.MyOrientationListener.OnOrientationListener;
import com.baidumap.ImageTransform;
import com.baidumap.Image_Utility;
import com.baidumap.MyPoint;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Context context;
	
	public static int N = 58;	//��Ƭ����
	double similarities[] = new double[N];	//�洢���ƶ�
	
	//��λ���
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	public BDLocationListener myListener = new MyLocationListener();
	private boolean isFirstIn = true;
	private double mLatitude;
	private double mLongitude;
	//�Զ��嶨λͼ��
	private BitmapDescriptor mIconLocation;
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	    mLocationClient.registerLocationListener( myListener );    //ע���������
		
	    this.context = this;
	    
		initView();
		
		//��ʼ����λ
		initLocation();
	}

	private void initLocation() {
		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);
		
		LocationClientOption option = new LocationClientOption();
		
        option.setLocationMode(LocationMode.Hight_Accuracy);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
        option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
        int span=1000;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
        option.setIgnoreKillProcess(false);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��  
        option.SetIgnoreCacheException(false);//��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
        option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
        
        mLocationClient.setLocOption(option);
        //��ʼ����λͼ��
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.arrow0);
        
        myOrientationListener = new MyOrientationListener(context);
        
        myOrientationListener.setOnOrientationListener(new OnOrientationListener(){
        	public void onOrientationChanged(float x) {
				mCurrentX = x;
			}
        });
	}

	private void initView() {
		mMapView = (MapView)findViewById(R.id.id_bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
    }  
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//������λ
    	mBaiduMap.setMyLocationEnabled(true);
    	if (!mLocationClient.isStarted())
    		mLocationClient.start();
    	//�������򴫸���
    	myOrientationListener.start();
    }
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }  
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
    	//ֹͣ��λ
    	mBaiduMap.setMyLocationEnabled(false);
    	mLocationClient.stop();
    	//ֹͣ���򴫸���
    	myOrientationListener.stop();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.id_map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_map_site:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_map_traffic:
			if (mBaiduMap.isTrafficEnabled()){
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("ʵʱ��ͨ(off)");
			}else{
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("ʵʱ��ͨ(on)");
			}
			break;
		case R.id.id_map_location:
			centerToMyLocation();
			break;
		case R.id.id_map_camera:
			myCamera();
			break;
		case R.id.id_map_getlocation:
			try {
				getLocation();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.id_map_offline:
			Intent intent = new Intent(MainActivity.this,
					OfflineMapActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void centerToMyLocation() {
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}
	
	//�������
	private void myCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//����android�Դ�������� 
		File file = new File(Environment.getExternalStorageDirectory()+"/000.jpg");
		Uri uri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, 1);
	}
	
	//����ͼƬ���ƶ�,����λ
	private void getLocation() throws IOException { 
		mBaiduMap.clear();
		
		double max = 0;
		int address = 0;
		double output1 = 0.0;
		double output2 = 0.0;
		BitmapDescriptor mMarker = BitmapDescriptorFactory.fromResource(R.drawable.location0);
		OverlayOptions options;
		
		Bitmap source = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/000.jpg");
		if (source == null)
		{
			Toast.makeText(context, "����������Ƭ��", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        source.compress(Bitmap.CompressFormat.JPEG, 20, baos);//����ѹ����������ѹ��������ݴ�ŵ�baos��    
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
        Bitmap sourceImage = MainFrame.bitmapGray(MainFrame.reduce(bitmap, 156, 208, true));
        //����Ƭ���и�˹ģ��
        HashMap<Integer,double[][]> result=ImageTransform.getGaussPyramid(Image_Utility.imageToDoubleArray(sourceImage), 20, 3, 1.6);
        HashMap<Integer,double[][]> dog=ImageTransform.gaussToDog(result, 6);
		HashMap<Integer,List<MyPoint>> keyPoints=ImageTransform.getRoughKeyPoint(dog,6);
		keyPoints=ImageTransform.filterPoints(dog, keyPoints, 10,0.03);
		sourceImage=ImageTransform.drawPoints(result,keyPoints);
        
        Bitmap image = null;  
        AssetManager am = getResources().getAssets();
        
		for (int i = 0; i < N; ++i){ 
				String imgname = "img_" + i +".jpg";
	        	InputStream is = am.open(imgname); 
	            image = BitmapFactory.decodeStream(is);  
	            is.close();  
	            MainFrame mainFrame = new MainFrame(sourceImage, image);
				similarities[i] = mainFrame.results;
		}

		for (int i=0; i<N; i++)
			if (similarities[i] > max)
			{
				address = i;
				max = similarities[i];
			}
		
		CopyAssets(Environment.getExternalStorageDirectory().toString(), "img_" + address +".jpg");
        ExifInterface exifInterface = new ExifInterface(Environment.getExternalStorageDirectory() + "/img_" + address +".jpg");
        
        String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String lngValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String lngRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        
        if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
                output1 = convertRationalLatLonToFloat(latValue, latRef);
                output2 = convertRationalLatLonToFloat(lngValue, lngRef);
        } 

        LatLng latLng = new LatLng(output1+0.003, output2+0.009);	// ��ΪУ׼��γ�ȣ������д�����
        
    	MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
    	mBaiduMap.animateMapStatus(msu);
    	
    	options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
    	@SuppressWarnings("unused")
		Marker marker = (Marker) mBaiduMap.addOverlay(options);
	}

	private class MyLocationListener implements BDLocationListener {
		
		public void onReceiveLocation(BDLocation location) {
			MyLocationData data = new MyLocationData.Builder()
			.direction(mCurrentX)//
			.accuracy(location.getRadius())//
			.latitude(location.getLatitude())//
			.longitude(location.getLongitude())//
			.build();
			
			mBaiduMap.setMyLocationData(data);
			//�����Զ���ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration
					(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);
			
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
			
			if (isFirstIn){
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
				
				//��ʾ��λλ��
				Toast.makeText(context, location.getAddrStr(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private static double convertRationalLatLonToFloat(String rationalString, String ref) {
		
        String[] parts = rationalString.split(",");

        String[] pair;
        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        pair = parts[2].split("/");
        double seconds = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());
        
        double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
        if ((ref.equals("S") || ref.equals("W"))) {
            return -result;
        }
        return result;
    }
	
	private void CopyAssets(String dir, String fileName){    
        File mWorkingPath = new File(dir);  
        if (!mWorkingPath.exists()) {  
            if (!mWorkingPath.mkdirs()) {  
                Log.e("--CopyAssets--", "cannot create directory.");  
            }  
        }  
        try {  
            InputStream in = this.getResources().getAssets().open(fileName);  
            System.err.println("");  
            File outFile = new File(mWorkingPath, fileName);  
            OutputStream out = new FileOutputStream(outFile);  
            // Transfer bytes from in to out  
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = in.read(buf)) > 0) {  
                out.write(buf, 0, len);  
            }  
            in.close();  
            out.close();  
        } catch (IOException e1) {  
            e1.printStackTrace();  
        }  
    } 
}
