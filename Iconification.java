/**
  * Copyright (c) 2016 Mian Safyan Shah
  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
  * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
  * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
  * Software is furnished to do so, subject to the following conditions:
 
  * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
  * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/

import java.lang.Runtime;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.Iterator;
import java.util.List;

public class Iconification extends JFrame implements DropTargetListener, ActionListener {
    
    // Strings
    private StringBuilder sBuilderShellOutput;
    
    // Object State
    private static boolean IS_IMAGE_SET = false;
    
    // DropTarget
    private DropTarget dropTargetThis;
    
    // Toolkit
    private Toolkit toolkit;
    
    // size
    private final int iWIDTH = 500;
    private final int iHEIGHT = 400;
    
    // screen size
    private Dimension dimensionScreenSize;
    // X and Y of Frame on Screen
    private int iX;
    private int iY;
    
    // Frame Size
    private Dimension dimensionPreferredSize = new Dimension(iWIDTH, iHEIGHT);
    private Dimension dimensionMaximumSize = new Dimension(iWIDTH, iHEIGHT);
    private Dimension dimensionMinimumSize = new Dimension(iWIDTH, iHEIGHT);
    
    // ImageIcon
    private ImageIcon imageIconPicture;
    
    // Label
    private JLabel jLabelImage;
    
    // UI Components
    private JButton jButtonExit;
    
    // Panel
    private JPanel jPanelImage;
    
    // dc
    public Iconification() {
        initDependencies();
        initUIComponents();
        attachListeners();
        finalizeUI();
    } // end of dc
    // method to initialise requirements
    private void initDependencies() {
        dropTargetThis = new DropTarget(this, DnDConstants.ACTION_LINK, this, true, null);
        dimensionScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        iX = (dimensionScreenSize.width - iWIDTH) / 2;
        iY = (dimensionScreenSize.height - iHEIGHT) / 2;
    } // end of initDependencies
    // method to initialise UI components
    private void initUIComponents() {
        jButtonExit = new JButton("Exit");
        jPanelImage = new JPanel();
        jLabelImage = new JLabel("image goes here", SwingConstants.CENTER);
    } // end of initUIComponents
    // method to attach Action Listeners to UI Components
    private void attachListeners() {
        jButtonExit.addActionListener(this);
    } // end of attachListeners
    // method to finalize UI
    private void finalizeUI() {
        jPanelImage.setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());
        setUndecorated(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 500);
        setLocation(iX, iY);
        
        jPanelImage.setLayout(new BorderLayout());
        jPanelImage.add(jLabelImage, BorderLayout.CENTER);
        add(jPanelImage, BorderLayout.CENTER);
        
        // finalize JButton UI
        jButtonExit.setFocusable(false);
        // add UI Components
        // add(jButtonExit, BorderLayout.SOUTH);
        setVisible(true);
    }
    public void drop(DropTargetDropEvent dtdeDrop) {
        acceptDropGetLink(dtdeDrop);
    }
    public void dragExit(DropTargetEvent dteDragExit) {
    }
    public void dropActionChanged(DropTargetDragEvent dtdeDropActionChanged) {
    }
    public void dragOver(DropTargetDragEvent dtdeDragOver) {
    }
    public void dragEnter(DropTargetDragEvent dtdeDragEnter) {
    }
    
    // method to get link of the dropped file
    private void acceptDropGetLink(DropTargetDropEvent dtdeDropEvent) {
        dtdeDropEvent.acceptDrop(DnDConstants.ACTION_LINK);
        
        try {
            Transferable transferableDrop = dtdeDropEvent.getTransferable();
            
            if(transferableDrop.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                Object objectFile = transferableDrop.getTransferData(DataFlavor.javaFileListFlavor);
                
                Iterator iteratorFileList = ((List)objectFile).iterator();
                
                while(iteratorFileList.hasNext()) {
                    File fileDropped = (File)iteratorFileList.next();
                    
                    if(!fileDropped.isDirectory()) {
                        if(verifyImageResolutionRequirement(fileDropped.getAbsolutePath())) {
                            imageIconPicture = new ImageIcon(fileDropped.getAbsolutePath());
                            // Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
                            Image imageScaled = ((Image)imageIconPicture.getImage()).getScaledInstance(iWIDTH, iHEIGHT, Image.SCALE_SMOOTH);
                            ImageIcon imageIconScaled = new ImageIcon(imageScaled);
                            jLabelImage.setText("");
                            jLabelImage.setIcon(imageIconScaled);
                            IS_IMAGE_SET = true;
                            
                            // call method to generate Icon set of the image provided
                            generateIconset(fileDropped);
                        } else {
                            jLabelImage.setIcon(null);
                            jLabelImage.setText("This image is either too small or too large.");
                            IS_IMAGE_SET = false;
                        }
                    }
                } // end of while
            }
        } catch(Exception X) {
        }
    } // end of method acceptDropGetLink
    
    // method to generate Iconset
    private final void generateIconset(File fileImage) {
        String sParentPath = fileImage.getParent().toString() + "/";
        
        // create iconset directory
        File fileIconsetFolder = new File(sParentPath + "AppIcon.iconset");
        fileIconsetFolder.mkdir();
        
        if(fileIconsetFolder.exists()) {
            String sIconsetPath = fileIconsetFolder.getPath() + "/";
            int[] iResolutions = {16, 32, 64, 128, 256, 512, 1024};
            Runtime runtime = Runtime.getRuntime();
            shellOutput("preparing different resolutions...");
            for(int i = 0; i < iResolutions.length; i++) {
                try {
                    
                    shellOutput("Working on resolution " + iResolutions[i] + "...");
                    shellOutput("Path used is : " + fileImage.getPath());
                    
                    int iRES = iResolutions[i];
                    int iRESSECONDFILE = iRES;
                    
                    shellOutput("*First iRES : " + iRES);
                    String sSetOne = "sips -Z" + " " + iRES + " " + fileImage.getPath() +  " " + "-o" + " " + sIconsetPath + "icon_" + iRES + "x" + iRES + "@1x.png";
                    iRES = iRES * 2;
                    shellOutput("*Second iRES {" + iRES + "}");
                    String sSetTwo = "sips -Z" + " " + iRES + " " + fileImage.getPath() +  " " + "-o" + " " + sIconsetPath + "icon_" + iRESSECONDFILE + "x" + iRESSECONDFILE + "@2x.png";
                    iRESSECONDFILE = iRES;
                    shellOutput("Executing sips to create icon set...");
                    
                    Process processSetOne = runtime.exec(sSetOne);
                    processSetOne.waitFor();
                    
                    Process processSetTwo = runtime.exec(sSetTwo);
                    processSetTwo.waitFor();
                    
                    shellOutput("done.");
                    
                    shellOutput("Creating ICNS...");
                    String sShellCommand = "iconutil -c icns" + " " + fileIconsetFolder.getPath() + " " + "-o" + " " + "AppIcon.icns";
                    
                    shellOutput("Starting ICNS Shell process...");
                    Process processICNSShell = runtime.exec(sShellCommand);
                    shellOutput("executed...");
                    BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(processICNSShell.getErrorStream()));
                    
                    sBuilderShellOutput = new StringBuilder();
                    String sShellOutput;
                    
                    while((sShellOutput = bufferedReaderError.readLine()) != null) {
                        sBuilderShellOutput.append(sShellOutput);
                        sBuilderShellOutput.append("\n");
                    }
                    shellOutput("Done.");
                    bufferedReaderError.close();
                    
                } catch(Exception X) {
                    JOptionPane.showMessageDialog(this, X.toString(), "Output", JOptionPane.ERROR_MESSAGE);
                }// end of try-catch
            } // end of for
            
            String sIcnsPath = fileIconsetFolder.getParent() + "/" + "AppIcon.icns";
                File fileIconset = new File(sIcnsPath);
                
                if(fileIconset.exists()) {
                    shellOutput("ICNS Generated successfully.");
                    jLabelImage.setIcon(null);
                    jLabelImage.setText("Done");
                }
            
            if(sBuilderShellOutput.toString().equals("")) {
            	JOptionPane.showMessageDialog(this, "Conversion Completed", "Output", JOptionPane.INFORMATION_MESSAGE);
            } else {
	            JOptionPane.showMessageDialog(this, sBuilderShellOutput.toString(), "Output", JOptionPane.ERROR_MESSAGE);
	            jLabelImage.setIcon(null);
	            jLabelImage.setText("Image file is not in a proper format.");
	        }
        } // end of if
    } // end of generateIconset method
    
    // method to check resolution of image
    private final boolean verifyImageResolutionRequirement(String sPATH) {
        boolean bVALIDIMAGE = false;
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(sPATH));
            int iIMAGE_WIDTH = bufferedImage.getWidth();
            int iIMAGE_HEIGHT = bufferedImage.getHeight();
            
            Dimension dimensionRequiredMax = new Dimension(1024, 1024);
            Dimension dimensionRequiredMin = new Dimension(16, 16);
            
            Dimension dimensionImage = new Dimension(iIMAGE_WIDTH, iIMAGE_HEIGHT);
            
            if((dimensionImage.width > dimensionRequiredMax.width) | (dimensionImage.height > dimensionRequiredMax.width )) {
                bVALIDIMAGE = false;
            } else if((dimensionImage.width < dimensionRequiredMin.width) | (dimensionImage.height < dimensionRequiredMin.height)) {
                bVALIDIMAGE = false;
            } else {
                bVALIDIMAGE = true;
            }
        } catch(Exception X) {
            X.printStackTrace();
        }
        return(bVALIDIMAGE);
    }
    // method to output string
    private final void shellOutput(String sMessage) {
    	System.out.println(sMessage);
    } // end of shellOutput
    // action performed method
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == jButtonExit) {
            System.exit(0);
        }
    } // end of actionPerformed
    // main
    public static void main(String[] args) {
        Iconification iconification = new Iconification();
    } // end of main
}
