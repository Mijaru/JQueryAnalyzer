package Components.Programs.SQLServer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import Components.Programs.DatabaseTreeCellRender;
import Components.Programs.JCheckTreeNode;
import Components.Programs.TableTreeCellRender;

public class ChangeTableOwner {
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
		private int _errors_io = 0;
		@SuppressWarnings("unused")
		private int _errors_other = 0;
		private JTree _database;
		private Font _default_font = new Font("Tahoma", Font.ROMAN_BASELINE, 11);
		
		private	ImageIcon check;
		private ImageIcon uncheck;

		private ImageIcon check_16;
		private ImageIcon uncheck_16;
		private JComboBox<String> _old;
		private JComboBox<String> _new;

		
		public ChangeTableOwner() {
			check_16 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
			check_16.setImage(check_16.getImage().getScaledInstance(16, 16, 100));
			uncheck_16 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
			uncheck_16.setImage(uncheck_16.getImage().getScaledInstance(16, 16, 100));
			check = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
			check.setImage(check.getImage().getScaledInstance(24, 24, 100));
			uncheck = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
			uncheck.setImage(uncheck.getImage().getScaledInstance(24, 24, 100));

			_DIALOG = new JFrame();
			_DIALOG.setTitle("JQuery Analizer - Assistente para substituição de owner das tabelas. [SQL Server]");
			_DIALOG.setMaximumSize(new Dimension(500,505));
			_DIALOG.setMinimumSize(new Dimension(500,505));
			_DIALOG.setPreferredSize(new Dimension(500,505));
			_DIALOG.setLocationRelativeTo(null);
			_DIALOG.setResizable(false);
			_DIALOG.setLayout(null);
			_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			_DIALOG.setIconImage(new ImageIcon(ClassLoader.getSystemResource("data_hammer.png")).getImage());

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
			_database.setRowHeight(18);
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
						Thread t = new Thread(new selectDatabase(_database.getSelectionPath().toString()));
						t.start();
					} 
				}
				public void mouseReleased(MouseEvent event) { }				
			});

			JLabel text3 = new JLabel("<html>Selecione as <b>tabelas</b> a serem reparadas:</html>");
			text3.setFont(_default_font);
			text3.setForeground(Color.DARK_GRAY);
			text3.setBounds(230,5,475,20);
			_DIALOG.add(text3);
			
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
			scrolls.setBounds(230,25,255,274);
			scrolls.setAutoscrolls(true);
			_DIALOG.add(scrolls);
			
			
			// -- -------------
			JPanel a1 = new JPanel();
			a1.setBounds(7,305,480,70);
			a1.setOpaque(true);
			//a1.setBackground(Color.GRAY);
			a1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), " ",SwingConstants.CENTER, SwingConstants.LEFT));
			a1.setLayout(null);
			_DIALOG.add(a1);
			
			JLabel l1 = new JLabel("<html><center><b>Opções</b> de proprietário:</center></html>");
			l1.setBounds(10,0,125,15);
			l1.setHorizontalAlignment(JLabel.CENTER);
			l1.setFont(_default_font);
			l1.setOpaque(true);
			l1.setForeground(Color.DARK_GRAY);
			a1.add(l1);
			
			JLabel t1 = new JLabel("Proprietário atual: ");
			t1.setBounds(10,20,100,15);
			t1.setFont(_default_font);
			t1.setForeground(Color.DARK_GRAY);
			a1.add(t1);
			
			_old = new JComboBox<String>();
			_old.setBounds(10,35,225,23);
			_old.setFont(_default_font);
			_old.setEditable(true);
			a1.add(_old);
			
			JLabel t2 = new JLabel("Novo proprietário: ");
			t2.setBounds(245,20,100,15);
			t2.setFont(_default_font);
			t2.setForeground(Color.DARK_GRAY);
			a1.add(t2);
			
			_new = new JComboBox<String>();
			_new.setBounds(245,35,225,23);
			_new.setFont(_default_font);
			_new.setEditable(true);
			a1.add(_new);
			
		
			_progress = new JProgressLabel();
			_progress.setPanelBounds(8,379,477,35);
			_progress.setText("<html>Selecione o <b>Banco de Dados</b></html>");
			_DIALOG.add(_progress);	
			
			
				
			
			_run = new JButton("<html><u>I</u>niciar Substituição</html>");
			_run.setFont(_default_font);
			_run.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					toogleActions(false);
					TreeModel model = _list.getModel();
					int option;
					_TABLE_LIST = new FastList<String>();
					String table = null;
					boolean tag = false;
					for (int i = 0; i < model.getChildCount(model.getRoot()); i++) {
						if (model.getChild(model.getRoot(), i) instanceof DefaultMutableTreeNode) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(model.getRoot(), i);
							for (int j = 0; j < node.getChildCount(); j++) {
								if (node.getChildAt(j) instanceof JCheckTreeNode) {
									JCheckTreeNode row = (JCheckTreeNode)node.getChildAt(j);
									if (row.isSelected()) {
										table = "";
										for (int k = 0; k < row.toString().length(); k++) {
											if (row.toString().charAt(k) == '<') { tag = true; }
											else if (row.toString().charAt(k) == '>') { tag = false; continue; }
											if (!tag) { table += row.toString().charAt(k); }
										}
										_TABLE_LIST.add(table); 
									}
								}
							}
							option = JOptionPane.showConfirmDialog(null, "Foram selecionadas: " + (_TABLE_LIST.size()) + "/" + (node.getChildCount()) + " tabelas para realizar o backup, existem: " + (node.getChildCount() - _TABLE_LIST.size()) + " tabelas desmarcadas.\nDeseja prosseguir com o backup?" , "Confirmação", JOptionPane.YES_OPTION);
							if (option == JOptionPane.YES_OPTION) {
								_TABLE_LIST_SIZE = _TABLE_LIST.size();
								_errors_io = 0;
								_errors_other = 0;
								executeChange();
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
			_run.setBounds(255,427,150,37);
			_run.setEnabled(false);
			_DIALOG.add(_run);
			
			_close = new JButton("<html><u>S</u>air</html>");
			_close.setMnemonic(KeyEvent.VK_S);
			_close.setFont(_default_font);
			_close.setBounds(410,427,75,37);
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
		
		public void show() {
			if (_DIALOG != null && !_DIALOG.isVisible()) {
				_DIALOG.setVisible(true);
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
				try {
					JQueryPane panel = MainWindow.getQueryPaneByHost(path[1]); 
					host = panel.getParameters().getHost();
				}
				catch (Exception e) { }
				location = (host != null ? host : "") + "/<u>"+path[2]+"</u>";
				_progress.setText("<html><font color='blue'>Aguardando conexão com o servidor <b>" + location + "</b></font></html>");
				_progress.setItemProgress(100.f);
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
					_progress.setText("<html>Conectado a database <font color='green'><b>" + location + "</b></font></html>");
					try {
						ResultSet rs = _CONNECTION.executeQuery("SELECT name FROM sysusers");
						_new.removeAllItems();
						_old.removeAllItems();
						while (rs != null && rs.next()) {
							_new.addItem(rs.getString(1));
							_old.addItem(rs.getString(1));
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
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
		
		public void executeChange() {
			if (_TABLE_LIST != null) {
				System.gc();
			}
			if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
				String[] table = _TABLE_LIST.get(0).split(":");
						
				doChange repair = new doChange(table[1], table[0]);
				_thread = new Thread(repair);
				_thread.start();
			}
			else {
				JOptionPane.showMessageDialog(null, "<html>O processo de <b>substituição de owner</b> das tabelas foi concluido." + (_errors_io > 0 ? " Foram encontrados: " + _errors_io + " erros durante o processo" : "") + "</html>", "JQueryAnalizer - Conclusão!", JOptionPane.OK_OPTION);
				getTableList(_SELECTED_DATABASE);
				_progress.setMainProgress(0f);
				_progress.setItemProgress(0f);
				toogleActions(true);
			}
		}
		
		public void toogleActions(boolean state) {
			_new.setEnabled(state);
			_old.setEnabled(state);
			_list.setEnabled(state);
			_run.setEnabled(state);
			_close.setEnabled(state);
			_database.setEnabled(state);
		}
		
		public void stopChange() {
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
				String database = null;
				DefaultMutableTreeNode root = new DefaultMutableTreeNode("Conexões disponíveis");
				DefaultTreeModel model = (DefaultTreeModel)_database.getModel();
				String host = null;
				for (int i = 0; i < tabs.getComponentCount(); i++) {
					c = tabs.getComponent(i);
					if (c != null && c instanceof JQueryPane) {
						pane = (JQueryPane)c;
						con = pane.getConnection();
						if (con == null || con.getServerType() != SQLConnectionManager.DB_MSSQL) {
							continue;
						}
						host = pane.getParameters().getHost().split(":")[0]; 
						DefaultMutableTreeNode child = new DefaultMutableTreeNode(host != null ? host : "?");
						root.add(child);
						if (con != null && con.isConnected()) {
							try {
								String query = null;
								switch (con.getVersion()) {
									case "MS2000":
//										query = "SELECT suser_sname(owner_sid), name FROM sys.databases";
//										break;
									case "MS2005":
									case "MS2008":
									case "MS2012":
									case "MS2014":
										query = "SELECT suser_sname(sid), name FROM master.dbo.sysdatabases";
										break;
									default:
										_DIALOG.dispose();
										JOptionPane.showMessageDialog(null, "Este recurso não é compatível com a versão do SQL Server que você está usando: " + con.getVersion(), "JQueryAnalizer - Aviso", JOptionPane.WARNING_MESSAGE);
										return;
								}
								rs = con.executeQuery(query);
								while (rs != null && rs.next()) {
									database = rs.getString(2);
									database = "<i><font color='#777777'>" + rs.getString(1) + "</font></i><font color='#FFFFFF'>:</font>" + (database != null ? database : "?");
									child.add(new DefaultMutableTreeNode("<html>" + database + "</html>"));
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
					JOptionPane.showMessageDialog(null, "<html>Este utilitário foi projetado para trabalhar apenas com<br>bases Microsoft SQL Server. Mas no momento <b>não há nenhuma conexão com SQL Server</b> disponível.</html>", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
					_DIALOG.dispose();
				}
			}
		}
		
		public void getTableList(String database) {
			if (database == null) {
				((DefaultTreeModel)_list.getModel()).setRoot(new DefaultMutableTreeNode("Selecione uma database!"));
				return;
			}
			String db = database.replace("<html>", "");
			db = db.replace("</html>", "");
			db = db.replace("<i>", "");
			db = db.replace("</i>", "");
			db = db.replace("<font", "");
			db = db.replace("color='#777777'>", "");
			db = db.replace("color='#FFFFFF'>", "");
			db = db.replace("</font>", "");
			db = db.split(":")[1];
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(_SELECTED_HOST == null ? "Você está desconectado do banco de dados, selecione a base de dados novamente." : _SELECTED_HOST);
			root.add(new DefaultMutableTreeNode(database == null ? "Você está desconectado ou não há database selecionada!" : db));
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
					_CONNECTION.executeUpdate("USE " + (_CONNECTION.getServerType() == 0 ? "`" + db.trim() + "`" : db.trim()));
					int size = 0;
					String table = null;
					String owner = null;
					ResultSet rs = _CONNECTION.executeQuery("sp_tables");
					while (rs != null && rs.next()) {
						if (rs.getString(4).equalsIgnoreCase("TABLE")) {
							++size;
							owner = rs.getString(2);
							table = rs.getString(3);
							table = "<i><font color='#777777'>" + (owner != null ? owner : "?") + "</font></i><font color='#FFFFFF'>:</font>" + table;
							row.add(new JCheckTreeNode("<html>" + table + "</html>"));
						}
					}
					if (size > 0) {
						_run.setEnabled(true);
					}
					else {
						_run.setEnabled(false);
					}
					model.setRoot((DefaultMutableTreeNode)model.getRoot());
				}
				catch (Exception e) { _errors_other += 1; e.printStackTrace(); }
				_list.revalidate();
				_list.repaint();
				MainWindow.expandAll(_list, true);
			}
		}
		
		
		
		
		public class doChange implements Runnable {
			private String _TABLE;
			private String _OWNER;
			public doChange (String table, String owner) {
				_TABLE = table;
				_OWNER = owner;
			}
			@Override
			public void run() {
				try {
					if (_CONNECTION != null && _CONNECTION.isConnected()) {
						float perc = (((_TABLE_LIST_SIZE - (_TABLE_LIST.size() - 1.f)) * 100.f) / _TABLE_LIST_SIZE);
						_progress.setText("<html>Andamento da substituição: <b>" + ((int)perc) + "%</b> concluido! <u>" + _TABLE + "</u></html>");
						_progress.setItemProgress(50.f);
						_progress.setMainProgress(perc);
						Exception e1 = null;
						switch (_CONNECTION.getVersion()) {
							case "MS2000":
								e1 = _CONNECTION.executeUpdate("SP_CHANGEOBJECTOWNER '" + _old.getSelectedItem().toString() + "." + _TABLE + "','" + _new.getSelectedItem().toString() + "'");
								break;
							case "MS2005":
							case "MS2008":
							case "MS2012":
							case "MS2014":
								e1 = _CONNECTION.executeUpdate(("ALTER SCHEMA [NEW] TRANSFER [OLD].[TABLE]").replace("[NEW]", _new.getSelectedItem().toString()).replace("[OLD]", _OWNER).replace("[TABLE]", _TABLE));
								break;
							default:
								_DIALOG.dispose();
								JOptionPane.showMessageDialog(null, "Este recurso não é compatível com a versão do SQL Server que você está usando: " + _CONNECTION.getVersion(), "JQuery Analizer - Aviso", JOptionPane.WARNING_MESSAGE);
								return;
						}
						_progress.setItemProgress(100.f);
						if (e1 != null) {
							JOptionPane.showMessageDialog(null, "Erro ao executar a substituição do proprietário da tabela: " + _TABLE + ".\nErro: " + e1.getMessage(), "JQuery Analizer - Erro", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				catch (Exception e) {
					_errors_io += 1;
					e.printStackTrace();
					if (e.getMessage() != null) {
						JOptionPane.showMessageDialog(null, "<html><b>Houve um erro ao executar a substituição do owner da tabela: " + _TABLE + "<br><font color='red'>" + e.getMessage() + "</font></html>", "jQueryAnalizer - Erro!", JOptionPane.OK_OPTION);
					}
				}
				if (_TABLE_LIST != null && _TABLE_LIST.size() > 0) {
					_TABLE_LIST.remove(0);
					executeChange();
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
