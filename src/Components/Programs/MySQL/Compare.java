package Components.Programs.MySQL;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.AbstractBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JParametersPanel;
import Components.MainWindowComponents.JProgressLabel;
import Components.MainWindowComponents.JQueryPane;
import Components.Programs.DatabaseTreeCellRender;
import Components.Programs.JCheckTreeNode;
import Components.Programs.TableTreeCellRender;

public class Compare {
	private JFrame _DIALOG = null;
	private JTree _list;
	private JButton _close;
	private List<String> _TABLE_LIST = new ArrayList<String>();
	private int _TABLE_LIST_SIZE = 0;
	private SQLConnectionManager _CONNECTION_ORIG;
	private SQLConnectionManager _CONNECTION_DEST;
	private JProgressLabel _progress;
	private Thread _thread;
	private JTree _origem;
	private JTree _destino;
	private JLabel _text5;
	private JButton _run;
	private JTextArea _LOG;
	private List<String> _SQL = new ArrayList<String>();
	private Font _FONT;
	private JButton _clipboard;
	private Logger _log;
	
	public Compare() {
		_log = MainWindow.getActiveLog();
		
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);
		
		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQuery Analizer - Assistente para comparar tabelas [MySQL]");
		_DIALOG.setMaximumSize(new Dimension(854,510));
		_DIALOG.setMinimumSize(new Dimension(854,510));
		_DIALOG.setPreferredSize(new Dimension(854,510));
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_DIALOG.setIconImages(MainWindow.getMainIconList());

		JLabel text2 = new JLabel("<html><b>»</b> Selecione a <i>base</i> de <b>referência</b>:</html>");
		text2.setFont(new Font("Verdana",Font.ROMAN_BASELINE,11));
		text2.setBounds(10,5,475,20);
		_DIALOG.add(text2);
		
