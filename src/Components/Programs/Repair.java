package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JParametersPanel;
import Components.MainWindowComponents.JProgressLabel;
import Components.MainWindowComponents.JQueryPane;
import Components.Programs.JCheckTreeNode;


/** LIMPESA DAS CLASSES INTERNAS - ESTAS CLASSES FORAM COLOCADAS EXTERNAMENTE */
public class Repair {
	private SQLConnectionManager _CONNECTION;
	private JFrame _DIALOG = null;
	private JTree _list;
	private JButton _run;
	private JButton _close;
	private List<String> _TABLE_LIST = new ArrayList<String>();
	private int _TABLE_LIST_SIZE;
	private int _TABLE_LIST_ERROR;
	private String _SELECTED_DATABASE;
	private String _SELECTED_HOST;
	private JProgressLabel _progress;
	private Thread _thread;
	private JTree _database;
	private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
	
	private	ImageIcon check;
	private ImageIcon uncheck;

	private JLabel _text4;
	private JComboBox<String> _primary_type;
	private JCheckBox _primary_autoinc;
	private ImageIcon check_16;
	private ImageIcon uncheck_16;
	private JCheckBox _repair;
	private JCheckBox _primary;
	private JCheckBox _innodb;
	private JCheckBox _optmize;
	private JTextField _primary_name;
	private JComboBox<String> _engine;

	
	public Repair() {
		check_16 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check_16.setImage(check_16.getImage().getScaledInstance(16, 16, 100));
		uncheck_16 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck_16.setImage(uncheck_16.getImage().getScaledInstance(16, 16, 100));
		check = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check.setImage(check.getImage().getScaledInstance(24, 24, 100));
		uncheck = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck.setImage(uncheck.getImage().getScaledInstance(24, 24, 100));

		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQuery Analizer - Reparação e otimização de tabelas [MySQL]");
		_DIALOG.setMaximumSize(new Dimension(500,550));
		_DIALOG.setMinimumSize(new Dimension(500,550));
		_DIALOG.setPreferredSize(new Dimension(500,550));
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_DIALOG.setIconImages(MainWindow.getMainIconList());

		JLabel text2 = new JLabel("<html>Selecione a <b>database</b>:</html>");
		text2.setFont(_default_font);
		text2.setForeground(Color.DARK_GRAY);
		text2.setBounds(10,5,475,20);
		_DIALOG.add(text2);
		
		_database = new JTree(new DefaultMutableTreeNode("Carregando lista de databases, aguarde!"));
		_database.setFont(_default_font);
		_database.setOpaque(true);
		_database.setBorder(null);
		_database.setCellRenderer(new DatabaseTreeCellRender(null, null));
		_database.setRowHeight(22);
		JScrollPane scrolls_1 = new JScrollPane(_database);
		scrolls_1.setBounds(10,25,215,274);
		scrolls_1.setAutoscrolls(true);
		_DIALOG.add(scrolls_1);
		_database.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') {
					Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
					t.start(); 
				}
			}
			public void keyReleased(KeyEvent event) { }
			public void keyTyped(KeyEvent event) { }
		});
		_database.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() > 1) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem item1 = new JMenuItem("Selecionar este banco");
					item1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
							t.start();
						}
					});
					menu.add(item1);
					JMenuItem item2 = new JMenuItem("Atualizar lista");
					item2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) { 
							Thread t = new Thread(new DatabaseList());
							t.start();
						}
					});
					menu.add(item2);
					menu.show(_database, event.getX(), event.getY());
				}
			}
			public void mouseEntered(MouseEvent event) { }
			public void mouseExited(MouseEvent event) { }
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) {
					String database = (_database.getSelectionPath() != null ? _database.getSelectionPath().toString() : "");
					Thread t = new Thread(new selectDatabase(database));
					t.start();
				} 
			}
			public void mouseReleased(MouseEvent event) { }				
		});
		_database.setBorder(new AbstractBorder(){
			private static final long serialVersionUID = 7066818237310557988L;

			public Insets getBorderInsets(Component c) {
				return new Insets(2,2,2,2);
			}
		});
		
		JLabel text3 = new JLabel("<html>Selecione as <b>tabelas</b> a serem reparadas:</html>");
		text3.setFont(_default_font);
		text3.setForeground(Color.DARK_GRAY);
		text3.setBounds(230,5,475,20);
		_DIALOG.add(text3);
		
		_list = new JTree(new DefaultMutableTreeNode("Aguarde!"));
		_list.setRowHeight(22);
		_list.setCellRenderer(new TableTreeCellRender());
		_list.setOpaque(true);
		_list.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				if ((event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') && event.getComponent() != null && event.getComponent() instanceof JTree) { selectTable(); }
			}
			public void keyReleased(KeyEvent a) { }
			public void keyTyped(KeyEvent a) { }
		});
		_list.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent event) { }
			public void mouseEntered(MouseEvent event) { }
			public void mouseExited(MouseEvent event) { }
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectTable(); } 
			}
			public void mouseReleased(MouseEvent event) { }
		});
		_list.setBorder(new AbstractBorder(){
			private static final long serialVersionUID = 7066818237310557988L;

			public Insets getBorderInsets(Component c) {
				return new Insets(2,2,2,2);
			}
		});
		
		JScrollPane scrolls = new JScrollPane(_list);
		scrolls.setBounds(230,25,255,274);
		scrolls.setAutoscrolls(true);
		_DIALOG.add(scrolls);
			
		// programas de reparação
		JLabel text6 = new JLabel();
		text6.setFont(_default_font);
		text6.setBounds(10,305,475,50);
		text6.setOpaque(true);
		text6.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(190,190,190), 1, true), "<html>&nbsp;Opções de <b>reparação/otimização</b> das tabelas selecionadas:&nbsp;</html>", 1, 0, new Font("Verdana", Font.ROMAN_BASELINE, 11), Color.BLACK));
		_DIALOG.add(text6);

		_repair = new JCheckBox("REPAIR");
		_repair.setFont(_default_font);
		_repair.setForeground(Color.BLACK);
		_repair.setBounds(8,18,80,24);
		_repair.setIcon(uncheck);
		_repair.setSelectedIcon(check);
		_repair.setFocusable(false);
		_repair.setSelected(true);
		text6.add(_repair);
		
		_primary = new JCheckBox("PRIMARY KEY");
		_primary.setFont(_default_font);
		_primary.setForeground(Color.BLACK);
		_primary.setBounds(108,18,120,24);
		_primary.setIcon(uncheck);
		_primary.setSelectedIcon(check);
		_primary.setFocusable(false);
		_primary.setSelected(true);
		_primary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_primary_name.setEnabled(_primary != null && _primary.isSelected());
				_primary_type.setEnabled(_primary != null && _primary.isSelected());
				_primary_autoinc.setEnabled(_primary != null && _primary.isSelected());
			}
		});

		text6.add(_primary);
		
		_innodb = new JCheckBox("");
		_innodb.setFont(_default_font);
		_innodb.setForeground(Color.BLACK);
		_innodb.setBounds(228,18,32,24);
		_innodb.setIcon(uncheck);
		_innodb.setSelectedIcon(check);
		_innodb.setFocusable(false);
		_innodb.setSelected(true);
		_innodb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				_engine.setEnabled(_innodb.isSelected());
			}
		});
		text6.add(_innodb);
		
		
		_engine = new JComboBox<String>();
		_engine.setFont(_default_font);
		_engine.setForeground(Color.BLACK);
		_engine.setBounds(262, 20, 100, 21);
		_engine.setEditable(true);
		_engine.addItem("InnoDB");
		_engine.addItem("MyISAM");
		_engine.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				Object item = _engine.getSelectedItem();
				if (item != null && !item.toString().isEmpty()) {
					_innodb.setName(item.toString());
				}
			}
		});
		_engine.setSelectedItem("InnoDB");
		text6.add(_engine);
		
		_optmize = new JCheckBox("OPTMIZE");
		_optmize.setFont(_default_font);
		_optmize.setForeground(Color.BLACK);
		_optmize.setBounds(373,18,90,24);
		_optmize.setIcon(uncheck);
		_optmize.setSelectedIcon(check);
		_optmize.setFocusable(false);
		_optmize.setSelected(true);
		text6.add(_optmize);			
		
		// chave primária
		JLabel text1 = new JLabel();
		text1.setFont(_default_font);
		text1.setBounds(10,360,475,50);
		text1.setOpaque(true);
		text1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(190,190,190), 1, true), "<html>&nbsp;Campo definido como <b>chave primária</b> das tabelas selecionadas:&nbsp;</html>", 1, 0, new Font("Verdana", Font.ROMAN_BASELINE, 11), Color.BLACK));
		_DIALOG.add(text1);
		
		_primary_name = new JTextField("ID");
		_primary_name.setFont(_default_font);
		_primary_name.setBounds(10,18,90,21);
		text1.add(_primary_name);
			
		_primary_type = new JComboBox<String>();
		_primary_type.setFont(_default_font);
		_primary_type.setEditable(true);
		_primary_type.setForeground(Color.BLACK);
		_primary_type.setBounds(105,18,120,21);
		_primary_type.addItem("INT(15)");
		_primary_type.addItem("INTEGER");
		_primary_type.addItem("BIGINT");
		_primary_type.addItem("FLOAT");
		_primary_type.addItem("DOUBLE");
		_primary_type.addItem("DECIMAL(10,2)");
		_primary_type.addItem("NUMERIC(10,2)");
		_primary_type.addItem("VARCHAR(30)");
		text1.add(_primary_type);
		
		_primary_autoinc = new JCheckBox("AUTO INCREMENT");
		_primary_autoinc.setFont(_default_font);
		_primary_autoinc.setForeground(Color.BLACK);
		_primary_autoinc.setBounds(228,16,200,24);
		_primary_autoinc.setIcon(uncheck);
		_primary_autoinc.setSelectedIcon(check);
		_primary_autoinc.setFocusable(false);
		_primary_autoinc.setSelected(true);
		text1.add(_primary_autoinc);
		
		_text4 = new JLabel("<html><b>Progresso</b> da reparação das tabelas:</html>");
		_text4.setFont(_default_font);
		_text4.setBounds(10,414,475,20);
		_text4.setOpaque(true);
		_DIALOG.add(_text4);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,434,475,35);
		_progress.setText("Selecione a database");
		_DIALOG.add(_progress);	
		
		
			
		
		_run = new JButton("<html>Iniciar Reparação</html>");
		_run.setFont(_default_font);
		_run.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				toogleActions(false);
				TreeModel model = _list.getModel();
				int option;
				_TABLE_LIST = new ArrayList<String>(); 
				for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
					if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
						for (int j = 0; j < node.getChildCount(); j++) {
							if (node.getChildAt(j) instanceof JCheckTreeNode) {
								JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j);
								if (row.isSelected()) { _TABLE_LIST.add(row.toString()); }
							}
						}
						option = JOptionPane.showConfirmDialog(null, "Foram selecionadas: " + (_TABLE_LIST.size()) + "/" + (node.getChildCount()) + " tabelas para realizar o backup, existem: " + (node.getChildCount() - _TABLE_LIST.size()) + " tabelas desmarcadas.\nDeseja prosseguir com o backup?" , "Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							_TABLE_LIST_SIZE = _TABLE_LIST.size();
							executeRepair();
						}
						else {
							toogleActions(true);
						}
						break;
					}
				}				
			}			
		});
		_run.setMnemonic(KeyEvent.VK_E);
		_run.setBounds(255,477,150,37);
		_DIALOG.add(_run);
		
		_close = new JButton("<html>Sair</html>");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(_default_font);
		_close.setBounds(410,477,75,37);
		_close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				for (WindowListener listener : _DIALOG.getWindowListeners()) {
					if (listener == null) { continue; }
					listener.windowClosing(null);
				}					
			}
			
		});
		_DIALOG.add(_close);
		
		_DIALOG.setVisible(true);
		
		Thread t = new Thread(new DatabaseList());
		t.start();
		
		_DIALOG.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent a) { }
			public void windowClosed(WindowEvent a) { }
			@SuppressWarnings("deprecation")
			public void windowClosing(WindowEvent arg0) {
				if (_thread != null && _thread.isAlive()) {
						int option = JOptionPane.showConfirmDialog(_DIALOG, "Existe uma reparação de tabelas em execução, tem certeza que deseja interromper esta reparação neste momento?", "JQueryAnalizer - Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							_thread.stop();
							_thread = null;
						}
						else {
							return;
						}
					}
					disconnect();
					MainWindow.saveProperties();
					_DIALOG.dispose();
					_DIALOG = null;
				}
				public void windowDeactivated(WindowEvent a) { }
				public void windowDeiconified(WindowEvent a) { }
				public void windowIconified(WindowEvent a) { }
				public void windowOpened(WindowEvent a) { }
		});
	}
	
	public class selectDatabase implements Runnable {
		private String[] path;
		private String location;
		public selectDatabase(String p) {
			p = p.replace(" ", "");
			p = p.replace("[", "");
			p = p.replace("]", "");
			_progress.setItemProgress(0.f);
			if (p.split(",").length != 3) {
				return;
			}
			path = p.split(",");
			String host = null;
			String server = null;
			try {
				JQueryPane panel = MainWindow.getQueryPaneByHost(path[1]); 
				host = panel.getParameters().getHost();
				server = (panel.getConnection().getServerType() == 0 ? "MySQL://" : "MsSQL://");
			}
			catch (Exception e) { }
			location = (server != null ? server : "") + (host != null ? host : "") + "/<u>"+path[2]+"</u>";
		}
		public void run() {
			if (path == null) {
				return;
			}
			JQueryPane pane = MainWindow.getQueryPaneByHost(path[1]);
			JParametersPanel parameters = (pane != null ? pane.getParameters() : null);
			if (pane == null || parameters == null) {
				JOptionPane.showMessageDialog(null, "Não foi possível selecionar a database: " + path[3], "Aviso!", JOptionPane.OK_OPTION);
				_progress.setText("<html><font color='red'>Não foi possível conectar a database selecionada</font></html>");
				return;
			}
			_SELECTED_HOST = parameters.getHost();
			_SELECTED_DATABASE = path[2];
			disconnect();
			_progress.setText("<html><font color='blue'>Aguardando conexão com o servidor <b>"+location+"</b></font></html>");
			_progress.setItemProgress(100.f);
			
			
			reconnect(parameters.getConnectorDriver(), parameters.getConnectionString(), parameters.getUser(), parameters.getPass());
			if (_CONNECTION != null && _CONNECTION.isConnected()) {
				((DatabaseTreeCellRender)_database.getCellRenderer()).updateSelectionDatabase(_SELECTED_DATABASE, _SELECTED_HOST);
				_progress.setText("<html><font color='blue'>Obtendo a lista das tabelas disponíveis... Aguarde!</font></html>");
				_progress.setItemProgress(100.f);
				getTableList(path[2]);
				_database.revalidate();
				_database.repaint();
				_progress.setText("<html><font color='green'>Conectado a database <b>"+location+"</b></font></html>");
			}
			else {
				_progress.setText("<html><font color='red'>Não foi possível conectar a database selecionada</font></html>");
				JOptionPane.showMessageDialog(null,"Voce está desconectado!","Aviso!",JOptionPane.YES_OPTION);
			}
		}
	}
	
	public void selectTable() {
		if (_list == null || (_list != null && _list.getSelectionPath() == null)) {
			return;
		}
		String selection = _list.getSelectionPath().toString();
		selection = selection.replace("[", "");
		selection = selection.replace("]", "");
		selection = selection.replace(" ", "");
		if (selection.split(",").length == 3) {
			TreeModel model = _list.getModel();
			int select_count = 0;
			boolean select_status = true;
			for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
				if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
					for (int j : _list.getSelectionRows()) {
						if (node.getChildAt(j - 2) instanceof JCheckTreeNode) {
							select_count += 1;
							JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j - 2);
							if (select_count == 1) {
								select_status = !row.isSelected();
							}
							row.setSelected(select_status);
						}
					}
				}
			}
		}
		_list.revalidate();
		_list.repaint();
	}
	
	
	public void executeRepair() {
		if (_TABLE_LIST != null) {
			System.gc();
		}
		if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
			doRepair repair = new doRepair(_TABLE_LIST.get(0));
			_thread = new Thread(repair);
			_thread.start();
		}
		else {
			JOptionPane.showMessageDialog(null, "<html>O processo de <b>reparação</b> das tabelas foi concluido.<br>- Tabelas marcadas para reparação: <u>" +_TABLE_LIST_SIZE + "</u><br>- Erros e advertências durante o processo: <u>" + _TABLE_LIST_ERROR + "</u><br><br>*** Opções de selecionadas para a reparação:<br>- Reparar tabelas: " + (_repair.isSelected() ? "SIM" : "NÃO") + "<br>- Redefinir a chave primária: " + (_primary.isSelected() ? "SIM" : "NÃO") + "<br>- Alterar o Engine das tabelas para " + _innodb.getName() + ": " + (_innodb.isSelected() ? "SIM" : "NÃO") + "<br>- Otimizar tabelas: " + (_optmize.isSelected() ? "SIM" : "NÃO") + "</html>", "JQueryAnalizer - Conclusão!", JOptionPane.OK_OPTION);
			toogleActions(true);
		}
	}
	
	public void toogleActions(boolean state) {
		this._repair.setEnabled(state);
		this._primary.setEnabled(state);
		this._innodb.setEnabled(state);
		this._engine.setEnabled(state);
		this._optmize.setEnabled(state);
		this._primary_name.setEnabled(state);
		this._primary_type.setEnabled(state);
		this._primary_autoinc.setEnabled(state);
		
		
		_list.setEnabled(state);
		_run.setEnabled(state);
		_close.setEnabled(state);
		_database.setEnabled(state);
	}
	
	public void stopRepair() {
		if (_thread != null && _thread.isAlive()) {
			_thread.interrupt();
			_thread = null;
		}
	}
	
	public class DatabaseList implements Runnable {
		private int _DATABASE_COUNT;
		public DatabaseList() {
			super();
			_SELECTED_DATABASE = null;
			_SELECTED_HOST = null;
		}
		public void run() {
			_DATABASE_COUNT = 0;			
			JTabbedPane tabs = MainWindow.getTabs();
			Component c = null;
			JQueryPane pane = null;
			SQLConnectionManager con = null;
			ResultSet rs = null;
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Conexões disponíveis");
			DefaultTreeModel model = (DefaultTreeModel)_database.getModel();
			String host = null;
			for (int i = 0; i < tabs.getComponentCount(); i++) {
				c = tabs.getComponent(i);
				if (c != null && c instanceof JQueryPane) {
					pane = (JQueryPane)c;
					con = pane.getConnection();
					host = pane.getParameters().getHost().split(":")[0]; 
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(host != null ? host : "?");
					root.add(child);
					if (con != null && con.isConnected()) {
						if (con.getServerType() != 0) continue;
						try {
							rs = con.executeQuery("SHOW DATABASES");
							while(rs.next()) {
								child.add(new DefaultMutableTreeNode(rs.getString(1)));
							}
							if (child.getChildCount() > 0) {
								_DATABASE_COUNT += child.getChildCount();
							}
						}
						catch (Exception e) { e.printStackTrace(); }
					}
				}
			}
			model.setRoot(root);
			_database.revalidate();
			MainWindow.expandAll(_database, true);
			getTableList(null);
			
			if (_DATABASE_COUNT == 0) {
				JOptionPane.showMessageDialog(null, "<html><u>Não existem conexões disponíveis</u> com bancos de dados neste momento...<br><br><b>Verifique os parâmetros de conexão e tente novamente em seguida.</b></html>", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
				_DIALOG.dispose();
			}
		}
	}
	
	public void getTableList(String database) {
		if (database == null) {
			((DefaultTreeModel)_list.getModel()).setRoot(new DefaultMutableTreeNode("Selecione uma database!"));
			return;
		}
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(_SELECTED_HOST == null ? "Você está desconectado do banco de dados, selecione a base de dados novamente." : _SELECTED_HOST);
		root.add(new DefaultMutableTreeNode(database == null ? "Você está desconectado ou não há database selecionada!" : database));
		DefaultTreeModel model = (DefaultTreeModel)_list.getModel();
		model.setRoot(root);
		DefaultMutableTreeNode row = null;
		for (int i = 0; i < model.getChildCount(root); i++) {
			row = (DefaultMutableTreeNode)model.getChild(root, i);
			if (row.toString() != null) break;
		}
		row.removeAllChildren();	
		if (_CONNECTION != null && _CONNECTION.isConnected()) {
			_SELECTED_DATABASE = database;
			try {
				_CONNECTION.executeUpdate("USE " + (_CONNECTION.getServerType() == 0 ? "`" + database.trim() + "`" : database));
				List<String> list = _CONNECTION.getTables();
				for (String table : list) {
					row.add(new JCheckTreeNode(table));
				}
				if (list != null && list.size() > 0) {
					_run.setEnabled(true);
				}
				else {
					_run.setEnabled(false);
				}
				model.setRoot((DefaultMutableTreeNode)model.getRoot());
			}
			catch (Exception e) { e.printStackTrace(); }
			_list.revalidate();
			_list.repaint();
			MainWindow.expandAll(_list, true);
		}
	}
	
	
	
	
	public class doRepair implements Runnable {
		private String _TABLE;
		public doRepair (String table) {
			_TABLE = table;
		}
		@SuppressWarnings("unused")
		public void run() {
			try {
				ResultSet rs = null;
				Exception e  = null;
				int steps    = (_repair.isSelected() ? 1 : 0) + (_primary.isSelected() ? 1 : 0) + (_innodb.isSelected() ? 1 : 0) + (_optmize.isSelected() ? 1 : 0);
				int step_cnt = 0;
				_progress.setMainProgress(((_TABLE_LIST_SIZE - _TABLE_LIST.size() * 1.f) / _TABLE_LIST_SIZE) * 100.f);
				
				/** -- REPARA A TABELA ANTES DE EXECUTAR OS DEMAIS COMANDOS -- */
				if (_repair.isSelected()) {
					_progress.setText("<html>Reparando a tabela: <b>" + _TABLE + "</b> <i>" + (++step_cnt) + "/" + (steps) + "</i></html>");
					_progress.setItemProgress(step_cnt * 100.f / steps);
					e = _CONNECTION.executeUpdate("REPAIR TABLE " + _TABLE);
					if (e != null) {
						++_TABLE_LIST_ERROR;
						e.printStackTrace();
					}
				}
				/** -- RECONSTROI A CHAVE PRIMÁRIA -- */
				if (_primary.isSelected()) {
					_progress.setText("<html>Recriando a chave primária da tabela: <u>" + _TABLE + "</u> <i>" + (++step_cnt) + "/" + (steps) + "</i></html>");
					_progress.setItemProgress(step_cnt * 100.f / steps);
					rs = _CONNECTION.executeQuery("DESCRIBE " + _TABLE);
					String  primary_type   = null;
					String  primary_name   = null;
					boolean primary_exists = false;
					boolean primary_equals = false;
					int options            = 0;
					while (rs.next()) {
						if (rs.getString(4).equalsIgnoreCase("pri")) {
							primary_equals = (primary_equals ? true : rs.getString(1).equalsIgnoreCase(_primary_name.getText().trim()));
							e = _CONNECTION.executeUpdate("ALTER TABLE " + _TABLE + " MODIFY COLUMN " + rs.getString(1) + " " + rs.getString(2));
							e = _CONNECTION.executeUpdate("ALTER TABLE `" + _TABLE + "` DROP PRIMARY KEY");
							if (e != null) e.printStackTrace();
						}
						else if (rs.getString(1).equalsIgnoreCase(_primary_name.getText())) {
							e = _CONNECTION.executeUpdate("ALTER TABLE " + _TABLE + " DROP COLUMN " + rs.getString(1).toUpperCase());
							if (e != null) e.printStackTrace();
						}
					}
					e = _CONNECTION.executeUpdate("ALTER TABLE " + _TABLE + " " + (primary_equals ? "MODIFY" : "ADD") + " COLUMN " + _primary_name.getText().toUpperCase().trim() + " " + _primary_type.getSelectedItem().toString().toUpperCase().trim() + " NOT NULL " + (_primary_autoinc.isSelected() ? "AUTO_INCREMENT" : "") + ", ADD PRIMARY KEY (`" + _primary_name.getText().toUpperCase().trim() + "`)");
					if (e != null) {
						++_TABLE_LIST_ERROR;
						e.printStackTrace();
					}
					if (rs != null) rs.close();
				}
				
				/** -- CONVERTE A ENGINE DA TABELA PARA InnoDB -- */
				if (_innodb.isSelected()) {
					_progress.setText("<html>Redefinindo o engine da tabela: <u>" + _TABLE + "</u> para <b>" + _innodb.getName() + "</b> <i>" + (++step_cnt) + "/" + (steps) + "</i></html>");
					_progress.setItemProgress(step_cnt * 100.f / steps);
					e = _CONNECTION.executeUpdate("ALTER TABLE " + _TABLE + " ENGINE=" + _innodb.getName());
					if (e != null) {
						++_TABLE_LIST_ERROR;
						e.printStackTrace();
					}
				}
				/** -- OTIMIZA O DESEMPENHO DA TABELA -- */
				if (_optmize.isSelected()) {
					_progress.setText("<html>Otimizando o desempenho da tabela: <u>" + _TABLE + "</u> <i>" + (++step_cnt) + "/" + (steps) + "</i></html>");
					_progress.setItemProgress(step_cnt * 100.f / steps);
					e = _CONNECTION.executeUpdate("OPTIMIZE TABLE " + _TABLE);
					if (e != null) {
						++_TABLE_LIST_ERROR;
						e.printStackTrace();
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null) {
					JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar o <u>backup</u></b>. A aplicação retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + "</font></html>", "Erro ao executar o backup!", JOptionPane.OK_OPTION);
				}
			}
			if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
				_TABLE_LIST.remove(0);
				executeRepair();
			}
		
		}
	}


	public void reconnect(String driver, String con, String user, String pass) {
		_CONNECTION = new SQLConnectionManager(driver, con, user, pass);
	}
	
	public void disconnect() {
		if (_CONNECTION != null && _CONNECTION.isConnected()) {
			_CONNECTION.closeConnection();
			_CONNECTION = null;
		}
	}
}
