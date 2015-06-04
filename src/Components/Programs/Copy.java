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
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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

public class Copy {
	private JFrame _DIALOG = null;
	private JTree _list;
	private JButton _close;
	private List<String> _TABLE_LIST = new ArrayList<String>();
	private int _TABLE_LIST_SIZE;
	private SQLConnectionManager _CONNECTION_ORIG;
	private SQLConnectionManager _CONNECTION_DEST;
	private JProgressLabel _progress;
	private Thread _thread;
	private int _errors_io = 0;
	private int _errors_other = 0;
	private JTree _origem;
	private JTree _destino;
	private JLabel _text5;
	private JButton _run;
	public Copy() {
		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQuery Analizer - Assistente para Cópia de databases [MySQL]");
		_DIALOG.setMaximumSize(new Dimension(500,510));
		_DIALOG.setMinimumSize(new Dimension(500,510));
		_DIALOG.setPreferredSize(new Dimension(500,510));
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_DIALOG.setIconImages(MainWindow.getMainIconList());
		// database de origem <-
		JLabel text2 = new JLabel("<html>Selecione a <i>database</i> de <b>origem</b>:</html>");
		text2.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
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

		JLabel text3 = new JLabel("<html>Selecione a <i>database</i> de <b>destino</b>:</html>");
		text3.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
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
		JLabel text4 = new JLabel("<html>Selecione as <i>tabelas</i> <b>(na origem)</b>:</html>");
		text4.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		text4.setBounds(230,5,475,20);
		_DIALOG.add(text4);
		
		_list = new JTree(new DefaultMutableTreeNode("Selecione uma database!"));
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
		
		
		_run = new JButton("Iniciar cópia");
		_run.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
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
								if (row.isSelected()) { _TABLE_LIST.add(row.toString()); }
							}
						}
						_TABLE_LIST_SIZE = _TABLE_LIST.size();
						int option = JOptionPane.showConfirmDialog(null, "<html>Foram selecionadas: " + (_TABLE_LIST.size()) + "/" + (node.getChildCount()) + " tabelas para a cópia, existem: " + (node.getChildCount() - _TABLE_LIST.size()) + " tabelas desmarcadas.<br><br><b>Deseja prosseguir com a cópia?</b><br><i><font color=red>Todas as tabelas na database destino com mesmo nome serão sobrescritas!</font></i></html>" , "Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							_errors_io = 0;
							_errors_other = 0;
							_registers_total = 0;
							
							try {
								EntryCount ec = new EntryCount(true);
								_thread = new Thread(ec);
								_thread.start();
							}
							catch (Exception e1) { _errors_io += 1; e1.printStackTrace(); }  
						}
						else {
							toogleActions(true);
						}
						return;
					}
				}
					
			}
		
		});
		_run.setMnemonic(KeyEvent.VK_E);
		_run.setBounds(256,440,150,35);
		_run.setEnabled(false);
		_DIALOG.add(_run);
	
		_close = new JButton("Sair");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_close.setBounds(411,440,75,35);
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
		
		
		_text5 = new JLabel("<html><b>Progresso</b> da cópia:</html>");
		_text5.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_text5.setBounds(10,380,475,20);
		_text5.setOpaque(true);
		_DIALOG.add(_text5);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,400,475,35);
		_progress.setText("");
		_DIALOG.add(_progress);			
		
		_DIALOG.setVisible(true);
		getDatabaseOriginList();
		getDatabaseDestinationList();
		
		_DIALOG.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent a) { }
			public void windowClosed(WindowEvent a) { }
			public void windowClosing(WindowEvent arg0) {
				if (_thread != null && _thread.isAlive()) {
					int option = JOptionPane.showConfirmDialog(_DIALOG, "Existe uma cópia de databases em execução, tem certeza que deseja interromper a cópia?", "Confirmação", JOptionPane.YES_OPTION);
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
			public void windowDeactivated(WindowEvent a) { }
			public void windowDeiconified(WindowEvent a) { }
			public void windowIconified(WindowEvent a) { }
			public void windowOpened(WindowEvent a) { }
				
		});
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
			_CONNECTION_ORIG = new SQLConnectionManager(parameters.getConnectorDriver(), parameters.getConnectionString(), parameters.getUser(), parameters.getPass());
			if (_CONNECTION_ORIG == null || (_CONNECTION_ORIG != null && !_CONNECTION_ORIG.isConnected())) {
				JOptionPane.showMessageDialog(null,"<html>A conexão com o banco de dados não foi bem sucedida!<br>- Verifique os parametros de conexão e tente novamente.</html>","Aviso!",JOptionPane.YES_OPTION);				
			}
			else {
				Exception e = _CONNECTION_ORIG.executeUpdate("USE " + path.split(",")[2]);
				if (e != null) {
					JOptionPane.showMessageDialog(null,"<html>Ocorreu um erro ao selecionar a database escolhida!<br><br><font color=red>" + e.getMessage() + "</font></html>","Aviso!",JOptionPane.YES_OPTION);
				}
				else {
					_CONNECTION_ORIG.setName(path.split(",")[2]);
					System.out.println("*** Conectado com sucesso! (" + _CONNECTION_ORIG.getName() + ")");
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
			_CONNECTION_DEST = new SQLConnectionManager(parameters.getConnectorDriver(), parameters.getConnectionString(), parameters.getUser(), parameters.getPass());
			if (_CONNECTION_DEST == null || (_CONNECTION_DEST != null && !_CONNECTION_DEST.isConnected())) {
				JOptionPane.showMessageDialog(null,"<html>A conexão com o banco de dados não foi bem sucedida!<br>- Verifique os parametros de conexão e tente novamente.</html>","Aviso!",JOptionPane.YES_OPTION);				
			}
			else {
				Exception e = _CONNECTION_DEST.executeUpdate("USE " + path.split(",")[2]);
				if (e != null) {
					JOptionPane.showMessageDialog(null,"<html>Ocorreu um erro ao selecionar a database escolhida!<br><br><font color=red>" + e.getMessage() + "</font></html>","Aviso!",JOptionPane.YES_OPTION);
				}
				else {
					_destino.setCellRenderer(new DatabaseTreeCellRender(path.split(",")[2], path.split(",")[1]));
					_CONNECTION_DEST.setName(path.split(",")[2]);
					System.out.println("*** Conectado com sucesso! (" + _CONNECTION_DEST.getName() + ")");
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
	
	
	public class EntryCount implements Runnable {
		private boolean start;
		public EntryCount(boolean s) {
			start = s;
		}

		@Override
		public void run() {
			if (_TABLE_LIST_SIZE == _TABLE_LIST.size()) {
				_registers_total = 0L;
				_registers_processed = 0L;
				ResultSet rs = null;
				try {
					int cnt = 0;
					for (Object table : _TABLE_LIST.toArray()) {
						if (table != null) {
							++cnt;
							rs = _CONNECTION_ORIG.executeQuery("SELECT COUNT(*) FROM " + table.toString());
							if (rs.next()) {
								_registers_total += rs.getInt(1);													
							}
							_progress.setText("<html>Listando os registros a serem copiados... <b>" + (int)(((cnt * 1.f) / _TABLE_LIST_SIZE) * 100.f) + "%</b> <i>" + table.toString() + "</i></html>");
						}
					}
				}
				catch (Exception e1) {
					toogleActions(true);
					e1.printStackTrace(); 
				}
				_progress.setText("<html>Foram encontrados <b>" + _registers_total + "</b> para o backup atual...</html>");
			}
			if (start) {
				executeCopy();
			}
		}
	}
	
	
	public void executeCopy() {
		if (_TABLE_LIST != null) {
			System.gc();
		}
		if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
			
			doCopy backup = new doCopy(_TABLE_LIST.get(0));
			_thread = new Thread(backup);
			_thread.start();
			
		}
		else {
			toogleActions(true);
			_progress.setText("<html>" + (_errors_io + _errors_io == 0 ? "<font color='#000000'><b>Cópia concluida com <b>sucesso</b>!" : "<font color='#990000'><b>Backup concluído com: " + (_errors_io + _errors_other) + " ERROS!</b></font>") + "</b></font></html>");
			JOptionPane.showMessageDialog(null, (_errors_io + _errors_io == 0 ? "Cópia concluida com sucesso!" : "Cópia concluída com: " + (_errors_io + _errors_other) + " erros!\n\nATENÇÃO: ESTA CÓPIA NÃO É CONFIÁVEL!!!"), "Aviso!", JOptionPane.OK_OPTION);
		}
	}
	
	public void stopCopy() {
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
			System.out.println("*** Copia abortada!");
		}
	}
	
	public void getDatabaseOriginList() {
		JTabbedPane tabs = MainWindow.getTabs();
		Component c = null;
		JQueryPane pane = null;
		SQLConnectionManager con = null;
		//ResultSet rs = null;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database de origem");
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
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database de destino");
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
	
	
	
	
	
	private long _registers_processed = 0L;
	private long _registers_total = 0L;
	
	public class doCopy implements Runnable {
		private String _TABLE;
		private long _paint_time;
		public doCopy (String table) {
			_TABLE = table;
		}
		@Override
		public void run() {
			try {
				_progress.setText("<html>Exportando <u>estrutura</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b></html>");
				// -- estrutura da tabela.
				ResultSet rs = _CONNECTION_ORIG.executeQuery("SHOW CREATE TABLE " + _TABLE);
				while (rs != null && rs.next()) {
					this.toDestinationDatabase("DROP TABLE IF EXISTS `" + _TABLE + "`");
					this.toDestinationDatabase(rs.getString(2));
				}
				rs.close();
				
				
				
				// ------------------------------------ inicio do processo de paginacao...
				_progress.setText("<html>Preparando exportação dos <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b></html>");
				// -- contagem de páginas de registros para exportação otimizada.
				rs = _CONNECTION_ORIG.executeQuery("SELECT COUNT(*) FROM " + _TABLE);
				StringBuffer out = null;
				int count = 0;
				int count2 = 0;
				int pages = 0;
				int page_size = Integer.parseInt(MainWindow.getPropertie("backup_page_size", "10000"));
				int min = 0;
				int max = 0;
				int row = 0;
				int row2 = 0;
				int block_size = 1000;
				boolean control = true;
				boolean header_parse = false;
				//float perc = 0;
				float perc2 = 0;
				float perc3 = 0;
				int query_length = 0;
				int query_maxlength = 0;
				String item = null;
				String header = null;
				ResultSetMetaData header_data = null;
				if (rs.next()) {
					count = rs.getInt(1);
					System.out.println("``` " + count + " Registros encontrados na tabela: " + _TABLE);
				}
				page_size = Math.max(page_size, (int)(count * (1.f / 100)));
				pages = (int)(count / page_size) + 1;
				rs.close();

				String command = null;
				Exception sql_exception = null;
				
				//_progress.setText("<html>" + count + " <u>registros</u> localizados, iniciando exportação dos dados</html>");
				for (int j = 1; count > 0 && j <= pages; j++) {
					min = max;
					max = Math.min(j * page_size, count);
					// -- preparacao dos dados para exportação com base nas páginas definidas anteriormente.
					_paint_time = (_paint_time == 0 ? System.nanoTime() : _paint_time);
					if (_paint_time + (40000000) <= System.nanoTime() || perc2 == 100) {
						perc3 = ((_registers_processed * 1.f) / _registers_total) * 100.f;
						// -- ok
						_text5.setText("<html><i>Progresso</i> da cópia: <font color='#000000'>Preparando registros para cópia... <i><b>" + j + "/" + pages + "</b></i></font></html>");
						_progress.setMainProgress(perc3);
						// -- ok
						_progress.setText("<html>Exportando <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b> <i>" + (int)perc2 + "%</i></html>");
						// -- ok
						_progress.setItemProgress(perc2);
						_paint_time = System.nanoTime();
					}
					/*
					_text5.setText("<html><i>Progresso</i> do backup: <font color='#000000'>Preparando registros para cópia... <i><b>" + j + "/" + pages + "</b></i></font></html>");
					_progress.setText("<html>Exportando <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b> <i>" + (int)perc2 + "%</i></html>");
					_progress.setItemProgress(perc2);
					*/
					rs = _CONNECTION_ORIG.getDumpData(_TABLE, _CONNECTION_DEST.getServerType(), page_size, min);
					
					/** - \/ controle de encerramento dos comandos! - */
					switch (_CONNECTION_DEST.getServerType()) {
					case 0:
						block_size = 1000;
						rs.last();
						count2 = rs.getRow();
						rs.beforeFirst();
						break;
					case 1:
						block_size = 1;
						count2 = Math.min(count, page_size + min > count ? Math.abs(count - min) : page_size);
						break;
					}
					row2 = 0;
					/** - /\ */
					
					// -- insert header
					header = "INSERT INTO " + _TABLE + " (";
					header_data = rs.getMetaData();
					for (int a = 1; a <= header_data.getColumnCount(); a++) {
						if (header_data.getColumnName(a) != null) {
							switch(_CONNECTION_DEST.getServerType()) {
								case 0:
									header += "`" + header_data.getColumnName(a) + "`"; // mysql
									break;
								case 1:
									header += header_data.getColumnName(a); // sql server.
									break;
							}
							if ((a) != header_data.getColumnCount()) { header += ","; }
						}
					}
					header += ")";
					
					SimpleDateFormat DateFormat = null;
					switch(_CONNECTION_DEST.getServerType()) {
					case 0:
						DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // datetime no formato do mysql
						break;
					case 1:
						DateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // datetime no formado to sql server
						break;
					}

					
					// -- insert values
					control = (count2 > row2);
					while (rs != null && count2 > 0 && control) {
						out = new StringBuffer();
						for (int i = 0; i < block_size && rs.next(); i++) {
							++row;
							++row2;
							++_registers_processed;
							if (!header_parse) {
								out.append(header + " VALUES \r\n");
								header_parse = true;
							}
							perc2 = (((row * 1.f) / count) * 100.f);
							_paint_time = (_paint_time == 0 ? System.nanoTime() : _paint_time);
							if (_paint_time + (40000000) <= System.nanoTime() || perc2 == 100) {
								perc3 = ((_registers_processed * 1.f) / _registers_total) * 100.f;
								_text5.setText("<html><i>Progresso</i> do backup: <font color='#000000'><i><b>" + ( Math.round(perc3 * Math.pow(10,1) ) / Math.pow(10, 1) ) + "%</b></i></font></html>");
								_progress.setMainProgress(perc3);
								_progress.setItemProgress(perc2);
								_progress.setText("<html>Exportando <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b> <i>" + ( Math.round(perc2 * Math.pow(10,1) ) / Math.pow(10,1) ) +"%</i></html>");
								_paint_time = System.nanoTime();
							}
							out.append("(");
							query_maxlength = Math.max(query_maxlength, query_length);
							query_length = out.length() + 3;
							for (int b = 1; b <= header_data.getColumnCount(); b++) {
								switch(header_data.getColumnType(b)) {
									case java.sql.Types.DATE:
									case java.sql.Types.TIME:
									case java.sql.Types.TIMESTAMP:
										item = (rs.getString(b) == null ? "NULL" : "'" + DateFormat.format(rs.getTimestamp(b)) + "'");
										break;
									default:
										item = (rs.getString(b) == null ? "NULL" : "'" + getSafeString(rs.getString(b)) + "'"); 
								}
								query_length += item.length();
								out.append(item);
								if ((b) != header_data.getColumnCount()) { out.append(","); }
							}
							out.append(")");
							if ((block_size > 1 && (i + 1) == block_size || block_size == 1) || count2 == row2 || (out.length() + query_maxlength) >= 1048576) {
								out.append(";\r\n");
								if (control == true && count2 == row2) {
									control = false;
								}
								System.out.print("    » Executando bloco com: " + (out.length() > 1024 * 1024 ? (int)(out.length() / (1024 * 1024)) + "M" : (out.length() > 1024 ? (int)(out.length() / 1024) + "K" : out.length())) + " bytes / " + (i + 1) + " comandos... ");
								command = out.toString();
								out = new StringBuffer();
								sql_exception = _CONNECTION_DEST.executeUpdate(command);
								command = null;
								if (sql_exception == null) {
									System.out.println("executado com sucesso!");
								}
								else {
									System.out.println("falha!\n" + sql_exception.getMessage());
								}
								if (_thread == null) {
									_DIALOG.dispose();
									if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
										_CONNECTION_ORIG.closeConnection();
									}
									if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
										_CONNECTION_DEST.closeConnection();
									}
									System.out.println("*** Copia abortada! [2]");
									return;
								}
								header_parse = false;
								break;
							}
							else {
								out.append(",\r\n");
							}
						}
						if (_thread == null) {
							System.out.println("``` Processo de cópia interrompido!");
							toogleActions(true);
							return;
						}
						if (header_parse) {
							System.out.println("~~~ AGUARDANDO COMANDOS PARA FINALIZAR BLOCO!!! length_alert=" + ((out.length() + query_maxlength) >= 1048576) + " // count=" + count + " // row=" + row);
						}

						if (control && rs.getRow() == 0) {
							control = false; 
						}							
					}
					rs.close();
				}
				
				// --- fim do processo de paginação...
				
			}
			catch (Exception e) {
				_errors_io += 1;
				e.printStackTrace();
				if (e.getMessage() != null) {
					JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar a <u>cópia</u></b>. A aplicação retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + "</font></html>", "Erro ao executar a cópia!", JOptionPane.OK_OPTION);
					int option = JOptionPane.showConfirmDialog(null, "<html>Deseja prosseguir com o cópia!?<br><i>Haverá boas chances desta cópia trazer dados truncados e/ou corrompidos</i>.</html>", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.NO_OPTION) {
						return;
					}
				}
			}
			if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
				_TABLE_LIST.remove(0);
				executeCopy();
			}
		
		}
		
		private void toDestinationDatabase(String register) {
			if (register == null) {
				return;
			}
			else if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
				if (_CONNECTION_DEST.executeUpdate(register) != null) {
					System.out.println("*** SQL Command with error:\n" + register);
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "<html>A conexão de destino não está disponível! <b>A cópia foi interrompida!</b></html>", "Erro ao executar a cópia!", JOptionPane.OK_OPTION);
			}
		}
	}
		
	public String getSafeString(String text) {
		text = text.replace("\\","\\\\");
		text = text.replace("\'","''");
		return text;
	}
	
	public static String charsetTranslate(String content, String encode) {
		byte[] list;
		try {
			list = content.getBytes(encode);
			return new String(list, encode);
		}
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return null;		
	}  
}
