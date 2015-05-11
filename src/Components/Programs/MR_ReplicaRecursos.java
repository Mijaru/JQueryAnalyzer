package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.util.Iterator;

import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import javolution.util.FastList;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JQDialog;

public class MR_ReplicaRecursos {
	private SQLConnectionManager _CONNECTION;
	private JQDialog _DIALOG;
	private Dimension _size = new Dimension(515,460);
	private Font _font_title;
	private Color _color_title;
	private JTable _LIST;
	private JLabel _active;
	
	public MR_ReplicaRecursos(SQLConnectionManager con) {
		_CONNECTION = con;
		_font_title = new Font("Verdana", Font.ROMAN_BASELINE, 11);
		_color_title = Color.DARK_GRAY;
		init();
	}
	
	private <unsigned> void init() {
		_DIALOG = new JQDialog(MainWindow.getMainFrame(), "JQueryAnalizer - (mrDigital) Ferramenta para replicação de recursos");
		_DIALOG.setSize(_size);
		_DIALOG.setMinimumSize(_size);
		_DIALOG.setMaximumSize(_size);
		_DIALOG.setPreferredSize(_size);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JQDialog.DISPOSE_ON_CLOSE);
		_DIALOG.setLocationRelativeTo(null);
		
		JLabel text_1 = new JLabel("Lista de usuários cadastrados");
		text_1.setFont(_font_title);
		text_1.setForeground(_color_title);
		text_1.setBounds(10, 10, 200, 15);
		_DIALOG.add(text_1);
		
		
		// -- submenu definicao da area e botoes... incluindo stilos.
		JPanel area_1 = new JPanel();		
		area_1.setOpaque(true);
		area_1.setBackground(new Color(200,200,200));
		area_1.setBounds(10, 30, 480, 27);
		area_1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		area_1.setLayout(new GridBagLayout());
		_DIALOG.add(area_1);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(3,3,3,3);
		gbc.gridy = 0;
		
