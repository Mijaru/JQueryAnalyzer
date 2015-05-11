package Components.MainWindowComponents;

import java.awt.Frame;

import javax.swing.JDialog;

public class JQDialog extends JDialog {

	private static final long serialVersionUID = -4518075118407988928L;

	public JQDialog(Frame owner, String title) {    
		super(owner, title, true);
	}

}
