package Components.Programs.MRDigital;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.MainWindowComponents.JQDialog;

public class MR_VerificaCadastros {
	private JQDialog _FRAME;
	private Font _FONT = null;
	private SQLConnectionManager _CONNECTION;
	private JTextArea _LOG;
	private Logger _log;

	/** -CONSTRUTOR- */
	public MR_VerificaCadastros(SQLConnectionManager sql) {
		_CONNECTION = sql;
		init();
	}
	
	public void startProgram() {
		if (_FRAME != null) {
			if (_CONNECTION == null || (_CONNECTION != null && (!_CONNECTION.isConnected() || !_CONNECTION.isDatabaseSelected()))) {
				JOptionPane.showMessageDialog(MainWindow.getMainFrame(), "Você deve selecionar uma database do publi antes de executar este programa!", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
				return;
			}
			if (!_FRAME.isVisible()) {
				_FRAME.setVisible(true);
			}
		}
	}
	
	public void init() {
		_log = MainWindow.getActiveLog();
		
		_FONT = new Font("Verdana", Font.ROMAN_BASELINE, 10);

		_FRAME = new JQDialog(MainWindow.getMainFrame(), "JQuery Analizer - Módulo de verificação do cadastro de CLIENTES e FORENCEDORES");
		_FRAME.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		_FRAME.setPreferredSize(new Dimension(550, 400));
		_FRAME.setMaximumSize(new Dimension(550, 400));
		_FRAME.setMinimumSize(new Dimension(550, 400));
		_FRAME.setLocationRelativeTo(null);
		_FRAME.getGlassPane().setBackground(new Color(180, 191, 222));
		_FRAME.setResizable(false);
		_FRAME.getContentPane().setLayout(null);
		_FRAME.setIconImages(MainWindow.getMainIconList());
		
		_LOG = new JTextArea();
		_LOG.setFont(_FONT);
		JScrollPane b1 = new JScrollPane(_LOG); /* Cria as barras de rolagem */
		b1.setBounds(10, 10, 525, 310);
		b1.setBorder(new LineBorder(Color.GRAY, 1, true));
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
		executar.setMnemonic('I');
		executar.setBounds(10, 325, 150, 35);
		executar.setFont(_FONT);
		_FRAME.add(executar);
		
		executar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent b) {
				_LOG.append("[#] Iniciando verificação de cadastros. Aguarde...\n");
				
				VerifyCad v = new VerifyCad();
				Thread t = new Thread(v);
				t.start();
			}
		});
	}
	
	private class VerifyCad implements Runnable {
		public void run() {
			Connection con = null;
			PreparedStatement st = null;
			if (_CONNECTION == null || !_CONNECTION.isConnected()) {
				JOptionPane.showMessageDialog(null, "<html>A conexão selecionada não está disponível.<br><i>Reestabeleça a conexão e tente novamente</i></html>", "jQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
				return;
			}
			else {
				con = _CONNECTION.getConnection();
			}
			try {
				if (_log != null) _log.warning("\t[»»»] Tool: { Check clients / suppliers info }\tBEGIN");
				
				for (String tn : _CONNECTION.getTables()) {				
					if (tn != null && tn.length() == 5 && (tn.contains("CLI") || tn.contains("cli"))) {
						ResultSet c_data = _CONNECTION.executeQuery("SELECT nome, codigo, razao_soc, cgc, endereco, municipio, estado, cep, telefone, im, inscricao, email FROM "+tn);
						int erros = 0;
						_LOG.append("[#] Verificando a tabela '"+tn.toUpperCase()+"'.\n");
						
						if (_log != null) _log.info("\t[***] Tool: { Check clients info }\tCheck client table: '" + tn + "', begin...");
						
						while (c_data.next()) {
							String razao_social = c_data.getString("razao_soc");
							String cgc          = c_data.getString("cgc");
							String endereco     = c_data.getString("endereco");
							String municipio    = c_data.getString("municipio");
							String estado       = c_data.getString("estado");
							String cep          = c_data.getString("cep");
							String telefone     = c_data.getString("telefone");
							
							String im           = c_data.getString("im");
							String inscricao    = c_data.getString("inscricao");
							
							String email        = c_data.getString("email");
							
							
							StringBuffer list = new StringBuffer(); 
							
							if (_log != null) _log.info("\t[***] Tool: { Check clients info }\tCheck client data: '" + c_data.getString("codigo") + " - " + c_data.getString("nome") + "'");
							
							/** -verificações gerais... step 1- */
							if (razao_social == null || (razao_social != null && razao_social.isEmpty())) {
								list.append("» Erro no campo 'RAZÃO SOCIAL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'RAZAO_SOC' it is not filled");
								++erros;
							}
							if (cgc == null || (cgc != null && cgc.isEmpty())) {
								list.append("»  Erro no campo 'CGC', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'CGC' it is not filled");
								++erros;
							}
							if (endereco == null || (endereco != null && endereco.isEmpty())) {
								list.append("»  Erro no campo 'ENDERECO', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'ENDERECO' it is not filled");
								++erros;
							}
							if (municipio == null || (municipio != null && municipio.isEmpty())) {
								list.append("»  Erro no campo 'MUNICIPIO', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'MUNICIPIO' it is not filled");
								++erros;
							}
							if (estado == null || (estado != null && estado.isEmpty())) {
								list.append("»  Erro no campo 'ESTADO', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'ESTADO' it is not filled");
								++erros;
							}
							if (cep == null || (cep != null && cep.isEmpty())) {
								list.append("»  Erro no campo 'CEP', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'CEP' it is not filled");
								++erros;
							}
						
							if (email == null) {
								list.append("»  Erro no campo 'E-MAIL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'EMAIL' it is not filled");
								++erros;
							}
							if (telefone == null) {
								list.append("»  Erro no campo 'TELEFONE', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'TELEFONE' it is not filled");
								++erros;
							}
							if (inscricao == null) {
								list.append("»  Erro no campo 'INSCRIÇÃO ESTADUAL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'INSCRICAO' it is not filled");
								++erros;
							}
							if (im == null) {
								list.append("»  Erro no campo 'INSCRIÇÃO MUNICIPAL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Field 'IM' it is not filled");
								++erros;
							}
						
							/** -verificações size... step 2- */
							if (telefone != null && telefone.length() > 16) {
								list.append("»  Erro no campo 'TELEFONE', o número de telefone informado é muito grande ["+telefone.length()+" caracteres].\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- Data filled in the field 'TELEFONE' are too big (" + telefone.length() + " characters)");
								++erros;
							}
							if (municipio != null) {
								if (estado != null) {
									st = con.prepareStatement("SELECT * FROM cidades WHERE cidade LIKE ? AND uf=?");
									st.setString(1, ("%[municipio]%").replace("[municipio]", municipio));
									st.setString(2, estado);
									ResultSet verify = null;
									if (st.execute()) {
										verify = st.getResultSet();
									}

									if (verify != null) {
										verify.last();
									}
									else {
										_LOG.append("[#] Análise interrompida devido a algum problema durante a execução da verificação dos municipios cadastrados.");
										return;
									}
									if (verify != null && verify.getRow() == 0) {
											list.append("»  Erro no campo 'MUNICIPIO', não foi possível encontrar o cadastro da cidade '"+municipio.toUpperCase()+"' no cadastro de cidades, esta cidade precisa ser incluída no cadastro. Utilieze o caminho: 'cadastro->tabelas auxiliares->cidades'.\n");
											if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- The city: '" + municipio.toUpperCase() + "' was not located in the cities table");
											++erros;
									}
									else if (verify != null && verify.getString("ibge") == null) {
										list.append("»  Erro no campo 'MUNICIPIO', não foi possível encontrar o código do IBGE para o municipio: '"+municipio.toUpperCase()+"', esta cidade precisa ter seu cadastro alterado. Utilieze o caminho: 'cadastro->tabelas auxiliares->cidades' para efetuar as alterações.\n");
										if (_log != null) _log.info("\t[  *] Tool: { Check clients info }\t\t- The city '" + municipio.toUpperCase() + "' not have IBGE code in the cities table");
										++erros;
									}
									verify.close();
									st.close();
								}
							}
							
							if (list.length() > 0) {
								_LOG.append("\n"+c_data.getString("codigo")+" - "+c_data.getString("nome")+".\n");
								_LOG.append(list.toString());
							}
							else {
								if (_log != null) _log.info("\t[  ›] Tool: { Check clients info }\t\t* Data OK");
							}
							_LOG.setCaretPosition(_LOG.getText().length());	
						}
						_LOG.append("[#] =========================================================================================\n");
						_LOG.append("[#] Total de erros encontrados = "+ erros +"\n[#] Fim da verificação para cadastros de CLIENTES.");
						
						if (_log != null) _log.info("\t[***] Tool: { Check clients info }\tCheck client table: '" + tn + "' end, errors found: '" + erros + "'");
					}
					
					/** ****************************************************** */
					
					if (tn != null && tn.length() == 5 && (tn.contains("FOR") || tn.contains("for"))) {
						ResultSet f_data = _CONNECTION.executeQuery("SELECT razao_soc, cgc, endereco, municipio, estado, cep, telefone, codigo, nome, email, im, inscricao FROM "+tn);
						int erros = 0;
						_LOG.setCaretPosition(_LOG.getText().length());
						_LOG.append("[#] Verificando a tabela '"+tn.toUpperCase()+"'.\n");
						if (_log != null) _log.info("\t[***] Tool: { Check suppliers info }\tCheck supplier table: '" + tn + "', begin...");
						while (f_data.next()) {
							String razao_social = f_data.getString("razao_soc");
							String cgc          = f_data.getString("cgc");
							String endereco     = f_data.getString("endereco");
							String municipio    = f_data.getString("municipio");
							String estado       = f_data.getString("estado");
							String cep          = f_data.getString("cep");
							String email        = f_data.getString("email");
							String telefone     = f_data.getString("telefone");
							String im           = f_data.getString("im");
							String inscricao    = f_data.getString("inscricao");
							
							
							StringBuffer list = new StringBuffer(); 
							
							if (_log != null) _log.info("\t[***] Tool: { Check suppliers info }\tCheck supplier data: '" + f_data.getString("codigo") + " - " + f_data.getString("nome") + "'");
							
							/** -verificações gerais... step 1- */
							if (razao_social == null) {
								list.append("»  Erro no campo 'RAZÃO SOCIAL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'RAZAO_SOC' it is not filled");
								++erros;
							}
							if (cgc == null) {
								list.append("»  Erro no campo 'CGC', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'CGC' it is not filled");
								++erros;
							}
							if (endereco == null) {
								list.append("»  Erro no campo 'ENDERECO', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'ENDERECO' it is not filled");
								++erros;
							}
							if (municipio == null) {
								list.append("»  Erro no campo 'MUNICIPIO', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'MUNICIPIO' it is not filled");
								++erros;
							}
							if (estado == null) {
								list.append("»  Erro no campo 'ESTADO', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'ESTADO' it is not filled");
								++erros;
							}
							if (cep == null) {
								list.append("»  Erro no campo 'CEP', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'CEP' it is not filled");
								++erros;
							}
							if (email == null) {
								list.append("»  Erro no campo 'E-MAIL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'EMAIL' it is not filled");
								++erros;
							}
							if (telefone == null) {
								list.append("»  Erro no campo 'TELEFONE', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'TELEFONE' it is not filled");
								++erros;
							}
							if (inscricao == null) {
								list.append("»  Erro no campo 'INSCRIÇÃO ESTADUAL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'INSCRICAO' it is not filled");
								++erros;
							}
							if (im == null) {
								list.append("»  Erro no campo 'INSCRIÇÃO MUNICIPAL', o campo não foi preenchido.\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Field 'IM' it is not filled");
								++erros;
							}
							/** -verificações size... step 2- */
							if (telefone != null && telefone.length() > 16) {
								list.append("»  Erro no campo 'TELEFONE', o número de telefone informado é muito grande ["+telefone.length()+" caracteres].\n");
								if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- Data filled in the field 'TELEFONE' are too big (" + telefone.length() + " characters)");
								++erros;
							}
							if (municipio != null) {
								if (estado != null) {
									st = con.prepareStatement("SELECT * FROM cidades WHERE cidade LIKE ? AND uf=?");
									st.setString(1, ("%[municipio]%").replace("[municipio]", municipio));
									st.setString(2, estado);
									ResultSet verify = null;
									if (st.execute()) {
										verify = st.getResultSet();
									}
									//st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
									//ResultSet verify = st.executeQuery("SELECT * FROM cidades WHERE cidade LIKE '%"+municipio+"%' AND uf='"+estado+"'");
									if (verify != null) {
										verify.last();
									}
									if (verify != null && verify.getRow() == 0) {
											list.append("»  Erro no campo 'MUNICIPIO', não foi possível encontrar o cadastro da cidade '"+municipio.toUpperCase()+"' no cadastro de cidades, esta cidade precisa ser incluída no cadastro. Utilieze o caminho: 'cadastro->tabelas auxiliares->cidades'.\n");
											if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- The city: '" + municipio.toUpperCase() + "' was not located in the cities table");
											++erros;
									}
									else if (verify != null && verify.getString("ibge") == null) {
										list.append("»  Erro no campo 'MUNICIPIO', não foi possível encontrar o código do IBGE para o municipio: '"+municipio.toUpperCase()+"', esta cidade precisa ter seu cadastro alterado. Utilieze o caminho: 'cadastro->tabelas auxiliares->cidades' para efetuar as alterações.\n");
										if (_log != null) _log.info("\t[  *] Tool: { Check suppliers info }\t\t- The city '" + municipio.toUpperCase() + "' not have IBGE code in the cities table");
										++erros;
									}
									verify.close();
									st.close();
								}
							}
							
							if (list.length() > 0) {
								_LOG.append("\n"+f_data.getString("codigo")+" - "+f_data.getString("nome")+".\n");
								_LOG.append(list.toString());
							}
							else {
								if (_log != null) _log.info("\t[  ›] Tool: { Check suppliers info }\t\t* Data OK");
							}
							_LOG.setCaretPosition(_LOG.getText().length());							
						}
						_LOG.append("[#] =========================================================================================\n");
						_LOG.append("[#] Total de erros encontrados = "+erros+"\n[#] Fim da verificação para cadastros de FORNECEDORES.\n\n");
						
						if (_log != null) _log.info("\t[***] Tool: { Check suppliers info }\tCheck supplier table: '" + tn + "' end, errors found: '" + erros + "'");

						JOptionPane.showMessageDialog(null, "Verificação concluída, foram encontrados: " + erros + " erros e/ou alertas nas tabelas de fornecedores.", "JQueryAnalizer - Verificação de cadastro de clientes e fornecedores", JOptionPane.OK_OPTION);
					}
					
				}
			}
			catch (Exception e) {
				e.printStackTrace(); 
			}
			if (_log != null) _log.warning("\t[«««] Tool: { Check Clients and Suppliers info }\tEND");
		}
		
	}
}