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
import java.sql.ResultSetMetaData;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import javolution.util.FastList;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JParametersPanel;
import Components.MainWindowComponents.JProgressLabel;
import Components.MainWindowComponents.JQueryPane;

public class Compare {
	private JFrame _DIALOG = null;
	private JTree _list;
	private JButton _run;
	private JButton _close;
	private FastList<String> _TABLE_LIST = new FastList<String>();
	private int _SELECTED_TABLES = 0;
	private SQLConnectionManager _CONNECTION_ORIG;
	private SQLConnectionManager _CONNECTION_DEST;
	private JProgressLabel _progress;
	private Thread _thread;
	private int _errors_io = 0;
	private int _errors_other = 0;
	private JTree _origem;
	private JTree _destino;
	private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
	private TreeCellRenderer _list_render = new TreeCellRenderer() {
		private JLabel root = null;
		private ImageIcon icon = null;				
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
			if (value == null) return null;
			int step = 0;
			icon = new ImageIcon(ClassLoader.getSystemResource("table.png"));						
			TreeModel model = tree.getModel();
			DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
			root = new JLabel();
			root.setOpaque(false);
			for (int i = 0; i < model.getChildCount(r); i++) {
				if (value != null && value.toString().equalsIgnoreCase(model.getChild(r, i).toString())) {
					icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));
					step = 1;
				}
			}
			if (value != null && value.toString().equalsIgnoreCase(r.toString())) {
				icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
				step = 2;
			}
			root.setText(value.toString() + " ");
			if (icon != null) {
				root.setIcon(icon);
			}
			final JCheckBox box = new JCheckBox();
			box.setBounds(17,-2,20,20);
			box.setMargin(new Insets(0,0,0,0));
			box.setOpaque(false);
			box.setSelected(true);				
			if (step == 0 && leaf) {
				root.setText("     " + value.toString() + " ");
				for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
					if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
						if (model.getChild(node, row - 2) instanceof JCheckTreeNode) {
							JCheckTreeNode select = (JCheckTreeNode)model.getChild(node, row - 2);
							box.setSelected(select.isSelected());
						}
					}
				}
				root.add(box);
			}
			root.setForeground(box.isSelected() ? Color.BLACK : Color.RED);
			if (selected) {
				root.setOpaque(true);
				root.setForeground(Color.WHITE);
				root.setBackground(Color.DARK_GRAY);
			}
			root.setFont(_default_font);
			return root;
		}				
	};
	private JTextArea _log;	
	public Compare() {
		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQueryAnalizer - Assistente para Comparação de databases");
		_DIALOG.setMaximumSize(new Dimension(500,615));
		_DIALOG.setMinimumSize(new Dimension(500,615));
		_DIALOG.setPreferredSize(new Dimension(500,615));
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_DIALOG.setIconImage(new ImageIcon(ClassLoader.getSystemResource("gauge.png")).getImage());
			// database de origem <-
		JLabel text2 = new JLabel("<html>Selecione a <i>database</i> de <u>referência</u>:</html>");
		text2.setFont(_default_font);
		text2.setForeground(Color.DARK_GRAY);
		text2.setBounds(10,5,475,20);
		_DIALOG.add(text2);
		
		_origem = new JTree(new DefaultMutableTreeNode("..."));
		_origem.setFont(_default_font);
		_origem.setOpaque(true);
		_origem.setBorder(null);
		_origem.setCellRenderer(new TreeCellRenderer() {
			private JLabel root = null;
			private ImageIcon icon = null;				
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
				if (value == null) return null;
				int step = 0;
				icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));						
				TreeModel model = tree.getModel();
				DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
				root = new JLabel();
				root.setOpaque(false);
				for (int i = 0; i < model.getChildCount(r); i++) {
					if (value != null && value.toString().equalsIgnoreCase(model.getChild(r, i).toString())) {
						icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
						step = 1;
					}
				}
				if (value != null && value.toString().equalsIgnoreCase(r.toString())) {
					icon = new ImageIcon(ClassLoader.getSystemResource("node.png"));
					step = 2;
				}
				if (icon != null) {
					root.setIcon(icon);
				}
				root.setForeground(selected ? Color.RED : Color.BLACK);
				if (step == 2 || value.toString().equalsIgnoreCase(_CONNECTION_ORIG != null && _CONNECTION_ORIG.getName() != null ? _CONNECTION_ORIG.getName() : "")) {
					root.setFont(new Font(_origem.getFont().getFamily(), step == 2 ? Font.BOLD : Font.ROMAN_BASELINE,_origem.getFont().getSize()));
					root.setForeground(step != 2 ? new Color(0,153,255) : new Color(0,0,120));
				}
				else if (step == 1) {
					root.setFont(new Font(_origem.getFont().getFamily(),Font.ITALIC,_origem.getFont().getSize()));
				}
				else if (step == 0) {
					root.setForeground(selected ? Color.RED : Color.GRAY);
					root.setFont(_origem.getFont());
				}
					if (selected) {
					root.setOpaque(true);
					if (!value.toString().equalsIgnoreCase(_CONNECTION_ORIG != null && _CONNECTION_ORIG.getName() != null ? _CONNECTION_ORIG.getName() : "")) {
						root.setForeground(Color.WHITE);
						root.setBackground(Color.BLACK);
					}
					else {
						root.setBackground(new Color(193,223,246));
					}
				}
				root.setText(value.toString() + " ");
				root.setSize(root.getFontMetrics(root.getFont()).stringWidth(value.toString()), 18);
				root.setPreferredSize(new Dimension((int)root.getFontMetrics(root.getFont()).getStringBounds(value.toString(), root.getGraphics()).getWidth() + 30, 18));
				return root;
			}				
		});
		_origem.setRowHeight(18);
		JScrollPane scrolls_1 = new JScrollPane(_origem);
		scrolls_1.setBounds(10,25,230,110);
		scrolls_1.setAutoscrolls(true);
		_DIALOG.add(scrolls_1);
		_origem.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') { selectOriginDatabase(); }
			}
			@Override
			public void keyReleased(KeyEvent event) { }
			@Override
			public void keyTyped(KeyEvent event) { }
		});
		_origem.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent event) { }
			@Override
			public void mouseEntered(MouseEvent event) { }
			@Override
			public void mouseExited(MouseEvent event) { }
			@Override
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectOriginDatabase(); } 
			}
			@Override
			public void mouseReleased(MouseEvent event) { }				
		});
			// database de destino <-
		JLabel text3 = new JLabel("<html>Selecione a <i>database</i> a ser <u>comparada</u>:</html>");
		text3.setFont(_default_font);
		text3.setForeground(Color.DARK_GRAY);
		text3.setBounds(10,141,475,20);
		_DIALOG.add(text3);
		
		_destino = new JTree(new DefaultMutableTreeNode("..."));
		_destino.setFont(_default_font);
		_destino.setOpaque(true);
		_destino.setBorder(null);
		_destino.setCellRenderer(new TreeCellRenderer() {
			private JLabel root = null;
			private ImageIcon icon = null;				
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
				if (value == null) return null;
				int step = 0;
				icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));						
				TreeModel model = tree.getModel();
				DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
				root = new JLabel();
				root.setOpaque(false);
				for (int i = 0; i < model.getChildCount(r); i++) {
					if (value != null && value.toString().equalsIgnoreCase(model.getChild(r, i).toString())) {
						icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
						step = 1;
					}
				}
				if (value != null && value.toString().equalsIgnoreCase(r.toString())) {
					icon = new ImageIcon(ClassLoader.getSystemResource("node.png"));
					step = 2;
				}
				if (icon != null) {
					root.setIcon(icon);
				}
				root.setForeground(selected ? Color.RED : Color.BLACK);
				if (step == 2 || value.toString().equalsIgnoreCase(_CONNECTION_DEST != null && _CONNECTION_DEST.getName() != null ? _CONNECTION_DEST.getName() : "")) {
					root.setFont(new Font(_origem.getFont().getFamily(), step == 2 ? Font.BOLD : Font.ROMAN_BASELINE,_origem.getFont().getSize()));
					root.setForeground(step != 2 ? new Color(0,153,255) : new Color(0,0,120));
				}
				else if (step == 1) {
					root.setFont(new Font(_origem.getFont().getFamily(),Font.ITALIC,_origem.getFont().getSize()));
				}
				else if (step == 0) {
					root.setForeground(selected ? Color.RED : Color.GRAY);
					root.setFont(_origem.getFont());
				}
					if (selected) {
					root.setOpaque(true);
					if (!value.toString().equalsIgnoreCase(_CONNECTION_DEST != null && _CONNECTION_DEST.getName() != null ? _CONNECTION_DEST.getName() : "")) {
						root.setForeground(Color.WHITE);
						root.setBackground(Color.BLACK);
					}
					else {
						root.setBackground(new Color(193,223,246));
					}
				}
				root.setText(value.toString() + " ");
				root.setSize(root.getFontMetrics(root.getFont()).stringWidth(value.toString()), 18);
				root.setPreferredSize(new Dimension((int)root.getFontMetrics(root.getFont()).getStringBounds(value.toString(), root.getGraphics()).getWidth() + 30, 18));
				return root;
			}				
		});
		_destino.setRowHeight(18);
		JScrollPane scrolls_2 = new JScrollPane(_destino);
		scrolls_2.setBounds(10,162,230,110);
		scrolls_2.setAutoscrolls(true);
		_DIALOG.add(scrolls_2);
		_destino.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') { selectDestinationDatabase(); }
			}
			@Override
			public void keyReleased(KeyEvent event) { }
			@Override
			public void keyTyped(KeyEvent event) { }
		});
		_destino.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent event) { }
			@Override
			public void mouseEntered(MouseEvent event) { }
			@Override
			public void mouseExited(MouseEvent event) { }
			@Override
			public void mousePressed(MouseEvent event) { 
				if (event.getClickCount() >= 2) { selectDestinationDatabase(); } 
			}
			@Override
			public void mouseReleased(MouseEvent event) { }				
		});
			
		
		// tabelas da database a serem salvas <-
		JLabel text4 = new JLabel("<html>Selecione as <i>tabelas</i> a serem analisadas:</html>");
		text4.setFont(_default_font);
		text4.setForeground(Color.DARK_GRAY);
		text4.setBounds(245,5,460,20);
		_DIALOG.add(text4);
		
		_list = new JTree(new DefaultMutableTreeNode("Selecione uma database!"));
		_list.getRootPane();
		_list.setCellRenderer(_list_render);
		_list.setOpaque(true);
		JScrollPane scrolls = new JScrollPane(_list);
		scrolls.setBounds(245,25,235,247);
		scrolls.setAutoscrolls(true);
		_DIALOG.add(scrolls);
		_list.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.getKeyCode() == 32 || event.getKeyCode() == 10 || event.getKeyChar() == '+' || event.getKeyChar() == '-') && event.getComponent() != null && event.getComponent() instanceof JTree) { selectOriginTable(); }
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
				if (event.getClickCount() >= 2) { selectOriginTable(); } 
			}
			@Override
			public void mouseReleased(MouseEvent event) { }
		});
		
		
		
		JLabel text5 = new JLabel("<html><i>Resultados</i> da comparação entre as databases</html>");
		text5.setFont(_default_font);
		text5.setForeground(Color.DARK_GRAY);
		text5.setBounds(10,280,400,15);
		_DIALOG.add(text5);
		
		_log = new JTextArea();
		_log.setFont(new Font(Font.MONOSPACED, Font.ROMAN_BASELINE, 12));
		_log.setForeground(Color.DARK_GRAY);
		JScrollPane scroll5 = new JScrollPane(_log);
		scroll5.setBounds(10,297,475,180);
		scroll5.setAutoscrolls(true);
		_DIALOG.add(scroll5);
		_log.append(Font.MONOSPACED);
		
		
		
		
		
		
		_run = new JButton("<html><u>I</u>niciar</html>");
		_run.setFont(_default_font);
		_run.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if ((_CONNECTION_ORIG == null || (_CONNECTION_ORIG != null && !_CONNECTION_ORIG.isConnected()))) {
					JOptionPane.showMessageDialog(null, "<html>A conexão com o <b>banco de dados de referência</b> não está disponível!<br><i>Verifique a conectividade com o bancos de dados de origem e tente novamente!</i></html>", "Alerta!", JOptionPane.OK_OPTION);
					return;
				}
				else if (_CONNECTION_DEST == null || (_CONNECTION_DEST != null && !_CONNECTION_DEST.isConnected())) {
					JOptionPane.showMessageDialog(null, "<html>A conexão com o <b>banco de dados a ser analisado</b> não está disponível!<br><i>Verifique a conectividade com o bancos de dados de origem e tente novamente!</i></html>", "Alerta!", JOptionPane.OK_OPTION);
					return;
				}
				
				if (_CONNECTION_ORIG.getName().equalsIgnoreCase(_CONNECTION_DEST.getName())) {
					int option = JOptionPane.showConfirmDialog(null, "<html>O <b>nome</b> da database de <i>referência</i> é o mesmo da database de <i>analise</i>, tem certeza que deseja prosseguir?</i></html>", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.NO_OPTION) {
						return;
					}
				}
				
				_origem.setEnabled(false);
				_destino.setEnabled(false);
				_list.setEnabled(false);
				_run.setEnabled(false);
				_close.setEnabled(false);
				_origem.setEnabled(false);
				TreeModel model = _list.getModel();
				_TABLE_LIST = new FastList<String>(); 
				for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
					if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
						for (int j = 0; j < node.getChildCount(); j++) {
							if (node.getChildAt(j) instanceof JCheckTreeNode) {
								JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j);
								if (row.isSelected()) { _TABLE_LIST.add(row.toString()); }
							}
						}
						int option = JOptionPane.showConfirmDialog(null, "<html>Foram selecionadas: " + (_TABLE_LIST.size()) + "/" + (node.getChildCount()) + " tabelas para a cópia, existem: " + (node.getChildCount() - _TABLE_LIST.size()) + " tabelas desmarcadas.<br><br><b>Deseja prosseguir com a cópia?</b><br><i><font color=red>Todas as tabelas na database destino com mesmo nome serão sobrescritas!</font></i></html>" , "Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							_errors_io = 0;
							_errors_other = 0;
							_SELECTED_TABLES = _TABLE_LIST.size();
							try {
								executeBackup();
							}
							catch (Exception e1) { _errors_io += 1; e1.printStackTrace(); }  
						}
						else {
							_origem.setEnabled(true);
							_destino.setEnabled(true);
							_list.setEnabled(true);
							_run.setEnabled(true);
							_close.setEnabled(true);
						}
						return;
					}
				}
					
			}
				
		});
		_run.setMnemonic(KeyEvent.VK_E);
		_run.setBounds(255,540,150,35);
		_run.setEnabled(false);
		_DIALOG.add(_run);
	
		_close = new JButton("<html><u>S</u>air</html>");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_close.setBounds(410,540,75,35);
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
		
		
		JLabel text6 = new JLabel("<html><i>Progresso</i> da comparação:</html>");
		text6.setFont(_default_font);
		text6.setForeground(Color.DARK_GRAY);
		text6.setBounds(10,480,475,20);
		text6.setOpaque(true);
		_DIALOG.add(text6);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,500,475,35);
		_progress.setText("");
		_DIALOG.add(_progress);			
		
		_DIALOG.setVisible(true);
		getDatabaseOriginList();
		getDatabaseDestinationList();
		
		_DIALOG.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent a) { }
			@Override
			public void windowClosed(WindowEvent a) { }
			@SuppressWarnings("deprecation")
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (_thread != null && _thread.isAlive()) {
					int option = JOptionPane.showConfirmDialog(_DIALOG, "Existe uma análise de databases em execução, tem certeza que deseja interromper a análise?", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						_thread.stop();
						_thread = null;
					}
					else { return; }
				}
				_DIALOG.dispose();
				if (_CONNECTION_ORIG != null && _CONNECTION_ORIG.isConnected()) {
					_CONNECTION_ORIG.closeConnection();
				}
				if (_CONNECTION_DEST != null && _CONNECTION_DEST.isConnected()) {
					_CONNECTION_DEST.closeConnection();
				}
				System.out.println("*** Copia abortada!");
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
			DefaultTreeModel model = (DefaultTreeModel)_list.getModel();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(host == null ? "Você está desconectado ou não há database selecionada!" : host);
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(database);
			root.add(child);
			try {
				ResultSet rs = _CONNECTION_ORIG.executeQuery("SHOW TABLES");
				while (rs.next()) {
					child.add(new JCheckTreeNode(rs.getString(1)));
				}
				rs.close();
			}
			catch (Exception e) {
				e.printStackTrace();
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
		System.out.println(selection);
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
	
	
	public void executeBackup() {
		if (_TABLE_LIST != null) {
			int perc = (int)( ( ( 1.f * (_SELECTED_TABLES - _TABLE_LIST.size() ) ) / (_SELECTED_TABLES) ) * 100);
			_progress.setMainProgress(perc);
			System.gc();
		}
		if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
			/** COMPARA E VERIFICA SE AS TABELAS MARCADAS POSSUEM UMA TABELA CORRESPONDENTE NA DATABASE EM ANÁLISE: */
			try {
				ResultSet rs = _CONNECTION_DEST.executeQuery("SHOW TABLES");
				boolean exists = false;
				FastList<String> list = new FastList<String>();
				for (int i = 0; i < _TABLE_LIST.size(); i++) {
					while (rs.next()) {
						exists = (exists ? true : (rs.getString(1) != null ? rs.getString(1).equalsIgnoreCase(_TABLE_LIST.get(i) != null ? _TABLE_LIST.get(i) : "") : false));
					}
					if (!exists) {
						list.add(_TABLE_LIST.get(i) != null ? _TABLE_LIST.get(i) : "");
					}
					rs.beforeFirst();
				}
				boolean result = false;
				_log.append("=====================================================\n");
				_log.append("1# Verificando a existência das tabelas selecionadas\n");
				_log.append("-----------------------------------------------------\n");
				if (list.size() > 0) { _log.append(" - As seguintes tabela não possuem correspondentes na database em análise\n"); }
				else {  _log.append(" - Todas as tabelas selecionadas possuem correspondentes na database em análise!\n"); }
				for (Object table : list.toArray()) {
					 result = _TABLE_LIST.remove((String)table);
					_log.append("    > " + table + (result ? "" : "*") + "\n");
				}
			}
			catch (Exception e) { }
			
			doCompare compare = new doCompare(_TABLE_LIST.get(0));
			_thread = new Thread(compare);
			_thread.start();
			
		}
		else {
			_list.setEnabled(true);
			_run.setEnabled(true);
			_close.setEnabled(true);
			_origem.setEnabled(true);
			_progress.setText("<html>" + (_errors_io + _errors_io == 0 ? "<font color='#000000'><b>Análise concluida com <b>sucesso</b>!" : "<font color='#990000'><b>Backup concluído com: " + (_errors_io + _errors_other) + " ERROS!</b></font>") + "</b></font></html>");
			JOptionPane.showMessageDialog(null, (_errors_io + _errors_io == 0 ? "Análise concluida com sucesso!" : "Cópia concluída com: " + (_errors_io + _errors_other) + " erros!\n\nATENÇÃO: ESTA CÓPIA NÃO É CONFIÁVEL!!!"), "Aviso!", JOptionPane.OK_OPTION);
		}
	}
	
	public void stopBackup() {
		if (_thread != null && _thread.isAlive()) {
			_thread.interrupt();
			_thread = null;
		}
	}
	
	public void getDatabaseOriginList() {
		JTabbedPane tabs = MainWindow.getTabs();
		Component c = null;
		JQueryPane pane = null;
		SQLConnectionManager con = null;
		ResultSet rs = null;
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
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database a ser comparada");
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
	
	
	
	
	public class doCompare implements Runnable {
		private String _TABLE;
		public doCompare (String table) {
			_TABLE = table;
		}
		@Override
		public void run() {
			try {
				_log.append("===================================================\n");
				_log.append("2# Comparando a estrutura das tabelas selecionadas\n");
				_log.append("---------------------------------------------------\n");
				_progress.setText("<html>Comparando <u>estrutura</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b></html>");
				// -- estrutura da tabela.
				ResultSet rsA = _CONNECTION_ORIG.executeQuery("DESCRIBE `" + _TABLE + "`");
				ResultSet rsB = _CONNECTION_DEST.executeQuery("DESCRIBE `" + _TABLE + "`");
				ResultSetMetaData headerA = rsA.getMetaData();
				ResultSetMetaData headerB = rsB.getMetaData();				
				int[] compatible = new int[6];			
				String strA = null;
				String strB = null;
				while (rsA.next()) {
					while (rsB.next()) {
						for (int i = 1; i <= headerA.getColumnCount(); i++) {
							strA = (i > headerA.getColumnCount() ? null : rsA.getString(i));
							strB = (i > headerB.getColumnCount() ? null : rsB.getString(i));
							switch (i) {
								case 1: // nome da coluna (1 = campo existe, 0 = campo nao existe)
									compatible[i] = (strA != null && strB != null  && strA.equalsIgnoreCase(strB) ? 1 : 0);
									break;
								case 2: // tipo de campo (1 = igual, -1 = tipo igual mas tamanho diferente, -2 tipos diferentes)
									compatible[i] = (strA != null && strB != null  && strA.equalsIgnoreCase(strB) ? 1 : 0);
									compatible[i] = (compatible[i] == 1 ? 1 : (strA != null && strB != null && strA.split("(")[0].equalsIgnoreCase(strB.split("(")[0]) ? -1 : -2));
									break;
								case 3: // pode ser nulo? (1 = campos iguais, 0 = campos diferentes)
								case 4: // chave primaria? (1 = campos iguais, 0 = campos diferentes)
								case 5: // valor padrao? (1 = campos iguais, 0 = campos diferentes)
									compatible[i] = ((strA != null && strB != null  && strA.equalsIgnoreCase(strB)) || (strA == null && strB == null) ? 1 : 0);
									break;
							}
						}
					}
					rsB.beforeFirst();
				}
				@SuppressWarnings("unused")
				FastList<String[]> compare_fail = new FastList<String[]>();
				
				/*
				while (rs != null && rs.next()) {
					this.toDestinationDatabase("DROP TABLE IF EXISTS `" + _TABLE + "`");
					this.toDestinationDatabase(rs.getString(2));
				}
				
				
				
				
				
				
				
				
				
				_progress.setText("<html>Preparando exportação dos <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b></html>");				
				rs.close();
				rs = _CONNECTION_ORIG.executeQuery("SELECT * FROM " + _TABLE);
				
				// -- insert header
				String                 header = "INSERT INTO " + _TABLE + " (";
				ResultSetMetaData header_data = rs.getMetaData();
				for (int i = 0; i < header_data.getColumnCount(); i++) {
					if (header_data.getColumnName(i + 1) != null) {
						header += "`" + header_data.getColumnName(i + 1) + "`";
						if ((i + 1) != header_data.getColumnCount()) { header += ","; }
					}
				}
				header += ")";

				// -- blocos de registros (de 1000 em 1000)
				StringBuffer out = null;
				rs.last();
				int count = rs.getRow();
				rs.beforeFirst();
				boolean control = true;
				int perc = 0;
				int perc2 = 0;
				int query_length = 0;
				int query_maxlength = 0;
				String item = null;
				while (rs != null && count > 0 && control) {
					out = new StringBuffer();
					out.append(header + " VALUES \r\n");
					for (int i = 0; i < 1000 && rs.next(); i++) {
						perc2 = (int)(((1.f + rs.getRow()) / count) * 100);
						if (perc2 != perc) {
							_progress.setText("<html>Exportando <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b> <i>" + perc2 +"%</i></html>");
							_progress.setItemProgress(perc2);
						}
						perc = perc2;
						out.append("(");
						query_maxlength = Math.max(query_maxlength, query_length);
						query_length = out.length() + 2; // ->> ; [+] \n <<- 
						for (int j = 1; j <= header_data.getColumnCount(); j++) {
							item = (rs.getString(j) == null ? "NULL" : "'" + getSecureString(rs.getString(j)) + "'");
							//item = (rs.getObject(j) == null ? "NULL" : "'" + getSecureString((String)rs.getObject(j)) + "'");
							//item = (rs.getNString(j) == null ? "NULL" : "'" + getSecureString(rs.getgetNString(j)) + "'");
							
							query_length += item.length();
							out.append(item); // <-- r2
							if ((j) != header_data.getColumnCount()) { out.append(","); }
						}
						out.append(")");
						if (i == 999 || count == rs.getRow() || (out.length() + query_maxlength) >= 1048576) {
							out.append(";\n");
							if (control == true && count == rs.getRow()) { control = false; }
							break;
						}
						else {
							out.append(",\n");
						}
					}
					if (_thread == null) {
						return;
					}
					this.toDestinationDatabase(out.toString());
					if (control && rs.getRow() == 0) { control = false; }
					
				}
				rs.close();
				*/
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
				executeBackup();
			}
		
		}
		
		@SuppressWarnings("unused")
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
	/*
	public class JProgressLabel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel label_1;
		private JProgressBar label_2;
		private JProgressBar label_3;

		public JProgressLabel() {
			super();
			this.setBounds(10,430,475,30);
			this.setLayout(null);
			this.setBorder(BorderFactory.createLineBorder(new Color(210,210,210)));
			// texto
			label_1 = new JLabel("");
			label_1.setOpaque(false);
			label_1.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 10));
			label_1.setForeground(Color.BLACK);
			label_1.setHorizontalAlignment(JLabel.CENTER);
			label_1.setVerticalAlignment(JLabel.CENTER);
			label_1.setBounds(0,0,475,26);
			// barra de progresso maior.
			label_2 = new JProgressBar();
			label_2.setBounds(10,430,475,30);
			label_2.setMinimum(0);
			label_2.setMaximum(100);
			label_2.setBounds(1,1,0,26);
			label_2.setBorderPainted(false);
			// barra de progresso menor.
			label_3 = new JProgressBar();
			label_3.setBounds(10,430,475,30);
			label_3.setMinimum(0);
			label_3.setMaximum(100);
			label_3.setBounds(1,27,0,3);
			label_3.setBorderPainted(false);
			label_3.setBorder(null);

			this.add(label_1);
			this.add(label_2);
			this.add(label_3);
		}
		
		public void setPanelBackground(Color c) {
			this.setBackground(c);
		}

		public void setPanelBounds(int x, int y, int w, int h) {
			this.setBounds(x, y, w, h);
			label_1.setBounds(0, 0, w, Math.round(h * 0.72f));
			label_2.setBounds(0, 0, w, Math.round(h * 0.72f));
			label_3.setBounds(0, Math.round(h * 0.72f), w, Math.round(h * 0.28f));
		}
		
		public void setText(String t) {
			label_1.setText(t);
		}
		
		public void setMainProgress(float perc) {		
			label_2.setValue((int)perc);
		}
		
		public void setItemProgress(float perc) {
			label_3.setValue((int)perc);
		}
	}
	*/
		
	public String getSecureString(String text) {
		if (text == null) {
			return "NULL";
		}
		text = text.replace("\\","\\" + "\\");
		text = text.replace("\'","\\" + "\'");
		text = text.replace("\"","\\" + "\"");
		text = text.replace("\t","\\" + "t");
		return text;
	}
		
	public class JCheckTreeNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = 1L;
		private boolean isSelected;
		  
		public JCheckTreeNode(Object node) {
			this(node, true, true);
		}
		
		public JCheckTreeNode(Object node, boolean allowsChildren, boolean isSelected) {
			super(node, allowsChildren);
		    this.isSelected = isSelected;
		}
		
		public void setSelected(boolean isSelected) {
		    this.isSelected = isSelected;
		    if (children != null) {
		    	Enumeration<?> e = children.elements();
		    	while (e.hasMoreElements()) {
		    		JCheckTreeNode leaf = (JCheckTreeNode)e.nextElement();
		    		leaf.setSelected(isSelected);
		    	}
		    }
		}

		public boolean isSelected() {
			return isSelected;
		}
	}

}
