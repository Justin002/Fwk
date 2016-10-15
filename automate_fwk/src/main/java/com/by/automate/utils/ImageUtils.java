package com.by.automate.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtils {

    private static String DEFAULT_PREVFIX = "thumb_";
    private static Boolean DEFAULT_FORCE = false;

    public static void compareImage(String imagePath1, String imagePath2) {
        String[] images = { imagePath1, imagePath2 };
        if (images.length == 0) {
            System.out.println("Usage >java BMPLoader ImageFile.bmp");
            System.exit(0);
        }

        // 分析圖片相似度
        String[][] list1 = getPX(images[0]);
        String[][] list2 = getPX(images[1]);
        int xiangsi = 0;
        int busi = 0;

        for (int i = 0; i < list1.length; i++) {
            for (int m = 0; m < list1[i].length; m++) {
                String[] value1 = list1[i][m].toString().split(",");
                String[] value2 = list2[i][m].toString().split(",");
                /*
                 * if (!Arrays.toString(value1).equals(Arrays.toString(value2)))
                 * { busi++; } else { xiangsi++; }
                 */

                if (compareArrays(value1, value2)) {
                    busi++;
                } else {
                    xiangsi++;
                }
            }
        }

        list1 = getPX(images[1]);
        list2 = getPX(images[0]);

        for (int i = 0; i < list1.length; i++) {
            for (int m = 0; m < list1[i].length; m++) {
                String[] value1 = list1[i][m].toString().split(",");
                String[] value2 = list2[i][m].toString().split(",");
                if (!Arrays.toString(value1).equals(Arrays.toString(value2))) {
                    busi++;
                } else {
                    xiangsi++;
                }
            }
        }

        System.out.println("before:" + xiangsi);
        System.out.println("busi:" + busi);

        String baifen = "";
        try {
            baifen = ((Double.parseDouble(xiangsi + "") / Double.parseDouble((busi + xiangsi) + "")) + "");
            baifen = baifen.substring(baifen.indexOf(".") + 1, baifen.indexOf(".") + 3);
        } catch (Exception e) {
            baifen = "0";
        }
        if (baifen.length() <= 0) {
            baifen = "0";
        }
        if (busi == 0) {
            baifen = "100";
        }

        System.out.println("相似像素数量：" + xiangsi + " 不相似像素数量：" + busi + " 相似率：" + Integer.parseInt(baifen) + "%");

    }

    private static boolean compareArrays(String[] value1, String[] value2) {

        for (int i = 0; i < value1.length; i++) {
            if ((Math.abs(Integer.parseInt(value1[i].toString())) - Math.abs(Integer.parseInt(value2[i].toString())) == 0)) {
                return false;
            }
        }

        return true;
    }

    public static String[][] getPX(String args) {

        int[] rgb = new int[3];

        File file = new File(args);
        BufferedImage bi = null;

        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            // TODO: handle exception
        }

        int width = bi.getWidth();
        int height = bi.getHeight();
        int minx = bi.getMinX();
        int miny = bi.getMinY();

        String[][] list = new String[width][height];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);

                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                list[i][j] = rgb[0] + "," + rgb[1] + "," + rgb[2];
                // System.out.println("list :" + rgb[0] + "," + rgb[1] + "," +
                // rgb[2]);
            }
        }

        return list;
    }

    public static void thumbnailImage(File imgFile, int w, int h, String prevfix, boolean force) {
        if (imgFile.exists()) {
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG,
                // JPEG, WBMP, GIF, gif]

                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;

                // 获取图片后缀
                if (imgFile.getName().indexOf(".") > -1) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }

                // 类型和图片后缀全部小写，然后判断后缀是否合法
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0) {
                    System.err.println("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return;
                }

                // System.out.println("target image's size, width:{}, height:{}.",w,h);
                Image img = ImageIO.read(imgFile);
                if (!force) {

                    // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if ((width * 1.0) / w < (height * 1.0) / h) {
                        if (width > w) {
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
                            // log.debug("change image's height, width:{}, height:{}.",w,h);
                        }
                    } else {
                        if (height > h) {
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
                            // log.debug("change image's width, width:{}, height:{}.",w,h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                String p = imgFile.getPath();
                // 将图片保存在原目录并加上前缀
                ImageIO.write(bi, suffix, new File(p.substring(0, p.lastIndexOf(File.separator)) + File.separator + prevfix + imgFile.getName()));
            } catch (IOException e) {
                System.err.println("generate thumbnail image failed.");
            }
        } else {
            System.err.println("the image is not exist.");
        }
    }

    /**
     * <p>
     * Title: cutImage
     * </p>
     * <p>
     * Description: 根据原图与裁切size截取局部图片
     * </p>
     * 
     * @param srcImg
     *            源图片
     * @param output
     *            图片输出流
     * @param rect
     *            需要截取部分的坐标和大小
     */
    public static void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect) {
        if (srcImg.exists()) {
            java.io.FileInputStream fis = null;
            ImageInputStream iis = null;
            try {
                fis = new FileInputStream(srcImg);
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;

                if (srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }

                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
                    System.err.println("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return;
                }

                // 将FileInputStream 转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);

                // 根据图片类型获取该种类型的ImageReader
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(iis, true);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0, param);
                ImageIO.write(bi, suffix, output);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                    if (iis != null)
                        iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("the src image is not exist.");
        }
    }

    public static void thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force) {
        File imgFile = new File(imagePath);
        thumbnailImage(imgFile, w, h, prevfix, force);
    }

    public void thumbnailImage(String imagePath, int w, int h, boolean force) {
        thumbnailImage(imagePath, w, h, DEFAULT_PREVFIX, force);
    }

    public void thumbnailImage(String imagePath, int w, int h) {
        thumbnailImage(imagePath, w, h, DEFAULT_FORCE);
    }

    private static String DEFAULT_CUT_PREVFIX = "cut_";

    public static void cutImage(File srcImg, OutputStream output, int x, int y, int width, int height) {
        cutImage(srcImg, output, new java.awt.Rectangle(x, y, width, height));
    }

    public static void cutImage(File srcImg, String destImgPath, java.awt.Rectangle rect) {
        File destImg = new File(destImgPath);
        if (destImg.exists()) {
            String p = destImg.getPath();
            try {
                if (!destImg.isDirectory())
                    p = destImg.getParent();
                if (!p.endsWith(File.separator))
                    p = p + File.separator;
                cutImage(srcImg,
                        new java.io.FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_" + new java.util.Date().getTime() + "_" + srcImg.getName()), rect);
            } catch (FileNotFoundException e) {
                // log.warn("the dest image is not exist.");
            }
        } else
            System.err.print("the dest image folder is not exist.");
    }

    public static void cutImage(File srcImg, String destImg, int x, int y, int width, int height) {
        cutImage(srcImg, destImg, new java.awt.Rectangle(x, y, width, height));
    }

    public static void cutImage(String srcImg, String destImg, int x, int y, int width, int height) {
        cutImage(new File(srcImg), destImg, new java.awt.Rectangle(x, y, width, height));
    }

    public static void copyImage(String oldPaht, String newPath) {
        FileInputStream fi;
        try {
            for (int i = 0; i < 20; i++) {
                if (!new File(oldPaht).exists()) {
                    try {
                        System.out.println("Hond on : 0.5s.");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            if (!new File(oldPaht).exists()) {
                System.err.println("路径不存在 " + oldPaht + ".");
            } else {
                fi = new FileInputStream(oldPaht);
                BufferedInputStream in = new BufferedInputStream(fi);

                FileOutputStream fo = new FileOutputStream(newPath);
                BufferedOutputStream out = new BufferedOutputStream(fo);

                byte[] buf = new byte[4096];
                int len = in.read(buf);
                while (len != -1) {
                    out.write(buf, 0, len);
                    len = in.read(buf);
                }
                out.close();
                fo.close();
                in.close();
                fi.close();
            }

        } catch (IOException e) {      
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // ImageUtils.compareImage("C:\\Users\\Wendy\\Desktop\\Image1.png",
        // "C:\\Users\\Wendy\\Desktop\\Image2.png");
        // test();
        // cutImage("C:\\Users\\Wendy\\Desktop\\img.png",
        // "C:\\Users\\Wendy\\Desktop", 0, 146, 720, 405);
        // thumbnailImage("C:\\Users\\Wendy\\Desktop\\cut__1440657046525_img.png",
        // 364, 204, "test", true);

        // copyImage("/Users/test01/AutoTest/workspace/cms-autotest/target/InstrumentDriver/log/SCArticle/Run 1/20151109_101335853_test010.png",
        // "/Users/test01/Desktop/FB3.jpg");
        String s = "/Users/test01/AutoTest/workspace/cms-autotest/test-output/Default suite";

        String newPath = s.replace("test-output/Default suite", "target/screenCaptures/" + 20151245 + ".png");
        System.out.println(newPath);
    }
}
