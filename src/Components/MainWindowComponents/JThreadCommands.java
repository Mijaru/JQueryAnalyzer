package Components.MainWindowComponents;

import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import javolution.util.FastList;
import Components.MainWindow;
import Components.SQLConnectionManager;
import Components.Util;
import Components.MainWindowComponents.JHistory.Type;
import Components.Programs.Backup;

/** ------------------------------------------------- */
/** FUNÇÃO PARA CONECTAR AO BANCO DE DADOS VIA thread */
/** ================================================= */
public class JThreadCommands implements Runnable {
		private int _index;
		private String _options;
		private Component _parent_component;

		public JThreadCommands (int index, String options, Component parent) {
			_index = index;
			_options = options;
			_parent_component = parent;
		}

		public JThreadCommands getInstance() {
			return this;
		}
		
		public void run() {
			if (MainWindow._debug) {
				System.out.println("<- src.Components.MainWindowComponents.JThreadCommands() ->> index: " + _index + ", options: " + _options + ", parent: " + _parent_component);
			}
			System.gc();
			switch(_index) {
				case 0: // iniciar a conexão com o servidor
					if (_parent_component instanceof JTabPanel) {
						JTabPanel pane = (JTabPanel)_parent_component;
						JParametersPanel parameters = pane.getParameters();
						if (parameters != null) {
							parameters.updateConnectionStatus(2);
							parameters.setColapsed(Boolean.getBoolean(MainWindow.getPropertie("main_info_initcolapse", "false")));
						}
						if (pane instanceof JQueryPane) {
							((JQueryPane)pane).addHistory(new JHistory(Type.CONNECTION, parameters));
						}
						pane.openConnection();
						SQLConnectionManager con = pane.getConnection();
						
						
						// --

						
						// --
						
						if (con != null && parameters != null && con.isConnected() && con.getServerType() == 1) {
							Component tab = MainWindow.getActiveTab();
							if (tab instanceof JQueryPane) {
								((JQueryPane)tab).refreshDatabaseList();
							}
						}
						
						try {
							String autoexec = MainWindow.getPropertie("main_autoexec_program", "");
							if (!autoexec.isEmpty()) {
								if (autoexec.equalsIgnoreCase("backup")) {
									Backup backup = new Backup();
									backup.stopBackup();
								}
							}
						} 
						catch (Exception e) { e.printStackTrace(); }
						//pane.updateStatus("<i>" + (pane.isConnected() ? "<font color='green'>CONECTADO COM SUCESSO</font></i><br>\\:> <b>READY</b>" : "<font color='red'>FALHA NA CONEXÃO</font></i><br>\\:> <b>FAIL</b>"));
					}
					JTabbedPane tabs = MainWindow.getTabs();
					if (tabs != null) {
						tabs.repaint();
					}
					break;
				case 1: // -- Executar Scripts - via teclado (Enter / F9)
				case 2: // -- Executar Scripts - via icone
					if (_parent_component instanceof JQueryPane) {
						JQueryPane				  panel = (JQueryPane) _parent_component;
						JParametersPanel  	 parameters = (panel != null && panel instanceof JQueryPane ? panel.getParameters() : null);
						SQLConnectionManager connection = (panel != null ? panel.getConnection() : null);
						Connection 					con = connection.getConnection();
						String[] 			   commands = clearCommands(_options + ";\r\n", connection);
						String					  alias = null, database = null;
						Statement					 st = null;
						ResultSetMetaData		   meta = null;
						ResultSet					 rs = null;

						panel.addQueryHistory(_options);

						for (int i = 0; i < commands.length; i++) {
							database = alias = null;
							if (commands[i] == null || commands[i].trim().isEmpty()) {
								continue;
							}
							else {
								alias = (commands[i].length() > 128 ? commands[i].substring(0, 128) : commands[i]).toLowerCase();
							}
							try {
								st = con.createStatement();
								connection.setLastQueryTime(0);
								panel.updateStatus("Executando: '" + commands[i].subSequence(0, commands[i].length() > 20 ? 20 : commands[i].length()) + "...'<br>\\:> <b>WAIT</b> | <font color='blue'>Aguardando resposta do banco de dados...</font>");
								long time = System.currentTimeMillis();
								boolean type = st.execute(commands[i]);
								connection.setLastQueryTime((System.currentTimeMillis() - time) / 1000.D);
								if (type) {
									// -- resultset
									rs = st.getResultSet();
									meta = rs.getMetaData();
									String[]   		columns = new String[meta.getColumnCount()];
									FastList<Object[]> list = new FastList<Object[]>();
									FastList<Object> record = new FastList<Object>();
									int           row_count = 0;
									
									for (int j = 1; j <= meta.getColumnCount(); j++) {
										if (columns[j - 1] == null) {
											columns[j - 1] = meta.getColumnName(j); 
										}
									}
									
							        while(rs.next()) {
							        	++row_count;
							        	for (int j = 1; j <= meta.getColumnCount(); j++) {
											switch (meta.getColumnType(j)) {
												case Types.INTEGER:
												case Types.BIGINT:
												case Types.BIT:
												case Types.TINYINT:
													record.add(rs.getInt(j));
													break;
												case Types.DATE:
												case Types.TIMESTAMP:
													record.add(rs.getDate(j));
													break;
												case Types.DECIMAL:
												case Types.FLOAT:
													record.add(rs.getFloat(j));
													break;
												case Types.CHAR:
												case Types.VARCHAR:
												case Types.LONGVARCHAR:
													record.add(rs.getString(j));
													break;
												default:
													System.out.println("JThreadCommands.run() ~ [CASE 2] -> ColumnName: "  + meta.getColumnName(j) + ", ColumnType: " + meta.getColumnType(j) + ", ColumnClassName: " + meta.getColumnTypeName(j));
													record.add(rs.getString(j));
											}
										}
							            list.add(record.toArray(new Object[record.size()]));
							            record.clear();
							        }
							        panel.updateStatus("A consulta retornou: <b>" + row_count + "</b> resultados,<br>preparando páginas para exibição...");
							        rs.close();
							        panel.updateQueryList(columns, list.toArray(new Object[list.size()][columns.length]));
								}
								else {
								// -- update
									panel.updateStatus("Comando executado com sucesso.<br>\\:> <b>OK</b>" + (st.getUpdateCount() >= 0 ? "| <b>" + st.getUpdateCount() + "</b> registros afetados." : ""));
									alias = commands[i].replace("  ", " ").trim().toLowerCase();
									if (connection != null && connection.isConnected()) {
										switch (connection.getServerType()) {
											case SQLConnectionManager.DB_MYSQL:
											case SQLConnectionManager.DB_MSSQL:
												if (alias.startsWith("use")) {
													database = commands[i].split(" ")[1].replace("`", "");
													//alias = null;
												}
												break;
											case SQLConnectionManager.DB_ORACLE:
												if (alias.startsWith("alter session set current_schema")) {
													database = commands[i].split("=")[1].trim();
													//alias = null;
												}
												break;
										}
										if (parameters != null && database != null) {
											parameters.setDatabase(database);
										}
										if (connection != null && database != null && connection.isConnected()) {
											panel.updateStatus("Alternando a base de dados:<br>\\:> <b>WAIT</b> | Aguardando lista de objetos disponíveis...");
											connection.setDatabase(database);
										}
										if (database != null) {
											MainWindow.getTabs().setTitleAt(MainWindow.getActiveTabIndex(), " Query: " + database + " ");
											MainWindow.getTabs().repaint();
											if (!panel.isDatabasePresent(database)) {
												panel.updateDatabaseList(connection.getDatabasesList());
											}
											else {
												panel.updateTableList();
											}
										}
										
										if (
											alias.startsWith("create table") ||
											alias.startsWith("drop table")
											) {
											panel.updateTableList();
										}
										if (
											alias.startsWith("create database")	||
											alias.startsWith("drop database")
											) {
											panel.updateDatabaseList(connection.getDatabasesList());
										}
										panel.updateStatus("Comando executado com sucesso:<br>\\:> <b>OK</b>");
									}
								}
							}
							catch (SQLException e) {
								if (e != null && e.getMessage() != null) {
									panel.updateStatus("Comando retornou um erro!<br>\\:> <b><font color='red'>FAIL</font></b>");
									panel.updateQueryStatus("O comando SQL: <font color=blue><u>" + commands[i].subSequence(0, commands[i].length() > 64 ? 64 : commands[i].length()) + "</u></font> retornou a seguinte messagem de erro: " , e);
									if ((commands.length - (2 + i)) > 0) {
										int option = JOptionPane.showConfirmDialog(null, "<html>O comando '<b>" + (commands[i].subSequence(0, commands[i].length() > 128 ? 128 : commands[i].length())) + "</b>' retornou o(s) seguinte(s) erro(s):<br><font color='red'>" + e.getMessage() + ".</font><br><b>Deseja continuar a execução dos comandos?</b><br><br><i>(Existem mais: " + (commands.length - (2 + i)) + " comandos a na fila).</i></html>", "Erro ao executar o comando SQL:", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
										if (option == JOptionPane.CANCEL_OPTION) {
											return;
										}
									}
								}
							}
							finally {
								try {
									if (st != null && !st.isClosed()) {
										st.close();
										st = null;
									}
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
// --
						}
					}
					break;
				case 3: // Script - limpar area de edição.
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.clearQuerySentence();
					}
					break;
					
				case 4: // exibir primeira página de registros.
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.loadQueryPage(1);
					}
					break;
				case 5: // exibe a página de registros anterior.
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.loadQueryPage(pane.getCurrentPage() - 1);
					}
					break;
				case 6: // exibe a próxima página de registros.
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.loadQueryPage(pane.getCurrentPage() + 1);
					}
					break;
				case 7: // exibe a última página de registros
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.loadQueryPage(pane.getQueryPageCount());
					}
					break;
					
