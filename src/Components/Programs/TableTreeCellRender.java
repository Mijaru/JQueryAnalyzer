package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.BorderFactory;
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
		private ImageIcon check = getCheckIcon();
		private ImageIcon uncheck = getUncheckIcon();
		private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
		private FontMetrics _default_font_metrics = null;
		private String text = null;
		
		private ImageIcon getCheckIcon() {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
			icon.setImage(icon.getImage().getScaledInstance(16, 16, 50));
			return icon;
		}
		
		private ImageIcon getUncheckIcon() {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
			icon.setImage(icon.getImage().getScaledInstance(16, 16, 50));
			return icon;
		}
		
		
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
			if (value == null) {
				return null;
			}
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
			box.setBounds(17,-2,24,24);
			box.setMargin(new Insets(5,5,5,5));
			box.setOpaque(false);
			box.setFocusable(false);
			box.setIcon(uncheck);
			box.setSelectedIcon(check);

			if (step == 0 && leaf) {
				if (text != null && !text.startsWith("<html>")) {
					text = "      " + text;
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
			root.setForeground(box.isSelected() || !leaf ? Color.BLACK : new Color(190,190,190));
			if (selected) {
				root.setOpaque(leaf);
				root.setForeground(Color.DARK_GRAY);
				if (leaf) {
					root.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
				}
			}
			root.setFont(_default_font);
			//String text = "";
			//boolean tag = false;
			text = text.replaceAll("\\<[^>]*>","");
			text = text.replace("&nbsp;", " ");
			/*
			for (int i = 0; i < this.text.length(); i++) {
				if (this.text.charAt(i) == '<') { tag = true; }
				else if (this.text.charAt(i) == '>') { tag = false; continue; }
				if (!tag) { text += this.text.charAt(i); }
			}
			*/
			_default_font_metrics = (_default_font_metrics == null ? root.getFontMetrics(_default_font) : _default_font_metrics);
			root.setSize(_default_font_metrics.stringWidth(text) + 40, 50);
			root.setMinimumSize(root.getSize());
			root.setMaximumSize(root.getSize());
			root.setPreferredSize(root.getSize());
			return root;
		}
		
		
	}
	
	