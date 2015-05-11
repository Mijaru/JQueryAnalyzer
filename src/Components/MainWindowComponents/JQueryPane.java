package Components.MainWindowComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import javolution.util.FastList;

import java.sql.Connection;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JHistory.Type;
import Components.MainWindowComponents.Highlighter.GroovyFilter;
import Components.MainWindowComponents.Highlighter.StyleManager;
import Components.Programs.JCheckTreeNode;

public class JQueryPane extends JTabPanel  {
		private static final long serialVersionUID = 1207142220113990255L;
		private SQLConnectionManager _CONNECTION;
		private JParametersPanel _PARAMETERS;
		private String[]   _QUERY_COLUMN_NAMES;
		private Object[][] _QUERY_DATA;
		
		// ------------------------------------------------------
		private static List<JHistory> _history = new FastList<JHistory>();
		private int _current_history;
		private int _current_page;
		// ------------------------------------------------------
		private JTree _list_c1;
		private JTextPane _text_c2;
		private JTable _table_d;
		private JLabel _cel_b;
		private JPanel _cel_a;
		private JCheckButton _bt_script_edit;
		private JLabel _label_c1;
		private JLabel _label_c2;
		private long _paint_time;
		private Queue<String> _status_log = new ConcurrentLinkedQueue<String>();
		private Thread _status_thread;
		
