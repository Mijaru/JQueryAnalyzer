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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.Models.JTableModel;

public class JPefformacePane extends JTabPanel {
		private static final long serialVersionUID = 1207142220113990255L;
		private SQLConnectionManager _CONNECTION;
		private JParametersPanel _PARAMETERS;
		
		// ------------------------------------------------------
		private JLabel _cel_b;
		private JPanel _cel_a;
		private JTable _table_c;
		
		private List<TablePefformaceData> _default_list_data;
		
		private int _current_row;
		private boolean _monitor_status;
		private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
		private JTable _graphic_table_list;
		private JLabel _graphic_label;
		private JScrollPane _graphic_scroll;
		private JCheckBox _graphic_auto_scroll;
		private JSlider _graphic_x_scale;
		private JFrame _graphic_dialog;
		private Color[] _gradiente_cores; 
		
		public JPefformacePane() {
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

			JCheckButton bt_info       = new JCheckButton(ClassLoader.getSystemResource("data_info.png"), new Dimension(36, 36),"bt_database_info");
	        JCheckButton bt_default    = new JCheckButton(ClassLoader.getSystemResource("monitor_default.png"), new Dimension(36, 36),"bt_monitor_default");
	        JCheckButton bt_load       = new JCheckButton(ClassLoader.getSystemResource("monitor_abrir_log.png"), new Dimension(36, 36),"bt_monitor_import_log");
	        JCheckButton bt_start      = new JCheckButton(ClassLoader.getSystemResource("monitor_start.png"), new Dimension(36, 36),"bt_monitor_start");
	        JCheckButton bt_pause      = new JCheckButton(ClassLoader.getSystemResource("monitor_pause.png"), new Dimension(36, 36),"bt_monitor_pause");
	        JCheckButton bt_stop       = new JCheckButton(ClassLoader.getSystemResource("monitor_stop.png"), new Dimension(36, 36),"bt_monitor_stop");
	        JCheckButton bt_graph      = new JCheckButton(ClassLoader.getSystemResource("summary.png"), new Dimension(36, 36),"bt_monitor_summary");
	        JCheckButton bt_finally    = new JCheckButton(ClassLoader.getSystemResource("graphic.png"), new Dimension(36, 36),"bt_monitor_graphic");

	        _cel_a.add(bt_default);
	        _cel_a.add(bt_load);
	        _cel_a.add(new JBraketButton(32));
	        _cel_a.add(bt_start);
	        _cel_a.add(bt_pause);
	        _cel_a.add(bt_stop);
	        _cel_a.add(new JBraketButton(32));
	        _cel_a.add(bt_info);
	        _cel_a.add(bt_graph);
	        _cel_a.add(bt_finally);
	        _cel_a.add(new JBraketButton(32));
	        _cel_a.setBorder(MainWindow.border_left);
	        
			// -menu de navegação- celula B
			// --------------------------------------------------------------------------------------------------------------------
	        main_table_def.insets = new Insets(5,0,0,5);
	        main_table_def.gridx = 2;
	        main_table_def.gridy = 1;
	        main_table_def.weightx = 1;
			
			_cel_b = new JLabel("<html><b>Status do monitoramento</b></html>");
			_cel_b.setToolTipText("teste");
			
			//_cel_b.getToo
			_cel_b.setOpaque(true);
			_cel_b.setBackground(new Color(200,200,200));
			_cel_b.setFont(new Font("Tahoma", Font.PLAIN, 10));
			_cel_b.setBorder(MainWindow.border_right);
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
			cel_c.setLayout(new GridBagLayout());			


			/* -#Celulas C1 e C2#- */
			GridBagConstraints cel_c_def = new GridBagConstraints();
			cel_c_def.fill = GridBagConstraints.BOTH;
			cel_c_def.gridx = 1;
			cel_c_def.gridy = 1;
			cel_c_def.weightx = 1.d;
			cel_c_def.weighty = 1.d;
			
			
			
			DefaultTableCellRenderer table_render = new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 6234287599035670995L;

				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					boolean pass;
					if (isShowing()) { return this; }
					for (int i = table.getRowCount();i > row; i--) {
						pass = true;
						this.setVerticalAlignment(JLabel.TOP);
						for (int j = 0; j < table.getSelectedRows().length; j++) {
							if (row == table.getSelectedRows()[j]) {
								setBackground(Color.DARK_GRAY);
								pass = false;
							}
						}
						if (!pass) { continue; }
						if ((row % 2) == 0) { setBackground(new Color(220,220,220)); }
						else { setBackground(new Color(240,240,240)); }
					}
					this.setText(" " + (value == null ? "" : value));
					return this;
				}
			};
			
			_table_c = new JTable();
			_table_c.setGridColor(Color.WHITE);
			_table_c.setShowGrid(true);
			_table_c.setRowHeight(22);
			_table_c.setDefaultRenderer(Object.class, table_render);
			_table_c.setDefaultEditor(String.class, new JCellEditor(new JTextField()));
			_table_c.setSelectionForeground(Color.WHITE);
			_table_c.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			_table_c.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			JScrollPane bar_c = new JScrollPane(_table_c);
			bar_c.setSize(new Dimension(1,1));
			bar_c.setPreferredSize(new Dimension(1,1));
			bar_c.setAutoscrolls(true);
			cel_c.add(bar_c, cel_c_def);
			
			this.add(cel_c, main_table_def);
			
			initSubmenuListeners();
			toogleEnabled(false);
			
			_gradiente_cores = new Color[100];
			for (int i = 0; i < 100; i++) {
				if (i < 33) {
					// i = 0  => 100% azul & 0% verde
					// 1 = 33 => 0% azul & 100% verde
					_gradiente_cores[i] = new Color(0,(33 - i) * 255 / 33, i * 255 / 33);
				}
				else if (i < 66) {
					// i = 33 => 100% verde & 0% amarelo
					// 1 = 33 => 0% azul & 100% verde
					//_gradiente_cores[i] = new Color(0,(33 - i) * 255 / 33, i * 255 / 33);
				}
				else {
					// i = 33 => 100% verde & 0% amarelo
					// 1 = 33 => 0% azul & 100% verde
					//_gradiente_cores[i] = new Color((66 - (33 - i)) * 33 / 255, (66 - i) * 33 / 255, 0);
				}
			}
		}
		
		private void toogleEnabled(boolean enable) {
			for (Component c :_cel_a.getComponents()) {
				if (c instanceof JCheckButton) {
					JCheckButton button = (JCheckButton)c;
					if (button.getName() != null && (
						button.getName().equalsIgnoreCase("bt_monitor_start")   ||
						button.getName().equalsIgnoreCase("bt_monitor_pause")   ||
						button.getName().equalsIgnoreCase("bt_monitor_stop")    ||
						button.getName().equalsIgnoreCase("bt_monitor_summary") ||
						button.getName().equalsIgnoreCase("bt_monitor_graphic") ||
						button.getName().equalsIgnoreCase("bt_database_info")
					)) {
						button.setEnabled(enable);
					}
				}
			}
		}
		
		private void initSubmenuListeners() {
			ActionListener submenu_action_listener = new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					if (event != null && event.getSource() != null && event.getSource() instanceof JCheckButton) {
						JCheckButton button = (JCheckButton)event.getSource();
						if (button.getName() == null) {
							return;
						}
						else if (button.getName().equalsIgnoreCase("bt_monitor_default")) {
							Thread t = new Thread(new MountPefformaceDefault());
							t.start();
						}
						else if (button.getName().equalsIgnoreCase("bt_monitor_import_log")) {
							Thread t = new Thread(new MountPefformaceListByLog());
							t.start();
						}
						else if (button.getName().equalsIgnoreCase("bt_monitor_start")) {
							if (_default_list_data == null) {
								JOptionPane.showMessageDialog(null, "<html>O sistema não está pronto para iniciar a monitoração.<br>Escolha um profile antes de iniciar a monitoração novamente.</html>", "JQueryAnalizer - Aviso", JOptionPane.OK_OPTION);
								return;
							}
							_monitor_status = true;
							Thread t = new Thread(new doPefformaceTest(_current_row));
							t.start();
						}
						else if (button.getName().equalsIgnoreCase("bt_monitor_pause")) {
							_monitor_status = false;
						}
						else if (button.getName().equalsIgnoreCase("bt_monitor_stop")) {
							if (!_monitor_status || (_monitor_status && JOptionPane.showConfirmDialog(null, "Deseja realmente interromper este monitoramento?", "JQueryAnalizer - Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
								_monitor_status = false;
								_default_list_data = null;
								toogleEnabled(false);
							}
						}
						else if (button.getName().equalsIgnoreCase("bt_monitor_graphic")) {
							prepareGraphicWindow();
						}
						
					}
				}
			};
			
			for (Component c : _cel_a.getComponents()) {
				if (c instanceof JCheckButton) {
					((JCheckButton)c).addActionListener(submenu_action_listener);
				}
			}
			
			TableColumnModel column_model = _table_c.getColumnModel();
			column_model.setColumnMargin(1);
			column_model.setColumnSelectionAllowed(false);
			TableColumn column = null;
			for (int i = 0; i < column_model.getColumnCount(); i++) {
				column = column_model.getColumn(i);
				switch(i) {
					case 1:
						column.setCellEditor(new JCellEditor(new JTextField()));
						break;
				}
			}
		}
		
		
		private void prepareGraphicWindow() {
			final ImageIcon ico_state_0 = new ImageIcon(ClassLoader.getSystemResource("unchecked.png"));
			ico_state_0.setImage(ico_state_0.getImage().getScaledInstance(24, 24, Image.SCALE_AREA_AVERAGING));
			final ImageIcon ico_state_1 = new ImageIcon(ClassLoader.getSystemResource("checked.png"));
			ico_state_1.setImage(ico_state_1.getImage().getScaledInstance(24, 24, Image.SCALE_AREA_AVERAGING));
			final ImageIcon ico_state_2 = new ImageIcon(ClassLoader.getSystemResource("selected.png"));
			ico_state_2.setImage(ico_state_2.getImage().getScaledInstance(24, 24, Image.SCALE_AREA_AVERAGING));
			
			
			//JQDialog dialog = new JQDialog(new JFrame(), "JQueryAnalizer - Gráficos de desempenho");
			_graphic_dialog = new JFrame();
			Dimension size = new Dimension(367,520);
			_graphic_dialog.setSize(size);
			_graphic_dialog.setPreferredSize(size);
			_graphic_dialog.setMaximumSize(size);
			_graphic_dialog.setMinimumSize(size);
			_graphic_dialog.setLayout(null);
			_graphic_dialog.setResizable(false);
			_graphic_dialog.setLocationRelativeTo(null);
			_graphic_dialog.setDefaultCloseOperation(JQDialog.DISPOSE_ON_CLOSE);
			
			JLabel text1 = new JLabel("Selecione as tabelas:");
			text1.setBounds(5,5,150,15);
			text1.setFont(_default_font);
			text1.setForeground(Color.DARK_GRAY);
			_graphic_dialog.add(text1);
			
			_graphic_table_list = new JTable();
			_graphic_table_list.setFont(_default_font);
			_graphic_table_list.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			_graphic_table_list.setAutoscrolls(true);
			_graphic_table_list.setGridColor(Color.WHITE);
			_graphic_table_list.setShowGrid(true);
			_graphic_table_list.setSelectionForeground(Color.WHITE);
			_graphic_table_list.setSelectionBackground(Color.DARK_GRAY);
			_graphic_table_list.setRowMargin(1);
			_graphic_table_list.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

				private static final long serialVersionUID = 3407711122579968156L;

				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					JLabel root = new JLabel();
					root.setOpaque(true);
					root.setFont(_graphic_table_list.getFont());
					boolean pass;
					if (isShowing()) { return this; }
					JTableModel model = (JTableModel)_graphic_table_list.getModel();
					
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
								root.setForeground(_graphic_table_list.getSelectionForeground());
								root.setBackground(_graphic_table_list.getSelectionBackground());							
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
			
			_graphic_label = new JLabel();
			_graphic_label.setOpaque(true);
			
			_graphic_table_list.addKeyListener(new KeyListener(){
				@Override public void keyPressed(KeyEvent ke) {
					if (ke != null && (ke.getKeyChar() == ' ' || ke.getKeyChar() == '+' || ke.getKeyChar() == '-')) {
						JTableModel model = (JTableModel)_graphic_table_list.getModel();
						int state = -1;
						int option = -1;
						option = ke.getKeyChar() == '-' ? 0 : option;
						option = ke.getKeyChar() == '+' ? 1 : option;
						for (int i = 0; i < _graphic_table_list.getSelectedRowCount(); i++) {
							if (model.getState(_graphic_table_list.getSelectedRows()[i]) > 1) { continue; }
							if (state == -1) {
								state = (model.getState(_graphic_table_list.getSelectedRows()[i]) == 0 ? 1 : 0);
							}
							model.setState(_graphic_table_list.getSelectedRows()[i], option >= 0 ? option : state);
						}
						drawGraphic(_graphic_label, drawGraphicSelectedList(model));
						_graphic_label.repaint();
						if (option >= 0) {
							_graphic_table_list.setRowSelectionInterval(Math.min(_graphic_table_list.getRowCount() - 1, _graphic_table_list.getSelectedRow() + 1), Math.min(_graphic_table_list.getRowCount() - 1, _graphic_table_list.getSelectedRow() + 1));
						}
						_graphic_table_list.repaint();
					}
				}
				@Override public void keyReleased(KeyEvent ke) { }
				@Override public void keyTyped(KeyEvent arg0) { }
			});
			_graphic_table_list.addMouseListener(new MouseListener(){
				@Override public void mouseClicked(MouseEvent e) { }
				@Override public void mouseEntered(MouseEvent e) { }
				@Override public void mouseExited(MouseEvent e) { }
				@Override public void mousePressed(MouseEvent e) { }
				@Override public void mouseReleased(MouseEvent e) {
					if (e.getButton() == 1 && e.getClickCount() > 1) {
						select(-1);
					}
					
				}
				private void select(int option) {
					JTableModel model = (JTableModel)_graphic_table_list.getModel();
					int state = -1;
					for (int i = 0; i < _graphic_table_list.getSelectedRowCount(); i++) {
						if (model.getState(_graphic_table_list.getSelectedRows()[i]) > 1) { continue; }
						if (state == -1) {
							state = (model.getState(_graphic_table_list.getSelectedRows()[i]) == 0 ? 1 : 0);
						}
						model.setState(_graphic_table_list.getSelectedRows()[i], option >= 0 ? option : state);
					}
					_graphic_table_list.repaint();
				}
			});

			
			JScrollPane scroll1 = new JScrollPane(_graphic_table_list);
			scroll1.setBounds(5,20,350,150);
			scroll1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			_graphic_dialog.add(scroll1);
			
			
			JLabel text2 = new JLabel("Gráfico do desempenho das tabelas selecionadas:");
			text2.setBounds(5,175,300,15);
			text2.setFont(_default_font);
			text2.setForeground(Color.DARK_GRAY);
			_graphic_dialog.add(text2);
			
			
			
			_graphic_scroll = new JScrollPane(_graphic_label);
			_graphic_label.setVerticalAlignment(JLabel.TOP);
			_graphic_scroll.setBounds(5,190,350,250);
			_graphic_scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			_graphic_dialog.add(_graphic_scroll);
			
			_graphic_auto_scroll = new JCheckBox("Rolagem automática");
			_graphic_auto_scroll.setFont(_default_font);
			_graphic_auto_scroll.setBounds(7,450,200,15);
			_graphic_dialog.add(_graphic_auto_scroll);
			
			_graphic_x_scale = new JSlider(1,20);
			_graphic_x_scale.setBounds(7,468,145,21);
			_graphic_x_scale.setValue(3);
			_graphic_x_scale.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent event) {
					JTableModel model = (JTableModel)_graphic_table_list.getModel();
					drawGraphic(_graphic_label, drawGraphicSelectedList(model));
				}
			});
			_graphic_dialog.add(_graphic_x_scale);

			initGraphicTableList();
			
			_graphic_dialog.setVisible(true);
		}
		
		private void initGraphicTableList() {
			// -- inicializa a tabela com 
			if (_table_c.getModel() != null && _graphic_dialog != null) {
				PefformaceTableModel model_ref = (PefformaceTableModel)_table_c.getModel();
				Object[][] data_ref = model_ref.getData();
				String[] col = {"","Tabela", "<html>T<sub>max</sub></html>", "<html>T<sub>med</sub></html>", "<html>T<sub>min</sub></html>", "Amostras"};
				String[][] data = new String[model_ref.getRowCount()][model_ref.getColumnCount()];
				for (int i = 0; i < data.length; i++) {
					data[i][0] = "";
					data[i][1] = (data_ref[i][1] != null ? data_ref[i][0].toString() : "");
					data[i][2] = (data_ref[i][6] != null ? data_ref[i][6].toString() : "");
					data[i][3] = (data_ref[i][7] != null ? data_ref[i][7].toString() : "");
					data[i][4] = (data_ref[i][8] != null ? data_ref[i][8].toString() : "");
					data[i][5] = (data_ref[i][9] != null ? data_ref[i][9].toString() : "");
				}
				JTableModel model_des = new JTableModel(col, data);
				_graphic_table_list.setModel(model_des);

				// -- atualizando o tamanho das colunas e linhas.
				int[] col_width = {16,90,35,35,35,60};
				for (int i = 0; i < data.length; i++) {
					_graphic_table_list.setRowHeight(i, 21);
				}
				for (int j = 0; j < col_width.length; j++) {
					_graphic_table_list.getColumnModel().getColumn(j).setWidth(10 + col_width[j]);
					_graphic_table_list.getColumnModel().getColumn(j).setMinWidth(10 + col_width[j]);
					_graphic_table_list.getColumnModel().getColumn(j).setPreferredWidth(10 + col_width[j]);
					_graphic_table_list.getColumnModel().setColumnMargin(1);
				}
				_graphic_table_list.repaint();
				drawGraphic(_graphic_label, drawGraphicSelectedList(model_des));
			}
		}
		
		private List<TablePefformaceData> drawGraphicSelectedList(JTableModel model) {
			if (model != null && _default_list_data != null) {
				Object[][] data = model.getData();
				List<TablePefformaceData> list = new ArrayList<TablePefformaceData>();
				for (int i = 0; i < data.length; i++) {
					if (model.getState(i) > 0) {
						for (TablePefformaceData item : _default_list_data) {
							if (item._table != null && data[i][0] != null && item._table.equalsIgnoreCase(data[i][1].toString())) {
								list.add(item);								
							}
						}
					}
				}
				return list;
			}
			return null;
		}
		
		private void drawGraphic(JLabel c, List<TablePefformaceData> list) {
			if (c == null || c.getGraphics() == null || list == null) return;
			Graphics g = c.getGraphics();
			int w = 0;
			int h = 228;
			// -- escala X ~> cada segundo corresponde a 3 pixels.
			// -- escala Y ~> cada ms corresponde a (400 / amplitude_y)
			double x = -1;
			double y = -1;
			//int count = 0;
			long t1 = -1;
			long t2 = -1;
			// -- obtem as escalas de medida...
			//    x = min, y = max
			for (TablePefformaceData data : list) {
				if (data._delays != null) {
					TreeSet<Long> keyMap = new TreeSet<Long>(data._delays.keySet());
					for (Long key : keyMap) {
						Integer delay = data._delays.get(key);
						if (t1 == -1 || t1 > key) t1 = key;
						if (t2 == -1 || t2 < key) t2 = key;
						if (x == -1 || x > delay) x = delay;
						if (y == -1 || y < delay) y = delay;
						//++count;
					}
				}
			}
			y = h / Math.abs(x - y);	     // pixels/ms
			x = _graphic_x_scale.getValue(); // pixels/s
			
			
			// -- ajusta as dimensoes da imagem responsavel pelo grafico e desenha o grid no fundo.
			w = Math.max((int)(x * (t2 - t1) / 1000), 348);
			
			BufferedImage image = new BufferedImage(w, h + 2, BufferedImage.TYPE_INT_RGB);
			Graphics g1 = image.getGraphics();
			g1.setColor(Color.WHITE);
			g1.fillRect(0, 0, w, h + 2);
			g1.setFont(_default_font);
			float grid = 0;
			
			int grid_x = 5;
			int grid_y = (w / 120);
			for (int i = 0; i <= grid_x; i++) {
				g1.setColor(new Color(240,240,240));
				g1.drawLine(0, (int)(i * (h + 1) / grid_x), w, (int)(i * h / grid_x));
				g1.setColor(Color.GRAY);
				grid = (float)(((h + 1) - (i * h / grid_x)) / y);
				grid = (float) (Math.round(grid * Math.pow(10,1) ) / Math.pow(10,1));
				g1.drawString( grid + " ms", 0,  (int)(i * (h + 1) / grid_x));
			}
			t2 -= t1; // tempo total em ms.
			DateFormat df_date = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
			Date date = null;
			for (int j = 0; j <= grid_y; j++) {
				g1.setColor(new Color(240,240,240));
				g1.drawLine((j * 120), 0, (j * 120), h);
				g1.setColor(Color.GRAY);
				date = new Date();
				date.setTime((long)(j * ((120 / x) * 1000) + t1));
				g1.drawString(df_date.format(date), (j * 120),  10);
				g1.drawString(df_time.format(date), (j * 120),  20);
			}
					
			// -- desenha as linhas.
			g1.setColor(Color.RED);
			Point p1 = null;
			for (TablePefformaceData data : list) {
				if (data._delays != null) {
					TreeSet<Long> keyMap = new TreeSet<Long>(data._delays.keySet());
					//Set<Long> keyMap = data._delays.keySet();
					p1 = new Point(0,h);
					for (Long key : keyMap) {
						Integer delay = data._delays.get(key);
						Point p2 = new Point((int)(x * ((key - t1) / 1000D)), h - (int)(y * delay));
						if (p1.x == 0 && p1.y == h) {
							p1 = p2;
						}
						g1.drawLine(p1.x, p1.y, p2.x, p2.y);
						p1 = p2;
					}
				}
			}
			
			for (int i = 0; i < _gradiente_cores.length; i++) {
				if (_gradiente_cores[i] != null) {
					g1.setColor(_gradiente_cores[i]); 
					g1.drawLine(100 + i*2, 0, 100 + i*2, 10);
				}
			}
			
			
			g1.finalize();
			c.setIcon(new ImageIcon(image));
			g.finalize();
			if (_graphic_scroll != null && _graphic_auto_scroll.isSelected()) {
				JScrollBar horizontal_bar = _graphic_scroll.getHorizontalScrollBar(); 
				horizontal_bar.setValue(horizontal_bar.getMaximum());
			}
		}
		
		
		private class MountPefformaceListByLog implements Runnable {
			public void run() {

				if (_CONNECTION == null || !_CONNECTION.isConnected()) {
					return;
				}
				else if (_CONNECTION.getServerType() > 0) {
					JOptionPane.showMessageDialog(null, "Este recurso está disponível apenas para servidores MySQL.", "Aviso!", JOptionPane.OK_OPTION);
					return;
				}
				
				try {
					// 01# -- Obtem o conteudo de texto do arquivo de Log ODBC do publi.
					JFileChooser chooser = new JFileChooser(new File(MainWindow.getPropertie("last_log_file", "C:/PubliW/")));
					chooser.setFileFilter(new FileFilter(){
						public boolean accept(File file) {
							String name = file.getName();
							if (name != null) {
								return (name.toLowerCase().endsWith(".log") || name.toLowerCase().endsWith(".lg") || file.isDirectory());
							}
							return false;
						}

						public String getDescription() {
							return "Publi ODBC Log Files (*.LOG; *.LG)";
						}
						
					});
					chooser.setToolTipText("Selecione o arquivo para abrir");
					chooser.setApproveButtonText("Abrir");
					chooser.setCurrentDirectory(new File("%root%"));
					int result = chooser.showSaveDialog(null);
					StringBuffer buffer = new StringBuffer();
					if (result == JFileChooser.APPROVE_OPTION) {
						MainWindow.setPropertie("last_log_file", chooser.getSelectedFile().getCanonicalPath());
						MainWindow.saveProperties();
						FileInputStream is = new FileInputStream(chooser.getSelectedFile());
						byte[] buf = new byte[1024];
						int read = 0;
						Charset charset = Charset.forName("latin1");
						while ((read = is.read(buf)) > 0) {
							ByteBuffer bb = ByteBuffer.wrap(buf, 0, read);
							buffer.append(charset.decode(bb).toString());
						}
						is.close();
					}

					// 02# -- Extrai do conteudo do arquivo apenas os registros de selects.
					List<String> select_list = new ArrayList<String>();
					if (buffer.length() > 0) {
						StringBuffer select = null;
						int tab_count = 0;
						String item = null;
						for (int i = 0; i < buffer.length(); i++) {
							if (select == null) {
								select = new StringBuffer();
							}
							switch (buffer.charAt(i)) {
								case 9:
									++tab_count;
									break;
								case 10:
									tab_count = 0;
									item = select.toString().toLowerCase();
									if (item.startsWith("select")) {
										select_list.add(select.toString());
									}
									select = null;
									break;
								default:
									if (tab_count >= 3) {
										select.append(buffer.charAt(i));
									}
							}
						}
						buffer = null;
						select = null;						
					}
					
					// 03# -- prepara o model e monta o grid na tela.
					if (select_list.size() > 0) {
						int count = 0;
						String table = null;
						
						Object[] line = null;
						String[] head = new String[]{"Tabela", "Comando SQL (Query)", "Tamanho", "Tipo", "Índices", "Resposta", "Resposta <Max>", "Resposta <Med>", "Resposta <Min>", "Consultas"};
						Object[][] grid = new Object[select_list.size()][head.length];
						
						_default_list_data = new ArrayList<TablePefformaceData>();
						
						for (String linha : select_list) {
							updateStatus("Buscando informações da consulta<br>\\:><b> " + table + "</b>");
							table = "reg#" + (count < 10 ? "00" + count : (count < 100 ? "0" + count : count));
							
							TablePefformaceData data = new TablePefformaceData();
							data._table = table;
							_default_list_data.add(data);
							
							line = new Object[]{"", "", 0, "", "", 0, 0, 0, 0, 0};
							line[0] = data._table;
							line[1] = linha;
							line[2] = 0;
							try {
								ResultSet rs = _CONNECTION.executeQuery("EXPLAIN " + linha);
								if (rs.next()) {
									line[3] = rs.getString("select_type");
									line[4] = rs.getString("possible_keys");
								}
								else {
									line[3] = "-";
									line[4] = "-";
								}
							rs.close();
							}
							catch (Exception e){
								line[3] = "-";
								line[4] = "-";
							}
							
							line[5] = "-";
							line[6] = "-";
							line[7] = "-";
							line[8] = 0;
							grid[count++] = line;
						}
						
						updateStatus("Exibindo informações<br>\\:> <b>...</b>");
						
						PefformaceTableModel model = new PefformaceTableModel(head, grid);
						_table_c.setModel(model);
						autoAdjustTable();
						toogleEnabled(true);
						updateStatus("Pronto para iniciar monitoração<br>\\:> <b>READY</b>");
						initGraphicTableList();
						if (JOptionPane.showConfirmDialog(null, "<html>As informações sobre a base foram obtidas com sucesso!<br>Deseja iniciar o processo de monitoramento agora?</html>", "JQueryAnalizer - Confirmação!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							_monitor_status = true;
							Thread t = new Thread(new doPefformaceTest(_current_row));
							t.start();						
						}
						
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					toogleEnabled(false);
				}
			}
			
			
		}
		
		private class MountPefformaceDefault implements Runnable {
			public void run() {

				if (_CONNECTION == null || !_CONNECTION.isConnected()) {
					return;
				}
				else if (_CONNECTION.getServerType() > 0) {
					JOptionPane.showMessageDialog(null, "Este recurso está disponível apenas para servidores MySQL.", "Aviso!", JOptionPane.OK_OPTION);
					return;
				}
				
				try {
					// 01# -- primeiro passo: obter uma listagem de todas as tabelas que contem registros.
					updateStatus("Listando tabelas da base<br>\\:><b> ...</b>");
					List<String> tables = _CONNECTION.getTables();
					List<String> list = new ArrayList<String>();
					for (String table : tables) {
						ResultSet rs = _CONNECTION.executeQuery("SELECT COUNT(*) FROM " + table);
						if (rs.next() && rs.getInt(1) > 0) {
							updateStatus("Listando tabelas da base<br>\\:><b> "+table+"</b>");
							list.add(table);
						}
						rs.close();
					}

					updateStatus("Buscando informações das tabelas da base<br>\\:><b> ...</b>");
					// 02# -- segundo passo: detectar a chave primária e listar os valores disponiveis para estas chaves.
					tables = list;
					_default_list_data = new ArrayList<TablePefformaceData>();
					List<Object> pk_values_list = null;
					String pk_name = null;
					for (String table : tables) {
						ResultSet rs = _CONNECTION.executeQuery("DESCRIBE " + table);
						while (rs.next()) {
							if ((pk_name = rs.getString(4)) != null && pk_name.equalsIgnoreCase("pri")) {
								pk_values_list = new ArrayList<Object>();
								pk_name = rs.getString(1);
								updateStatus("Buscando informações das tabelas da base<br>\\:><b> " + table + "</b> '<i>" + pk_name + "</i>' [Primary Key]");
								rs.close();
								rs = _CONNECTION.executeQuery("SELECT " + pk_name + " FROM " + table);
								while (rs.next()) {
									pk_values_list.add(rs.getObject(1));
								}
								TablePefformaceData data = new TablePefformaceData();
								data._table = table;
								data._pk_name = pk_name;
								data._pk_values_list = pk_values_list;
								_default_list_data.add(data);
								rs.close();
								break;
							}
						}
					}
					
					
					// 03# terceiro passo: montando a estrutura da visualizacao.
					updateStatus("Obtendo informações complementares sobre as consultas<br>\\:> <b>...</b>");
					String[] head = new String[]{"Tabela", "Comando SQL (Query)", "Tamanho", "Tipo", "Índices", "Resposta", "Resposta <Max>", "Resposta <Med>", "Resposta <Min>", "Consultas"};
					Object[][] grid = new Object[_default_list_data.size()][head.length];

					Object[] line = null;
					int count = 0;
					TablePefformaceData data = null;
					for (int i = 0; i < _default_list_data.size(); i++) {
						if ((data = _default_list_data.get(i)) != null) {
							updateStatus("Obtendo informações complementares sobre as consultas<br>\\:> <b>" + data._table + "</b> <i>" + data._pk_values_list.size() + "</i> [Rows]");
							line = new Object[]{"", "", 0, "", "", 0, 0, 0, 0, 0};
							line[0] = data._table;
							line[1] = "SELECT * FROM " + line[0] + " WHERE " + data._pk_name + "=[RANDOM_VALUE]";
							line[2] = 0;
							ResultSet rs = _CONNECTION.executeQuery("EXPLAIN " + line[1].toString().replace("[RANDOM_VALUE]", "'" + data.getRandonPkValue() + "'"));
							if (rs.next()) {
								line[3] = rs.getString("select_type");
								line[4] = rs.getString("possible_keys");
							}
							else {
								line[3] = "-";
								line[4] = "-";
							}
							rs.close();
							line[5] = "-";
							line[6] = "-";
							line[7] = "-";
							line[8] = 0;
							grid[count++] = line;
						}
					}
					updateStatus("Exibindo informações<br>\\:> <b>...</b>");
					
					PefformaceTableModel model = new PefformaceTableModel(head, grid);
					_table_c.setModel(model);
					autoAdjustTable();
					toogleEnabled(true);
					updateStatus("Pronto para iniciar monitoração<br>\\:> <b>READY</b>");
					initGraphicTableList();
					if (JOptionPane.showConfirmDialog(null, "<html>As informações sobre a base foram obtidas com sucesso!<br>Deseja iniciar o processo de monitoramento agora?</html>", "JQueryAnalizer - Confirmação!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						_monitor_status = true;
						Thread t = new Thread(new doPefformaceTest(_current_row));
						t.start();						
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					toogleEnabled(false);
				}
			}
		}
		
		private class doPefformaceTest implements Runnable {
			private int _row;
			public doPefformaceTest(int row) {
				
				_row = row;
			}
			
			public void run() {
				String query = getQueryByRow(_row);
				if (query != null && !query.isEmpty() && _CONNECTION != null && _CONNECTION.isConnected()) {
					TablePefformaceData data = getDataByRow(_row);
					if (query.toLowerCase().contains("[random_value]")) {
						query = query.toLowerCase().replace("[random_value]", "'" + data.getRandonPkValue().toString() + "'");
					}
					try {
						updateStatus("Disparando consulta<br>\\:> <b>" + (query.length() > 128 ? query.substring(0,125) + "..." : query) + "</b>");
						data._last_sent_time = System.currentTimeMillis();
						ResultSet rs = _CONNECTION.executeQuery(query);
						int time = (int)(System.currentTimeMillis() - data._last_sent_time);
						updateStatus("Disparando consulta<br>\\:> retorno recebido com <b>" + time + " ms</b></b>");
						data._delays.put(System.currentTimeMillis(), time);
						ResultSetMetaData header_data = rs.getMetaData();
						int size = 0;
						Object object = null;
						while (rs.next()) {
							for (int i = 1; i <= header_data.getColumnCount(); i++) {
								size += ((object = rs.getObject(1)) != null ? object.toString().length() : 0);							
							}
						}
						
						PefformaceTableModel model = (PefformaceTableModel)_table_c.getModel();
						
						JTableModel model_g = null;
						
						Set<Long> key_set = data._delays.keySet();
						Iterator<Long> iterator = key_set.iterator();
						int max = 0;
						int med = 0;
						int min = 0;
						int delay = 0;
						int count = 0;
						while (iterator.hasNext()) {
							++count;
							delay = data._delays.get(iterator.next());
							if (max == 0 || max < delay) {
								max = delay;
							}
							if (min == 0 || min > delay) {
								min = delay;
							}
							med += delay;
						}
						med = (med / count); 

						model.setValueAt(size, _row, 2); // tamanho
						model.setValueAt(time, _row, 5); // atual
						model.setValueAt(max, _row, 6); // max
						model.setValueAt(med, _row, 7); // med
						model.setValueAt(min, _row, 8); // min
						model.setValueAt(data._delays.size(), _row, 9);
						
						// -- atualiza o grafico caso ele esteja visivel.
						if (_graphic_table_list != null && _graphic_table_list.isVisible()) {
							model_g = (JTableModel)_graphic_table_list.getModel();
							model_g.setValueAt(String.valueOf(max), _row, 2);
							model_g.setValueAt(String.valueOf(med), _row, 3);
							model_g.setValueAt(String.valueOf(min), _row, 4);
							model_g.setValueAt(String.valueOf(data._delays.size()), _row, 5);
							_graphic_table_list.revalidate();
							_graphic_table_list.repaint();
							drawGraphic(_graphic_label, drawGraphicSelectedList(model_g));
						}						
						rs.close();
						_table_c.revalidate();
						_table_c.repaint();
						if (_monitor_status) {
							if (_row + 1 < model.getRowCount()) {
								_current_row = _row + 1;
							}
							else {
								_current_row = 0;
							}
							Thread t = new Thread(new doPefformaceTest(_current_row));
							Thread.sleep(200);
							t.start();
						}

					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}
			}
			
			private TablePefformaceData getDataByRow(int row) {
				PefformaceTableModel model = (PefformaceTableModel)_table_c.getModel();
				if (model != null) {
					if (row >= model.getRowCount()) return null;
					String table = (String)model.getValueAt(row, 0);
					if (table != null && _default_list_data != null) {
						//TablePefformaceData[] lista = _default_list_data.toArray(new TablePefformaceData[_default_list_data.size()]);
						TablePefformaceData data = null;
						for (TablePefformaceData t : _default_list_data) {
							if (t != null && t._table != null && t._table.equalsIgnoreCase(table)) {
								data = t;
								break;								
							}
						}
						return data;
					}
				}
				return null;
			}
			
			private String getQueryByRow(int row) {
				PefformaceTableModel model = (PefformaceTableModel)_table_c.getModel();
				if (model != null) {
					if (row >= model.getRowCount()) return null;
					String query = (String)model.getValueAt(row, 1);
					if (query != null) {
						return query;
					}
				}
				return null;
			}
		}
				
		private class TablePefformaceData {
			public String _table;
			public String _pk_name;
			public List<Object> _pk_values_list;
			public HashMap<Long, Integer> _delays = new HashMap<Long, Integer>(); // tempo em milisegundos do timestamp (tempo do envio) x tempo em milisegundos de resposta.
			public long _last_sent_time;
			public Object getRandonPkValue() {
				if (_pk_values_list != null) {
					Random random = new Random();
					return _pk_values_list.get(random.nextInt(_pk_values_list.size()));
				}
				return null;
			}
		}

		
		
		
		public void autoAdjustTable() {
			String[] text = null;
			PefformaceTableModel model = (PefformaceTableModel)_table_c.getModel();
			if (model == null) return;
			int[] col_width = new int[model.getColumnCount()];
			int[] row_height = new int[model.getRowCount()];
			FontMetrics fm = _table_c.getFontMetrics(_table_c.getFont());
			for (int i = 0; i < col_width.length; i++) {
				col_width[i] = fm.stringWidth(model.getColumnName(i));
			}
			for (int i = 0; i < row_height.length; i++) {
				for (int j = 0; j < col_width.length; j++) {
					text = (model.getValueAt(i, j) == null ? "" : model.getValueAt(i, j).toString()).split("\n");
					for (String linha : text) {
						row_height[i] = Math.max(row_height[i], fm.getHeight() * text.length);
						col_width[j] = Math.max(col_width[j], fm.stringWidth(linha));
					}	
				
				}					
			}

			for (int i = 0; i < row_height.length; i++) {
				_table_c.setRowHeight(i, 10 + row_height[i]);
				_table_c.setRowMargin(1);
			}
			
			for (int j = 0; j < col_width.length; j++) {
				_table_c.getColumnModel().getColumn(j).setWidth(10 + col_width[j]);
				_table_c.getColumnModel().getColumn(j).setMinWidth(10 + col_width[j]);
				_table_c.getColumnModel().getColumn(j).setPreferredWidth(10 + col_width[j]);
				_table_c.getColumnModel().setColumnMargin(1);
			}
			_table_c.repaint();
		}
		
		
		

		/** -- Gerenciamento de conexão */
		public void openConnection() {
			if (_PARAMETERS != null) {
				
				// 1# Conecta ao servidor.
				if (_CONNECTION != null && _CONNECTION.isConnected()) {
					_CONNECTION.closeConnection();
					_CONNECTION = null;
				}
				System.out.println("*** [Pefformace] Conectando ao servidor... " + _PARAMETERS.getConnectionString());
				_CONNECTION = new SQLConnectionManager(_PARAMETERS.getConnectorDriver(), _PARAMETERS.getConnectionString(), _PARAMETERS.getUser(), _PARAMETERS.getPass());
				// 2# Verifica o status e as propriedades do servidor.
				_PARAMETERS.updateConnectionStatus(_CONNECTION.getConnectionStatus());
				JCheckButton bt = MainWindow.getMenuMainButton("bt_connection");
				if (bt == null) return;
				switch (_CONNECTION.getConnectionStatus()) {
					case 0:
						bt = new JCheckButton(ClassLoader.getSystemResource("server_down.png"), new Dimension(48, 48), "bt_connection");
						break;
					case 1:
					case 2:
						bt = new JCheckButton(ClassLoader.getSystemResource("server_up.png"), new Dimension(48, 48), "bt_connection");
						break;
				}
				
				MainWindow.getMenuMain().remove(0);
				MainWindow.getMenuMain().add(bt, 0);
				for (MouseListener ml : bt.getMouseListeners()) {
					if (ml == null) {
						continue;
					}
					else if (ml.toString().contains("JCheckButton")) {
						ml.mouseExited(null);
					}
				}
				
				Component[] list = MainWindow.getTabComponents();
				if (list != null) {
					for (int i = 0; i < list.length; i++) {
						Component c = list[i];
						if (c instanceof JPefformacePane && ((JPefformacePane)c).equals(this)) {
							MainWindow.getTabs().setTitleAt(i, " Desempenho: " + _PARAMETERS.getDatabase());

						}
					}
				}

				if (_PARAMETERS != null && !_PARAMETERS.getDatabase().isEmpty()) {
					_CONNECTION.executeUpdate("USE " + (_CONNECTION.getServerType() == 0 ? "`" + _PARAMETERS.getDatabase().trim() + "`" : _PARAMETERS.getDatabase().trim()));
				}
				
			}
		}
		
		public void updateStatus(String message, Exception erro) {
			showToolTip(_cel_b, erro, message);
		}
		
		protected void showToolTip(JComponent component, Exception error, String message) {  
            String toolTipText = "<html><table width=350><tr><td><font color=red>" + (error != null ? "Excessão" : "Aviso") + "</font><br>" + (message != null ? message + "<br>" : "") + (error != null ?  "<b>" + error.getMessage() + "</b>" : "") + "</td></tr></table></html>";  
              
            final JToolTip tooltip1 = component.createToolTip();  

            PopupFactory popupFactory = PopupFactory.getSharedInstance();  
            tooltip1.setTipText(toolTipText);
            final Popup tooltip = popupFactory.getPopup(component, tooltip1, component.getLocationOnScreen().x, component.getLocationOnScreen().y + component.getHeight() + 2);  
            tooltip.show();  
            
            tooltip1.addMouseListener(new MouseListener(){
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
		
		public void updateStatus(String status) {
			_cel_b.setText("<html><table width='100%' cellspacing=0 cellpadding=0><tr><td><b>Histórico de comandos:</b><br>" + status + (getConnection() != null ?  " [" + getConnection().getLastQueryTime() + " s]" : "") + "</td></tr></html>");
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
		
		private class PefformaceTableModel extends AbstractTableModel {
			private static final long serialVersionUID = -2173685424567032936L;
			protected String[] columnNames = null;
	        protected Object[][] data = null;
			
			public PefformaceTableModel(String[] col, Object[][] rows) {
				if (col != null)
					columnNames = col;
				if (rows != null)
					data = rows;
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
	        	return data;
	        }
	        
	        @SuppressWarnings("unused")
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
	            return data[row][col];
	        }

	        public void setValueAt(Object obj, int row, int col) {
	            data[row][col] = obj;
	        }
	        
	        public boolean isCellEditable(int row, int col) {
	        	if (col == 1) return true;
	        	return false;
	        }
	    }
		
		public String getParametersPath() {
			String out = "";
			if (getConnection() != null) {
				switch (getConnection().getServerType()) {
					case 0: out = "MySQL://"; break;
					case 1: out = "MsSQL://"; break;
				}
			}
			else { out = "UnknowServer://"; }
			if (getParameters() != null) {
				out += (getParameters().getHost() != null ? getParameters().getHost() : "UnknowHost:####") + "/";
				out += (getParameters().getDatabase() != null ? "<u>"+getParameters().getDatabase()+"</u>" : "UnknowDatabase");
			}
			else { out += "UnknowHost:####/UnknowDatabase"; }
			return out;
		}
		
		public void setConnection(SQLConnectionManager value) {
			_CONNECTION = value;
		}
		
		public class JCellEditor extends DefaultCellEditor {

			private static final long serialVersionUID = -9086644498507588298L;

			public JCellEditor(JTextField field) {
				super(field);
				field.setOpaque(false);
				field.addKeyListener(_editor_cell_keylistener);
			}
		}
		
		public KeyListener _editor_cell_keylistener = new KeyListener(){
			@Override public void keyPressed(KeyEvent e) {
				PefformaceTableModel model = (PefformaceTableModel)_table_c.getModel();
				model.setValueAt(((JTextField)e.getComponent()).getText(), _table_c.getSelectedRow(), _table_c.getSelectedColumn());
			}
			@Override public void keyReleased(KeyEvent e) { }
			@Override public void keyTyped(KeyEvent e) { }			
		};
		
	}