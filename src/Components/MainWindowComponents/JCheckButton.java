package Components.MainWindowComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import Components.MainWindow;


public class JCheckButton extends JCheckBox {
	private static final long serialVersionUID = 1L;
	private ImageIcon _icon;
	private ImageIcon _icon_selected;
	private URL       _icon_url;
	private Dimension _icon_dimension;
	
	public JCheckButton getButton() { return this; }
	/*
	public void paintBorder(Graphics g) {
		if (isBorderPainted() && isSelected()) {
			Dimension size = getSize();
			g.setColor(Color.DARK_GRAY);
			g.drawLine(0, 0, size.width, 0);
			g.drawLine(0, size.height - 1, size.width, size.height - 1);
			g.drawLine(0, 0, 0, size.height);
			g.drawLine(size.width - 1, 0, size.width - 1, size.height);
		}
	}
	*/
	public JCheckButton(URL u, Dimension d, String name) {
		this.setSelected(true);
		_icon_url = u;
		_icon_dimension = d;
		BufferedImage bi = new BufferedImage(_icon_dimension.width, _icon_dimension.height, BufferedImage.TRANSLUCENT);
		Graphics g = bi.getGraphics();
		
		_icon = new ImageIcon(_icon_url);
		_icon.setImage(_icon.getImage().getScaledInstance(_icon_dimension.width - 4, _icon_dimension.height - 4, Image.SCALE_AREA_AVERAGING));
		g.drawImage(_icon.getImage(), 2, 2, null);
		this.setIcon(new ImageIcon(bi));
		
		// -- mouse over icon...
		this.setRolloverEnabled(true);
		_icon = new ImageIcon(_icon_url);
		_icon.setImage(_icon.getImage().getScaledInstance(_icon_dimension.width, _icon_dimension.height, Image.SCALE_AREA_AVERAGING));
		this.setRolloverIcon(_icon);
		this.setRolloverSelectedIcon(_icon);
		
		try {
			String[] path = _icon_url.getFile().split("/");
			bi = new BufferedImage(_icon_dimension.width, _icon_dimension.height, BufferedImage.TRANSLUCENT);
			g = bi.getGraphics();
			
			_icon_selected = new ImageIcon(ClassLoader.getSystemResource(path[path.length - 1].replace(".png", "_selected.png")));
			_icon_selected.setImage(_icon_selected.getImage().getScaledInstance(_icon_dimension.width - 4, _icon_dimension.height - 4, Image.SCALE_AREA_AVERAGING));
			g.drawImage(_icon_selected.getImage(), 2, 2, null);
			this.setSelectedIcon(new ImageIcon(bi));
			
			_icon_selected = new ImageIcon(ClassLoader.getSystemResource(path[path.length - 1].replace(".png", "_selected.png")));
			_icon_selected.setImage(_icon_selected.getImage().getScaledInstance(_icon_dimension.width, _icon_dimension.height, Image.SCALE_AREA_AVERAGING));
			this.setRolloverSelectedIcon(_icon_selected);
		}
		catch (Exception e) { }
		
		
		
		
        this.setBorder(BorderFactory.createLineBorder(new Color(150,150,150)));
        this.setBorderPainted(true);
        
		this.setName(name);
		this.setBorderPainted(false);
		this.setBorder(BorderFactory.createLineBorder(new Color(180,180,180)));
		this.setBackground(new Color(215,215,215));
		this.setOpaque(false);
		this.setFocusable(false);
		this.setMargin(new Insets(5,5,5,5));
		this.setSize(new Dimension(d.width,d.height));
		this.setPreferredSize(new Dimension(d.width,d.height));
		this.setMaximumSize(new Dimension(d.width,d.height));
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				Component c = (Component)ev.getSource();
				if (c != null && c.getName() != null && c instanceof JCheckButton) {
					if (c.isEnabled() == false) return;
					else if (c.getName().equalsIgnoreCase("bt_connection")) bt_connect();
					else if (c.getName().equalsIgnoreCase("bt_script_run")) bt_script_run();
					else if (c.getName().equalsIgnoreCase("bt_script_save")) bt_script_save();
				
					else if (c.getName().equalsIgnoreCase("bt_history_back")) bt_history_page(-1);
					else if (c.getName().equalsIgnoreCase("bt_history_next")) bt_history_page(1);
					
					else if (c.getName().equalsIgnoreCase("bt_page_first")) bt_sort_page(1);
					else if (c.getName().equalsIgnoreCase("bt_page_back")) bt_sort_page(2);
					else if (c.getName().equalsIgnoreCase("bt_page_next")) bt_sort_page(3);
					else if (c.getName().equalsIgnoreCase("bt_page_last")) bt_sort_page(4);
					
					else if (c.getName().equalsIgnoreCase("bt_page_adjust")) bt_page_adjust();
					else if (c.getName().equalsIgnoreCase("bt_query_search")) bt_result_search();
					else if (c.getName().equalsIgnoreCase("bt_query_save")) bt_result_save();
				}
			}
		});
	}	
	
	
	/** ------------------------------------ */
	/** FUNÇÕES DOS BOTÕES E MENUS...        */
	/** ==================================== */
	public void bt_connect() {
		JTabPanel active = null;
		if (MainWindow.getActiveTab() instanceof JTabPanel) {
			active = (JTabPanel)MainWindow.getActiveTab();
			if (active.isConnected()) {
				if (JOptionPane.showConfirmDialog(null, "<html><font face='verdana' size='3'>Você já está conectado a uma base de dados.<br><br><i>Deseja reconectar a base de dados</i>: <b>" +active.getParametersPath()+ "</b> ?</font></html>", "Deseja reconectar-se?", JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
					active.closeConnection();
				}
				else {
					return;
				}
			}		
			active.updateStatus("<i><font color='yellow'>INICIANDO CONEXÃO COM SERVIDOR...</font></i><br>\\:> <b>PENDING</b>");
		}
		
		JThreadCommands tc = new JThreadCommands(0, null, active);
		Thread t = new Thread(tc);
		t.start();
	}
	
	public void bt_script_run() {
		JQueryPane pane = null;
		if (MainWindow.getActiveTab() instanceof JQueryPane) {
			pane = (JQueryPane)MainWindow.getActiveTab();
			JThreadCommands tc = new JThreadCommands(2, pane.getQuerySentence(), MainWindow.getActiveTab());
			Thread t = new Thread(tc);
			t.start();
		}
	}

	public void bt_script_save() {
		JThreadCommands tc = new JThreadCommands(12, null, MainWindow.getActiveTab());
		Thread t = new Thread(tc);
		t.start();
	}
	
	public void bt_sort_page(int op) {
		JThreadCommands tc = new JThreadCommands(3 + op, null, MainWindow.getActiveTab());
		Thread t = new Thread(tc);
		t.start();
	}
	
	public void bt_page_adjust() {
		JThreadCommands tc = new JThreadCommands(8, null, MainWindow.getActiveTab());
		Thread t = new Thread(tc);
		t.start();
	}
	
	public void bt_history_page(int op) {
		JThreadCommands tc = new JThreadCommands(9, op + "", MainWindow.getActiveTab());
		Thread t = new Thread(tc);
		t.start();
	}
	
	public void bt_result_search() {
		JThreadCommands tc = new JThreadCommands(10, JOptionPane.showInputDialog(null, "Qual termo deseja buscar?\nPara executar uma busca com termos exatos coloque a expressão entre os caractere: '#'"), MainWindow.getActiveTab());
		Thread t = new Thread(tc);
		t.start();
	}
	
	public void bt_result_save() {
		
		
		final JQueryPane pane = (JQueryPane)MainWindow.getActiveTab();
		
		
		final JPopupMenu popup = new JPopupMenu();
		JMenuItem toHTML = new JMenuItem("Exportar em formato HTML");
		toHTML.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JThreadCommands tc = new JThreadCommands(11, "html", MainWindow.getActiveTab());
				Thread t = new Thread(tc);
				t.start();
			}
		});
		popup.add(toHTML);
		
		JMenuItem toXLS = new JMenuItem("Exportar em formato XLS (Excel)");
		toXLS.setName("export_toXLS");
		toXLS.setEnabled(false);
		popup.add(toXLS);
		
		JMenuItem toCSV = new JMenuItem("Exportar em formato CSV (Excel)");
		toCSV.setName("export_toCSV");
		toCSV.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JThreadCommands tc = new JThreadCommands(11, "csv", MainWindow.getActiveTab());
				Thread t = new Thread(tc);
				t.start();
			}
		});
		popup.add(toCSV);
		Point mouse = pane.getMousePosition();
		popup.show(pane, mouse.x, mouse.y);
		
		
		
		
		
		
		
	}
}