		private DefaultTableCellRenderer _default_renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6234287599035670995L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean pass;
				if (isShowing()) { return this; }
				for (int i = table.getRowCount();i > row; i--) {
					pass = true;
					setVerticalAlignment(JLabel.TOP);
					setDoubleBuffered(true);
					setBorder(new RendererBorder());
					for (int j = 0; j < table.getSelectedRows().length; j++) {
						if (row == table.getSelectedRows()[j]) {
							setBackground(Color.DARK_GRAY);
							pass = false;
						}
					}
					if (!pass) {
						continue; 
					}
					if ((row % 2) == 0) {
						setBackground(new Color(220,220,220));
					}
					else {
						setBackground(new Color(240,240,240)); 
					}
					setFont(_table_d.getFont());
				}
				setUI(new MultiLineLabelUI());
				return this;
			}
		};
		
		private DefaultTableCellRenderer _datetime_renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6234287599035670995L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean pass;
				if (isShowing()) { return this; }
				for (int i = table.getRowCount();i > row; i--) {
					pass = true;
					setVerticalAlignment(JLabel.TOP);
					setHorizontalAlignment(JLabel.CENTER);
					setDoubleBuffered(true);
					setBorder(new RendererBorder());
					for (int j = 0; j < table.getSelectedRows().length; j++) {
						if (row == table.getSelectedRows()[j]) {
							setBackground(Color.DARK_GRAY);
							pass = false;
						}
					}
					if (!pass) {
						continue; 
					}
					if ((row % 2) == 0) {
						setBackground(new Color(220,220,220));
					}
					else {
						setBackground(new Color(240,240,240)); 
					}
					setFont(_table_d.getFont());
				}
				setUI(new MultiLineLabelUI());
				return this;
			}
		};
		
		private DefaultTableCellRenderer _number_renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 7386737135957494657L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean pass;
				
				if (isShowing()) { return this; }
				for (int i = table.getRowCount();i > row; i--) {
					pass = true;
					setVerticalAlignment(JLabel.TOP);
					setHorizontalAlignment(JLabel.RIGHT);
					setDoubleBuffered(true);
					setBorder(new RendererBorder());
					for (int j = 0; j < table.getSelectedRows().length; j++) {
						if (row == table.getSelectedRows()[j]) {
							setBackground(Color.DARK_GRAY);
							pass = false;
						}
					}
					if (!pass) {
						continue; 
					}
					if ((row % 2) == 0) {
						setBackground(new Color(220,220,220));
					}
					else {
						setBackground(new Color(240,240,240)); 
					}
					setFont(_table_d.getFont());
				}
				setUI(new MultiLineLabelUI());
				return this;
			}
		};
	
		private class RendererBorder extends AbstractBorder {
			private static final long serialVersionUID = -1685979758391397721L;
			public Insets getBorderInsets(Component c, Insets insets){
                 return new Insets(insets.top + 3, insets.left + 3, insets.bottom + 3, insets.right + 3);
			 }
		}
		
		
		public JQueryPane() {
			super();
			
			this.setLayout(new GridBagLayout());
			this.setOpaque(true);
			
			GridBagConstraints main_table_def = new GridBagConstraints();
			main_table_def.fill = GridBagConstraints.BOTH;
			main_table_def.insets = new Insets(5,5,0,0);
			main_table_def.gridx = 1;
			main_table_def.gridy = 1;

			// |---|---|
			// | A | B |
			// |---|---|    |----|--------|
			// |   C   | -> | c1 |   c2   |
			// |-------|    |----|--------|
			// |   D   |
			// |-------|
			// -menu de navegação- celula A
			// --------------------------------------------------------------------------------------------------------------------
			_cel_a = new JPanel();
			_cel_a.setOpaque(true);
			_cel_a.setBackground(new Color(200,200,200));
			this.add(_cel_a, main_table_def);

			JCheckButton bt_first      = new JCheckButton(ClassLoader.getSystemResource("first.png"), new Dimension(36, 36),"bt_page_first");
			bt_first.setToolTipText("Primeira página de registros.");
	        JCheckButton bt_back       = new JCheckButton(ClassLoader.getSystemResource("back.png"), new Dimension(36, 36),"bt_page_back");
	        bt_back.setToolTipText("Página anterior.");
	        JCheckButton bt_next       = new JCheckButton(ClassLoader.getSystemResource("next.png"), new Dimension(36, 36),"bt_page_next");
	        bt_next.setToolTipText("Próxima página.");
	        JCheckButton bt_last       = new JCheckButton(ClassLoader.getSystemResource("last.png"), new Dimension(36, 36),"bt_page_last");
	        bt_last.setToolTipText("Última página de reigstros.");
	        JCheckButton bt_script_save = new JCheckButton(ClassLoader.getSystemResource("script_save.png"), new Dimension(36, 36),"bt_script_save");
	        bt_script_save.setToolTipText("Salvar histórico de scripts.");
			JCheckButton bt_undo       = new JCheckButton(ClassLoader.getSystemResource("undo.png"), new Dimension(36, 36),"bt_history_back");
			bt_undo.setToolTipText("Script SQL anterior.");
	        JCheckButton bt_rendo      = new JCheckButton(ClassLoader.getSystemResource("redo.png"), new Dimension(36, 36),"bt_history_next");
	        bt_rendo.setToolTipText("Próximo script SQL.");
	        _bt_script_edit = new JCheckButton(ClassLoader.getSystemResource("script_editor.png"), new Dimension(36, 36),"bt_script_editor");
	        _bt_script_edit.setToolTipText("Ativa/Desativa a identificação das palavras chave nos scripts.");
	        _bt_script_edit.setSelected(true);
	        
	        ActionListener script_editor_listener = new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					//toggleEditor();
					updateQueryLabel();
				}
	        };
	        
	        _bt_script_edit.addActionListener(script_editor_listener);
	        bt_undo.addActionListener(script_editor_listener);
	        bt_rendo.addActionListener(script_editor_listener);
	        
	        JCheckButton bt_script_run = new JCheckButton(ClassLoader.getSystemResource("script_start.png"), new Dimension(36, 36),"bt_script_run");
	        bt_script_run.setToolTipText("Executar o script atual.");
	        JCheckButton bt_table_search = new JCheckButton(ClassLoader.getSystemResource("table_search.png"), new Dimension(36, 36),"bt_query_search");
	        bt_table_search.setToolTipText("Pesquisar por termo dentro dos registros atuais.");
	        JCheckButton bt_table_save = new JCheckButton(ClassLoader.getSystemResource("table_save.png"), new Dimension(36, 36),"bt_query_save");
	        bt_table_save.setToolTipText("Salvar a página de registros listados atualmente.");
	        JCheckButton bt_table_fill = new JCheckButton(ClassLoader.getSystemResource("table_adjust.png"), new Dimension(36, 36),"bt_page_adjust");
	        bt_table_fill.setToolTipText("Ajustar as dimensões das células dos registros atualmente listados.");

	        _cel_a.add(bt_first);
	        _cel_a.add(bt_back);
	        _cel_a.add(bt_next);
	        _cel_a.add(bt_last);
	        _cel_a.add(new JBraketButton(32));
	        _cel_a.add(bt_script_save);
	        _cel_a.add(_bt_script_edit);
	        _cel_a.add(bt_script_run);
	        _cel_a.add(bt_table_search);
	        _cel_a.add(bt_table_fill);
	        _cel_a.add(bt_table_save);
	        _cel_a.add(new JBraketButton(32));
	        _cel_a.add(bt_undo);
	        _cel_a.add(bt_rendo);
	        _cel_a.add(new JBraketButton(32));
	        _cel_a.setBorder(MainWindow.border_left);
	        
			// -menu de navegação- celula B
			// --------------------------------------------------------------------------------------------------------------------
	        main_table_def.insets = new Insets(5,0,0,5);
	        main_table_def.gridx = 2;
	        main_table_def.gridy = 1;
	        main_table_def.weightx = 1;
			
			_cel_b = new JLabel("<html><b>Histórico da Query</b></html>");
			_cel_b.setToolTipText("teste");
			_cel_b.setOpaque(true);
			_cel_b.setBackground(new Color(200,200,200));
			_cel_b.setFont(new Font("Tahoma", Font.PLAIN, 10));
			_cel_b.setBorder(MainWindow.border_right);
			_cel_b.setMaximumSize(new Dimension(100, 20));
			this.add(_cel_b, main_table_def);
			
			
			// #celulas C
			// --------------------------------------------------------------------------------------------------------------------
	        main_table_def.fill = GridBagConstraints.BOTH;
			main_table_def.insets = new Insets(5,5,5,5);
			main_table_def.gridx = 1;
			main_table_def.gridy = 2;
			main_table_def.weightx = 1;
			main_table_def.weighty = 0.001;
			main_table_def.gridwidth = 2;
			
			JPanel cel_c = new JPanel();
			cel_c.setLayout(new GridBagLayout()); // <- grid C


			/* -#Celulas C1 e C2#- */
			GridBagConstraints cel_c_def = new GridBagConstraints();
			cel_c_def.fill = GridBagConstraints.BOTH;
			cel_c_def.gridx = 1;
			cel_c_def.gridy = 1;
			
			

			JPanel cel_c1 = new JPanel();          // celula C1 - seleção de databases do servidor
			cel_c1.setLayout(new GridBagLayout()); // <- grid C1
			
			if (cel_c1 != null) {
				GridBagConstraints in_c1_def = new GridBagConstraints();
				in_c1_def.fill = GridBagConstraints.BOTH;
				in_c1_def.insets = new Insets(0,0,0,0);
				in_c1_def.gridx = 1;
				in_c1_def.gridy = 1;
				in_c1_def.weightx = 1;

				ImageIcon icon_c1 = new ImageIcon(ClassLoader.getSystemResource("data_table.png"));
				icon_c1.setImage(icon_c1.getImage().getScaledInstance(32, 32, Image.SCALE_AREA_AVERAGING));
				_label_c1 = new JLabel("<html>Bases de dados disponíveis:<br>&nbsp;</html>");
				_label_c1.setFont(new Font("Verdana",Font.ROMAN_BASELINE, 12));
				_label_c1.setForeground(Color.DARK_GRAY);
				_label_c1.setVerticalTextPosition(SwingConstants.BOTTOM);
				_label_c1.setSize(new Dimension(150,36));
				_label_c1.setPreferredSize(new Dimension(150,36));
				_label_c1.setIcon(icon_c1);
				cel_c1.add(_label_c1, in_c1_def);
			
				in_c1_def.insets = new Insets(0,0,0,0);
				in_c1_def.gridx = 1;
				in_c1_def.gridy = 2;
				in_c1_def.weightx = 1.d;
				in_c1_def.weighty = 1.d;
			
				_list_c1 = new JTree(new DefaultMutableTreeNode("Desconectado"));
				_list_c1.getRootPane();
				_list_c1.setRowHeight(18);
				
				_list_c1.setCellRenderer(new TreeCellRenderer(){
					private JLabel root = new JLabel();
					private ImageIcon icon;
					
					public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,	boolean leaf, int row, boolean hasFocus) {
						if (value == null) return null;
						String item = value.toString();
						JCheckTreeNode node = null;
						TreeModel model = tree.getModel();
						
						icon = new ImageIcon(ClassLoader.getSystemResource("server.png"));
						if (value instanceof JCheckTreeNode) {
							node = (JCheckTreeNode)value;
							switch (node.getType()) {
								case JCheckTreeNode.DATABASE:
									icon = new ImageIcon(ClassLoader.getSystemResource("data.png"));
									break;
								case JCheckTreeNode.TABLE:
									icon = new ImageIcon(ClassLoader.getSystemResource("table.png"));
									break;
								case JCheckTreeNode.VIEW:
									icon = new ImageIcon(ClassLoader.getSystemResource("view.png"));
									break;
								case JCheckTreeNode.UNKNOW:
									icon = new ImageIcon(ClassLoader.getSystemResource("unchecked_16.png"));
									break;
							}
						}
						
						DefaultMutableTreeNode r = (DefaultMutableTreeNode)model.getRoot();
						boolean isRoot = (item != null && item.trim().equalsIgnoreCase(r.toString().trim()));

						root = new JLabel();
						root.setText(value.toString() + " ");
						root.setIcon(icon);
						if (_PARAMETERS != null) {
							item = _PARAMETERS.getDatabase();
							if (value.toString() != null && item != null && value.toString().equalsIgnoreCase(item)) {
								root.setForeground(Color.BLUE);
							}
						}
						if (selected) {
							root.setOpaque(true);
							root.setForeground(Color.WHITE);
							root.setBackground(Color.DARK_GRAY);
						}
						root.setFont(new Font("Verdana", (isRoot || (value.toString() != null && item != null && value.toString().equalsIgnoreCase(item))) ? Font.BOLD : Font.PLAIN, 12));
						return root;
					}
					
				});
				
				_list_c1.setOpaque(true);
				_list_c1.setBackground(new Color(250,250,250));
				
				_list_c1.addMouseListener(new MouseListener(){
					public void mouseClicked(MouseEvent event) {
						if (_list_c1 == null || _list_c1.getSelectionCount() < 1) return;
						String tree = _list_c1.getSelectionPath().toString();
						tree = tree.replace("[", "");
						tree = tree.replace("]", "");
						tree = tree.replace(", ", ",");
						if (getConnection() != null && getConnection().getServerType() != 0) {
							System.out.println("menu de contexto desativado para servidores que não sejam o mysql.");
							return;
						}
						final String[] selection = tree.split(",");
						if (event.getButton() == 3) {
							JPopupMenu popup = new JPopupMenu();
							if (selection.length == 2) {
								SQLConnectionManager con = MainWindow.getActiveTabConnection();
								if (con == null || !con.isConnected()) {
									return;
								}
								JMenuItem[] items = null;
								switch (con.getServerType()) {
								
									// -- Menu para banco MYSQL
								
									case 0:
										items = new JMenuItem[4];
										// -- selecionar
										items[0] = new JMenuItem("<html>Selecionar base <i><font color='green'>%BASE%</font></i></html>".replace("%BASE%", selection[1]));
										items[0].addActionListener(new ActionListener(){
											public void actionPerformed(ActionEvent e) {
												runLater(new JThreadCommands(1, "USE `%BASE%`".replace("%BASE%", selection[1]), MainWindow.getActiveTab()));
											}
										});
										// -- mais opcoes
										items[1] = new JMenu("Mais opções da base");
										// -- -- remover base.
										items[2] = new JMenuItem("<html>Excluir base <i><font color='red'>%BASE%</font></i></html>".replace("%BASE%", selection[1]));
										items[2].addActionListener(new ActionListener(){
											public void actionPerformed(ActionEvent e) {
												int option = JOptionPane.showConfirmDialog(null, "<html>Deseja realmente excluir a base: <i><b><font color='red'>%BASE%</font></b></i><br><br><b>Este processo é irreversível!</b></html>".replace("%BASE%", selection[1]), "Confirmação!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
												if (option == JOptionPane.YES_OPTION) {
													runLater(new JThreadCommands(1, "DROP DATABASE `%BASE%`".replace("%BASE%", selection[1]), MainWindow.getActiveTab()));
												}
											}
										});
										items[1].add(items[2]);
										
										// -- criar base
										items[3] = new JMenuItem("Criar base");
										items[3].addActionListener(new ActionListener(){
											public void actionPerformed(ActionEvent e) {
												String database = JOptionPane.showInputDialog(null, "<html>Informe o nome da base a ser criada.<br><br><b>Este processo requer privilégios elevados no banco de dados!</b></html>", "Confirmação!", JOptionPane.INFORMATION_MESSAGE);
												if (database != null && !database.isEmpty()) {
													runLater(new JThreadCommands(1, "CREATE DATABASE `%BASE%`".replace("%BASE%", database), MainWindow.getActiveTab()));
												}
											}
										});
										
										popup.add(items[0]);		 // selecionar base
										popup.add(new JSeparator()); // --
										popup.add(items[1]);		 // mais opcoes -> remover base
										popup.add(new JSeparator()); // --
										popup.add(items[3]);		 // criar base
										break;
								}
								if (popup.getComponents().length > 0) {
									popup.show(event.getComponent(), event.getX(), event.getY());
								}
							}
							
						}
					}
					public void mouseEntered(MouseEvent arg0) { }
					public void mouseExited(MouseEvent arg0) { }
					public void mousePressed(MouseEvent event) {
						if (event.getClickCount() > 1) {
							if (_list_c1.getSelectionPath() == null) {
								return;
							}
							String selection = _list_c1.getSelectionPath().toString();
							selection = selection.replace("[", "");
							selection = selection.replace("]", "");
							selection = selection.replace(", ", ",");
							if (selection.split(",").length == 2) {
								SQLConnectionManager con = getConnection();
								if (con != null && con.isConnected()) {
									String switch_db = null;
									switch (con.getServerType()) {
										case 0:
											switch_db = "USE `$database$`".replace("$database$", selection.split(",")[1]);
											break;
										case 3:
											switch_db = "USE $database$".replace("$database$", selection.split(",")[1]);
											break;
										case 1:
											switch_db = "ALTER SESSION SET CURRENT_SCHEMA=" + selection.split(",")[1];
											break;
										case 2:
											JOptionPane.showMessageDialog(null, "<html>Em servidores PostgreeSQL não é possível alternar entre as databases durante uma conexão já estabelecida. Reconecte a aplicação à database desejada.</html>", "jQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
											break;
									}
									Connection c = con.getConnection();
									try {
										c.setCatalog(selection.split(",")[1]);
										JThreadCommands tc = new JThreadCommands(2, switch_db, MainWindow.getActiveTab());
										Thread t = new Thread(tc);
										t.start();
									}
									catch (SQLException e) {
										e.printStackTrace();
									}
								}
								else {
									JOptionPane.showMessageDialog(null, "<html>A conexão com a base está inativa, reconecte com a base e tente novamente.</html>", "jQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
								}

							}
							else if (selection.split(",").length == 3) {
								JThreadCommands tc = new JThreadCommands(2, "SELECT * FROM " + selection.split(",")[2], MainWindow.getActiveTab());
								Thread t = new Thread(tc);
								t.start();								
							}
						}
					}
					public void mouseReleased(MouseEvent arg0) { }
					
				});
				
				JScrollPane bar_c1 = new JScrollPane(_list_c1);
				bar_c1.setAutoscrolls(true);
				bar_c1.setSize(new Dimension(250,100));
				bar_c1.setMinimumSize(new Dimension(250,100));
				bar_c1.setPreferredSize(new Dimension(250,100));
				cel_c1.add(bar_c1, in_c1_def);
			}
			/***/
	        JPanel cel_c2 = new JPanel();
	        cel_c2.setLayout(new GridBagLayout()); // <- grid C2
			
			if (cel_c2 != null) {
				GridBagConstraints in_c2_def = new GridBagConstraints();
				in_c2_def.fill = GridBagConstraints.BOTH;
				in_c2_def.insets = new Insets(0,0,0,0);
				in_c2_def.gridx = 1;
				in_c2_def.gridy = 1;
				in_c2_def.weightx = 1;
			
				ImageIcon icon_c2 = new ImageIcon(ClassLoader.getSystemResource("script.png"));
				icon_c2.setImage(icon_c2.getImage().getScaledInstance(32, 32, Image.SCALE_AREA_AVERAGING));
				_label_c2 = new JLabel("<html>Scripts e consultas:<br>&nbsp;</html>");
				_label_c2.setFont(new Font("Verdana",Font.ROMAN_BASELINE, 12));
				_label_c2.setForeground(Color.DARK_GRAY);
				_label_c2.setVerticalTextPosition(SwingConstants.BOTTOM);
				_label_c2.setSize(new Dimension(150,36));
				_label_c2.setPreferredSize(new Dimension(150,36));
				_label_c2.setIcon(icon_c2);
				cel_c2.add(_label_c2, in_c2_def);
			
				in_c2_def.insets = new Insets(0,0,0,0);
				in_c2_def.gridx = 1;
				in_c2_def.gridy = 2;
				in_c2_def.weightx = 1.d;
				in_c2_def.weighty = 1.d;
				// -- correção para scrollbar horizontal! -> baseado em: http://www.coderanch.com/t/334112/GUI/java/JTextPane
				//JEditor editor = JEditor.getInstance();
				_text_c2 = new JTextPane() {
					private static final long serialVersionUID = 2230501883134305789L;
					public boolean getScrollableTracksViewportWidth() {
						return (getSize().width < getParent().getSize().width);
					}
					public void setSize(Dimension d) {
						if (d.width < getParent().getSize().width) {
							d.width = getParent().getSize().width;
						}
						super.setSize(d);
					}
				};
				//_text_c2.getDocument().addUndoableEditListener();
				_text_c2.getDocument().addDocumentListener(new DocumentListener(){
					public void changedUpdate(DocumentEvent arg0) {
					}
					public void insertUpdate(DocumentEvent arg0) {
					}
					public void removeUpdate(DocumentEvent arg0) {
					}
				});

				_text_c2.setBorder(null);
				_text_c2.setMargin(new Insets(5,5,5,5));
				_text_c2.setAlignmentY(TOP_ALIGNMENT);
				_text_c2.setBorder(new RendererBorder());
				_text_c2.setOpaque(true);
				_text_c2.setBackground(new Color(250,250,250));
				_text_c2.setFont(new Font(Font.MONOSPACED, Font.ROMAN_BASELINE, 18));
				StyledDocument doc = _text_c2.getStyledDocument();
				StyleManager.getInstance().createStyles(doc);

		        GroovyFilter watchDocument = new GroovyFilter(doc);
		        doc.addDocumentListener(watchDocument);
				_text_c2.addKeyListener(new KeyListener(){
					public void keyPressed(KeyEvent a) {
						if (a.getKeyCode() == 120) {
							JThreadCommands tc = new JThreadCommands(1, _text_c2.getText(), MainWindow.getActiveTab());
							Thread t = new Thread(tc);
							t.start();
						}
						else if (a.getKeyCode() == 10 && _text_c2.getText() != null && !_text_c2.getText().contains(";")) {
							_text_c2.setText(_text_c2.getText().replace("\n", ""));
							JThreadCommands tc = new JThreadCommands(1, _text_c2.getText(), MainWindow.getActiveTab());
							Thread t = new Thread(tc);
							t.setPriority(Thread.MIN_PRIORITY);
							t.start();
						}						
					}
					public void keyReleased(KeyEvent event) {
						if (!(event.getKeyCode() >= 37 && event.getKeyCode() <= 40)) {
							//toggleEditor();
						}
						//System.out.println("... " + event.getKeyCode());
					}
					public void keyTyped(KeyEvent event) { }
				});
				JScrollPane bar_c2 = new JScrollPane(_text_c2);
				bar_c2.setAutoscrolls(true);			
				bar_c2.setSize(new Dimension(10,100));
				bar_c2.setPreferredSize(new Dimension(10,100));
				cel_c2.add(bar_c2, in_c2_def);
			}
			
			final JSplitPane split_c_hor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cel_c1, cel_c2);
			split_c_hor.setDividerLocation(Integer.parseInt(MainWindow.getPropertie("H_DIVIDER_LOC", "250")));
			split_c_hor.setUI(new BasicSplitPaneUI() {
	            public BasicSplitPaneDivider createDefaultDivider() {
	                return new BasicSplitPaneDivider(this) {
						private static final long serialVersionUID = 1951000791232707325L;
						public void paint(Graphics g) { } 
					};
	            }
	        });
			split_c_hor.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pce) {
					MainWindow.setPropertie("H_DIVIDER_LOC", String.valueOf(split_c_hor.getDividerLocation()));
					MainWindow.saveProperties();
				}
			});			
			cel_c_def.weightx = 1.d;
			cel_c_def.weighty = 1.d;
			split_c_hor.setBorder(null);
			split_c_hor.setDividerSize(5);
			cel_c.add(split_c_hor, cel_c_def);
			
			// #celulas D
			// --------------------------------------------------------------------------------------------------------------------
			JPanel cel_d = new JPanel();
			cel_d.setLayout(new GridBagLayout());
			
			if (cel_d != null) {
				GridBagConstraints cel_d_def = new GridBagConstraints();
				cel_d_def.fill = GridBagConstraints.BOTH;
				cel_d_def.gridx = 1;
				cel_d_def.gridy = 1;
				cel_d_def.weightx = 1;
				cel_d_def.weighty = 1;
				cel_d_def.insets = new Insets(0,0,0,0);
			
				
			
				_table_d = new JTable();
				_table_d.setGridColor(Color.WHITE);
				_table_d.setShowGrid(true);
				_table_d.setRowHeight(22);
				_table_d.setIntercellSpacing(new Dimension(1, 1));
//				_table_d.setDefaultRenderer(Object.class, table_render);
				
				_table_d.setDefaultRenderer(Object.class, _default_renderer);
				_table_d.setDefaultRenderer(Date.class, _datetime_renderer);
				_table_d.setDefaultRenderer(Integer.class, _number_renderer);
				_table_d.setDefaultRenderer(Float.class, _number_renderer);
				
				_table_d.setSelectionForeground(Color.WHITE);
				_table_d.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				_table_d.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
				JScrollPane bar_d = new JScrollPane(_table_d);
				bar_d.setSize(new Dimension(1,1));
				bar_d.setPreferredSize(new Dimension(1,1));
				bar_d.setAutoscrolls(true);
				cel_d.add(bar_d, cel_d_def);
			}
			
			final JSplitPane split_d_ver = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cel_c, cel_d);
			split_d_ver.setDividerLocation(Integer.parseInt(MainWindow.getPropertie("V_DIVIDER_LOC", "170")));
			split_d_ver.setUI(new BasicSplitPaneUI() {
	            public BasicSplitPaneDivider createDefaultDivider() {
	                return new BasicSplitPaneDivider(this) {
						private static final long serialVersionUID = 1951000791232707325L;
						public void paint(Graphics g) { } 
					};
	            }
	        });
			split_d_ver.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pce) {
					MainWindow.setPropertie("V_DIVIDER_LOC", String.valueOf(split_d_ver.getDividerLocation()));
					MainWindow.saveProperties();
				}
			});			
			split_d_ver.setBorder(null);
			split_d_ver.setDividerSize(5);
			add(split_d_ver, main_table_def);
			_current_history = 0;
			loadQueryPage(0);
			loadQueryHistory(0);
			updateQueryLabel();
		}

		
		/** -- Gerenciamento de conexão */
		public void openConnection() {
			if (_PARAMETERS != null) {
				// #01 - Conecta ao servidor.
				if (_CONNECTION != null && _CONNECTION.isConnected()) {
					_CONNECTION.closeConnection();
					_CONNECTION = null;
				}
				System.out.println("*** [Query] Conectando ao servidor... " + _PARAMETERS.getConnectionString());
				_CONNECTION = new SQLConnectionManager(_PARAMETERS.getConnectorDriver(), _PARAMETERS.getConnectionString(), _PARAMETERS.getUser(), _PARAMETERS.getPass());
				
				// -- #01 - Atualiza informações sobre a conexão...
				_PARAMETERS.updateConnectionStatus(_CONNECTION.getConnectionStatus());
				
				// -- #02 - Atualiza o botão do menu principal.
				JCheckButton button = (isConnected() ? new JCheckButton(ClassLoader.getSystemResource("server_up.png"), new Dimension(48, 48), "bt_connection") : new JCheckButton(ClassLoader.getSystemResource("server_down.png"), new Dimension(48, 48), "bt_connection"));
				MainWindow.getMenuMain().remove(0);
				MainWindow.getMenuMain().add(button, 0);
				for (MouseListener listener : button.getMouseListeners()) {
					if (listener.toString().contains("JCheckButton")) {
						listener.mouseExited(null); 
					}
				}
				
				// -- #03 - Atualiza status do JQueryPane.
				if (isConnected()) {
					updateStatus("<i><font color='green'><b>CONECTADO COM SUCESSO</b></font></i><br>\\:> <b>WAIT</b> | <font color='blue'>Aguardando lista de objetos disponíveis...</font>");
					// -- #04 - Atualiza as guias [JQueryPane]
					Component[] list = MainWindow.getTabComponents();
					if (list != null) {
						for (int i = 0; i < list.length; i++) {
							Component c = list[i];
							if (c instanceof JQueryPane && ((JQueryPane)c).equals(this)) {
								MainWindow.getTabs().setTitleAt(i, " Query: " + _PARAMETERS.getDatabase());
							}
						}
					}
					updateDatabaseList(_CONNECTION.getDatabasesList());
					updateStatus("<i><font color='green'><b>CONECTADO COM SUCESSO</b></font></i><br>\\:> <b>READY</b>");
				}
				else {
					Exception exception = _CONNECTION.getLastError();
					updateStatus("<i><font color='red'>ERRO AO CONECTAR</font></i><br>\\:> <b>FAIL</b> | <font color='black'>" + (exception == null  ? "Erro não especificado" : (exception.getMessage().length() > 48 ? exception.getMessage().substring(0, 48) + "..." : exception.getMessage())) + "</font>");
					_PARAMETERS.setColapsed(false);
					return;
				}
			}
		}
		
		public void updateQueryStatus(String message, Exception erro) {
			showToolTip(_cel_b, erro, message);
		}
		
		protected void showToolTip(JComponent component, Exception error, String message) {  
            String toolTipText = "<html><table width=350><tr><td><font color=red>" + (error != null ? "Erro" : "Aviso") + "</font><br>" + (message != null ? message + "<br>" : "") + (error != null ?  "<b>" + error.getMessage() + "</b>" : "") + "</td></tr></table></html>";  
              
            final JToolTip tooltip1 = component.createToolTip();

            PopupFactory popupFactory = PopupFactory.getSharedInstance();  
            tooltip1.setTipText(toolTipText);
            final Popup tooltip = popupFactory.getPopup(component, tooltip1, component.getLocationOnScreen().x, component.getLocationOnScreen().y + component.getHeight() + 2);  
            tooltip.show();  
            
            tooltip1.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent arg0) {
					tooltip.hide();					
				}
				public void mouseEntered(MouseEvent arg0) { }
				public void mouseExited(MouseEvent arg0) { }
				public void mousePressed(MouseEvent arg0) { }
				public void mouseReleased(MouseEvent arg0) { }
            });
		}  
		
		public boolean isConnected() {
			return (_CONNECTION != null && _CONNECTION.isConnected() ? true : false);
		}
		
		public void closeConnection() {
			if (isConnected()) {
				_CONNECTION.closeConnection();
			}
		}
		
		public SQLConnectionManager getConnection() {
			return _CONNECTION;
		}
		
		/** ------------------------ */
		/** -- Parametros da conexão */
		/** ======================== */
		public void setParameters(JParametersPanel parameters) {
			_PARAMETERS = parameters;
		}
		public JParametersPanel getParameters() {
			return _PARAMETERS;
		}
		
		private void refreshStatus() {
			if (_status_log == null || _status_log.isEmpty()) {
				return;
			}
			_status_thread = new Thread(new Runnable(){
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable(){
							public void run() {
								String status = _status_log.poll();
								if (status != null && !status.isEmpty()) {
									
									//_cel_b.setText("<html><table width='100%' cellspacing=0 cellpadding=0><tr><td><b>Histórico de comandos:</b><br>" + status + (getConnection() != null && getConnection().getLastQueryTime() > 0 ?  " [" + getConnection().getLastQueryTime() + " s]" : " [0.00 s]") + "</td></tr></html>");
									
									FontMetrics metrics = _cel_b.getFontMetrics(_cel_b.getFont());
									Dimension      size = _cel_b.getSize();
									String[]      lines = status.split("<br>");
									String 		   line = null;
									StringBuilder 	out = new StringBuilder();
									for (int i = 0; i < lines.length; i++) {
										if (metrics.stringWidth(lines[i].replaceAll("\\<[^>]*>","")) + 35 < size.width) {
											out.append(lines[i]); 
										}
										else {
											line = lines[i].replaceAll("\\<[^>]*>","");
											for (int j = line.length(); j > 0; j--) {
												if (metrics.stringWidth(line.substring(0, j)) + 65 < size.width) {
													String[] words = (line.substring(0, j) + "...").split(" ");
													line = "";
													for (int k = 0; k < words.length; k++) {
														if (k == 1) {
															out.append("<b>");
															out.append(words[k]);
															out.append("</b>");
														}
														else {
															out.append(words[k]);
														}
														out.append(" ");
													}
													
													break;
												}
											}
										}
										if (i + 1 != lines.length) {
											out.append("<br>");
										}
									}
									
									_cel_b.setText("<html><table width='100%' cellspacing=0 cellpadding=0><tr><td><b>Histórico de comandos:</b><br>" + out.toString() + (getConnection() != null && getConnection().getLastQueryTime() > 0 ?  " [" + getConnection().getLastQueryTime() + " s]" : " [0.00 s]") + "</td></tr></html>");
									_cel_b.setToolTipText("<html>" + status + "</html>");
									
									/*
									_cel_b.setText("<html><table width='100%' cellspacing=0 cellpadding=0><tr><td><b>Histórico de comandos:</b><br>" + status + (getConnection() != null && getConnection().getLastQueryTime() > 0 ?  " [" + getConnection().getLastQueryTime() + " s]" : " [0.00 s]") + "</td></tr></html>");
									_cel_b.setToolTipText(status);
									*/
									
								}
								if (_status_log != null && !_status_log.isEmpty()) {
									_status_thread = null;
									refreshStatus();
								}
							}
						});
					}
					catch (Exception e) { e.printStackTrace(); }
				}
			});
			_status_thread.start();
		}
		
		public void updateStatus(final String status) {
			_status_log.offer(status);
			if (_status_thread == null || !_status_thread.isAlive()) {
				refreshStatus();	
			}
		}
		
		
		
		
		public void updateQueryList(String[] col_names, Object[][] data) {
			_QUERY_COLUMN_NAMES = col_names;
			_QUERY_DATA = data;
			loadQueryPage(1);
		}
		
		
		public void loadQueryPage(int page) {
			// abilita e desabilita os botoes de navegação entre as páginas conforme necessário.
			switch(getQueryPageCount()) {
				case -1:
				case 0:
				case 1: // desabilita todos os botoes de navegação já que só existe 1 página ou nenhuma das páginas para exibir.
					setMenuButtonStatus(0, false);
					setMenuButtonStatus(1, false);
					setMenuButtonStatus(2, false);
					setMenuButtonStatus(3, false);
					if (getQueryPageCount() != 1) {
						updateStatus("Não existem páginas para exibição.<br>\\:> <b>OK</b>");
						return;
					}
					break;
				default:
					setMenuButtonStatus(0, (page <= 1 ? false : true));
					setMenuButtonStatus(1, (page <= 1 ? false : true));
					setMenuButtonStatus(2, (page >= this.getQueryPageCount() ? false : true));
					setMenuButtonStatus(3, (page >= this.getQueryPageCount() ? false : true));
			}
			// carrega e exibe a página selecionada.
			if (page < 1) {
				page = 1;
			}
			else if (page > getQueryPageCount()) {
				page = getQueryPageCount();
			}
			
			_current_page = page;
			
			page = (page - 1) * 1000;
			
			int rows = Math.min(_QUERY_DATA.length - page, 1000);
			
			Object[][] temp = new Object[rows][_QUERY_COLUMN_NAMES.length];
			
			_paint_time = System.nanoTime();
			
			float progress = 0;
			for (int a = 0; a < temp.length; a++) {
				for (int b = 0; b < _QUERY_COLUMN_NAMES.length; b++) {
					if (a >= _QUERY_DATA.length) {
						continue;
					}
					if (_paint_time <= System.nanoTime() || progress == 100) {
						updateStatus("Preparando visualização da página: <font color='red'>" + ((page + 1000) / 1000) + "</font> <b>" + progress + "%</b><br>\\:> <b>...</b>");
						_paint_time = 40000000 + System.nanoTime();
					}
					progress = (((a + 1F) / temp.length) * 100F);

					temp[a][b] = (_QUERY_DATA[a + page][b] == null ? "null" : _QUERY_DATA[a + page][b]);
				}
			}
			updateStatus("Exibindo página <font color='blue'>" + ((page + 1000) / 1000) + "/" + this.getQueryPageCount() + "</font> <b>" + page + " - " + (page + temp.length) + "</b> / " + _QUERY_DATA.length + "<br>\\:> <b>OK</b>");
			QueryTableModel table_model  = new QueryTableModel(_QUERY_COLUMN_NAMES, temp);
			_table_d.setModel(table_model);
			_table_d.setName("result");
			_table_d.setRowSorter(new TableRowSorter<QueryTableModel>(table_model));
			System.gc();
		}		
		
		
		public void searchQuery(String search) {
			if (search == null || (search.isEmpty())) {
				updateStatus("Termo de busca <b>inválida</b>.<br>\\:> <b>OK</b>");
				return;
			}
			else if (_QUERY_DATA == null || (_QUERY_DATA != null && _QUERY_DATA.length == 0)) {
				JOptionPane.showMessageDialog(null, "Não existem resultados para efetuar uma pesquisa, para iniciar uma pesquisa você deve executar uma QUERY antes.");
				return;
			}
			// desabilita os botoes de navegacao...
			setMenuButtonStatus(0, false);
			setMenuButtonStatus(1, false);
			setMenuButtonStatus(2, false);
			setMenuButtonStatus(3, false);
			
			ArrayList<Object[]> list = new ArrayList<Object[]>();
			boolean sensitive = search.contains("#");
			if (sensitive) {
				search = search.replace("#", "");
			}
			String target = null;
			float progress = 0;
			for (int i = 0; i < _QUERY_DATA.length; i++) {
				for (int j = 0; j < _QUERY_DATA[i].length; j++) {
					target = String.valueOf(_QUERY_DATA[i][j]);
					if (sensitive) {
						if (target.contains(search)) {
							list.add(_QUERY_DATA[i]);
							break;
						}
					}
					else if (target.toLowerCase().contains(search.toLowerCase())) {
						list.add(_QUERY_DATA[i]);
						break;
					}
				}
				
				
				progress = (((i + 1f) / _QUERY_DATA.length) * 100f);
				if (_paint_time <= System.nanoTime() || progress == 100) {
					updateStatus("Pesquisando página: <font color='blue'>" + (int)((i / 1000) + 1) + "/" + getQueryPageCount() + "</font> <b>" + progress + "%</b><br>\\:> <b>...</b>");
					_paint_time = 40000000 + System.nanoTime();
				}
				progress = (int)(((i + 1.f) / _QUERY_DATA.length) * 100);
				
			}
			
			
			Object[][] regs = list.toArray(new Object[list.size()][_QUERY_COLUMN_NAMES.length]);
			QueryTableModel table_model  = new QueryTableModel(_QUERY_COLUMN_NAMES, regs);
			_table_d.setModel(table_model);
			_table_d.setName("search");
			_table_d.setRowSorter(new TableRowSorter<QueryTableModel>(table_model));
			autoAdjustQueryTable();
			updateStatus("Encontrados <font color='blue'>" + list.size() + "</font> registros,<br>\\:> <b>OK</b>");
		}
		
		
		public void autoAdjustQueryTable() {
			if (_table_d != null && _table_d.getModel() instanceof QueryTableModel) {
				QueryTableModel model = (QueryTableModel)_table_d.getModel();
				Object[][] 	data	  = model.getData();
				Object[] 	headers	  = model.getColumnNames();
				int[]		cols 	  = null;
				int[] 		rows 	  = null;
				FontMetrics metrics   = _table_d.getFontMetrics(_table_d.getFont());
				String[]	lines	  = null;
				float		progress  = 0;
				if (headers != null && headers.length > 0) {
					cols = new int[headers.length];
					rows = new int[data.length];
					// -- calcula a largura mínima de cada coluna - header.
					for (int i = 0; i < headers.length; i++) {
						cols[i] = (headers[i] == null ? 0 : metrics.stringWidth(headers[i].toString()));
					}
					
					// -- calcula a largura máxima de cada coluna - data					
					_paint_time = System.nanoTime();
					progress = 0;
					for (int i = 0; i < data.length; i++) {
						for (int j = 0; j < data[i].length; j++){
							lines = (data[i][j] == null ? null : data[i][j].toString().split("\n"));
							for (int k = 0; lines != null && k < lines.length; k++) {
								cols[j] = Math.max(cols[j], metrics.stringWidth(lines[k]));
								rows[i] = Math.max(rows[i], lines.length * (metrics.getHeight() + metrics.getLeading()));
							}
							progress = (((i + 1f) / data.length) * 100f);
							if (_paint_time <= System.nanoTime() || progress == 100) {
								updateStatus("Calculando dimensões: <font color='blue'>" + getCurrentPage() + "/" + getQueryPageCount() + "</font> <font color='red'><b>" + progress + "%</b></font><br>\\:> <b>...</b>");
								_paint_time = 40000000 + System.nanoTime();
							}
						}
					}
					
					// -- aplica o tamanho das colunas.					
					_paint_time = System.nanoTime();
					progress = 0;
					TableColumnModel col_model = _table_d.getColumnModel();
					TableColumn col = null; 
					for (int j = 0; col_model != null && j < cols.length; j++) {
						col = col_model.getColumn(j);
						col.setWidth(cols[j] + 10);
						col.setMinWidth(cols[j] + 10);
						col.setPreferredWidth(cols[j] + 10);
					}
					for (int i = 0; i < rows.length; i++) {
						_table_d.setRowHeight(i, rows[i] + 6);
						progress = (((i + 1f) / rows.length) * 100f);
						if (_paint_time <= System.nanoTime() || progress == 100) {
							updateStatus("Aplicando dimensões: <font color='blue'>" + getCurrentPage() + "/" + getQueryPageCount() + "</font>  <font color='red'><b>" + progress + "%</b></font><br>\\:> <b>...</b>");
							_paint_time = 40000000 + System.nanoTime();
						}
					}
					updateStatus("Exibindo página <font color='blue'>" + getCurrentPage() + "/" + getQueryPageCount() + "</font> <b>" + ((getCurrentPage() - 1) * 1000) + " - " + (((getCurrentPage() - 1) * 1000) + 1000) + "</b> / " + _QUERY_DATA.length + "<br>\\:> <b>OK</b>");
				}
				MainWindow.repaintCurrentTab();
				//MainWindow.fullRepaint();
			}
		}
		
		public void setMenuButtonStatus(int position, boolean enabled) {
			Component c = null;
			for (int i = 0; i < _cel_a.getComponentCount(); i++) {
				c = _cel_a.getComponent(i);
				if (position == i && c != null) {
					c.setEnabled(enabled);
					_cel_a.remove(c);
					_cel_a.add(c, i);
					for (MouseListener listener : c.getMouseListeners()) { if (listener == null) continue; else if (listener.toString().contains("JCheckButton")) listener.mouseExited(null); }
				}
			}
		}

		public int getQueryPageCount() {
			if (_QUERY_DATA == null) return -1;
			return (_QUERY_DATA.length / 1000) + 1;
		}
		
		public int getCurrentPage() {
			return _current_page;
		}
				
		// <------------------------------------------------------------------------------------------------->
		
		public void loadQueryHistory(int page) {
			switch(getHistorySize()) {
				case -1:
				case 0:
					setMenuButtonStatus(12, false);
					setMenuButtonStatus(13, false);
					return;
				default:
					setMenuButtonStatus(12, (page <= 1 ? false : true));
					setMenuButtonStatus(13, (page >= (getHistorySize()) ? false : true));
			}
			if (_history != null) {
				int i = 0;
				for (JHistory his : _history) {
					if (his.getType() == Type.QUERY) {
						++i;
						if (page == i) {
							_current_history = page;
							if (MainWindow._debug) {
								System.out.println("-> " + i + " ~ [" + his.getRaw() + "] -> " + _current_history);
							}
							_text_c2.setText(his.getRaw() == null ? "" : his.getRaw().toString());
							//toggleEditor();
							updateQueryLabel();
							MainWindow.repaintCurrentTab();
							//MainWindow.fullRepaint();
						}
						else {
							if (MainWindow._debug) {
								System.out.println("=> " + i + " ~ " + his.getRaw());	
							}
						}
						//++i;
					}
				}
			}
		}
		
		public void addQueryHistory(String query) {
			if (_history != null && query != null) {
				query = query.trim();
				_history.add(new JHistory(Type.QUERY, query));
			}
			_current_history = getHistorySize();
			loadQueryHistory(_current_history);
			updateQueryLabel();
		}
		
		public void addHistory(JHistory hist) {
			if (hist != null) {
				_history.add(hist);
			}
		}
		
		public int getHistorySize() {
			if (_history != null && _history.size() > 0) {
				int i = 0;
				for (JHistory his : _history) {
					if (his.getType() == Type.QUERY) {
						++i;
					}
				}
				return i;
			}
			return 0;
		}
		
		public int getHistoryPage() {
			return _current_history;
		}
		
		public void updateQueryLabel() {
			_label_c2.setText("<html>Scripts e consultas:<br><font size=2>Status do editor <b>»</b> <font color='#1E90FF'><b>" + (_bt_script_edit.isSelected() ? "LIGADO" : "DESLIGADO") + "</b></i></font> <font size=2 color='#888888'><b>" + getHistoryPage() + "</font></b>/<font size=2 color='#888888'><b>"  + getHistorySize() + "</b></font></html>");
		}
		
		public JHistory[] getHistory() {
			if (_history != null) {
				return _history.toArray(new JHistory[_history.size()]);
			}
			return null;
		}
		
		public void saveQuery(final String extension) {
			int page = this.getCurrentPage();
			String data_type = _table_d.getName();
			Object[][] data = null;
			
			if (page < 1) page = 1;
			else if (page > getQueryPageCount()) page = getQueryPageCount();
			int progress = 0;			
			if (data_type == null) {
				updateStatus("Não há informações para serem exportados<br>\\:> <b>OK</b>");
				return;
			}
			else if (data_type.equalsIgnoreCase("result")) {
				// carrega registros da página selecionada.
				page = (page - 1) * 1000;
				int rows = Math.min(_QUERY_DATA.length - page, 1000);
				data = new Object[rows][_QUERY_COLUMN_NAMES.length];
				for (int a = 0; a < data.length; a++) {
					for (int b = 0; b < _QUERY_COLUMN_NAMES.length; b++) {
						if (a >= _QUERY_DATA.length) continue;
						data[a][b] = _QUERY_DATA[a + page][b];
					}
					if ((int)(((1.f + a) / data.length) * 100) != progress) {
						updateStatus("Preparando dados: <font color='red'><b>" + (int)(((1.f + a) / data.length) * 100) + "%</b></font><br>\\:> <b>...</b>");
					}
					progress = (int)(((1.f + a) / data.length) * 100);
				}
			}
			else if (data_type.equalsIgnoreCase("search")) {
				// obtém registros da pesquisa atual.
				QueryTableModel model = (QueryTableModel)_table_d.getModel();
				data = model.getData();
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < model.getColumnCount(); j++) {
						if (data[i][j] == null) continue;
						data[i][j] = MainWindow.parseHtmlToPlain(data[i][j].toString());
						        
					}
					if ((int)(((1.f + i) / data.length) * 100) != progress) {
						updateStatus("Preparando dados: <font color='red'><b>" + (int)(((1.f + i) / data.length) * 100) + "%</b></font><br>\\:> <b>...</b>");
					}
					progress = (int)(((1.f + i) / data.length) * 100);
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "A tabela em exibição não pode ser exportada.");
				return;
			}
			
			JFileChooser eleitor = new JFileChooser();
			eleitor.setFileFilter(new FileFilter(){
				public boolean accept(File file) {
					String name = file.getName();
					if (name != null) {
						return (name.toLowerCase().endsWith("." + extension.toLowerCase()) || file.isDirectory());
					}
					return false;
				}

				public String getDescription() {
					return "Arquivos *." + (extension.equalsIgnoreCase("html") ? "HTML" : "CSV");
				}
				
			});
			eleitor.setCurrentDirectory(new File(MainWindow.getPropertie("QUERY_EXPORT_FILE", "C:\\")));
			
			int result = eleitor.showSaveDialog(MainWindow.getMainFrame());
			
			if (result == JFileChooser.APPROVE_OPTION) {

				File file = null;
				if (eleitor != null && eleitor.getSelectedFile() != null && !eleitor.getSelectedFile().getPath().contains(extension.replace("*", ""))) {
					file = new File(eleitor.getSelectedFile().getPath() + "." + extension.replace("*", ""));
				}
				else {
					file = eleitor.getSelectedFile();
				}
				try {
					MainWindow.setPropertie("QUERY_EXPORT_FILE", file.getAbsolutePath());
					MainWindow.saveProperties();
					FileOutputStream out = new FileOutputStream(file);
					
					if (extension.equalsIgnoreCase("html")) {
						String text = "<html>";
						text += "<style type='text/css'> body { font-family: 'Verdana'; font-size: 10px; background-color: #eee; } table, td { font-size: 10px; border: 1px solid #fff; } .line0 { background-color: #999; color: #fff; } .line1 { background-color: #ddd; } .line2 { background-color: #ccc } </style>";
						text += "<body><br><b>Query executada:</b> " + _text_c2.getText() + "<br><br><table cellpadding=5 cellspacing=0 border=0>";
						Object value = null;
						out.write(text.getBytes());
						for (int i = -1; i < data.length; i++) {
							text = "<tr>";
							for (int j = 0; j < _QUERY_COLUMN_NAMES.length; j++) {
								 // nome das colunas.
								if (i < 0) {
									value = _QUERY_COLUMN_NAMES[j];
									text +=  "<td class='line0'>" + (value == null ? "&nbsp;" : "<b>" + MainWindow.parseToHtml(value.toString()) + "</b>") + "</td>";
									if (j == _QUERY_COLUMN_NAMES.length) {
										text += "</tr>";
										out.write(text.getBytes());
										++i;
										text = null;
										continue;
									}
									else continue;
								}
								// linhas de registros.
								value = data[i][j];
								text +=  "<td class='" + (i % 2 == 0 ? "line1" : "line2") + "'>" + (value == null ? "&nbsp;" : MainWindow.parseToHtml(value.toString())) + "</td>";
							}
							if (text != null) {
								text += "</tr>";
								out.write(text.getBytes());
								if ((int)(((1.f + i) / data.length) * 100) != progress) {
									updateStatus("Exportando dados: <font color='green'><b>" + (int)(((1.f + i) / data.length) * 100) + "%</b></font><br>\\:> <b>...</b>");
								}
								progress = (int)(((1.f + i) / data.length) * 100);
							}
						}
						text = "</table></body></html>";
						out.write(text.getBytes());
						out.close();
						JOptionPane.showMessageDialog(null, "Conteúdo da tabela atual foi exportado para o arquivo:\n" + file.getAbsolutePath().toUpperCase());
						Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + file.getPath());
					}
					else if (extension.equalsIgnoreCase("csv")) {
						String text = null;
						String value = null;
						for (int i = -1; i < data.length; i++) {
							text = "";
							for (int j = 0; j < _QUERY_COLUMN_NAMES.length; j++) {
								 // nome das colunas.
								if (i < 0) {
									value = _QUERY_COLUMN_NAMES[j];
									text +=  (value == null ? " " : value.contains("\n") ? "\"" + value.replace("\"", "'") + "\"" : value) + ";";
									if (j == _QUERY_COLUMN_NAMES.length) {
										text += "\n";
										out.write(text.getBytes());
										++i;
										text = null;
										continue;
									}
									else continue;
								}
								// linhas de registros.
								value = data[i][j].toString();
								text +=  (value == null ? " " : value.contains("\n") ? "\"" + value.replace("\"", "'") + "\"" : value) + ";";
							}
							if (text != null) {
								text += "\n";
								out.write(text.getBytes());
								if ((int)(((1.f + i) / data.length) * 100) != progress) {
									updateStatus("Exportando dados: <font color='green'><b>" + (int)(((1.f + i) / data.length) * 100) + "%</b></font><br>\\:> <b>...</b>");
								}
								progress = (int)(((1.f + i) / data.length) * 100);
							}
						}
						out.close();
						JOptionPane.showMessageDialog(null, "Conteúdo da tabela atual foi exportado para o arquivo:\n" + file.getAbsolutePath());
						Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + file.getPath());
					}
					updateStatus("Exporção concluída com sucesso!<br>\\:> <b>OK</b>");
					
					
				}
				catch(IOException e2) {
					e2.printStackTrace();
				}
			}
			

		}
		
		
		public void updateTableList() {
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					DefaultTreeModel model = (DefaultTreeModel)_list_c1.getModel();
					Object root = model.getRoot();
					DefaultMutableTreeNode row = null;
					String database = _CONNECTION.getDatabase();
					for (int i = 0; i < model.getChildCount(root); i++) {
						row = (DefaultMutableTreeNode)model.getChild(root, i);
						if (row.toString() != null && database != null && row.toString().equalsIgnoreCase(database.trim())) {
							break;
						}
					}
					row.removeAllChildren();
					JCheckTreeNode item = null;
					List<String> list = _CONNECTION.getViews();
					if (list != null) {
						for (String table : list) {
							item = new JCheckTreeNode(table);
							item.setType(JCheckTreeNode.VIEW);
							row.add(item);
						}
				    }
					list = _CONNECTION.getTables();
					if (list != null) {
						for (String table : list) {
							item = new JCheckTreeNode(table);
							item.setType(JCheckTreeNode.TABLE);
							row.add(item);
				    	}
					}
					if (model != null && model.getRoot() != null) {
						model.setRoot((DefaultMutableTreeNode)model.getRoot());
					}
					MainWindow.expandAll(_list_c1, getCurrentDatabaseTreePath(), true);
				}
			});
		}
		
		private TreePath getCurrentDatabaseTreePath() {
			DefaultTreeModel model = (DefaultTreeModel)_list_c1.getModel();
			String database = _CONNECTION.getDatabase();
			if (database != null && model != null && model.getRoot() instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
				for (int i = 0; i < root.getChildCount(); i++) {
					TreeNode node = root.getChildAt(i);
					if (node.toString().equalsIgnoreCase(database.trim())) {
						return getTreePath(node);
					}
				}
			}
			return null;
		}
		
		private TreePath getTreePath(TreeNode treeNode) {
			List<Object> nodes = new ArrayList<Object>();
			if (treeNode != null) {
				nodes.add(treeNode);
				treeNode = treeNode.getParent();
				while (treeNode != null) {
					nodes.add(0, treeNode);
					treeNode = treeNode.getParent();
				}
			}
		    return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
		}
		
		public boolean isDatabasePresent(String database) {
			DefaultTreeModel model = (DefaultTreeModel)_list_c1.getModel();
			if (database != null && model != null && model.getRoot() instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
				for (int i = 0; i < root.getChildCount(); i++) {
					TreeNode node = root.getChildAt(i);
					if (node.toString().equalsIgnoreCase(database.trim())) {
						return true;
					}
				}
			}
			return false;
		}
		
		public void updateDatabaseList(final List<String> list) {
			
			//SwingUtilities.invokeLater(new Runnable(){
			//	public void run() {
					if (list != null) {
						DefaultMutableTreeNode root = new DefaultMutableTreeNode(getParameters().getHost());
						JCheckTreeNode item = null;
						String database = null;
					    for (String data : list) {
					    	item = new JCheckTreeNode(data);
					    	item.setType(JCheckTreeNode.DATABASE);
					    	root.add(item);	
					    }
					    
						DefaultTreeModel model = (DefaultTreeModel)_list_c1.getModel();
						model.setRoot(root);
						try {
							database = (_CONNECTION == null || _CONNECTION.getDatabase() == null || _CONNECTION.getDatabase().isEmpty() ? null : _CONNECTION.getDatabase());
							if (database != null) {
								updateTableList();
							}
						}
						catch (Exception e) {
							String message = null;
							if (getConnection() == null || !getConnection().isConnected()) {
								message = "A conexão não está ativa.";
							}
							else if (getConnection().getDatabase() == null || getConnection().getDatabase().isEmpty()) {
								message = "Não há nenhuma database selecionada.";
							}
							if (message != null) {
								JOptionPane.showMessageDialog(null, message, "JQueryAnalizer - Erro!", JOptionPane.OK_OPTION);
							}
							e.printStackTrace();
						}
					}
					updateDatabaseLabel();
					//MainWindow.expandAll(_list_c1, true);
			//	}
			//});
		}
		
		public void updateDatabaseLabel() {
			if (_CONNECTION != null) {
				String name = _CONNECTION.getDatabase();
				_label_c1.setText("<html>Bases de dados disponíveis:<br><font size=2>Selecionada <b>»</b> <font size=2 color='#1E90FF'><b>" + (name != null && !name.isEmpty() ? name : "NENHUMA") + "</b></font></html>");
			}
		}
		
		
		public void clearQuerySentence() {
			_text_c2.setText("");
		}
		
		public String getQuerySentence() {
			return _text_c2.getText();
		}
		
		public void setQuerySentence(String c) {
			_text_c2.setText(c);
		}

		private class QueryTableModel extends AbstractTableModel {
			
			private static final long serialVersionUID = -2173685424567032936L;
			
			protected String[] columnNames = null;
	        
			protected Object[][] data = null;
			
			public QueryTableModel(String[] col, Object[][] rows) {
				if (col != null)
					columnNames = col;
				if (rows != null)
					data = rows;
			}
	        
	        
			public Class<?> getColumnClass(int col) {
				if (getRowCount() > 0) {
					Object item = null;
					item = getValueAt(0, col); 
					if (item != null) {
						if (item instanceof Integer) {
							return Integer.class;
						}
						if (item instanceof Date) {
							return Date.class;
						}
						if (item instanceof Float) {
							return Float.class;
						}
					}
				}
				return Object.class;
			}
			
	        @SuppressWarnings("unused")
			public void setColumnNames(String[] data){
	        	this.columnNames = data;
	        }
	        
        
	        @SuppressWarnings("unused")
			public void setRows(Object[][] data){
	        	this.data = data;
	        	for (int i = 0; i < this.columnNames.length; i++) {
	        		for (int j = 0; j < data[i].length; j++) {
	        			this.setValueAt(data[i][j], i, j);
	        		}
	        	}
	        }
	        
	        public Object[][] getData() {
	        	return (Object[][])this.data;
	        }
	        
			public String[] getColumnNames() {
	        	return (String[])this.columnNames;
	        }
	        
	        public int getColumnCount() {
	            return columnNames.length;
	        }

	        public int getRowCount() {
	            return data.length;
	        }

	        public String getColumnName(int col) {
	            return columnNames[col];
	        }

	        public Object getValueAt(int row, int col) {
	        	if (data[row][col] != null) {
	        		if (data[row][col] instanceof Integer) {
	        			return Integer.valueOf(data[row][col].toString());
	        		}
	        	}
	            return data[row][col];
	        }

	        
	        public boolean isCellEditable(int row, int col) {
	        	if (getValueAt(row, col) instanceof String) {
	        		String value = (String)getValueAt(row, col);
	        		if (value != null && value.split("<br>").length > 1) return true; 
	        	}
	        	return false;
	        }
	    }
		
		
		
		public void setConnection(SQLConnectionManager value) {
			_CONNECTION = value;
		}
		
		public void runLater(Runnable r) {
			if (r == null) {
				return;
			}
			SwingUtilities.invokeLater(r);
		}
		
		
		public void refreshDatabaseList() {
			updateDatabaseList(_CONNECTION.getDatabasesList());
		}
		
	}