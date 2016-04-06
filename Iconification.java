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
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.Iterator;
import java.util.List;

public class Iconification extends JFrame implements DropTargetListener, ActionListener {

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
		setSize(500, 500);
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
			System.out.println("preparing different resolutions...");
			for(int i = 0; i < iResolutions.length; i++) {
				try {

					System.out.println("Working on resolution " + iResolutions[i] + "...");
					System.out.println("Path used is : " + fileImage.getPath());
					
					int iRES = iResolutions[i];
					int iRESSECONDFILE = iRES;
					
					System.out.println("**********First iRES : " + iRES);
					String sSetOne = "sips -Z" + " " + iRES + " " + fileImage.getPath() +  " " + "-o" + " " + sIconsetPath + "icon_" + iRES + "x" + iRES + "@1x.png";
					iRES = iRES * 2;
					System.out.println("**********Second iRES {" + iRES + "}");
					String sSetTwo = "sips -Z" + " " + iRES + " " + fileImage.getPath() +  " " + "-o" + " " + sIconsetPath + "icon_" + iRESSECONDFILE + "x" + iRESSECONDFILE + "@2x.png";
					iRESSECONDFILE = iRES;
					System.out.println("Executing sips to create icon set...");
					
					runtime.exec(sSetOne);
					runtime.exec(sSetTwo);
					System.out.println("done.");
					
					System.out.println("Creating ICNS...");
					String sShellCommand = "iconutil -c icns" + " " + fileIconsetFolder.getPath() + " " + "-o" + " " + "AppIcon.icns";
					runtime.exec(sShellCommand);
					
				} catch(Exception X) {
					X.printStackTrace();
				} // end of try-catch
				
				String sIcnsPath = fileIconsetFolder.getParent() + "/" + "AppIcon.icns";				
				File fileIconset = new File(sIcnsPath);
				
				if(fileIconset.exists()) {
						System.out.println("ICNS Generated successfully.");
						jLabelImage.setIcon(null);
						jLabelImage.setText("Done");
				}
			} // end of for
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