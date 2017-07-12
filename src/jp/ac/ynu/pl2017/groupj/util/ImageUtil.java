package jp.ac.ynu.pl2017.groupj.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;




public class ImageUtil {
	
	private static Image haikuimage=null;
	private static Image haikuimageWithStr=null;
	///*
		public static void main(String[] args) {
			// TODO 自動生成されたメソッド・スタブ
			BufferedImage backimage=null;
			BufferedImage compimage=null;
			BufferedImage compimage2=null;
			BufferedImage wctest=null;
			

			try {
				//backimage = ImageIO.read(new File("fallbackground.jpg"));
				backimage =ImageIO.read(new File("spring-mask.png"));
			    compimage2=ImageIO.read(new File("ColorCat.jpg"));//ここに画像パス
			    compimage=ImageIO.read(new File("ColorFish.jpg"));//ここに画像パス
			    wctest=ImageIO.read(new File("spring_wordcloud.png"));
			} catch (Exception e) {
			    e.printStackTrace();
			    backimage = null;
			    compimage = null;
			}
			JFrame frame=new JFrame();
			BufferedImage synimg;
			BufferedImage newbackimage=null;
			Image[] fximg={SwingFXUtils.toFXImage(compimage, null),SwingFXUtils.toFXImage(compimage2, null)};
			Image wcView=SwingFXUtils.toFXImage(wctest,null);
			Image backimagefx=SwingFXUtils.toFXImage(backimage,null);
			java.awt.Image wcs=WCImagemodify(wcView,backimagefx);
			String[] Haiku={"古池や","蛙飛び込む","水の音"};
			Image Haikuimg=null;
			createHaikuImage(150,backimagefx,fximg,Haiku);
			//synimg=SwingFXUtils.fromFXImage(gethaikuimgWithstr(), null);//俳句付き画像
			synimg=SwingFXUtils.fromFXImage(getHaikuimg(), null); //俳句なし画像表示テスト用
			
			JLabel icon=new JLabel(new ImageIcon((java.awt.Image)synimg));
			//JLabel icon=new JLabel(new ImageIcon(wcs));



			frame.add(icon);
			frame.setVisible(true);
			frame.setSize(synimg.getWidth(null)+100,synimg.getHeight(null)+100 );
			icon.setBounds(20, 20, synimg.getWidth(null),synimg.getHeight(null) );
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		//*/

		private static BufferedImage getAlphaImage(BufferedImage img,BufferedImage backimg) {
			if (img == null|| backimg==null) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			//ピクセル値取得
			int[] pixel = new int[width*height];
			PixelGrabber pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}
			
			int backwidth = backimg.getWidth(null);
			int backheight = backimg.getHeight(null);
			//ピクセル値取得
			int[] backpixel = new int[backwidth*backheight];
			PixelGrabber bpg = new PixelGrabber(backimg,0,0,backwidth,backheight,backpixel,0,backwidth);
			try {
				bpg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}
			//透明化
			//int color = alpha.getRGB();
			for(int i=0;i<width*height;i++){
				if (pixel[i]==backpixel[i]) {
					pixel[i] = pixel[i]& 0xAFFFFFFF;
				}
			}
			
			//イメージに戻す
			java.awt.Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return createBufferedImage(alphaImage);
		}

		private static BufferedImage getBWImage(BufferedImage img){ //完全に白黒化
			if(img==null) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			int[] pixel = new int[width*height];
			PixelGrabber pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}

			int blackRGB=Color.BLACK.getRGB();
			int whiteRGB=Color.WHITE.getRGB();