		_origem = new JTree(new DefaultMutableTreeNode("..."));
		_origem.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 12));
		_origem.setOpaque(true);
		_origem.setBorder(null);
		_origem.setCellRenderer(new DatabaseTreeCellRender(null, null));
		_origem.setRowHeight(22);
		_origem.setBackground(new Color(240, 255, 240));
		JScrollPane scrolls_1 = new JScrollPane(_origem);
		scrolls_1.setBounds(10,25,215,163);
		scrolls_1.setAutoscrolls(true);
		_DIALOG.add(scrolls_1);
		_origem.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') { selectOriginDatabase(); }
			}
			public void keyReleased(KeyEvent event) { }
			public void keyTyped(KeyEvent event) { }
		});
		_origem.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent event) { }
			public void mouseEntered(MouseEvent event) { }
			public void mouseExited(MouseEvent event) { }
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectOriginDatabase(); } 
			}
			public void mouseReleased(MouseEvent event) { }				
		});
		_origem.setBorder(new AbstractBorder(){
			private static final long serialVersionUID = 7066818237310557988L;

			public Insets getBorderInsets(Component c) {
				return new Insets(2,2,2,2);
			}
		});

		JLabel text3 = new JLabel("<html><b>»</b> Selecione a <i>base</i> de <b>amostra</b>:</html>");
		text3.setFont(new Font("Verdana",Font.ROMAN_BASELINE,11));
		text3.setBounds(10,191,475,20);
		_DIALOG.add(text3);
		
		_destino = new JTree(new DefaultMutableTreeNode("..."));
		_destino.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 12));
		_destino.setOpaque(true);
		_destino.setBorder(null);
		_destino.setCellRenderer(new DatabaseTreeCellRender(null, null));
		_destino.setRowHeight(22);
		_destino.setBackground(new Color(255,240,240));
		JScrollPane scrolls_2 = new JScrollPane(_destino);
		scrolls_2.setBounds(10,212,215,163);
		scrolls_2.setAutoscrolls(true);
		_DIALOG.add(scrolls_2);
		_destino.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') { selectDestinationDatabase(); }
			}
			public void keyReleased(KeyEvent event) { }
			public void keyTyped(KeyEvent event) { }
		});
		_destino.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent event) { }
			public void mouseEntered(MouseEvent event) { }
			public void mouseExited(MouseEvent event) { }
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectDestinationDatabase(); } 
			}
			public void mouseReleased(MouseEvent event) { }				
		});
		_destino.setBorder(new AbstractBorder(){
			private static final long serialVersionUID = 7066818237310557988L;

			public Insets getBorderInsets(Component c) {
				return new Insets(2,2,2,2);
			}
		});
		
		// tabelas da database a serem salvas <-
		JLabel text4 = new JLabel("<html><b>»</b> <i>Tabelas</i> a serem <b>comparadas</b>:</html>");
		text4.setFont(new Font("Verdana",Font.ROMAN_BASELINE,11));
		text4.setBounds(230,5,475,20);
		_DIALOG.add(text4);
		
		_list = new JTree(new DefaultMutableTreeNode("Selecione uma DATABASE!"));
		_list.setRowHeight(22);
		_list.setCellRenderer(new TableTreeCellRender());
		_list.setOpaque(true);
		JScrollPane scrolls = new JScrollPane(_list);
		scrolls.setBounds(230,25,255,350);
		scrolls.setAutoscrolls(true);
		_DIALOG.add(scrolls);
		_list.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				if ((event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') && event.getComponent() != null && event.getComponent() instanceof JTree) { selectOriginTable(); }
			}
			public void keyReleased(KeyEvent a) { }
			public void keyTyped(KeyEvent a) { }
		});
		_list.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent event) { }
			public void mouseEntered(MouseEvent event) { }
			public void mouseExited(MouseEvent event) { }
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectOriginTable(); } 
			}
			public void mouseReleased(MouseEvent event) { }
		});
		_list.setBorder(new AbstractBorder(){
			private static final long serialVersionUID = 7066818237310557988L;

			public Insets getBorderInsets(Component c) {
				return new Insets(2,2,2,2);
			}
		});
		
		
		JLabel text5 = new JLabel("<html><b>»</b> <i>Resultado</i> da <b>análise</b>:</html>");
		text5.setFont(new Font("Verdana",Font.ROMAN_BASELINE,11));
		text5.setBounds(490,5,475,20);
		_DIALOG.add(text5);
		
		_LOG = new JTextArea();
		_LOG.setFont(_FONT);
		
		JScrollPane scroll_log = new JScrollPane(_LOG);
		scroll_log.setBounds(490,25,350,350);
		_DIALOG.add(scroll_log);
				
		
		
		_run = new JButton("Iniciar análise");
		_run.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_run.setMnemonic(KeyEvent.VK_E);
		_run.setBounds(457,440,150,35);
		_run.setEnabled(false);
		_run.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				if ((_CONNECTION_ORIG == null || (_CONNECTION_ORIG != null && !_CONNECTION_ORIG.isConnected()))) {
					JOptionPane.showMessageDialog(null, "<html>A conexão com o <b>banco de dados de origem</b> não está disponível!<br><i>Verifique a conectividade com o bancos de dados de origem e tente novamente!</i></html>", "Alerta!", JOptionPane.OK_OPTION);
					return;
				}
				else if (_CONNECTION_DEST == null || (_CONNECTION_DEST != null && !_CONNECTION_DEST.isConnected())) {
					JOptionPane.showMessageDialog(null, "<html>A conexão com o <b>banco de dados de destino</b> não está disponível!<br><i>Verifique a conectividade com o bancos de dados de origem e tente novamente!</i></html>", "Alerta!", JOptionPane.OK_OPTION);
					return;
				}
				
				if (_CONNECTION_ORIG.getName().equalsIgnoreCase(_CONNECTION_DEST.getName())) {
					int option = JOptionPane.showConfirmDialog(null, "<html>O <b>nome</b> da database de <i>ORIGEM</i> é o mesmo da database de <i>DESTINO</i>, tem certeza que deseja prosseguir?<br><i><font color=red>Caso seja selecionado a mesma database de origem dos dados como a database de destino, haverá a perda irreversível dos dados!</color></i></html>", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.NO_OPTION) {
						return;
					}
				}
				toogleActions(false);
				TreeModel model = _list.getModel();
				_TABLE_LIST = new ArrayList<String>(); 
				for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
					if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
						for (int j = 0; j < node.getChildCount(); j++) {
							if (node.getChildAt(j) instanceof JCheckTreeNode) {
								JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j);
								if (row.isSelected()) { 
									_TABLE_LIST.add(row.toString());
									_TABLE_LIST_SIZE = _TABLE_LIST.size();
								}
							}
						}
						try {
							_thread = new Thread(new Runnable(){
								public void run() {
									_LOG.append("[#] Progresso da análise das tabelas:\n");
									
									_log.warning("\t[»»»] Tool: { Compare tables }\tBEGIN");
									
									executeAnalise();
								}
							});
							_thread.start();
						}
						catch (Exception e1) {
							e1.printStackTrace(); 
						}
					}
				}
					
			}
		
		});
		_DIALOG.add(_run);
		 
		
		_clipboard = new JButton("Copiar resultado");
		_clipboard.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_clipboard.setMnemonic(KeyEvent.VK_C);
		_clipboard.setBounds(612,440,150,35);
		_clipboard.setEnabled(false);
		_clipboard.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Clipboard teclado = Toolkit.getDefaultToolkit().getSystemClipboard();  
				StringSelection selecao = new StringSelection(_LOG.getText()); 
				teclado.setContents(selecao, null);
			}
		});
		_DIALOG.add(_clipboard);
		
		_close = new JButton("Sair");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_close.setBounds(767,440,75,35);
		_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				for (WindowListener listener : _DIALOG.getWindowListeners()) {
					if (listener == null) { continue; }
					listener.windowClosing(null);
				}
			}
			
		});
		_DIALOG.add(_close);
		
		
		_text5 = new JLabel("<html><b>» Progresso</b> da comparação:</html>");
		_text5.setFont(new Font("Verdana",Font.ROMAN_BASELINE,11));
		_text5.setBounds(10,380,475,20);
		_text5.setOpaque(true);
		_DIALOG.add(_text5);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,400,830,35);
		_progress.setText("");
		_DIALOG.add(_progress);			
		
		_DIALOG.addWindowListener(new WindowListener(){
			public void windowClosing(WindowEvent arg0) {
				if (_thread != null && _thread.isAlive()) {
					int option = JOptionPane.showConfirmDialog(_DIALOG, "Existe uma analise de tabelas em execução, tem certeza que deseja interromper a análise?", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						_thread = null;
						return;
					}
				}
				_DIALOG.dispose();
				if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
					_CONNECTION_ORIG.closeConnection();
				}
				if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
					_CONNECTION_DEST.closeConnection();
				}
			}
			public void windowActivated (WindowEvent a)		{ }
			public void windowClosed (WindowEvent a)		{ }
			public void windowDeactivated (WindowEvent a)	{ }
			public void windowDeiconified (WindowEvent a)	{ }
			public void windowIconified (WindowEvent a)		{ }
			public void windowOpened (WindowEvent a)		{ }
		});
	}

	public void startPrograma() {
		_DIALOG.setVisible(true);
		getDatabaseOriginList();
		getDatabaseDestinationList();
	}
	
	public void toogleActions(boolean state) {
		_origem.setEnabled(state);
		_destino.setEnabled(state);
		_list.setEnabled(state);
		_run.setEnabled(state);
		_close.setEnabled(state);		
	}
	
	public void selectOriginDatabase() {
		String path = _origem.getSelectionPath().toString();
		path = path.replace(" ", "");
		path = path.replace("[", "");
		path = path.replace("]", "");
		if (path.split(",").length != 3) {
			return; 
		}		
		JQueryPane pane = MainWindow.getQueryPaneByHost(path.split(",")[1]);
		if (pane != null && pane.getParameters() != null) {
			JParametersPanel parameters = pane.getParameters();
			if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
				_CONNECTION_ORIG.closeConnection();
			}
			_log.info("\t[***] Tool: { Compare tables }\t Starting new JDBC connection with REFERENCE Database");
			_log.info("\t    * Tool: { Compare tables }\t » Connection String: " + parameters.getConnectionString());
			_log.info("\t    * Tool: { Compare tables }\t » User: " + parameters.getUser());
			_log.info("\t    * Tool: { Compare tables }\t » Pass: " + parameters.getPass());
			_CONNECTION_ORIG = new SQLConnectionManager(parameters.getConnectorDriver(), parameters.getConnectionString(), parameters.getUser(), parameters.getPass());
			if (_CONNECTION_ORIG == null || (_CONNECTION_ORIG != null && !_CONNECTION_ORIG.isConnected())) {
				_log.info("\t    * Tool: { Compare tables }\t » Status: FAIL");
				_progress.setText("<html>Falha ao conectar a base de <b>referência</b> <font color='gray'>" + path.split(",")[2] + "</font></html>");
				JOptionPane.showMessageDialog(null,"<html>A conexão com o banco de dados não foi bem sucedida!<br>- Verifique os parametros de conexão e tente novamente.</html>","Aviso!",JOptionPane.YES_OPTION);
			}
			else {
				_progress.setText("<html>Conectado a base de <b>referência</b> <font color='green'>" + path.split(",")[2] + "</font></html>");
				_log.info("\t    * Tool: { Compare tables }\t » Status: CONNECTED");
				_log.info("\t    * Tool: { Compare tables }\t » Switch database: " + path.split(",")[2]);
				Exception e = _CONNECTION_ORIG.executeUpdate("USE " + path.split(",")[2]);
				if (e != null) {
					JOptionPane.showMessageDialog(null,"<html>Ocorreu um erro ao selecionar a database escolhida!<br><br><font color=red>" + e.getMessage() + "</font></html>","Aviso!",JOptionPane.YES_OPTION);
				}
				else {
					_CONNECTION_ORIG.setName(path.split(",")[2]);
					getOriginTableList();
					if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
						_run.setEnabled(true);
					}
					else {
						_run.setEnabled(false);
					}
					return;
				}
			}
		}
		else { JOptionPane.showMessageDialog(null,"<html>Não foi possível conectar ao servidor selecionado!<br>- Verifique os parametros de conexão e tente novamente.</html>","Aviso!",JOptionPane.YES_OPTION); }
		if (_CONNECTION_ORIG != null) {
			_CONNECTION_ORIG.closeConnection(); 
		}
		_CONNECTION_ORIG = null;
	}
	
	
	public void getOriginTableList() {
		if (_origem.getSelectionPath() == null) {
			return;
		}
		String path = _origem.getSelectionPath().toString();
		path = path.replace(" ", "");
		path = path.replace("[", "");
		path = path.replace("]", "");
		if (path.split(",").length != 3) {
			return; 
		}
		String host     = path.split(",")[1];
		String database = path.split(",")[2];
		if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
			_origem.setCellRenderer(new DatabaseTreeCellRender(path.split(",")[2], path.split(",")[1]));
			DefaultTreeModel model = (DefaultTreeModel)_list.getModel();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(host == null ? "Você está desconectado ou não há database selecionada!" : host);
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(database);
			root.add(child);
			
			for (String item : _CONNECTION_ORIG.getTables()) {
				if (item != null) { child.add(new JCheckTreeNode(item)); }
			}
			model.setRoot(root);
			_list.revalidate();
			_list.repaint();
			MainWindow.expandAll(_list, true);
		}
	}
	
	public void selectDestinationDatabase() {
		if (_destino.getSelectionPath() == null) {
			return;
		}			
		String path = _destino.getSelectionPath().toString();
		path = path.replace(" ", "");
		path = path.replace("[", "");
		path = path.replace("]", "");
		if (path.split(",").length != 3) {
			return; 
		}		
		JQueryPane pane = MainWindow.getQueryPaneByHost(path.split(",")[1]);
		if (pane != null && pane.getParameters() != null) {
			JParametersPanel parameters = pane.getParameters();
			if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
				_CONNECTION_DEST.closeConnection();
			}
			
			_log.info("\t[***] Tool: { Compare tables }\t Starting new JDBC connection with SAMPLE Database");
			_log.info("\t    * Tool: { Compare tables }\t » Connection String: " + parameters.getConnectionString());
			_log.info("\t    * Tool: { Compare tables }\t » User: " + parameters.getUser());
			_log.info("\t    * Tool: { Compare tables }\t » Pass: " + parameters.getPass());
			
			_CONNECTION_DEST = new SQLConnectionManager(parameters.getConnectorDriver(), parameters.getConnectionString(), parameters.getUser(), parameters.getPass());
			if (_CONNECTION_DEST == null || (_CONNECTION_DEST != null && !_CONNECTION_DEST.isConnected())) {
				_log.info("\t    * Tool: { Compare tables }\t » Status: FAIL");
				_progress.setText("<html>Conectado a base de <b>amostra</b> <font color='gray'>" + path.split(",")[2] + "</font></html>");
				JOptionPane.showMessageDialog(null,"<html>A conexão com o banco de dados não foi bem sucedida!<br>- Verifique os parametros de conexão e tente novamente.</html>","Aviso!",JOptionPane.YES_OPTION);
			}
			else {
				Exception e = _CONNECTION_DEST.executeUpdate("USE " + path.split(",")[2]);
				_progress.setText("<html>Conectado a base de <b>amostra</b> <font color='green'>" + path.split(",")[2] + "</font></html>");
				_log.info("\t    * Tool: { Compare tables }\t » Status: CONNECTED");
				_log.info("\t    * Tool: { Compare tables }\t » Switch database: " + path.split(",")[2]);
				if (e != null) {
					JOptionPane.showMessageDialog(null,"<html>Ocorreu um erro ao selecionar a database escolhida!<br><br><font color=red>" + e.getMessage() + "</font></html>","Aviso!",JOptionPane.YES_OPTION);
				}
				else {
					_destino.setCellRenderer(new DatabaseTreeCellRender(path.split(",")[2], path.split(",")[1]));
					_CONNECTION_DEST.setName(path.split(",")[2]);
					if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
						_run.setEnabled(true);
					}
					else {
						_run.setEnabled(false);
					}
					return;
				}
			}
		}
		else { JOptionPane.showMessageDialog(null,"<html>Não foi possível conectar ao servidor selecionado!<br>- Verifique os parametros de conexão e tente novamente.</html>","Aviso!",JOptionPane.YES_OPTION); }
		if (_CONNECTION_DEST != null) {
			_CONNECTION_DEST.closeConnection(); 
		}
		_CONNECTION_DEST = null;
	}
	
	public void selectOriginTable() {
		if (_list.getSelectionPath() == null) {
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

	public void executeAnalise() {
		if (_TABLE_LIST != null) {
			System.gc();
		}
		if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
			doAnalise analise= new doAnalise(_TABLE_LIST.get(0));
			_thread = new Thread(analise);
			_thread.start();
			_progress.setMainProgress(100f  * (_TABLE_LIST_SIZE - _TABLE_LIST.size() + 1) / _TABLE_LIST_SIZE);
			_clipboard.setEnabled(false);
		}
		else {
			toogleActions(true);
			_LOG.append("[#] Concluido! Sugestões de comandos para correção dos campos:\n");
			_progress.setText("<html>Análise concluida com <b>sucesso</b>!</html>");
			_log.warning("\t[«««] Tool: { Compare tables }\tEND");
			_clipboard.setEnabled(true);
			if (_SQL.size() > 0) {
				for (String sql : _SQL) {
					_LOG.append(sql);
				}
			}
		}
	}
	
	public void stopAnalise() {
		if (_thread != null && _thread.isAlive()) {
			_thread.interrupt();
			_thread = null;
			_DIALOG.dispose();
			if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
				_CONNECTION_ORIG.closeConnection();
			}
			if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
				_CONNECTION_DEST.closeConnection();
			}
		}
	}
	
	public void getDatabaseOriginList() {
		JTabbedPane tabs = MainWindow.getTabs();
		Component c = null;
		JQueryPane pane = null;
		SQLConnectionManager con = null;
		//ResultSet rs = null;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database de referência");
		DefaultTreeModel model = (DefaultTreeModel)_origem.getModel();
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
					for (String item : con.getDatabasesList()) {
						if (item != null) { child.add(new DefaultMutableTreeNode(item)); }
					}
				}
			}
		}
		model.setRoot(root);
		_origem.setModel(model);
		_origem.revalidate();
		MainWindow.expandAll(_origem, true);
	}
	
	public void getDatabaseDestinationList() {
		JTabbedPane tabs = MainWindow.getTabs();
		Component c = null;
		JQueryPane pane = null;
		SQLConnectionManager con = null;
		ResultSet rs = null;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database de amostra");
		DefaultTreeModel model = (DefaultTreeModel)_destino.getModel();
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
						rs = con.executeQuery("SHOW DATABASES");
						while (rs.next()) {
							child.add(new DefaultMutableTreeNode(rs.getString(1)));
						}
					}
					catch (Exception e) { e.printStackTrace(); }
				}
			}
		}
		model.setRoot(root);
		_destino.setModel(model);
		_destino.revalidate();
		MainWindow.expandAll(_destino, true);
	}
	

	public class doAnalise implements Runnable {
		private String _TABLE;
		public doAnalise (String table) {
			_TABLE = table;
		}
		public void run() {
			try {
				
				if (_log != null) _log.info("\t[***] Tool: { Compare tables }\tCompare table: '" + _TABLE + "'");
				
				String[][] map_a = null;
				String[][] map_b = null;
				ResultSet rs = null;
				PreparedStatement ps = null;
				_LOG.append("      » Analisando a tabela: '" + _TABLE + "':\n");
				_progress.setText("<html>Analisando a tabela: { <font color='blue'><b>" + _TABLE + "</b></font> }</html>");
				
				// -- referencia.
				Connection con_a = _CONNECTION_ORIG.getConnection();
				ps = con_a.prepareStatement("SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE, (CASE WHEN CHARACTER_MAXIMUM_LENGTH=0 OR CHARACTER_MAXIMUM_LENGTH IS NULL THEN CONCAT(NUMERIC_PRECISION,',', NUMERIC_SCALE) ELSE CHARACTER_MAXIMUM_LENGTH END) AS `LENGTH` FROM (SELECT * FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name IN (?) GROUP BY column_name, ordinal_position, data_type, column_type HAVING COUNT(1)=1) A");
				ps.setString(1, _TABLE);
				if (ps.execute()) {
					rs = ps.getResultSet();
					rs.last();
					map_a = new String[rs.getRow()][5];
					rs.beforeFirst();
					for (int i = 0; rs.next(); i++) {
						_progress.setItemProgress(100f * (i + 1) / map_a.length);					
						map_a[i][0] = rs.getString("COLUMN_NAME");
						map_a[i][1] = rs.getString("ORDINAL_POSITION");
						map_a[i][2] = rs.getString("DATA_TYPE");
						map_a[i][3] = rs.getString("LENGTH");
					}
					if (rs != null && rs.isClosed()) {
						rs.close();
						rs = null;
					}
				}
				if (ps != null && ps.isClosed()) {
					ps.close();
					ps = null;
				}
				
				
				// -- amostra.
				Connection con_b = _CONNECTION_DEST.getConnection();
				ps = con_b.prepareStatement("SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE, (CASE WHEN CHARACTER_MAXIMUM_LENGTH=0 OR CHARACTER_MAXIMUM_LENGTH IS NULL THEN CONCAT(NUMERIC_PRECISION,',', NUMERIC_SCALE) ELSE CHARACTER_MAXIMUM_LENGTH END) AS `LENGTH` FROM (SELECT * FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name IN (?) GROUP BY column_name, ordinal_position, data_type, column_type HAVING COUNT(1)=1) A");
				ps.setString(1, _TABLE);
				if (ps.execute()) {
					rs = ps.getResultSet();
					rs.last();
					map_b = new String[rs.getRow()][5];
					rs.beforeFirst();
					for (int i = 0; rs.next(); i++) {
						map_b[i][0] = rs.getString("COLUMN_NAME");
						map_b[i][1] = rs.getString("ORDINAL_POSITION");
						map_b[i][2] = rs.getString("DATA_TYPE");
						map_b[i][3] = rs.getString("LENGTH");
					}
					if (rs != null && rs.isClosed()) {
						rs.close();
						rs = null;
					}
				}
				if (ps != null && ps.isClosed()) {
					ps.close();
					ps = null;
				}
				
				// -- Analise #01
				int row_A, row_B, events = 0;
				boolean field, ops;
				String sql = null;
				for (int i = 0; i < map_a.length; i++) {
					row_A = i;
					row_B = 0;
					field = false;
					ops = false;
					for (int j = 0; j < map_b.length; j++) {
						row_B = j;
						if (map_a[row_A][0].equalsIgnoreCase(map_b[row_B][0])) {
							field = true;
							break;
						}
					}
					if (map_b.length == 0) {
						_LOG.append("            «#0» A tabela: `" + _TABLE + "` NÃO EXISTE na base de amostra!\n");
						if (_log != null) _log.info("\t   0. Tool: { Compare tables }\tThe table '" + _TABLE + "' does not exists in sample schema");
						sql = _CONNECTION_ORIG.getTableStructure(_TABLE, _CONNECTION_DEST.getServerType());
						sql = "-- " + sql.replace("\n", "") + "\n";
						++events;
						ops = true;
						break;
					}
					else {
						if (!field) {
							sql = "-- ALTER TABLE `[table]` ADD COLUMN [column] [type]";
							sql = sql.replace("[table]", _TABLE);
							sql = sql.replace("[column]", map_a[row_A][0]);
							sql = sql.replace("[type]", map_a[row_A][2] + (map_a[row_A][3] != null ? "(" + (map_a[row_A][3].endsWith(".0") ? map_a[row_A][3].replace(".0", "") : map_a[row_A][3]) + ")" : ""));
							sql += ";\n";
							_LOG.append("            «#1» O campo: '" + map_a[row_A][0] + "' NÃO EXISTE na tabela '" + _TABLE + "' na base de amostra.\n");
							if (_log != null) _log.info("\t   1. Tool: { Compare tables }\tThe field '" + map_a[row_A][0] + "' does not exists in sample schema table '" + _TABLE + "'.");
							ops = true;
							++events;
						}
						else {
							sql = "-- ALTER TABLE `[table]` MODIFY COLUMN [column] [type]";
							sql = sql.replace("[table]", _TABLE);
							sql = sql.replace("[column]", map_a[row_A][0]);
							sql = sql.replace("[type]", map_a[row_A][2] + (map_a[row_A][3] != null ? "(" + (map_a[row_A][3].endsWith(".0") ? map_a[row_A][3].replace(".0", "") : map_a[row_A][3]) + ")" : ""));
							
							if (!(map_a[row_A][2] + map_a[row_A][3]).equalsIgnoreCase(map_b[row_B][2] + map_b[row_B][3])) {
								_LOG.append("            «#2» O campo: '" + map_b[row_B][0] + "' está em um FORMATO e/ou TIPO diferente:\n                  * RE = " + map_a[row_A][2] + (map_a[row_A][3] != null ? "(" + map_a[row_A][3] + ")" : "") + " // AM = " + map_b[row_B][2] + (map_b[row_B][3] != null ? "(" + map_b[row_B][3] + ")" : "") + "\n");
								if (_log != null) _log.info("\t   2. Tool: { Compare tables }\tThe '" + map_b[row_B][0] + "' field is in a different format. Suggest: [" + sql + "]");
								++events;
								ops = true;
							}
							if (!map_a[row_A][1].equalsIgnoreCase(map_b[row_B][1])) {
								_LOG.append("            «#3» O campo: '" + map_b[row_B][0] + "' está em uma POSIÇÃO diferente:\n                  * RE = " + map_a[row_A][1] + " // AM = " + map_b[row_B][1] + "\n");
								if (_log != null) _log.info("\t   3. Tool: { Compare tables }\tThe '" + map_b[row_B][0] + "' field is in a different position [reference position = " + map_a[row_A][1] + " | sample position = " + map_b[row_B][1] + "]");
								for (; row_B > 0; row_B--) {
									if (map_b[row_B][5] != null && map_b[row_B][5].equals("*")) {
										sql += " AFTER [column]";
										sql = sql.replace("[column]", map_b[row_B][0]);
										break;
									}
								}
								ops = true;
								++events;
							}
							else {
								map_b[row_B][4] = "*";
							}
							sql += ";\n";
						}
					}
					
					if (ops) {
						_SQL.add(sql);
					}
				}
				if (events == 0) {
					_LOG.append("            » Tabela OK\n");
					if (_log != null) _log.info("\t   ›  Tool: { Compare tables }\tThe table '" + _TABLE + "' is OK");
				}
				_LOG.setCaretPosition(_LOG.getText().length());
				
			}
			catch (Exception e) {
			}
			if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
				_LOG.append("\n");
				_TABLE_LIST.remove(0);
				executeAnalise();
			}
		}
	}
}
