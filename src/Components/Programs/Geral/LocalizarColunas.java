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
import java.sql.ResultSetMetaData;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JQDialog;

public class LocalizarColunas {
	private JQDialog _FRAME;
	private Font _FONT = null;
	private JTextArea _LOG;
	private SQLConnectionManager _CONNECTION;
	
	/** -CONSTRUTOR- */
	public LocalizarColunas(SQLConnectionManager connection) {
		_CONNECTION = connection;
	}

	public void startPrograma() {
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);

		_FRAME = new JQDialog(MainWindow.getMainFrame(), "JQuery Analizer - Localiza as tabelas com determinada coluna.");
		_FRAME.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_FRAME.setPreferredSize(new Dimension(550, 400));
		_FRAME.setMaximumSize(new Dimension(550, 400));
		_FRAME.setMinimumSize(new Dimension(550, 400));
		_FRAME.setLocationRelativeTo(null);
		_FRAME.getGlassPane().setBackground(new Color(180, 191, 222));
		_FRAME.setResizable(false);
		_FRAME.getContentPane().setLayout(null);
		_FRAME.addWindowListener(new WindowListener(){
			public void windowClosing(WindowEvent arg0) {
				_FRAME.dispose();
			}
			public void windowActivated(WindowEvent arg0) { }
			public void windowClosed(WindowEvent arg0) { }
			public void windowDeactivated(WindowEvent arg0) { }
			public void windowDeiconified(WindowEvent arg0) { }
			public void windowIconified(WindowEvent arg0) { }
			public void windowOpened(WindowEvent arg0) { }
			
		});
		
		_LOG = new JTextArea();
		_LOG.setFont(new Font("Courier New", Font.ROMAN_BASELINE, 14));
		_LOG.setText("INSTRU��ES:\n-----------\nClique em iniciar e informe o nome do campo que deseja\nlocalizar e aguarde a conclus�o da busca e os resultados\nser�o listados nesta tela.\n\nFunciona com MYSQL e MS SQL SERVER.\n\n");
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

			@Override
			public void actionPerformed(ActionEvent b) {
				int found = 0;
				try {
					String campo = JOptionPane.showInputDialog(null, "Informe o nome da coluna que deseja localizar", "JQueryAnalizer - Confirma��o", JOptionPane.QUESTION_MESSAGE);
					_LOG.append("� Localizando tabelas com o campo: " + campo + "\n");
					ResultSet rs = null;
					ResultSetMetaData rs_meta = null;
					for (String table : _CONNECTION.getTables()) {
						try {
							rs = _CONNECTION.executeQuery("SELECT * FROM " + table + " WHERE 1=0");
							rs_meta = rs.getMetaData();
							for (int i = 1; i <= rs_meta.getColumnCount(); i++) {
								if ((campo.contains("%") && rs_meta.getColumnName(i).toLowerCase().contains(campo.replace("%", "").toLowerCase())) || rs_meta.getColumnName(i).equalsIgnoreCase(campo)) {
									++found;
									_LOG.append("*** " + table + "\t[" + rs_meta.getColumnName(i) + "]\t -> " + rs_meta.getColumnTypeName(i) + "(" + rs_meta.getColumnDisplaySize(i) + ")" + "\n");
								}
							}
							
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					_LOG.append("� Concluido! " + found + " tabelas localizadas.\n");
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		});

		_FRAME.pack();
		_FRAME.setVisible(true);
	}
	
	
}