		ImageIcon ico_reload = new ImageIcon(ClassLoader.getSystemResource("prg_reload.png"));
		ico_reload.setImage(ico_reload.getImage().getScaledInstance(20, 20, 100));
		JLabel reload = new JLabel(ico_reload);
		reload.setToolTipText("Atualiza a lista de usuários");
		reload.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent me) {
				_active.setText("<html>Obtendo dados para atualizar a lista, <i>aguarde</i>...</html>");
				Browser b = new Browser();
				Thread t = new Thread(b);
				t.start();
			}
			@Override
			public void mouseEntered(MouseEvent a) { }
			@Override
			public void mouseExited(MouseEvent a) { }
			@Override
			public void mousePressed(MouseEvent a) { }
			@Override
			public void mouseReleased(MouseEvent a) { }			
		});
		gbc.gridx = 0;
		area_1.add(reload, gbc);
		
		gbc.gridx = 1;
		area_1.add(new JBracket(1,20), gbc);
		
		ImageIcon ico_select = new ImageIcon(ClassLoader.getSystemResource("prg_check.png"));
		ico_select.setImage(ico_select.getImage().getScaledInstance(20, 20, 100));
		JLabel select = new JLabel(ico_select);
		select.setToolTipText("Seleciona o usuário de referência para a replicação dos recursos!");
		select.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent me) {
				JTableModel model = (JTableModel)_LIST.getModel();
				if (model == null || _LIST.getSelectedRow() < 0) return;
				model.setState(_LIST.getSelectedRow(), (model.getState(_LIST.getSelectedRow()) == 2 ? 0 : 2));
				for (int i = 0; i < model.getRowCount(); i++) {
					if (i == _LIST.getSelectedRow()) continue;
					if (model.getState(i) == 2) {
						model.setState(i, 0);
					}
				}
				_LIST.repaint();
			}
			@Override
			public void mouseEntered(MouseEvent a) { }
			@Override
			public void mouseExited(MouseEvent a) { }
			@Override
			public void mousePressed(MouseEvent a) { }
			@Override
			public void mouseReleased(MouseEvent a) { }			
		});
		gbc.gridx = 2;
		area_1.add(select, gbc);
		
		ImageIcon ico_uncheck = new ImageIcon(ClassLoader.getSystemResource("prg_star.png"));
		ico_uncheck.setImage(ico_uncheck.getImage().getScaledInstance(20, 20, 100));
		JLabel uncheck = new JLabel(ico_uncheck);
		uncheck.setToolTipText("Desmarca todos os usuários da lista");
		uncheck.addMouseListener(new MouseListener(){
			@Override public void mouseClicked(MouseEvent arg0) {
				JTableModel model = (JTableModel)_LIST.getModel();
				for (int row = 0; row < model.getRowCount(); row++) {
					model.setState(row, 0);	
				}
				_LIST.repaint();				
			}
			@Override public void mouseEntered(MouseEvent arg0) { }
			@Override public void mouseExited(MouseEvent arg0) { }
			@Override public void mousePressed(MouseEvent arg0) { }
			@Override public void mouseReleased(MouseEvent arg0) { }
		});
		gbc.gridx = 3;
		area_1.add(uncheck, gbc);
		
		gbc.gridx = 4;
		area_1.add(new JBracket(1,20), gbc);
		
		ImageIcon ico_clipboard = new ImageIcon(ClassLoader.getSystemResource("prg_clipboard.png"));
		ico_clipboard.setImage(ico_clipboard.getImage().getScaledInstance(20, 20, 100));
		JLabel clipboard = new JLabel(ico_clipboard);
		clipboard.setToolTipText("Copia a lista atual de usuarios para a area de trabalho");
		clipboard.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent arg0) {
				JTableModel model = (JTableModel)_LIST.getModel();
				StringBuffer columns = new StringBuffer();
				StringBuffer content = new StringBuffer();
				String temp = null;
				for (int row = 0; row < model.getRowCount(); row++) {
					for (int col = 0; col < model.getColumnCount(); col++) {
						if (row == 0) {
							columns.append(model.getColumnName(col) + "\t");
						}
						temp = (String)model.getValueAt(row, col);
						content.append((temp != null ? temp : "") + "\t");
					}
					content.append("\n");
				}
				int length = columns.length();
				columns.append("\n");
				for (int i = 0; i < length; i++) {
					columns.append(columns.charAt(i) != '\t' ? "-" : "\t");
				}
				Clipboard keyboard = Toolkit.getDefaultToolkit().getSystemClipboard();  
				StringSelection select = new StringSelection(columns.toString() + "\n" + content.toString()); 
				keyboard.setContents(select, null);  
			}
			@Override public void mouseEntered(MouseEvent arg0) { }
			@Override public void mouseExited(MouseEvent arg0) { }
			@Override public void mousePressed(MouseEvent arg0) { }
			@Override public void mouseReleased(MouseEvent arg0) { }
		});
		gbc.gridx = 5;
		area_1.add(clipboard, gbc);
		
		
		Dimension dim_space = new Dimension(350, 24);
		_active = new JLabel("<html>Obtendo dados para preencher a lista, <i>aguarde</i>...</html>");
		_active.setSize(dim_space);
		_active.setPreferredSize(dim_space);
		_active.setMaximumSize(dim_space);
		_active.setMinimumSize(dim_space);
		_active.setHorizontalTextPosition(JLabel.RIGHT);
		_active.setHorizontalAlignment(JLabel.RIGHT);
		gbc.gridx = 6;
		area_1.add(_active, gbc);
		
			
		final ImageIcon ico_state_0 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
		ico_state_0.setImage(ico_state_0.getImage().getScaledInstance(24, 24, 100));
		final ImageIcon ico_state_1 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
		ico_state_1.setImage(ico_state_1.getImage().getScaledInstance(24, 24, 100));
		final ImageIcon ico_state_2 = new ImageIcon(ClassLoader.getSystemResource("selected.png"));
		ico_state_2.setImage(ico_state_2.getImage().getScaledInstance(24, 24, 100));
		
		
		_LIST = new JTable();
		_LIST.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_LIST.setAutoscrolls(true);
		_LIST.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 10));
		_LIST.setGridColor(Color.WHITE);
		_LIST.setShowGrid(true);
		_LIST.setSelectionForeground(Color.WHITE);
		_LIST.setSelectionBackground(Color.DARK_GRAY);
		_LIST.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 3407711122579968156L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JLabel root = new JLabel();
				root.setOpaque(true);
				root.setFont(_LIST.getFont());
				boolean pass;
				if (isShowing()) { return this; }
				JTableModel model = (JTableModel)_LIST.getModel();
				
				if (column == 0) {
					switch (model != null ? model.getState(row) : 3) {
						case 0:
							root.setIcon(ico_state_0);
							break;
						case 1:
							root.setIcon(ico_state_1);
							break;
						case 2:
							root.setIcon(ico_state_2);
							break;
					}						
				}
				else {
					root.setText(value != null ? " " + value.toString() : " ");
					root.setForeground(Color.BLACK);
				}
				for (int i = table.getRowCount();i > row; i--) {
					pass = true;
					root.setVerticalAlignment(JLabel.CENTER);
					for (int j = 0; j < table.getSelectedRows().length; j++) {
						if (row == table.getSelectedRows()[j]) {
							root.setForeground(_LIST.getSelectionForeground());
							root.setBackground(_LIST.getSelectionBackground());							
							pass = false;
						}
					}
					if (!pass) { continue; }
					if ((row % 2) == 0) { root.setBackground(new Color(220,220,220)); }
					else { root.setBackground(new Color(240,240,240)); }
				}

				switch (model != null ? model.getState(row) : 3) {
					case 1:
						root.setForeground(new Color(0,160,0));
						break;
					case 2:
						root.setForeground(new Color(160,0,0));
						break;
				}			

				return root;
			}
		});
		_LIST.addKeyListener(new KeyListener(){
			@Override public void keyPressed(KeyEvent ke) {
				if (ke != null && (ke.getKeyChar() == ' ' || ke.getKeyChar() == '+' || ke.getKeyChar() == '-')) {
					JTableModel model = (JTableModel)_LIST.getModel();
					int state = -1;
					int option = -1;
					option = ke.getKeyChar() == '-' ? 0 : option;
					option = ke.getKeyChar() == '+' ? 1 : option;
					for (int i = 0; i < _LIST.getSelectedRowCount(); i++) {
						if (model.getState(_LIST.getSelectedRows()[i]) > 1) { continue; }
						if (state == -1) {
							state = (model.getState(_LIST.getSelectedRows()[i]) == 0 ? 1 : 0);
						}
						model.setState(_LIST.getSelectedRows()[i], option >= 0 ? option : state);
					}
					if (option >= 0) {
						_LIST.setRowSelectionInterval(Math.min(_LIST.getRowCount() - 1, _LIST.getSelectedRow() + 1), Math.min(_LIST.getRowCount() - 1, _LIST.getSelectedRow() + 1));
					}
					_LIST.repaint();
				}
			}
			@Override public void keyReleased(KeyEvent ke) { }
			@Override public void keyTyped(KeyEvent arg0) { }
		});
		_LIST.addMouseListener(new MouseListener(){
			@Override public void mouseClicked(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseReleased(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() > 1) {
					select(-1);
				}
				else if (e.getButton() > 1) {
					JTableModel model = (JTableModel)_LIST.getModel();
					JPopupMenu menu = new JPopupMenu();
					final int option = (model.getState(_LIST.getSelectedRow()) == 0 ? 1 : 0);
					JMenuItem item = new JMenuItem((option == 0 ? "Desmarcar" : "Marcar") + " usuários  selecionados como alvos de replicação");
					item.addActionListener(new ActionListener(){
						@Override public void actionPerformed(ActionEvent arg0) {
							select(option);
						}
					});
					menu.add(item);
					menu.setVisible(true);
					menu.show(_LIST, e.getX(), e.getY());
				}
			}
			private void select(int option) {
				JTableModel model = (JTableModel)_LIST.getModel();
				int state = -1;
				for (int i = 0; i < _LIST.getSelectedRowCount(); i++) {
					if (model.getState(_LIST.getSelectedRows()[i]) > 1) { continue; }
					if (state == -1) {
						state = (model.getState(_LIST.getSelectedRows()[i]) == 0 ? 1 : 0);
					}
					model.setState(_LIST.getSelectedRows()[i], option >= 0 ? option : state);
				}
				_LIST.repaint();
			}
		});
		JScrollPane scroll_01 = new JScrollPane(_LIST);
		scroll_01.setBounds(10, 56, 480, 220);
		scroll_01.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		_DIALOG.add(scroll_01);
		
		JPanel area_2 = new JPanel();		
		area_2.setOpaque(true);
		area_2.setBounds(7, 285, 485, 90);
		area_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), " Opções de replicação ", 1, 0, this._font_title, this._color_title));
		area_2.setLayout(null);
		_DIALOG.add(area_2);
		
		
		
		final JRadioButton recursos = new JRadioButton("Replicar rec. gerais do usuario principal para os usuarios selecionados");
		recursos.setBounds(10, 20, 450, 15);
		recursos.setOpaque(false);
		recursos.setFont(_font_title);
		recursos.setSelected(true);

		area_2.add(recursos);
		
		final JRadioButton fundir = new JRadioButton("Adicionar aos rec. dos usuários selecionados os rec. do usuário principal");
		fundir.setBounds(10, 40, 450, 15);
		fundir.setOpaque(false);
		fundir.setFont(_font_title);
		fundir.setSelected(false);
		area_2.add(fundir);
		fundir.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				recursos.setSelected(!fundir.isSelected());
			}			
		});
		recursos.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				fundir.setSelected(!recursos.isSelected());
			}			
		});
		
		final JCheckBox campoXML = new JCheckBox("Replicar recursos XML do usuário principal");
		campoXML.setBounds(10, 60, 300, 15);
		campoXML.setOpaque(false);
		campoXML.setFont(_font_title);
		area_2.add(campoXML);
		
		final JButton replicar = new JButton("Replicar");
		replicar.setBounds(250, 380, 135, 35);
		replicar.setFont(_font_title);
		replicar.setEnabled(false);
		replicar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTableModel model = (JTableModel)_LIST.getModel();
				int reference = 0;
				int replicate = 0;
				String sql_ref = "";
				String sql_rep = "";
				FastList<String> sql_merge = new FastList<String>();
				//String sql = "UPDATE usuarios SET recursos=%REF% WHERE %REP%";
				Exception fail = null;
				try {
					ResultSet rs = null;
					byte[] recursos_a = null;
					byte[] recursos_b = null;
					byte[] recursos_c = null;
					for (int row = 0; row < model.getRowCount(); row++) {
						if (model.getState(row) == 2) {
							rs = _CONNECTION.executeQuery("SELECT recursos, camposxml FROM usuarios WHERE recno=" + model.getValueAt(row, 6));

							while (rs.next()) {
								if (recursos.isSelected() || fundir.isSelected()) {
									recursos_a = rs.getString(1).getBytes(_CONNECTION.getVariable("character_set_connection"));
									sql_ref += "recursos='" + MainWindow.getSQLSafeString(rs.getString(1) != null ? rs.getString(1) : rs.getString(1)).trim() + "'";
								}
								if (campoXML.isSelected()) {
									sql_ref += (sql_ref.isEmpty() ? "" : ", ") + "camposxml='" + MainWindow.getSQLSafeString(rs.getString(2) != null ? rs.getString(2) : rs.getString(2)).trim() + "'";
								}
							}
							if (rs != null) { rs.close(); }
							++reference;
							
						}
					}
					for (int row = 0; row < model.getRowCount(); row++) {
						if (model.getState(row) == 1) {
							sql_rep += (sql_rep.isEmpty() ? "" : " OR ") + "recno=" + model.getValueAt(row, 6);
							
							rs = _CONNECTION.executeQuery("SELECT recursos FROM usuarios WHERE recno=" + model.getValueAt(row, 6));

							while (rs.next()) {
								if (recursos.isSelected()) {
									recursos_c = recursos_a;
								}
								else {
									recursos_b = rs.getString(1).getBytes(_CONNECTION.getVariable("character_set_connection"));
									recursos_c = new byte[Math.max(recursos_a.length, recursos_b.length)];
									for (int i = 0; i < recursos_c.length; i++) {
										recursos_c[i] = bitMerge((i < recursos_a.length ? recursos_a[i] : 0x00), (i < recursos_b.length ? recursos_b[i] : 0x00));
									}
								}
								sql_merge.add("UPDATE usuarios SET recursos='" + MainWindow.getSQLSafeString(new String(recursos_c, _CONNECTION.getVariable("character_set_connection"))) + "' WHERE recno=" + model.getValueAt(row, 6));
							}
							if (rs != null) { rs.close(); }
												
							++replicate;
						}
					}
					if (reference > 0 && replicate > 0) {
						
						if (JOptionPane.showConfirmDialog(null, "<html>Você está tentando " + (recursos.isSelected() ? "replicar" : "mesclar") + " recursos para <b>" + replicate + "</b> usuário(s).<br><br><font color=red>Tem certeza que deseja prosseguir?</font><br><br><font color=blue><b>Lembre-se que esse procedimento é irreversível e que um backup deve ser feito antes de qualquer alteração desta natureza!</b></font></html>", "JQueryAnalizer - Replicação/Mescla de recursos", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
							return;
						}
						
						if (recursos.isSelected()) {
							fail = _CONNECTION.executeUpdate("UPDATE usuarios SET " + sql_ref + " WHERE " + sql_rep);
							if (fail != null) {
								System.out.println("Falha ao replicar recursos do usuário selecionado (" + reference + ") para os usuarios replicados (" + replicate + ")\n" + fail.getMessage());
							}
							else {
								Browser b = new Browser();
								Thread t = new Thread(b);
								t.start();
								JOptionPane.showMessageDialog(_DIALOG, "<html>Replicação de recursos concluida com sucesso para <b>" + replicate + "</b> usuários</html>", "JQueryAnalizer - Procedimento concluído", JOptionPane.OK_OPTION);
							}
						}
						else {
							int count_error = 0;
							Iterator<String> it = sql_merge.iterator();
							System.out.println(sql_merge.size());
							String sql = null;
							while (it.hasNext()) {
								sql = it.next();
								fail = _CONNECTION.executeUpdate(sql);
								if (fail != null) {
									++count_error;
									System.out.println("Falha ao fundir recursos do usuário selecionado (" + reference + ") para os usuarios replicados (" + replicate + ")\n" + fail.getMessage());
								}
							}
							
							if (count_error == 0) {
								Browser b = new Browser();
								Thread t = new Thread(b);
								t.start();
								JOptionPane.showMessageDialog(_DIALOG, "<html>Mescla de recursos concluida com sucesso para <b>" + replicate + "</b> usuários</html>", "JQueryAnalizer - Procedimento concluído com sucesso!", JOptionPane.OK_OPTION);								
							}
							else {
								JOptionPane.showMessageDialog(_DIALOG, "<html>Ocorreram <b>" + count_error + "</b> durante a mescla de recursos.</html>", "JQueryAnalizer - Procedimento concluído com falha!", JOptionPane.OK_OPTION);
							}
						}
						
					}
					else {
						if (reference == 0) {
							JOptionPane.showMessageDialog(_DIALOG, "<html>Não há usuário selecionado como <b>referência</b> para a replicação dos recursos!</html>", "JQueryAnalizer - Procedimento interrompido", JOptionPane.OK_OPTION);
							return;
						}
						if (replicate == 0) {
							JOptionPane.showMessageDialog(_DIALOG, "<html>Não há usuário(s) selecionado(s) como <b>alvos</b> para a replicação dos recursos!<br><i>É necessário que haja ao menos 1 (um) usuário como alvo da replicação de recursos</i></html>", "JQueryAnalizer - Procedimento interrompido", JOptionPane.OK_OPTION);
							return;
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		_DIALOG.add(replicar);
		
		JButton sair = new JButton("Sair");
		sair.setBounds(390, 380, 100, 35);
		sair.setFont(_font_title);
		sair.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent act) {
				_DIALOG.dispose();
			}
			
		});
		
		final ActionListener action = new ActionListener(){
			@Override public void actionPerformed(ActionEvent ae) {
				boolean ref = false;
				boolean rep = false;
				JTableModel model = (JTableModel)_LIST.getModel();
				for (int row = 0; row < model.getRowCount(); row++) {
					if (model.getState(row) == 2) { ref = true; }
					if (model.getState(row) == 1) { rep = true; }
				}
				replicar.setEnabled(ref && rep && (recursos.isSelected() || fundir.isSelected() || campoXML.isSelected())); 
			}
		};
		final KeyListener key = new KeyListener() {
			@Override public void keyPressed(KeyEvent arg0) { }
			@Override public void keyReleased(KeyEvent arg0) { action.actionPerformed(null); }
			@Override public void keyTyped(KeyEvent arg0) { }
		};
		final MouseListener mouse = new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseReleased(MouseEvent e) { }
		};
		recursos.addActionListener(action);
		campoXML.addActionListener(action);
		select.addMouseListener(mouse);
		_LIST.addKeyListener(key);
		_LIST.addMouseListener(mouse);
		_DIALOG.add(sair);
	}
	
	public class Browser implements Runnable {
		public Browser () {}
		public void run() {
		
		String[] cols = {"","Usuário", "Departamento", "Situação", "MD5(Recursos)", "MD5[CamposXML]", "Recno"};
		String[][] data = null;
		try {
			ResultSet rs = _CONNECTION.executeQuery("SELECT recno FROM usuarios");
			int row_cnt = 0;
			while (rs.next()) {
				++row_cnt;
			}
			rs.close();
			rs = _CONNECTION.executeQuery("SELECT '', usuario, depto, bloqueado, (recursos) AS 'MD5(Recursos)', (camposxml) AS 'MD5(CamposXML)', recno FROM usuarios ORDER BY depto, usuario, bloqueado, recursos");
			//rs.last();
			//rs.beforeFirst();
			data = new String[row_cnt][cols.length];
			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < cols.length; j++) {
					switch (j) {
						case 3:
							data[i][j] = (rs.getBoolean(j + 1) ? "Desativado" : "Ativo");
							break;
						case 4:
						case 5:
							data[i][j] = MainWindow.md5(rs.getString(j + 1));//.toUpperCase();
							break;
						default:
							data[i][j] = rs.getString(j + 1);
					}
				}
			}
			rs.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		JTableModel model = new JTableModel(cols, data);
		FontMetrics font_metrics = _LIST.getFontMetrics(_LIST.getFont());
		_LIST.setModel(model);
		_LIST.setRowHeight(font_metrics.getHeight() + 6);
		int[] column_width = new int[model.getColumnCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				column_width[j] = Math.max(font_metrics.stringWidth(model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString() : "") + 10, column_width[j]);
			}
		}
		for (int i = 0; i < _LIST.getColumnModel().getColumnCount(); i++) {
			if (_LIST.getColumnModel().getColumn(i) != null) {
				_LIST.getColumnModel().getColumn(i).setWidth(i == 0 ? 25 : column_width[i]);
				_LIST.getColumnModel().getColumn(i).setMinWidth(i == 0 ? 25 : column_width[i]);
				_LIST.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 25 : column_width[i]);
			}
		}
		_LIST.revalidate();
		_LIST.repaint();
		_active.setText("<html>Lista carregada com <b>" + model.getRowCount() + "</b> usuários!</html>");
		}
	}
	
	public void toogleVisible() {
		if (_DIALOG != null) {
			_DIALOG.setVisible(!_DIALOG.isVisible());
		}
	}
	
	public void hide() {
		if (_DIALOG != null) {
			_DIALOG.setVisible(false);
		}
	}
	
	public void show() {
		if (_DIALOG != null) {
			if (_CONNECTION == null || (_CONNECTION != null && (!_CONNECTION.isConnected() || !_CONNECTION.isDatabaseSelected()))) {
				JOptionPane.showMessageDialog(MainWindow.getMainFrame(), "Você deve selecionar uma database do publi antes de executar este programa!", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
				return;
			}
			Browser b = new Browser();
			Thread t = new Thread(b);
			t.start();
			if (!_DIALOG.isVisible()) {
				_DIALOG.setVisible(true);
			}
		}
	}
	
	public void destroy() {
		if (_DIALOG != null) {
			_DIALOG.setVisible(false);
			_DIALOG.dispose();
			_DIALOG = null;
		}
	}
	
	
	private class JTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -2173685424567032936L;
		
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
		
		public int getState(int row) {
			if (row < 0 || row > columnState.length) {
				return -1;
			}
			else {
				return columnState[row];
			}
		}
		
		public void setState(int row, int state) {
			columnState[row] = (short)state;
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
        
        @SuppressWarnings("unused")
		public String[][] getData() {
        	return (String[][])this.data;
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

        
        public boolean isCellEditable(int row, int col) {
        	if (getValueAt(row, col) instanceof String) {
        		String value = (String)getValueAt(row, col);
        		if (value != null && value.split("<br>").length > 1) return true; 
        	}
        	return false;
        }
	}
	
	private class JBracket extends JLabel {

		private static final long serialVersionUID = -2891376662698902830L;

		public JBracket(int w, int h) {
			this.setSize(w, h);
			this.setPreferredSize(this.getSize());
			this.setMinimumSize(this.getSize());
			this.setMaximumSize(this.getSize());
			this.setOpaque(true);
			this.setBackground(Color.GRAY);
		}
	}
	
	private byte bitMerge(byte a, byte b) {
		String bit_a = Integer.toString(a, 2);
		String bit_b = Integer.toString(b, 2);
		String merge = "";
		byte value_a = 0;
		byte value_b = 0;
		
		for (byte i = 0; i < Math.max(bit_a.length(), bit_b.length()); i++) {
			value_a = (byte)(i < bit_a.length() && bit_a.charAt(i) == '1' ? 1 : 0); 
			value_b = (byte)(i < bit_a.length() && bit_a.charAt(i) == '1' ? 1 : 0);
			merge += (value_a == 1 || value_b == 1 ? '1' : '0');
		}
		return (byte)(int)Integer.valueOf(merge, Character.MIN_RADIX);
	}
	 
}
