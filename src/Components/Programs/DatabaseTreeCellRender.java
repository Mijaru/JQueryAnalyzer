package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

public class DatabaseTreeCellRender implements TreeCellRenderer {
			
		private JLabel root = null;
		private ImageIcon icon = null;
		private String _SELECTED_DATABASE;
		private String _SELECTED_HOST;
		private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
		
		public DatabaseTreeCellRender(String t1, String t2) {
			_SELECTED_DATABASE = t1;
			_SELECTED_HOST = t2;
		}
		
		public void updateSelectionDatabase(String t1, String t2) {
			_SELECTED_DATABASE = t1;
			_SELECTED_HOST = t2;
		}
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
			if (value == null) return null;
			int step = 0;
			icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));
			TreeModel model = tree.getModel();
			DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
			root = new JLabel();
			root.setOpaque(false);
			for (int i = 0; i < model.getChildCount(r); i++) {
				if (value != null && value.toString().equalsIgnoreCase(model.getChild(r, i).toString())) {
					icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
					step = 1;
				}
			}
			if (value != null && value.toString().equalsIgnoreCase(r.toString())) {
				icon = new ImageIcon(ClassLoader.getSystemResource("node.png"));
				step = 2;
			}
			if (icon != null) {
				root.setIcon(icon);
			}
			root.setForeground(selected ? Color.RED : Color.BLACK);
			if (step == 2 || value.toString().replace(" ", "").equalsIgnoreCase(_SELECTED_DATABASE != null ? _SELECTED_DATABASE : "")) {
				root.setFont(new Font(_default_font.getFamily(), step == 2 ? Font.BOLD : Font.ROMAN_BASELINE,_default_font.getSize()));
				root.setForeground(step != 2 ? new Color(0,153,255) : new Color(0,0,120));
			}
			else if (step == 1) { 
				if (value.toString().equalsIgnoreCase(_SELECTED_HOST != null ? _SELECTED_HOST.split(":")[0] : "")) {
					root.setForeground(step == 1 ? new Color(0,153,255) : new Color(0,0,120));
				}
				root.setFont(new Font(_default_font.getFamily(),Font.ITALIC,_default_font.getSize()));					
			}
			else if (step == 0) {
				root.setForeground(selected ? Color.RED : Color.DARK_GRAY);
				root.setFont(_default_font);
			}

			if (selected) {
				root.setOpaque(true);
				if (!value.toString().equalsIgnoreCase(_SELECTED_DATABASE != null ? _SELECTED_DATABASE : "") && !value.toString().equalsIgnoreCase(_SELECTED_HOST != null ? _SELECTED_HOST.split(":")[0] : "")) {
					root.setForeground(Color.DARK_GRAY);
					root.setBackground(new Color(200,200,200));
				}
				else {
					root.setBackground(new Color(193,223,246));
				}
			}
			//root.setText(value.toString() + " ");
			root.setText(value.toString().replace("</html>", "&nbsp;&nbsp;&nbsp;&nbsp;</html>"));
			String text = "";
			boolean tag = false;
			for (int i = 0; i < value.toString().length(); i++) {
				if (value.toString().charAt(i) == '<') { tag = true; }
				else if (value.toString().charAt(i) == '>') { tag = false; continue; }
				if (!tag) { text += value.toString().charAt(i); }
			}
			root.setSize(root.getFontMetrics(root.getFont()).stringWidth(text), 18);
			root.setPreferredSize(new Dimension((int)root.getFontMetrics(root.getFont()).getStringBounds(text, root.getGraphics()).getWidth() + 30, 18));
			return root;
		}				
	};