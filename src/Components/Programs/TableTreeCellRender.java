package Components.Programs;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

public class TableTreeCellRender implements TreeCellRenderer {
		private JLabel root = null;
		private ImageIcon icon = null;	
		private ImageIcon check_16 = new ImageIcon(ClassLoader.getSystemResource("checked_16.png"));
		private ImageIcon uncheck_16 = new ImageIcon(ClassLoader.getSystemResource("unchecked_16.png"));
		private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
		private String text = null;
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
			if (value == null) return null;
			int step = 0;
			text = value.toString();
									
			TreeModel model = tree.getModel();
			DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
			root = new JLabel(text);
			
			root.setOpaque(false);
			for (int i = 0; i < model.getChildCount(r); i++) {
				if (value != null && value.toString().equalsIgnoreCase(model.getChild(r, i).toString())) {
					icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));
					step = 1;
				}
			}
			if (text != null && text.equalsIgnoreCase(r.toString())) {
				icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
				step = 2;
			}
			final JCheckBox box = new JCheckBox();
			box.setBounds(17,-2,20,20);
			box.setMargin(new Insets(0,0,0,0));
			box.setOpaque(false);
			box.setFocusable(false);
			box.setIcon(uncheck_16);
			box.setSelectedIcon(check_16);
			if (step == 0 && leaf) {
				if (text != null && !text.startsWith("<html>")) {
					text = "    " + text;
				}
				root.setText(text.replace("<html>", "<html>&nbsp;&nbsp;&nbsp;&nbsp;").replace("</html>", "&nbsp;&nbsp;&nbsp;&nbsp;</html>"));
				for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
					if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
						if (row - 2 >= 0 && model.getChild(node, row - 2) instanceof JCheckTreeNode) {
							JCheckTreeNode select = (JCheckTreeNode)model.getChild(node, row - 2);
							box.setSelected(select.isSelected());
							switch (select.getType()) {
								case JCheckTreeNode.VIEW:
									icon = new ImageIcon(ClassLoader.getSystemResource("view.png"));
									break;
								case JCheckTreeNode.DATABASE:
									icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));
									break;
								case JCheckTreeNode.TABLE:
									icon = new ImageIcon(ClassLoader.getSystemResource("table.png"));
									break;
								default:
									icon = new ImageIcon(ClassLoader.getSystemResource("unchecked_16.png"));
									break;
							}
						}
					}
				}
				root.add(box);
			}
			if (icon != null) {
				root.setIcon(icon);
			}
			root.setForeground(box.isSelected() ? Color.BLACK : new Color(130,130,130));
			if (selected) {
				root.setOpaque(true);
				root.setForeground(Color.DARK_GRAY);
				root.setBackground(new Color(200,200,200));
			}
			root.setFont(_default_font);
			String text = "";
			boolean tag = false;
			for (int i = 0; i < this.text.length(); i++) {
				if (this.text.charAt(i) == '<') { tag = true; }
				else if (this.text.charAt(i) == '>') { tag = false; continue; }
				if (!tag) { text += this.text.charAt(i); }
			}
			root.setSize(root.getFontMetrics(root.getFont()).stringWidth(text), 18);
			root.setPreferredSize(new Dimension((int)root.getFontMetrics(root.getFont()).getStringBounds(text, root.getGraphics()).getWidth() + 50, 18));
			return root;
		}
		
		
	}
	
	