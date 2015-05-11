package Components.Programs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class MailSmtpTest {
	
	private Border _default_border = new Border() {
		@Override
		public Insets getBorderInsets(Component arg0) {	return new Insets(2,4,2,4); }

		@Override
		public boolean isBorderOpaque() { return true; }

		@Override
		 public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Color c1 = new Color(180,180,180);
		    Color c0 = g.getColor();
		    g.setColor(c1);
		    g.drawLine(x, y, x, y + height - 1);
		    g.drawLine(x, y + height - 1, x + width, y + height - 1);
		    g.drawLine(x, y + height - 1, x + width, y + height - 1);
		    g.drawLine(x + width - 1, y, x + width - 1, y + height);
		    g.drawLine(x, y, x + width, y);
		    g.setColor(c0);
		  }
    	
    };
    
    private Border _border_bottom_line = new Border() {
		public Insets getBorderInsets(Component arg0) {	return new Insets(2,4,2,4); }

		public boolean isBorderOpaque() { return true; }

		 public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Color c1 = new Color(180,180,180);
		    Color c0 = g.getColor();
		    g.setColor(c1);
		    g.drawLine(x, y + height - 1, x + width, y + height - 1);
		    g.setColor(c0);
		  }
    };
    
	private JTextField _smtp;
	private JTextField _senha;
	private JComboBox<String> _criptografia;
	private JTextArea _mensagem;
	private JTextArea _log;
	private JTextField _email_remetente;
	private JTextField _email_destinatario;
	private JCheckBox _auth;
	private JTextField _usuario;
	private JCheckBox _html;
	private JFrame _DIALOG;
	private Font _default_font = new Font("Verdana", Font.PLAIN, 11);
	
	public MailSmtpTest() {
		_DIALOG = new JFrame();
		_DIALOG.setTitle("JQueryAnalizer - Teste de conexão SMTP para envio de e-mails.");
		Dimension size = new Dimension(463,562);
		_DIALOG.setMaximumSize(size);
		_DIALOG.setMinimumSize(size);
		_DIALOG.setPreferredSize(size);
		_DIALOG.setLocationRelativeTo(null);
		_DIALOG.setResizable(false);
		_DIALOG.setLayout(null);
		_DIALOG.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		_DIALOG.setIconImage(new ImageIcon(ClassLoader.getSystemResource("folder_terminal.png")).getImage());
		
		JLabel h1 = new JLabel("<html>&nbsp;<b>#1.</b> Dados de acesso:</html>");
		h1.setBounds(5,5,447,17);
		h1.setFont(new Font("Verdana", Font.PLAIN, 11));
		//h1.setBorder(BorderFactory.createLineBorder(new Color(180,180,180)));
		h1.setBorder(_border_bottom_line);
		//h1.setBackground(new Color(180,180,180));
		//h1.setOpaque(true);
		_DIALOG.add(h1);
		
		JPanel dados = new JPanel();
		dados.setBounds(5,23,447,113);
		//dados.setBackground(new Color(200,200,200));
		dados.setLayout(null);
		//dados.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
		
		JLabel dados_t1 = new JLabel("Endereço SMTP: ");
		dados_t1.setBounds(5,5,100,15);
		dados_t1.setHorizontalAlignment(JLabel.RIGHT);
		dados_t1.setFont(_default_font);
		dados.add(dados_t1);
		
		JLabel dados_t2 = new JLabel("Usuário: ");
		dados_t2.setBounds(5,27,100,15);
		dados_t2.setHorizontalAlignment(JLabel.RIGHT);
		dados_t2.setFont(_default_font);
		dados.add(dados_t2);
		
		JLabel dados_t3 = new JLabel("Senha: ");
		dados_t3.setBounds(5,49,100,15);
		dados_t3.setHorizontalAlignment(JLabel.RIGHT);
		dados_t3.setFont(_default_font);
		dados.add(dados_t3);
		
		JLabel dados_t4 = new JLabel("Criptografia: ");
		dados_t4.setBounds(255,5,90,15);
		dados_t4.setHorizontalAlignment(JLabel.RIGHT);
		dados_t4.setFont(_default_font);
		dados.add(dados_t4);
		
		JLabel dados_t5 = new JLabel("Remetente: ");
		dados_t5.setBounds(5,71,100,15);
		dados_t5.setHorizontalAlignment(JLabel.RIGHT);
		dados_t5.setFont(_default_font);
		dados.add(dados_t5);
		
		JLabel dados_t6 = new JLabel("Destinatário: ");
		dados_t6.setBounds(5,93,100,15);
		dados_t6.setHorizontalAlignment(JLabel.RIGHT);
		dados_t6.setFont(_default_font);
		dados.add(dados_t6);
		
		
		_smtp = new JTextField("smtp.gmail.com:465");
		_smtp.setBounds(105,2,150,21);
		_smtp.setFont(_default_font);
		_smtp.setBorder(_default_border);
		_smtp.setHorizontalAlignment(JTextField.CENTER);
		dados.add(_smtp);
		
		_usuario = new JTextField();
		_usuario.setBounds(105,24,150,21);
		_usuario.setFont(_default_font);
		_usuario.setBorder(_default_border);
		_usuario.setHorizontalAlignment(JTextField.CENTER);
		dados.add(_usuario);
		
		_senha = new JPasswordField();
		_senha.setBounds(105,46,150,21);
		_senha.setFont(_default_font);
		_senha.setBorder(_default_border);
		_senha.setHorizontalAlignment(JTextField.CENTER);
		dados.add(_senha);
		
		_email_remetente = new JTextField();
		_email_remetente.setBounds(105,68,340,21);
		_email_remetente.setFont(_default_font);
		_email_remetente.setBorder(_default_border);
		dados.add(_email_remetente);
		
		
		_email_destinatario = new JTextField();
		_email_destinatario.setBounds(105,90,340,21);
		_email_destinatario.setFont(_default_font);
		_email_destinatario.setBorder(_default_border);
		dados.add(_email_destinatario);
		
		_criptografia = new JComboBox<String>();
		_criptografia.setBounds(345,2,100,21);
		_criptografia.setBorder(_default_border);
		_criptografia.setFont(_default_font);
		_criptografia.setEditable(true);
		_criptografia.getEditor().getEditorComponent().setEnabled(false);
		_criptografia.getEditor().getEditorComponent().setForeground(Color.BLACK);
		_criptografia.addItem("Desabilitado");
		_criptografia.addItem("TLS");
		_criptografia.addItem("SSL");
		_criptografia.setForeground(Color.BLACK);
		dados.add(_criptografia);
		
		_auth = new JCheckBox("Autenticar conexão");
		_auth.setBounds(263,24,200,21);
		_auth.setFont(_default_font);
		_auth.setOpaque(false);
		dados.add(_auth);
		
		_html = new JCheckBox("Mensagem em HTML");
		_html.setBounds(263,46,200,21);
		_html.setFont(_default_font);
		_html.setOpaque(false);
		dados.add(_html);
		
		
		JLabel h2 = new JLabel("<html><b>#2.</b> Mensagem:</html>");
		h2.setBounds(5,142,447,17);
		h2.setFont(new Font("Verdana", Font.PLAIN, 11));
		h2.setBorder(_border_bottom_line);
		//h2.setOpaque(true);
		//h2.setBorder(BorderFactory.createLineBorder(new Color(160,160,160)));
		//h2.setBackground(new Color(180,180,180));
		_DIALOG.add(h2);
		
		_mensagem = new JTextArea("Esta é uma mensagem de teste.\n\r");
		_mensagem.setFont(_default_font);
		
		JScrollPane scroll_mensagem = new JScrollPane(_mensagem);
		scroll_mensagem.setBounds(5,162,447,206);
		_DIALOG.add(scroll_mensagem);
		
		JLabel h3 = new JLabel("<html><b>#3.</b> Log:</html>");
		h3.setBounds(5,373,447,17);
		h3.setFont(new Font("Verdana", Font.PLAIN, 11));
		h3.setBorder(_border_bottom_line);
		//h3.setBorder(BorderFactory.createLineBorder(new Color(160,160,160)));
		//h3.setBackground(new Color(180,180,180));
		_DIALOG.add(h3);
		
		_log = new JTextArea();
		_log.setFont(_default_font);
		
		JScrollPane scroll_log = new JScrollPane(_log);
		scroll_log.setBounds(5,393,447,98);
		_DIALOG.add(scroll_log);
		
		
		JButton enviar = new JButton("Enviar");
		enviar.setBounds(270,495,110,35);
		enviar.setOpaque(false);
		enviar.setFont(_default_font);
		enviar.setMnemonic('E');
		enviar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				
				int    crypt = _criptografia.getSelectedIndex();
				String host  = _smtp.getText().split(":")[0];
				String port  = _smtp.getText().split(":").length > 1 ? _smtp.getText().split(":")[1] : "25";
				
				Properties props = new Properties();
                /*
                props.setProperty("proxySet","true");
                props.setProperty("socksProxyHost","192.168.155.1");	// IP do Servidor Proxy
                props.setProperty("socksProxyPort","1080");				// Porta do servidor Proxy
                */
				props.put("mail.transport.protocol", "smtp"); //define protocolo de envio como SMTP
				props.put("mail.smtp.starttls.enable", crypt == 1 ? "true" : "false"); 
				props.put("mail.smtp.startssl.enable", crypt == 2 ? "true" : "false");
				props.put("mail.smtp.host", host); //server SMTP do GMAIL
				props.put("mail.smtp.auth", _auth.isSelected() ? "true" : "false");
				props.put("mail.smtp.user", _usuario.getText());
				props.put("mail.debug", "true");
				props.put("mail.smtp.port", port);
				props.put("mail.smtp.socketFactory.port", port);
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.socketFactory.fallback", "false");
		
				//Cria um autenticador que sera usado a seguir
				SimpleAuth auth = null;
				auth = new SimpleAuth (_usuario.getText(),_senha.getText());
		
				//Session - objeto que ira realizar a conexão com o servidor
				Session session = Session.getDefaultInstance(props, auth);
				session.setDebug(true); //Habilita o LOG das ações executadas durante o envio do email
				PrintStream ps = new PrintStream(new OutputStream() {
					public void write(int b) throws IOException {}
					public void write(byte[] b, int off, int len) throws IOException {
						_log.append(new String(b, off, len));
					}
				});
				session.setDebugOut(ps);

				//Objeto que contém a mensagem
				Message msg = new MimeMessage(session);
				try {
					//Setando o destinatário
					msg.setRecipient(Message.RecipientType.TO, new InternetAddress(_email_destinatario.getText()));
					//Setando a origem do email
					msg.setFrom(new InternetAddress(_usuario.getText()));
					//Setando o assunto
					msg.setSubject("JQueryAnalizer - Teste das configurações de e-mail.");
					//Setando o conteúdo/corpo do email
					msg.setContent(_mensagem.getText(), _html.isSelected() ? "html" : "text/plain");

				}
				catch (Exception e) {
					_log.append("----------------------[Erro ao montar mensagem para envio:]\n" + e.getMessage() + "\n");
				}
		
				//Objeto encarregado de enviar os dados para o email
				Transport tr;
				try {
					tr = session.getTransport("smtp"); //define smtp para transporte
					/*
					 *  1 - define o servidor smtp
					 *  2 - seu nome de usuario do gmail
					 *  3 - sua senha do gmail
					 */
					tr.connect(host, _usuario.getText(), _senha.getText());
					msg.saveChanges(); // don't forget this
					//envio da mensagem
					tr.sendMessage(msg, msg.getAllRecipients());
					tr.close();
					JOptionPane.showMessageDialog(null, "E-mail enviado com sucesso!", "JQueryAnalizer - Aviso!", JOptionPane.OK_OPTION);
				}
				catch (Exception e) {
					System.out.println(">> Erro: Envio Mensagem");
					_log.append("----------------------[ Erro no envio da mensagem:]\n" + e.getMessage() + "\n");
				}
			}
		});
		_DIALOG.add(enviar);
		
		JButton sair = new JButton("Sair");
		sair.setBounds(382,495,70,35);
		sair.setOpaque(false);
		sair.setFont(_default_font);
		sair.setMnemonic('S');
		sair.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				_DIALOG.dispose();
				_DIALOG = null;
			}			
		});
		_DIALOG.add(sair);
		
		_DIALOG.add(dados);
	}
	
	public void start() {
		if (_DIALOG != null) {
			_DIALOG.setVisible(true);
		}
	}
	
	
	private class SimpleAuth extends Authenticator {
		public String username = null;
		public String password = null;


		public SimpleAuth(String user, String pwd) {
			username = user;
			password = pwd;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication (username,password);
		}
	}

}
