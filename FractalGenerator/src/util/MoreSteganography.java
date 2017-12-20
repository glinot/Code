/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Gregoire
 */
public class MoreSteganography {

    public static final byte ETX = 0x3;
    public static final byte STX = 0x2;
    
    public static final String COP = "ULTIMATE_FRACTAL_GENERATOR BY Raphael Schruoffeneger && Gregoire Linot Ultimate_Tech Corp ";

   
    public static void main(String[] args) throws IOException {
        BufferedImage buff = new BufferedImage(200, 200, 1);
        hideStringInBufferedImage(buff, COP);
        ImageIO.write(buff, "png", new File("D:\\dsd.png"));
    }
    public static void hideStringInBufferedImage(BufferedImage img, String str) {
        str+="\n"+COP;
        if ((2 + str.length()+COP.length()) * 8 < img.getWidth() * img.getHeight()) {
            byte[] toEncode = new byte[2 + str.length()+COP.length()];
            toEncode[0] = STX;
            for (int i = 0; i < str.length(); i++) {
                toEncode[i + 1] = (byte) str.charAt(i);

            }
            toEncode[toEncode.length - 1-COP.length()] = ETX;
            encodeByteInBufferedImage(img, toEncode);
        } else {
            System.err.println("Not enought place to encode String ");
        }
    }

    public static String getStringHiddenInBufferedImage(BufferedImage img) {
        int end = getPosOfEnd(img);
        if (isATextHidden(img) && end != -1) {
            byte[] bytes = readNByteFromBufferedImage(img, 1, end);
            return new String(bytes);
        }
        return "";
    }

    private static boolean isATextHidden(BufferedImage img) {
        return readByteFromBufferedImage(img, 0) == STX;
    }

    private static int getPosOfEnd(BufferedImage img) {
        int i = 0;
        while (i < img.getWidth() * img.getHeight() / 8) {
            byte b = 0;
            if ((b = readByteFromBufferedImage(img, i)) == ETX) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static byte[] readNByteFromBufferedImage(BufferedImage img, int start, int end) {
        byte[] res = new byte[end - start];
        for (int i = start; i < end; i++) {
            res[i - start] = readByteFromBufferedImage(img, i);
        }
        return res;
    }

    public static byte readByteFromBufferedImage(BufferedImage img, int pos) {
        byte res = 0x0;
        for (int i = 0; i < 8; i++) {
            Color color = new Color(img.getRGB((pos * 8 + i) % img.getWidth(), (pos * 8 + i) / img.getWidth()));
            byte r = (byte) (color.getRed() & 0xFF);
            res = setBitAt(res, i, getBitAt(r, 0) == 1);
        }
        return res;
    }

    public static void encodeByteInBufferedImage(BufferedImage image, byte[] b) {
        if (image.getHeight() * image.getWidth() < b.length * 8) {
            System.err.println("Not Enought places");

        } else {

            for (int i = 0; i < 8 * b.length; i += 8) {

                for (int j = 0; j < 8; j++) {
                    int x = (i + j) % image.getWidth(), y = (i + j) / image.getWidth();
                    Color col = new Color(image.getRGB(x, y));
                    char r = (char) (col.getRed());
                    
                    r = setBitAt(r, 0, getBitAt(b[i / 8], j) == 1);
                    try {
                     image.setRGB(x, y, new Color(r, col.getGreen(), col.getBlue()).getRGB());   
                    } catch (Exception e) {
                        System.out.println(r + "original "+(byte) (col.getRed()));
                    }
                    
                }
            }
        }
    }

    private static void printByteArray(byte[] arr) {
        for (byte b : arr) {
            System.out.println(getStringBitOfByte(b));
        }
    }

    private static byte[] decodeBytesHiddedInBytes(byte[] src) {
        if (src.length % 8 != 0) {
            return null;
        } else {
            byte[] res = new byte[src.length / 8];
            int pos = 0;
            for (int i = 0; i < src.length; i += 8) {
                byte toAdd = 0x0;
                for (int j = 0; j < 8; j++) {
                    byte b = src[i + j];
                    byte b1 = getBitAt(b, 0);
                    toAdd = setBitAt(toAdd, j, b1 == 1);
                }
                res[pos] = toAdd;
                pos++;

            }
            return res;
        }
    }

    private static void hideBytesInBytes(byte[] src, byte[] target) {
        if (src.length > target.length * 8) {
            return;
        } else {
            int pos = 0;
            for (byte u : src) {
                for (int i = 0; i < 8; i++) {
                    boolean bit = getBitAt(u, i) == 1;
                    target[pos] = setBitAt(target[pos], 0, bit);
                    pos++;

                }
            }

        }
    }

    private static byte getBitAt(byte b, int pos) {
        return (byte) ((b >> pos) & 1);
    }
    private static char getBitAt(char b, int pos) {
        return (char) ((b >> pos) & 1);
    }

    private static byte getByte(byte[] b) {
        for (int i = 0; i < 8; i++) {

        }
        return b[0];
    }

    private static String getStringBitOfByte(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    private static byte setBitAt(byte b, int bitIndex, boolean value) {

        if (value) {
            b = (byte) (b | (1 << bitIndex));

        } else {
            b = (byte) (b & ~(1 << bitIndex));
        }

        return b;

    }
     private static char setBitAt(char b, int bitIndex, boolean value) {

        if (value) {
            b = (char) (b | (1 << bitIndex));

        } else {
            b = (char) (b & ~(1 << bitIndex));
        }

        return b;

    }

    private static int setBitAt(int b, int bitIndex, boolean value) {

        if (value) {
            b = (byte) (b | (1 << bitIndex));

        } else {
            b = (byte) (b & ~(1 << bitIndex));
        }

        return b;

    }
}
