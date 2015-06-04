package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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

public class Console {
	private SQLConnectionManager _CONNECTION;
	private JFrame _DIALOG = null;
	private JTree _list;
	private JButton _run;
	private JButton _close;
	private List<String> _TABLE_LIST = new ArrayList<String>();
	private int _TABLE_LIST_SIZE;
	private String _SELECTED_DATABASE;
	private String _SELECTED_HOST;
	private JProgressLabel _progress;
	private Thread _thread;
	//private int _errors_io = 0;
	private int _errors_other = 0;
	private JTree _database;
	private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
	
	private	ImageIcon check;
	private ImageIcon uncheck;

	private List<String> _command_regular = new ArrayList<String>();
	private List<String> _command_blocked = new ArrayList<String>();
	
	private JLabel _text4;
	private ImageIcon check_16;
	private ImageIcon uncheck_16;
	private JTextArea _editor;

	
	public Console() {
		check_16 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check_16.setImage(check_16.getImage().getScaledInstance(16, 16, 100));
		uncheck_16 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck_16.setImage(uncheck_16.getImage().getScaledInstance(16, 16, 100));
		check = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check.setImage(check.getImage().getScaledInstance(24, 24, 100));
		uncheck = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck.setImage(uncheck.getImage().getScaledInstance(24, 24, 100));

		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQuery Analizer - Console para comandos blocados por tabela.");
		Dimension size = new Dimension(500,578);
		_DIALOG.setMaximumSize(size);
		_DIALOG.setMinimumSize(size);
		_DIALOG.setPreferredSize(size);
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		_DIALOG.setIconImages(MainWindow.getMainIconList());
		
		// database de origem <-
		JLabel text2 = new JLabel("<html>Selecione a <b>database</b>:</html>");
		text2.setFont(_default_font);
		text2.setBounds(10,5,475,20);
		_DIALOG.add(text2);
		
		_database = new JTree(new DefaultMutableTreeNode("Carregando lista de databases, aguarde!"));
		_database.setFont(_default_font);
		_database.setOpaque(true);
		_database.setBorder(null);
		_database.setCellRenderer(new DatabaseTreeCellRender(_SELECTED_DATABASE, _SELECTED_HOST));
		_database.setRowHeight(22);
		JScrollPane scrolls_1 = new JScrollPane(_database);
		scrolls_1.setBounds(10,25,215,240);
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
					Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
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
		
		// tabelas da database a serem salvas <-
		JLabel text3 = new JLabel("<html>Selecione as <b>tabelas</b> para ser alteradas:</html>");
		text3.setFont(_default_font);
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
		scrolls.setBounds(230,25,255,240);
		scrolls.setAutoscrolls(true);
		_DIALOG.add(scrolls);
			
		// programas de reparação
		JLabel text6 = new JLabel();
		text6.setFont(_default_font);
		text6.setForeground(Color.DARK_GRAY);
		text6.setBounds(10,271,477,167);
		text6.setOpaque(true);
		text6.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(190,190,190), 1, true), "<html>&nbsp;<b>Scripts</b> SQL:&nbsp;</html>", 1, 0, new Font("Verdana", Font.ROMAN_BASELINE, 11), Color.BLACK));
		_DIALOG.add(text6);
		
		_editor = new JTextArea("-- ATENÇÃO!\n\r// a. Todos os comandos devem ser encerrados com ';'\n\r// b. Para executar um comando em várias tabelas\n\r//    substitua o nome da tabela por '#table#' sem as aspas.\n\r// c. Comentários suportados: '--', '//', e '/* ... */'\n\roptimize table #table#;");
		_editor.setFont(new Font(Font.MONOSPACED, _default_font.getStyle(), 13));
		_editor.setForeground(Color.DARK_GRAY);
		
		JScrollPane scrolls1 = new JScrollPane(_editor);
		scrolls1.setBounds(7,20,463,140);
		scrolls1.setBorder(new LineBorder(new Color(190,190,190)));
		text6.add(scrolls1);
		
		_text4 = new JLabel("<html><b>Progresso</b> da execução dos comandos:</html>");
		_text4.setFont(_default_font);
		_text4.setBounds(10,440,475,20);
		_text4.setOpaque(true);
		_DIALOG.add(_text4);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,460,475,35);
		_progress.setText("Selecione a DATABASE");
		_DIALOG.add(_progress);	
		
		
			
		
		_run = new JButton("<html>Executar Comandos</html>");
		_run.setFont(_default_font);
		_run.addActionListener(new ActionListener(){
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
						option = JOptionPane.showConfirmDialog(null, "Foram selecionadas: " + (_TABLE_LIST.size()) + "/" + (node.getChildCount()) + " tabelas, existem: " + (node.getChildCount() - _TABLE_LIST.size()) + " tabelas desmarcadas.\nDeseja prosseguir com o a execução dos comandos?" , "JQueryAnalizer - Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							_TABLE_LIST_SIZE = _TABLE_LIST.size();
							_errors_other = 0;
							executeScript();
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
		_run.setBounds(256,505,150,35);
		_run.setEnabled(false);
		_DIALOG.add(_run);
		
		_close = new JButton("<html>Sair</html>");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(_default_font);
		_close.setBounds(411,505,75,35);
		_close.addActionListener(new ActionListener(){
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
			JOptionPane.showMessageDialog(null, "<html>O <b>script</b> foi concluido.<br>- Tabelas selecionadas: <u>" +_TABLE_LIST_SIZE + "</u><br>- Número de comandos regulares (executados uma única vez durante o processo): <u>" + _command_regular.size() + "</u><br>- Número de comandos desdobrados (executados em cada tabela marcada): <u>" + _command_blocked.size() + "</u><br>- Erros durante o processo: <u>" + _errors_other + "</u></html>", "JQueryAnalizer - Conclusão!", JOptionPane.OK_OPTION);
			toogleActions(true);
		}
	}
	
	public void toogleActions(boolean state) {
			
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
			for (int i = 0; i < tabs.getComponentCount(); i++) {
				c = tabs.getComponent(i);
				if (c != null && c instanceof JQueryPane) {
					pane = (JQueryPane)c;
					con = pane.getConnection();
					host = pane.getParameters().getHost().split(":")[0]; 
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(host != null ? host : "?");
					root.add(child);
					if (con != null && con.isConnected()) {
						try {
							_DATABASE_COUNT = 0;
							for (String database : con.getDatabasesList()) {
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
				_CONNECTION.switchDatabase(database);
				int size = 0;
				for (String table : _CONNECTION.getTables()) {
					++size;
					row.add(new JCheckTreeNode(table));
				}
				if (size > 0) {
					_run.setEnabled(true);
				}
				else {
					_run.setEnabled(false);
				}
				model.setRoot((DefaultMutableTreeNode)model.getRoot());
			}
			catch (Exception e) {
				_errors_other += 1;
				e.printStackTrace(); 
			}
			_list.revalidate();
			_list.repaint();
			MainWindow.expandAll(_list, true);
		}
	}
	
	
	
	
	public class doCommand implements Runnable {
		
		private String _TABLE;
		
		public doCommand (String table) {
			_TABLE = table;
		}
		public void run() {
			try {
				float perc_a = 0.f;
				float perc_b = 0.f;
				float perc_c = 0.f;

				long count = 0;
				
				String command = null;
				Exception e = null;
				
				// -limpeza de comentários e preparação geral dos commandos-
				if (_TABLE_LIST_SIZE == _TABLE_LIST.size()) {
					_command_blocked.clear();
					_command_regular.clear();
					ByteBuffer buffer = ByteBuffer.wrap(("\n\r" + _editor.getText()).getBytes());
					StringBuffer bloc = new StringBuffer();
					int length = ("\n\r" + _editor.getText()).getBytes().length;
					byte[] byte_historico = new byte[6];
					byte byte_a = 0;
					boolean[] string_detector  = {false, false};
					boolean[] comment_detector  = {false, false, false, false};

					for (int j = 0; j < length; j++) {
						byte_a = buffer.get(j);
						byte[] t1 = {byte_a};

						byte_historico[5] = byte_historico[4];
						byte_historico[4] = byte_historico[3];
						byte_historico[3] = byte_historico[2];
						byte_historico[2] = byte_historico[1];
						byte_historico[1] = byte_historico[0];
						byte_historico[0] = byte_a;

						if (!string_detector[0]) {
							/** detecta comentarios do tipo [/#,#/] ou [/##,#/] */
							if (!comment_detector[0]) {
								comment_detector[0] = ((byte_historico[0] == '*' && byte_historico[1] == '/'));	// sinaliza o inicio do comentario de multiplas linhas
								if (comment_detector[0] && bloc.length() >= 1) {
									bloc.deleteCharAt(bloc.length() - 1);
									continue;
								}
							}
							else if (comment_detector[0] && !comment_detector[1]) {
								comment_detector[1] = ((byte_historico[0] == '/' && byte_historico[1] == '*')); // sinaliza o final do comentario de multiplas linhas
								if (comment_detector[1]) {
									continue;
								}
							}
							
							/** detecta comentarios do tipo [//], [--] ou {removido [#]} */
							if (!comment_detector[0] && !comment_detector[2]) {
								comment_detector[2] = ((byte_historico[0] == '/' && byte_historico[1] == '/') || (byte_historico[0] == '-' && byte_historico[1] == '-')/* || (byte_historico[0] == ' ' && byte_historico[1] == '#')*/);	// sinaliza o inicio do comentario.
								if (comment_detector[2] && bloc.length() >= 1) {
									bloc.deleteCharAt(bloc.length() - 1);
									continue;
								}
							}
							else if (!comment_detector[0] && comment_detector[2] && !comment_detector[3] && byte_historico[0] == '\n') {	// sinaliza o final da linha para comentários simples.
								comment_detector[3] = true;
								if (comment_detector[3]) {
									continue;
								}
							}							
						}
						
						/** - detector de strings - */
						if (comment_detector[0] == false && comment_detector[2] == false) {							
							string_detector[1] = (string_detector[0] ? (byte_historico[0] == '\'' ? (byte_historico[1] == '\\' ? (byte_historico[2] == '\\') : true ) : false) : false); // detecta o final de uma string
							string_detector[0] = (string_detector[0] ? true : (byte_historico[0] == '\'' ? (byte_historico[1] == '\\' ? (byte_historico[2] == '\\') : true ) : false)); // detecta o inicio de uma string
						
						}
						if (string_detector[0] == true && string_detector[1] == true) {
							string_detector[0] = false; // efetua a liberação das restrições de string para a adição dos comandos.
							string_detector[1] = false;
						}

						/** - Adiciona trecho não comentado do script ao buffer - */
						if (!comment_detector[0] && !comment_detector[2]) {
							bloc.append(new String(t1));
						}
						
						/** -- Separa os comandos em comandos blocados ou comandos não blocados de acordo com o campo #table# -> MySQL -- */
						
						if (!string_detector[0] && !string_detector[1] && byte_historico[0] == ';') {
							bloc.deleteCharAt(bloc.length() - 1);
							command = bloc.toString().trim();
							System.out.println("[SPECIAL CONSOLE - SQL]: " + command);
							bloc = new StringBuffer();
							if (!command.isEmpty()) {
								if (command.toLowerCase().contains("#table#")) { _command_blocked.add(command); }
								else { _command_regular.add(command); }
							}
						}
						else if ((comment_detector[0] && comment_detector[1]) || (comment_detector[2] && comment_detector[3])) {
							comment_detector[0] = comment_detector[1] = comment_detector[2] = comment_detector[3] = false;
						}
					}
				}
				
				//if (_TABLE_LIST.size() > 0) { toogleActions(true); return; }
				
				// <-- executa uma única vez os comandos não blocados.
				Iterator<String> it = null;
				if (_TABLE_LIST_SIZE == _TABLE_LIST.size() && _command_regular.size() > 0) {
					it = _command_regular.iterator();
					count = 0;
					while (it.hasNext()) {
						perc_a = ((1.f * ++count) / _command_regular.size()) * 100.f;
						perc_c = (1.f - ((1.f * _TABLE_LIST.size()) / _TABLE_LIST_SIZE)) * 100.f;
						command = it.next();
						if (!command.isEmpty()) {
							e = _CONNECTION.executeUpdate(command);
							if (e != null) { showExceptionDialog(e, command); }
						}
						if (perc_a - perc_b >= 0.05f || perc_a == 100.f) {
							perc_b = perc_a;
							_progress.setText("<html>Executando comandos <i>não blocados</i>: <b>" + perc_a + "%</b> <u>" + count + "/" + _command_regular.size() + "</u></html>" );
							_progress.setMainProgress(perc_c);
							_progress.setItemProgress(perc_a);
						}
					}
				}
				if (_command_blocked.size() > 0) {
					it = _command_blocked.iterator();
					count = 0;
					while (it.hasNext()) {
						perc_a = ((1.f * ++count) / _command_blocked.size()) * 100;
						perc_c = (1.f - ((_TABLE_LIST.size() - 1.f) / _TABLE_LIST_SIZE)) * 100.f;
						command = it.next().toLowerCase().replaceAll("#table#", _TABLE);
						if (!command.isEmpty()) {
							e = _CONNECTION.executeUpdate(command);
							if (e != null) { showExceptionDialog(e, command); }
						}
						if (perc_a - perc_b >= 0.05f || perc_a == 100.f) {
							perc_b = perc_a;
							_progress.setText("<html>Executando comandos <i>blocados</i>: <b>" + perc_a + "%</b> <u>" + count + "/" + _command_blocked.size() + "</u> <i>" + _TABLE + "</i></html>" );
							_progress.setMainProgress(perc_c);
							_progress.setItemProgress(perc_a);
						}						
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
			}
			executeScript();
		}
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
					@Override
					public void windowActivated(WindowEvent a) { System.out.println("a"); }
					@Override
					public void windowClosed(WindowEvent a) { System.out.println("b"); }
					@Override
					public void windowClosing(WindowEvent event) {
						
					}
					@Override
					public void windowDeactivated(WindowEvent event) {
						System.out.println("d");
						System.out.println("-> " + event.getComponent().toString().equalsIgnoreCase("copiar detalhes do erro") + " // " + event.getComponent());
						if (event.getComponent().toString().equalsIgnoreCase("copiar detalhes do erro")) {
							Clipboard keyboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							StringSelection selection = new StringSelection("*** Comando SQL enviado ao servidor:\n\r=> " + command + "\n\r\n\r*** Mensagem de erro reportada pelo servidor:\n\r=> " + e.getMessage() + "\n\r=> " + e.getCause()); 
							keyboard.setContents(selection, null);
							System.out.println(selection);
						}
					//	dialog.dispose();
						++_errors_other;
					}
					@Override
					public void windowDeiconified(WindowEvent a) { System.out.println("e"); }
					@Override
					public void windowIconified(WindowEvent a) { System.out.println("f"); }
					@Override
					public void windowOpened(WindowEvent a) { System.out.println("g"); }
				});
				dialog.addMouseListener(new MouseListener(){

					@Override
					public void mouseClicked(MouseEvent arg0) { System.out.println("a0"); }

					@Override
					public void mouseEntered(MouseEvent arg0) { System.out.println("a1"); }

					@Override
					public void mouseExited(MouseEvent arg0) { System.out.println("a2"); }

					@Override
					public void mousePressed(MouseEvent arg0) { System.out.println("a3"); }

					@Override
					public void mouseReleased(MouseEvent arg0) { System.out.println("a4"); }
					
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
	
	
}
