package mandelbrot.controller;

// @author Raphael
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import geometry.CenteredCircle;
import geometry.Vector2D;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.Timer;
import graphicalsystem.IntPoint;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import mandelbrot.gui.ProGUI;
import static mandelbrot.gui.ProGUI.BROWSE_SCREENSHOT_FOLDER;
import static mandelbrot.gui.ProGUI.COLORS_OPTION;
import static mandelbrot.gui.ProGUI.GO_TO_OPTION;
import static mandelbrot.gui.ProGUI.ITERATIONS_OPTION;
import static mandelbrot.gui.ProGUI.SCREENSHOT_OPTION;
import static mandelbrot.gui.ProGUI.VALIDATE_GO_TO;
import static mandelbrot.gui.ProGUI.VALIDATE_ITERATIONS;
import static mandelbrot.gui.ProGUI.VALIDATE_SCREENSHOT;
import mandelbrot.rendering.MdbRenderer;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import mandelbrot.gui.ColorGradientChooser;
import mandelbrot.gui.listeners.CurveListener;
import mandelbrot.gui.SplashScreen;
import mandelbrot.rendering.MdbRendererListener;
import mandelbrot.rendering.iterationcalculator.IterationCalculatorSimple;
import mandelbrot.rendering.parameterscalculator.ParameterCalculatorSimpleLimit;
import mandelbrot.rendering.parameterscalculator.ParameterCalculatorSimpleLog;
import mandelbrot.rendering.threading.MdbRendererThreaded;
import net.iharder.dnd.FileDrop;
import util.Files;
import util.MdbParsing;
import util.curves.Function2D;
import util.MoreMath;
import util.MoreSteganography;
import util.audio.MandelAudioPlayer;
import util.audio.MandelAudioPlayerListenner;
import util.audio.MinimLoader;
import util.curves.ManipulableFunction;
import util.curves.Spline;
import util.curves.SplineCubicLooped;
import util.network.TcpClient;