			for(int i=0;i<width*height;i++){
				if (pixel[i] != blackRGB && pixel[i] != whiteRGB) {
					int Bdistance=(int)Math.sqrt(Math.pow((double)r(pixel[i]),2.0)
							+Math.pow((double)g(pixel[i]),2.0)+Math.pow((double)b(pixel[i]),2.0));
					int Wdistance=(int)Math.sqrt(Math.pow((double)(255-r(pixel[i])),2.0)
							+Math.pow((double)(255-g(pixel[i])),2.0)+Math.pow((double)(255-b(pixel[i])),2.0));
					if(Bdistance<=Wdistance){
						pixel[i]=blackRGB;
					}else{
						pixel[i]=whiteRGB;
					}
				}
			}
			java.awt.Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return createBufferedImage(alphaImage);

		}

		private static BufferedImage magnifyScaleImage(BufferedImage img, int scale) { //scale:任意のスケール
			 int width = img.getWidth(null);
			 int height = img.getHeight(null);
			 java.awt.Image scaledImage = img.getScaledInstance(
			 width * scale/100, height*scale/100, java.awt.Image.SCALE_DEFAULT);
			 return createBufferedImage(scaledImage);
			 }

		private static BufferedImage magnifySizeImage(BufferedImage img, int width,int height) {//width,height:任意のサイズ
			 //int width = img.getWidth(null);
			 //int height = img.getHeight(null);
			java.awt.Image scaledImage = img.getScaledInstance(
			 width, height, java.awt.Image.SCALE_DEFAULT);
			
			 return createBufferedImage(scaledImage);
			 }

		private static BufferedImage synthesizeImage(java.awt.Image back,java.awt.Image comp,int x,int y){
			//画像合成メソッド backは背景,compは合成画像,x,yは合成始点座標
			if(back==null||comp==null) return null;
			int width = back.getWidth(null);
			int height = back.getHeight(null);
			BufferedImage synthesizedimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = synthesizedimg.getGraphics();
			g.drawImage(back, 0, 0, null);
			g.drawImage(comp, x, y, null);
			return synthesizedimg;
		}

		private static BufferedImage AlphaChange(BufferedImage img,int alpha){
			if(img==null) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			//ピクセル値取得
			int[] pixel = new int[width*height];
			PixelGrabber pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}
			//半透明化
			//int color = alpha;
			for(int i=0;i<width*height;i++){
					//pixel[i]=pixel[i] | 0xF0000000;
					pixel[i] = pixel[i] & 0xAFFFFFFF;
			}
			java.awt.Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return createBufferedImage(alphaImage);
		}

		private static java.awt.Image CircleImage(BufferedImage img){
			if(img==null) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			//if(width<x || height<y) return img;
			BufferedImage Bcircle=null;

			//ピクセル値取得
			int[] pixel = new int[width*height];
			PixelGrabber pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}

			//円画像のピクセル値取得
			try {
				Bcircle= ImageIO.read(new File("res\\image\\circle.png"));
			} catch (Exception e) {
			    e.printStackTrace();
			    return null;
			}
			//Image c=(Image)Bcircle;
			Bcircle=magnifySizeImage(Bcircle,width,height);
			java.awt.Image c=(java.awt.Image)Bcircle;
			int[] circlepixel = new int[width*height];
			PixelGrabber cpg = new PixelGrabber(c,0,0,width,height,circlepixel,0,width);
			try {
				cpg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}

			for(int i=0;i<width*height;i++){
				if(circlepixel[i]==-1){
					pixel[i]=pixel[i]& 0x00FFFFFF;
				}
			}
			java.awt.Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return alphaImage;


			}

		private static BufferedImage DrawString(BufferedImage img,String[] str){
			if(img==null || str.length!=3 ) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			//BufferedImage newimage=(BufferedImage)img;
			BufferedImage newimage=img;
			Graphics graphics = newimage.createGraphics();
			
			int Maxlen=0;
			for(int i=0;i<str.length-1;i++){
				Maxlen=Math.max(str[i].length(),str[i+1].length());
			}
			int fontsize=450/Maxlen-10;
			//文字合成
			graphics.setColor(Color.BLACK);
			Font font = new Font("ＭＳ 明朝", Font.BOLD,fontsize);
			for(int len=0;len<str.length;len++){
			for(int i=0;i<str[len].length();i++){
				graphics.setFont(font);
				graphics.drawString(String.valueOf(str[len].charAt(i)),300+len*(-110),100+fontsize*i);
				}
			}
			return newimage;

			}

		private static BufferedImage getgrayImage(BufferedImage img){
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			BufferedImageOp op = new ColorConvertOp(cs,null);
			//BufferedImage grayimage=(BufferedImage)img;
			BufferedImage grayimage=img;
			int imageType=grayimage.getType();
			BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),img.getHeight(null),imageType);
			op.filter(grayimage,bufferedImage);
			return bufferedImage;
		}

		private static BufferedImage createBufferedImage(java.awt.Image img) {
			BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

			Graphics g = bimg.getGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();

			return bimg;
		}
		
		public static void createHaikuImage(int compsize,Image backimage,Image[] img,String[] str){
			//2種の俳句画像を生成、compsizeは合成素材画像サイズ,backimageは背景画像,strは俳句をいれる
			if(backimage==null || img.length==0 || str.length!=3) return;
			BufferedImage newbackimage=null;
			resetHaikuImage();
			
			newbackimage=SwingFXUtils.fromFXImage(backimage,null);
			newbackimage=magnifySizeImage(newbackimage,450,450);//背景画像加工 //450*450
			//newbackimage=getgrayImage(newbackimage);
			BufferedImage synimg=newbackimage;
			//BufferedImage synimg=bufferImage;
			Random rnd = new Random(getHaikukey(str));
			
			for(int i=0;i<img.length;i++){
				BufferedImage bimg=SwingFXUtils.fromFXImage(img[i], null);
				bimg=magnifySizeImage(bimg,compsize,compsize);//コンポーネント画像加工
				bimg=getgrayImage(bimg);
				java.awt.Image afterimg=CircleImage(bimg);
			
				int x=rnd.nextInt(450-compsize); //座標のランダム生成
				int y=rnd.nextInt(450-compsize);
			
				synimg=synthesizeImage(synimg,afterimg,x,y); //合成
				}
			//synimg=getAlphaImage(synimg,newbackimage);
			//synimg=AlphaChange(newbackimage,0);
			//Image Haikuimg=SwingFXUtils.toFXImage(synimg, null);
			haikuimage=SwingFXUtils.toFXImage(synimg, null);
			synimg=DrawString(synimg,str);
			haikuimageWithStr=SwingFXUtils.toFXImage(synimg, null);
			//return Haikuimg;
			
			
			
		}
		
		private static int getHaikukey(String[] str){
			int key=0;
			for(int i=0;i<str.length;i++){
				for(int j=0;j<str[i].length();j++){
					key+=str[i].charAt(j)*i;
				}
			}
			return key;
		}
		
		public static Image getHaikuimg(){//俳句なし画像を返す
			return haikuimage;
		}
		
		public static Image gethaikuimgWithstr(){//俳句付き画像を返す
			return haikuimageWithStr;
		}
		
		private static void resetHaikuImage(){//2種の俳句画像リセット
			haikuimage=null;
			haikuimageWithStr=null;
		}
		
		public static BufferedImage alphatest(BufferedImage org){
			int w = org.getWidth();
			int h = org.getHeight();
			BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					if (org.getRGB(x, y) == 0xFFFFFFFF) {//ピンク
						dst.setRGB(x, y, 0);//透明
					}
					else {
						dst.setRGB(x, y, org.getRGB(x, y));
					}
				}
			}
			return dst;

		}
		
		public static java.awt.Image WCImagemodify(Image img,Image backimg){//途中 ワードクラウドの画像処理
			BufferedImage bimg=SwingFXUtils.fromFXImage(img, null);
			bimg=magnifySizeImage(bimg,450,450);
			int width = bimg.getWidth(null);
			int height = bimg.getHeight(null);
			int[] pixel = new int[width*height];
			PixelGrabber pg = new PixelGrabber(bimg,0,0,width,height,pixel,0,width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}

			int whiteRGB=Color.WHITE.getRGB();
			
			/*
			for(int i=0;i<width*height;i++){//白に近い色を白にする
				if ((255-r(pixel[i]))+(255-r(pixel[i]))+(255-r(pixel[i]))<20) {
					pixel[i]=whiteRGB;
				}
			}
			//*/

			for(int i=0;i<width*height;i++){//白透過
				if (pixel[i] == whiteRGB) {
					pixel[i]=pixel[i]&0x00FFFFFF;
				}
			}
			java.awt.Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			
			BufferedImage newbackimage=SwingFXUtils.fromFXImage(backimg, null);
			newbackimage=magnifySizeImage(newbackimage,450,450);
			
			int[] bpixel = new int[width*height];
			PixelGrabber bpg = new PixelGrabber(newbackimage,0,0,width,height,bpixel,0,width);
			try {
				bpg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}
			
			for(int i=0;i<width*height;i++){//白透過
				bpixel[i]=pixel[i]&0xFFFFFFFF;
			}
			java.awt.Image backalphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, bpixel, 0, width));
			
			
			return (java.awt.Image)synthesizeImage(backalphaImage,alphaImage,0,0);
			
			
			
		}
		

		    public static int a(int c){
		        return c>>>24;
		    }
		    public static int r(int c){
		        return c>>16&0xff;
		    }
		    public static int g(int c){
		        return c>>8&0xff;
		    }
		    public static int b(int c){
		        return c&0xff;
		    }
		    public static int rgb(int r,int g,int b){
		        return 0xff000000 | r <<16 | g <<8 | b;
		    }
		    public static int argb(int a,int r,int g,int b){
		        return a<<24 | r <<16 | g <<8 | b;
		    }


}
