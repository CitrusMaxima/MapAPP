package com.baidumap;

import java.math.BigDecimal;

import android.graphics.*;

public class MainFrame {

	public double results = 0.0;

	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	//@SuppressLint("NewApi")
	public MainFrame(Bitmap sourceImage,Bitmap target) {
		///�Ҷ�ͼ��
		Bitmap targetImage=bitmapGray(target);		
		
		results = start(greyHistogram(sourceImage), greyHistogram(targetImage));
	}
	

	
	/**
	 * �Ҷȴ�����ȡ�Ҷ�ͼ��
	 * @param imagePath
	 * @return
	 */
	public static Bitmap bitmapGray(Bitmap bmSrc) {  
        // �õ�ͼƬ�ĳ��Ϳ�  
        int width = bmSrc.getWidth();  
        int height = bmSrc.getHeight();  
        // ����Ŀ��Ҷ�ͼ��  
        Bitmap bmpGray = null;  
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
        // ��������  
        Canvas c = new Canvas(bmpGray);  
        Paint paint = new Paint();  
        ColorMatrix cm = new ColorMatrix();  
        cm.setSaturation(0);  
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);  
        paint.setColorFilter(f);  
        c.drawBitmap(bmSrc, 0, 0, paint);  
        return bmpGray;  
    }  

	
	/**
	 * ��ȡ�Ҷ���������:����ÿ���Ҷ�ֵ�����ص�
	 * 
	 * @param bitmapImage
	 * @return
	 */
	public int[] greyHistogram(Bitmap bitmapImage){
		
		int width=bitmapImage.getWidth();
		int height=bitmapImage.getHeight();
		
		int featureArray[]=new int[256];
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				int color = bitmapImage.getPixel(i, j);//i��jΪbitmap����Ӧ��λ��
				int r = Color.red(color);
				int g = Color.green(color);
				int b = Color.blue(color);
				
				int rgb=((r*256)+g)*256+b;
				int grey=(rgb>>16)&0xFF;

				featureArray[grey]=featureArray[grey]+1;
				
			}
		}
		 return featureArray;
	}
	
	
	/**
	 * 
	 * @param source
	 * @param targets
	 */
	public double start(int[] source,int[]targets){

		double similarity=calSimilarity(source, targets);
		return similarity;

	}
	
	/**
	 * �Ƚ����ƶȣ����ü���ƽ��ֵ��С��
	 * @param source
	 * @param target
	 */
	public double calSimilarity(int[] source,int[] target){
		int length=source.length;
		double min=0,max=0;
		for(int i=0;i<length;i++){
			if(source[i]>target[i]){
				max=max+Math.sqrt(source[i]*target[i]);
				min=min+target[i];
			}else{
				max=max+Math.sqrt(source[i]*target[i]);
				min=min+source[i];
			}
		}
		
		return (double)min/max;

	}

	public static Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {  
        // ������Ҫ�ĳߴ羫ȷ����ѹ������, ������⣺public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode);  
        // scale��ʾҪ������С��λ, roundingMode��ʾ��δ�������С��λ��BigDecimal.ROUND_DOWN��ʾ�Զ�����  
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();  
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();  
        if (isAdjust) {// ������Զ�����������������ͼƬ������  
            sx = (sx < sy ? sx : sy);sy = sx;// �ĸ�����Сһ�㣬�����ĸ�����  
        }  
        Matrix matrix = new Matrix();  
        matrix.postScale(sx, sy);// ����api�еķ�������ѹ�����ʹ󹦸����  
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
    }
}
