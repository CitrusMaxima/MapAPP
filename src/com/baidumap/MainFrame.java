package com.baidumap;

import java.math.BigDecimal;

import android.graphics.*;

public class MainFrame {

	public double results = 0.0;

	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	//@SuppressLint("NewApi")
	public MainFrame(Bitmap sourceImage,Bitmap target) {
		///灰度图像
		Bitmap targetImage=bitmapGray(target);		
		
		results = start(greyHistogram(sourceImage), greyHistogram(targetImage));
	}
	

	
	/**
	 * 灰度处理，获取灰度图像
	 * @param imagePath
	 * @return
	 */
	public static Bitmap bitmapGray(Bitmap bmSrc) {  
        // 得到图片的长和宽  
        int width = bmSrc.getWidth();  
        int height = bmSrc.getHeight();  
        // 创建目标灰度图像  
        Bitmap bmpGray = null;  
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
        // 创建画布  
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
	 * 获取灰度特征向量:计算每个灰度值的像素点
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
				int color = bitmapImage.getPixel(i, j);//i、j为bitmap所对应的位置
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
	 * 比较相似度，采用几何平均值最小法
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
        // 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode);  
        // scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃  
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();  
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();  
        if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸  
            sx = (sx < sy ? sx : sy);sy = sx;// 哪个比例小一点，就用哪个比例  
        }  
        Matrix matrix = new Matrix();  
        matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了  
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
    }
}
