package Components.MainWindowComponents;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

public class JBraketButton extends JLabel {
	private static final long serialVersionUID = 6913134203091007523L;
	public JBraketButton(int height) {
		super();
        this.setOpaque(true);
        this.setBackground(Color.GRAY);
        this.setSize(1, height);
        this.setPreferredSize(new Dimension(1, height));
        this.setMinimumSize(new Dimension(1, height));        
        this.setMaximumSize(new Dimension(1, height));
	}
}