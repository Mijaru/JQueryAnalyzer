package Components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import Components.MainWindowComponents.JQueryPane;
import Components.MainWindowComponents.JThreadCommands;
import Components.Programs.Backup;
import Components.Programs.ColumnFormat;
import Components.Programs.ColumnFormat2;
import Components.Programs.MR_AjustarEventosFatura;
import Components.Programs.MR_CheckTables;
import Components.Programs.MR_LocalizarColunas;
import Components.Programs.MR_PrefixFinder;
import Components.Programs.MR_ReplicaRecursos;
import Components.Programs.MR_VerificaCadastros;
import Components.Programs.MailSmtpTest;
import Components.Programs.Repair;
import Components.Programs.Restore;
import Components.Programs.RestorePubli;
import Components.Programs.SQLServer.ChangeTableOwner;

public class MainWindowMenu extends JMenuBar {
	private static final long serialVersionUID = -7239390223993585239L;
	private JMenuBar _menu;
	public MainWindowMenu() {
        /** Create the menu bar */
        _menu = new JMenuBar();
                
        /** file */
        JMenu file = new JMenu("Arquivo");
        _menu.add(file);
        
        // -- SALVAR HISTORICO
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
        // -- QUIT
        final JMenuItem quit = new JMenuItem("Sair");
        quit.setMnemonic('S');
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFrame frame = MainWindow.getMainFrame();
				frame.dispose();
				// --> System.exit(0);
			}			
		});
		quit.setName("sair");
        file.add(quit);
        
        
        /** programs */
        JMenu programs = new JMenu("Programas");
        _menu.add(programs);
        
        
        // ~~~~~~~~~~~~~~~~~~~~
        // Submenu MRDigital
        // ====================
        JMenu mrdigital = new JMenu("M&R Digital");
        if (System.getenv("USERDOMAIN") != null && System.getenv("USERDOMAIN").toLowerCase().contains("mrdigital")) { 
        	programs.add(mrdigital);
        }
        
		
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
        
        
		/** programs -> mrdigital -> limpar arquivo de backup */
        final JMenuItem programa_01 = new JMenuItem("[Publi] Restaurar arquivo de Backup");
        programa_01.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				RestorePubli restore = new RestorePubli();
				restore.start();
				System.out.println("Restaurar Backup do Publi");
			}			
		});
        programa_01.setName("restaura_backup_publi_mysql");
		mrdigital.add(programa_01);
		
		JSeparator separator_01 = new JSeparator();
	    separator_01.setBorder(BorderFactory.createEmptyBorder());
	    separator_01.setForeground(Color.GRAY);
	    mrdigital.add(separator_01);
	   
	    /** programas -> mrdigital -> Procura prefixos de PI já utilizados e disponíveis. */
		final JMenuItem programa_10 = new JMenuItem("<html>[MR] Assistente normalizar eventos do faturamento.</html>");
        programa_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_AjustarEventosFatura ajustar = new MR_AjustarEventosFatura(MainWindow.getActiveTabConnection());
				ajustar.startPrograma();
				System.out.println("normalizar_eventos_faturamento");
			}			
		});
        programa_10.setName("checar_tabelas_do_sistema");
		mrdigital.add(programa_10);
	    
	    /** programas -> mrdigital -> Procura prefixos de PI já utilizados e disponíveis. */
		final JMenuItem programa_08 = new JMenuItem("<html>[MR] Assistente para verificar as tabelas do sistema.</html>");
        programa_08.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_CheckTables check = new MR_CheckTables(MainWindow.getActiveTabConnection());
				check.startPrograma();
				System.out.println("checar_tabelas_do_sistema");
			}			
		});
        programa_08.setName("checar_tabelas_do_sistema");
		mrdigital.add(programa_08);
		
		/** programas -> mrdigital -> Procura prefixos de PI já utilizados e disponíveis. */
		final JMenuItem programa_09 = new JMenuItem("<html>[MR] Assistente para localizar tabelas com determinada coluna.</html>");
        programa_09.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MR_LocalizarColunas verify = new MR_LocalizarColunas(MainWindow.getActiveTabConnection());
				verify.startPrograma();
				System.out.println("localizar_colunas");
			}			
		});
        programa_09.setName("localizar_colunas");
		mrdigital.add(programa_09);
		
		final JMenuItem programa_02 = new JMenuItem("<html>[MR] Verifica campos de tabelas <u>ALTER</u>, <u>ADD</u>, <u>MODIFY</u>, <u>UPDATE</u> [MySQL]</html>");
        programa_02.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unused")
				ColumnFormat cf = new ColumnFormat(MainWindow.getActiveTabConnection());
				//cf.startPrograma();
				System.out.println("Verifica campos de tabelas ALTER, ADD, MODIFY, UPDATE [MySQL]");
			}			
		});
        programa_02.setName("verifica_campos_complementares_mysql");
		mrdigital.add(programa_02);
		
		/** programs -> mrdigital -> replicar recursos de usuarios */
        final JMenuItem programa_03 = new JMenuItem("[MR] Replica recursos de usuários");
        programa_03.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				MR_ReplicaRecursos mr = new MR_ReplicaRecursos(MainWindow.getActiveTabConnection());
				System.out.println("[MR] Replica recursos de usuario");
				mr.show();
			}			
		});
        programa_03.setName("mr_replica_recursos");
		mrdigital.add(programa_03);
        
		/** programs -> mrdigital -> verificar cadastros */
        final JMenuItem programa_04 = new JMenuItem("[MR] Verifica cadastros de clientes e fornecedores");
        programa_04.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				MR_VerificaCadastros mr = new MR_VerificaCadastros(MainWindow.getActiveTabConnection());
				mr.show();
				System.out.println("[MR] Verifica cadastros de clientes e fornecedores.");
			}			
		});
        programa_04.setName("mr_verifica_cadastros");
        mrdigital.add(programa_04);
        
        /** programas -> mrdigital -> normaliza campos para instalação do PubliNET */
		final JMenuItem programa_05 = new JMenuItem("<html>[MR] Normaliza banco de dados para instalação do <u>PubliNET</u></html>");
        programa_05.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				ColumnFormat2 cf = new ColumnFormat2(MainWindow.getActiveTabConnection());
				cf.startPrograma();
				System.out.println("Verifica campos DTINCLUSAO, USINCLUSAO, DTMANU e USMANU em todas as tabelas [MySQL]");
			}			
		});
        programa_05.setName("normaliza_campos_instalacao_publinet");
		mrdigital.add(programa_05);
		
        /** programas -> mrdigital -> Procura prefixos de PI já utilizados e disponíveis. */
		final JMenuItem programa_06 = new JMenuItem("<html>[MR] Procura prefixos de PI disponíveis</html>");
        programa_06.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				MR_PrefixFinder mr = new MR_PrefixFinder(MainWindow.getActiveTabConnection());
				mr.startPrograma();
				System.out.println("Procura prefixos de PI disponíveis!");
			}			
		});
        programa_06.setName("procura_prefixos_de_PI_disponiveis");
		mrdigital.add(programa_06);
		
		/** programas -> mrdigital -> Procura prefixos de PI já utilizados e disponíveis. */
		final JMenuItem programa_07 = new JMenuItem("<html>[MR] Assistente para teste das configurações de E-Mail (SMTP).</html>");
        programa_07.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				MailSmtpTest smtp = new MailSmtpTest();
				smtp.start();
				System.out.println("Testar conexão SMTP.");
			}			
		});
        programa_07.setName("testar_conexao_smtp");
		mrdigital.add(programa_07);
				
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
		
        
		// -----------------        
        JSeparator separator_02 = new JSeparator();
        separator_02.setBorder(BorderFactory.createEmptyBorder());
        separator_02.setForeground(Color.GRAY);
        programs.add(separator_02);
        
        /** programs -> repair tables */
        final JMenuItem repair = new JMenuItem("Reparar/Otimizar tabelas");
		repair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unused")
				Repair rp = new Repair();
			}			
		});
		repair.setName("repair");
		programs.add(repair);
		
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
		programs.add(backup);
		
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
		programs.add(restore);
		
        
		// -----------------
        JSeparator separator_1 = new JSeparator();
        separator_1.setBorder(BorderFactory.createEmptyBorder());
        separator_1.setForeground(Color.GRAY);
        programs.add(separator_1);
        
		/** programs -> compare tables */
        final JMenuItem dropalltables = new JMenuItem("<html>Comando para <font color=red>APAGAR</font> <i>todas as tabelas</i></html>");
		dropalltables.addActionListener(new ActionListener() {
			@Override
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
		dropalltables.setName("droptables");
		programs.add(dropalltables);
		
		// -----------------
        JSeparator separator_2 = new JSeparator();
        separator_2.setBorder(BorderFactory.createEmptyBorder());
        separator_2.setForeground(Color.GRAY);
        programs.add(separator_2);
        
		JMenu sqlserver = new JMenu("Microsoft SQL Server");
		programs.add(sqlserver);
		
		// -- programa para substituir o owner das tabelas.
		final JMenuItem sqlserver_change_owner = new JMenuItem("Substituição do proprietário das tabelas");
		sqlserver_change_owner.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ChangeTableOwner change = new ChangeTableOwner();
				change.show();
			}			
		});
		sqlserver.add(sqlserver_change_owner);
	}
	
	public JMenuBar getMenu() {
		return _menu;
	}
}