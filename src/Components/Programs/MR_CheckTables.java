package Components.Programs;


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
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import javolution.util.FastList;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JQDialog;

public class MR_CheckTables {
	private JQDialog _FRAME;
	private Font _FONT = null;
	private JTextArea _LOG;
	private SQLConnectionManager _CONNECTION;
	private Thread _ACTIVE_THREAD;
	
	/** -CONSTRUTOR- */
	public MR_CheckTables(SQLConnectionManager connection) {
		_CONNECTION = connection;
	}

	public void startPrograma() {
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);

		_FRAME = new JQDialog(MainWindow.getMainFrame(), "JQueryAnalizer [MRDigital] - Verifica o status atual de cada tabela.");
		_FRAME.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_FRAME.setPreferredSize(new Dimension(550, 400));
		_FRAME.setMaximumSize(new Dimension(550, 400));
		_FRAME.setMinimumSize(new Dimension(550, 400));
		_FRAME.setLocationRelativeTo(null);
		_FRAME.getGlassPane().setBackground(new Color(180, 191, 222));
		_FRAME.setResizable(false);
		_FRAME.getContentPane().setLayout(null);
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
		_LOG.setFont(new Font("Courier New", Font.ROMAN_BASELINE, 14));
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
					boolean pass = false;
					if (_LOG.getText() != null && !_LOG.getText().isEmpty()) {
						int option = JOptionPane.showConfirmDialog(null, "Deseja checar apenas a lista de tabelas informada no corpo do assistente?", "JQueryAnalizer - Confirmaçao", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						pass = (option == JOptionPane.YES_OPTION);
					}
					if (!pass) {
						_LOG.setText("");
						_LOG.append("» Localizando tabelas do sistema, seja paciente!!!\n");
						for (String table : _CONNECTION.getTables()) {
							_LOG.append("   " + table + "\n");
						}
					}
					_LOG.append("\n» Executando verificação nas tabelas selecionadas!\n");
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
		@Override
		public void run() {
			StringTokenizer st = new StringTokenizer(_LOG.getText(),"\n");
			st.nextToken();
			FastList<String> list = new FastList<String>();
			String item = null;
			while (st.hasMoreElements()) {
				item = st.nextToken();
				if (item.startsWith("   ")) {
					list.add(item);
				}
				else {
					break;
				}
			}
			try {
				ResultSet rs = null;
				for (String table : list) {
					item = "CHECK TABLE " + table.trim() + " EXTENDED";
					rs = _CONNECTION.executeQuery(item);
					_LOG.append("   » " + item + "\n");
					if (rs == null) {
						_LOG.append("    - erro detectado: " + _CONNECTION.getLastError() + "\n");
					}
					while (rs != null && rs.next()) {
						_LOG.append("    - table: " + rs.getString(1) + "\n    - operation: " + rs.getString(2) + "\n    - type: " + rs.getString(3) + "\n    - text: " + rs.getString(4) + "\n\n");
					}
					rs.close();
				}
				_LOG.append("» Concluído!");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
}