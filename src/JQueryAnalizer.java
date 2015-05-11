import Components.MainWindow;
public class JQueryAnalizer {

	//private static MainWindow _main_window = null;
	
	public static void main(String[] args) {
		/*
		for (int i = 0; i < keywords.length; i++) {
			for (int j = (i + 1); j < keywords.length; j++) {
				if (keywords[i].equalsIgnoreCase(keywords[j])) {
					System.out.println(" >> " + keywords[j]);
				}
			}
		}
		*/
			
		try {
			MainWindow main_window = new MainWindow();
			main_window.mountDialog();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
