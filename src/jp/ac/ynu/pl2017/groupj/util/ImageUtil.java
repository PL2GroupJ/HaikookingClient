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
import java.util.HashMap;
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
	private static String pathToShapeimg="res/image/shapedimage";
	private static File[] shapeimgfile=(new File(pathToShapeimg)).listFiles();
	private static int shapenum=shapeimgfile.length;
	private static int springshapenum=0;
	private static int summershapenum=0;
	private static int autumnshapenum=0;
	private static int wintershapenum=0;
	private static HashMap<String,BufferedImage> shapeimgmap=new HashMap<String,BufferedImage>(){
		{
			for(int i=0;i<shapenum;i++){
				try{
					BufferedImage img=ImageIO.read(shapeimgfile[i]);
					String filename=shapeimgfile[i].getName().substring(0,shapeimgfile[i].getName().indexOf(".")); 
					if(filename.startsWith("spring")){
						springshapenum++;
					}else if(filename.startsWith("summer")){
						summershapenum++;
					}else if(filename.startsWith("autumn")){
						autumnshapenum++;
					}else if(filename.startsWith("winter")){
						wintershapenum++;
					}
					put(filename,img);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
    	}
	};
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
			String[] Haiku={"古池や","かえる飛び込む","水の音"};
			Image Haikuimg=null;
			createHaikuImage(150,backimagefx,fximg,Haiku,"spring");
			synimg=SwingFXUtils.fromFXImage(gethaikuimgWithstr(), null);//俳句付き画像
			//synimg=SwingFXUtils.fromFXImage(getHaikuimg(), null); //俳句なし画像表示テスト用
			
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
			Bcircle=getBWImage(Bcircle);
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
		
		private static java.awt.Image ShapedImage(BufferedImage img,BufferedImage shape){
			//imgが切り抜く素材画像,shapeが切り抜く形の画像
			if(img==null||shape==null) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			BufferedImage shapedimg=shape;

			//ピクセル値取得
			int[] pixel = new int[width*height];
			PixelGrabber pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}

			//Image c=(Image)Bcircle;
			shapedimg=magnifySizeImage(shapedimg,width,height);
			shapedimg=getBWImage(shapedimg);
			java.awt.Image c=(java.awt.Image)shapedimg;
			int[] shapepixel = new int[width*height];
			PixelGrabber cpg = new PixelGrabber(c,0,0,width,height,shapepixel,0,width);
			try {
				cpg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}

			for(int i=0;i<width*height;i++){
				if(shapepixel[i]==-1){
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
			//2種の俳句画像を生成、compsizeは合成素材正方画像サイズ,backimageは背景画像,strは俳句をいれる
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
				//java.awt.Image afterimg=CircleImage(bimg);
				BufferedImage shapedimage=null;
				try{
				shapedimage=shapeimgmap.get("circle");
				}catch(Exception e){
					e.printStackTrace();
				}
				java.awt.Image afterimg=ShapedImage(bimg,shapedimage);
			
				int x=rnd.nextInt(450-compsize); //座標のランダム生成
				int y=rnd.nextInt(450-compsize);
			
				synimg=synthesizeImage(synimg,afterimg,x,y); //合成
				}
			//synimg=getAlphaImage(synimg,newbackimage);
			//synimg=AlphaChange(newbackimage,0);
			//Image Haikuimg=SwingFXUtils.toFXImage(synimg, null);
			haikuimage=SwingFXUtils.toFXImage(synimg, null);
			synimg=DrawString(synimg,str);
			synimg=createBufferedImage(FontFramed((java.awt.Image)synimg));
			haikuimageWithStr=SwingFXUtils.toFXImage(synimg, null);
			//return Haikuimg;
			
			
			
		}
		
		public static void createHaikuImage(int compsize,Image backimage,Image[] img,String[] str,String season){
			//2種の俳句画像を生成、compsizeは合成素材正方画像サイズ,backimageは背景画像,strは俳句をいれる
			if(backimage==null || img.length==0 || str.length!=3) return;
			BufferedImage newbackimage=null;
			resetHaikuImage();
			
			newbackimage=SwingFXUtils.fromFXImage(backimage,null);
			newbackimage=magnifySizeImage(newbackimage,450,450);//背景画像加工 //450*450
			BufferedImage synimg=newbackimage;
			Random rnd = new Random(getHaikukey(str));
			int seasonmaxnum=0;
			
			if(season=="spring") seasonmaxnum=springshapenum;
			else if(season=="summer") seasonmaxnum=summershapenum;
			else if(season=="autumn") seasonmaxnum=autumnshapenum;
			else if(season=="winter") seasonmaxnum=wintershapenum;
			
			if(seasonmaxnum!=0){
			for(int i=0;i<img.length;i++){
				int randomnum=rnd.nextInt(seasonmaxnum);
				StringBuffer seasonkey=new StringBuffer(season);
				seasonkey.append(Integer.toString(randomnum+1));
				System.out.println(seasonkey);
				BufferedImage bimg=SwingFXUtils.fromFXImage(img[i], null);
				bimg=magnifySizeImage(bimg,compsize,compsize);//コンポーネント画像加工
				bimg=getgrayImage(bimg);
				//java.awt.Image afterimg=CircleImage(bimg);
				BufferedImage shapedimage=null;
				try{
					shapedimage=shapeimgmap.get(seasonkey.toString());
				}catch(Exception e){
					e.printStackTrace();
				}
				java.awt.Image afterimg=ShapedImage(bimg,shapedimage);
			
				int x=rnd.nextInt(450-compsize); //座標のランダム生成
				int y=rnd.nextInt(450-compsize);
			
				synimg=synthesizeImage(synimg,afterimg,x,y); //合成
				}
			
			haikuimage=SwingFXUtils.toFXImage(synimg, null);
			synimg=DrawString(synimg,str);
			synimg=createBufferedImage(FontFramed((java.awt.Image)synimg));
			haikuimageWithStr=SwingFXUtils.toFXImage(synimg, null);
			}else{
				createHaikuImage(compsize,backimage,img,str);
			}
			
			
		}
		
		private static int getHaikukey(String[] str){//俳句に応じたkeyを返す(乱数シード用)
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
			
			for(int i=0;i<width*height;i++){
				bpixel[i]=bpixel[i]&0x3FFFFFFF;
			}
			java.awt.Image backalphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, bpixel, 0, width));
			
			
			return (java.awt.Image)synthesizeImage(backalphaImage,alphaImage,0,0);
			
			
			
		}
		
		private static java.awt.Image FontFramed(java.awt.Image img){
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
			
			for(int i=0;i<width*height;i++){//白透過
				if(pixel[i]==blackRGB){
					if(i+height*3<height*width)	{
						for(int j=1;j<4;j++){
							if(pixel[i+height*j]!=blackRGB&&i+height*j<=width*height)	pixel[i+height*j]=whiteRGB;
							if(pixel[i-height*j]!=blackRGB&&i-height*j>=width*height)	pixel[i-height*j]=whiteRGB;
							if(pixel[i-1*j]!=blackRGB&&i+1*j<=width*height)	pixel[i-1*j]=whiteRGB;
							if(pixel[i+1*j]!=blackRGB&&i-1*j>=width*height)	pixel[i+1*j]=whiteRGB;
							if(pixel[i+height+1]!=blackRGB&&i+height+1<=width*height)	pixel[i+height+1]=whiteRGB;
							if(pixel[i+height-1]!=blackRGB&&i+height-1<=width*height)	pixel[i+height-1]=whiteRGB;
							if(pixel[i-height+1]!=blackRGB&&i-height+1>=width*height)	pixel[i-height+1]=whiteRGB;
							if(pixel[i-height-1]!=blackRGB&&i-height+1>=width*height)	pixel[i-height-1]=whiteRGB;
							}
						}
				}
			}
			java.awt.Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return alphaImage;
		}
		
		
		private static int[][] createXYpos(int key,BufferedImage compimg[],int num,int xmax,int ymax){
		//配置コンポーネントがある程度重複しないような乱数生成メソッド
			if(compimg.length==0||num==0||xmax==0||ymax==0) return null;
			int overlappixel=0; //重なるピクセルの許可数
			int failcount=0; //連続乱数失敗回数
			int[][] xypos=new int[num][2];//返す座標値
			
			for(int i=0;i<xypos.length;i++){//配列初期化
				for(int j=0;j<2;j++) xypos[i][j]=0;
			}
			
			//以下真っ白画像作成
			int whiteRGB=Color.WHITE.getRGB();
			int[] pixel = new int[xmax*ymax];
			for(int i=0;i<xmax*ymax;i++){
				pixel[i]=whiteRGB;
			}
			
			java.awt.Image whiteimage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(xmax, ymax, pixel, 0, xmax));
			
			
			Random rnd = new Random(key);
			BufferedImage bimg;
			java.awt.Image newimage=whiteimage;
			
			for(int i=0;i<num;i++){
				bimg=compimg[i];
				int width = bimg.getWidth(null);
				int height = bimg.getHeight(null);
			while(failcount<20){
				int x=rnd.nextInt(xmax-width); 
				int y=rnd.nextInt(ymax-height);
				if(pixel[x+y*ymax]==whiteRGB){
					newimage=synthesizeImage(whiteimage,compimg[0],x,y);
					xypos[i][0]=x;
					xypos[i][1]=y;
				}else{
					failcount++;
				}
			}
			}
			
			return xypos;
		}
		
		
			//以下は引数からアルファ値及びRGBのそれぞれの値を抽出するメソッド
		    private static int a(int c){
		        return c>>>24;
		    }
		    private static int r(int c){
		        return c>>16&0xff;
		    }
		    private static int g(int c){
		        return c>>8&0xff;
		    }
		    private static int b(int c){
		        return c&0xff;
		    }
		    private static int rgb(int r,int g,int b){
		        return 0xff000000 | r <<16 | g <<8 | b;
		    }
		    private static int argb(int a,int r,int g,int b){
		        return a<<24 | r <<16 | g <<8 | b;
		    }


}
