/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import geometry.CenteredCircle;
import geometry.Vector2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import mandelbrot.rendering.MdbRenderer;
import mandelbrot.rendering.iterationcalculator.IterationCalculator;
import mandelbrot.rendering.iterationcalculator.IterationCalculatorSimple;
import mandelbrot.rendering.parameterscalculator.ParameterCalculatorSimple;
import mandelbrot.rendering.parameterscalculator.ParameterCalculatorSimpleLog;
import mandelbrot.rendering.threading.MdbRendererThreaded;
import util.curves.SplineCubicLooped;

/**
 *
 * @author Gregoire
 */
public class MdbParsing {

    public static final String CLASS_START = "START_";
    public static final String CLASS_END = "END_";
    public static final String SUBCLASS_OFFSET = "   ";

    private static String encodeZoomedFrame(ZoomedFrame fra) {
        String res = CLASS_START + "ZoomedFrame" + "\n";
        res += "Zoom=" + fra.getZoom() + "\n";
        res += "CenterX=" + fra.getCenter().getX() + "\n";
        res += "CenterY=" + fra.getCenter().getY() + "\n";
        res += CLASS_END + "ZoomedFrame";
        return res;
    }

    private static String encodeIterationCalculator(IterationCalculator calc) {
        String res = CLASS_START + "IterationCalculator" + "\n";
        res += "MaxIter=" + calc.getMaxIter() + "\n";
        res += CLASS_END + "IterationCalculator";
        return res;
    }

    private static String encodeColorPalette(ColorPalette colorPalette) {
        String res = CLASS_START + "ColorPalette" + "\n";
        res += "MdbSetColor=" + colorPalette.getMdbSetColor() + "\n";
        res += "HsbMode=" + colorPalette.isHSBMode() + "\n";
        res += "comp1StartX=" + colorPalette.getComp1StartX() + "\n";
        res += "comp2StartX=" + colorPalette.getComp2StartX() + "\n";
        res += "comp3StartX=" + colorPalette.getComp3StartX() + "\n";
        res += "comp1EndX=" + colorPalette.getComp1EndX() + "\n";
        res += "comp2EndX=" + colorPalette.getComp2EndX() + "\n";
        res += "comp3EndX=" + colorPalette.getComp3EndX() + "\n";
        res += encodeSplineCubicLooped(((SplineCubicLooped) colorPalette.getComp1Function()), 1) + "\n";
        res += encodeSplineCubicLooped((SplineCubicLooped) colorPalette.getComp2Function(), 2) + "\n";
        res += encodeSplineCubicLooped((SplineCubicLooped) colorPalette.getComp3Function(), 3) + "\n";
        res += CLASS_END + "ColorPalette";
        return res;
    }
    
    private static String encodeSplineCubicLooped(SplineCubicLooped loop, int NB) {
        String res = CLASS_START + "SplineCubicLooped_" + NB + "\n";
        res += "loopStart=" + loop.getLoopStart() + "\n";
        res += "loopEnd=" + loop.getLoopEnd() + "\n";
        
        // harvest loop points
        double [] xTab = new double[loop.getN()];
        double [] yTab = new double[loop.getN()];
        for (int i = 0; i < loop.getN(); i++) {
            xTab[i]=loop.getX(i);
            yTab[i]=loop.getY(i);
        }
        res += "pointsX=" + tabToString(xTab) + "\n";
        res += "pointsY=" + tabToString(yTab) + "\n";
        res += CLASS_END + "SplineCubicLooped_" + NB;
        return res;

    }

    private static String tabToString(double[] t) {
        String res = "";
        for (double u : t) {
            res += u + " ";
        }
        return res;
    }

    public static MdbRenderer makeRendererFromString(String str) {
        return null;
    }

    private static String[] subArray(String[] arr, int start, int end) {
        if (start < end && start >= 0 && end < arr.length) {
            String[] res = new String[end - start];
            for (int i = start; i < end; i++) {
                res[i - start] = arr[i];
                return res;
            }
        }
        return null;
    }

    private static ArrayList<String> findAllClassesBeginningWith(String className, String[] toParse) {
        ArrayList<Integer> starts = findAllLinesBeginingWith(CLASS_START + className, toParse);
        ArrayList<Integer> ends = findAllLinesBeginingWith(CLASS_END + className, toParse);
        ArrayList<String> res = new ArrayList<>();
        if (ends.size() == starts.size()) {
            for (int i = 0; i < ends.size(); i++) {
                String resS = "";
                int start = starts.get(i);
                int end = ends.get(i);
                for (int j = start + 1; j < end; j++) {
                    resS += toParse[j] + "\n";
                }
                res.add(resS);
            }
        }
        return res;

    }

