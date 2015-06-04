package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JProgressLabel;
import Components.MainWindowComponents.JQDialog;
import Components.MainWindowComponents.JQueryPane;
import Components.MainWindowComponents.JTextEditor;

public class RestorePubli {
	private JQDialog _DIALOG;
	private JTextEditor _file;
	private JButton _run;
	private JButton _close;
	private Thread _thread;
	private JProgressLabel _progress;
	private Font _default_font = new Font("Verdana", Font.ROMAN_BASELINE, 11);
	private JComboBox<String> _option_in_cs; 
	private SQLConnectionManager _CONNECTION;
	private int _script_error;
	private long _script_line;
	private long _script_line_start_block;
	/** -- CONVERTE O FORMATO DE ARQUIVO PubliSQL para SQL Server [Limpeza] -- */
	
	
	public RestorePubli() {
		Component active = MainWindow.getActiveTab();
		if (active instanceof JQueryPane) {
			JQueryPane pane = (JQueryPane)active;
			_CONNECTION = pane.getConnection();
			String mensagem = null;
			if (_CONNECTION == null || (_CONNECTION != null && !_CONNECTION.isConnected())) {
				mensagem = "<html>Você não está conectado a um banco de dados!<br><i>*** Para que um backup seja restaurado você deve primeiro conectar a uma base de dados e selecionar o banco de dados de destino!</i></html>";
			}
			else if (!_CONNECTION.isDatabaseSelected()){
				mensagem = "<html>Você está conectado a um banco de dados mas ainda não selecionou o banco de dados de destino,<br><i>*** Selecione um banco de dados de destino e tente novamente!</i></html>";
			}
			if (mensagem != null) {
				JOptionPane.showMessageDialog(null, mensagem, "JQueryAnalizer - Alerta!", JOptionPane.OK_OPTION);
				return;				
			}
		}
		
		
		_DIALOG = new JQDialog(MainWindow.getMainFrame(), "JQuery Analizer - Assistente para restauração de backup's [Publi]");
		Dimension size = new Dimension(500, 200);
		_DIALOG.setMaximumSize(size);
		_DIALOG.setMinimumSize(size);
		_DIALOG.setPreferredSize(size);
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_DIALOG.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent a) { }
			public void windowClosed(WindowEvent a) { }
			@SuppressWarnings("deprecation")
			public void windowClosing(WindowEvent arg0) {
				if (_thread != null && _thread.isAlive()) {
					int option = JOptionPane.showConfirmDialog(_DIALOG, "Existe uma restauração de backup em execução, tem certeza que deseja interromper?", "Confirmação", JOptionPane.YES_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						_thread.stop();
						_thread = null;
					}
					else {
						return;
					}
				}
				_DIALOG.dispose();
			}
			public void windowDeactivated(WindowEvent a) { }
			public void windowDeiconified(WindowEvent a) { }
			public void windowIconified(WindowEvent a) { }
			public void windowOpened(WindowEvent a) { }
		});
		JLabel text1 = new JLabel();
		text1.setFont(new Font("Verdana",Font.PLAIN,12));
		text1.setText("<html>Selecione o <b>arquivo de origem</b>:</html>");
		text1.setBounds(10,5,475,20);
		text1.setOpaque(true);
		_DIALOG.add(text1);
		
		_file = new JTextEditor(null, new Dimension(475,24));
		_file.setBounds(10,25,475,24);
		_DIALOG.add(_file);
		
		JLabel text4 = new JLabel();
		text4.setFont(new Font("Verdana",Font.PLAIN,12));
		text4.setText("<html><b>Progresso</b> da restauração do backup:</html>");
		text4.setBounds(10,60,475,20);
		text4.setOpaque(true);
		_DIALOG.add(text4);
		
		_progress = new JProgressLabel();
		_progress.setPanelBounds(10,80,475,35);
		_progress.setText("<html>Assistente pronto para inciar a restauração do backup!</html>");
		_DIALOG.add(_progress);	
		
		
		_run = new JButton("<html>Iniciar Importação</html>");
		_run.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_run.setMnemonic(KeyEvent.VK_I);
		_run.setBounds(255,125,150,40);
		_run.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				_script_error = 0;
				_script_line_start_block = 0;
				toogleActions(false);
				processBackup process = new processBackup(_file.getText());
				_thread = new Thread(process);
				_thread.start();
			}
		});
		_DIALOG.add(_run);
			
		_close = new JButton("<html>Sair</html>");
		_close.setMnemonic(KeyEvent.VK_S);
		_close.setFont(new Font("Verdana",Font.ROMAN_BASELINE,12));
		_close.setBounds(410,125,75,40);
		_close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				_DIALOG.dispose();					
			}
			
		});
		_DIALOG.add(_close);
			
		JLabel opcoes = new JLabel();
		opcoes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "<html>&nbsp;<b>Codificação</b> do arquivo de backup&nbsp;</html>", SwingConstants.CENTER, SwingConstants.CENTER, _default_font, Color.BLACK));
		opcoes.setBounds(10, 120, 235, 45);
		_DIALOG.add(opcoes);
		
		
		_option_in_cs = new JComboBox<String>();
		_option_in_cs.setEditable(true);
		_option_in_cs.setBounds(10,18,215,20);
		_option_in_cs.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
		Iterator<Charset> list = Charset.availableCharsets().values().iterator();
		while (list.hasNext()) {
			_option_in_cs.addItem(list.next().name());	
		}
		_option_in_cs.setSelectedItem("ISO-8859-1");
		opcoes.add(_option_in_cs);
	}

	public void startProgram() {
		if (_DIALOG != null) {
			JOptionPane.showMessageDialog(null, "<html>Esta modalidade de restauração de backups é indicada para os backups feitos a partir do publi" +
												"<br>(para versões posteriores ao dia 16/03/2011)" +
												"<br>Esta modalidade de backup suporta blocos de comandos com até 128M Bytes.<br>" +
												"<br><font color=red><b>ATENÇÃO com a <u>página de códigos</u> utilizada na leitura do arquivo de backup</b></font></html>", "JQueryAnalizer - Aviso importante", JOptionPane.OK_OPTION);
			_DIALOG.setVisible(true);
		}
	}
	
	
	private void toogleActions(boolean state) {
		_run.setEnabled(state);
		_close.setEnabled(state);
		_option_in_cs.setEnabled(state);
	}
	
	
	private class processBackup implements Runnable {
		private String _file;
		
		private long _paint_time = 0L;
		
		public processBackup(String path) {
			_file = path;
		}
		
		@SuppressWarnings("resource")
		public void run() {
			FileInputStream fout = null;  
			FileChannel file_channel = null;
			try {
				if (_CONNECTION.isConnected() && _CONNECTION.isDatabaseSelected()) {
					if (JOptionPane.showConfirmDialog(null, "<html><font color=red size=4><b>Atenção</b>!</font><br>Deseja restaurar o arquivo de backup selecionado para a database:<br><u>'<b>"+ _CONNECTION.getDatabase() +"</b>'</u></html>", "JQueryAnalizer - Confirmação para restauração de backup!", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
						toogleActions(true);
						return;
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "<html><font color=red size=2><b>Atenção</b>!</font><br>Você precisa estar conectado a uma base de dados (e ter uma database selecionada) para restaurar qualquer backup,<br>verifique sua conexão com a base de dados e tente novamente!</u></html>", "JQueryAnalizer - Confirmação para restauração de backup!", JOptionPane.OK_OPTION);
					toogleActions(true);
					return;
				}
				fout = new FileInputStream(_file);  
				file_channel = fout.getChannel();
				
				ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 5);
				
				byte[] byte_historico = new byte[6];
				
				String   command = null;
				
				Charset file = Charset.forName(_option_in_cs.getSelectedItem().toString());
				System.out.println("### Iniciando restauração de backup do Publi,");
				System.out.println("    Charset definido para o arquivo de backup: [" + file.name() + "]");
				byte byte_a = 0; // 

				int length = 0;
				
				float perc_a = 0;
				float perc_b = 0;

				String table = "";
				
				long tables = 0;
				long rows   = 0;
				
				boolean[] string_detector  = {false, false};
				boolean[] comment_detector  = {false, false, false, false}; // {inicio, fim, comentario em linha simples, fim da linha}
				
				long size = file_channel.size();
				long count = 0;
				
				Exception exception = null;
				
				// -- aloca 128mb para o bytebuffer de montagem dos comandos SQL
				ByteBuffer out = ByteBuffer.allocate(1024 * 1024 * 128);
				out.clear();

				for (long i = 0; i < size; i++) {
					buffer.clear();
					length = file_channel.read(buffer, i);
					i += length - 1;
					for (int j = 0; j < length; j++) {
						++count;
						
						byte_a = buffer.get(j);

						byte_historico[5] = byte_historico[4];
						byte_historico[4] = byte_historico[3];
						byte_historico[3] = byte_historico[2];
						byte_historico[2] = byte_historico[1];
						byte_historico[1] = byte_historico[0];
						byte_historico[0] = byte_a;

						if (byte_historico[0] == '\n') {
							++_script_line;
						}
						
						if (!string_detector[0]) {
							/** detecta comentarios do tipo [/#,#/] ou [/##,#/] */
							if (!comment_detector[0]) {
								// -- sinaliza o inicio do comentario de multiplas linhas
								comment_detector[0] = ((byte_historico[0] == '*' && byte_historico[1] == '/'));
								if (comment_detector[0]) {
									//bloc.deleteCharAt(bloc.length() - 1);
									out.position(out.position() - 1);
									continue;
								}
							}
							else if (comment_detector[0] && !comment_detector[1]) {
								// -- sinaliza o final do comentario de multiplas linhas
								comment_detector[1] = ((byte_historico[0] == '/' && byte_historico[1] == '*'));
							}
							/** detecta comentarios do tipo [//], [--] ou [#] */
							if (!comment_detector[0] && !comment_detector[2]) {
								// -- sinaliza o inicio do comentario.                                																																--> GO tratado como comentário para backups do SQL server...
								comment_detector[2] = ((byte_historico[0] == '/' && byte_historico[1] == '/') || (byte_historico[0] == '-' && byte_historico[1] == '-') || (byte_historico[0] == '#') || (byte_historico[2] == '\n' && byte_historico[1] == 'G' && byte_historico[0] == 'O'));
								if (comment_detector[2]) {
									if (out.position() > 0) {
										out.position(out.position() - 1);
									}
									continue;
								}
							}
							else if (!comment_detector[0] && comment_detector[2] && !comment_detector[3] &&/* byte_historico[0] == '\r' && */byte_historico[0] == '\n') {
								// -- sinaliza o final da linha.
								comment_detector[3] = true;
							}							
						}
						
						if (comment_detector[0] == false && comment_detector[2] == false) {						
							string_detector[1] = (string_detector[0] ? stringDelimiter(byte_historico) : false);
							string_detector[0] = (string_detector[0] ? true : stringDelimiter(byte_historico));
							if (string_detector[0] == true && string_detector[1] == true) {
								string_detector[0] = false;
								string_detector[1] = false;
							}
							if (out.position() == 0) {
								_script_line_start_block = _script_line;
							}
							out.put(byte_a);
							
							if (!string_detector[0] && !string_detector[1] && byte_historico[0] == ';') {
								command = null;
								out.position(out.position() - 1);
								out.flip();
								command = file.decode(out).toString().trim();
								out.clear();
								
								if (command != null) {
									if (_thread == null) {
										System.out.println("*** Restauração de backup abortada pelo usuário! (backup do publi)");
										return;
									}
								}
								if (command.startsWith("CREATE")) {
									exception = _CONNECTION.executeUpdate(command);
									++tables;
								}
								else if (command.startsWith("INSERT")) {
									exception = _CONNECTION.executeUpdate(command);
									++rows;
								}
								else if (command.startsWith("DROP") && (!command.contains("DATABASE") || command.startsWith("IF"))) {
									exception = _CONNECTION.executeUpdate(command);
								}
								else if (!command.isEmpty() && !command.equalsIgnoreCase(";")) {
									System.out.println("### Unknow SQL command:\n" + (command.length() > 256 ? command.substring(0,256) + "..." : command) + "\n<- [EOC]");
								}
								
								if (exception == null) {
									// --- System.out.println(".");
								}
								else {
									++_script_error;
									System.out.println("\n~ ERROR! Command Block started at line: '" + _script_line_start_block + "'\n ----> " + exception.getMessage()+"\n[SQL:>]\n" + (command.length() > 1024 ? command.substring(0,1024) + "..." : command) + "\n[<:SQL]\n\n");
									if (JOptionPane.showConfirmDialog(null, "<html>Erro detectado:<br><b>" + exception.getMessage() + "</b><br><br>Deseja seguir com a restauração?</html>", "JQueryAnalizer - Erro ao restaurar backup", JOptionPane.YES_OPTION) == JOptionPane.NO_OPTION) {
										return;
									}
								}
								// -- nome da tabela processada atualmente
								if (command.length() > 256) { table = command.substring(0, 256); }
								else { table = command.substring(0, command.length()); }
								
								if (table.split(" ").length > 2) {	table = "<u>"+ table.split(" ")[2].replace("`", "") + "</u>"; }
								else { table = "<u>Undefined</u>"; }
								command = null;				
							}
							
						}
						else if ((comment_detector[0] && comment_detector[1]) || (comment_detector[2] && comment_detector[3])) {
							comment_detector[0] = comment_detector[1] = comment_detector[2] = comment_detector[3] = false;
						}
						
						// -- progresso da restauracao.
						_paint_time = (_paint_time == 0 ? System.nanoTime() : _paint_time);
						perc_a = ((1.f * count) / size) * 100.f;						
						if (_paint_time + (40000000) <= System.nanoTime() || perc_a == 100) {
							perc_a = (float) (Math.round(perc_a * Math.pow(10,2) ) / Math.pow(10,2));
							perc_b = perc_a - (int) perc_a;
							_progress.setText("<html><i>" + perc_a + ((!Float.toString(perc_b).startsWith("0.0") || perc_b == 0.f) && Integer.parseInt(Float.toString(perc_b).replace("0.", "")) < 10 ? "0" : "") + "%</i> concluido! Processadas: <b>" + tables + "</b> tabelas / <b>" + rows + "</b> blocos. " + table + "</html>");
							_progress.setMainProgress(perc_a);
							_paint_time = System.nanoTime();
						}
					}
					
				}
				if (_script_error > 0) {
					JOptionPane.showMessageDialog(_DIALOG, "<html>Ocorreram <font color=red>" + _script_error + "</font> erros durante a restauração do arquivo,<br>observe o log de erros para verificar os problemas ocorridos.<br><br><i>Esta restauração pode conter dados parciais, portanto não é confiável!</i></html>", "JQueryAnalizer - Restauração finalizada com erros.", JOptionPane.OK_OPTION);
				}
				else {
					JOptionPane.showMessageDialog(_DIALOG, "Restauração concluida com sucesso!", "JQueryAnalizer - Restauração concluida!", JOptionPane.OK_OPTION);					
				}
				toogleActions(true);
				file_channel.close();
				fout.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				toogleActions(true);
				try {
					file_channel.close();
					fout.close();
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	public String charsetTranslate(String content, String encode) {
		if (encode == null) {
			return content;
		}
		byte[] list;
		try {
			list = content.getBytes();
			return new String(list, encode);
		}
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return null;
	}
	
	public boolean stringDelimiter(byte[] byte_historico) {
		if (byte_historico[0] != '\'') {
			return false;
		}
		else {
			if (byte_historico[1] != '\\') {
				return true;
			}
			else {
				int search = 0;
				for (int i = 1; i < byte_historico.length; i++) {
					if (byte_historico[i] == '\\') {
						search++;
					}
					else {
						return (search % 2 == 0);
					}
				}
				return (search % 2 == 0);
			}
		}
	}

	public void expandAll(JTree tree, boolean expand) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();
	    expandAll(tree, new TreePath(root), expand);
	}
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
	    TreeNode node = (TreeNode)parent.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	        for (Enumeration<?> e=node.children(); e.hasMoreElements(); ) {
	            TreeNode n = (TreeNode)e.nextElement();
	            TreePath path = parent.pathByAddingChild(n);
	            expandAll(tree, path, expand);
	        }
	    }
	    if (expand) { tree.expandPath(parent); }
	    else { tree.collapsePath(parent); }
	}
}
