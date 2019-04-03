package gov.gsa.pivconformancegui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class GuiRunnerToolbar extends JToolBar implements ActionListener{
	
	public GuiRunnerToolbar() {
		super("85b GUI Runner");
				
	}
	
	protected void addButtons(JToolBar toolBar) {
	    JButton button = null;

	    button = makeButton("Run1", "doRun",
	                                  "Run Tests",
	                                  "Run");
	    toolBar.add(button);

	    
	    button = makeButton("Stop1", "doStop",
	                                  "Stop Tests",
	                                  "Stop");
	    toolBar.add(button);
	    
	    button = makeButton("Open1", "doOpen",
                "Open Tests",
                "Open");
	    toolBar.add(button);
	    
	    button = makeButton("Save1", "doSave",
                "Save Tests",
                "Save");
	    toolBar.add(button);

	    
	}

	protected JButton makeButton(String imageName,
	                                       String actionCommand,
	                                       String toolTipText,
	                                       String altText) {
	    //Look for the image.
	    String imgLocation = "images/"
	                         + imageName
	                         + ".gif";
	    URL imageURL = GuiRunnerToolbar.class.getResource(imgLocation);

	    //Create and initialize the button.
	    JButton button = new JButton();
	    button.setActionCommand(actionCommand);
	    button.setToolTipText(toolTipText);
	    button.addActionListener(this);

	    if (imageURL != null) {                      //image found
	        button.setIcon(new ImageIcon(imageURL, altText));
	    } else {                                     //no image found
	        button.setText(altText);
	        System.err.println("Resource not found: " + imgLocation);
	    }

	    return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
	}
}
