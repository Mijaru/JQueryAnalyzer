package Components;

public class Util {

	public static float round(double value, int places) {
	    if (places < 0) {
	    	throw new IllegalArgumentException();
	    }
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (float) tmp / factor;
	}
	
	public static String toPercent(float value) {
		String out = String.valueOf(round(value, 2)) + "%";
		if (out.indexOf(".") == -1) {
			out += ".00%";
		}
		else if ((out.indexOf("%") - out.indexOf(".")) == 2) {
			out = out.replace("%", "0%");
		}
		return out;
	}
}
