package Components.MainWindowComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import Components.MainWindow;
import Components.SQLConnectionManager;

public class JParametersPanel extends JPanel {
		private static final long serialVersionUID = -3626776291864793194L;
		private Font _default_font;
		private Font _verdana_12_b = new Font("Verdana", Font.PLAIN, 12);
		private Border _parameters_border = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), " Parâmetros de conexão com o banco de dados ", TitledBorder.CENTER, TitledBorder.CENTER, _verdana_12_b, Color.DARK_GRAY);
		private JComboBox<String> _type;
		private JTextField _host;
		private JTextField _user;
		private JTextField _pass;
		private JTextField _name;
		private JLabel _status;
		public int _max_height = 155;
		public int _min_height = 27;
		private int _cur_height = _max_height;
		private JParametersPanel _this;
		private Thread _collapse_thread;
		private CollapsePane _collapse_task;
		private boolean _hasBorder = true;
		private int _connection_status;
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (_hasBorder) {
				g.setColor(new Color(180,180,180));
				
				g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
				
				g.drawLine(0, 3, 0, getHeight());
			
				FontMetrics metrics = this.getFontMetrics(_verdana_12_b);
				int length = metrics.stringWidth(" Parâmetros de conexão com o banco de dados ");
				int a = getWidth() / 2 - length / 2;
				int b = (getWidth() / 2) + length / 2;
				
				g.drawLine(0, 3, a, 3);
				
				g.drawLine(b, 3, getWidth(), 3);
				
				g.drawLine(getWidth() - 1, 22, getWidth() - 1, getHeight());
			}
		}
		
		public JParametersPanel() {
			super();
			_default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
			this.setLayout(null);
			
			final JLabel header = new JLabel("");
			header.setHorizontalAlignment((int)LEFT_ALIGNMENT);
			header.setFont(_default_font);
			header.setForeground(new Color(40,145,200));
			header.setName("header");
			header.setOpaque(true);
			header.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
			addComponentListener(new ComponentListener() {
				public void componentHidden(ComponentEvent a) { }
				public void componentMoved(ComponentEvent a)  { }
				public void componentShown(ComponentEvent a)  { }
				public void componentResized(ComponentEvent event) {
					Dimension d = getSize();
					
					if (event == null || _cur_height <= _min_height || _cur_height >= _max_height) {

						//int width = header.getFontMetrics(header.getFont()).stringWidth(header.getText()) + 22;
						header.setBounds(d.width - 24, 3, 24, 24);

						ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(isCollapsed() ? "show.png" : "hide.png"));
						icon.setImage(icon.getImage().getScaledInstance(16, 16, Image.SCALE_AREA_AVERAGING));
						
						header.setIcon(icon);
						header.revalidate();
						header.setToolTipText(isCollapsed() ? "Visualizar os parâmetros de conexão." : "Ocultar os parâmetros de conexão.");
						
						if (event == null) {
							d = new Dimension(d.width, isCollapsed() ? _min_height : _max_height);
							setSize(d);
							setPreferredSize(d);
						}
					}
					if (_status != null) {
						_status.setBounds(290,20,d.width - 290,130);
						updateConnectionStatus(_connection_status);
					}
				}
			});
			
			header.addMouseListener(new MouseListener(){
				public void mouseReleased(MouseEvent arg0) { }
				public void mouseClicked(MouseEvent arg0) { }
				public void mouseEntered(MouseEvent arg0) {
					header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				public void mouseExited(MouseEvent arg0) {
					header.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				public void mousePressed(MouseEvent arg0) {
					_this.setColapsed(_cur_height > _min_height);
				}
			});
			
			this.add(header);
	        this.setSize(new Dimension(0, _cur_height));
	        this.setPreferredSize(new Dimension(0,_cur_height));
	        
	        
			
	        
			JLabel t_host = new JLabel("Servidor: ");
			t_host.setBounds(10,20,100,20);
			t_host.setHorizontalAlignment(SwingConstants.RIGHT);
			t_host.setFont(_default_font);
			this.add(t_host);
			
			JLabel t_user = new JLabel("Usuário: ");
			t_user.setBounds(10,46,100,20);
			t_user.setHorizontalAlignment(SwingConstants.RIGHT);
			t_user.setFont(_default_font);
			this.add(t_user);
			
			JLabel t_pass = new JLabel("Senha: ");
			t_pass.setBounds(10,72,100,20);
			t_pass.setHorizontalAlignment(SwingConstants.RIGHT);
			t_pass.setFont(_default_font);
			this.add(t_pass);
			
			JLabel t_name = new JLabel("Base de dados: ");
			t_name.setBounds(10,99,100,20);
			t_name.setHorizontalAlignment(SwingConstants.RIGHT);
			t_name.setFont(_default_font);
			this.add(t_name);

			JLabel t_db_type = new JLabel("Tipo de banco: ");
			t_db_type.setBounds(10,124,100,20);
			t_db_type.setHorizontalAlignment(SwingConstants.RIGHT);
			t_db_type.setFont(_default_font);
			this.add(t_db_type);

			
			KeyListener key_listener = new KeyListener(){
				private JTabPanel _active;
				public void keyPressed(KeyEvent key) {
					saveProperties();
					if (key.getKeyCode() == 10) {
						if (MainWindow.getActiveTab() instanceof JTabPanel) {
							_active = (JTabPanel)MainWindow.getActiveTab();
							if (_active != null && _active.isConnected()) {
								if (JOptionPane.showConfirmDialog(null, "<html><font face='verdana' size='3'>Você já está conectado a uma base de dados.<br><br><i>Deseja reconectar a base de dados</i>: <b>" + _active.getParametersPath()+ "</b> ?</font></html>", "Deseja reconectar-se?", JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
									_active.closeConnection();
								}
								else {
									return;
								}
							}
							Thread tt = new Thread(new Runnable(){
								public void run() {
									_active.updateStatus("<i><font color='yellow'>INICIANDO CONEXÃO COM SERVIDOR...</font></i><br>\\:> <b>WAITING</b>");		
								}
							});
							tt.start();
						}
						JThreadCommands tc = new JThreadCommands(0, null, _active);
						Thread t = new Thread(tc);
						t.start();
					}
				}
				public void keyReleased(KeyEvent arg0) { }
				public void keyTyped(KeyEvent arg0)    { }
				
			};
			_host = new JTextField("localhost:3306");
			_host.setBounds(110,20,170,24);
			_host.setHorizontalAlignment(SwingConstants.CENTER);
			_host.setFont(_default_font);
			_host.addKeyListener(key_listener);
			_host.setToolTipText("Endereço do servidor: IP/Hostname.");
			this.add(_host);
				
			_user = new JTextField("publi");
			_user.setBounds(110,46,170,24);
			_user.setHorizontalAlignment(SwingConstants.CENTER);
			_user.setFont(_default_font);
			_user.addKeyListener(key_listener);
			_user.setToolTipText("Usuário de acesso ao banco de dados.");
			this.add(_user);
				
			_pass = new JPasswordField("publi");
			_pass.setBounds(110,72,170,24);
			_pass.setHorizontalAlignment(SwingConstants.CENTER);
			_pass.setFont(_default_font);
			_pass.addKeyListener(key_listener);
			_pass.setToolTipText("Senha de acesso ao banco de dados.");
			this.add(_pass);
				
			_name = new JTextField("");
			_name.setBounds(110,98,170,24);
			_name.setHorizontalAlignment(SwingConstants.CENTER);
			_name.setFont(_default_font);
			_name.addKeyListener(key_listener);
			_name.setToolTipText("Nome da base de dados.");
			this.add(_name);
			
			_type = new JComboBox<String>() {

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
			_type.setEditable(false);
			_type.setBounds(110,124,170,24);
			_type.setFont(_default_font);
			_type.addItem("Oracle MySQL Server");
			_type.addItem("Oracle Database Server");
			_type.addItem("PostgreSQL Server");
			_type.addItem("Microsoft SQL Server");
			_type.setToolTipText("Selecione o tipo de banco de dados.");
			_type.addKeyListener(key_listener);
			_type.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent action) {
					String host = _host.getText();
					host = host.split(":")[0];
					_host.setText(host);
				}
				
			});
			this.add(_type);
			
			_status = new JLabel();
			_status.setFont(_default_font);
			_status.setVerticalAlignment(SwingConstants.TOP);
			this.add(_status);

			String info = "<table cellspacing=0 cellpadding=0 border=0>";
			info += "<tr>";
			info += "<td width=18><img src='" + ClassLoader.getSystemResource("disconnected.png") + "' width='24' height='24' border='0'></td><td width=1000><font color='red' face='Tahoma' size=3><b>Desconectado</b></font> <font size=3>" + getHost() + "</font>";
			info += "</td></tr>";
			info += "<td colspan=2>A conexão com o servidor não foi iniciada.</td>";
			info += "</tr>";
			info += "</table>";
			this.setToolTipText("Informações sobre este banco de dados.");
			this.setConnectionInfo(info);

			this.setBorder(_parameters_border);
			_this = this;
			
			loadProperties();
		}
				
		public void setColapsed(boolean state) {
			if (_collapse_thread != null && _collapse_task != null) {
				_collapse_task.stop();
				_collapse_thread = null;
			}
			_collapse_task = new CollapsePane(state);
			_collapse_thread = new Thread(_collapse_task);
			_collapse_thread.setDaemon(true);
			_collapse_thread.start();
		}
		
		public boolean isCollapsed() {
			return (_cur_height <= _min_height);
		}
		
		
		
		
		private class CollapsePane implements Runnable {
		    public static final int DEFAULT_UPS = 500;
		    public static final int DEFAULT_NO_DELAYS_PER_YIELD = 16;
		    public static final int DEFAULT_MAX_FRAME_SKIPS = 5;
		 
		    private boolean collapse;
		    private long desiredUpdateTime;
		    private boolean running;
		 
		    private long afterTime;
		    private long beforeTime = System.currentTimeMillis();
		 
		    private long overSleepTime = 0;
		    private long excessTime = 0;
		 
		    private int noDelaysPerYield = DEFAULT_NO_DELAYS_PER_YIELD;
		    private int maxFrameSkips = DEFAULT_MAX_FRAME_SKIPS;
		 
		    int noDelays = 0;
		    public CollapsePane(boolean collapse, int ups, int maxFrameSkips, int noDelaysPerYield) {
		        super();
		        if (ups < 1)
		            throw new IllegalArgumentException("You must display at least one frame per second!");
		 
		        if (ups > 1000)
		            ups = 1000;
		        this.collapse = collapse;
		        this.desiredUpdateTime = 1000000000L / ups;
		        this.running = true;
		        this.maxFrameSkips = maxFrameSkips;
		        this.noDelaysPerYield = noDelaysPerYield;
		    }
		 
		    public CollapsePane(boolean collapse, int ups) {
		    	this(collapse, ups, DEFAULT_MAX_FRAME_SKIPS, DEFAULT_NO_DELAYS_PER_YIELD);
		    }
		 
		    public CollapsePane(boolean collapse) {
		        this(collapse, DEFAULT_UPS);
		    }
		 
		    private void sleep(long nanos) {
		        try {
		            noDelays = 0;
		            long beforeSleep = System.nanoTime();
		            Thread.sleep(nanos / 1000000L, (int) (nanos % 1000000L));
		            overSleepTime = System.nanoTime() - beforeSleep - nanos;
		        }
		        catch (Exception e) {}
		    }
		 
		    private void yieldIfNeed() {
		        if (++noDelays == noDelaysPerYield) {
		            Thread.yield();
		            noDelays = 0;
		        }
		    }
		 
		    private long calculateSleepTime() {
		        return desiredUpdateTime - (afterTime - beforeTime) - overSleepTime;
		    }
		 
		    public void run() {
		        running = true;
		 
		        try {
		            while (running) {
		                beforeTime = System.nanoTime();
		                skipFramesInExcessTime();
		 
		                // -- Updates, Renders and Paint the Screen
		                if (CollapsePane(collapse, true)) {
		                	stop();
		                	break;
		                }
		                
		                afterTime = System.nanoTime();
		                long sleepTime = calculateSleepTime();
		                if (sleepTime >= 0) {
		                	sleep(sleepTime);
		                }
		                else {
		                	excessTime -= sleepTime; // Sleep time is negative
		                	overSleepTime = 0L;
		                	yieldIfNeed();
		               }
		           }
		       }
		       catch (Exception e) {
		    	   e.printStackTrace();
		       }
		       finally {
		    	   running = false;
		       }
		    }
		 
		    private void skipFramesInExcessTime() {
		        int skips = 0;
		        while ((excessTime > desiredUpdateTime) && (skips < maxFrameSkips)) {
		            excessTime -= desiredUpdateTime;
		            if (CollapsePane(collapse, false)) {
		            	stop();
		            	break;
		            }
		            skips++;
		        }
		    }
		 
		    public void stop() {
		        running = false;
		    }
		}
		
		
		private boolean CollapsePane(boolean action, boolean paint) {
			
			// -> true = collapse
			// -> false = expand
			Dimension d = null;//getSize();
			boolean collapsed = false;
			if (action == true) {
				if (_cur_height >= _min_height) {
					if (_cur_height > _max_height) {
						_cur_height = _max_height;
					}
					_cur_height -= Math.max(_cur_height / _min_height, 1); // colapsa rapidamente no inicio e vai reduzindo a velocidade a medida que se aprixima do limite minimo...
					collapsed = _cur_height <= _min_height;
					if (collapsed) {
						_cur_height = _min_height;
						JTabPanel pane = (MainWindow.getActiveTab() != null && MainWindow.getActiveTab() instanceof JTabPanel ? (JTabPanel)MainWindow.getActiveTab() : null);
						for (Component component : getComponents()) {
							if (component != null && ((component.getName() != null && !component.getName().equalsIgnoreCase("header")) || component.getName() == null)) {
								component.setVisible(false);
							}
						}
						if (pane != null) {
							_hasBorder = false;
							setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "<html><b>»</b>&nbsp;Parâmetros de conexão com: (" + getParametersString() + ")</html>", 1, 0, new Font(_default_font.getFontName(), _default_font.getStyle(), 12), (pane != null && pane.isConnected() ? new Color(40,160,80) : new Color(160,80,40)) ));
						}
					}
				}
				d = new Dimension(getSize().width, _cur_height);
				setSize(d);
				setPreferredSize(d);
				if (paint || (collapsed && action)) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							//revalidate();
							repaint();
						}
					});
					
				}
				return collapsed;
			}
			// expand action.
			else {
				if (_cur_height <= _max_height) {
					_cur_height += Math.max((_max_height - _cur_height) / _min_height,1); // expande rapidamente e vai reduzindo a velocidade enquanto se aproxima do valor maximo.
					if (_cur_height > _max_height) {
						_cur_height = _max_height;
					}
					collapsed = _cur_height >= _max_height;
					if (collapsed) {
						for (Component component : _this.getComponents()) {
							if (component != null && ((component.getName() != null && !component.getName().equalsIgnoreCase("header")) || component.getName() == null)) {
								component.setVisible(true);
							}
						}
						_hasBorder = true;
						setBorder(_parameters_border);
					}
				}
				d = new Dimension(getSize().width, _cur_height);
				setSize(d);
				setPreferredSize(d);
				if (paint || (collapsed && action)) {				
					revalidate();
				}
				return collapsed;
			}			
		}
		
		public void setDefaultBorder() {
			if (_this != null) {
				_this.setBorder(_parameters_border);
			}
		}
		
		/** ------------------------------------------------- *
		 ** Retorna a String de conexão com o banco de dados. *
		 ** ================================================= */
		public String getConnectionString() {
			switch (_type.getSelectedIndex()) {
				case 0: // mysql
					if (_host.getText().split(":").length == 1) {
						_host.setText(_host.getText() + ":3306");
					}
					return "jdbc:mysql://" + _host.getText() + "/" + _name.getText() + "?autoReconnect=true&zeroDateTimeBehavior=convertToNull&noDatetimeStringSync=true&useAffectedRows=true";
				case 1:
					if (_host.getText().split(":").length == 1) {
						_host.setText(_host.getText() + ":1521");
					}
					// -- para conectar a uma instancia especifica requer que o parametro informado no campo database seja: INSTANCIA:DATABASE
					return "jdbc:oracle:thin:@" + _host.getText() + (_name.getText() != null && _name.getText().contains(":") ? ":" + _name.getText().split(":")[0] : "");
				case 2:
					if (_host.getText().split(":").length == 1) {
						_host.setText(_host.getText() + ":5432");
					}
					return "jdbc:postgresql://" + _host.getText() + "/" + _name.getText();
				case 3: // sql server
					if (_host.getText().split(":").length == 1) {
						_host.setText(_host.getText() + ":1433");
					}
					return "jdbc:jtds:sqlserver://" + _host.getText() + "/" + _name.getText() + ";appname=jTDS application";				
				default:
					return null;
			}
		}
		
		
		/** ------------------------------------------------------------------ *
		 ** Retorna a String do driver utilizado para conectar a base de dados *
		 ** ================================================================== */
		public String getConnectorDriver() {
			switch (_type.getSelectedIndex()) {
				case 0:
					return "com.mysql.jdbc.Driver";
				case 1:
					return "oracle.jdbc.driver.OracleDriver";
				case 2:
					return "org.postgresql.Driver";
				case 3:
					return "net.sourceforge.jtds.jdbc.Driver";				
			}
			return null;
		}
		
		public String getDatabase() {
			return (_name == null ? null : _name.getText());
		}
		
		public String getUser() {
			return (_user == null ? null : _user.getText());
		}
		
		public String getPass() {
			return (_pass == null ? null : _pass.getText());
		}
		
		public String getHost() {
			return _host.getText();
		}
		
		public void setConnectionInfo(String text) {
			_status.setText("<html><table cellspacing=0 cellpadding=0 border=0 width='100%'><tr><td>" + text + "</td></tr></table></html>");
		}
		
		public void setDatabase(String database) {
			_name.setText(database);
		}
		
		public void setUser(String user) {
			_user.setText(user);
		}
		
		public void setPass(String pass) {
			_pass.setText(pass);
		}
		
		public void setHost(String host) {
			_host.setText(host);
		}
		
		public void flush(JParametersPanel new_panel, SQLConnectionManager connection) {
			this.setDatabase(new_panel.getDatabase());
			this.setUser(new_panel.getUser());
			this.setPass(new_panel.getPass());
			this.setHost(new_panel.getHost());
			
			this.updateConnectionStatus(connection != null ? connection.getConnectionStatus() : -1 );
		}
		
		public String getParametersString() {
			String out = "";
			switch (_type.getSelectedIndex()) {
				case 0: out = "<b>Oracle MySQL Server</b>://"; break;
				case 1: out = "<b>Oracle Database Server</b>://"; break;
				case 2: out = "<b>PostgreSQL Server</b>://"; break;
				case 3: out = "<b>Microsoft SQL Server</b>://"; break;
			}
			return out + getHost() + "/" + getDatabase();
		}
		
		public void updateConnectionStatus(int status) {
			_connection_status = status;
			//System.out.println("w2=" + _status.getWidth());
			String info = "<table cellspacing=0 cellpadding=0 border=0 width='" + _status.getWidth() + "'><tr>";
			if (status == 1) {
				info += "<td width='24'><img src='" + ClassLoader.getSystemResource("connected.png") + "' border='0'></td> <td align='left' width='99%'>&nbsp;<font color='#458B00' size=5><b>Conectado</b></font> [<font size=3>" +  getParametersString() + "</font>]";
			}
			else if (status == 2) {
				info += "<td width='24'><img src='" + ClassLoader.getSystemResource("warning.png") + "' border='0'></td> <td align='left' width='99%'>&nbsp;<font color='#FF8247' size=5><b>Conectando</b></font> [<font size=3>" +  getParametersString() + "</font>]";
			}
			else {
				info += "<td width='24'><img src='" + ClassLoader.getSystemResource("disconnected.png") + "' width=24 height=24 border='0'></td> <td align='left' width='99%'>&nbsp;<font color='#CD3700' size=5><b>Desconectado</b></font> [<font size=3>" + getParametersString() + "</font>]";
			}
			info += "</td></tr>";
			
			info += "<tr><td colspan='2'><table border=0>";
			if (status != 2) {
				JTabPanel panel = MainWindow.getTabByParametersPanel(this);
				if (panel != null && panel.getConnection() != null) {
					String text = panel.getConnection().getConnectionInfo();
					if (text != null) {
						if (text.contains("#")) {
							for (String i : text.split(";")) {
								if (i == null) continue;
								else if (i.split("#").length > 1) {
									info += "<tr><td align='right' valign='top' width=130><font color='#777777'>" + i.split("#")[0] + (i.split("#")[0].trim().isEmpty() ? "" : "<b>:</b>") + "</font>&nbsp;</td><td>" + i.split("#")[1] + "</td></tr>";
								}
							}
						}
						else {
							info += "<tr><td align='right' valign='top' width=130><b>Erro</b>:&nbsp;</td><td>" + text + "</td></tr>";
						}
					}
				}
			}
			info += "</table></td></tr>";
			info += "</table>";
			this.setConnectionInfo(info);
		}
		
		public void saveProperties() {
			MainWindow.setPropertie("main_host",_host.getText());
			MainWindow.setPropertie("main_user",_user.getText());
			MainWindow.setPropertie("main_pass",_pass.getText());
			MainWindow.setPropertie("main_database",_name.getText());
			MainWindow.setPropertie("main_database_type",_type.getSelectedIndex() + "");
			MainWindow.saveProperties();
		}
		
		public void loadProperties() {
			_host.setText(MainWindow.getPropertie("main_host","localhost"));
			_user.setText(MainWindow.getPropertie("main_user","root"));
			_pass.setText(MainWindow.getPropertie("main_pass",""));
			_name.setText(MainWindow.getPropertie("main_database",""));
			_type.setSelectedIndex(Integer.parseInt(MainWindow.getPropertie("main_database_type","0")));
		}
}