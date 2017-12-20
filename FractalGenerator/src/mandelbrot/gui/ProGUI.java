package mandelbrot.gui;

// @author Raphaël

import geometry.StraightRectangle;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mandelbrot.controller.Controller;


public class ProGUI extends javax.swing.JFrame
{
    public static final String 
            GO_TO_OPTION = "GO_TO_OPTION",
            ITERATIONS_OPTION = "ITERATIONS_OPTION",
            COLORS_OPTION = "COLORS_OPTION",
            SCREENSHOT_OPTION = "SCREENSHOT_OPTION",
            BROWSE_SCREENSHOT_FOLDER = "BROWSE_SCREENSHOT_FOLDER",
            VALIDATE_GO_TO = "VALIDATE_GO_TO",
            VALIDATE_ITERATIONS = "VALIDATE_ITERATIONS",
            VALIDATE_COLORS = "VALIDATE_COLORS",
            VALIDATE_SCREENSHOT = "VALIDATE_SCREENSHOT";
    
    private final Controller controller;
    
    public ProGUI(Controller controller)
    {
        this.controller = controller;
        initComponents();
        addWindowListener(controller);
        
        try
        {
            for(Field field : getClass().getDeclaredFields())
            {
                if(field.getGenericType() == javax.swing.JButton.class)
                {
                    ((javax.swing.JButton)field.get(this)).addActionListener(controller);
                }
            }
        }
        catch(IllegalArgumentException | IllegalAccessException ex)
        {
            Logger.getLogger(ProGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ((ColorGradientChooser)colorComp1GradientChooser).setTranslatable(true);
        ((ColorGradientChooser)colorComp1GradientChooser).setScalable(true);
         
        
        ((ColorGradientChooser)colorComp1GradientChooser).addCurveListener(controller);
        ((ColorGradientChooser)colorComp2GradientChooser).addCurveListener(controller);
        ((ColorGradientChooser)colorComp3GradientChooser).addCurveListener(controller);
      
                
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPanes = new javax.swing.JTabbedPane();
        explorationToolBar = new javax.swing.JPanel();
        goToButton = new javax.swing.JButton();
        iterationsButton = new javax.swing.JButton();
        colorsButton = new javax.swing.JButton();
        screenshotButton = new javax.swing.JButton();
        simulationToolBar = new javax.swing.JPanel();
        optionPanes = new javax.swing.JPanel();
        goToOptionPane = new javax.swing.JPanel();
        xTextField = new javax.swing.JTextField();
        yTextField = new javax.swing.JTextField();
        zoomTextField = new javax.swing.JTextField();
        zoomLabel = new javax.swing.JLabel();
        yLabel = new javax.swing.JLabel();
        xLabel = new javax.swing.JLabel();
        validateGoToButton = new javax.swing.JButton();
        iterationsOptionPane = new javax.swing.JPanel();
        iterationsTextField = new javax.swing.JTextField();
        iterationsLabel = new javax.swing.JLabel();
        estimatedTimeLabel1 = new javax.swing.JLabel();
        estimatedTimeLabel2 = new javax.swing.JLabel();
        validateIterationsButton = new javax.swing.JButton();
        colorsOptionPane = new javax.swing.JPanel();
        colorComp1GradientChooser = new mandelbrot.gui.ColorGradientChooser(controller.getPalette().getComp1Function(), controller.getPalette().isRGBMode() ? mandelbrot.config.color.ColorPalette.ColorComponent.red : mandelbrot.config.color.ColorPalette.ColorComponent.hue);
        colorComp2GradientChooser = new mandelbrot.gui.ColorGradientChooser(controller.getPalette().getComp2Function(), controller.getPalette().isRGBMode() ? mandelbrot.config.color.ColorPalette.ColorComponent.green : mandelbrot.config.color.ColorPalette.ColorComponent.saturation);
        colorComp3GradientChooser = new mandelbrot.gui.ColorGradientChooser(controller.getPalette().getComp3Function(), controller.getPalette().isRGBMode() ? mandelbrot.config.color.ColorPalette.ColorComponent.blue : mandelbrot.config.color.ColorPalette.ColorComponent.brightness);
        colorGradientViewer = new javax.swing.JPanel();
        screenshotOptionPane = new javax.swing.JPanel();
        lastScreenshotDisplayer = new ImageDisplayer(controller, Controller.LAST_SCREENSHOT_FIELD_NAME);
        validateScreenshotButton = new javax.swing.JButton();
        screenshotFolderBrowserButton = new javax.swing.JButton();
        screenshotFolderLabel = new javax.swing.JLabel();
        overviewPane = new javax.swing.JPanel();
        proMainViewer = new ProMainViewer(controller);
        statusBar = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        infoLabel = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new java.awt.Dimension(640, 480));

        goToButton.setText("Aller à");
        goToButton.setActionCommand(GO_TO_OPTION);
        goToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToButtonActionPerformed(evt);
            }
        });

        iterationsButton.setText("Itérations");
        iterationsButton.setActionCommand(ITERATIONS_OPTION);

        colorsButton.setText("Couleurs");
        colorsButton.setActionCommand(COLORS_OPTION);

        screenshotButton.setText("Capture");
        screenshotButton.setActionCommand(SCREENSHOT_OPTION);

        javax.swing.GroupLayout explorationToolBarLayout = new javax.swing.GroupLayout(explorationToolBar);
        explorationToolBar.setLayout(explorationToolBarLayout);
        explorationToolBarLayout.setHorizontalGroup(
            explorationToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorationToolBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goToButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iterationsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(screenshotButton)
                .addGap(0, 541, Short.MAX_VALUE))
        );
        explorationToolBarLayout.setVerticalGroup(
            explorationToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorationToolBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(explorationToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goToButton)
                    .addComponent(iterationsButton)
                    .addComponent(colorsButton)
                    .addComponent(screenshotButton))
                .addContainerGap())
        );

        tabbedPanes.addTab("Exploration", explorationToolBar);

        javax.swing.GroupLayout simulationToolBarLayout = new javax.swing.GroupLayout(simulationToolBar);
        simulationToolBar.setLayout(simulationToolBarLayout);
        simulationToolBarLayout.setHorizontalGroup(
            simulationToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 857, Short.MAX_VALUE)
        );
        simulationToolBarLayout.setVerticalGroup(
            simulationToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        tabbedPanes.addTab("Simulation", simulationToolBar);

        optionPanes.setLayout(new java.awt.CardLayout());

        goToOptionPane.setBackground(new java.awt.Color(51, 255, 204));

        xTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        xTextField.setText(Double.toString(util.MoreMath.round(controller.getExploRenderer().getCurrentFrame().getCenter().getX(), 10)));

        yTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        yTextField.setText(Double.toString(util.MoreMath.round(controller.getExploRenderer().getCurrentFrame().getCenter().getY(), 10)));

        zoomTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        zoomTextField.setText(Double.toString(util.MoreMath.round(controller.getExploRenderer().getCurrentFrame().getZoom(), 10)));

        zoomLabel.setText("zoom :");

        yLabel.setText("y :");

        xLabel.setText("x :");

        validateGoToButton.setText("Aller");
        validateGoToButton.setActionCommand(VALIDATE_GO_TO);

        javax.swing.GroupLayout goToOptionPaneLayout = new javax.swing.GroupLayout(goToOptionPane);
        goToOptionPane.setLayout(goToOptionPaneLayout);
        goToOptionPaneLayout.setHorizontalGroup(
            goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(goToOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(goToOptionPaneLayout.createSequentialGroup()
                        .addGroup(goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(zoomLabel)
                            .addComponent(yLabel)
                            .addComponent(xLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(xTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(yTextField)
                            .addComponent(zoomTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, goToOptionPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(validateGoToButton)))
                .addContainerGap())
        );
        goToOptionPaneLayout.setVerticalGroup(
            goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(goToOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(goToOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zoomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zoomLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 246, Short.MAX_VALUE)
                .addComponent(validateGoToButton)
                .addContainerGap())
        );

        optionPanes.add(goToOptionPane, "GO_TO_OPTION");

        iterationsOptionPane.setBackground(new java.awt.Color(204, 255, 51));

        iterationsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        iterationsTextField.setText(Integer.toString(Controller.DEFAULT_MAX_ITER));

        iterationsLabel.setText("<html>Nombre<br>&nbsp;&nbsp;d'itérations   :</html>");

        estimatedTimeLabel1.setText("<html>Temps de<br>calcul estimé :</html>");

        estimatedTimeLabel2.setText("0.01 s");

        validateIterationsButton.setText("Regénérer");
        validateIterationsButton.setActionCommand(VALIDATE_ITERATIONS);

        javax.swing.GroupLayout iterationsOptionPaneLayout = new javax.swing.GroupLayout(iterationsOptionPane);
        iterationsOptionPane.setLayout(iterationsOptionPaneLayout);
        iterationsOptionPaneLayout.setHorizontalGroup(
            iterationsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iterationsOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(iterationsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(iterationsOptionPaneLayout.createSequentialGroup()
                        .addComponent(iterationsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iterationsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                    .addGroup(iterationsOptionPaneLayout.createSequentialGroup()
                        .addComponent(estimatedTimeLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(estimatedTimeLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, iterationsOptionPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(validateIterationsButton)))
                .addContainerGap())
        );
        iterationsOptionPaneLayout.setVerticalGroup(
            iterationsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iterationsOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(iterationsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(iterationsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iterationsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(iterationsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(estimatedTimeLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(estimatedTimeLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 256, Short.MAX_VALUE)
                .addComponent(validateIterationsButton)
                .addContainerGap())
        );

        optionPanes.add(iterationsOptionPane, "ITERATIONS_OPTION");

        colorsOptionPane.setBackground(new java.awt.Color(102, 102, 255));

        colorComp1GradientChooser.setBackground(new java.awt.Color(255, 255, 0));

        javax.swing.GroupLayout colorComp1GradientChooserLayout = new javax.swing.GroupLayout(colorComp1GradientChooser);
        colorComp1GradientChooser.setLayout(colorComp1GradientChooserLayout);
        colorComp1GradientChooserLayout.setHorizontalGroup(
            colorComp1GradientChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        colorComp1GradientChooserLayout.setVerticalGroup(
            colorComp1GradientChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );

        colorComp2GradientChooser.setBackground(new java.awt.Color(255, 255, 0));

        javax.swing.GroupLayout colorComp2GradientChooserLayout = new javax.swing.GroupLayout(colorComp2GradientChooser);
        colorComp2GradientChooser.setLayout(colorComp2GradientChooserLayout);
        colorComp2GradientChooserLayout.setHorizontalGroup(
            colorComp2GradientChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        colorComp2GradientChooserLayout.setVerticalGroup(
            colorComp2GradientChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        colorComp3GradientChooser.setBackground(new java.awt.Color(255, 255, 0));

        javax.swing.GroupLayout colorComp3GradientChooserLayout = new javax.swing.GroupLayout(colorComp3GradientChooser);
        colorComp3GradientChooser.setLayout(colorComp3GradientChooserLayout);
        colorComp3GradientChooserLayout.setHorizontalGroup(
            colorComp3GradientChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        colorComp3GradientChooserLayout.setVerticalGroup(
            colorComp3GradientChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        colorGradientViewer.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout colorGradientViewerLayout = new javax.swing.GroupLayout(colorGradientViewer);
        colorGradientViewer.setLayout(colorGradientViewerLayout);
        colorGradientViewerLayout.setHorizontalGroup(
            colorGradientViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        colorGradientViewerLayout.setVerticalGroup(
            colorGradientViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout colorsOptionPaneLayout = new javax.swing.GroupLayout(colorsOptionPane);
        colorsOptionPane.setLayout(colorsOptionPaneLayout);
        colorsOptionPaneLayout.setHorizontalGroup(
            colorsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorsOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(colorsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorComp1GradientChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorComp2GradientChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorComp3GradientChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorGradientViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        colorsOptionPaneLayout.setVerticalGroup(
            colorsOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorsOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(colorComp1GradientChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorComp2GradientChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorComp3GradientChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorGradientViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        optionPanes.add(colorsOptionPane, "COLORS_OPTION");

        screenshotOptionPane.setBackground(new java.awt.Color(102, 255, 102));

        javax.swing.GroupLayout lastScreenshotDisplayerLayout = new javax.swing.GroupLayout(lastScreenshotDisplayer);
        lastScreenshotDisplayer.setLayout(lastScreenshotDisplayerLayout);
        lastScreenshotDisplayerLayout.setHorizontalGroup(
            lastScreenshotDisplayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        lastScreenshotDisplayerLayout.setVerticalGroup(
            lastScreenshotDisplayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        validateScreenshotButton.setText("Sauvegarder capture");
        validateScreenshotButton.setActionCommand(VALIDATE_SCREENSHOT);

        screenshotFolderBrowserButton.setText("Choisir dossier");
        screenshotFolderBrowserButton.setActionCommand(BROWSE_SCREENSHOT_FOLDER);

        screenshotFolderLabel.setText("Dossier actuel : " + controller.getScreenshotFolder());

        javax.swing.GroupLayout screenshotOptionPaneLayout = new javax.swing.GroupLayout(screenshotOptionPane);
        screenshotOptionPane.setLayout(screenshotOptionPaneLayout);
        screenshotOptionPaneLayout.setHorizontalGroup(
            screenshotOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(screenshotOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(screenshotOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastScreenshotDisplayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, screenshotOptionPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(validateScreenshotButton))
                    .addGroup(screenshotOptionPaneLayout.createSequentialGroup()
                        .addGroup(screenshotOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(screenshotFolderBrowserButton)
                            .addComponent(screenshotFolderLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        screenshotOptionPaneLayout.setVerticalGroup(
            screenshotOptionPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(screenshotOptionPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lastScreenshotDisplayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(screenshotFolderBrowserButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(screenshotFolderLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(validateScreenshotButton)
                .addContainerGap())
        );

        optionPanes.add(screenshotOptionPane, "SCREENSHOT_OPTION");

        overviewPane.setBackground(new java.awt.Color(255, 102, 102));

        javax.swing.GroupLayout overviewPaneLayout = new javax.swing.GroupLayout(overviewPane);
        overviewPane.setLayout(overviewPaneLayout);
        overviewPaneLayout.setHorizontalGroup(
            overviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        overviewPaneLayout.setVerticalGroup(
            overviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
        );

        proMainViewer.setBackground(new java.awt.Color(153, 255, 153));

        javax.swing.GroupLayout proMainViewerLayout = new javax.swing.GroupLayout(proMainViewer);
        proMainViewer.setLayout(proMainViewerLayout);
        proMainViewerLayout.setHorizontalGroup(
            proMainViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        proMainViewerLayout.setVerticalGroup(
            proMainViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        statusBar.setBackground(new java.awt.Color(153, 153, 255));

        infoLabel.setEditable(false);

        javax.swing.GroupLayout statusBarLayout = new javax.swing.GroupLayout(statusBar);
        statusBar.setLayout(statusBarLayout);
        statusBarLayout.setHorizontalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoLabel)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusBarLayout.setVerticalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPanes)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(overviewPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optionPanes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proMainViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(tabbedPanes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(optionPanes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(overviewPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(proMainViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void goToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_goToButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel colorComp1GradientChooser;
    private javax.swing.JPanel colorComp2GradientChooser;
    private javax.swing.JPanel colorComp3GradientChooser;
    private javax.swing.JPanel colorGradientViewer;
    private javax.swing.JButton colorsButton;
    private javax.swing.JPanel colorsOptionPane;
    private javax.swing.JLabel estimatedTimeLabel1;
    private javax.swing.JLabel estimatedTimeLabel2;
    private javax.swing.JPanel explorationToolBar;
    private javax.swing.JButton goToButton;
    private javax.swing.JPanel goToOptionPane;
    private javax.swing.JTextField infoLabel;
    private javax.swing.JButton iterationsButton;
    private javax.swing.JLabel iterationsLabel;
    private javax.swing.JPanel iterationsOptionPane;
    private javax.swing.JTextField iterationsTextField;
    private javax.swing.JPanel lastScreenshotDisplayer;
    private javax.swing.JPanel optionPanes;
    private javax.swing.JPanel overviewPane;
    private javax.swing.JPanel proMainViewer;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton screenshotButton;
    private javax.swing.JButton screenshotFolderBrowserButton;
    private javax.swing.JLabel screenshotFolderLabel;
    private javax.swing.JPanel screenshotOptionPane;
    private javax.swing.JPanel simulationToolBar;
    private javax.swing.JPanel statusBar;
    private javax.swing.JTabbedPane tabbedPanes;
    private javax.swing.JButton validateGoToButton;
    private javax.swing.JButton validateIterationsButton;
    private javax.swing.JButton validateScreenshotButton;
    private javax.swing.JLabel xLabel;
    private javax.swing.JTextField xTextField;
    private javax.swing.JLabel yLabel;
    private javax.swing.JTextField yTextField;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JTextField zoomTextField;
    // End of variables declaration//GEN-END:variables

    public Controller getController() {
        return controller;
    }

    public JButton getColorsButton() {
        return colorsButton;
    }

    public JPanel getColorsOptionPane() {
        return colorsOptionPane;
    }

    public JLabel getEstimatedTimeLabel1() {
        return estimatedTimeLabel1;
    }

    public JLabel getEstimatedTimeLabel2() {
        return estimatedTimeLabel2;
    }

    public JPanel getExplorationToolBar() {
        return explorationToolBar;
    }

    public JButton getGoToButton() {
        return goToButton;
    }

    public JPanel getGoToOptionPane() {
        return goToOptionPane;
    }

    public JButton getIterationsButton() {
        return iterationsButton;
    }

    public JLabel getIterationsLabel() {
        return iterationsLabel;
    }

    public JPanel getIterationsOptionPane() {
        return iterationsOptionPane;
    }

    public JTextField getIterationsTextField() {
        return iterationsTextField;
    }

    public JPanel getOptionPanes() {
        return optionPanes;
    }

    public JPanel getOverviewPane() {
        return overviewPane;
    }

    public ProMainViewer getProMainViewer() {
        return (ProMainViewer)proMainViewer;
    }

    public JPanel getSimulationToolBar() {
        return simulationToolBar;
    }

    public JTabbedPane getTabbedPanes() {
        return tabbedPanes;
    }

    public JButton getValidateGoToButton() {
        return validateGoToButton;
    }

    public JButton getValidateIterationsButton() {
        return validateIterationsButton;
    }

    public JLabel getxLabel() {
        return xLabel;
    }

    public JTextField getxTextField() {
        return xTextField;
    }

    public JLabel getyLabel() {
        return yLabel;
    }

    public JTextField getyTextField() {
        return yTextField;
    }

    public JLabel getZoomLabel() {
        return zoomLabel;
    }

    public JTextField getZoomTextField() {
        return zoomTextField;
    }

    public JPanel getLastScreenshotDisplayer() {
        return lastScreenshotDisplayer;
    }

    public JButton getScreenshotButton() {
        return screenshotButton;
    }

    public JButton getScreenshotFolderBrowserButton() {
        return screenshotFolderBrowserButton;
    }

    public JLabel getScreenshotFolderLabel() {
        return screenshotFolderLabel;
    }

    public JPanel getScreenshotOptionPane() {
        return screenshotOptionPane;
    }

    public JButton getValidateScreenshotButton() {
        return validateScreenshotButton;
    }

    public ColorGradientChooser getColorComp1GradientChooser() {
        return (ColorGradientChooser)colorComp1GradientChooser;
    }

    public ColorGradientChooser getColorComp2GradientChooser() {
        return (ColorGradientChooser)colorComp2GradientChooser;
    }

    public ColorGradientChooser getColorComp3GradientChooser() {
        return (ColorGradientChooser)colorComp3GradientChooser;
    }

    public JTextField getInfoLabel() {
        return infoLabel;
    }

    public JPanel getjPanel1() {
        return statusBar;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JPanel getColorGradientViewer() {
        return colorGradientViewer;
    }

    /*public CurveScroller getCurveEditor() {
        return (CurveScroller)curveEditor;
    }*/
}