public class Controller implements ActionListener, MouseListener, MouseMotionListener,
        MouseWheelListener, WindowListener, ComponentListener, MdbRendererListener, CurveListener, FileDrop.Listener , MandelAudioPlayerListenner
{
    public static final int DEFAULT_MAX_ITER = 1000;
    public static final double DEFAULT_ZOOM = 1;
    public static final String LAST_SCREENSHOT_FIELD_NAME = "lastScreenshot";
    
    private final ProGUI proGui;
    private final MdbRenderer exploRenderer;
    private IntPoint mdbViewerMousePos;
    private double zoomMultiplier;
    private final Timer resizingTimer;
    private final JFileChooser screenshotFolderChooser;
    private String screenshotFolder;
    public BufferedImage lastScreenshot;
    private ColorPalette palette;
    
    
    /*Audio player +FFT for custom mandelAnimations */
    private MandelAudioPlayer playerAudio;
    
    public Controller()
    {
        palette = new ColorPalette(
                new SplineCubicLooped(new double[]{0.0,0.0625,0.125,0.1875,0.25,0.3125,0.375,0.4375,0.5,0.5625,0.625,0.6875,0.75,0.8125,0.9375}, new double[]{0.2578125,0.09765625,0.03515625,0.015625,0.0,0.046875,0.09375,0.22265625,0.5234375,0.82421875,0.94140625,0.96875,0.99609375,0.796875,0.4140625}, 0, 1), 0, 100,
                new SplineCubicLooped(new double[]{0.0,0.0625,0.125,0.1875,0.25,0.3125,0.375,0.4375,0.5,0.5625,0.625,0.6875,0.75,0.8125,0.9375}, new double[]{0.1171875,0.02734375,0.00390625,0.015625,0.02734375,0.171875,0.3203125,0.48828125,0.70703125,0.921875,0.91015625,0.78515625,0.6640625,0.5,0.203125}, 0, 1), 0, 100,
                new SplineCubicLooped(new double[]{0.0,0.0625,0.125,0.1875,0.25,0.3125,0.375,0.4375,0.5,0.5625,0.625,0.6875,0.75,0.8125,0.9375}, new double[]{0.05859375,0.1015625,0.18359375,0.28515625,0.390625,0.5390625,0.69140625,0.81640625,0.89453125,0.96875,0.74609375,0.37109375,0.0,0.0,0.01171875}, 0, 1), 0, 100,
                false);
        exploRenderer =
       new MdbRendererThreaded(
               new ZoomedFrame(MdbRenderer.DEFAULT_INITIAL_FRAME,
                       MdbRenderer.DEFAULT_INITIAL_FRAME.getCenter(),DEFAULT_ZOOM),
               0,0,
               new IterationCalculatorSimple(new CenteredCircle(2), DEFAULT_MAX_ITER),
               new ParameterCalculatorSimpleLog(),
               palette);
        exploRenderer.addMdbRendererListener(this);
        
        mdbViewerMousePos = null;
        zoomMultiplier = 2;
        resizingTimer = new Timer(1, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                exploRenderer.renderImage();
                //proGui.getProMainViewer().repaint();
                updateInfo();
            }
        });
        resizingTimer.setRepeats(false);
        
        screenshotFolderChooser = new JFileChooser();
        screenshotFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        screenshotFolderChooser.setDialogTitle("Sélectionner le dossier des captures");
        //screenshotFolder = System.getProperty("user.dir");
        screenshotFolder = System.getProperty("user.home")
                + File.separatorChar + "Pictures"
                + File.separatorChar + "Captures Fractale Mandelbrot";
        System.out.println(screenshotFolder);
        lastScreenshot = null;
        
        proGui = new ProGUI(this);
        if(exploRenderer instanceof MdbRendererThreaded){
            ((MdbRendererThreaded)exploRenderer).addMdbRendererListener(this);
        }
        this.playerAudio = new MandelAudioPlayer(50);
        new FileDrop(proGui, this);
       }
    
    public static void main(String[] args) throws InterruptedException
    {
        try
        {
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(ProGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        Controller controller = new Controller();
        SplashScreen.startDefaultSplashScreen();
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                controller.proGui.pack();
                controller.proGui.setVisible(true);
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            // update optionPanes display
            case GO_TO_OPTION :
                ((CardLayout)proGui.getOptionPanes().getLayout())
                    .show(proGui.getOptionPanes(), e.getActionCommand());
                break;
            case ITERATIONS_OPTION :
                ((CardLayout)proGui.getOptionPanes().getLayout())
                    .show(proGui.getOptionPanes(), e.getActionCommand());
                break;
            case SCREENSHOT_OPTION :
                ((CardLayout)proGui.getOptionPanes().getLayout())
                    .show(proGui.getOptionPanes(), e.getActionCommand());
                break;
            case COLORS_OPTION :
                //Spline s = new Spline(new double[]{.1,.3,.5,.7,.9}, new double[]{.4,.2,.8,.1,.5}, new int[]{1,0,3,0});
                proGui.getColorComp1GradientChooser().setCurve(palette.getComp1Function());
                proGui.getColorComp2GradientChooser().setCurve(palette.getComp2Function());
                proGui.getColorComp3GradientChooser().setCurve(palette.getComp3Function());
                ((CardLayout)proGui.getOptionPanes().getLayout())
                    .show(proGui.getOptionPanes(), e.getActionCommand());
                
                java.awt.EventQueue.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateColorGradientViewer();
                    }
                });
                
                //Spline s2 = new Spline(new Double[]{1,3,5,7,9}, new Double[]{4,2,8,1,5}, new Integer[]{1,0,3,0});
                //proGui.getCurveEditor().setFunction(s2);
                //proGui.getCurveEditor().repaint();
                break;
            
            // update Mandelbrot center and zoom
            case VALIDATE_GO_TO :
                exploRenderer.getCurrentFrame().getCenter().setCoord(
                        Double.parseDouble(proGui.getxTextField().getText()),
                        Double.parseDouble(proGui.getyTextField().getText()));
                exploRenderer.getCurrentFrame().setHorZoom(
                        Double.parseDouble(proGui.getZoomTextField().getText()));
                exploRenderer.computeImage();
                updateInfo();
                break;
            
            // update config max iteration
            case VALIDATE_ITERATIONS :
                exploRenderer.getIterationCalculator().setMaxIter(
                        Integer.parseInt(proGui.getIterationsTextField().getText()));
                exploRenderer.computeImage();
                updateInfo();
                break;
            
            case BROWSE_SCREENSHOT_FOLDER :
                int answer = screenshotFolderChooser.showOpenDialog(null);
                
                if(answer == JFileChooser.APPROVE_OPTION)
                {
                    screenshotFolder = screenshotFolderChooser.getSelectedFile().getAbsolutePath();
                    proGui.getScreenshotFolderLabel().setText("Dossier actuel : "+screenshotFolder);
                }
                break;
            
            case VALIDATE_SCREENSHOT :
                String rendererStr=MdbParsing.stringEncodeConfigGeneration(exploRenderer);
                System.out.println(rendererStr);
                MoreSteganography.hideStringInBufferedImage(exploRenderer.getMdbImage(), rendererStr);
                Files.safelySaveScreenshot(exploRenderer.getMdbImage(), screenshotFolder);
                lastScreenshot = exploRenderer.getMdbImage();
                proGui.getLastScreenshotDisplayer().repaint();
                break;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getSource() == proGui.getProMainViewer()
                && (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3))
        {
            exploRenderer.computeZoomedImage(e.getX(), e.getY(), zoomMultiplier, e.getButton() == MouseEvent.BUTTON1);
            updateInfo();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        if(e.getSource() == proGui.getProMainViewer())
        {
            mdbViewerMousePos = null;
            proGui.getProMainViewer().repaint();
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(e.getSource() == proGui.getProMainViewer())
        {
            mdbViewerMousePos = new IntPoint(e.getX(), e.getY());
            proGui.getProMainViewer().repaint();
        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(e.getSource() == proGui.getProMainViewer())
        {
            double fact;
            
            if(e.isControlDown())
                fact = 0.1;
            else if(e.isAltDown())
                fact = 10;
            else
                fact = 1;
            
            zoomMultiplier -= e.getWheelRotation()*fact;
            zoomMultiplier = MoreMath.limit(zoomMultiplier, 1, 100);
            proGui.getProMainViewer().repaint();
        }
    }
    
    @Override
    public void windowOpened(WindowEvent e)
    {
        
    }
    
    @Override
    public void windowClosing(WindowEvent e)
    {
        
    }
    
    @Override
    public void windowClosed(WindowEvent e)
    {
        
    }
    
    @Override
    public void windowIconified(WindowEvent e)
    {
        
    }
    
    @Override
    public void windowDeiconified(WindowEvent e)
    {
        
    }
    
    @Override
    public void windowActivated(WindowEvent e)
    {
        
    }
    
    @Override
    public void windowDeactivated(WindowEvent e)
    {
        
    }
    
    @Override
    public void componentResized(ComponentEvent e)
    {
        if(proGui != null && proGui.getProMainViewer() != null
                && e.getSource() == proGui.getProMainViewer())
        {
            exploRenderer.setWidth(proGui.getProMainViewer().getWidth());
            exploRenderer.setHeight(proGui.getProMainViewer().getHeight());
            resizingTimer.stop();
            resizingTimer.start();
        }
    }
    
    @Override
    public void componentMoved(ComponentEvent e)
    {
        
    }
    
    @Override
    public void componentShown(ComponentEvent e)
    {
        
    }
    
    @Override
    public void componentHidden(ComponentEvent e)
    {
        
    }
    
    public void updateInfo()
    {
        ZoomedFrame zF = exploRenderer.getCurrentFrame();
        proGui.getInfoLabel().setText("x = "+zF.getCenter().getX()
                + ", y = "+zF.getCenter().getY()
                + ", zoom = "+zF.getHorZoom());
    }
    
    public ProGUI getProGui() {
        return proGui;
    }
    
    public MdbRenderer getExploRenderer() {
        return exploRenderer;
    }

    public IntPoint getMdbViewerMousePos() {
        return mdbViewerMousePos;
    }

    public double getZoomMultiplier() {
        return zoomMultiplier;
    }

    public Timer getResizingTimer() {
        return resizingTimer;
    }

    public JFileChooser getScreenshotFolderChooser() {
        return screenshotFolderChooser;
    }

    public String getScreenshotFolder() {
        return screenshotFolder;
    }

    public BufferedImage getLastScreenshot() {
        return lastScreenshot;
    }
    
    @Override
    public void canRepaint()
    {
        proGui.getProMainViewer().repaint();
    }

    public ColorPalette getPalette() {
        return palette;
    }
    
    @Override
    public void updateProgessBar(double i) {
        int value=(int)(i*100);
        proGui.getProgressBar().setValue(value);
        proGui.getProgressBar().repaint();
        
    }
    
    @Override
    public void curveChanged(ChangeEvent e)
    {
        if(e.getSource() == proGui.getColorComp1GradientChooser()
        || e.getSource() == proGui.getColorComp2GradientChooser()
        || e.getSource() == proGui.getColorComp3GradientChooser())
        {
            updateColorGradientViewer();
        }
        resizingTimer.stop(); resizingTimer.start();
    }
    
    public void updateColorGradientViewer()
    {
        Function2D f1 = palette.getComp1Function(),
                   f2 = palette.getComp2Function(),
                   f3 = palette.getComp3Function();
        Graphics g = proGui.getColorGradientViewer().getGraphics();
        float comp1, comp2, comp3;
        int viewerWidth = proGui.getColorGradientViewer().getWidth(),
            viewerHeight = proGui.getColorGradientViewer().getHeight(),
            rgb;
        
        for(int i = 0; i < viewerWidth; i++)
        {
            comp1 = (float)f1.eval((double)i/viewerWidth).doubleValue();
            comp2 = (float)f2.eval((double)i/viewerWidth).doubleValue();
            comp3 = (float)f3.eval((double)i/viewerWidth).doubleValue();
            
            g.setColor(ColorPalette.createColor(comp1, comp2, comp3, palette.isHSBMode()));
            g.drawLine(i, 0, i, viewerHeight-1);
        }
    }
    
    @Override
    public void filesDropped(File[] files) {
        if (files != null) {
            if (files.length >= 1) {
                try {
                    File f = files[0];
                    if (!(f.getName().toLowerCase().endsWith("mp3") || f.getName().toLowerCase().endsWith("png") || f.getName().toLowerCase().endsWith("bmp"))) {
                        JOptionPane.showMessageDialog(null, "Ce n'est pas une image en mp3 ou bmp ou une musique en mp3", "Erreur", JOptionPane.ERROR_MESSAGE);

                    }
                    if (f.getName().endsWith("png") || f.getName().endsWith("bmp")) {
                        String st = MoreSteganography.getStringHiddenInBufferedImage(ImageIO.read(f));
                        //  System.out.println("" + st);
                        if (!(st == null)) {
                            try {
                                ZoomedFrame fr = MdbParsing.parseZoomedFrame(st);
                                IterationCalculatorSimple it = new IterationCalculatorSimple(new CenteredCircle(2), MdbParsing.parseIterationCalculator(st).getMaxIter());
                                ColorPalette p = MdbParsing.parseColorPalette(st);
                                if (fr != null && it != null && p != null) {
                                    this.exploRenderer.setCurrentFrame(fr);
                                    this.exploRenderer.setIterationCalculator(it);
                                    this.exploRenderer.getPalette().setComp1Function(p.getComp1Function());
                                    this.exploRenderer.getPalette().setComp2Function(p.getComp2Function());
                                    this.exploRenderer.getPalette().setComp3Function(p.getComp3Function());
                                    proGui.getColorComp1GradientChooser().setCurve(p.getComp1Function());
                                    proGui.getColorComp2GradientChooser().setCurve(p.getComp2Function());
                                    proGui.getColorComp3GradientChooser().setCurve(p.getComp3Function());
                                    proGui.getColorComp1GradientChooser().repaint();
                                    proGui.getColorComp2GradientChooser().repaint();
                                    proGui.getColorComp3GradientChooser().repaint();
                                    this.exploRenderer.computeImage();
                                    updateInfo();
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Désolé,ce n'est pas une image de Mandelbrot, ou elle a été modifiée ", "Erreur", JOptionPane.ERROR_MESSAGE);
                            }

                        }
                    }
                    if (f.getName().endsWith("mp3") && f.exists()) {
                        playerAudio.load(f);
                        playerAudio.start();
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Erreur de lecture");
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }

    @Override
    public void onNewFFT(FFT fft) {
    
        final int N = 10;
        double [] a  = new double [N];
        for (int i = 0; i < N; i++) {
            a[i] = fft.getBand(i);
            
        }
        this.proGui.getColorComp1GradientChooser().
    
    
    }
}
       