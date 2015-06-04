package Components;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import Components.MainWindowComponents.JQueryPane;
import Components.MainWindowComponents.JThreadCommands;
import Components.Programs.Backup;
import Components.Programs.MRDigital.MR_AjustarEventosFatura;
import Components.Programs.MRDigital.MR_PrefixFinder;
import Components.Programs.MRDigital.MR_ReplicaRecursos;
import Components.Programs.MRDigital.MR_VerificaCadastros;
import Components.Programs.MailSmtpTest;
import Components.Programs.Repair;
import Components.Programs.Restore;
import Components.Programs.RestorePubli;
import Components.Programs.MRDigital.MR_NormalizaBasePublinet;
import Components.Programs.MySQL.Compare;
import Components.Programs.SQLServer.ChangeTableOwner;
import Components.Programs.Geral.LocalizarColunas;
import Components.Programs.Geral.CheckTables;

public class MainWindowMenu extends JMenuBar {
	private static final long serialVersionUID = -7239390223993585239L;
	private JMenuBar _menu;
	private Font _default_font = new Font("Verdana", Font.PLAIN, 11);
	
	public MainWindowMenu() {
        /** Create the menu bar */
        _menu = new JMenuBar();
                
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ~~ MENU: ARQUIVO
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        JMenu file = new JMenu("Arquivo");
        file.setFont(_default_font);
        _menu.add(file);
        
        // -- Salvar histórico.
        JMenuItem save = new JMenuItem("Salvar Histórico");
        save.setFont(_default_font);
        save.setMnemonic('H');
        save.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_save_script.png")));
        save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				// -- logged.
				Logger log = MainWindow.getActiveLog();
				if (log != null) log.warning("\t[»»»] Begin menu action: ARQUIVO > SALVAR HISTORICO");
				JThreadCommands jt = new JThreadCommands(12, null, null);
				jt.run();
				if (log != null) log.warning("\t[«««] Ended menu action: ARQUIVO > SALVAR HISTORICO");
			}
        });
        file.add(save);
        
        // -- visualizar log atual.
        JMenuItem log = new JMenuItem("Visualizar log");
        log.setFont(_default_font);
        log.setMnemonic('V');
        log.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_open_log.png")));
        log.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				File log = MainWindow.getActiveLogFile();
				try {
					Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + log.getAbsolutePath());
				}
				catch (Exception e) {
					
				}
			}
        });
        file.add(log);
        
        // -- 
        file.add(new JSeparator());
        // --
        
        // -- Sair.
        final JMenuItem quit = new JMenuItem("Sair");
        quit.setFont(_default_font);
        quit.setMnemonic('S');
        quit.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_exit.png")));
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// -- logged.
				Logger log = MainWindow.getActiveLog();
				 if (log != null) log.warning("\t[***] Request menu action: ARQUIVO > SAIR");
				JFrame frame = MainWindow.getMainFrame();
				frame.dispose();
				System.exit(0);
			}			
		});
		quit.setName("sair");
        file.add(quit);
        
        
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ~~ MENU: FERRAMENTAS
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        JMenu ferramentas = new JMenu("Ferramentas");
        ferramentas.setFont(_default_font);
        ferramentas.setMnemonic('F');
        _menu.add(ferramentas);
        
        // -- --------------------------------------------------------------------------------------------
        // -- SUBMENU: MRDIGITAL
        // -- --------------------------------------------------------------------------------------------
        JMenu mrdigital = new JMenu("M&R Digital");
        if (System.getenv("USERDOMAIN") != null && System.getenv("USERDOMAIN").toLowerCase().contains("mrdigital")) { 
        	ferramentas.add(mrdigital);
        }
        
        // -- Restaurar arquivo de backup.
        final JMenuItem mrdigital_01 = new JMenuItem("<html><font color='#777777'>[Publi]</font> Restaurar arquivo de Backup</html>");
        mrdigital_01.setFont(_default_font);
        mrdigital_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// -- logged.
				Logger log = MainWindow.getActiveLog();
				if (log != null) log.warning("\t[»»»] Begin tool: FERRAMENTAS > MR DIGITAL > RESTAURAR BACKUP [mrdigital_01]");
				RestorePubli program = new RestorePubli();
				program.startProgram();
				if (log != null) log.warning("\t[«««] Ended tool: FERRAMENTAS > MR DIGITAL > RESTAURAR BACKUP [mrdigital_01]");
			}			
		});
		mrdigital.add(mrdigital_01);
		
		// --
	    mrdigital.add(new JSeparator());
	    // --
	   
	    // -- Localizar prefixos de PIT's disponiveis.
		final JMenuItem mrdigital_02 = new JMenuItem("<html><font color='#777777'>[MR]</font> Assistente normalizar campo de eventos do faturamento (NFSe)</html>");
		mrdigital_02.setFont(_default_font);
        mrdigital_02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// logged - Ok
				MR_AjustarEventosFatura program = new MR_AjustarEventosFatura(MainWindow.getActiveTabConnection());
				program.startProgram();
			}			
		});
		mrdigital.add(mrdigital_02);
	    
		
		// -- Replicar recursos de usuarios
        final JMenuItem mrdigital_03 = new JMenuItem("<html><font color='#777777'>[MR]</font> Replica recursos de usuários</html>");
        mrdigital_03.setFont(_default_font);
        mrdigital_03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				MR_ReplicaRecursos program = new MR_ReplicaRecursos(MainWindow.getActiveTabConnection());
				program.startProgram();
			}			
		});
		mrdigital.add(mrdigital_03);
        
		// -- Verificar cadastros de clientes e fornecedores
        final JMenuItem mrdigital_04 = new JMenuItem("<html><font color='#777777'>[MR]</font> Verifica cadastros de clientes e fornecedores</html>");
        mrdigital_04.setFont(_default_font);
        mrdigital_04.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				MR_VerificaCadastros program = new MR_VerificaCadastros(MainWindow.getActiveTabConnection());
				program.startProgram();
			}			
		});
        mrdigital.add(mrdigital_04);
        
        // -- Normaliza banco de dados para o Publinet
		final JMenuItem mrdigital_05 = new JMenuItem("<html><font color='#777777'>[MR]</font> Normaliza banco de dados para o Publinet</html>");
		mrdigital_05.setFont(_default_font);
        mrdigital_05.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				MR_NormalizaBasePublinet program = new MR_NormalizaBasePublinet(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}			
		});
		mrdigital.add(mrdigital_05);
		
		// -- Lista prefixos de PIT's/PI's disponíveis
		final JMenuItem mrdigital_06 = new JMenuItem("<html><font color='#777777'>[MR]</font> Procura prefixos de PIT's e Planos disponíveis</html>");
		mrdigital_06.setFont(_default_font);
        mrdigital_06.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				MR_PrefixFinder program = new MR_PrefixFinder(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}			
		});
		mrdigital.add(mrdigital_06);
		
		// --
		ferramentas.add(new JSeparator());
		// --
		
        // -- --------------------------------------------------------------------------------------------
        // -- SUBMENU: GERAL
        // -- --------------------------------------------------------------------------------------------
		JMenu geral = new JMenu("MySQL & SQL Server");
		geral.setFont(_default_font);
		geral.setMnemonic('S');
		ferramentas.add(geral);
		
		// -- Assistente para localizar tabelas com determinada coluna.
		final JMenuItem geral_01 = new JMenuItem("<html>Buscar nas tabelas pela ocorrência de uma determinada coluna</html>");
		geral_01.setFont(_default_font);
        geral_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				LocalizarColunas program = new LocalizarColunas(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}			
		});
		geral.add(geral_01);
		
		// -- Assistente para verificar as tabelas do sistema. 
		final JMenuItem geral_02 = new JMenuItem("<html>Verificar as tabelas da database atual</html>");
		geral_02.setFont(_default_font);
		geral_02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				CheckTables program = new CheckTables(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}		
		});
		geral.add(geral_02);
		
		// --
	    geral.add(new JSeparator());
	    // --
		
		final JMenuItem geral_99 = new JMenuItem("<html>Script para <font color=red><b>EXCLUIR</b></font> todas as tabelas</html>");
		geral_99.setFont(_default_font);
		geral_99.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				final JQueryPane active = (MainWindow.getActiveTab() instanceof JQueryPane ? (JQueryPane)MainWindow.getActiveTab() : null);
				List<String> list = null;
				if (active != null && active.getConnection() != null && active.getConnection().isConnected() && ((list = active.getConnection().getTables()) != null && list.size() > 0)) {
					int type = active.getConnection().getServerType();
					StringBuffer tables = new StringBuffer();
					for (int i = 0; i < list.size(); i++) {
						tables.append(type == SQLConnectionManager.DB_MYSQL ? "`" : ""); 
						tables.append(list.get(i));
						tables.append(type == SQLConnectionManager.DB_MYSQL ? "`" : "");
						tables.append(i + 1 == list.size() ? "" : ", ");
					}
					active.setQuerySentence("DROP TABLE " + tables.toString());
				}
				else if (active != null && (active.getConnection() == null || !active.getConnection().isConnected())) {
					JOptionPane.showMessageDialog(null, "Voce está desconectado!", "Aviso!", JOptionPane.OK_OPTION);
				}
				else if (active != null && active.getConnection() != null && active.getConnection().isConnected() && (active.getConnection().getDatabase() == null || active.getConnection().getDatabase().isEmpty())) {
					JOptionPane.showMessageDialog(null, "Voce está conectado ou não selecionou uma database válida!", "Aviso!", JOptionPane.OK_OPTION);
				}
			}			
		});
		geral.add(geral_99);
		
		JMenu mysql = new JMenu("MySQL");
		mysql.setFont(_default_font);
		ferramentas.add(mysql);
		
        final JMenuItem mysql_01 = new JMenuItem("Comparar tabelas");
        mysql_01.setFont(_default_font);
		mysql_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
