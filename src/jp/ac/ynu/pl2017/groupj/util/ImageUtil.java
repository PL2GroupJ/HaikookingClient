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
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageUtil {

	private static Image haikuimage = null;
	private static Image haikuimageWithStr = null;
	private static int eachshapenum = 2;
	private static int compsize = 150;

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		BufferedImage backimage = null;
		BufferedImage compimage = null;
		BufferedImage compimage2 = null;
		BufferedImage compimage3 = null;
		BufferedImage compimage4 = null;
		BufferedImage compimage5 = null;
		BufferedImage compimage6 = null;

		try {
			// backimage = ImageIO.read(new File("fallbackground.jpg"));
			backimage = ImageIO.read(new File("spring-mask.png"));
			compimage2 = ImageIO.read(new File("ColorCat.jpg"));// ここに画像パス
			compimage = ImageIO.read(new File("ColorFish.jpg"));// ここに画像パス
			compimage3 = ImageIO.read(new File("fish.png"));
			compimage4 = ImageIO.read(new File("ColorDog.jpg"));
			compimage5 = ImageIO.read(new File("rose.png"));
			compimage6 = ImageIO.read(new File("spring-mask.png"));

		} catch (Exception e) {
			e.printStackTrace();
			backimage = null;
			compimage = null;
		}
		JFrame frame = new JFrame();
		BufferedImage synimg;
		Image[] fximg = { SwingFXUtils.toFXImage(compimage, null), SwingFXUtils.toFXImage(compimage2, null),
				SwingFXUtils.toFXImage(compimage3, null), SwingFXUtils.toFXImage(compimage4, null) };
		Image backimagefx = SwingFXUtils.toFXImage(backimage, null);
		String[] Haiku = { "あ", "あ", "あ" };
		Season season = Season.WINTER;
		createHaikuImage(backimagefx, fximg, Haiku, season);
		synimg = SwingFXUtils.fromFXImage(gethaikuimgWithstr(), null);// 俳句付き画像
		// synimg=SwingFXUtils.fromFXImage(getHaikuimg(), null); //俳句なし画像表示テスト用

		JLabel icon = new JLabel(new ImageIcon((java.awt.Image) synimg));
		synimg = SwingFXUtils.fromFXImage(getHaikuimg(), null);
		JLabel icon2 = new JLabel(new ImageIcon(synimg));

		frame.add(icon);
		frame.add(icon2);
		frame.setVisible(true);
		frame.setSize(synimg.getWidth(null) * 2 + 100, synimg.getHeight(null) + 100);
		icon.setBounds(20, 20, synimg.getWidth(null), synimg.getHeight(null));
		icon2.setBounds(20 + synimg.getHeight(null), 20, synimg.getWidth(null), synimg.getHeight(null));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	// */

	private static BufferedImage getBWImage(BufferedImage img) { // 完全に白黒化
		if (img == null)
			return null;
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		int[] pixel = new int[width * height];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}

		int blackRGB = Color.BLACK.getRGB();
		int whiteRGB = Color.WHITE.getRGB();

		for (int i = 0; i < width * height; i++) {
			if (pixel[i] != blackRGB && pixel[i] != whiteRGB) {
				int Bdistance = (int) Math.sqrt(Math.pow((double) r(pixel[i]), 2.0)
						+ Math.pow((double) g(pixel[i]), 2.0) + Math.pow((double) b(pixel[i]), 2.0));
				int Wdistance = (int) Math.sqrt(Math.pow((double) (255 - r(pixel[i])), 2.0)
						+ Math.pow((double) (255 - g(pixel[i])), 2.0) + Math.pow((double) (255 - b(pixel[i])), 2.0));
				if (Bdistance <= Wdistance) {
					pixel[i] = blackRGB;
				} else {
					pixel[i] = whiteRGB;
				}
			}
		}
		java.awt.Image alphaImage = Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, pixel, 0, width));
		return createBufferedImage(alphaImage);

	}

	private static BufferedImage magnifySizeImage(BufferedImage img, int width, int height) {// width,height:任意のサイズ
		java.awt.Image scaledImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);

		return createBufferedImage(scaledImage);
	}

	private static BufferedImage synthesizeImage(java.awt.Image back, java.awt.Image comp, int x, int y) {
		// 画像合成メソッド backは背景,compは合成画像,x,yは合成始点座標
		if (back == null || comp == null)
			return null;
		int width = back.getWidth(null);
		int height = back.getHeight(null);
		BufferedImage synthesizedimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = synthesizedimg.getGraphics();
		g.drawImage(back, 0, 0, null);
		g.drawImage(comp, x, y, null);
		return synthesizedimg;
	}

	private static java.awt.Image CircleImage(BufferedImage img) {
		if (img == null)
			return null;
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		BufferedImage Bcircle = null;

		// ピクセル値取得
		int[] pixel = new int[width * height];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}
		InputStream is;
		try {
			is = ImageUtil.class.getClassLoader().getResourceAsStream("image/shapedimage/circle.png");
			Bcircle = ImageIO.read(is);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Bcircle = magnifySizeImage(Bcircle, width, height);
		Bcircle = getBWImage(Bcircle);
		java.awt.Image c = (java.awt.Image) Bcircle;
		int[] circlepixel = new int[width * height];
		PixelGrabber cpg = new PixelGrabber(c, 0, 0, width, height, circlepixel, 0, width);
		try {
			cpg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}

		for (int i = 0; i < width * height; i++) {
			if (circlepixel[i] == -1) {
				pixel[i] = pixel[i] & 0x00FFFFFF;
			}
		}
		java.awt.Image alphaImage = Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, pixel, 0, width));
		try {
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return alphaImage;

	}

	private static java.awt.Image ShapedImage(BufferedImage img, BufferedImage shape) {
		// imgが切り抜く素材画像,shapeが切り抜く形の画像
		if (img == null || shape == null)
			return null;
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		BufferedImage shapedimg = shape;

		// ピクセル値取得
		int[] pixel = new int[width * height];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}

		shapedimg = magnifySizeImage(shapedimg, width, height);
		shapedimg = getBWImage(shapedimg);
		java.awt.Image c = (java.awt.Image) shapedimg;
		int[] shapepixel = new int[width * height];
		PixelGrabber cpg = new PixelGrabber(c, 0, 0, width, height, shapepixel, 0, width);
		try {
			cpg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}

		for (int i = 0; i < width * height; i++) {
			if (shapepixel[i] == -1) {
				pixel[i] = pixel[i] & 0x00FFFFFF;
			}
		}
		java.awt.Image alphaImage = Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, pixel, 0, width));

		return alphaImage;

	}

	private static BufferedImage DrawString(BufferedImage img, String[] str) {
		if (img == null || str.length != 3)
			return null;
		BufferedImage newimage = img;
		Graphics graphics = newimage.createGraphics();
		int defaultsize=120;

		int Maxlen = 0;
		for (int i = 0; i < str.length - 1; i++) {
			Maxlen = Math.max(str[i].length(), str[i + 1].length());
		}
		int fontsize = 450 / Maxlen - 10;
		if(fontsize>130){
			fontsize=120;
		}
		
		// 文字合成
		graphics.setColor(Color.BLACK);
		Font font = new Font("ＭＳ 明朝", Font.BOLD, fontsize);
		for (int len = 0; len < str.length; len++) {
			for (int i = 0; i < str[len].length(); i++) {
				graphics.setFont(font);
				graphics.drawString(String.valueOf(str[len].charAt(i)),300 +len*(-130), 100 + fontsize * i);
			}
		}
		return newimage;

	}

	private static BufferedImage getgrayImage(BufferedImage img) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		BufferedImageOp op = new ColorConvertOp(cs, null);
		BufferedImage grayimage = img;
		int imageType = grayimage.getType();
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), imageType);
		op.filter(grayimage, bufferedImage);
		return bufferedImage;
	}

	private static BufferedImage createBufferedImage(java.awt.Image img) {
		BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics g = bimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
	}

	public static void createHaikuImage(Image backimage, Image[] img, String[] str) {
		// 2種の俳句画像を生成、compsizeは合成素材正方画像サイズ,backimageは背景画像,strは俳句をいれる
		if (backimage == null || img.length == 0 || str.length != 3)
			return;
		BufferedImage newbackimage = null;
		resetHaikuImage();

		newbackimage = SwingFXUtils.fromFXImage(backimage, null);
		newbackimage = magnifySizeImage(newbackimage, 450, 450);// 背景画像加工
																// //450*450
		BufferedImage synimg = newbackimage;
		int xypos[][] = createXYpos(getHaikukey(str), img.length, 450, 450);
		for (int i = 0; i < img.length; i++) {
			BufferedImage bimg = SwingFXUtils.fromFXImage(img[i], null);
			bimg = magnifySizeImage(bimg, compsize, compsize);// コンポーネント画像加工
			bimg = getgrayImage(bimg);
			java.awt.Image afterimg = CircleImage(bimg);

			synimg = synthesizeImage(synimg, afterimg, xypos[i][0], xypos[i][1]); // 合成
		}
		synimg = createBufferedImage(removeblack(synimg));
		haikuimage = SwingFXUtils.toFXImage(synimg, null);
		synimg = DrawString(synimg, str);
		synimg = createBufferedImage(FontFramed((java.awt.Image) synimg));
		haikuimageWithStr = SwingFXUtils.toFXImage(synimg, null);
		// return Haikuimg;

	}

	public static void createHaikuImage(Image backimage, Image[] img, String[] str, Season season) {
		// 2種の俳句画像を生成、compsizeは合成素材正方画像サイズ,backimageは背景画像,strは俳句をいれる
		if (backimage == null || img.length == 0 || str.length != 3)
			return;
		BufferedImage newbackimage = null;
		resetHaikuImage();

		newbackimage = SwingFXUtils.fromFXImage(backimage, null);
		newbackimage = magnifySizeImage(newbackimage, 450, 450);// 背景画像加工
																// //450*450
		BufferedImage synimg = newbackimage;
		Random rnd = new Random(getHaikukey(str));
		boolean seasonflg = true;
		InputStream[] is = new InputStream[eachshapenum];
		BufferedImage[] shapeimage = new BufferedImage[eachshapenum];
		
		switch (season) {
		case SPRING:{
			is[0] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\spring1.png");
			is[1] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\spring2.jpg");
			try {
				shapeimage[0] = ImageIO.read(is[0]);
				shapeimage[1] = ImageIO.read(is[1]);

			} catch (Exception e) {
				e.printStackTrace();

			}
			break;
		}
		case SUMMER:{
			is[0] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\summer1.png");
			is[1] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\summer2.png");
			try {
				shapeimage[0] = ImageIO.read(is[0]);
				shapeimage[1] = ImageIO.read(is[1]);
				is[0].close();
				is[1].close();

			} catch (Exception e) {
				e.printStackTrace();

			}
			break;
		}
		case AUTUMN:{
			is[0] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\autumn1.gif");
			is[1] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\autumn2.jpg");
			try {
				shapeimage[0] = ImageIO.read(is[0]);
				shapeimage[1] = ImageIO.read(is[1]);
				is[0].close();
				is[1].close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		
		case WINTER:{
			is[0] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\winter1.png");
			is[1] = ImageUtil.class.getClassLoader().getResourceAsStream("image\\shapedimage\\winter2.jpg");
			try {
				shapeimage[0] = ImageIO.read(is[0]);
				shapeimage[1] = ImageIO.read(is[1]);
				is[0].close();
				is[1].close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}		
		default:
			seasonflg = false;
			break;
		}

		int xypos[][] = createXYpos(getHaikukey(str), img.length, 450, 450);

		if (seasonflg) {
			for (int i = 0; i < img.length; i++) {
				int randomnum = rnd.nextInt(2);
				BufferedImage bimg = SwingFXUtils.fromFXImage(img[i], null);
				bimg = magnifySizeImage(bimg, compsize, compsize);// コンポーネント画像加工
				bimg = getgrayImage(bimg);
				// java.awt.Image afterimg=CircleImage(bimg);
				BufferedImage shapedimage = null;
				try {

					// shapedimage = shapeimgmap.get(seasonkey.toString());
					shapedimage = shapeimage[randomnum];
				} catch (Exception e) {
					e.printStackTrace();
				}
				java.awt.Image afterimg = ShapedImage(bimg, shapedimage);
				synimg = synthesizeImage(synimg, afterimg, xypos[i][0], xypos[i][1]); // 合成
			}
			synimg = createBufferedImage(removeblack(synimg));
			haikuimage = SwingFXUtils.toFXImage(synimg, null);
			synimg = DrawString(synimg, str);
			synimg = createBufferedImage(FontFramed((java.awt.Image) synimg));
			haikuimageWithStr = SwingFXUtils.toFXImage(synimg, null);
		} else {
			createHaikuImage(backimage, img, str);
		}

	}

	private static java.awt.Image removeblack(BufferedImage bimg) {
		if (bimg == null)
			return null;
		bimg = magnifySizeImage(bimg, 450, 450);
		int width = bimg.getWidth(null);
		int height = bimg.getHeight(null);
		int[] pixel = new int[width * height];
		PixelGrabber pg = new PixelGrabber(bimg, 0, 0, width, height, pixel, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		int blackRGB = Color.BLACK.getRGB();
		for (int i = 0; i < width * height; i++) {
			if (pixel[i] == blackRGB)
				pixel[i] = 0xFF000001;
		}
		java.awt.Image alphaImage = Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, pixel, 0, width));
		return alphaImage;

	}

	private static int getHaikukey(String[] str) {// 俳句に応じたkeyを返す(乱数シード用)
		int key = 0;
		for (int i = 0; i < str.length; i++) {
			for (int j = 0; j < str[i].length(); j++) {
				key += str[i].charAt(j) * i;
			}
		}
		return key;
	}

	public static Image getHaikuimg() {// 俳句なし画像を返す
		return haikuimage;
	}

	public static Image gethaikuimgWithstr() {// 俳句付き画像を返す
		return haikuimageWithStr;
	}

	private static void resetHaikuImage() {// 2種の俳句画像リセット
		haikuimage = null;
		haikuimageWithStr = null;
	}

	public static BufferedImage alphatest(BufferedImage org) {
		int w = org.getWidth();
		int h = org.getHeight();
		BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (org.getRGB(x, y) == 0xFFFFFFFF) {// ピンク
					dst.setRGB(x, y, 0);// 透明
				} else {
					dst.setRGB(x, y, org.getRGB(x, y));
				}
			}
		}
		return dst;

	}


	private static java.awt.Image FontFramed(java.awt.Image img) {
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		int[] pixel = new int[width * height];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			return null;
		}

		int blackRGB = Color.BLACK.getRGB();
		int whiteRGB = Color.WHITE.getRGB();

		for (int i = 0; i < width * height; i++) {// 白透過
			if (pixel[i] == blackRGB) {
				if (i + height * 3 < height * width) {
					if (i + height < width * height)
						if (pixel[i + height] != blackRGB)
							pixel[i + height] = whiteRGB;
					if (i - height >= 0)
						if (pixel[i - height] != blackRGB)
							pixel[i - height] = whiteRGB;
					if (i != 0)
						if (pixel[i - 1] != blackRGB)
							pixel[i - 1] = whiteRGB;
					if (i + 1 < width * height)
						if (pixel[i + 1] != blackRGB)
							pixel[i + 1] = whiteRGB;
					if (i + height + 1 < width * height)
						if (pixel[i + height + 1] != blackRGB)
							pixel[i + height + 1] = whiteRGB;
					if (i + height - 1 < width * height)
						if (pixel[i + height - 1] != blackRGB)
							pixel[i + height - 1] = whiteRGB;
					if (i - height + 1 >= 0)
						if (pixel[i - height + 1] != blackRGB)
							pixel[i - height + 1] = whiteRGB;
					if (i - height - 1 >= 0)
						if (pixel[i - height - 1] != blackRGB)
							pixel[i - height - 1] = whiteRGB;
				}
			}
		}
		java.awt.Image alphaImage = Toolkit.getDefaultToolkit()
				.createImage(new MemoryImageSource(width, height, pixel, 0, width));
		return alphaImage;
	}

	private static int[][] createXYpos(int key, int num, int xmax, int ymax) {
		// 配置コンポーネントがある程度重複しないような乱数生成メソッド
		if (num == 0 || xmax == 0 || ymax == 0)
			return null;
		int failcount = 0; // 連続乱数失敗回数
		int[][] xypos = new int[num][2];// 返す座標値

		for (int i = 0; i < xypos.length; i++) {// 配列初期化
			for (int j = 0; j < 2; j++)
				xypos[i][j] = 0;
		}

		Random rnd = new Random(key);
		int x = 0;
		int y = 0;
		int xterminal = compsize;
		int yterminal = compsize;
		boolean leftup = false;// 左上がかぶるかどうか
		boolean rightup = false;// 右上がかぶるかどうか
		boolean leftdown = false;// 左下がかぶるかどうか
		boolean rightdown = false;// 右下がかぶるかどうか
		int testx = 0;
		int testy = 0;
		int currentnum = 0;
		int faild = 0;//合計失敗回数
		int overlappixel = 0;//重なってよいピクセル数
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				x = rnd.nextInt(xmax - compsize);
				y = rnd.nextInt(ymax - compsize);
				xypos[i][0] = x;
				xypos[i][1] = y;
			} else {
				x = rnd.nextInt(xmax - compsize);
				y = rnd.nextInt(ymax - compsize);
				xterminal = x + compsize;
				yterminal = y + compsize;
				while (currentnum < i) {
					while (failcount < 50) {
						testx = xypos[currentnum][0];
						testy = xypos[currentnum][1];
						leftup = (x >= testx && x <= testx + compsize - overlappixel)
								&& (y >= testy && y <= testy + compsize - overlappixel);// 左上がかぶるかどうか
						rightup = (xterminal >= testx + overlappixel && xterminal <= testx + compsize)
								&& (y >= testy && y <= testy + compsize - overlappixel);// 右上がかぶるかどうか
						leftdown = (x >= testx && x <= testx + compsize - overlappixel)
								&& (yterminal >= testy + overlappixel && yterminal <= testy + compsize);// 左下がかぶるかどうか
						rightdown = (xterminal >= testx + overlappixel && xterminal <= testx + compsize)
								&& (yterminal >= testy + overlappixel && yterminal <= testy + compsize);// 右下がかぶるかどうか
						if (leftup || rightup || leftdown || rightdown) {
							failcount++;
							x = rnd.nextInt(xmax - compsize);
							y = rnd.nextInt(ymax - compsize);
							xterminal = x + compsize;
							yterminal = y + compsize;
							currentnum = 0;
							faild++;
							overlappixel = (faild / 150) * (compsize / 8);
						} else {
							break;
						}
					}
					failcount = 0;
					currentnum++;
				}
				xypos[i][0] = x;// 連続失敗値
				xypos[i][1] = y;
				failcount = 0;
				currentnum = 0;

			}
		}
		return xypos;
	}

	// 以下は引数からアルファ値及びRGBのそれぞれの値を抽出するメソッド

	private static int r(int c) {
		return c >> 16 & 0xff;
	}

	private static int g(int c) {
		return c >> 8 & 0xff;
	}

	private static int b(int c) {
		return c & 0xff;
	}

}
