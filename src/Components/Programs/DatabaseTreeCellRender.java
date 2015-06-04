package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

public class DatabaseTreeCellRender implements TreeCellRenderer {
			
		private JLabel root = null;
		private Image icon = null;
		private String _SELECTED_DATABASE;
		private String _SELECTED_HOST;
		private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
		private Image _data = getIconData();
		private Image _server = getIconServer();
		private Image _node = getIconNode();
		
		private Image getIconData() {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));
			//icon.setImage(icon.getImage().getScaledInstance(15, 15, 100));
			return icon.getImage();
		}
		
		private Image getIconServer() {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
			//icon.setImage(icon.getImage().getScaledInstance(15, 15, 100));
			return icon.getImage();
		}
		
		private Image getIconNode() {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("node.png"));
			//icon.setImage(icon.getImage().getScaledInstance(15, 15, 100));
			return icon.getImage();
		}
		
		public DatabaseTreeCellRender(String t1, String t2) {
			_SELECTED_DATABASE = t1;
			_SELECTED_HOST = t2;
		}
		
		public void updateSelectionDatabase(String t1, String t2) {
			_SELECTED_DATABASE = t1;
			_SELECTED_HOST = t2;
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
			if (value == null) return null;
			int step = 0;
			icon = _data;
			TreeModel model = tree.getModel();
			DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
			root = new JLabel();
			root.setOpaque(false);
			for (int i = 0; i < model.getChildCount(r); i++) {
				if (value != null && value.toString().equalsIgnoreCase(model.getChild(r, i).toString())) {
					icon = _server;
					step = 1;
				}
			}
			if (value != null && value.toString().equalsIgnoreCase(r.toString())) {
				icon = _node;
				step = 2;
			}
			if (icon != null) {
				root.setIcon(new ImageIcon(icon));
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
				root.setOpaque(leaf);
				if (!value.toString().equalsIgnoreCase(_SELECTED_DATABASE != null ? _SELECTED_DATABASE : "") && !value.toString().equalsIgnoreCase(_SELECTED_HOST != null ? _SELECTED_HOST.split(":")[0] : "")) {
					root.setForeground(Color.DARK_GRAY);
					if (leaf) {
						root.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
					}
				}
				else if (leaf) {
					root.setBorder(BorderFactory.createLineBorder(new Color(193,223,246)));
				}
			}
			root.setText(value.toString().replace("</html>", "&nbsp;&nbsp;&nbsp;&nbsp;</html>"));
			String text = "";
			boolean tag = false;
			for (int i = 0; i < value.toString().length(); i++) {
				if (value.toString().charAt(i) == '<') { tag = true; }
				else if (value.toString().charAt(i) == '>') { tag = false; continue; }
				if (!tag) { text += value.toString().charAt(i); }
			}
			root.setSize(root.getFontMetrics(root.getFont()).stringWidth(text), 22);
			root.setPreferredSize(new Dimension((int)root.getFontMetrics(root.getFont()).getStringBounds(text, root.getGraphics()).getWidth() + 40, 22));
			return root;
		}				
	};