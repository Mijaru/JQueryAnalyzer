package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import javolution.util.FastList;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JParametersPanel;
import Components.MainWindowComponents.JProgressLabel;
import Components.MainWindowComponents.JQueryPane;
import Components.Programs.JCheckTreeNode;

public class ColumnFormat {
	private SQLConnectionManager _CONNECTION;
	private JFrame _DIALOG = null;
	private JTree _list;
	private JButton _run;
	private JButton _close;
	private FastList<String> _TABLE_LIST = new FastList<String>();
	private int _TABLE_LIST_SIZE;
	private String _SELECTED_DATABASE;
	private String _SELECTED_HOST;
	private JProgressLabel _progress;
	private Thread _thread;
	private JTree _database;
	private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
	
	private	ImageIcon check;
	private ImageIcon uncheck;

	FastList<String> _command_regular = new FastList<String>();
	FastList<String> _command_blocked = new FastList<String>();
	
	private JLabel _text4;
	private ImageIcon check_16;
	private ImageIcon uncheck_16;
	private JTable _editor;

	
	public ColumnFormat(SQLConnectionManager con) {
		check_16 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check_16.setImage(check_16.getImage().getScaledInstance(16, 16, 100));
		uncheck_16 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck_16.setImage(uncheck_16.getImage().getScaledInstance(16, 16, 100));
		check = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check.setImage(check.getImage().getScaledInstance(24, 24, 100));
		uncheck = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck.setImage(uncheck.getImage().getScaledInstance(24, 24, 100));

		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQueryAnalizer - Assistente para normalização de campos.");
		Dimension size = new Dimension(500,580);
		_DIALOG.setMaximumSize(size);
		_DIALOG.setMinimumSize(size);
		_DIALOG.setPreferredSize(size);
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_DIALOG.setIconImage(new ImageIcon(ClassLoader.getSystemResource("folder_terminal.png")).getImage());

		// database de origem <-
		JLabel text2 = new JLabel("<html>Selecione a <b>database</b>:</html>");
		text2.setFont(_default_font);
		text2.setForeground(Color.DARK_GRAY);
		text2.setBounds(10,5,475,20);
		_DIALOG.add(text2);
		
		_database = new JTree(new DefaultMutableTreeNode("Carregando lista de databases, aguarde!"));
		_database.setFont(_default_font);
		_database.setOpaque(true);
		_database.setBorder(null);
		_database.setCellRenderer(new DatabaseTreeCellRender(_SELECTED_DATABASE, _SELECTED_HOST));
		_database.setRowHeight(18);
		JScrollPane scrolls_1 = new JScrollPane(_database);
		scrolls_1.setBounds(10,25,215,240);
		scrolls_1.setAutoscrolls(true);
		_DIALOG.add(scrolls_1);
		_database.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') {
					Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
					t.start(); 
				}
			}
			@Override
			public void keyReleased(KeyEvent event) { }
			@Override
			public void keyTyped(KeyEvent event) { }
		});
		_database.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() > 1) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem item1 = new JMenuItem("Selecionar este banco");
					item1.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
							t.start();
						}
					});
					menu.add(item1);
					JMenuItem item2 = new JMenuItem("Atualizar lista");
					item2.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) { 
							Thread t = new Thread(new DatabaseList());
							t.start();
						}
					});
					menu.add(item2);
					menu.show(_database, event.getX(), event.getY());
				}
			}
			@Override
			public void mouseEntered(MouseEvent event) { }
			@Override
			public void mouseExited(MouseEvent event) { }
			@Override
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) {
					Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
					t.start();
				} 
			}
			@Override
			public void mouseReleased(MouseEvent event) { }				
		});

		// tabelas da database a serem salvas <-
		JLabel text3 = new JLabel("<html>Selecione as <b>tabelas</b> para ser alteradas:</html>");
		text3.setFont(_default_font);
		text3.setForeground(Color.DARK_GRAY);
		text3.setBounds(230,5,475,20);
		_DIALOG.add(text3);
		
		_list = new JTree(new DefaultMutableTreeNode("Aguarde!"));
		_list.getRootPane();
		_list.setCellRenderer(new TableTreeCellRender());
		_list.setOpaque(true);
		
		_list.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') && event.getComponent() != null && event.getComponent() instanceof JTree) { selectTable(); }
			}
			@Override
			public void keyReleased(KeyEvent a) { }
			@Override
			public void keyTyped(KeyEvent a) { }
		});
		_list.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event) { }
			@Override
			public void mouseEntered(MouseEvent event) { }
			@Override
			public void mouseExited(MouseEvent event) { }
			@Override
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectTable(); } 
			}
			@Override
			public void mouseReleased(MouseEvent event) { }
		});
		JScrollPane scrolls = new JScrollPane(_list);
		scrolls.setBounds(230,25,255,240);
		scrolls.setAutoscrolls(true);
		_DIALOG.add(scrolls);
			
		// programas de reparação
		JLabel text6 = new JLabel();
		text6.setFont(_default_font);
		text6.setForeground(Color.DARK_GRAY);
		text6.setBounds(10,271,477,167);
		text6.setOpaque(true);
		text6.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(190,190,190), 1, true), " Campos a serem padronizados nas tabelas marcadas: ", 1, 0, new Font("Verdana", Font.ROMAN_BASELINE, 11), Color.DARK_GRAY));
		_DIALOG.add(text6);

		_editor = new JTable();
		_editor.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_editor.setAutoscrolls(true);
		_editor.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 10));
		_editor.setGridColor(Color.DARK_GRAY);
		_editor.setShowGrid(true);
		_editor.setSelectionForeground(Color.BLACK);
		_editor.setSelectionBackground(new Color(200,200,200));
		_editor.setDefaultEditor(String.class, new JCellEditor(new JTextField()));
		_editor.setEnabled(false);
		_editor.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 3407711122579968156L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean pass;
				if (isShowing()) { return this; }
				JLabel root = new JLabel();
				root.setOpaque(true);
				root.setFont(_editor.getFont());
				root.setText(value != null ? " " + value.toString() : " ");
				root.setForeground(Color.BLACK);
				for (int i = table.getRowCount();i > row; i--) {
					pass = true;
					root.setVerticalAlignment(JLabel.CENTER);
					for (int j = 0; j < table.getSelectedRows().length; j++) {
						if (row == table.getSelectedRows()[j]) {
							root.setForeground(_editor.getSelectionForeground());
							root.setBackground(_editor.getSelectionBackground());							
							pass = false;
						}
					}
					if (!pass) { continue; }
					root.setBackground(Color.WHITE);
				}
				return root;
			}
		});

		_editor.addKeyListener(new KeyListener(){
			@Override public void keyPressed(KeyEvent ke) {
				if (ke != null && (ke.getKeyChar() == ' ' || ke.getKeyChar() == '+' || ke.getKeyChar() == '-' || ke.getKeyCode() == KeyEvent.VK_INSERT)) {
					if (ke.getKeyCode() == KeyEvent.VK_INSERT || ke.getKeyChar() == '+') {
						JTableModel model = (JTableModel)_editor.getModel();
						model.addRow(_editor.getSelectedRow(), 1);
					}
					else if (ke.getKeyChar() == '-') {
						JTableModel model = (JTableModel)_editor.getModel();
						model.addRow(_editor.getSelectedRow(), -1);
					}
				}
			}
			@Override public void keyReleased(KeyEvent ke) { }
			@Override public void keyTyped(KeyEvent ke) { }
		});

		JTableModel model = new JTableModel(new String[]{"Option","Column name","Type","Size","Default value"}, new Object[][]{{"","","","",""}});
		_editor.setModel(model);
		_editor.setRowHeight(24);
		
		TableColumnModel column_model = _editor.getColumnModel();
		column_model.setColumnMargin(1);
		column_model.setColumnSelectionAllowed(false);
		TableColumn column = null;
		int width = 0;
		String[] opt = new String[]{"UPDATE","ADD","ALTER", "DEL"};
		String[] typ = new String[]{"*","Int", "BigInt", "Float", "Long", "Short",  "Decimal", "Char", "VarChar", "Numeric", "Date", "DateTime"};
		for (int i = 0; i < column_model.getColumnCount(); i++) {
			column = column_model.getColumn(i);
			switch(i) {
				case 0:
					width = 80;
					column.setCellRenderer(new JComboBoxRenderer(opt));
					column.setCellEditor(new JComboBoxEditor(new JComboBox<String>(), opt));
					break;
					//continue;
				case 1:
					width = 120;
					column.setCellEditor(new JCellEditor(new JTextField()));
					break;
				case 2:
					width = 80;
					column.setCellRenderer(new JComboBoxRenderer(typ));
					column.setCellEditor(new JComboBoxEditor(new JComboBox<String>(), typ));
					break;
				case 3:
					width = 60;
					column.setCellEditor(new JCellEditor(new JTextField()));
					break;
				case 4:
					width = 120;
					column.setCellEditor(new JCellEditor(new JTextField()));
					break;
			}
			column.setPreferredWidth(width);
		}
		
		JScrollPane scrolls1 = new JScrollPane(_editor);
		scrolls1.setBounds(7,20,463,140);
		scrolls1.setBorder(new LineBorder(new Color(190,190,190)));

		text6.add(scrolls1);
		
		_text4 = new JLabel("<html><b>Progresso</b> da execução dos comandos:</html>");
		_text4.setFont(_default_font);
		_text4.setForeground(Color.DARK_GRAY);
		_text4.setBounds(10,440,475,20);
		_text4.setOpaque(true);
		_DIALOG.add(_text4);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,460,475,35);
		_progress.setText("Selecione a database");
		_DIALOG.add(_progress);	
		
		
			
		
		_run = new JButton("<html><u>E</u>xecutar Comandos</html>");
		_run.setFont(_default_font);
		_run.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				toogleActions(false);
				_TABLE_LIST = getTableList();
				if (_TABLE_LIST != null && !_TABLE_LIST.isEmpty()) {
					executeScript();
				}
			}			
		});
		_run.setMnemonic(KeyEvent.VK_E);
		_run.setBounds(255,505,150,37);
		_run.setEnabled(false);
		_DIALOG.add(_run);
		
		_close = new JButton("<html><u>S</u>air</html>");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(_default_font);
		_close.setBounds(410,505,75,37);
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
			@Override
			public void windowActivated(WindowEvent a) { }
			@Override
			public void windowClosed(WindowEvent a) { }
			@SuppressWarnings("deprecation")
			@Override
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
				@Override
				public void windowDeactivated(WindowEvent a) { }
				@Override
				public void windowDeiconified(WindowEvent a) { }
				@Override
				public void windowIconified(WindowEvent a) { }
				@Override
				public void windowOpened(WindowEvent a) { }
		});
	}
	
	
	private FastList<String> getTableList(){
		TreeModel model = _list.getModel();
		int option;
		FastList<String> list = new FastList<String>(); 
		for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
			if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
				for (int j = 0; j < node.getChildCount(); j++) {
					if (node.getChildAt(j) instanceof JCheckTreeNode) {
						JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j);
						if (row.isSelected()) { list.add(row.toString()); }
					}
				}
				option = JOptionPane.showConfirmDialog(null, "Foram selecionadas: " + (list.size()) + "/" + (node.getChildCount()) + " tabelas, existem: " + (node.getChildCount() - _TABLE_LIST.size()) + " tabelas desmarcadas.\nDeseja prosseguir com o a execução dos comandos?" , "JQueryAnalizer - Confirmação", JOptionPane.YES_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					_TABLE_LIST_SIZE = list.size();
				}
				else {
					list.clear();
					toogleActions(true);
				}
				break;
			}
		}				
		return list;
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
			disconnect();
			_progress.setText("<html><font color='blue'>Aguardando conexão com o servidor <b>"+location+"</b></font></html>");
			_progress.setItemProgress(100.f);
			
			
			reconnect(parameters.getConnectorDriver(), parameters.getConnectionString(), parameters.getUser(), parameters.getPass());
			if (_CONNECTION != null && _CONNECTION.isConnected()) {
				_progress.setText("<html><font color='blue'>Obtendo a lista das tabelas disponíveis... Aguarde!</font></html>");
				_progress.setItemProgress(100.f);
				getTableList(path[2]);
				DatabaseTreeCellRender renderer = (DatabaseTreeCellRender)_database.getCellRenderer();
				renderer.updateSelectionDatabase(_SELECTED_DATABASE, _SELECTED_HOST);
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
			int select_mark  = 0;
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
							if (select_status) { ++select_mark; }
							_editor.setEnabled(select_mark > 0);
						}
					}
				}
			}
		}
		_list.revalidate();
		_list.repaint();
	}
	
	public void executeScript() {
		if (_TABLE_LIST != null) {
			System.gc();
		}
		if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
			doCommand cmd = new doCommand(_TABLE_LIST.get(0));
			_thread = new Thread(cmd);
			_thread.start();
		}
		else {
			Clipboard keyboard = Toolkit.getDefaultToolkit().getSystemClipboard();  
			StringSelection selection = new StringSelection(out.toString()); 
			keyboard.setContents(selection, null); 
			toogleActions(true);
			String size = (out.length() >= 1048576 ? round(out.length() / 1048576.f, 2) + "M Bytes" : (out.length() >= 1024 ? round(out.length() / 1024, 2) + "K Bytes" : out.length() + " Bytes") );
			JOptionPane.showMessageDialog(null, "<html>O script foi montado com sucesso (total de " + size + ") e está disponível na área de transferência,<br>para visualiza-lo inicialize um editor de textos e tecle CTRL+V<br><br><font color=red><b>NÃO ESQUEÇA DE VERIFICAR O CONTEÚDO DO SCRIPT E A BASE SELECIONADA ANTES DE EXECUTAR OS COMANDOS!</b></font></html>", "JQueryAnalizer - Conclusão!", JOptionPane.OK_OPTION);
		}
	}
	
	public void toogleActions(boolean state) {
			
		_list.setEnabled(state);
		_run.setEnabled(state);
		_close.setEnabled(state);
		_database.setEnabled(state);
		if (state == false) {
			_editor.setEnabled(false);
		}
		else {
			_editor_cell_keylistener.keyPressed(null);
		}
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
		@Override
		public void run() {
			_DATABASE_COUNT = 0;			
			JTabbedPane tabs = MainWindow.getTabs();
			Component c = null;
			JQueryPane pane = null;
			SQLConnectionManager con = null;
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Conexões disponíveis");
			DefaultTreeModel model = (DefaultTreeModel)_database.getModel();
			String host = null;
			int count = 0;
			int selection = 0;
			for (int i = 0; i < tabs.getComponentCount(); i++) {
				c = tabs.getComponent(i);
				if (c != null && c instanceof JQueryPane) {
					pane = (JQueryPane)c;
					con = pane.getConnection();
					host = pane.getParameters().getHost().split(":")[0]; 
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(host != null ? host : "?");
					root.add(child);
					++count;
					if (con != null && con.isConnected()) {
						try {
							_DATABASE_COUNT = 0;
							for (String database : con.getDatabasesList()) {
								++count;
								if (con.getDatabase() != null && con.getDatabase().trim().equalsIgnoreCase(database)) {
									selection = count;
								}
								child.add(new DefaultMutableTreeNode(database));
								++_DATABASE_COUNT;
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
			
			_database.setSelectionRow(selection);
			selectDatabase sd = new selectDatabase(_database.getSelectionPath().toString());
			Thread t = new Thread(sd);
			t.start();
			
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
				// multi database type... sql server / mysql
				_CONNECTION.executeUpdate("USE " + (_CONNECTION.getServerType() == 0 ? "`" + database.trim() + "`" : database));
				JCheckTreeNode item = null;
				for (String table : _CONNECTION.getTables()) {
					item = new JCheckTreeNode(table);
					row.add(item);
					if (item.isSelected() && !_editor.isEnabled()) {
						_editor.setEnabled(true);
					}
					
				}
				model.setRoot((DefaultMutableTreeNode)model.getRoot());
			}
			catch (Exception e) {
				e.printStackTrace(); 
			}
			_list.revalidate();
			_list.repaint();
			MainWindow.expandAll(_list, true);
		}
	}
	
	
	
	private StringBuffer out = new StringBuffer();
	public class doCommand implements Runnable {
		
		private String _TABLE;
		
		public doCommand (String table) {
			_TABLE = table;
		}
		public void run() {
			try {
				
				JTableModel model = (JTableModel)_editor.getModel();
				String option = null;
				String name   = null;
				String type   = null;
				String size   = null;
				String value  = null;
				ResultSet rs = _CONNECTION.executeQuery("SELECT * FROM " + _TABLE + " WHERE 1=0");
				ResultSetMetaData structure = rs.getMetaData();
				FastList<String> columns = new FastList<String>();
				int length    = -1;
				int precision = -1;
				for (Object[] line : model.getData()) {
					option = (line[0] == null ? "" : line[0].toString().trim());
					name   = (line[1] == null ? "" : line[1].toString().trim());
					type   = (line[2] == null ? "" : line[2].toString().trim());
					size   = (line[3] == null ? "" : line[3].toString().trim());
					value  = (line[4] == null ? "" : line[4].toString().trim());
					// -- verifica os parametros de tamanho e precisao do campo.
					if (!size.isEmpty()) {
						length    = Integer.parseInt(size.split(",").length > 1 ? size.split(",")[0] : size);
						precision = Integer.parseInt(size.split(",").length > 1 ? size.split(",")[1] : "-1");
					}
					// -- alteração para todas as tabelas.
					for (int i = 1; i <= structure.getColumnCount(); i++) {
						_progress.setItemProgress((100.f * (1.f + i)) / structure.getColumnCount());
						// ++ nome das colunas = * (qualquer)
						if (name.trim().equalsIgnoreCase("*")) {
							if (type.trim().equalsIgnoreCase("*")) {
								if ((structure.getPrecision(i) == length || length == -1) && (structure.getScale(i) == precision || precision == -1)) {
									columns.add(structure.getColumnName(i));	
								}
								
							}
							else if (structure.getColumnTypeName(i).trim().equalsIgnoreCase(type.trim())) {
								if ((structure.getPrecision(i) == length || length == -1) && (structure.getScale(i) == precision || precision == -1)) {
									columns.add(structure.getColumnName(i));	
								}
							}
						}
						// ++ nome das colunas afetadas foi especificado.
						else if (structure.getColumnName(i).trim().equalsIgnoreCase(name.trim())) {
							if (type.trim().equalsIgnoreCase("*")) {
								if ((structure.getPrecision(i) == length || length == -1) && (structure.getScale(i) == precision || precision == -1)) {
									columns.add(structure.getColumnName(i));	
								}
								//columns.add(structure.getColumnName(i));								
							}
							else if (structure.getColumnTypeName(i).trim().equalsIgnoreCase(type.trim())) {
								if ((structure.getPrecision(i) == length || length == -1) && (structure.getScale(i) == precision || precision == -1)) {
									columns.add(structure.getColumnName(i));	
								}
								//columns.add(structure.getColumnName(i));
							}
						}	
					}
										

					// {adciona / altera / insere} colunas

					// *** atualiza coluna
					if (option.trim().equalsIgnoreCase("update")) {
						if (!name.isEmpty() && !value.isEmpty()) {
							if (columns.toArray().length > 0) {
								out.append("\n-- Parâmetros: [Operação: " + option + "][Campo(s): " + name + "][Tipo(s): " + type + "][Tamanho: " + size + "][Valor padrão: " + value + "] / Tabela: " + _TABLE);
								out.append("\nUPDATE " + _TABLE + " SET ");
								for (int i = 0; i < columns.size(); i++) {
									out.append(columns.get(i).trim().toLowerCase() + "=" + value.trim().toLowerCase().replace("#column_name#", columns.get(i).trim().toLowerCase()));
									if (i + 1 < columns.size()) {
										out.append(", ");
									}
									else {
										out.append(";\n");
									}
								}
							}
							
						}
						else if (name.isEmpty()) {
							JOptionPane.showMessageDialog(_DIALOG, "o nome da coluna deve ser especificado para o tipo de operação definido como 'UPDATE'", "JQueryAnalizer - Mensagem", JOptionPane.OK_OPTION);
							toogleActions(true);
							return;
						}
						else if (value.isEmpty()) {
							JOptionPane.showMessageDialog(_DIALOG, "o valor padrão para a coluna deve ser especificado para o tipo de operação definido como 'UPDATE'", "JQueryAnalizer - Mensagem", JOptionPane.OK_OPTION);
							toogleActions(true);
							return;
						}
					}
					// +++ adiciona coluna
					if (option.trim().equalsIgnoreCase("add")) {
						if (!name.isEmpty() && !type.isEmpty()) {
							out.append("\n-- Parâmetros: [Operação: " + option + "][Campo(s): " + name + "][Tipo(s): " + type + "][Tamanho: " + size + "][Valor padrão: " + value + "] / Tabela: " + _TABLE);
							out.append("\nALTER TABLE " + _TABLE + " ADD COLUMN " + name.trim() + " " + type.trim() +(length > 0 ? (precision > 0 ? "(" + length + "," + precision + ")" : "(" + length + ")") : "") + " " + (value.isEmpty() ? "DEFAULT NULL" : value.trim()) + ";\n");
						}
						else if (name.isEmpty()) {
							JOptionPane.showMessageDialog(_DIALOG, "O nome da coluna deve ser especificado para o tipo de operação definido como 'ADD'", "JQueryAnalizer - Mensagem", JOptionPane.OK_OPTION);
							toogleActions(true);
							return;
						}
						else if (type.isEmpty()) {
							JOptionPane.showMessageDialog(_DIALOG, "O tipo da coluna deve ser especificado para o tipo de operação definido como 'ADD'", "JQueryAnalizer - Mensagem", JOptionPane.OK_OPTION);
							toogleActions(true);
							return;
						}
					}
					
					// @@@ altera um campo
					if (option.trim().equalsIgnoreCase("alter")) {
						if (!name.isEmpty() && !value.isEmpty()) {
							if (columns.toArray().length > 0) {
								out.append("\n-- Parâmetros: [Operação: " + option + "][Campo(s): " + name + "][Tipo(s): " + type + "][Tamanho: " + size + "][Valor padrão: " + value + "] / Tabela: " + _TABLE);
								//out.append("\nUPDATE " + _TABLE + " SET ");
								boolean found = false;
								for (int i = 0; i < columns.size(); i++) {
									if (columns.get(i).equalsIgnoreCase(name.trim())) {
										System.out.println("->" + _TABLE + " // " + name);
										found = true;
										out.append("ALTER TABLE " + _TABLE);
										if (_CONNECTION.getServerType() == 0) {
											out.append(" MODIFY COLUMN " + name.trim() + " " + type.trim() +(length > 0 ? (precision > 0 ? "(" + length + "," + precision + ")" : "(" + length + ")") : "") + " " + (value.isEmpty() ? "DEFAULT NULL" : value.trim()) + ";\n");
										}
										else {
											out.append(" ALTER COLUMN " + name.trim() + " " + type.trim() +(length > 0 ? (precision > 0 ? "(" + length + "," + precision + ")" : "(" + length + ")") : "") + " " + (value.isEmpty() ? "DEFAULT NULL" : value.trim()) + ";\n");
										}
									}
								}
								if (!found) {
									out.append("\nALTER TABLE " + _TABLE + " ADD COLUMN " + name.trim() + " " + type.trim() +(length > 0 ? (precision > 0 ? "(" + length + "," + precision + ")" : "(" + length + ")") : "") + " " + (value.isEmpty() ? "DEFAULT NULL" : value.trim()) + ";\n");
								}
							}
							
						}
						else if (name.isEmpty()) {
							JOptionPane.showMessageDialog(_DIALOG, "O nome da coluna deve ser especificado para o tipo de operação definido como 'ALTER'", "JQueryAnalizer - Mensagem", JOptionPane.OK_OPTION);
							toogleActions(true);
							return;
						}
						else if (type.isEmpty()) {
							JOptionPane.showMessageDialog(_DIALOG, "O tipo da coluna deve ser especificado para o tipo de operação definido como 'ALTER'", "JQueryAnalizer - Mensagem", JOptionPane.OK_OPTION);
							toogleActions(true);
							return;
						}
					}
					
					
				}
				rs.close();
				float perc_a = ( (100.f * (1.f + _TABLE_LIST_SIZE - _TABLE_LIST.size()) ) / _TABLE_LIST_SIZE);				
				_progress.setText("<html>Tabela verificada <i><b>"+ _TABLE + "</b></i>[<u><i>" + columns.size() + "</u></i>]. Já foram verificadas <b>" + (int)perc_a + "%</b> das tabelas.</html>");
				_progress.setMainProgress(perc_a);
			}					
			catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null) {
					JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar o <u>backup</u></b>. A aplicação retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + "</font></html>", "Erro ao executar o backup!", JOptionPane.OK_OPTION);
				}
			}		
			if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
				_TABLE_LIST.remove(0);
			}
			executeScript();
		}
		
		@SuppressWarnings("unused")
		private void showExceptionDialog(final Exception e, final String command) {
			if (e != null) {
				ImageIcon icon = new ImageIcon(_DIALOG.getIconImage());
				icon.setImage(icon.getImage().getScaledInstance(64, 64, 100));

				JOptionPane bla = new JOptionPane("<html>O comando executado retornou o seguinte erro:<br><textarea cols='200' rows='3'>" + e.getMessage() + "</textarea><hr>&nbsp;</html>", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
				bla.setMaximumSize(new Dimension(500,150));
				bla.setMinimumSize(new Dimension(500,150));
				bla.setOptionType(JOptionPane.YES_NO_OPTION);
				bla.setIcon(icon);
				bla.setOptions(new String[]{"Copiar detalhes do erro", "Sair"});
				
				JDialog dialog = bla.createDialog(_DIALOG, "JQueryAnalizer Special Console - Erro");
				dialog.setMaximumSize(new Dimension(500,200));
				dialog.setMinimumSize(new Dimension(500,200));
				dialog.setLocationRelativeTo(null);
			    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			    dialog.addWindowListener(new WindowListener() {
					public void windowActivated(WindowEvent a) { }
					public void windowClosed(WindowEvent a) { }
					public void windowClosing(WindowEvent event) { }
					@Override
					public void windowDeactivated(WindowEvent event) {
						if (event.getComponent().toString().equalsIgnoreCase("copiar detalhes do erro")) {
							Clipboard keyboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							StringSelection selection = new StringSelection("*** Comando SQL enviado ao servidor:\n\r=> " + command + "\n\r\n\r*** Mensagem de erro reportada pelo servidor:\n\r=> " + e.getMessage() + "\n\r=> " + e.getCause()); 
							keyboard.setContents(selection, null);
						}
					}
					public void windowDeiconified(WindowEvent a) { }
					public void windowIconified(WindowEvent a) { }
					public void windowOpened(WindowEvent a) { }
				});
				dialog.addMouseListener(new MouseListener(){
					public void mouseClicked(MouseEvent arg0) { }
					public void mouseEntered(MouseEvent arg0) { }
					public void mouseExited(MouseEvent arg0) { }
					public void mousePressed(MouseEvent arg0) { }
					public void mouseReleased(MouseEvent arg0) { }
				});
			    dialog.setVisible(true);
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
	
	
	
	private class JTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -2173685424567032936L;
		
		@SuppressWarnings("unused")
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
		
		public void addRow(int position, int count) {
			if (data.length + count < 0) { return; }
			Object[][] temp = new Object[data.length + count][columnNames.length];
			String[]   line = new String[columnNames.length];
			int search = 0;
			for (int row = 0; row < temp.length; row++) {
				if (row > position && row <= position + count) {
					temp[row] = line;
					continue;
				}
				temp[row] = data[search++];
			}
			data = temp;
			this.fireTableDataChanged();
		}

        @SuppressWarnings("unused")
		public void setColumnNames(String[] data){
        	this.columnNames = data;
        }
        
    
        @SuppressWarnings("unused")
		public void setRows(Object[][] data){
        	this.data = data;
        }
        
		public Object[][] getData() {
        	return this.data;
        }
        
        @SuppressWarnings("unused")
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
        
        public void setValueAt(Object value, int row, int col) {
        	data[row][col] = value;
        	//_editor.setModel(this);
        	//_editor.repaint();
        }
        
        public boolean isCellEditable(int row, int col) {
        	return true;
        }
	}
	
	public KeyListener _editor_cell_keylistener = new KeyListener(){
		@Override public void keyPressed(KeyEvent e) {
			boolean enable = false;
			JTableModel model = (JTableModel)_editor.getModel();
			for (Object[] line : model.getData()) {
				if (line[1] != null && !((String)line[1]).isEmpty() && line[2] != null && !((String)line[2]).isEmpty()) {
					enable = true;
				}
			}
			_run.setEnabled(enable);			
		}
		@Override public void keyReleased(KeyEvent e) { }
		@Override public void keyTyped(KeyEvent e) { }			
	};
	
	public class JComboBoxRenderer extends JComboBox<Object> implements TableCellRenderer {
		private static final long serialVersionUID = 2297302892044853136L;

		public JComboBoxRenderer(String[] items) {
	    	super(items);
	    }

	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        if (isSelected) {
	            setForeground(_editor.getSelectionForeground());
	            setBackground(_editor.getSelectionBackground());
	        }
	        else {
	            setForeground(_editor.getForeground());
	            setBackground(_editor.getBackground());
	        }
	        //if (getSelectedIndex() < 0 && value == null || (value != null && ((String)value).isEmpty())) {
	        //	setSelectedIndex(0);
	        //}
	        //else {
	        	setSelectedItem(value);	
	        //}
	        setOpaque(true);
	        setEditable(true);
	        setBorder(BorderFactory.createLineBorder(_editor.getBackground()));
	        return this;
	    }
	}

	public class JComboBoxEditor extends DefaultCellEditor {
		private static final long serialVersionUID = -3533566463433362956L;

		public JComboBoxEditor(JComboBox<String> main, String[] items) {
	        super(main);
	        main.removeAllItems();
	        for (String item : items) {
	        	main.addItem(item);
	        }
	        main.setEditable(true);
	        main.setOpaque(true);
	        main.setBorder(null);
	        main.setForeground(Color.WHITE);
	        main.setBackground(Color.RED);
	        main.addKeyListener(_editor_cell_keylistener);
	    }
	}
	
	public class JCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -9086644498507588298L;

		public JCellEditor(JTextField field) {
			super(field);
			field.setOpaque(true);
			field.addKeyListener(_editor_cell_keylistener);
		}
	}
	
	private float round(float value, int precision) {
		return Math.round(( value * Math.pow(10, precision) ) / Math.pow(10, precision) );
	}
}