    private static ArrayList<Integer> findAllLinesBeginingWith(String name, String[] t) {
        ArrayList<Integer> ints = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            if (t[i].startsWith(name)) {
                ints.add(i);
            }
        }
        return ints;
    }

    private static String findClassString(String className, String[] toParse) {
        int start = findLineBeginingWith(CLASS_START + className, toParse);
        int end = findLineBeginingWith(CLASS_END + className, toParse);
        if (start != -1 && end != -1) {
            String res = "";
            for (int i = start + 1; i < end; i++) {
                res += toParse[i] + "\n";
            }
            return res;
        }

        return null;
    }

    private static int findLineBeginingWith(String toSearch, String[] t) {
        if (t == null) {
            return -1;
        }
        for (int i = 0; i < t.length; i++) {
            if (!(t[i] == null)) {
                if (t[i].startsWith(toSearch)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String getAttribute(String attrName, String[] t) {
        int pos = findLineBeginingWith(attrName + "=", t);
        if (pos != -1) {
            return t[pos].replace(attrName + "=", "");
        }
        return null;
    }

    public static String stringEncodeConfigGeneration(MdbRenderer renderer) {
        String res = "";
        res += encodeZoomedFrame(renderer.getCurrentFrame()) + "\n";
        res += encodeColorPalette(renderer.getPalette()) + "\n";
        res += encodeIterationCalculator(renderer.getIterationCalculator());
        return res;
    }

    private static double getDoubleAttribute(String name, String[] t) {
        return Double.parseDouble(getAttribute(name, t));
    }

    public static SplineCubicLooped parseSplineCubicLooped(String z) {
        String t[] = z.split("\n");
        try {

            return new SplineCubicLooped(
                    parseDoubleArray(getAttribute("pointsX", t)),
                    parseDoubleArray(getAttribute("pointsY", t)),
                    getDoubleAttribute("loopStart", t),
                    getDoubleAttribute("loopEnd", t));
        } catch (Exception e) {
            return null;
        }
    }

    private static double[] parseDoubleArray(String arr) {
        String[] t = arr.split(" ");
        double[] res = new double[t.length];
        for (int i = 0; i < t.length; i++) {
            res[i] =Double.parseDouble(t[i]);
        }
        return res;
    }

    public static ColorPalette parseColorPalette(String z) {
        String cpS = findClassString("ColorPalette", z.split("\n"));
        String t[] = cpS.split("\n");
        try {
            return new ColorPalette(
                    parseSplineCubicLooped(findClassString("SplineCubicLooped_1", t)), getDoubleAttribute("comp1StartX", t), getDoubleAttribute("comp1EndX", t),
                    parseSplineCubicLooped(findClassString("SplineCubicLooped_2", t)), getDoubleAttribute("comp2StartX", t), getDoubleAttribute("comp2EndX", t),
                    parseSplineCubicLooped(findClassString("SplineCubicLooped_3", t)), getDoubleAttribute("comp3StartX", t), getDoubleAttribute("comp3EndX", t),
                    Boolean.parseBoolean(getAttribute("HsbMode", t)));
        } catch (Exception e) {
            return null;
        }
    }

    public static ZoomedFrame parseZoomedFrame(String z) {
        String zFS = findClassString("ZoomedFrame",z.split("\n"));
        String t[] = zFS.split("\n");
        String zoomS = getAttribute("Zoom", t);
        String centerXS = getAttribute("CenterX", t);
        String centerYS = getAttribute("CenterY", t);
        System.out.println(zoomS + "  " + centerXS + " " + centerYS);
        double zoom, centerX, centerY;
        try {
            zoom = Double.parseDouble(zoomS);
            centerX = Double.parseDouble(centerXS);
            centerY = Double.parseDouble(centerYS);
        } catch (Exception e) {
            System.out.println("Erro");
            return null;
        }
        return new ZoomedFrame(MdbRenderer.DEFAULT_INITIAL_FRAME, new Vector2D(centerX, centerY), zoom);
    }

    public static IterationCalculator parseIterationCalculator(String it) {
        
        String t[] = findClassString("IterationCalculator", it.split("\n")).split("\n");
        String maxIter = getAttribute("MaxIter", t);
        try {
            return new IterationCalculatorSimple(new CenteredCircle(2), Integer.parseInt(maxIter));
        } catch (Exception e) {
            return null;
        }
    }

   
    public static void main(String[] args) throws IOException {
        ZoomedFrame fra = new ZoomedFrame(MdbRenderer.DEFAULT_INITIAL_FRAME);
        IterationCalculatorSimple s = new IterationCalculatorSimple(new CenteredCircle(2), 100);
        ColorPalette palette = new ColorPalette(
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.2578125, 0.09765625, 0.03515625, 0.015625, 0.0, 0.046875, 0.09375, 0.22265625, 0.5234375, 0.82421875, 0.94140625, 0.96875, 0.99609375, 0.796875, 0.4140625}, 0, 1), 0, 100,
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.1171875, 0.02734375, 0.00390625, 0.015625, 0.02734375, 0.171875, 0.3203125, 0.48828125, 0.70703125, 0.921875, 0.91015625, 0.78515625, 0.6640625, 0.5, 0.203125}, 0, 1), 0, 100,
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.05859375, 0.1015625, 0.18359375, 0.28515625, 0.390625, 0.5390625, 0.69140625, 0.81640625, 0.89453125, 0.96875, 0.74609375, 0.37109375, 0.0, 0.0, 0.01171875}, 0, 1), 0, 100,
                false);
        fra.setZoom(1000);
        MdbRenderer ren = new MdbRendererThreaded(fra, 1000, 1000, s, new ParameterCalculatorSimpleLog(), palette);

        String toParse = stringEncodeConfigGeneration(ren);
        System.out.println(parseZoomedFrame(toParse));
        System.out.println(parseColorPalette(toParse));
    }
}
