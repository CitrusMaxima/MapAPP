package com.baidumap;

import android.graphics.*;

/**
 * ����ͼ��������
 * @author Administrator
 *
 */
public class Image_Utility {


	///soble����
	private static int [][]sobleX={{-1,0,1},
							{-2,0,2},
							{-1,0,1}};
	
	private static int [][]sobleY={{1,2,1},
							{0,0,0},
							{-1,-2,-1}};
	
	//���ղ�����
	///�ṹԪ��
	private static int sData[]={
			0,0,1,0,0,
			0,0,1,0,0,
			1,1,1,1,1,	
			0,0,1,0,0,
			0,0,1,0,0
	};
	
	private static int sX=5;///�ṹԪ�ص�����
	private static int sY=5;////�ṹԪ�ص�����
	
	
	
	
	
	/**
	 * ͼ��Ŀ����㣺 �ȸ�ʴ������
	 * @param sourceImage  �˴�����Ҷ�ͼ����߶�ֵͼ��
	 * @param shreshold :��ֵ�������������ͽ��С����ֵʱ����Ȼ����ͼ��λ�õ�ֵΪ0�������и�ʴ����ʱ��
	 * 					 ���Ҷ�ֵ���ڵ�����ֵ��С����ֵ��ʱ���ҽṹԪ��Ϊ1��0��ʱ������Ϊ��Ӧλ��ƥ���ϣ�
	 * 					���Ϊ��ֵͼ����Ӧ�ô���1��
	 * @return
	 */
	public static int[][] open(int [][]source,int threshold){
		
		int width=source[0].length;
		int height=source.length;
		
		int[][] result=new int[height][width];

		///�ȸ�ʴ����
		result=correde(source, threshold);
		//����������
		result=dilate(result, threshold);
			
		return result;
	}
	
	
	/**
	 * ���ø�ʴ������б�Ե��ȡ��������
	 * @param source
	 * @param threshold
	 * @return
	 */
	public static int[][] edgeExtract(int [][]source,int threshold){

		int width=source[0].length;
		int height=source.length;
		
		int[][] result=new int[height][width];
		int[][] result1=new int[height][width];
		///�ȸ�ʴ����
		result=correde(source, threshold);
		//����������
		result1=dilate(source, threshold);
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				int temp=Math.abs(result1[j][i]-result[j][i]);	
				if(temp>=100){
					result[j][i]=temp;
				}else{
					result[j][i]=0;
				}
			}
			
		}	///��ȡ��Ե

		return result;
	}
	
	
	
	
	
	
	/**
	 * ��ʴ����
	 * @param source
	 * @param shreshold ���Ҷ�ֵ������ֵ��С����ֵ��ʱ���ҽṹԪ��Ϊ1��0��ʱ������Ϊ��Ӧλ��ƥ���ϣ�
	 * @return
	 */
	public static int[][] correde(int[][] source,int threshold){
		int width=source[0].length;
		int height=source.length;
		
		int[][] result=new int[height][width];
		
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				int tempx=sX/2;
				int tempy=sY/2;
				///��Ե�����в�������Ե�ڲŲ���
				if(i>=tempy&&j>=tempx&&i<height-tempy&&j<width-tempx){
					int max =0;
					
					///�ԽṹԪ�ؽ��б���
					for(int k=0;k<sData.length;k++){
						int x=k%sX;///�̱�ʾxƫ����
						int y=k/sX;///������ʾyƫ����
						
						
						if(sData[k]!=0){
							///��Ϊ0ʱ������ȫ��������ֵ�����������Ϊ0����������
							if(source[i-tempy+y][j-tempx+x]>=threshold){
								if(source[i-tempy+y][j-tempx+x]>max){
									max=source[i-tempy+y][j-tempx+x];
								}
							}else{
								////��ṹԪ�ز�ƥ��,��ֵ0,��������
								max=0;
								break;
							}
						}
					}
					
					////�˴�����������ֵ����maxС����ֵ��ʱ��͸�Ϊ0
					result[i][j]=max;
					
				}else{
					///ֱ�Ӹ�ֵ
					result[i][j]=source[i][j];
					
				}///end of the most out if-else clause .				
				
			}
		}///end of outer for clause
		
		return result;
	}
	
	
	/**
	 * ��������
	 * @param source
	 * @param threshold  ����������ֵС����ֵʱ��ͼ����ֵ��Ȼ��Ϊ0
	 * @return
	 */
	public static int[][] dilate(int[][] source,int threshold){
		int width=source[0].length;
		int height=source.length;
		
		int[][] result=new int[height][width];
		
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				
				int tempx=sX/2;
				int tempy=sY/2;
				///��Ե�����в�������Ե�ڲŲ���
				if(i>=tempy&&j>=tempx&&i<height-tempy&&j<width-tempx){
					int max =0;
					
					///�ԽṹԪ�ؽ��б���
					for(int k=0;k<sData.length;k++){
						int y=k/sX;///�̱�ʾxƫ����
						int x=k%sX;///������ʾyƫ����
						
						if(sData[k]!=0){
							///���ṹԪ���в�Ϊ0ʱ,ȡ��ͼ���ж�Ӧ��������ֵ����ͼ��ǰλ����Ϊ�Ҷ�ֵ
							if(source[i-tempy+y][j-tempx+x]>max){
								max=source[i-tempy+y][j-tempx+x];
							}
						}
					}
					

					result[i][j]=max;
					
				}else{
					///ֱ�Ӹ�ֵ
					result[i][j]=source[i][j];
				}				
				
			}
		}
		
		return result;
	}
	
	
	
	
	/**
	 * ͼ��soble�����ݶȻ������Ҷ�ͼ��ȡ
	 * @param sourceImage
	 * @param threshold
	 * @return
	 */
	public static Bitmap sobleTran(Bitmap sourceImage,int threshold){
		int width=sourceImage.getWidth();
		int height=sourceImage.getHeight();
		Bitmap targetImage=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				
				int rgb=0;	
				int color = 0;
				int r,g,b;
				if(i>0&&j>0&&j<height-1&i<width-1){
					
					color = sourceImage.getPixel(i-1, j-1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB0=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i-1, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB1=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i-1, j+1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB2=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i, j-1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB3=((r*256)+g)*256+b>>16;
					
					color = sourceImage.getPixel(i, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB4=((r*256)+g)*256+b>>16;
					
					color = sourceImage.getPixel(i, j+1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB5=((r*256)+g)*256+b>>16; 
					
					color = sourceImage.getPixel(i+1, j-1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB6=((r*256)+g)*256+b>>16;
					
					color = sourceImage.getPixel(i+1, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB7=((r*256)+g)*256+b>>16;
					
					color = sourceImage.getPixel(i+1, j+1);
					int grayRGB8=((r*256)+g)*256+b>>16;

				
					///soble���ӻ�ȡ�ݶ�
					int result=0;
					int dx=sobleX[0][0]*grayRGB0+sobleX[0][1]*grayRGB1+sobleX[0][2]*grayRGB2
							+sobleX[1][0]*grayRGB3+sobleX[1][1]*grayRGB4+sobleX[1][2]*grayRGB5
							+sobleX[2][0]*grayRGB6+sobleX[2][1]*grayRGB7+sobleX[2][2]*grayRGB8;
					int dy=sobleY[0][0]*grayRGB0+sobleY[0][1]*grayRGB1+sobleY[0][2]*grayRGB2
							+sobleY[1][0]*grayRGB3+sobleY[1][1]*grayRGB4+sobleY[1][2]*grayRGB5
							+sobleY[2][0]*grayRGB6+sobleY[2][1]*grayRGB7+sobleY[2][2]*grayRGB8;
					result=(int) Math.sqrt(dx*dx+dy*dy);

					if(result<=threshold){
						///�˴���ֵ��Ϊ??ʵ��Ч�����
						rgb=0;
						//System.out.print(0);
					}else{
						int grayRGB=result;
						rgb=(grayRGB<<16)|(grayRGB<<8)|grayRGB;
					}
					int grayRGB=result;
					rgb=(grayRGB<<16)|(grayRGB<<8)|grayRGB;
				}else{
					color = sourceImage.getPixel(i, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					rgb=((r*256)+g)*256+b;
				}
				targetImage.setPixel(i, j, rgb);
				
			}
		}
		
		
		return targetImage;
		
	}
	
	

	/**
	 * ��x����y����ȡ�ݶȼ���ֵ��Ϊ�Ҷ�ֵ
	 * @param sourceImage
	 * @param threshold
	 * @return
	 */
	public static Bitmap xyTran(Bitmap sourceImage,int threshold){
		int width=sourceImage.getWidth();
		int height=sourceImage.getHeight();
		Bitmap targetImage=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				
				int rgb=0;	
				int color = 0;
				int r,g,b;
				if(i>0&&j>0&&j<height-1&i<width-1){
					
					color = sourceImage.getPixel(i, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB0=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i+1, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB1=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i, j+1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB2=((r*256)+g)*256+b>>16;

					int xd=Math.abs(grayRGB0-grayRGB1);
					int yd=Math.abs(grayRGB2-grayRGB0);
					int result=xd>yd?xd:yd;///�ݶȴ���
				
					
					if(result<=threshold){
						///�˴���ֵ��Ϊ??ʵ��Ч�����
						result=0;
						//System.out.print(0);
					}
					
					int grayRGB=result;
					rgb=(grayRGB<<16)|(grayRGB<<8)|grayRGB;
					
				}else{
					color = sourceImage.getPixel(i, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					rgb=((r*256)+g)*256+b;
				}
				
				targetImage.setPixel(i, j, rgb);
				
			}
		}
		
		
		return targetImage;
		
	}
	
	
	/**
	 * ��˹�˲���ȫ��ƽ��
	 * @param sourceImage
	 * @return
	 */
	public static Bitmap guassFilter(Bitmap sourceImage){
		int width=sourceImage.getWidth();
		int height=sourceImage.getHeight();
		Bitmap targetImage=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				
				int rgb=0;		
				int color = 0;
				int r,g,b;
				if(i>0&&j>0&&j<height-1&i<width-1){
					
					color = sourceImage.getPixel(i, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB0=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i+1, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB1=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i-1, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB2=((r*256)+g)*256+b>>16;
				
					color = sourceImage.getPixel(i, j-1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB3=((r*256)+g)*256+b>>16;
					
					color = sourceImage.getPixel(i, j+1);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					int grayRGB4=((r*256)+g)*256+b>>16;

					int grayRGB=Math.abs(4*grayRGB0+grayRGB1+grayRGB2+grayRGB3+grayRGB4)/8;
					rgb=(grayRGB<<16)|(grayRGB<<8)|grayRGB;
					
				}else{
					color = sourceImage.getPixel(i, j);
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);
					rgb=((r*256)+g)*256+b;
				}
				
				targetImage.setPixel(i, j, rgb);
				
			}
		}
		
		
		return targetImage;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * �Ҷ�ͼ����ȡ����
	 * @param image
	 * @return int[][]����
	 */
	public static int[][] imageToArray(Bitmap image){
		
		int width=image.getWidth();
		int height=image.getHeight();
		int color = 0;
		int r,g,b;
		
		int[][] result=new int[height][width];
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				color = image.getPixel(i, j);
				r = Color.red(color);
				g = Color.green(color);
				b = Color.blue(color);
				int rgb=((r*256)+g)*256+b;
				int grey=(rgb>>16)&0xFF;
//				System.out.println(grey);
				result[j][i]=grey;
				
			}
		}
		 return result ;
	}
	
	/**
	 * �Ҷ�ͼ����ȡ����
	 * @param image
	 * @return int[][]����
	 */
	public static double[][] imageToDoubleArray(Bitmap image){
		
		int width=image.getWidth();
		int height=image.getHeight();
		int color = 0;
		int r,g,b;
		
		double[][] result=new double[height][width];
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				color = image.getPixel(i, j);
				r = Color.red(color);
				g = Color.green(color);
				b = Color.blue(color);
				int rgb=((r*256)+g)*256+b;
				int grey=(rgb>>16)&0xFF;
//				System.out.println(grey);
				result[j][i]=grey;
				
			}
		}
		 return result ;
	}
	
	
	
	/**
	 * ����תΪ�Ҷ�ͼ��
	 * @param sourceArray
	 * @return
	 */
	public static Bitmap arrayToGreyImage(int[][] sourceArray){
		int width=sourceArray[0].length;
		int height=sourceArray.length;
		Bitmap targetImage=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				int greyRGB=sourceArray[j][i];
				int rgb=(greyRGB<<16)|(greyRGB<<8)|greyRGB;
				
				targetImage.setPixel(i, j, rgb);
			}
		}	
		
		return targetImage;
	}
	

	/**
	 * ����תΪ�Ҷ�ͼ��
	 * @param sourceArray
	 * @return
	 */
	public static Bitmap doubleArrayToGreyImage(double[][] sourceArray){
		int width=sourceArray[0].length;
		int height=sourceArray.length;
		Bitmap targetImage=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				int greyRGB=(int) sourceArray[j][i];
				int rgb=(greyRGB<<16)|(greyRGB<<8)|greyRGB;
				
				targetImage.setPixel(i, j, rgb);
			}
		}	
		
		return targetImage;
	}
	
	
}
