package jp.ac.ynu.pl2017.groupj.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class ImageUtil {

		public static void main(String[] args) {
			// TODO 自動生成されたメソッド・スタブ
			BufferedImage backimage=null;
			BufferedImage compimage=null;
			BufferedImage compimage2=null;

			try {
				//backimage = ImageIO.read(new File("fallbackground.jpg"));
				backimage =ImageIO.read(new File("spring.jpg"));
			    //compimage = ImageIO.read(new File("fish.png"));
			    //compimage = ImageIO.read(new File("rose.png"));
			    //compimage = ImageIO.read(new File("GrayCat.jpg"));
			    //compimage = ImageIO.read(new File("GrayCat2.jpg"));
			    //compimage=ImageIO.read(new File("ColorDog.jpg"));
			    compimage2=ImageIO.read(new File("ColorCat.jpg"));
			    compimage=ImageIO.read(new File("ColorFish.jpg"));
			} catch (Exception e) {
			    e.printStackTrace();
			    backimage = null;
			    compimage = null;
			}
			JFrame frame=new JFrame();
			Image img1;
			Image img2;
			Image synimg;
			Image newbackimage=null;
			//img=(Image)backimage;
			//img=magnifyScaleImage((Image)backimage,200);
			//img=magnifySizeImage(getAlphaImage((Image)backimage,Color.WHITE),100,100);
			newbackimage=magnifySizeImage((Image)backimage,600,600);//背景画像加工 //450*450
			newbackimage=AlphaChange(newbackimage,0);
			//newbackimage=getgrayImage(newbackimage);

			//img1=getAlphaImage(getBWImage((Image)compimage),Color.BLACK);//コンポーネント画像1加工
			//img1=magnifySizeImage((Image)img1,300,300);
			img1=magnifySizeImage((Image)compimage,200,200);
			img1=getBWImage(img1);
			img1=CircleImage(img1);

			//img2=getAlphaImage(getBWImage((Image)compimage2),Color.BLACK);//コンポーネント画像2加工
			img2=magnifySizeImage((Image)compimage2,200,200);
			img2=getgrayImage(img2);
			img2=CircleImage(img2);


			synimg=synthesizeImage(newbackimage,img1,20,20);//
			//JLabel icon=new JLabel(new ImageIcon(synimg));


			synimg=synthesizeImage(synimg,img2,200,300);//
			synimg=DrawString(synimg,"古池や");
			JLabel icon=new JLabel(new ImageIcon(synimg));



			frame.add(icon);
			frame.setVisible(true);
			frame.setSize(synimg.getWidth(null)+100,synimg.getHeight(null)+100 );
			icon.setBounds(20, 20, synimg.getWidth(null),synimg.getHeight(null) );
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		public static Image getAlphaImage(Image img,Color alpha) {
			if (img == null) return null;
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
			//透明化
			int color = alpha.getRGB();
			for(int i=0;i<width*height;i++){
				if (pixel[i] != color) {
					pixel[i] = pixel[i] & 0x00FFFFFF;
				}
			}

			//イメージに戻す
			Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return alphaImage;
		}

		public static Image getBWImage(Image img){ //完全に白黒化
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
			Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return alphaImage;

		}

		public static Image magnifyScaleImage(Image img, int scale) { //scale:任意のスケール
			 int width = img.getWidth(null);
			 int height = img.getHeight(null);
			 Image scaledImage = img.getScaledInstance(
			 width * scale/100, height*scale/100, Image.SCALE_DEFAULT);
			 return scaledImage;
			 }

		public static Image magnifySizeImage(Image img, int width,int height) {//width,height:任意のサイズ
			 //int width = img.getWidth(null);
			 //int height = img.getHeight(null);
			 Image scaledImage = img.getScaledInstance(
			 width, height, Image.SCALE_DEFAULT);
			 return scaledImage;
			 }

		public static Image synthesizeImage(Image back,Image comp,int x,int y){
			//画像合成メソッド backは背景,compは合成画像,x,yは合成始点座標
			if(back==null||comp==null) return null;
			int width = back.getWidth(null);
			int height = back.getHeight(null);
			BufferedImage synthesizedimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = synthesizedimg.getGraphics();
			g.drawImage(back, 0, 0, null);
			g.drawImage(comp, x, y, null);
			return (Image)synthesizedimg;
		}

		public static Image AlphaChange(Image img,int alpha){
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
			Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return alphaImage;
		}

		public static Image CircleImage(Image img){//途中
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
			Image c=(Image)Bcircle;
			c=magnifySizeImage(c,width,height);
			int[] circlepixel = new int[width*height];
			PixelGrabber cpg = new PixelGrabber(c,0,0,width,height,circlepixel,0,width);
			try {
				cpg.grabPixels();
			} catch (InterruptedException e) {
				return null;
			}
			//int disX=width-x;
			//int disY=height-y;

			for(int i=0;i<width*height;i++){
				if(circlepixel[i]==-1){
					pixel[i]=pixel[i]& 0x00FFFFFF;
				}
			}
			Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			return alphaImage;

			//g.setClip(new Ellipse2D.Double(0,0,200,200));
			//g.drawImage();

			//Image alphaImage = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixel, 0, width));
			//return alphaImage;

			}

		public static Image DrawString(Image img,String str){
			if(img==null) return null;
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			//BufferedImage newimage=(BufferedImage)img;
			BufferedImage newimage=createBufferedImage(img);
			Graphics graphics = newimage.createGraphics();

			//	いたずら書き
			graphics.setColor(Color.BLACK);
			Font font = new Font("ＭＳ 明朝", Font.BOLD, 100);
			for(int i=0;i<str.length();i++){
				graphics.setFont(font);
				graphics.drawString(String.valueOf(str.charAt(i)),400,200+100*i);
			}
			return (Image)newimage;

			}

		public static Image getgrayImage(Image img){
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			BufferedImageOp op = new ColorConvertOp(cs,null);
			//BufferedImage grayimage=(BufferedImage)img;
			BufferedImage grayimage=createBufferedImage(img);
			int imageType=grayimage.getType();
			BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),img.getHeight(null),imageType);
			op.filter(grayimage,bufferedImage);
			return (Image)bufferedImage;
		}

		public static BufferedImage createBufferedImage(Image img) {
			BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

			Graphics g = bimg.getGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();

			return bimg;
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