				case 8: // ajusta o conteudo da tabela a grade.
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.autoAdjustQueryTable();
					}
					break;
					
				case 9: // navega por entre o histórico... <- voltando
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.loadQueryHistory(pane.getHistoryPage() + Integer.parseInt(_options));
					}
					break;
					
				case 10: // pesquisa entre os resultados da query.
					if (_parent_component instanceof JQueryPane) {
						JQueryPane pane = (JQueryPane)_parent_component;
						pane.searchQuery(_options);
					}
					break;
				case 11: // pesquisa entre os resultados da query.
					if (_parent_component instanceof JQueryPane) {
						final JQueryPane pane = (JQueryPane)_parent_component;
						pane.saveQuery(_options);
					}
					break;
				case 12:
					//if (_parent_component instanceof JQueryPane) {
						//final JQueryPane pane = (JQueryPane)_parent_component;
						//pane.saveHistory();
						MainWindow.saveFullHistory();
					//}
					break;
			}
			
		}

		@SuppressWarnings("null")
		private String[] clearCommands(String statements, SQLConnectionManager con) {
			if (statements == null || statements.isEmpty() || con == null || !con.isConnected()) {
				return null;
			}
			
			StringBuilder sb = new StringBuilder(statements);
			String data = null, version_a = null, version_b = null;
			char cur = 0x00, pos = 0x00, flag = 0x00, comment = 0x00, escape = 0x00;
			int loc_a = 0, loc_b = 0, length = 0;
			float packet;
			List<String> out = new ArrayList<String>();
			
			for (int j = 0; j < sb.length(); j++) {
				data = null;
				cur = sb.charAt(j);
				pos = (j + 1 >= sb.length() ? 0 : sb.charAt(j + 1));
				switch (cur) {
					case '*':
						if (flag == 0x00 && comment != 0x00 && pos == '/') {
							loc_b = ++j + 1;
							data = sb.substring(loc_a, loc_b).trim();
							switch (con.getServerType()) {
								case SQLConnectionManager.DB_MYSQL:
									if (data.startsWith("/*!")) {
										loc_b += 1;
										data = sb.substring(loc_a, loc_b).trim();
										version_a = data.substring(data.indexOf("!") + 1, data.indexOf(" "));
										if (version_b.startsWith("MY") && Integer.parseInt(version_b.substring(2)) >=  Integer.parseInt(version_a.substring(0, 1))) {
											data = data.substring(data.indexOf(" "), data.indexOf(";") - 2).trim();
											if (!data.toLowerCase().startsWith("use")) {
												if (MainWindow._debug) {
													System.out.println(">> < " + data + " > -> [ " + version_a + " > " + version_b + " ]");
												}
												
												out.add(data);
											}
											break;
										}
									}
									break;
								default:
									if (MainWindow._debug && data != null) {
										System.out.println(comment + "" + comment + " Comment! { " + data + " }");
									}		
							}
							
							
							sb.delete(loc_a, loc_b);
							j = loc_a;
							loc_a = loc_b = 0;
							comment = 0x00;
						}
						break;
				
					case '#': // [ #  ] - comentários gerais limha simples - All.
						if (flag == 0x00 && comment == 0x00 && pos == ' ') {
							comment = cur;
							loc_a = j;
						}
						break;
						
					case 0x2F: // [ // ] - comentários gerais limha simples - All.
						if (flag == 0x00 && comment == 0x00 && (pos == '/' || pos == '*')) {
							comment = pos;
							loc_a = j;
						}
						break;
						
					case 0x2D: // [ -- ] - comentários gerais linha simples - all.
						if (flag == 0x00 && comment == 0x00 && pos == '-') {
							comment = cur;
							loc_a = j;
						}
						break;
						
					case 0x0A: // [ \n ] - encerramento de comentário de linha simples.
						if (flag == 0x00 && (comment == '-' || comment == '/' || comment == '#')) {
							loc_b = j;
							if (MainWindow._debug) {
								System.out.println(comment + "" + comment + " Comment! < " + sb.substring(loc_a, loc_b).trim() + " >");
							}
							sb.delete(loc_a, loc_b);
							j = loc_a;
							loc_a = loc_b = 0;
							comment = 0x00;
						}
						break;
				
					case 0x5C: // [ \ ] -- escape char - MySQL.
						switch (con.getServerType()) {
							case SQLConnectionManager.DB_MYSQL:
								if (escape == 0x00) {
									escape = cur;
									continue;	
								}
								break;
						}
						break;
				
					case 0x27: // [ ' ] -- string delimiter - MySQL / MsSQL.
						if (escape != 0x00 || comment != 0x00) {
							break;
						}
						if (flag == 0x00) {
							switch (con.getServerType()) {
								case SQLConnectionManager.DB_MYSQL:
									flag = cur;
									break;
									
								case SQLConnectionManager.DB_SQLSERVER: // -- ok
									flag = cur;
									break;
							}
						}
						else if (flag == 0x27) {
							switch (con.getServerType()) {
								case SQLConnectionManager.DB_MYSQL:
									
								case SQLConnectionManager.DB_SQLSERVER: // -- ok
									flag = 0x00;
									break;
							}
						}
						break;
						
					case 0x3B: // [ ; ] -- sql statement end - all DBs.
						if (flag == 0x00 && escape == 0x00 && comment == 0x00) {
							packet = (1f + j) / (1024f * 1024);
														
							data = sb.substring(0, j).trim();
							length = data.length();
							if (MainWindow._debug) {
								System.out.println(">> < " + data.substring(0, length > 56 ? 56 : length) + (length > 56 ? "..." : "") + " > " + Util.toPercent(packet).replace("%", "MB"));
							}
							out.add(data);
							++j;
							sb = new StringBuilder(sb.substring(++j));
							j = 0;
						}
				}
				escape = 0x00;
			}
			return out.toArray(new String[out.size()]);
		}
		
	}
	