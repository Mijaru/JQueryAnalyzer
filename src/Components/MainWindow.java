package Components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import Components.MainWindowComponents.JBraketButton;
import Components.MainWindowComponents.JCheckButton;
import Components.MainWindowComponents.JHistory;
import Components.MainWindowComponents.JParametersPanel;
import Components.MainWindowComponents.JPefformacePane;
import Components.MainWindowComponents.JQueryPane;
import Components.MainWindowComponents.JTabPanel;
import Components.MainWindowComponents.JThreadCommands;
import Components.Programs.Backup;
import Components.Programs.Copy;
import Components.Programs.Repair;
import Components.Programs.Restore;
import Components.Programs.Console;

import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

public class MainWindow {
	public static boolean _debug = false;
	private String _version = "v.3.11 11/05/2015";
	private JParametersPanel _PARAMETERS;
	private static JPanel _MENU_MAIN;
	private static int _ACTIVE_TAB_INDEX;
	private static Component _ACTIVE_TAB;
	private static JTabbedPane _tabbed_pane;
	private static Properties _CONFIG;
	private static String _CONFIG_FILE;
	
	public static String getPropertie(String propertie, String def_value) {
		if (_CONFIG != null && !_CONFIG.isEmpty()) {
			return _CONFIG.getProperty(propertie, def_value);
		}
		return null;
	}
	
	public static void setPropertie(String propertie, String value) {
		if (_CONFIG != null && !_CONFIG.isEmpty()) {
			_CONFIG.setProperty(propertie, value);
		}
	}

