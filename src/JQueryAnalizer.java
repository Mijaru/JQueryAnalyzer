import Components.MainWindow;
public class JQueryAnalizer {
	
	public static void main(String[] args) {
		try {
			MainWindow main_window = new MainWindow();
			main_window.mountDialog();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
