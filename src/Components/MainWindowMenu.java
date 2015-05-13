package Components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
import Components.Programs.SQLServer.ChangeTableOwner;
import Components.Programs.Geral.LocalizarColunas;
import Components.Programs.Geral.CheckTables;

public class MainWindowMenu extends JMenuBar {
	private static final long serialVersionUID = -7239390223993585239L;
	private JMenuBar _menu;
	public MainWindowMenu() {
        /** Create the menu bar */
        _menu = new JMenuBar();
                
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ~~ MENU: ARQUIVO
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        JMenu file = new JMenu("Arquivo");
        _menu.add(file);
        
        // -- Salvar histórico.
        JMenuItem save = new JMenuItem("Salvar Historico");
        save.setMnemonic('H');
        save.setName("historico");
        save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				Thread t = new Thread(new JThreadCommands(12, null, null));
				 t.start();
			}
        });
        file.add(save);
        
        // -- 
        file.add(new JSeparator());
        // --
        
        // -- Sair.
        final JMenuItem quit = new JMenuItem("Sair");
        quit.setMnemonic('S');
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
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
        mrdigital_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				RestorePubli program = new RestorePubli();
				program.startProgram();
			}			
		});
		mrdigital.add(mrdigital_01);
		
		// --
	    mrdigital.add(new JSeparator());
	    // --
	   
	    // -- Localizar prefixos de PIT's disponiveis.
		final JMenuItem mrdigital_02 = new JMenuItem("<html><font color='#777777'>[MR]</font> Assistente normalizar campo de <b>EVENTOS</b> do faturamento</html>");
        mrdigital_02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_AjustarEventosFatura program = new MR_AjustarEventosFatura(MainWindow.getActiveTabConnection());
				program.startProgram();
			}			
		});
		mrdigital.add(mrdigital_02);
	    
		
		// -- Replicar recursos de usuarios
        final JMenuItem mrdigital_03 = new JMenuItem("<html><font color='#777777'>[MR]</font> Replica recursos de usuários</html>");
        mrdigital_03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_ReplicaRecursos program = new MR_ReplicaRecursos(MainWindow.getActiveTabConnection());
				program.startProgram();
			}			
		});
		mrdigital.add(mrdigital_03);
        
		// -- Verificar cadastros de clientes e fornecedores
        final JMenuItem mrdigital_04 = new JMenuItem("<html><font color='#777777'>[MR]</font> Verifica cadastros de <b>CLIENTES</b> e <b>FORNECEDORES</b></html>");
        mrdigital_04.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_VerificaCadastros program = new MR_VerificaCadastros(MainWindow.getActiveTabConnection());
				program.startProgram();

			}			
		});
        mrdigital.add(mrdigital_04);
        
        // -- Normaliza banco de dados para o Publinet
		final JMenuItem mrdigital_05 = new JMenuItem("<html><font color='#777777'>[MR]</font> Normaliza banco de dados para o <b>PUBLINET</b></html>");
        mrdigital_05.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_NormalizaBasePublinet program = new MR_NormalizaBasePublinet(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}			
		});
		mrdigital.add(mrdigital_05);
		
		// -- Lista prefixos de PIT's/PI's disponíveis
		final JMenuItem mrdigital_06 = new JMenuItem("<html><font color='#777777'>[MR]</font> Procura prefixos de <b>PIT's</b> e <b>PI's</b> disponíveis</html>");
        mrdigital_06.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
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
		geral.setMnemonic('S');
		ferramentas.add(geral);
		
		// -- Assistente para localizar tabelas com determinada coluna.
		final JMenuItem geral_01 = new JMenuItem("<html>Localizar tabelas com determinada coluna.</html>");
        geral_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				LocalizarColunas program = new LocalizarColunas(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}			
		});
        geral_01.setName("geral_01");
		geral.add(geral_01);
		
		// -- Assistente para verificar as tabelas do sistema. 
		final JMenuItem geral_02 = new JMenuItem("<html>Verificar as tabelas do sistema.</html>");
		geral_02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				CheckTables program = new CheckTables(MainWindow.getActiveTabConnection());
				program.startPrograma();
			}			
		});
		geral_02.setName("geral_02");
		geral.add(geral_02);
		
		// --
	    geral.add(new JSeparator());
	    // --
		
		final JMenuItem geral_99 = new JMenuItem("<html>Script para <font color=red><b>excluir</b></font> todas as tabelas</html>");
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
		
		
		
		JMenu mssql = new JMenu("SQL Server");
		ferramentas.add(mssql);
		
		// -- programa para substituir o owner das tabelas.
		final JMenuItem mssql_01 = new JMenuItem("Substituição do proprietário das tabelas");
		mssql_01.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ChangeTableOwner program = new ChangeTableOwner();
				program.show();
			}			
		});
		mssql.add(mssql_01);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
				
		/** programs -> compare tables *//**
        final JMenuItem compare = new JMenuItem("Comparar databases.");
		compare.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				final JQueryPane active = (MainWindow.getActiveTab() instanceof JQueryPane ? (JQueryPane)MainWindow.getActiveTab() : null);
				if (active != null && active.getConnection() != null && active.getConnection().isConnected()) {
					@SuppressWarnings("unused")
					Compare co = new Compare();
				}
			}			
		});
		compare.setName("compare");
		programs.add(compare);
		/***/
		
		// ---
        ferramentas.add(new JSeparator());
		// ---        
		
		final JMenuItem ferramenta_01 = new JMenuItem("<html>Testar configurações de E-Mail [<b>SMTP</b>]</html>");
        ferramenta_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MailSmtpTest program = new MailSmtpTest();
				program.start();
			}			
		});
		ferramentas.add(ferramenta_01);
		
		// ---
        ferramentas.add(new JSeparator());
		// ---
        
        /** programs -> repair tables */
        final JMenuItem repair = new JMenuItem("Reparar tabelas");
		repair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unused")
				Repair rp = new Repair();
			}			
		});
		ferramentas.add(repair);
		
        /** programs -> efetuar backup do mysql */
        final JMenuItem backup = new JMenuItem("Executar backup");
		backup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Backup backup = new Backup();
				backup.start();
			}			
		});
		backup.setName("backup");
		ferramentas.add(backup);
		
		/** programs -> restaurar backup do mysql */
        final JMenuItem restore = new JMenuItem("Restaurar backup");
		restore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Restore rs = new Restore();
				rs.start();
				System.out.println("Restaurar mysql!");
			}			
		});
		restore.setName("restore");
		ferramentas.add(restore);
		
        
	
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ~~ MENU: ABOUT
		// ~~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		JMenu about = new JMenu("Sobre");
        about.setMnemonic('S');
        _menu.add(about);
        
        JMenuItem author = new JMenuItem("Autor");
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