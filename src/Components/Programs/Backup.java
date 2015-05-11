package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import javolution.util.FastList;
import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.Util;
import Components.MainWindowComponents.JParametersPanel;
import Components.MainWindowComponents.JProgressLabel;
import Components.MainWindowComponents.JQueryPane;
import Components.MainWindowComponents.JTextEditor;

public class Backup {
	private SQLConnectionManager _connection;
	private JFrame _dialog = null;
	private JTree _list;
	private JButton _run;
	private JButton _close;
	private List<String> _table_list = new ArrayList<String>();
	private int _table_list_size;
	private String _selected_database;
	private File _output_file = null;
	private String _selected_host;
	private FileOutputStream _output_stream = null;
	private JProgressLabel _progress;
	private Thread _thread;
	private int _errors_io = 0;
	private int _errors_other = 0;
	private JTextEditor _file;
	private JTree _database;
	private JComboBox<String> _options;
	private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
	private long _registers_total = 0;
	private long _registers_processed = 0;
	private float _perc_a;
	private float _perc_b;
	private JLabel _text4;
	private ImageIcon check_16;
	private ImageIcon uncheck_16;
	private JComboBox<String> _options2;
	private JCheckBox _options3;
	private long _paint_time = 0L;
	private static List<DatabaseSupported> _supported_databases = new ArrayList<DatabaseSupported>(); 
	static {
		_supported_databases.add(new DatabaseSupported(SQLConnectionManager.DB_MYSQL, null));
		_supported_databases.add(new DatabaseSupported(SQLConnectionManager.DB_MSSQL, "&nbsp; &nbsp; &nbsp;*** Os seguintes ítens não serão exportados:<ul><li>Índices de tabelas.</li><li>Views do banco de dados.</li><li>Procedures do banco de dados.</li></ul>"));
	}
	private static class DatabaseSupported {
		private int _type;
		private String _restrictions;
		public DatabaseSupported(int type, String msg) {
			_type = type;
			_restrictions = msg;
		}
		public String getRestrictions() {
			return _restrictions;
		}
		public int getType() {
			return _type;
		}
	}
	
	
	public Backup() {
		check_16 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		check_16.setImage(check_16.getImage().getScaledInstance(16, 16, 100));
		uncheck_16 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		uncheck_16.setImage(uncheck_16.getImage().getScaledInstance(16, 16, 100));
		
			_dialog = new JFrame();
			_dialog.setTitle("JQuery Analizer - Assistente de Backup");
			Dimension size = new Dimension(500, 560);
			_dialog.setMaximumSize(size);
			_dialog.setMinimumSize(size);
			_dialog.setPreferredSize(size);
			_dialog.setLocationRelativeTo(null);
			_dialog.setResizable(false);
			_dialog.setLayout(null);
			_dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			_dialog.setIconImage(new ImageIcon(ClassLoader.getSystemResource("data_backup.png")).getImage());

			JLabel text1 = new JLabel("<html>Arquivo de <i>destino</i> do Backup:</html>");
			text1.setFont(_default_font);
			text1.setForeground(Color.DARK_GRAY);
			text1.setBounds(10,5,475,20);
			text1.setOpaque(true);
			_dialog.add(text1);
			
			_file = new JTextEditor(null, new Dimension(475,24));
			_file.setBounds(10,25,475,24);
			_file.setText(MainWindow.getPropertie("restore_file", "C:\\"));
			_file.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) { }
				public void keyTyped(KeyEvent arg0) { }
				public void keyReleased(KeyEvent arg0) {
					MainWindow.setPropertie("restore_file", _file.getText());
					MainWindow.saveProperties();
				}
			});
			_file.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					MainWindow.setPropertie("restore_file", _file.getText());
					MainWindow.saveProperties();
				}
			});
			_file.setSupportedFileExtensions(new String[]{".SQL", ".001"}, "Backups SQL");
			_file.setApproveButtonLabel("Abrir");
			_dialog.add(_file);
			
			// database de origem <-
			JLabel text2 = new JLabel("<html>Selecione a <i>database</i>:</html>");
			text2.setFont(_default_font);
			text2.setForeground(Color.DARK_GRAY);
			text2.setBounds(10,55,475,20);
			_dialog.add(text2);
			
			_database = new JTree(new DefaultMutableTreeNode("Carregando lista de databases, aguarde!"));
			_database.setFont(_default_font);
			_database.setOpaque(true);
			_database.setBorder(null);
			_database.setCellRenderer(new DatabaseTreeCellRender(null, null));
			_database.setRowHeight(18);
			JScrollPane scrolls_1 = new JScrollPane(_database);
			scrolls_1.setBounds(10,75,215,274);
			scrolls_1.setAutoscrolls(true);
			_dialog.add(scrolls_1);
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
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						t.start();
					} 
				}
				public void mouseReleased(MouseEvent event) { }				
			});
			// tabelas da database a serem salvas <-
			JLabel text3 = new JLabel("<html>Selecione as <i>tabelas</i>:</html>");
			text3.setFont(_default_font);
			text3.setForeground(Color.DARK_GRAY);
			text3.setBounds(230,55,475,20);
			_dialog.add(text3);
			
			_list = new JTree(new DefaultMutableTreeNode("Aguarde!"));
			_list.getRootPane();
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
			JScrollPane scrolls = new JScrollPane(_list);
			scrolls.setBounds(230,75,255,274);
			scrolls.setAutoscrolls(true);
			_dialog.add(scrolls);
			
			
			_run = new JButton("<html><u>E</u>xecutar Backup</html>");
			_run.setFont(_default_font);
			_run.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					int option = JOptionPane.NO_OPTION;
					if (_options != null && _options.getSelectedIndex() != 0) {
						option = JOptionPane.showConfirmDialog(null, "Você selecionou a opçao de '"+_options.getSelectedItem()+"', com esta opção de backup selecionada o backup realizado não será completo. Deseja prosseguir?" , "Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.NO_OPTION) { return; }									
					}
					toogleActions(false);
					TreeModel model = _list.getModel();
					_table_list = new ArrayList<String>();
					String table = null;
					String text = null;
					boolean tag = false;
					for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
						if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
							for (int j = 0; j < node.getChildCount(); j++) {
								if (node.getChildAt(j) instanceof JCheckTreeNode) {
									JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j);
									table = row.toString();
									text = "";
									for (int k = 0; k < table.length(); k++) {
										if (table.charAt(k) == '<') { tag = true; }
										else if (table.charAt(k) == '>') { tag = false; continue; }
										if (!tag) { text += table.charAt(k); }
									}
									if (row.isSelected()) { _table_list.add(text); }
								}
							}
							
							_table_list_size = _table_list.size();
							option = JOptionPane.showConfirmDialog(null, "Foram selecionadas: " + (_table_list.size()) + "/" + (node.getChildCount()) + " tabelas para realizar o backup, existem: " + (node.getChildCount() - _table_list.size()) + " tabelas desmarcadas.\nDeseja prosseguir com o backup?" , "Confirmação", JOptionPane.YES_OPTION);
							if (option == JOptionPane.YES_OPTION) {
								_errors_io = 0;
								_errors_other = 0;
								_output_file = new File(_file.getText());
								if (_output_file.exists()) {
									option = JOptionPane.showConfirmDialog(null, "O arquivo selecionado: '" + _output_file.getAbsolutePath().toUpperCase() + "' já existe.\n\nDeseja sobrescrever este arquivo!?" , "Confirmação", JOptionPane.YES_OPTION);
									if (option == JOptionPane.YES_OPTION) {
										_output_file.delete();
									}
									else {
										toogleActions(true);
										return;
									}
								}
								try {
									_output_stream = new FileOutputStream(_output_file);
									_progress.setText("<html><i>Contabilizando</i> os registros do backup atual...</html>");
									_registers_total = 0;
									try {
										EntryCount ec = new EntryCount(true);
										_thread = new Thread(ec);
										_thread.start();
									}
									catch (Exception e1) {
										toogleActions(true);
										e1.printStackTrace();
									}
								}
								catch (FileNotFoundException e1) {
									JOptionPane.showMessageDialog(null, "<html>Não foi possível criar o arquivo de backup: <b><font color='red'>" + e1.getMessage() + "</font></b></html>", "Erro!", JOptionPane.OK_OPTION);
									toogleActions(true);
									_errors_io += 1;
									e1.printStackTrace(); 
								}  
							}
							else {
								toogleActions(true);
							}
							return;
						}
					}
					if (_selected_database == null || (_selected_database != null && _selected_database.isEmpty())) {
						toogleActions(true);
						JOptionPane.showMessageDialog(null, "<html>Você deve <u>selecionar uma database</u> antes de executar um backup.</html>", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
						return;
					}
					if (_table_list == null || (_table_list != null && _table_list.size() == 0)) {
						toogleActions(true);
						JOptionPane.showMessageDialog(null, "<html>Não existem <u>tabelas marcadas</u> para backup!</html>", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
					}
				}
			});
			_run.setMnemonic(KeyEvent.VK_E);
			_run.setBounds(255,487,150,35);
			_dialog.add(_run);
			
			_close = new JButton("<html><u>S</u>air</html>");
			_close.setMnemonic(KeyEvent.VK_S);
			_close.setFont(_default_font);
			_close.setBounds(410,487,75,35);
			_close.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					for (WindowListener listener : _dialog.getWindowListeners()) {
						if (listener == null) { continue; }
						listener.windowClosing(null);
					}					
				}
			});
			_dialog.add(_close);
			
			_text4 = new JLabel("<html><i>Progresso</i> do backup:</html>");
			_text4.setFont(_default_font);
			_text4.setForeground(Color.DARK_GRAY);
			_text4.setBounds(10,354,475,20);
			_text4.setOpaque(true);
			_dialog.add(_text4);
			
			_progress = new JProgressLabel();
			_progress.setPanelBounds(10,374,475,35);
			_progress.setText("selecione a database");
			_dialog.add(_progress);			
			
			JLabel opcoes = new JLabel();
			opcoes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Opções de backup", SwingConstants.CENTER, SwingConstants.CENTER, _default_font, Color.DARK_GRAY));
			opcoes.setBounds(10,420,475,65);
			_dialog.add(opcoes);
			
			
			JLabel option_t1 = new JLabel("<html><b>a</b>. Tipo de backup.</html>");
			option_t1.setBounds(10, 18, 225, 18);
			option_t1.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
			opcoes.add(option_t1);
			
			_options = new JComboBox<String>() {
				private static final long serialVersionUID = -6516283705456571873L;
				public void draw(Graphics g) {
					Rectangle area = getBounds();
					BufferedImage image = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_RGB);					
					JLabel root = new JLabel(getSelectedItem().toString());
					root.setBounds(area);
					root.setBackground(getBackground());
					root.setForeground(getForeground());
					root.setHorizontalAlignment(JLabel.CENTER);
					root.setOpaque(true);
					root.setFont(getFont());
					root.paintImmediately(area);
					Graphics2D g1 = image.createGraphics();
					g1.fillRect(0, 0, area.width, area.height);
					root.paint(g1);
					g1.dispose();
					g.drawImage(image, 0, 0, null);
				}
				public void paintComponent(Graphics g) {
					draw(g);
				}
			};
			_options.setBounds(10,38,225,20);
			_options.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
			_options.addItem("Exportar estruturas e dados");
			_options.addItem("Exportar estruturas");
			_options.addItem("Exportar dados");
			opcoes.add(_options);
			
			JLabel option_t2 = new JLabel("<html><b>b</b>. Banco de dados de destino.</html>");
			option_t2.setBounds(240, 18, 225, 18);
			option_t2.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
			opcoes.add(option_t2);
			
			_options2 = new JComboBox<String>() {
				private static final long serialVersionUID = -6516283705456571873L;
				public void draw(Graphics g) {
					Rectangle area = getBounds();
					BufferedImage image = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_RGB);					
					JLabel root = new JLabel(getSelectedItem().toString());
					root.setBounds(area);
					root.setBackground(getBackground());
					root.setForeground(getForeground());
					root.setHorizontalAlignment(JLabel.CENTER);
					root.setOpaque(true);
					root.setFont(getFont());
					root.paintImmediately(area);
					Graphics2D g1 = image.createGraphics();
					g1.fillRect(0, 0, area.width, area.height);
					root.paint(g1);
					g1.dispose();
					g.drawImage(image, 0, 0, null);
				}
				public void paintComponent(Graphics g) {
					draw(g);
				}
			};
			_options2.setBounds(240,38,225,20);
			_options2.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
			_options2.addItem("Oracle MySQL Server");
			//_options2.addItem("Oracle Database Server");
			//_options2.addItem("PostgreSQL Server");
			_options2.addItem("Microsoft SQL Server");
			opcoes.add(_options2);
			
			_options3 = new JCheckBox("Não paginar consultas");
			_options3.setBounds(10,500,200,15);
			_options3.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
			_dialog.add(_options3);

			_dialog.addWindowListener(new WindowListener(){
				public void windowActivated(WindowEvent a) { }
				public void windowClosed(WindowEvent a) { }
				@SuppressWarnings("deprecation")
				public void windowClosing(WindowEvent arg0) {
					if (_thread != null && _thread.isAlive()) {
						int option = JOptionPane.showConfirmDialog(_dialog, "Existe um backup em execução, tem certeza que deseja interromper o backup?", "Confirmação", JOptionPane.YES_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							_thread.stop();
							_thread = null;
						}
						else {
							return;
						}
					}
					disconnect();
					_dialog.dispose();
					_dialog = null;
				}
				public void windowDeactivated(WindowEvent a) { }
				public void windowDeiconified(WindowEvent a) { }
				public void windowIconified(WindowEvent a) { }
				public void windowOpened(WindowEvent a) { }
			});
	}
	
	public void start() {
		if (_dialog != null && !_dialog.isVisible()) {
			_dialog.setVisible(true);
		}
		Thread t = new Thread(new DatabaseList());
		t.start();
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
			_selected_host = parameters.getHost();
			_progress.updateProgress("<html>Aguardando conexão com o banco de dados <font color='blue'>" + location + "</font></html>", 0f, 0f);
			System.out.println("driver: " + parameters.getConnectorDriver() + ", string: " + parameters.getConnectionString().replace("/;", "/" + path[2] + ";"));
			reconnect(parameters.getConnectorDriver(), parameters.getConnectionString().replace("/;", "/" + path[2] + ";"), parameters.getUser(), parameters.getPass());
			if (_connection != null && _connection.isConnected()) {
				boolean check = false;
				String restrictions = null;
				for (DatabaseSupported ds : _supported_databases) {
					if (_connection != null && _connection.isConnected() && ds.getType() == _connection.getServerType()) {
						check = true;
						restrictions = ds.getRestrictions();
					}
				}
				if (!check) {
					toogleActions(false);
					_database.setEnabled(true);
					_progress.updateProgress("<html>O banco de dados <font color='blue'>"+location+"</font> não suportado!</html>", 100f, 0f);
					JOptionPane.showMessageDialog(null, "Este recurso ainda não foi implementado para o banco de dados selecionado.", "JQueryAnalizer - Informação!", JOptionPane.OK_OPTION);
					return;
				}
				else {
					toogleActions(true);
				}
				if (check && restrictions != null) {
					JOptionPane.showMessageDialog(null, "<html>Esta modalidade de backup não está plenamente implementada,<br>principalmente quando se trata de uma operação cross-database,<br>as restrições conhecidas são:<br>" + restrictions + "</html>", "JQueryAnalizer - Informação!", JOptionPane.OK_OPTION);
				}
				_progress.updateProgress("<html><font color='blue'>Obtendo a lista das tabelas disponíveis... Aguarde!</font></html>", 0f, 0f);
				getTableList(path[2]);
				_database.setCellRenderer(new DatabaseTreeCellRender(path[2], path[1]));
				_progress.updateProgress("<html>Conectado ao banco de dados <font color='green'><b>"+location+"</b></font></html>", 0f, 0f);
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
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
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
		});
	}
	
	public class EntryCount implements Runnable {
		private boolean start;
		public EntryCount(boolean s) {
			start = s;
		}
		public void run() {
			if (_table_list_size == _table_list.size()) {
				ResultSet rs = null;
				try {
					int cnt = 0;
					String text = null;
					for (Object table : _table_list.toArray()) {
						if (table != null) {
							++cnt;
							Statement stmt = _connection.getConnection().createStatement();  
							stmt.execute("SELECT COUNT(*) FROM " + table.toString());  
							rs = stmt.getResultSet();
							while (rs.next()) {
								_registers_total += (("0|2").contains(String.valueOf(_options.getSelectedIndex())) ? rs.getLong(1) : 0);
								switch (_options.getSelectedIndex()) {
									case 0:
									case 1:
										++_registers_total;
										break;
								}
							}
							rs.close();
							stmt.close();
							_perc_a = (cnt * 100f) / _table_list.size();
							text = "<html>Calculando volume de registros: <b>" + Util.toPercent(_perc_a) + "</b> { <font color='blue'>" + table.toString() + "</font> }</html>";
							_progress.updateProgress(text, _perc_a, 0f);
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
				executeBackup();
			}
		}
	}
	
	public void executeBackup() {
		if (_table_list != null) {
			System.gc();
		}
		if (_table_list != null && _table_list.size() > 0) {
			doBackup backup = new doBackup(_table_list.get(0));
			_thread = new Thread(backup);
			_thread.start();
			
		}
		else {
			try {
				_output_stream.close();
				Thread t = new Thread(new doCompact(_file.getText(), _file.getText().substring(0, _file.getText().lastIndexOf(".")) + ".zip"));
				Thread.sleep(10);
				t.start();
			}
			catch (Exception e) {
				_errors_io += 1;
				e.printStackTrace();
			}
		}
	}
	
	public void toogleActions(boolean state) {
		_list.setEnabled(state);
		_run.setEnabled(state);
		_close.setEnabled(state);
		_database.setEnabled(state);
		_options.setEnabled(state);
		_options2.setEnabled(state);
	}
	
	public void stopBackup() {
		if (_thread != null && _thread.isAlive()) {
			_thread.interrupt();
			_thread = null;
		}
	}
	
	public class DatabaseList implements Runnable {
		public DatabaseList() {
			super();
			_selected_database = null;
			_selected_host = null;
		}
		public void run() {
			JTabbedPane tabs = MainWindow.getTabs();
			Component c = null;
			JQueryPane pane = null;
			SQLConnectionManager con = null;
			final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Conexões disponíveis");
			final DefaultTreeModel model = (DefaultTreeModel)_database.getModel();
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
						for (String database : con.getDatabasesList()) {
							if (database != null) { child.add(new DefaultMutableTreeNode(database)); } 
						}
					}
				}
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					model.setRoot(root);
					_database.repaint();
					MainWindow.expandAll(_database, true);
					getTableList(null);		
				}
			});
		}
	}
	
	public void getTableList(String database) {
		if (database == null) {
			((DefaultTreeModel)_list.getModel()).setRoot(new DefaultMutableTreeNode("Selecione uma database!"));
			return;
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(_selected_host == null ? "Você está desconectado do banco de dados, selecione a base de dados novamente." : _selected_host);
		root.add(new DefaultMutableTreeNode(database == null ? "Você está desconectado ou não há database selecionada!" : database));
		final DefaultTreeModel model = (DefaultTreeModel)_list.getModel();
		model.setRoot(root);
		DefaultMutableTreeNode row = null;
		for (int i = 0; i < model.getChildCount(root); i++) {
			row = (DefaultMutableTreeNode)model.getChild(root, i);
			if (row.toString() != null) break;
		}
		row.removeAllChildren();	
		if (_connection.isConnected()) {
			_selected_database = database;
			_connection.switchDatabase(database.trim());
			int size = 0;
			JCheckTreeNode node = null;
			for (String table : _connection.getTables()) {
				size += 1;
				node = new JCheckTreeNode(table);
				node.setType(JCheckTreeNode.TABLE);
				node.setSelected(true);
				row.add(node);
			}
			for (String view : _connection.getViews()) {
				size += 1;
				node = new JCheckTreeNode(view);
				node.setType(JCheckTreeNode.VIEW);
				node.setSelected(true);
				row.add(node);
			}
			if (size > 0) {
				_run.setEnabled(true);
			}
			else {
				_run.setEnabled(false);
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					model.setRoot((DefaultMutableTreeNode)model.getRoot());
					_list.revalidate();
					_list.repaint();
					MainWindow.expandAll(_list, true);
				}
			});
			
		}
	}
	
	public int getBackupDestination() {
		switch (_options2.getSelectedIndex()) {
			case 0:
				return SQLConnectionManager.DB_MYSQL;
			case 1:
				return SQLConnectionManager.DB_MSSQL;
		}
		return -1;
	}
	
	public enum BackupMethod {FULL, STRUCTURE_ONLY, DATA_ONLY};
	private class doBackup implements Runnable {
		private String _table;
		private BackupMethod _mode;
		
		public doBackup(String table) {
			_table = (_connection != null && _connection.getServerType() == SQLConnectionManager.DB_MYSQL ? "`" + table + "`" : table);
			if (_options != null) {
				switch (_options.getSelectedIndex()) {
					case 0:
						_mode = BackupMethod.FULL; 
						break;
					case 1:
						_mode = BackupMethod.STRUCTURE_ONLY;
						break;
					case 2:
						_mode = BackupMethod.DATA_ONLY;
						break;
				}
			}
		}
		
		public void run() {
			if (_connection == null || !_connection.isConnected()) {
				System.out.println("*** Conexão perdida, o backup da tabela: " + _table + ", não foi realizado.");
				return;
			}
			Statement			st		= null;
			ResultSet 			rs		= null;
			ResultSetMetaData	rs_meta	= null;
			Connection			con		= null;
			StringBuffer		out		= new StringBuffer();
			String				pk		= null,
								col		= null,
								cols	= null,
								header	= null,
								text	= null;
			int					count	= 0,
								start	= 0,
								offset	= 0,
								length	= 0,
								limit 	= 1000,
								size	= 0;
			boolean 			single	= false;
			List<String>		fields	= new FastList<String>();
			
			if (_connection.isTable(_table)) {
				if (_mode == BackupMethod.FULL || _mode == BackupMethod.STRUCTURE_ONLY) {
					
					System.out.println("*** Efetuando backup da estrutura da tabela: " + _table);
				
					// ------------------------------------------
					// -> Obtém o comando para dropar a tabela ->
					// ------------------------------------------
					switch (getBackupDestination()) {
						case SQLConnectionManager.DB_MYSQL:
						case SQLConnectionManager.DB_POSTGREE:
							header = ("DROP [object] IF EXISTS [table];\r\n").replace("[object]", _connection.isTable(_table) ? "TABLE" : "VIEW").replace("[table]", _table);
							break;
						case SQLConnectionManager.DB_ORACLE:
							header = ("SET ESCAPE OFF;\r\nSET ESCAPE \\;\r\nDROP TABLE [table];\r\n").replace("[table]", _table);
							break;
						case SQLConnectionManager.DB_SQLSERVER:
							header = ("IF EXISTS (SELECT * FROM sysobjects WHERE type='U' AND name='[table]') DROP TABLE [table];\r\n").replace("[table]", _table).replace("`", "");
							break;
					}
					text = "<html>Preparando exportação da <u><i>estrutura</i></u> da tabela: { <font color='blue'>" + (_table != null ? _table.replace("`", "") : "") + "</font> }</html>";
					_progress.updateProgress(text, _perc_b, 0f);
				
					try {
						StringBuffer struct = new StringBuffer(header);
						if ((header = _connection.getTableStructure(_table, getBackupDestination())) == null) {
							throw new Exception("O JQueryAnalizer não foi capaz de recuperar a estrutura da tabela: " + _table);
						}
						else {
							_perc_b = ((++_registers_processed * 100f) / _registers_total);
							_progress.updateProgress(text, _perc_b, 0f);
							struct.append(header);
							struct.append(";\r\n");
							_output_stream.write(struct.toString().getBytes("UTF-8"));
						}
					
					}
					catch (Exception e) {
						_errors_io += 1;
						e.printStackTrace();
						if (e.getMessage() != null) {
							JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar o <u>backup</u></b>. A aplicação retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + "</font></html>", "Erro ao executar o backup!", JOptionPane.OK_OPTION);
							int option = JOptionPane.showConfirmDialog(null, "<html>Deseja prosseguir com o backup!?<br><i>Haverá boas chances deste backup trazer dados truncados e/ou corrompidos</i>.</html>", "Confirmação", JOptionPane.YES_OPTION);
							if (option == JOptionPane.NO_OPTION) {
								_table_list.clear();
								executeBackup();
								return;
							}
							else {
								// -- reinicia o ciclo pra proxima tabela.
								if (_table_list != null && _table_list.size() > 0) {
									_table_list.remove(0);
									executeBackup();
								}
								return;
							}
						}
					}
					
				}
			
				if (_mode == BackupMethod.FULL || _mode == BackupMethod.DATA_ONLY) {
				
					System.out.println("*** Efetuando backup dos dados da tabela: " + _table);
					out.append("-- inicio do dump da tabela " + _table + "\r\n");
				
					try {
						// ------------------------------------------
						// -> Obtem a chave PK da tabela           ->
						// ------------------------------------------
						con = _connection.getConnection();
						DatabaseMetaData meta = con.getMetaData();
						rs = meta.getPrimaryKeys(null, null, _table);
						try {
							while (rs.next()) {
								pk = rs.getString("COLUMN_NAME");
							}
						}
						catch (Exception e) { e.printStackTrace(); }
						finally {
							if (rs != null && !rs.isClosed()) {
								rs.close();
								rs = null;
							}
						}

						// ------------------------------------------
						// -> Lista as colunas exceto a PK         ->
						// ------------------------------------------
						rs = meta.getColumns(null, null, _table, null);
						try {
							while (rs.next()) {
								col = rs.getString("COLUMN_NAME");
								if (!col.equalsIgnoreCase(pk)) {
									cols = (cols == null ? "" : cols + ",") + col;
									fields.add(col);
								}
							}
						}
						catch (Exception e) { e.printStackTrace(); }
						finally {
							if (rs != null && !rs.isClosed()) {
								rs.close();
								rs = null;
							}
						}
					
						// ------------------------------------------
						// -> Obtem uma contagem dos registros     ->
						// ------------------------------------------
						if (_options3 != null && !_options3.isSelected()) {
							st = _connection.getConnection().createStatement();
							rs = st.executeQuery(("SELECT /*40001 SQL_NO_CACHE */ COUNT(*) FROM [table]").replace("[table]", _table));
							if (rs != null && rs.next()) {
								count = rs.getInt(1);
								size = Math.min(1000, (int)(count * 0.1)); // --> 10% ou packs de 1000 registros.
								size = Math.max(size, 250);
								//limit = ((count / 1000f) > (int)(count / 1000f) ? (int)(count / 1000f) + 1 : (int)(count / 1000f));
								limit = ((count / (size * 1f)) > (int)(count / (size * 1f)) ? (int)(count / (size * 1f)) + 1 : (int)(count / (size * 1f)));
							}
							if (rs != null && !rs.isClosed()) {
								rs.close();
							}
							if (st != null && !st.isClosed()) {
								st.close();
							}
						}
					}
					catch (Exception e) { e.printStackTrace(); }
					
					// ------------------------------------------
					// -> Prepara o HEADER e os FIELDS p/ exp. ->
					// ------------------------------------------
					switch (getBackupDestination()) {
						case SQLConnectionManager.DB_MYSQL:
							header = ("INSERT INTO [table] ([fields]) VALUES ").replace("[table]", _table).replace("[fields]", cols + (cols != null && col.isEmpty() ? "" : ",") + pk);
							fields.add(pk);
							single = false;
							break;
							
						case SQLConnectionManager.DB_MSSQL: // -- não leva as chaves primárias.
							header = ("INSERT INTO [table] ([fields]) VALUES ").replace("[table]", _table).replace("[fields]", cols).replace("`", "");
							// -- devo perguntar na hora do backup?
							switch (_connection.getVersion()) {
								case "MS7":
								case "MS2000":
								case "MS2005":
									single = true;
									break;
								default: // -- MS2008+ multiplos
									single = false;
							}
							break;
					}
				
					// ------------------------------------------
					// -> Prepara os dados para exportação     ->
					// ------------------------------------------
					length = 0;
					for (int i = 0; i < limit; i++) {
						if (_options3 != null && !_options3.isSelected()) {
							start = Math.min(i * size, count);
							offset = Math.min(start + size, count);
						}
						text = "<html>Preparando exportação dos <u>registros</u> da tabela: { <font color='blue'>" + _table + "</font> }</html>";
						_progress.updateProgress(text, _perc_b, _perc_a);
						try {
							if (i == 0) {
								out.append("-- inicio dos dados da tabela ");
								out.append(_table);
								out.append("\r\n");
							}
							rs = _connection.getTableData(_table, start, offset);
							rs_meta = rs.getMetaData();
						}
						catch (Exception e) { e.printStackTrace(); rs = null; rs_meta = null; }
					
					
						// ------------------------------------------
						// -> Prepara os registros para exportação ->
						// ------------------------------------------
						if (rs != null) {
							try {
								StringBuffer register = null;
								for (int j = 0; rs.next(); j++) {
									register = new StringBuffer();
									if (j == 0 || single || (j % 1000 == 0) || ((1f * out.length() / (1024 * 1024)) >= 0.9)) {
										if (out.length() > 3) {
											out.replace(out.length() - 3, out.length(), ";\r\n");
											_output_stream.write(out.toString().getBytes("UTF-8"));
											out = new StringBuffer();
										}
										register.append(header);
									}
									register.append("(");
									for (int k = 0; k < fields.size(); k++) {
										register.append(getData(fields.get(k), rs, rs_meta));
										register.append(k + 1 == fields.size() ? "" : ",");
									}
									register.append(")");
									if (single) {
										register.append(";\r\n");
									}
									else {
										register.append(",\r\n");
									}
									out.append(register);
								
									_perc_a = ((++length * 100f) / count);
									_perc_b = ((++_registers_processed * 100f) / _registers_total);
									if (_paint_time + (40000000) <= System.nanoTime() || _perc_a >= 100 || _perc_b >= 100) {
										try {
											SwingUtilities.invokeAndWait(new Runnable() {
												public void run() {
													_text4.setText("<html><i>Progresso</i> do backup: <font color='#000000'><i><b>" + Util.toPercent(_perc_b) + "</b></i></font></html>");				
												}
											});
										}
										catch (Exception e) { e.printStackTrace(); }
									}
									text = "<html>Exportando <u>registros</u> da tabela: { <font color='blue'>" + (_table != null ? _table.replace("`","") : "") + "</font> } <i><b>" + Util.toPercent(_perc_a) +"</b></i></html>";
									_progress.updateProgress(text, _perc_b, _perc_a);
								}
								if (!rs.isClosed()) {
									rs.close();
								}
								System.gc();
								if (out.length() > 3 && out.substring(out.length() - 3).contains(",\r\n")) {
									out.replace(out.length() - 3, out.length(), ";\r\n");
								}
								if (out.length() > 0) {
									_output_stream.write(out.toString().getBytes("UTF-8"));
									out = new StringBuffer();
								}
							}
							catch (Exception e) { if (!reportError(e)) { return; } }
						}
						
					}
					try {
						out.append("-- fim dos dados da tabela ");
						out.append(_table);
						out.append("\r\n");
						_output_stream.write(out.toString().getBytes("UTF-8"));
					}
					catch (Exception e) { if (!reportError(e)) { return; } }					
				}
				
				
				// ------------------------------------------
				// <- Exportação dos índices			   ->
				// ------------------------------------------
				try {
					// -- Índices!
					if (_mode == BackupMethod.FULL || _mode == BackupMethod.STRUCTURE_ONLY) {
						/*
						out.append("-- inicio dos indices da tabela ");
						out.append(_table);
						out.append("\r\n");
						out.append(_connection.getTableIndexes(_table, getBackupDestination()));
						_output_stream.write(out.toString().getBytes("UTF-8"));
						out = new StringBuffer();
						out.append("-- fim dos indices da tabela ");
						out.append(_table);
						out.append("\r\n");
						*/
					}
				}
				catch (Exception e) { if (!reportError(e)) { return; } }
				
				
				
			}
			// -- reinicia o ciclo pra proxima tabela.
			if (_table_list != null && _table_list.size() > 0) {
				_table_list.remove(0);
				executeBackup();
			}
		}
		
		
		public boolean reportError(Exception e) {
			_errors_io += 1;
			e.printStackTrace();
			if (e.getMessage() != null) {
				JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar o <u>backup</u></b>. A aplicação retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + "</font></html>", "Erro ao executar o backup!", JOptionPane.OK_OPTION);
				int option = JOptionPane.showConfirmDialog(null, "<html>Deseja prosseguir com o backup!?<br><i>Haverá boas chances deste backup trazer dados truncados e/ou corrompidos</i>.</html>", "Confirmação", JOptionPane.YES_OPTION);
				if (option == JOptionPane.NO_OPTION) {
					_table_list.clear();
					executeBackup();
					return false;
				}
				try {
					_output_stream.write((";\r\n").getBytes("UTF-8"));
				}
				catch (Exception e1) { e1.printStackTrace(); }
			}
			return true;
		}
		
		
		public Object getData(String col, ResultSet rs, ResultSetMetaData meta) {
			if (meta != null && rs != null && col != null) {
				try {
					SimpleDateFormat df = null;
					switch(getBackupDestination()) {
						case SQLConnectionManager.DB_MYSQL:
						case SQLConnectionManager.DB_POSTGREE:
							df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							break;
							
						case SQLConnectionManager.DB_ORACLE:
							df = new SimpleDateFormat("dd-MM-yyyy");
							break;
							
						case SQLConnectionManager.DB_SQLSERVER:
							df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							break;
					}
					
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						if (meta.getColumnName(i).equalsIgnoreCase(col)) {
							switch (meta.getColumnType(i)) {
								case Types.DATE:
								case Types.TIME:
								case Types.TIMESTAMP:
									return (rs.getString(col) == null ? "NULL" : "'" + (getBackupDestination() != 0 ? df.format(rs.getTimestamp(col)) : rs.getString(col)) + "'");

								case Types.FLOAT:
								case Types.DECIMAL:
								case Types.DOUBLE:
									return (rs.getString(col) == null ? "NULL" : String.valueOf(rs.getDouble(col)));

								case Types.INTEGER:
								case Types.BIGINT:
									return (rs.getString(col) == null ? "NULL" : rs.getString(col));

								case Types.LONGVARCHAR:
								case Types.CLOB:
									switch(getBackupDestination()) {
										case SQLConnectionManager.DB_MYSQL:
										case SQLConnectionManager.DB_POSTGREE:
										case SQLConnectionManager.DB_SQLSERVER:
											return (rs.getString(col) == null ? "NULL" : "'" + getSafeString(rs.getString(col)) + "'");
									}												
									break;
								
								case Types.BLOB:
								case Types.VARBINARY:
								case Types.LONGVARBINARY:
									return (rs.getString(col) == null ? (getBackupDestination() == 1 ? "empty_blob()" : "NULL") : toHex(rs.getBytes(col)));

								default:
									return (rs.getString(col) == null ? "NULL" : "'" + getSafeString(rs.getString(col)) + "'"); 
									
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
	
	
	
	
	
	
	
	
	// -- desativado para testes...
	/*
	public class doBackup2 implements Runnable {
		private String _TABLE;
		public doBackup2 (String table) {
			_TABLE = table;
		}
		public void run() {
			try {
				ResultSet rs = null;
				float perc2 = 0;
				float perc3 = 0;
				
				// -- estrutura da tabela.
				if (_options != null && (_options.getSelectedIndex() == 0 || _options.getSelectedIndex() == 1)) {
					String structure = "";
					_progress.setText("<html>Exportando <u>estrutura</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b></html>");
					switch (getBackupDestination()) {
						case 0:
						case 2:
							structure = "DROP " + (_connection.isTable(_TABLE) ? "TABLE" : "VIEW") + " IF EXISTS `" + _TABLE + "`;\r\n";
							break;
						case 1: // Oracle
							structure = "SET ESCAPE OFF;\r\nSET ESCAPE \\;\r\nDROP TABLE " + _TABLE + ";\r\n";
							break;
						case 3:
							structure = "IF EXISTS (SELECT * FROM sysobjects WHERE type='U' AND name='" + _TABLE + "') DROP TABLE " + _TABLE + ";\r\n";
							break;
					}
					structure += _connection.getDumpStructure(_TABLE, getBackupDestination());
					_output_stream.write(structure.getBytes("UTF-8"));
					//_SELECTED_OUTPUTCHANNEL.write(ByteBuffer.wrap(structure.getBytes("UTF-8")));
				}

				// -- Registros da tabela (DADOS).
				if (_connection.isTable(_TABLE) && _options != null && (_options.getSelectedIndex() == 0 || _options.getSelectedIndex() == 2)){
					_progress.setText("<html>Preparando exportação dos <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b></html>");
					
					// -- contagem de páginas de registros para exportação otimizada.
					rs = _connection.executeQuery("SELECT COUNT(*) FROM " + _TABLE);
					StringBuffer out = null;
					int count = 0;
					int count2 = 0;
					int pages = 0;
					int page_size = 5000;
					int min = 0;
					int max = 0;
					int row = 0;
					int row2 = 0;
					int block_size = 1000;
					boolean control = true;
					boolean header_parse = false;
					int query_length = 0;
					int query_maxlength = 0;
					String item = null;
					//String header = null;
					StringBuffer header = null;
					ResultSetMetaData header_data = null;
					if (rs.next()) {
						count = rs.getInt(1);
						System.out.println("*** " + count + " Registros encontrados na tabela: " + _TABLE);
					}
					page_size = Math.max(page_size, (int)(count * (1.f / 100)));
					// -- sem utilizar a paginação o total de paginas sera 1.
					pages = (_options3.isSelected() ? 0 : (int)(count / page_size)) + 1;
					rs.close();

					StringBuffer large_objects = new StringBuffer();
					
					for (int j = 1; count > 0 && j <= pages; j++) {
						min = max;
						max = Math.min(j * page_size, count);
						
						// -- preparacao dos dados para exportação com base nas páginas definidas anteriormente.
						_text4.setText("<html><i>Progresso</i> do backup: <font color='#000000'>Preparando registros para exportação... <i><b>" + j + "/" + pages + "</b></i></font></html>");
						_progress.setText("<html>Exportando <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b> <i>" + (int)perc2 + "%</i></html>");
						_progress.setItemProgress(perc2);
						rs = _connection.getDumpData(_TABLE, getBackupDestination(), (_options3.isSelected() ? 0 : page_size), min);
						if (rs == null) {
							System.out.println("--- Falha ao obter os dados da tabela: " + _TABLE);
							continue;
						}
						// ---> controle de encerramento dos comandos!
						switch (_options2.getSelectedIndex()) {
							case 0: // mysql
							case 2: // postgre
								block_size = 1000;
								rs.last();
								count2 = rs.getRow();
								rs.beforeFirst();
								break;
							case 1: // oracle
							case 3: // sql server
								block_size = 1;
								rs.last();
								count2 = rs.getRow();
								rs.beforeFirst();
								break;
						}
						row2 = 0;
						// --
						
						// -- insert header
						//header = null;
						header = new StringBuffer();
						switch(_options2.getSelectedIndex()) {
							case 0: // mysql
							case 1: // oracle
							case 2: // postgre
							case 3: // sql server
								//header = "INSERT INTO " + _TABLE + " (";
								header.append("INSERT INTO ");
								header.append(_TABLE);
								header.append(" (");
								break;
						}
						header_data = rs.getMetaData();
						for (int a = 1; a <= header_data.getColumnCount(); a++) {
							if (header_data.getColumnName(a) != null) {
								switch(getBackupDestination()) {
									case 0:
										//header += "`" + header_data.getColumnName(a) + "`"; // mysql
										header.append("`");
										header.append(header_data.getColumnName(a));
										header.append("`");
										
										break;
									case 1:
									case 2:
										//header += header_data.getColumnName(a); // sql server.
										header.append(header_data.getColumnName(a));
										break;
								}
								if ((a) != header_data.getColumnCount()) {
									//header += ",";
									header.append(",");
								}
							}
						}
						//header += ")";
						header.append(")");
						
						SimpleDateFormat DateFormat = null;
						switch(getBackupDestination()) {
							case 0:
							case 2:
								DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								break;
							case 1:
								DateFormat = new SimpleDateFormat("dd-MM-yyyy");
								break;
							case 3:
								DateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
									switch(getBackupDestination()) {
										case 0: // mysql
										case 2: // postgre
										case 3: // sql server
											out.append(header + " VALUES \r\n");
											break;
										case 1: // oracle
											out.append(header + " VALUES ");
									}
									header_parse = true;
								}
								
								_paint_time = (_paint_time == 0 ? System.nanoTime() : _paint_time);
								perc2 = (((row * 1.f) / count) * 100.f);
								perc3 = ((_registers_processed * 1.f) / _registers_total) * 100.f;
								if (_paint_time + (40000000) <= System.nanoTime() || perc3 == 100) {
									_text4.setText("<html><i>Progresso</i> do backup: <font color='#000000'><i><b>" + ( Math.round(perc3 * Math.pow(10,1) ) / Math.pow(10, 1) ) + "%</b></i></font></html>");
									_progress.setMainProgress(perc3);
									_progress.setItemProgress(perc2);
									_progress.setText("<html>Exportando <u>registros</u> da tabela: <b>" + _TABLE.toLowerCase() + "</b> <i>" + (int)perc2 +"%</i></html>");
									_paint_time = System.nanoTime();
								}
								out.append("(");
								query_maxlength = Math.max(query_maxlength, query_length);
								query_length = out.length() + 2;
								for (int b = 1; b <= header_data.getColumnCount(); b++) {
									switch(header_data.getColumnType(b)) {
										case Types.DATE:
										case Types.TIME:
										case Types.TIMESTAMP:
											item = (rs.getString(b) == null ? "NULL" : "'" + (getBackupDestination() != 0 ? DateFormat.format(rs.getTimestamp(b)) : rs.getString(b)) + "'");
											break;
										case Types.FLOAT:
										case Types.DECIMAL:
										case Types.DOUBLE:
											item = (rs.getString(b) == null ? "NULL" : String.valueOf(rs.getDouble(b)));
											break;
										case Types.INTEGER:
										case Types.BIGINT:
											item = (rs.getString(b) == null ? "NULL" : rs.getString(b));
											break;
										case Types.LONGVARCHAR:
										case Types.CLOB:
											switch(getBackupDestination()) {
												case 0: // mysql
												case 2: // postgre
												case 3: // sql server
													item = (rs.getString(b) == null ? "NULL" : "'" + getSafeString(rs.getString(b)) + "'");
													break;
											}												
											break;
											
										case Types.BLOB:
										case Types.VARBINARY:
										case Types.LONGVARBINARY:
											item = (rs.getString(b) == null ? (getBackupDestination() == 1 ? "empty_blob()" : "NULL") : toHex(rs.getBytes(b)));
											break;
										default:
											item = (rs.getString(b) == null ? "NULL" : "'" + getSafeString(rs.getString(b)) + "'"); 
									}
	
									query_length += item.length();
									out.append(item);
									if ((b) != header_data.getColumnCount()) {
										out.append(","); 
									}
								}
								out.append(")");
								if ((block_size > 1 && (i + 1) == block_size || block_size == 1) || count2 == row2 || (out.length() + query_maxlength) >= 1048576) {
									switch (getBackupDestination()) {
										case 0: // mysql
										case 1: // oracle
										case 2: // postgree
										case 3: // sql server
											out.append(";\r\n");
											if (large_objects.length() > 0) {
												out.append(large_objects.toString());
												large_objects = new StringBuffer();
											}
											break;									}
									if (control == true && count2 == row2) {
										control = false;
									}
									header_parse = false;
									break;
								}
								else {
									switch (getBackupDestination()) {
										case 0: // mysql
										case 1: // oracle!?
										case 2: // postgree
										case 3: // sql server
											out.append(",\r\n");
											break;
									}
								}
							}
							if (_thread == null) {
								return;
							}
							_output_stream.write(out.toString().getBytes("UTF-8"));
							//_SELECTED_OUTPUTCHANNEL.write(ByteBuffer.wrap(out.toString().getBytes("UTF-8")));
							if (control && rs.getRow() == 0) {
								control = false; 
							}							
						}
						rs.close();
					}
				}
			
			}
			catch (Exception e) {
				_errors_io += 1;
				e.printStackTrace();
				if (e.getMessage() != null) {
					JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar o <u>backup</u></b>. A aplicação retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + "</font></html>", "Erro ao executar o backup!", JOptionPane.OK_OPTION);
					int option = JOptionPane.showConfirmDialog(null, "<html>Deseja prosseguir com o backup!?<br><i>Haverá boas chances deste backup trazer dados truncados e/ou corrompidos</i>.</html>", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.NO_OPTION) {
						return;
					}
					try {
						_output_stream.write((";\r\n").getBytes("UTF-8"));
						//_SELECTED_OUTPUTCHANNEL.write(ByteBuffer.wrap((";\r\n").getBytes())); 
					}
					catch (Exception e1) { }
				}
			}
			if (_table_list != null && _table_list.size() > 0) {
				_table_list.remove(0);
				executeBackup();
			}
		
		}
	}
	 */
	
	private class doCompact implements Runnable {
		private int TAMANHO_BUFFER = 64 * 1024;
		private File origem;
		private File destino;
		
		public doCompact(String a, String b) {
			origem = new File(a);
			destino = new File(b);
		}
		public void run() {
			int cnt;
			long count = 0;
			long size;
			
			byte[] dados = new byte[TAMANHO_BUFFER];
			                   
			BufferedInputStream origem = null;
			FileInputStream streamDeEntrada = null;
			FileOutputStream destino = null;
			ZipOutputStream saida = null;
			ZipEntry entry = null;
			String text = null;
			
			try {
				
				size = this.origem.length();
				
				destino = new FileOutputStream(this.destino);
				saida = new ZipOutputStream(new BufferedOutputStream(destino));
				streamDeEntrada = new FileInputStream(this.origem);
				origem = new BufferedInputStream(streamDeEntrada, TAMANHO_BUFFER);
				entry = new ZipEntry(this.origem.getName());
				saida.setLevel(9);
				saida.putNextEntry(entry);
				_progress.setItemProgress(0f);
				while((cnt = origem.read(dados, 0, TAMANHO_BUFFER)) != -1) {
					saida.write(dados, 0, cnt);
					count += cnt;
					_perc_a = ((count * 100f) / size);
					text = "<html><i>Compactando</i> o backup: <font color='#000000'><i><b>" + Util.toPercent(_perc_a) + "</b></i></font></html>";
					_progress.updateProgress(text, _perc_a, 0f);
				}
				origem.close();
				saida.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			toogleActions(true);
			_progress.setText("<html>" + (_errors_io + _errors_io == 0 ? "<font color='black'>Backup fianlziado com <b>sucesso</b>!" : "<font color='#990000'>Backup finalizado com: " + (_errors_io + _errors_other) + " ERROS!</b></font>") + "</b></font></html>");
			JOptionPane.showMessageDialog(null, (_errors_io + _errors_io == 0 ? "Backup Concluido com sucesso!" : "Backup concluído com: " + (_errors_io + _errors_other) + " erros!\n\nATENÇÃO: ESTE BACKUP NÃO É CONFIÁVEL!!!"), "Aviso!", JOptionPane.OK_OPTION);
		}
		
	}
	

	private String _especial_chars = "%$&\\";
	
	public String getSafeString(String text) {
		switch (getBackupDestination()) {
			case SQLConnectionManager.DB_MYSQL:
			case SQLConnectionManager.DB_POSTGREE:
				text = text.replace("\\","\\" + "\\");
				text = text.replace("\'","''");
				break;
			case SQLConnectionManager.DB_MSSQL:
				text = text.replace("'","''");
				break;
			case SQLConnectionManager.DB_ORACLE:
				// -- caractere padrão de escape: !
				for (char escape : _especial_chars.toCharArray()) {
					text = text.replace("" + escape, "\\" + escape);
				}
				break;
		}
		return text;
	}
	
	public static String toHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return "0x" + new String(hexChars);
	}
	
	public void reconnect(String driver, String con, String user, String pass) {
		_connection = new SQLConnectionManager(driver, con, user, pass);
	}
	
	public void disconnect() {
		if (_connection != null && _connection.isConnected()) {
			_connection.closeConnection();
			_connection = null;
		}
	}
	
	
}
