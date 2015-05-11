package Components.Programs;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;


public class JCheckTreeNode extends DefaultMutableTreeNode {
	public static final int DATABASE = 0;
	public static final int TABLE = 1;
	public static final int VIEW = 2;
	public static final int UNKNOW = 3;
	
	private static final long serialVersionUID = 1L;
	private boolean isSelected;
	private int type = TABLE;
		  
	public JCheckTreeNode(Object node) {
		this(node, true, true);
	}
			
	public JCheckTreeNode(Object node, boolean allowsChildren, boolean isSelected) {
		super(node, allowsChildren);
	    this.isSelected = isSelected;
	}
			
	public void setSelected(boolean isSelected) {
	    this.isSelected = isSelected;
	    if (children != null) {
	    	Enumeration<?> e = children.elements();
	    	while (e.hasMoreElements()) {
	    		JCheckTreeNode leaf = (JCheckTreeNode)e.nextElement();
	    		leaf.setSelected(isSelected);
	    		
	    	}
	    }
	}
			
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}

	public boolean isSelected() {
		return isSelected;
	}

}
