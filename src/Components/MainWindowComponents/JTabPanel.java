package Components.MainWindowComponents;

import javax.swing.JComponent;
import javax.swing.JPanel;

import Components.SQLConnectionManager;

public abstract class JTabPanel extends JPanel {

	private static final long serialVersionUID = 6792297246375329431L;

	public abstract void openConnection();
	public abstract boolean isConnected();
	public abstract void closeConnection();
	public abstract SQLConnectionManager getConnection();
	
	public abstract void setParameters(JParametersPanel parameters);
	public abstract JParametersPanel getParameters();
	public abstract void updateStatus(String status);
	
	protected abstract void showToolTip(JComponent component, Exception error, String message);
	
	public String getParametersPath() {
		String out = "";
		if (getConnection() != null) {
			switch (getConnection().getServerType()) {
				case 0: out = "Oracle MySQL Server://"; break;
				case 1: out = "Oracle Database Server://"; break;
				case 2: out = "PostgreSQL Server"; break;
				case 3: out = "Microsoft SQL Server"; break;
			}
		}
		else { out = "Unknow Server://"; }
		if (getParameters() != null) {
			out += (getParameters().getHost() != null ? getParameters().getHost() : "UnknowHost:####") + "/";
			out += (getParameters().getDatabase() != null ? "<u>"+getParameters().getDatabase()+"</u>" : "UnknowDatabase");
		}
		else { out += "UnknowHost:####/UnknowDatabase"; }
		return out;
	}
}
