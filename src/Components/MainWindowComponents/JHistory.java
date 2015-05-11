package Components.MainWindowComponents;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JHistory {

	public static enum Type { CONNECTION, QUERY, ERROR };
	
	private Type _type;
	private Date _timestamp;
	private String _value;
	private Object _raw;
	
	public JHistory(Type type, Object object) {
		_type = type;
		_timestamp = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		_value = "";
		_raw = object;
		StringBuilder sb = new StringBuilder();
		// -- string.
		if (object instanceof String) {
			sb.append("# ");
			sb.append(df.format(_timestamp));
			sb.append(" QUERY BEGIN:\r\n");
			sb.append(object.toString().trim());
			sb.append("\r\n# QUERY END\r\n");
		}
		else if (object instanceof Exception) {
			Exception e = (Exception) object;
			sb.append("# ");
			sb.append(df.format(_timestamp));
			sb.append(" EXCEPTION REPORT:\r\n");
			sb.append("// -- ERROR MESSAGE: ");
			sb.append(e.getMessage());
			sb.append("\r\n");
			sb.append("// -- STACK TRACE: \n/*");
			for (StackTraceElement stack : e.getStackTrace()) {
				sb.append(stack.toString());
				sb.append("\r\n");
			}
			sb.append("*/\r\n");
		}
		else if (object instanceof JParametersPanel) {
			JParametersPanel p = (JParametersPanel) object;
			sb.append("# ");
			sb.append(df.format(_timestamp));
			sb.append(" CONNECTION STARTED:\r\n");
			
			sb.append("-- HOST: ");
			sb.append(p.getHost());
			sb.append("\r\n");

			sb.append("-- DATABASE: ");
			sb.append(p.getDatabase());
			sb.append("\r\n");
			
			sb.append("-- USER: ");
			sb.append(p.getUser());
			sb.append("\r\n");
			
			sb.append("-- PASS: ");
			sb.append(p.getPass());
			sb.append("\r\n");
			
			sb.append("-- CONNECTION STRING: ");
			sb.append(p.getConnectionString());
			sb.append("\r\n");
			
			sb.append("\r\n# CONNECTION ENDED\r\n");
		}
		_value = sb.toString();
		sb = null;
	}
	
	public Type getType() {
		return _type;
	}
	
	public String getLog() {
		return _value;
	}
	
	public Object getRaw() {
		return _raw;
	}
}