	public static boolean saveProperties() {
		try {
			_CONFIG.store(new FileOutputStream(System.getProperty("user.dir") + "\\Config.properties"), _CONFIG.propertyNames().toString());
			_CONFIG_FILE = (new File(System.getProperty("user.dir") + "\\Config.properties")).getCanonicalPath();
			if (_MAIN == null || !_MAIN.isVisible()) {
				System.exit(0);
			}
			return true;
		}
		catch (Exception e) { e.printStackTrace(); return false; }
	}
	
	
    public static Border border_left = new Border() {
    	public Insets getBorderInsets(Component arg0) {	return new Insets(0,0,0,0); }
    	public boolean isBorderOpaque() { return true; }
    	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    		Color lineColor = new Color(180,180,180);
    		Color oldColor = g.getColor();
		    g.setColor(lineColor);
		    g.drawLine(x, y + height - 1, x + width, y + height - 1);
		    g.drawLine(x, y - 1, x, y + height);
		    g.drawLine(x, y, x + width, y);
		    g.setColor(oldColor);
    	}
    };
    
    public static Border border_right = new Border() {
		public Insets getBorderInsets(Component arg0) {	return new Insets(0,0,0,0); }
		public boolean isBorderOpaque() { return true; }
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Color lineColor = new Color(180,180,180);
		    Color oldColor = g.getColor();
		    g.setColor(lineColor);
		    g.drawLine(x, y + height - 1, x + width, y + height - 1);
		    g.drawLine(x + width - 1, y, x + width - 1, y + height);
		    g.drawLine(x, y, x + width, y);
		    g.setColor(oldColor);
		}
    };

	
	private static JFrame _MAIN;
	private Dimension _MIN_DIMENSION = null;
	private ImageIcon _tab_query_icon;
	private ImageIcon _tab_add_icon;
	private ImageIcon _tab_del_icon;
	private GridBagConstraints _layout;

	private ImageIcon _tab_gauge_icon;

	public void mountDialog() {
		_CONFIG = new Properties();
		try {
			_CONFIG.load(new FileInputStream(System.getProperty("user.dir") + "\\Config.properties"));
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		try	{ UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { /** -não reporta erros- */ }
		
		_MIN_DIMENSION = new Dimension(850, 600);
		
		_MAIN = new JFrame();
		_MAIN.setTitle("JQuery Analizer - " + _version);
		_MAIN.setMinimumSize(_MIN_DIMENSION);
		_MAIN.setResizable(true);          // permite redimensionamento
		_MAIN.setLocationRelativeTo(null); // janela no centro
		_MAIN.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_MAIN.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent arg0) { }
			public void windowClosed(WindowEvent arg0) { }
			public void windowClosing(WindowEvent arg0) {
				if (JOptionPane.showConfirmDialog(_MAIN, "Deseja realmente encerrar o JQueryAnalizer?", "Confirmação!", JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
					for (Component c : _tabbed_pane.getComponents()) {
						if (c != null && c instanceof JQueryPane) {
							try { ((JQueryPane)c).getConnection().closeConnection(); }
							catch (Exception e) {}
						}
					}
					saveProperties();
					System.exit(0);
				}
			}
			public void windowDeactivated(WindowEvent arg0) { }
			public void windowDeiconified(WindowEvent arg0) { }
			public void windowIconified(WindowEvent arg0) { }
			public void windowOpened(WindowEvent arg0) { }
		});
		_MAIN.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent a) { }
			public void componentMoved(ComponentEvent a) { }
			public void componentShown(ComponentEvent a) { }
			public void componentResized(ComponentEvent event) {
				if (_PARAMETERS != null) {
					for (ComponentListener listener : _PARAMETERS.getComponentListeners()) {
						listener.componentResized(null);
					}
				}
			}
		});
			
		_MAIN.getContentPane().setLayout(new GridBagLayout());
		_MAIN.setIconImage(new ImageIcon(ClassLoader.getSystemResource("JQueryAnalizer.png")).getImage());
		MainWindowMenu menu = new MainWindowMenu();
		_MAIN.setJMenuBar(menu != null ? menu.getMenu() : null);
		
		/* ---------------------------------------- */
		/* -barra de ferramentas, ícones e funções- */
		/* ---------------------------------------- */		
        _layout = new GridBagConstraints();  
        _layout.fill = GridBagConstraints.BOTH;
        _layout.insets = new Insets(5,5,0,0);
        _layout.gridx = 1;
        _layout.gridy = 1;
        _layout.weightx = 0.001;
        _layout.weighty = 0.001;
        
        
        JCheckButton bt_connection = new JCheckButton(ClassLoader.getSystemResource("server_down.png"), new Dimension(48, 48), "bt_connection");
        bt_connection.setEnabled(true);
        bt_connection.setToolTipText("Conecta / Desconecta de uma base de dados.");
        
        JCheckButton bt_db_search  = new JCheckButton(ClassLoader.getSystemResource("data_search.png"), new Dimension(48, 48), "");
        bt_db_search.setEnabled(false);
        JCheckButton bt_db_backup  = new JCheckButton(ClassLoader.getSystemResource("data_backup.png"), new Dimension(48, 48), "");
        bt_db_backup.setEnabled(true);
        bt_db_backup.setToolTipText("Executa um backup da database atual.");
        bt_db_backup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if ((MainWindow.getActiveTab() != null) && (MainWindow.getActiveTab() instanceof JQueryPane)) {
					Backup backup = new Backup();
					backup.start();
					backup.stopBackup();
				}
			}
        });
        JCheckButton bt_db_restore = new JCheckButton(ClassLoader.getSystemResource("data_restore.png"), new Dimension(48, 48), "");
        bt_db_restore.setToolTipText("<html>Restaura um backup salvo anteriormente.<br>*** Apenas backups feitos pelo JQueryAnalizer.</html>");
        bt_db_restore.setEnabled(true);
        bt_db_restore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if ((MainWindow.getActiveTab() != null) && (MainWindow.getActiveTab() instanceof JQueryPane)) {
					Restore restore = new Restore();
					restore.start();
				}
			}
        });
        JCheckButton bt_db_copy = new JCheckButton(ClassLoader.getSystemResource("data_copy.png"), new Dimension(48, 48), "");
        bt_db_copy.setToolTipText("Copia os dados de uma database (A) diretamente em outra database (B).");
        bt_db_copy.setEnabled(true);
        bt_db_copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if ((MainWindow.getActiveTab() != null) && (MainWindow.getActiveTab() instanceof JQueryPane)) {
					@SuppressWarnings("unused")
					Copy copy = new Copy();
				}
			}
        });
        JCheckButton bt_db_repair = new JCheckButton(ClassLoader.getSystemResource("data_hammer.png"), new Dimension(48, 48), "");
        bt_db_repair.setToolTipText("Inicia o processo completo de reparação das tabelas de uma database disponivel.");
        bt_db_repair.setEnabled(true);
        bt_db_repair.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if ((MainWindow.getActiveTab() != null) && (MainWindow.getActiveTab() instanceof JQueryPane)) {
					@SuppressWarnings("unused")
					Repair repair = new Repair();
				}
			}
        });
        JCheckButton bt_console = new JCheckButton(ClassLoader.getSystemResource("data_console.png"), new Dimension(48, 48), "");
        bt_console.setToolTipText("Console Especial");
        bt_console.setEnabled(true);
        bt_console.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if ((MainWindow.getActiveTab() != null) && (MainWindow.getActiveTab() instanceof JQueryPane)) {
					@SuppressWarnings("unused")
					Console console = new Console();
				}
			}
        });
        JCheckButton bt_db_pefformace = new JCheckButton(ClassLoader.getSystemResource("data_gauge.png"), new Dimension(48, 48), "");
        bt_db_pefformace.setToolTipText("Verifica a performace da base.");
        bt_db_pefformace.setEnabled(true);
        bt_db_pefformace.addActionListener(new ActionListener(){ 
        	public void actionPerformed(ActionEvent event) {
				addPanel(1);
			}
        });
        
        _MENU_MAIN = new JPanel();
        _MENU_MAIN.setOpaque(true);
        _MENU_MAIN.setBackground(new Color(200,200,200));
        _MENU_MAIN.setAlignmentY(SwingConstants.SOUTH);
        _MENU_MAIN.add(bt_connection);
        _MENU_MAIN.add(new JBraketButton(48));
        _MENU_MAIN.add(bt_db_copy,null);
        _MENU_MAIN.add(bt_db_restore,null);
        _MENU_MAIN.add(bt_db_backup,null);
        _MENU_MAIN.add(new JBraketButton(48));
        _MENU_MAIN.add(bt_db_repair,null);
        _MENU_MAIN.add(bt_console,null);
        _MENU_MAIN.add(bt_db_pefformace,null);
        

        

        _MENU_MAIN.setBorder(border_left);
        
        _MAIN.getContentPane().add(_MENU_MAIN, _layout);
        
        
        _layout.insets = new Insets(5,0,0,5);
        _layout.gridx = 2;
        _layout.gridy = 1;
        _layout.weightx = 0.999;
        _layout.weighty = 0.001;
        
        JLabel ToolBarLog = new JLabel();
        ToolBarLog.setOpaque(true);
        ToolBarLog.setBackground(new Color(200,200,200));
        ToolBarLog.setBorder(border_right);
        _MAIN.getContentPane().add(ToolBarLog, _layout);
        
        _layout.insets = new Insets(0,5,3,5);
        _layout.gridx = 1;
        _layout.gridy = 2;
        _layout.gridwidth =  2;
        _layout.weightx = 1.000;
        _layout.weighty = 0.001;
        _layout.anchor = GridBagConstraints.WEST;
        
        _PARAMETERS = createParametersPanel();
        _MAIN.getContentPane().add(_PARAMETERS, _layout);
        
        _layout.insets = new Insets(0,5,5,5);
        _layout.gridx = 1;
        _layout.gridy = 3;
        _layout.gridwidth =  2;
        _layout.weightx = 1;
        _layout.weighty = 0.999;
        
        _tabbed_pane = new JTabbedPane();
        _tabbed_pane.setOpaque(true);
        _tabbed_pane.setBorder(BorderFactory.createEmptyBorder());
        _tabbed_pane.setTabPlacement(JTabbedPane.TOP);
        _tabbed_pane.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
        _tabbed_pane.setOpaque(true);
        _tabbed_pane.setFocusable(false);
        _MAIN.getContentPane().add(_tabbed_pane, _layout);
        
        _tab_query_icon = new ImageIcon(ClassLoader.getSystemResource("window.png"));
        //_tab_query_icon.setImage(_tab_query_icon.getImage().getScaledInstance(32, 32, 50));
        _tab_query_icon.setImage(_tab_query_icon.getImage().getScaledInstance(24, 24, 50));
        
        _tab_gauge_icon = new ImageIcon(ClassLoader.getSystemResource("gauge.png"));
        //_tab_gauge_icon.setImage(_tab_gauge_icon.getImage().getScaledInstance(32, 32, 50));
        _tab_gauge_icon.setImage(_tab_gauge_icon.getImage().getScaledInstance(24, 24, 50));
        
        _tab_add_icon = new ImageIcon(ClassLoader.getSystemResource("window_new.png"));
        //_tab_add_icon.setImage(_tab_add_icon.getImage().getScaledInstance(32, 32, 50));
        _tab_add_icon.setImage(_tab_add_icon.getImage().getScaledInstance(24, 24, 50));
        
        _tab_del_icon = new ImageIcon(ClassLoader.getSystemResource("window_delete.png"));
        //_tab_del_icon.setImage(_tab_del_icon.getImage().getScaledInstance(32, 32, 50));
        _tab_del_icon.setImage(_tab_del_icon.getImage().getScaledInstance(24, 24, 50));
        
        final JPanel tab_del = new JPanel();
        tab_del.setName("del");
        final JPanel tab_add = new JPanel();
        tab_add.setName("add");
        JQueryPane query_panel = new JQueryPane();
        query_panel.setName("main");
        
        
        
        _tabbed_pane.addTab(" Query ", _tab_query_icon, query_panel, "Guia de scripts/consultas.");
        _tabbed_pane.addTab(null, _tab_add_icon, tab_add, "Adiciona uma nova guia de consulta.");
        _tabbed_pane.addTab(null, _tab_del_icon, tab_del, "Remove a guia selecionada atualmente.");
        _tabbed_pane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (_tabbed_pane.getSelectedComponent() != null) {
					Component component = _tabbed_pane.getComponentAt(_ACTIVE_TAB_INDEX);
					String message = null;
					// -- ação selecionada...
					switch (_tabbed_pane.getSelectedComponent().getName() == null ? "" : _tabbed_pane.getSelectedComponent().getName()) {
						case "add":
							addPanel(0);		
							break;
						case "del":
							if (component != null && component.getName() != null && component.getName().equalsIgnoreCase("main")) {
								message = "Esta é a guia de consulta principal, ela não pode ser excluida.";
							}
							else {
								if (component instanceof JQueryPane) {
									JQueryPane a = (JQueryPane)component;
									if (a.isConnected()) {
										a.closeConnection();
									}
								}
								else if (component instanceof JPefformacePane) {
									JPefformacePane b = (JPefformacePane)component;
									if (b.isConnected()) {
										b.closeConnection();
									}
								}
								_tabbed_pane.setName(String.valueOf(_ACTIVE_TAB_INDEX));
								_tabbed_pane.setSelectedIndex(0);
								_ACTIVE_TAB_INDEX = Integer.parseInt(_tabbed_pane.getName() == null ? "0" : _tabbed_pane.getName());
								_tabbed_pane.removeTabAt(_ACTIVE_TAB_INDEX);
							}
							_ACTIVE_TAB_INDEX = 0;
							_ACTIVE_TAB = _tabbed_pane.getComponentAt(0);
							_tabbed_pane.setSelectedIndex(_ACTIVE_TAB_INDEX);
							if (message != null) {
								JOptionPane.showMessageDialog(null, message, "Aviso!", JOptionPane.OK_OPTION/*, new ImageIcon(ClassLoader.getSystemResource("JQueryAnalizer.png"))*/);
								return;
							}
							break;
					}
				}
				
				_ACTIVE_TAB_INDEX = _tabbed_pane.getSelectedIndex();
				_ACTIVE_TAB = _tabbed_pane.getSelectedComponent();
				if (_ACTIVE_TAB == null) {
					System.out.println("--> painel inválido selecionado!");
				}
				else if (_ACTIVE_TAB instanceof JTabPanel) {
					JTabPanel tab = ((JTabPanel)_ACTIVE_TAB);
        			JParametersPanel parameters = tab.getParameters();
        			SQLConnectionManager con = tab.getConnection(); 
        			if (parameters != null && con != null) {
        				//System.out.println("[painel selecionado] -> tipo de conexao: "+con.getServerType() + " \\ host: " + parameters.getHost() + " | isConnected? " + con.isConnected());
        				updateParametersComponent(parameters);
        				JCheckButton bt = null;
        				switch (con.getConnectionStatus()) {
        					case 0:
        						bt = new JCheckButton(ClassLoader.getSystemResource("server_down.png"), new Dimension(48, 48), "bt_connection");
        						break;
        					case 1:
        					case 2:
        						bt = new JCheckButton(ClassLoader.getSystemResource("server_up.png"), new Dimension(48, 48), "bt_connection");
        						break;
       					}
						bt.setToolTipText("Conecta ao servidor.");
    					MainWindow.getMenuMain().remove(0);
    					MainWindow.getMenuMain().add(bt, 0);
        			}
		        }	
				fullRepaint();
			}
        });
        
        _ACTIVE_TAB_INDEX = 0;
        _ACTIVE_TAB = _tabbed_pane.getComponent(_ACTIVE_TAB_INDEX);
        if (_ACTIVE_TAB instanceof JTabPanel) {
        	((JTabPanel)_ACTIVE_TAB).setParameters(_PARAMETERS);
        }
        
		
        _MAIN.setVisible(Boolean.parseBoolean(MainWindow.getPropertie("main_window_visible", "true")));
        
        System.out.println("----------------------------------------------------------------");
        System.out.println(" BASE DIR: " + System.getProperty("user.dir"));                
        System.out.println("----------------------------------------------------------------");
        System.out.println(" CONFIG PROPERTIES: " + _CONFIG_FILE);
        System.out.println("----------------------------------------------------------------");
        Set<Entry<Object, Object>> object = _CONFIG.entrySet();
        int i = 0;
        for (Object obj : object.toArray()) {
        	System.out.println(" " + (++i) + ". " + obj.toString().toUpperCase());
        }
        System.out.println("----------------------------------------------------------------");
		for (Component c : _tabbed_pane.getComponents()) {
			if (c == null) {
				continue;
			}
			else if (c instanceof JTabPanel) {
				Thread t = new Thread(new JThreadCommands(0, null, c));
				t.start();
			}
		}
	}
	
	private void addPanel(int type) {
		JTabPanel new_panel = null;
		String name = null;
		ImageIcon icon = this._tab_gauge_icon;
		switch (type) {
		case 0: // jquerypanel
			name = " Query ";
			icon = _tab_query_icon;
			new_panel = new JQueryPane();
			break;
		case 1: // jpefformacepanel
			name = " Desempenho ";
			icon 	  = _tab_gauge_icon;
			new_panel = new JPefformacePane();
			break;
		}
		
		final JPanel tab_del = new JPanel();
        tab_del.setName("del");
        final JPanel tab_add = new JPanel();
        tab_add.setName("add");
		
        JParametersPanel parameters = createParametersPanel();
        new_panel.setParameters(parameters);
        _tabbed_pane.removeTabAt(_tabbed_pane.getTabCount() - 1);
        _tabbed_pane.removeTabAt(_tabbed_pane.getTabCount() - 1);
        _tabbed_pane.addTab(name, icon, new_panel, null);
        _tabbed_pane.addTab(null, _tab_add_icon, tab_add, "Adiciona uma nova guia de consulta.");
        _tabbed_pane.addTab(null, _tab_del_icon, tab_del, "Remove a guia selecionada atualmente.");
        
		// ---
        updateParametersComponent(parameters);
		_ACTIVE_TAB_INDEX = _tabbed_pane.getTabCount() - 3;
		_tabbed_pane.setSelectedIndex(_ACTIVE_TAB_INDEX);
		_ACTIVE_TAB = _tabbed_pane.getSelectedComponent();
        _tabbed_pane.revalidate();
        JThreadCommands tc = new JThreadCommands(0, null, new_panel);
		Thread t = new Thread(tc);
		t.start();
        fullRepaint();
	}
	
	public static Dimension getMainWindowSize() {
		return _MAIN.getSize();
	}
	
	
	/** Retorna uma nova tab de parametros */
	public JParametersPanel createParametersPanel() {
		final JParametersPanel panel = new JParametersPanel();
        panel.setName("parameters");
        panel.setDefaultBorder();
        return panel;
	}
	
	
	public static JCheckButton getMenuMainButton(String name) {
		for (int i = 0; i < _MENU_MAIN.getComponentCount(); i++) {
			Component c = _MENU_MAIN.getComponent(i);
			if (c == null) continue;
			else if (c instanceof JCheckButton && c.getName().equalsIgnoreCase(name)) return (JCheckButton)c;
		}
		return null;
	}
			
	public static String parseToHtml(String text) {
		text = text.replace("&", "&amp;");
		text = text.replace(" ", "&nbsp;");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		text = text.replace("á","&aacute;"); text = text.replace("â","&acirc;");
		text = text.replace("à","&agrave;"); text = text.replace("ã","&atilde;");
		text = text.replace("ç","&ccedil;"); text = text.replace("é","&eacute;");
		text = text.replace("ê","&ecirc;");  text = text.replace("í","&iacute;");
		text = text.replace("ó","&oacute;"); text = text.replace("ô","&ocirc;");
		text = text.replace("õ","&otilde;"); text = text.replace("ú","&uacute;");
		text = text.replace("ü","&uuml;");	 text = text.replace("Á","&Aacute;");
		text = text.replace("Â","&Acirc;");  text = text.replace("À","&Agrave;");
		text = text.replace("Ã","&Atilde;"); text = text.replace("Ç","&Ccedil;");
		text = text.replace("É","&Eacute;"); text = text.replace("Ê","&Ecirc;");
		text = text.replace("Í","&Iacute;"); text = text.replace("Ó","&Oacute;");
		text = text.replace("Ô","&Ocirc;");  text = text.replace("Õ","&Otilde;");
		text = text.replace("Ú","&Uacute;"); text = text.replace("Ü","&Uuml;");
		
		text = text.replace("\n", "<br>");
		return text;
	}
	
	public static String parseHtmlToPlain(String text) {
		text = text.replace("&amp;","&");
		text = text.replace("&nbsp;"," ");
		text = text.replace("&lt;","<");
		text = text.replace("&gt;",">");
		text = text.replace("&aacute;","á"); text = text.replace("&acirc;","â");
		text = text.replace("&agrave;","à"); text = text.replace("&atilde;","ã");
		text = text.replace("&ccedil;","ç"); text = text.replace("&eacute;","é");
		text = text.replace("&ecirc;","ê");  text = text.replace("&iacute;","í");
		text = text.replace("&oacute;","ó"); text = text.replace("&ocirc;","ô");
		text = text.replace("&otilde;","õ"); text = text.replace("&uacute;","ú");
		text = text.replace("&uuml;","ü");	 text = text.replace("&Aacute;","Á");
		text = text.replace("&Acirc;","Â");  text = text.replace("&Agrave;","À");
		text = text.replace("&Atilde;","Ã"); text = text.replace("&Ccedil;","Ç");
		text = text.replace("&Eacute;","É"); text = text.replace("&Ecirc;","Ê");
		text = text.replace("&Iacute;","Í"); text = text.replace("&Oacute;","Ó");
		text = text.replace("&Ocirc;","Ô");  text = text.replace("&Otilde;","Õ");
		text = text.replace("&Uacute;","Ú"); text = text.replace("&Uuml;","Ü");
		
		text = text.replace("<br>","\n");
		text = text.replace("<html>","");
		text = text.replace("</html>","");
		text = text.replace("<body leftmargin=3 topmargin=3 marginwidth=3 marginheight=3>","");
		text = text.replace("</body>","");
		return text;
	}
	
	
	public static void setActiveTab(Component c) {
		for (int i = 0; i < _tabbed_pane.getComponentCount(); i++) {
			if (c == _tabbed_pane.getComponent(i)) {
				_ACTIVE_TAB = c;
				_ACTIVE_TAB_INDEX = i;
				_tabbed_pane.setSelectedIndex(i);
			}
		}
	}
	
	public static Component getActiveTab() {
		return _ACTIVE_TAB;
	}
	
	public static int getActiveTabIndex() {
		return _ACTIVE_TAB_INDEX;
	}
	
	public static JTabbedPane getTabs() {
		return _tabbed_pane;
	}
	
	public static Component[] getTabComponents() {
		if (_tabbed_pane != null) {
			return _tabbed_pane.getComponents();
		}
		return null;
	}
	
	public static JTabPanel getTabByParametersPanel(JParametersPanel panel) {
		if (panel != null) {
			for (Component p : MainWindow.getTabComponents()) {
				if (p == null) continue;
				else if (p instanceof JTabPanel && ((JTabPanel)p).getParameters() != null && ((JTabPanel)p).getParameters().equals(panel)) {
					return ((JTabPanel)p);
				}
			}			
		}
		return null;
	}
	
	
	public static JQueryPane getQueryPaneByHost(String host) {
		JQueryPane pane = null;
		String pane_host = null;
		for (Component c : getTabs().getComponents()) {
			if (c != null && c instanceof JQueryPane) {
				pane = (JQueryPane)c;
				pane_host = (pane.getParameters() != null && pane.getParameters().getHost() != null ? pane.getParameters().getHost() : "");
				if (pane_host.toLowerCase().contains(host)) {
					return pane;
				}
			}
		}
		return null;
	}
	
	public static JPanel getMenuMain() {
		return _MENU_MAIN;
	}
	
	public Component getComponentByName(String name) {
		
		for (Component c : _MAIN.getContentPane().getComponents()) {
			if (c != null && c.getName() != null && c.getName().equalsIgnoreCase(name)) return c;
		}
		
		return null;
	}
	
	public static void quit() {
		for (WindowListener listener : _MAIN.getWindowListeners()) {
			if (listener != null) {
				listener.windowClosing(null);
			}
		}
	}
	
	public void updateParametersComponent(JParametersPanel parameters) {
		_PARAMETERS = parameters;
        Component comp = getComponentByName("parameters");
        if (comp != null) {
        	_MAIN.remove(comp);
            _layout.insets = new Insets(0,5,3,5);
            _layout.gridx = 1;
            _layout.gridy = 2;
            _layout.gridwidth =  2;
            _layout.weightx = 1.000;
            _layout.weighty = 0.001;
            _layout.fill = GridBagConstraints.BOTH;
            _layout.anchor = GridBagConstraints.WEST;
            _MAIN.add(_PARAMETERS, _layout);
            _MAIN.revalidate();
        }
		if (_PARAMETERS != null) {
			for (ComponentListener listener : _PARAMETERS.getComponentListeners()) {
				listener.componentResized(null);
			}
		}
		_PARAMETERS.revalidate();
	}
	
	
	public static void expandAll(JTree tree, boolean expand) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();
	    expandAll(tree, new TreePath(root), expand);
	}
	public static void expandAll(JTree tree, TreePath parent, boolean expand) {
	    TreeNode node = (TreeNode)parent.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	        for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
	            TreeNode n = (TreeNode)e.nextElement();
	            TreePath path = parent.pathByAddingChild(n);
	            expandAll(tree, path, expand);
	        }
	    }
	    if (expand) { tree.expandPath(parent); }
	    else { tree.collapsePath(parent); }
	}
	public static String getVMProperty(String key) {
		String property = System.getProperty(key);
		if (property != null) return property;
		return null;
	}
	
	public static SQLConnectionManager getActiveTabConnection() {
		if (_ACTIVE_TAB instanceof JQueryPane) {
			JQueryPane pane = (JQueryPane)_ACTIVE_TAB;
			return pane.getConnection();
		}
		return null;
	}
	
	public static String md5(String senha){ 
		if (senha == null) {
			return "MD5 Indefinido";
		}
		String sen = "";  
	    MessageDigest md = null;  
	    try {  
	    	md = MessageDigest.getInstance("MD5");  
	        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));  
	        sen = hash.toString(16);           
	    } 
	    catch (NoSuchAlgorithmException e) {  
	    	e.printStackTrace();  
	    }       
	    return sen;  
	}
	
	public static JFrame getMainFrame() {
		return _MAIN;
	}

	public static String getSQLSafeString(String s) {
		if (s == null) {
			return null; 
		}
		else {
			s = s.replace("'", "''");
			s = s.replace("\\", "\\\\");
			return s;
		}
	}
	
	// -- metodos adicionados dia 05/11/2014
	public static void fullRepaint() {
		SwingUtilities.invokeLater(new Runnable(){ public void run() { if (_MAIN != null && _MAIN.isVisible()) { _MAIN.repaint(); } } });
	}
	
	public static void repaintCurrentTab() {
		Thread t = new Thread(new Runnable(){
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run() {
							getActiveTab().repaint();
						}
					});
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		});
		t.start();
	}
	
	public static void saveFullHistory() {
		// -- coleta todos os logs e prepara para salvar...
		StringBuffer out = new StringBuffer();
		List<JHistory> list = new ArrayList<JHistory>();
		JHistory[] hists = null;
		Component[] comps = getTabComponents();
		if (comps != null) {
			for (Component comp : comps) {
				if (comp instanceof JQueryPane) {
					JQueryPane qp = (JQueryPane) comp;
					hists = qp.getHistory();
					if (hists != null) {
						for (JHistory hist : hists) {
							list.add(hist);
						}
					}
				}
			}
		}
		for (JHistory hist : list) {
			out.append(hist.getLog());
		}
		//System.out.println("-------------------------------------------------\n" + out.toString());
		final String[] extensions = new String[]{".SQL"};
		JFileChooser eleitor = new JFileChooser(MainWindow.getPropertie("save_log_file", "C:\\"));
		eleitor.setToolTipText("Selecione o arquivo para abrir");
		eleitor.setApproveButtonText("Salvar arquivo de histórico...");
		eleitor.setCurrentDirectory(new File("%root%"));
		eleitor.setFileFilter(new FileFilter(){
			public boolean accept(File file) {
				if (file == null || file.getName() == null || file.getName().isEmpty()) {
					return false;
				}
				String name = file.getName();
				if (extensions != null && extensions.length > 0 && name.lastIndexOf(".") >= 0) {
					String ext_a = name.substring(name.lastIndexOf("."));
					for (String ext_b : extensions) {
						if (ext_b != null && ext_a.toLowerCase().contains(ext_b.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
				return true;
			}
			public String getDescription() {
				return "Scripts SQL";
			}
			
		});
		int result = eleitor.showSaveDialog(_MAIN);
		if (result == JFileChooser.APPROVE_OPTION) {
			String name = (eleitor.getSelectedFile() != null ? eleitor.getSelectedFile().getName() : "");
			String path = (eleitor.getSelectedFile() != null ? eleitor.getSelectedFile().getAbsolutePath() : "");
			path = path.replace(name, "");
			if (name != null && !name.toLowerCase().contains(".") && (eleitor.getSelectedFile() != null && !eleitor.getSelectedFile().exists())) {
				name += ".SQL";
			}
			path += name;
			MainWindow.setPropertie("save_log_file", path);
			MainWindow.saveProperties();
			try {
				FileOutputStream fout = new FileOutputStream(new File(path));
				if (fout != null) {
					fout.write(out.toString().getBytes("UTF-8"));
					fout.close();
				}
				if (MainWindow.getActiveTab() instanceof JQueryPane) {
					((JQueryPane)MainWindow.getActiveTab()).updateStatus("Exporção dos logs <b>concluída</b> com sucesso!<br>\\:> <b>OK</b>");	
				}
				Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + path);
			}
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}
