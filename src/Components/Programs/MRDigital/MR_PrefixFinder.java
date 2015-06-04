package Components.Programs.MRDigital;


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

public class MR_PrefixFinder {
	private JQDialog _FRAME;
	private Font _FONT = null;
	private JTextArea _LOG;
	private SQLConnectionManager _CONNECTION;
	private List<String> _TABLE_LIST = new ArrayList<String>();
	private Thread _ACTIVE_THREAD;
	private Logger _log;
	
	/** -CONSTRUTOR- */
	public MR_PrefixFinder(SQLConnectionManager connection) {
		_CONNECTION = connection;
	}

	public void startPrograma() {
		_log = MainWindow.getActiveLog();
		
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);

		_FRAME = new JQDialog(MainWindow.getMainFrame(), "JQuery Analizer - Localiza e lista de forma ordenada os Prefixos de PI utilizados no publi e os prefixos ainda disponíveis.");
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
			public void windowActivated(WindowEvent arg0) { }
			public void windowClosed(WindowEvent arg0) { }
			public void windowClosing(WindowEvent arg0) {
				_FRAME.dispose();
			}
			public void windowDeactivated(WindowEvent arg0) { }
			public void windowDeiconified(WindowEvent arg0) { }
			public void windowIconified(WindowEvent arg0) { }
			public void windowOpened(WindowEvent arg0) { }
		});
		
		_LOG = new JTextArea();
		_LOG.setFont(_FONT);
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

			@Override
			public void actionPerformed(ActionEvent arg0) {
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
					List<String> list = new ArrayList<String>();
					_LOG.append("[#] Localizando tabelas de CLIENTES e de PIT's, seja paciente!!!\n");
					
					if (_log != null) _log.warning("\t[»»»] Tool: { PIT prefix }\tBEGIN");
					
					for (String table : _CONNECTION.getTables()) {
						if (table != null && (table.toLowerCase().contains("pit") || table.toLowerCase().contains("cli")) && (table.endsWith("1") || table.endsWith("1") || table.endsWith("2") || table.endsWith("3") || table.endsWith("4") || table.endsWith("5") || table.endsWith("6") || table.endsWith("7") || table.endsWith("8") || table.endsWith("9") || table.endsWith("0"))) {
							_LOG.append("         » " + table + "\n");
							list.add(table);
						}
					}
					if (list.size() > 0) {
						_TABLE_LIST = list;
						Execute exec = new Execute();
						_ACTIVE_THREAD = new Thread(exec);
						_ACTIVE_THREAD.start();
					}
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		});

		_FRAME.pack();
		_FRAME.setVisible(true);
	}
	
	private class Execute implements Runnable {
		public void run() {
			String sql = "SELECT DISTINCT t.prefixo FROM (";
			int n = 0;
			
			if (_log != null) _log.info("\t[***] Tool: { PIT prefix }\tAnalyzed tables: [" + _TABLE_LIST.toString().replace("{", "").replace("}", "") + "]");
			
			for (String table : _TABLE_LIST.toArray(new String[_TABLE_LIST.size()])) {
				++n;
				if (table != null) {
					sql += (n > 1 ? " UNION ALL " : "") + (table.toLowerCase().contains("pit") ? "SELECT DISTINCT (CASE WHEN pit IS NOT NULL THEN LEFT(pit, 2) ELSE '' END) AS 'prefixo' FROM " + table + " WHERE pit IS NOT NULL AND pit<>''" : "SELECT DISTINCT prefixo_pi AS 'prefixo' FROM " + table + " WHERE prefixo_pi IS NOT NULL AND prefixo_pi<>''");
				}
			}
			sql += ") AS t";
			try {
				ResultSet rs = _CONNECTION.executeQuery(sql);
				List<String> prefixos_usados = new ArrayList<String>();
				while (rs.next()) {
					prefixos_usados.add(rs.getString(1));
				}
				
				List<String> prefixos_possiveis = new ArrayList<String>();
				String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890.-=+";
				
				for (int i = 0; charset.length() > i; i++) {
					for (int j = 0; charset.length() > j; j++) {
						prefixos_possiveis.add(charset.charAt(i) + "" + charset.charAt(j));
					}
				}
				_LOG.append("[>] O mapa de caracteres utilizado no processo foi: '" + charset + "'.\n");
				_LOG.append("[>] Já foram utilizados: " + prefixos_usados.size() + " códigos de prefixos distintos de um total de: " + prefixos_possiveis.size() + " combinações possiveis.\n");
				_LOG.append("[>] Segue abaixo a lista dos códigos de prefixos JÁ UTILIZADOS:\n");
				for (int i = 0; prefixos_usados.size() > i; i++) {
					if (i > 0 && i % 5 == 0) {
						_LOG.append("\n");
					}
					_LOG.append("\t" + prefixos_usados.get(i));
				}
				_LOG.append("\n");
				List<String> prefixos_disponiveis = new ArrayList<String>();
				boolean exists = false;
				for (int i = 0; prefixos_possiveis.size() > i; i++) {
					exists = false;
					for (int j = 0; prefixos_usados.size() > j; j++) {
						if (exists) { break; }
						else if (prefixos_usados.get(j).equalsIgnoreCase(prefixos_possiveis.get(i))) {
							exists = true;
						}
					}
					if (!exists) {
						prefixos_disponiveis.add(prefixos_possiveis.get(i));
					}
				}
				_LOG.append("[>] Ainda existem: " + prefixos_disponiveis.size() + " códigos de prefixos disponiveis para utilização.\n");
				_LOG.append("[>] Segue abaixo a lista dos códigos de prefixos DISPONÍVEIS:\n");
				for (int i = 0; prefixos_disponiveis.size() > i; i++) {
					if (i > 0 && i % 5 == 0) {
						_LOG.append("\n");
					}
					_LOG.append("\t" + prefixos_disponiveis.get(i));
				}
				
				if (_log != null) _log.info("\t[***] Tool: { PIT prefix }\tCharset: [" + charset + "]");
				if (_log != null) _log.info("\t[***] Tool: { PIT prefix }\tUsed prefix: [" + prefixos_possiveis.toString().replace("{", "").replace("}", "") + "]");
				if (_log != null) _log.info("\t[***] Tool: { PIT prefix }\tAvailable prefix: [" + prefixos_disponiveis.toString().replace("{", "").replace("}", "") + "]");
				if (_log != null) _log.warning("\t[«««] Tool: { PIT prefix }\tEND");
				
				_LOG.append("\n");
				_LOG.append("[#] Concluído!");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}