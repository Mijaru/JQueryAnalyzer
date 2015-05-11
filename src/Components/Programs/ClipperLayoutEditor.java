package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javolution.util.FastList;

import Components.MainWindowComponents.JQDialog;

public class ClipperLayoutEditor {
	private Dimension _size = new Dimension(1000,600);
	private JFrame _DIALOG;
	
	@SuppressWarnings("unused")
	private int _mouse_button;
	private int _mouse_x;
	private int _mouse_y;
	@SuppressWarnings("unused")
	private boolean _mouse_pressed;
	
	private Color _color_selected;
	
	private BufferedImage _grid;
	private FastList<BufferedImage> _list = new FastList<BufferedImage>();
	private Dimension _page_size_px;
	
	private enum tool {POINTER, TEXT, LINE, RECT, RECTFILL};
	private tool _tool_select;
	
	public ClipperLayoutEditor() {
		
		_tool_select = tool.POINTER;
		
		_DIALOG = new JFrame();
		_DIALOG.setTitle("teste");
		_DIALOG.setSize(_size);
		_DIALOG.setMinimumSize(_size);
		_DIALOG.setPreferredSize(_size);
		_DIALOG.setDefaultCloseOperation(JQDialog.DISPOSE_ON_CLOSE);
		_DIALOG.setLayout(new GridBagLayout());		
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(true);
		//_DIALOG.setExtendedState(JFrame.MAXIMIZED_BOTH);
		GridBagConstraints cte = new GridBagConstraints();
		
		// (#)(_)(_)
		// (_)(_)(_)
		// (_______)
		cte.fill = GridBagConstraints.BOTH;
		cte.weightx = 0.01f;
		cte.weighty = 0.01f;
		cte.gridx = 0;
		cte.gridy = 0;
		cte.insets = new Insets(0,0,0,0);//new Insets(5,5,5,5);
		
		JLabel corner_tl = new JLabel();
		corner_tl.setOpaque(true);
		corner_tl.setBackground(Color.gray);
		_DIALOG.add(corner_tl, cte);
		
		// (_)(#)(_)
		// (_)(_)(_)
		// (_______)
		cte.weightx = 0.99f;
		cte.weighty = 0.01f;
		cte.gridx = 1;
		cte.gridy = 0;
		
		JPanel toolbar = new JPanel();
		toolbar.setOpaque(true);
		//toolbar.setBackground(Color.gray);
		toolbar.setLayout(new GridBagLayout());
		_DIALOG.add(toolbar, cte);
		
		GridBagConstraints toolbar_cte = new GridBagConstraints();
		toolbar_cte.fill = GridBagConstraints.BOTH;
		toolbar_cte.weightx = 0.01f;
		toolbar_cte.weighty = 0.01f;
		toolbar_cte.gridx = 0;
		toolbar_cte.gridy = 0;
		toolbar_cte.insets = new Insets(5,5,5,5);
		
		JLabel colors = new JLabel();
		colors.setSize(new Dimension(236, 64));
		colors.setMaximumSize(colors.getSize());
		colors.setMinimumSize(colors.getSize());
		colors.setPreferredSize(colors.getSize());
		colors.setLayout(null);
		colors.setOpaque(true);
		//colors.setBackground(Color.GRAY);
		toolbar.add(colors, toolbar_cte);
		
		final JLabel color_selected = new JLabel();
		color_selected.setOpaque(true);
		color_selected.setBackground(Color.BLACK);
		color_selected.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), BorderFactory.createLineBorder(Color.WHITE)));
		color_selected.setBounds(3,3,48,48);
		colors.add(color_selected);
		
		Color[] pallet = {Color.BLACK,
				Color.WHITE,
				Color.GRAY,
				Color.LIGHT_GRAY,
				new Color(185,0,21),
				new Color(185,122,87),
				Color.RED,
				Color.PINK,
				new Color(255,127,29),
				new Color(255,201,14),
				new Color(255,242,0),
				new Color(238,228,146),
				new Color(34,177,76),
				new Color(181,230,19),
				new Color(0,162,236),
				new Color(153,217,234),
				new Color(63,72,204),
				new Color(112,146,190),
				new Color(163,73,164),
				new Color(200,191,231)};
		int pallet_cnt = 0;		
		MouseListener color_ml = new MouseListener() {
			@Override public void mouseClicked(MouseEvent ev) { }
			@Override public void mouseEntered(MouseEvent ev) { }
			@Override public void mouseExited(MouseEvent ev) { }
			@Override public void mousePressed(MouseEvent ev) { }
			@Override public void mouseReleased(MouseEvent ev) { 
				Component c = ev.getComponent();
				if (c != null) {
					_color_selected = c.getBackground();
					color_selected.setBackground(_color_selected);
					color_selected.revalidate();
				}
			}
		};
		
		for (int i = 1; i < 11; i++) {
			for (int j = 0; j < 2; j++) {
				JLabel color = new JLabel();
				color.setFocusCycleRoot(true);
				color.setFocusable(true);
				color.setOpaque(true);
				color.setBackground(pallet[pallet_cnt]);
				color.setBounds(i * 25 + 30, j * 25 + 3, 23, 23);
				color.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210,210,210)), BorderFactory.createLineBorder(Color.WHITE)));
				color.addMouseListener(color_ml);
				colors.add(color);
				++pallet_cnt;
			}
		}
		
		toolbar_cte.weightx = 0.01f;
		toolbar_cte.weighty = 0.01f;
		toolbar_cte.gridx = 1;
		toolbar_cte.gridy = 0;
		
		JPanel button_1 = new JPanel();
		button_1.setSize(new Dimension(100,50));
		button_1.setPreferredSize(button_1.getSize());
		button_1.setMaximumSize(button_1.getSize());
		button_1.setMinimumSize(button_1.getSize());
		button_1.setLayout(new FlowLayout());
		toolbar.add(button_1, toolbar_cte);
		
		
		ImageIcon icon_pointer = new ImageIcon(ClassLoader.getSystemResource("pointer.png"));
		final JCheckBox pointer = new JCheckBox();
		pointer.setOpaque(false);
		pointer.setBackground(new Color(230,230,230));
		pointer.setName("pointer");
		pointer.setIcon(icon_pointer);
		button_1.add(pointer);
		
		ImageIcon icon_text = new ImageIcon(ClassLoader.getSystemResource("text.png"));
		final JCheckBox text = new JCheckBox();
		text.setOpaque(false);
		text.setBackground(new Color(230,230,230));
		text.setName("text");
		
		text.setIcon(icon_text);
		button_1.add(text);
		
		ImageIcon icon_line = new ImageIcon(ClassLoader.getSystemResource("line.png"));
		final JCheckBox line = new JCheckBox();
		line.setOpaque(false);
		line.setBackground(new Color(230,230,230));
		line.setName("line");	
		line.setIcon(icon_line);
		button_1.add(line);
		
		ImageIcon icon_rect = new ImageIcon(ClassLoader.getSystemResource("rect.png"));
		final JCheckBox rect = new JCheckBox();
		rect.setOpaque(false);
		rect.setBackground(new Color(230,230,230));
		rect.setName("rect");
		rect.setIcon(icon_rect);
		button_1.add(rect);
		
		ImageIcon icon_rectfill = new ImageIcon(ClassLoader.getSystemResource("rect_fill.png"));
		final JCheckBox rectfill = new JCheckBox();
		rectfill.setOpaque(false);
		rectfill.setBackground(new Color(230,230,230));
		rectfill.setName("rectfill");
		rectfill.setIcon(icon_rectfill);
		button_1.add(rectfill);
		
		
		ActionListener tool_selection = new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt) {

				text.setOpaque(false); text.repaint();
				line.setOpaque(false); line.repaint();
				pointer.setOpaque(false); pointer.repaint();
				rect.setOpaque(false); rect.repaint();
				rectfill.setOpaque(false); rectfill.repaint();
				
				if (evt.getSource() instanceof JCheckBox) {
					JCheckBox item = (JCheckBox)evt.getSource();
					item.setOpaque(true);
					if (item.getName().equalsIgnoreCase("pointer")) {
						_tool_select = tool.POINTER;		
					}
					else if (item.getName().equalsIgnoreCase("text")) {
						_tool_select = tool.TEXT;
					}
					else if (item.getName().equalsIgnoreCase("line")) {
						_tool_select = tool.LINE;
					}
					else if (item.getName().equalsIgnoreCase("rect")) {
						_tool_select = tool.RECT;
					}
					else if (item.getName().equalsIgnoreCase("rectfill")) {
						_tool_select = tool.RECTFILL;
					}
				}
			}			
		};
		pointer.addActionListener(tool_selection);
		text.addActionListener(tool_selection);
		line.addActionListener(tool_selection);
		rect.addActionListener(tool_selection);
		rectfill.addActionListener(tool_selection);
		
		
		// (_)(_)(#)
		// (_)(_)(_)
		// (_______)
		cte.weightx = 0.01f;
		cte.weighty = 0.01f;
		cte.gridx = 2;
		cte.gridy = 0;
		
		JLabel corner_tr = new JLabel();
		corner_tr.setOpaque(true);
		corner_tr.setBackground(Color.gray);
		_DIALOG.add(corner_tr, cte);
		

		// --
		// (_)(_)(_)
		// (#)(_)(_)
		// (_______)
		cte.fill = GridBagConstraints.BOTH;
		cte.weightx = 0.01f;
		cte.weighty = 0.99f;
		cte.gridx = 0;
		cte.gridy = 1;
		
		JLabel corner_ml = new JLabel();
		corner_ml.setOpaque(true);
		corner_ml.setBackground(Color.gray);
		_DIALOG.add(corner_ml, cte);
		
		// (_)(_)(_)
		// (_)(#)(_)
		// (_______)
		cte.weightx = 0.99f;
		cte.weighty = 0.99f;
		cte.gridx = 1;
		cte.gridy = 1;
		
	
		
		final JLayeredPane content = new JLayeredPane();
		content.setLayout(new GridBagLayout());//layers.setLayout(null);
		
		GridBagConstraints content_cte = new GridBagConstraints();
		content_cte.fill = GridBagConstraints.BOTH;
		content_cte.weighty = 0.9f;
		content_cte.gridy = 0;
		content_cte.insets = new Insets(0,0,0,0);
		
		//final JLabel page = new JLabel();
		_page_size_px = new Dimension((int)milimetersToPixels(211), (int)milimetersToPixels(254));
		final JLayeredPane page = new JLayeredPane();
		setAbsoluteSize(page, _page_size_px);
		//page.setSize(_page_size_px);
		//page.setMinimumSize(_page_size_px);
		//page.setMaximumSize(_page_size_px);
		//page.setPreferredSize(_page_size_px);
		final JLabel grid = new JLabel();
		setAbsoluteSize(grid, _page_size_px);
		
		final JLabel edit = new JLabel();
		edit.setLayout(null);
		setAbsoluteSize(edit, _page_size_px);
		
		grid.addMouseMotionListener(new MouseMotionListener(){
			// 
			@Override public void mouseDragged(MouseEvent e) {
				BufferedImage buffer = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_RGB );
				Graphics g = buffer.getGraphics();
				g.drawImage(_grid, 0, 0, null);
				// linhas de posicionamento
				int x = (_mouse_x < e.getX() ? _mouse_x : e.getX());
				int y = (_mouse_y < e.getY() ? _mouse_y : e.getY());
				int w = Math.abs(e.getX() - _mouse_x);
				int h = Math.abs(e.getY() - _mouse_y);
				g.setColor(new Color(0,0,0,30)); // preto 30% alpha
				g.fillRect(x, y, w, h);
				g.setColor(new Color(0,0,0,90)); // preto 90% alpha;
				g.drawRect(x, y, w, h);				
				g.finalize();
				grid.setIcon(new ImageIcon(buffer));
			}
			@Override public void mouseMoved(MouseEvent e) {
				
				
				//BufferedImage buffer = new BufferedImage(layers.getWidth(), layers.getHeight(), BufferedImage.TYPE_INT_RGB );
				BufferedImage buffer = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_RGB );
				Graphics g = buffer.getGraphics();
				g.drawImage(_grid, 0, 0, null);
				// linhas de posicionamento
				g.setColor(Color.ORANGE);
				int x = e.getX();
				int y = e.getY();
				g.drawLine(x, 0, x, page.getSize().height);		
				g.drawLine(0, y, page.getSize().width, y);
				
				g.finalize();
				grid.setIcon(new ImageIcon(buffer));
			}
			
		});
		grid.addMouseListener(new MouseListener(){
			@Override public void mouseClicked(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) {
				_mouse_x = e.getX();
				_mouse_y = e.getY();
				_mouse_pressed = true;
				Graphics2D g = (Graphics2D)_grid.getGraphics();
				FastList<String> lines = new FastList<String>();
				String text = "";
				String subt = "";
				JEditorPane obj = null;
				FontMetrics metrics = null;
				if (edit.getComponentCount() > 0) {
					@SuppressWarnings("unused")
					int x, y, w, h, i, j = 0;
					boolean goes = false;
					//int y = 0;
					for (Component c : edit.getComponents()) {
						if (c == null || g == null) continue;
						if (c instanceof JEditorPane) {
							obj = (JEditorPane)c;
							text = obj.getText();
							text = text.replace("\r", "");
							metrics = obj.getFontMetrics(obj.getFont());
							x = obj.getX() + 1;
							y = obj.getY() + metrics.getHeight();
							w = obj.getWidth() - 2;
							h = obj.getHeight() - 2;
							
							for (String reg : text.split("\n")) {
								for (String word : reg.split(" ")) {
									i = metrics.stringWidth(word);
									if (i <= w && j <= w) {
										j += i;
										subt += word + " ";
									}
									else if (j == 0 && i > w) {
										for (byte chr : word.getBytes()) {
											j += metrics.stringWidth((char)chr + "");
											if (j <= w) {
												subt += (char)(chr);
											}
											else {
												lines.add(subt);
												subt = "";
												i = 0;
												j = 0;
											}
										}
										lines.add(subt);
										subt = "";
										i = 0;
										j = 0;
									}
									else if ((i + j > w) || (j > 0 && i > w)) {
										goes = true;
									}
									
									if (goes) { break; }
									
								}
								lines.add(subt);
								subt = "";
								i = 0;
								j = 0;
							}
							
							for (String line : lines) {
								g.setFont(obj.getFont());
								g.setColor(_color_selected != null ? _color_selected : Color.BLACK);
								g.drawString(line, x, y);
								g.finalize();
								y += metrics.getStringBounds(line, g).getHeight();
							}
						}
					}
					g.finalize();
					edit.removeAll();
					edit.revalidate();
					edit.repaint();
				}
			}
			@Override public void mouseReleased(MouseEvent e) {
				_mouse_pressed = false;
				
				BufferedImage buffer = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_RGB );
				Graphics g = buffer.getGraphics();
				g.drawImage(_grid, 0, 0, null);
				// linhas de posicionamento
				//g.setColor(Color.BLUE);
				g.setColor(_color_selected != null ? _color_selected : Color.BLACK);
				int x = (_mouse_x < e.getX() ? _mouse_x : e.getX());
				int y = (_mouse_y < e.getY() ? _mouse_y : e.getY());
				int w = Math.abs(e.getX() - _mouse_x);
				int h = Math.abs(e.getY() - _mouse_y);

				switch (_tool_select) {
					case POINTER:
						break;
					case TEXT:
						JEditorPane jta = new JEditorPane();
						jta.setBounds(x, y, w + 1, h + 1);
						jta.setOpaque(false);
						jta.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
						edit.add(jta);
						break;
					case LINE:
						g.drawLine(_mouse_x, _mouse_y, e.getX(), e.getY());
						break;
					case RECT:
						g.drawRect(x, y, w, h);
						break;
					case RECTFILL:
						g.fillRect(x, y, w, h);
						break;
				}
				// -- função desenhar retângulo.
				//g.drawRect(x, y, w, h);		
				
				// -- função desenha linha.
				//g.drawLine(_mouse_x, _mouse_y, e.getX(), e.getY());
				
				// -- funcao imprime texto [L]
				/*
				String text = "left";
				g.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 14));
				FontMetrics metrics = g.getFontMetrics(g.getFont());
								
				Rectangle2D txt_dim = metrics.getStringBounds(text, g);
				int txt_w = (int)txt_dim.getWidth();
				int txt_h = (int)txt_dim.getHeight();
				g.drawString(text, x, y);
				
				text = "center";
				txt_dim = metrics.getStringBounds(text, g);
				txt_w = (int)txt_dim.getWidth();
				txt_h = (int)txt_dim.getHeight();
				g.setColor(Color.DARK_GRAY);
				g.drawString(text, x + ((w - txt_w) / 2), y);
				
				text = "right";
				txt_dim = metrics.getStringBounds(text, g);
				txt_w = (int)txt_dim.getWidth();
				txt_h = (int)txt_dim.getHeight();
				g.setColor(Color.CYAN);
				g.drawString(text, x + (w - txt_w), y);
				
				*/
				
				
				
				g.finalize();
				_list.add(_grid);
				_grid = buffer;
				grid.setIcon(new ImageIcon(buffer));
			}
		});
		page.add(grid);
		page.setLayer(grid, 0);
		
		page.add(edit);
		page.setLayer(edit, 1);
	
		final JScrollPane page_scrolls = new JScrollPane(page);
		Dimension scroll_size = new Dimension(page.getSize().width + 19, page.getSize().height);
		this.setAbsoluteSize(page_scrolls, scroll_size);

		content_cte.weightx = 1f;
		content_cte.gridx = 0;
		content.add(new JLabel(), content_cte);
		content_cte.weightx = 0.001f;
		content_cte.gridx = 1;
		content.add(page_scrolls, content_cte);
		content_cte.weightx = 1f;
		content_cte.gridx = 2;
		content.add(new JLabel(), content_cte);
		
		_DIALOG.add(content, cte);
		
		// (_)(_)(_)
		// (_)(_)(#)
		// (_______)
		cte.weightx = 0.01f;
		cte.weighty = 0.99f;
		cte.gridx = 2;
		cte.gridy = 1;
		
		JLabel corner_mr = new JLabel();
		corner_mr.setOpaque(true);
		corner_mr.setBackground(Color.gray);
		_DIALOG.add(corner_mr, cte);

		// --
		// (_)(_)(_)
		// (_)(_)(_)
		// (#)(_)(_)
		cte.weightx = 0.01f;
		cte.weighty = 0.01f;
		cte.gridx = 0;
		cte.gridy = 2;
		
		JLabel corner_bl = new JLabel();
		corner_bl.setOpaque(true);
		corner_bl.setBackground(Color.gray);
		_DIALOG.add(corner_bl, cte);
		
		// (_)(_)(_)
		// (_)(_)(_)
		// (_)(#)(_)
		cte.weightx = 0.99f;
		cte.weighty = 0.01f;
		cte.gridx = 1;
		cte.gridy = 2;
		
		JLabel corner_bm = new JLabel();
		corner_bm.setOpaque(true);
		corner_bm.setBackground(Color.gray);
		_DIALOG.add(corner_bm, cte);
		
		// (_)(_)(_)
		// (_)(_)(_)
		// (_)(_)(#)
		cte.weightx = 0.01f;
		cte.weighty = 0.01f;
		cte.gridx = 2;
		cte.gridy = 2;
		
		JLabel corner_br = new JLabel();
		corner_br.setOpaque(true);
		corner_br.setBackground(Color.gray);
		_DIALOG.add(corner_br, cte);

		
		
		
		
		
		
		
		
		
		
		_grid = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_RGB );
		Graphics g = _grid.getGraphics();
		g.fillRect(0, 0, page.getSize().width, page.getSize().height);
			
		// grid
		int grid_scale = (int)milimetersToPixels(10); // grid de 1 cm
		g.setColor(new Color(245,245,245));
		for (int i = 0; i * grid_scale < page.getWidth(); i++) {
			g.drawLine(grid_scale * i, 0, grid_scale * i, page.getSize().height);
		}
		for (int j = 0; j * grid_scale < page.getHeight(); j++) {
			g.drawLine(0, grid_scale * j, page.getSize().width, grid_scale * j);
		}
		g.finalize();
		grid.setIcon(new ImageIcon(_grid));
				
		
		
		
		
		
		
		
		
		
		
		_DIALOG.setVisible(true);
	}
	
	
	
	public float milimetersToPixels(int value) {
		return (Toolkit.getDefaultToolkit().getScreenResolution() * value) / 25.4f;
	}
	
	public float pixelsToMilimeters(int value) {
		return (25.4f * value) / Toolkit.getDefaultToolkit().getScreenResolution();
	}
	
	public void setAbsoluteSize(Component c, Dimension s) {
		c.setSize(s);
		c.setPreferredSize(s);
		c.setMaximumSize(s);
		c.setMinimumSize(s);
	}
	
	@SuppressWarnings("unused")
	private class Layer {
		private tool tool;
		private int x;
		private int y;
		private int w;
		private int h;
		private Color color;
		private Font font;
		private String label;
		
		
		public Layer(tool t, String s, Color c, Font f, int x, int y, int w, int h) {
			this.tool = t;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.font = f;
			this.color =  c;
		}
		
		public Rectangle getBounds() {
			return new Rectangle(x, y, w, h);
		}
		
		public boolean contains(Point p) {
			return (p != null && (p.x >= x && p.x <= x + w) && (p.y >= y && p.y <= y + h));
		}
		public boolean contains(int px, int py) {
			return contains(new Point(px, py));
		}
		
		public void drawComponent(Component observer) {
			
		}
		
		public void drawnGraphics() {
			
		}
	}
}


