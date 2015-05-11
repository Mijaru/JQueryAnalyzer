package Components.MainWindowComponents.Models;

import javax.swing.table.AbstractTableModel;

public class JTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2173685424567032936L;
	
	protected short[] columnState;
	protected String[] columnNames = null;
    protected Object[][] data = null;
	
	public JTableModel(String[] col, Object[][] rows) {
		if (col != null)
			columnNames = col;
		if (rows != null) {
			data = rows;
			columnState = new short[rows.length];
		}
	}
	
	public int getState(int row) {
		if (row < 0 || row > columnState.length) {
			return -1;
		}
		else {
			return columnState[row];
		}
	}
	
	public void setState(int row, int state) {
		columnState[row] = (short)state;
	}
    
	public void setColumnNames(String[] data){
    	this.columnNames = data;
    }
    
	public void setRows(Object[][] data){
    	this.data = data;
    	for (int i = 0; i < this.columnNames.length; i++) {
    		for (int j = 0; j < data[i].length; j++) {
    			this.setValueAt(data[i][j], i, j);
    		}
    	}
    }
    
	public Object[][] getData() {
    	return this.data;
    }
    
	public String[] getColumnNames() {
    	return (String[])this.columnNames;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
    	if (data == null) {
    		return 0;
    	}
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public void setValueAt(Object obj, int row, int col) {
        data[row][col] = obj;
    }
    
    public boolean isCellEditable(int row, int col) {
    	if (getValueAt(row, col) instanceof String) {
    		String value = (String)getValueAt(row, col);
    		if (value != null && value.split("<br>").length > 1) return true; 
    	}
    	return false;
    }
}