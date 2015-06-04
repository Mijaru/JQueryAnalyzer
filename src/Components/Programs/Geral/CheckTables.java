package Components.Programs.Geral;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JQDialog;

public class CheckTables {
	private JQDialog _FRAME;
	private Font _FONT = null;
	private JTextArea _LOG;
	private SQLConnectionManager _CONNECTION;
	private Thread _ACTIVE_THREAD;
	private List<String> _TABLES = new ArrayList<String>();
	private Logger _log;
	
	/** -CONSTRUTOR- */
	public CheckTables(SQLConnectionManager connection) {
		_CONNECTION = connection;
	}

	public void startPrograma() {
		_log = MainWindow.getActiveLog();
		
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);

		_FRAME = new JQDialog(MainWindow.getMainFrame(), "JQuery Analizer - Verifica o status atual de cada tabela.");
		_FRAME.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_FRAME.setPreferredSize(new Dimension(550, 400));
		_FRAME.setMaximumSize(new Dimension(550, 400));
		_FRAME.setMinimumSize(new Dimension(550, 400));
		_FRAME.setLocationRelativeTo(null);
		_FRAME.getGlassPane().setBackground(new Color(180, 191, 222));
		_FRAME.setResizable(false);
		_FRAME.getContentPane().setLayout(null);
		_FRAME.setIconImages(MainWindow.getMainIconList());
		_FRAME.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent event) { }
			public void windowClosed(WindowEvent event) { }
			public void windowClosing(WindowEvent event) {
				System.out.println("Encerrando a normalização por requisição do usuário");
				_FRAME.dispose();
			}
			public void windowDeactivated(WindowEvent event) { }
			public void windowDeiconified(WindowEvent event) { }
			public void windowIconified(WindowEvent event) { }
			public void windowOpened(WindowEvent event) { }
		});
		
		_LOG = new JTextArea();
		_LOG.setFont(_FONT);
		_LOG.setText("[#] Este script verifica o status das tabelas existentes na base selecionada.\n[>] MySQL: Ao clicar em iniciar o script vai efetuar um diagnóstico nas tabelas.\n[>] Microsoft SQL Server: O script vai efetuar a reparação automatica das tabelas.\n\n");
		JScrollPane b1 = new JScrollPane(_LOG);
		b1.setBounds(10, 10, 525, 310);
		b1.setBorder(new LineBorder(Color.GRAY, 1, true));
		b1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		_FRAME.getContentPane().add(b1);
		
		JButton copiar = new JButton("Copiar");
		copiar.setBounds(165, 325, 150, 35);
		copiar.setFont(_FONT);
		_FRAME.add(copiar);
		
		copiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Clipboard teclado = Toolkit.getDefaultToolkit().getSystemClipboard();  
				StringSelection selecao = new StringSelection(_LOG.getText()); 
				teclado.setContents(selecao, null);  
			}
		});

		
		JButton executar = new JButton("Iniciar");
		executar.setBounds(10, 325, 150, 35);
		executar.setFont(_FONT);
		_FRAME.add(executar);
		
		executar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent b) {
				try {
					_LOG.append("---\n");
					_LOG.append("[>] Localizando tabelas do sistema, seja paciente!!!\n");
					for (String table : _CONNECTION.getTables()) {
						_LOG.append("      » " + table + "\n");
						_TABLES.add(table);
					}
					_LOG.append("\n[>] Executando verificação nas tabelas localizadass!\n");
					
					_log.warning("\t[»»»] Tool: { Check tables }\tBEGIN");
					
					switch (_CONNECTION.getServerType()) {
						case 3: // -- Microsoft SQL Server
							String item = "ALTER DATABASE " + _CONNECTION.getDatabase() + " SET single_user WITH no_wait;";
							_LOG.append("   » " + item + "\n");
							Exception e = _CONNECTION.executeUpdate(item);
							_LOG.append("	   > " + (e == null ? "OK" : "Erro: " + e.getCause()) + "\n");
							_log.info("\t[***] Tool: { Check tables }\t" + item + (e == null ? "" : " [Exception: " + e.getMessage() + "]"));
							break;
					}
					Execute exec = new Execute();
					_ACTIVE_THREAD = new Thread(exec);
					_ACTIVE_THREAD.start();
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		});

		_FRAME.pack();
		_FRAME.setVisible(true);
	}
	
	private class Execute implements Runnable {
		public void run() {
			String item = null;
			try {
				ResultSet rs = null;
				for (String table : _TABLES) {
					switch (_CONNECTION.getServerType()) {
						// -- MySQL
						case 0:
							item = "CHECK TABLE `" + table.trim() + "` EXTENDED";
							rs = _CONNECTION.executeQuery(item);
							_LOG.append("   » " + item + "\n");
							_log.info("\t[***] Tool: { Check tables }\t" + item);
							if (rs == null) {
								Exception e = _CONNECTION.getLastError();
								_LOG.append("      > Erro: " + e.getMessage() + " [" +e.getCause()+ "]\n");
								_log.info("\t   ›  Tool: { Check tables }\tException: " + _CONNECTION.getLastError().getMessage());
							}
							while (rs != null && rs.next()) {
								_LOG.append("      › Table: " + rs.getString(1) + "\n      › Operation: " + rs.getString(2) + "\n      › Type: " + rs.getString(3) + "\n      › Text: " + rs.getString(4) + "\n\n");
								_log.info("\t   1. Tool: { Check tables }\tTable: " + rs.getString(1));
								_log.info("\t   2. Tool: { Check tables }\tOperation: " + rs.getString(2));
								_log.info("\t   3. Tool: { Check tables }\tType: " + rs.getString(3));
								_log.info("\t   4. Tool: { Check tables }\tText: " + rs.getString(4));
							}
							rs.close();
							break;
						// -- Microsoft SQL Server
						case 3:
							item = "DBCC CHECKTABLE ('" +table.trim()+ "', REPAIR_ALLOW_DATA_LOSS);";
							_LOG.append("   » " + item + "\n");
							_log.info("\t[***] Tool: { Check tables }\t" + item);
							Exception e = _CONNECTION.executeUpdate(item);
							if (e != null) { 
								_LOG.append("      > Erro: " + e.getMessage() + " [" +e.getCause()+ "]\n");
								_log.info("\t   ›  Tool: { Check tables }\tException: " + _CONNECTION.getLastError().getMessage());
							}
							break;
					}
					_LOG.setCaretPosition(_LOG.getText().length());
				}
				switch (_CONNECTION.getServerType()) {
					// -- Microsoft SQL Server
					case 3:
						item = "ALTER DATABASE " + _CONNECTION.getDatabase() + " SET multi_user WITH no_wait;";
						_LOG.append("   » " + item + "\n");
						Exception e = _CONNECTION.executeUpdate(item);
						_LOG.append("      > " + (e == null ? "OK" : "Erro: " + e.getMessage() + " [" +e.getCause()+ "]") + "\n");
						_log.info("\t[***] Tool: { Check tables }\t" + item + (e == null ? "" : " [Exception: " + e.getMessage() + "]"));
						break;
				}
				_LOG.append("[#] Concluído!");
				_log.warning("\t[«««] Tool: { Check tables }\tEND");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
}