package Components.MainWindowComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import Components.MainWindow;

	public class JTextEditor extends JPanel {
		private static final long serialVersionUID = 1L;
		private JTextField layer_1;
		private String[] _extensions;
		private String _label = null, _approve = null;

		public JTextEditor(String name, Dimension d) {
			
			layer_1 = new JTextField(MainWindow.getPropertie("last_file","C:\\"));
			layer_1.setBorder(null);
			layer_1.setFont(new Font("Verdana",Font.PLAIN, d.height - 10));
			layer_1.setBounds(4, 1, d.width - (64), d.height - 2);
			if (name != null) layer_1.setText(name);
			
			JButton layer_2 = new JButton(_approve == null ? "Abrir" : _approve);
			layer_2.setBounds(d.width - 61, 1, 60, d.height - 2);
			layer_2.setFont(new Font("Verdana",Font.PLAIN,10));
			layer_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser eleitor = new JFileChooser(new File(layer_1.getText()));
					eleitor.setToolTipText("Selecione o arquivo para abrir");
					eleitor.setApproveButtonText("Abrir");
					eleitor.setCurrentDirectory(new File("%root%"));
					eleitor.setFileFilter(new FileFilter(){
						public boolean accept(File file) {
							if (file == null || file.getName() == null || file.getName().isEmpty()) {
								return false;
							}
							String name = file.getName();
							if (_extensions != null && _extensions.length > 0 && name.lastIndexOf(".") >= 0) {
								String ext_a = name.substring(name.lastIndexOf("."));
								for (String ext_b : _extensions) {
									if (ext_b != null && ext_a.toLowerCase().contains(ext_b.toLowerCase())) {
										return true;
									}
								}
								return false;
							}
							return true;
						}
						public String getDescription() {
							if (_label != null) {
								return _label;
							}
							if (_extensions != null && _extensions.length > 0) {
								String out = "";
								for (String ext : _extensions) {
									out += (out.isEmpty() ? "" : ", ") + ext;
								}
								return out;
							}
							return null;
						}
						
					});
					int result = eleitor.showSaveDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						String name = (eleitor.getSelectedFile() != null ? eleitor.getSelectedFile().getName() : "");
						String path = (eleitor.getSelectedFile() != null ? eleitor.getSelectedFile().getAbsolutePath() : "");
						path = path.replace(name, "");
						if (name != null && !name.toLowerCase().contains(".") && (eleitor.getSelectedFile() != null && !eleitor.getSelectedFile().exists())) {
							name += ".SQL";
						}
						path += name;
						layer_1.setText(path);
						ActionListener[] list = layer_1.getActionListeners();
						if (list != null) {
							for (ActionListener action : list) {
								action.actionPerformed(null);
							}
						}
					}
					
				}
			});
			
			this.setLayout(null);
			this.setBounds(0, 0, d.width, d.height);
			this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			this.setOpaque(true);
			this.setBackground(layer_1.getBackground());
			this.add(layer_1);
			this.add(layer_2);
			
		}
		
		public void setApproveButtonLabel(String label) {
			_approve = label;
		}
		
		public void setSupportedFileExtensions(String[] list, String label) {
			_extensions = list;
			_label = label;
		}
		
		public void addKeyListener(KeyListener keyListener) {
			layer_1.addKeyListener(keyListener);
		}
		
		public void addActionListener(ActionListener actionlistener) {
			layer_1.addActionListener(actionlistener);
		}
		
		public String getText() {
			return layer_1.getText();
		}
		
		public void setText(String t) {
			layer_1.setText(t);
		}
		
		public void setForeground(Color c) {
			if (c != null && layer_1 != null) layer_1.setForeground(c);
		}
	}