// -- logged - Ok
				Compare program = new Compare();
				program.startPrograma();
			}			
		});
		mysql.add(mysql_01);
		
		
		JMenu mssql = new JMenu("SQL Server");
		mssql.setFont(_default_font);
		ferramentas.add(mssql);
		
		// -- programa para substituir o owner das tabelas.
		final JMenuItem mssql_01 = new JMenuItem("<html>Alterar do proprietário das tabelas</html>");
		mssql_01.setFont(_default_font);
		mssql_01.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
// -- logged - Ok
				ChangeTableOwner program = new ChangeTableOwner();
				program.show();
			}			
		});
		mssql.add(mssql_01);

		final JMenuItem mssql_02 = new JMenuItem("<html>Criar schema</html>");
		mssql_02.setToolTipText("<html>Requer que o <b>owner</b> seja um usuário previamente cadastrado</html>");
		mssql_02.setFont(_default_font);
		mssql_02.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
// -- logged - Ok
				Logger log = MainWindow.getActiveLog();
				SQLConnectionManager con = MainWindow.getActiveTabConnection();
				if (con != null && con.isConnected()) {
					String schema = JOptionPane.showInputDialog(null, "<html>Informe o nome do </b>Schema</b></html>", "JQueryAnalizer - Entrada de dados", JOptionPane.QUESTION_MESSAGE);
					String owner = JOptionPane.showInputDialog(null, "<html>Informe o nome do </b>Owner</b> para o <b>Schema</b></html>", "JQueryAnalizer - Entrada de dados", JOptionPane.QUESTION_MESSAGE);
					String sql = ("CREATE SCHEMA [schema] AUTHORIZATION [owner]").replace("[schema]", schema).replace("[owner]", owner);
					if (schema != null && !schema.isEmpty() && owner != null && !owner.isEmpty()) {
						log.warning("\t[***] { Create schema }\t" + sql);
						Exception e1 = con.executeUpdate(sql);
						if (e1 != null) {
							log.severe("\t   ›  Exception: " + e1.getMessage());
							JOptionPane.showMessageDialog(null, "<html>Ocorreu um erro ao criar o Schema <b>" + schema + "</b> com permissões para o Owner <b>" + owner + "</b><br><b>Exception:</b> <font color='red'>" + e1.getMessage() + "</font></html>", "JQueryAnalizer - Aviso", JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							JOptionPane.showMessageDialog(null, "<html>Schema <b>" + schema + "</b> foi criado com sucesso com permissões para o Owner <b>" + owner + "</b></html>", "JQueryAnalizer - Aviso", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}			
		});
		mssql.add(mssql_02);
		
		
		// ---
        ferramentas.add(new JSeparator());
		// ---        
		
		final JMenuItem ferramenta_01 = new JMenuItem("<html>Testar configurações de E-Mail [<b>SMTP</b>]</html>");
		ferramenta_01.setFont(_default_font);
		ferramenta_01.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_mail.png")));
        ferramenta_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// -- logged.
				Logger log = MainWindow.getActiveLog();
				if (log != null) log.warning("\t[»»»] Begin tool: FERRAMENTAS > TESTAR SERVIDOR SMTP [ferramenta_01]");
				MailSmtpTest program = new MailSmtpTest();
				program.start();
				if (log != null) log.warning("\t[«««] Ended tool: FERRAMENTAS > TESTAR SERVIDOR SMTP [ferramenta_01]");
			}			
		});
		ferramentas.add(ferramenta_01);
		
		// ---
        ferramentas.add(new JSeparator());
		// ---
        
        /** programs -> repair tables */
        final JMenuItem ferramenta_02 = new JMenuItem("Reparar tabelas");
        ferramenta_02.setFont(_default_font);
        ferramenta_02.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_fix.png")));
		ferramenta_02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// -- logged.
				Logger log = MainWindow.getActiveLog();
				if (log != null) log.warning("\t[»»»] Begin tool: FERRAMENTAS > REPARAR TABELAS [ferramenta_02]");
				@SuppressWarnings("unused")
				Repair rp = new Repair();
				if (log != null) log.warning("\t[«««] Ended tool: FERRAMENTAS > REPARAR TABELAS [ferramenta_02]");
			}			
		});
		ferramentas.add(ferramenta_02);
		
        /** programs -> efetuar backup do mysql */
        final JMenuItem ferramenta_03 = new JMenuItem("Executar backup");
        ferramenta_03.setFont(_default_font);
        ferramenta_03.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_backup.png")));
		ferramenta_03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// -- logged.
				Logger log = MainWindow.getActiveLog();
				if (log != null) log.warning("\t[»»»] Begin tool: FERRAMENTAS > EFETUAR BACKUP [ferramenta_03]");
				Backup backup = new Backup();
				backup.start();
				if (log != null) log.warning("\t[«««] Ended tool: FERRAMENTAS > EFETUAR BACKUP [ferramenta_03]");
				
			}			
		});
		ferramentas.add(ferramenta_03);
		
		/** programs -> restaurar backup do mysql */
        final JMenuItem ferramenta_04 = new JMenuItem("Restaurar backup");
        ferramenta_04.setFont(_default_font);
        ferramenta_04.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_restore.png")));
		ferramenta_04.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// logged - ok
				Restore program = new Restore();
				program.startPrograma();
			}			
		});
		ferramenta_04.setName("restore");
		ferramentas.add(ferramenta_04);
		
        
	
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ~~ MENU: ABOUT
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		JMenu about = new JMenu("Sobre");
		about.setFont(_default_font);
        about.setMnemonic('S');
        _menu.add(about);
        
        JMenuItem author = new JMenuItem("Autor");
        author.setFont(_default_font);
        author.setIcon(new ImageIcon(ClassLoader.getSystemResource("menu_about.png")));
        author.setMnemonic('A');
        author.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "<html><center>Williams Artimã Chaves<br><a href='mailto:williamsartiman@gmail.com'>williamsartiman@gmail.com</a></center></html>", "JQuery Analizer - Autor", JOptionPane.INFORMATION_MESSAGE);
			}
        });
        about.add(author);
	}
	
	public JMenuBar getMenu() {
		return _menu;
	}
}