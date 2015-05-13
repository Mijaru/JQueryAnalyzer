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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;







import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JQDialog;

public class MR_AjustarEventosFatura {
	private JQDialog _FRAME;
	private Font _FONT = null;
	private JTextArea _LOG;
	private SQLConnectionManager _CONNECTION;
	private Thread _ACTIVE_THREAD;
	private String _backup, _source, SQL = "";
	private List<String> _SUPPORT_NAMESPACES = new ArrayList<String>();
	
	/** -CONSTRUTOR- */
	public MR_AjustarEventosFatura(SQLConnectionManager connection) {
		_CONNECTION = connection;
		
		_SUPPORT_NAMESPACES.add("http://www.portalfiscal.inf.br/nfe"); // -- Brasilia DF
	}

	public void startProgram() {
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);

		_FRAME = new JQDialog(MainWindow.getMainFrame(), "JQuery Analizer - Ajustar eventos do faturamento");
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
		_LOG.setFont(_FONT);
		_LOG.append("[#] Este script vai reorganizar o campo de EVENTOS do faturamento\n[>] São suportados as seguintes estrutura XML dos municipios:\n");
		_LOG.append("         »  Prefeitura de Brasília/DF.\n");
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
				int option = JOptionPane.showConfirmDialog(null, "Incluir no clipboard os comandos SQL utilizados?", "JQueryAnalizer - Confirmação", JOptionPane.QUESTION_MESSAGE);
				StringSelection selecao = null;
				selecao = new StringSelection(_LOG.getText() + (option == JOptionPane.YES_OPTION ? "\r\n*** QUERYES ***\r\n" + SQL : "")); 
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
					boolean pass = true;
					_backup = null;
					_source = JOptionPane.showInputDialog(null, "Informe o nome da tabela a analisar, por exemplo 'fat01e'", "Informe o nome da tabela", JOptionPane.QUESTION_MESSAGE);
					_LOG.append("---\n");
					Exception e = null;
					int option = JOptionPane.showConfirmDialog(null, "Deseja efetuar um backup da tabela indicada antes de realizar o procedimento?", "JQueryAnalizer - Confirmaçao", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (option == JOptionPane.YES_OPTION) {
						pass = false;
						DateFormat df = new SimpleDateFormat("_YYYYMMdd_HHmmss");
						_backup = _source + df.format(new Date());
						e = _CONNECTION.executeUpdate(("CREATE TABLE `[backup]` LIKE `[source]`").replace("[backup]", _backup).replace("[source]", _source));
						if (e != null) {
							_LOG.append("» Erro ao criar tabela de backup {" + _backup + "}: " + e.getMessage() + "\n" + e.getCause() + "\n");
						}
						else {
							e = _CONNECTION.executeUpdate(("INSERT INTO `[backup]` SELECT * FROM `[source]`").replace("[backup]", _backup).replace("[source]", _source));
							if (e != null) {
								_LOG.append("» Erro ao copiar conteudo da tabela atual {" + _source + "} para a tabela de backup {" + _backup + "}: " + e.getMessage() + "\n" + e.getCause() + "\n\n");
							}
							else {
								pass = true;
							}
						}
						
					}
					if (pass) {
						_LOG.append("» Iniciando analise dos objetos, esse processo pode demorar um pouco, aguarde...\n");
						Execute exec = new Execute();
						_ACTIVE_THREAD = new Thread(exec);
						_ACTIVE_THREAD.start();
					}
					else {
						_LOG.append("» Processo abortado devido a erros existentes durante o backup da tabela.\n");
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
			try {
				ResultSet rs = _CONNECTION.executeQuery("SELECT enviado, retorno, SUBSTRING(idfat, 3, 7), recno FROM " + _source + " WHERE tr IN ('EN', 'DI') AND sistema='NFE'");// AND LENGTH(enviado)>(64*1024)");
				String xml_e = null;
				@SuppressWarnings("unused")
				String xml_r = null;
				String idfat = null;
				int	   recno = 0;
				
				SAXBuilder builder = null;
				InputStream stream = null;
				Document doc = null;
				Element root = null, IDLote = null, NFe = null;
				List<Element> list = null;
				boolean found = false;
				Connection con = _CONNECTION.getConnection();
				PreparedStatement st = null;
				XMLOutputter xout = new XMLOutputter(); 
				xout.setFormat(Format.getPrettyFormat());

				while (rs.next()) {
					found = false;
					xml_e = rs.getString(1);
					xml_r = rs.getString(2);
					idfat = rs.getString(3);
					recno = rs.getInt(4);
					
					_LOG.append("» Analisando registro: " + idfat + " / " + recno + "\n");
					_LOG.setCaretPosition(_LOG.getText().length());
					
					builder = new SAXBuilder();	
					stream  = new ByteArrayInputStream(xml_e.getBytes("UTF-8"));
					doc 	= null;
					doc     = builder.build(stream);
					root    = doc.getRootElement();
					
					if (!_SUPPORT_NAMESPACES.contains(root.getNamespaceURI())) {
						_LOG.append("         » Estrutura do XML não suportada! (Namespace URI: " + root.getNamespaceURI() + ")\n");
						_LOG.setCaretPosition(_LOG.getText().length());
						continue;
					}
					else {
						_LOG.append("         » XML namespace: '" + root.getNamespaceURI() + "'\n");
						_LOG.setCaretPosition(_LOG.getText().length());
					}
					
					list    = root.getChildren();
					for (Element element : list) {
						if (element.getName().equalsIgnoreCase("IDLOTE")) {
							IDLote = element.clone();
						}
						else if (element.getName().equalsIgnoreCase("NFE")) {
							NFe = element.clone();
							ElementFilter filter = new ElementFilter("cNF");
							for (Element c : NFe.getDescendants(filter)){
								if (c.getTextNormalize().contains(idfat)) {
									found = true;
									break;
								}
							}
							if (found) {
								break;
							}
						}
					}
					if (NFe != null) {
						root.removeContent();
						if (IDLote != null) {
							root.addContent(IDLote);
						}
						root.addContent(NFe);
						StringWriter sw = new StringWriter();
						xout.output(doc, sw);
						StringBuffer sb = sw.getBuffer();
						IDLote = NFe = null;
						_LOG.append("                  » Realizando ajuste... (RPS " + idfat + ", XML ajustado em " + (sb.length() / 1024) + "kb)\n");
						_LOG.setCaretPosition(_LOG.getText().length());
						st = con.prepareStatement(("UPDATE [source] SET enviado=? WHERE recno=?").replace("[source]", _source));
						st.setString(1, sb.toString());
						st.setInt(2, recno);
						SQL += st.toString().substring(st.toString().indexOf( ": ") + 2) + ";\r\n";
						if (!st.execute() && st.getUpdateCount() >= 1) {
							_LOG.append("                  » Ajuste realizado com sucesso!\n");
						}
						else {
							_LOG.append("                  » Ocorreu algum erro ao realizar o ajuste!\n");
						}
						_LOG.setCaretPosition(_LOG.getText().length());
						
					}
					else {
						_LOG.append("                  » Não foi localizado conteudo para ajuste neste registro.\n");
					}

				}
				_LOG.append("» Concluído!\n");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
}