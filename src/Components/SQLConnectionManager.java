package Components;
	

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;  
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.ResultSetMetaData;
import java.sql.SQLException;  
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import Components.MainWindowComponents.JQueryPane;
import oracle.sql.CLOB;

  
public class SQLConnectionManager {
	
	public static final int DB_MYSQL = 0;
	public static final int DB_ORACLE = 1;
	public static final int DB_POSTGREE = 2;
	public static final int DB_SQLSERVER = 3;
	public static final int DB_MSSQL = 3;
	
	public static final boolean _debug = true;
	
	private Connection _connection;
	private String _login;
	private String _pass;
	private static String _driver = "";
	private static String _connection_string = "";
	private String _name;
	private String _database;
	private String _connection_info;
	private int _server_type = 0;
	private double _time;
	private Exception _last_exception;
   
	private PreparedStatement _default_statement;
	private PreparedStatement _statement_update;
	
	private List<String> _tables = new ArrayList<String>();
	private List<String> _views = new ArrayList<String>();
	private boolean _ready = true;
   
	/**	===================================================== */
	/** Construtor da classe [SQLConnectionManager]           */
	/**	----------------------------------------------------- */
	public SQLConnectionManager(String driver, String connection, String user, String pass) {
		_driver = driver;
		_connection_string = connection;
		if (connection != null) {
			if (connection.startsWith("jdbc:mysql:")) {
				_server_type = 0;
			}
			else if (connection.startsWith("jdbc:oracle:thin:")) {
				_server_type = 1;
			}
			else if (connection.startsWith("jdbc:postgresql:")) {
				_server_type = 2;
			}
			else if (connection.startsWith("jdbc:jtds:")) {
				_server_type = 3;
			}
		}
		_login = user;
		_pass = pass;
		if (_connection != null || isConnected()) {
			_connection = null;
		}
		try {  
			Class.forName(_driver);		   
			switch (_server_type) {
				case 0:
				case 2:
				case 3:
					_connection = DriverManager.getConnection(_connection_string, _login, _pass);
		   			break;
				case 1:
		   			Properties con_prop = new Properties();
		   			con_prop.put("user", _login);
		   			con_prop.put("password", _pass);
		   			con_prop.put("processEscapes", "false");
		   			con_prop.put("SetBigStringTryClob", "true"); // minimizar problemas com inserção de strings maiores que 4k em campos CLOB.
		   			_connection = DriverManager.getConnection(_connection_string, _login, _pass);
		   			break;
			}
			if (_connection != null && getServerType() < 2) {
				_connection.setAutoCommit(true);
			}
			if (isConnected()) {
				String info = "";   
				ResultSet rs = null;
				switch (getServerType()) {
				
					// -- MySQL
					case 0:
			   			rs = this.executeQuery("SHOW VARIABLES");
			   			while (rs != null && rs.next()) {
			   				String prop_name = rs.getString(1).toUpperCase();
							if (prop_name.equalsIgnoreCase("version")) {
								info += prop_name + "#<i>" + rs.getString(2) + "</i>;";
							}
							else if (prop_name.equalsIgnoreCase("storage_engine")) {
								info += prop_name + "#<i>" + rs.getString(2) + "</i>;";
							}
							else if (prop_name.equalsIgnoreCase("datadir")) {
								info += prop_name + "#<i>" + rs.getString(2) + "</i>;";
							}
							else if (prop_name.equalsIgnoreCase("datetime_format")) {
								info += prop_name + "#<i>" + rs.getString(2) + "</i>;";
							}
							else if (prop_name.equalsIgnoreCase("basedir")) {
								info += prop_name + "#<i>" + rs.getString(2) + "</i>;";
							}
						}
						rs.close();
						break;
						
					// -- Oracle
			   		case 1:
			   			executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=SYSTEM");
			   			rs = executeQuery("SELECT * FROM v$version");
			   			if (rs == null) {
			   				info += "AVISO#Para mais informações conecte-se com o usuário SYSTEM<br>ou outro usuario com permissão de administrador do banco.;";
			   			}
			   			else {
			   				for (int i = 0; rs.next(); i++) {
			   					switch(i) {
			   						case 0:
			   							info += "Versão do servidor#";
			   							break;
			   						default:
			   							info += " #";
			   					}
			   					info += rs.getString(1) + ";";
			   				}
			   			}
			   			break;

			   		// -- Postgree
			   		case 2:
			   			info += "Conectado!";
			   			break;
			   		
			   		// -- SQL Server
			   		case 3:	
			   			rs = this.executeQuery("SELECT SERVERPROPERTY('productversion'), SERVERPROPERTY ('productlevel'), SERVERPROPERTY ('edition')");
			   			while (rs.next()) {
			   				info += "Product Version#"+rs.getString(1)+";";
			   				info += "Product Level#"+rs.getString(2)+";";
			   				info += "Edition#"+rs.getString(3)+";";
			   			}
			   			rs.close();
			   			rs = this.executeQuery("SELECT @@VERSION");
			   			while (rs.next()) {
			   				info += "Label#"+rs.getString(1).replace("\n", "<br>");
			   			}
			   			rs.close();
			   			break;
			   }
			   _connection_info = info;
		   }
		   
	   }
	   catch (ClassNotFoundException c) {
		   c.printStackTrace();
	   }
	   catch (SQLException s) {
		   _last_exception = s;
		   s.printStackTrace();
		   _connection_info = s.getMessage();
	   }
   }
   
	/**	===================================================== */
	/** Retorna a conexão JDBC.							      */
	/**	----------------------------------------------------- */
	public Connection getConnection() {
		return _connection;
	}
	
	/**	===================================================== */
	/** Finaliza a conexão JDBC.						      */
	/**	----------------------------------------------------- */
	public void closeConnection() {
		try {
			Class.forName(_driver);
			_connection.close();  
			_connection = null;  
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	 
	/**	===================================================== */
	/** Verifica se a conexão JDBC está ativa.                */
	/**	----------------------------------------------------- */
	public boolean isConnected() {
		if (_connection == null) {
			return false;
		}
		try {
			return !_connection.isClosed(); 
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false; 
		}
	}
	
	/**	===================================================== */
	/** Retorna a Database selecionada atualmente.            */
	/**	----------------------------------------------------- */
	public String getDatabase() {
		try {
			if (_connection == null || _connection.isClosed()) {
				return null;
			}
		}
		catch (Exception e) {
			// -- void --
		}
		try {
			_database = _connection.getCatalog();
		}
		catch (SQLException e) {
			// -- void --
		}
		if (_database == null || _database.isEmpty()) {
			try {
				_database = _connection.getSchema();
			}
			catch (SQLException e) {
				// -- void --
			}   
		}
		if (_database == null || _database.isEmpty()) {
			try {
				Statement st = _connection.createStatement();
				ResultSet rs = st.executeQuery("SELECT DATABASE()");
				if (rs.next()) {
					_database = rs.getString(1);
				}
				st.close();
			}
			catch (Exception e) {
				//e.printStackTrace();
				_database = null; 
			}
		}
		return _database;
	}
	
	/**	===================================================== */
	/** Verifica se existe uma Database selecionada.          */
	/**	----------------------------------------------------- */
	public boolean isDatabaseSelected() {
		return getDatabase() != null ? true : false;
	}
	
	/**	===================================================== */
	/** Define a Database selecionada.				          */
	/**	----------------------------------------------------- */
	public void setDatabase(String database) {
		try {
			if (_connection != null && !_connection.isClosed()) {
				_connection.setCatalog(database);
				//_connection.setSchema(database); // --> problema com o [jTDS - SQL Server]
				_database = database;
			}
		}
		catch (Exception e) {
			// -- void --
		}
	}
	
	/**	===================================================== */
	/** Alterna a Database selecionada.				          */
	/**	----------------------------------------------------- */
	public void switchDatabase(String database) {
		switch(getServerType()) {
			case 0:
				executeUpdate("USE `" + database + "`"); 
				break;
			case 1:
				Exception e = executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=" + database); 
				if (e != null) {
					e.printStackTrace();
				}
				break;
			case 2:
				System.out.println("PostgreSQL nao possui metodos para alternar entre schemas. Reconecte a aplicacao.");
				break;
			case 3:
				executeUpdate("USE " + database);
				break;
		}
	}
	
	/**	===================================================== */
	/** Retorna o valor atual da variável indicada.	          */
	/** -> Implementado para MsSQL e MySQL.			          */
	/**	----------------------------------------------------- */
	public String getVariable(String key) {
		try {
			if (_connection == null || _connection.isClosed()) {
				return null;
			}
		}
		catch (Exception e) { }
		ResultSet rs = null;
		Statement st = null;
		String index = null, value = null;
		try {
			st = _connection.createStatement();
			switch (getServerType()) {
				case DB_MYSQL:
					st.executeQuery("SHOW VARIABLES");
					rs = st.getResultSet();
					while (rs != null && rs.next()) {
						index = rs.getString(1);
						if (index.equalsIgnoreCase(key)) {
							value = rs.getString(2);
						}
					}
					break;
					
				case DB_MSSQL:
					st.executeQuery(("SELECT SERVERPROPERTY('[property]')").replace("[property]", key));
					rs = st.getResultSet();
					if (rs != null && rs.next()) {
						value = rs.getString(1);
					}
					break;
					
				default:
					return null;
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try {
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
				if (st != null && !st.isClosed()) {
					st.close();
				}				
			}
			catch (Exception e1) { } 
		}
		return value;
	}
	
	
	
	/**	===================================================== */
	/** Retorna a lista de Databases disponíveis.	          */
	/**	----------------------------------------------------- */
	public List<String> getDatabasesList() {
		try {
			if (_connection == null || _connection.isClosed()) {
				return null;
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		List<String> list = new ArrayList<String>();
		try {
			ResultSet rs = null;
			Statement st = _connection.createStatement();;
			switch (getServerType()) {
				case 0:
					st.executeQuery("SHOW DATABASES");
					break;
				case 1:
					st.executeQuery("SELECT username FROM dba_users WHERE account_status LIKE '%OPEN%'");
					break;
				case 2:
					st.executeQuery("SELECT pg_database.datname FROM pg_database UNION SELECT pg_database.datname FROM pg_database WHERE pg_database.datdba NOT IN (SELECT usesysid FROM pg_user) ORDER BY 1");
					break;
				case 3:
					st.executeQuery("SELECT name FROM master.dbo.sysdatabases");
					//st.executeQuery("EXEC sp_databases");
					break;
			}
			if (!st.isClosed()) {
				rs = st.getResultSet();
			}
			while (rs != null && !rs.isClosed() && rs.next()) {
				list.add(rs.getString(1));
			}
			if (rs != null && !rs.isClosed()) {
				rs.close(); 
			}
			if (st != null && !st.isClosed()) {
				st.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**	===================================================== */
	/** Retorna a lista de Tabelas disponíveis.		          */
	/**	----------------------------------------------------- */
	public List<String> getTables() {
		if (getDatabase() == null) {
			return null;
		}
		try {
			if (isConnected()) {
				ResultSet rs = null;
				List<String> out = new ArrayList<String>();
				if (!_connection.getAutoCommit()) {
					_connection.commit();
				}
				Statement stmt = _connection.createStatement();
				String version = null;
				switch (getServerType()) {
					// -- MySQL
					case DB_MYSQL: 
						version = getVariable("version");
						stmt.execute(version != null && version.startsWith("4.") ? "SHOW TABLES" : "SELECT TABLE_NAME from information_schema.tables WHERE TABLE_SCHEMA='" + getDatabase() + "' AND TABLE_TYPE LIKE '%TABLE%'");
						break;
					// -- Oracle
					case DB_ORACLE: 
						stmt.execute("SELECT object_name FROM ALL_OBJECTS WHERE object_type='TABLE' AND owner='" + getDatabase() + "' AND status='VALID'");
						break;
					// -- Postgree
					case DB_POSTGREE:
						stmt.execute("SELECT c.relname FROM pg_catalog.pg_class c LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relkind IN ('r','') AND n.nspname NOT IN ('pg_catalog', 'pg_toast') AND pg_catalog.pg_table_is_visible(c.oid) ORDER BY 1");
						break;
					// -- SQL Server
					case DB_MSSQL:
						stmt.execute("EXEC sp_tables"); // -- MS2000 | MS2012 -> SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE'
						break;
				}
				rs = stmt.getResultSet();
				while (rs != null && rs.next()) {
					switch (getServerType()) {
						case 3:
							if (getDatabase() != null && rs.getString(1).equalsIgnoreCase(getDatabase()) && rs.getString(2).equalsIgnoreCase("DBO") && rs.getString(4).equalsIgnoreCase("TABLE")) {
								out.add(rs.getString(3));
							}
							break;
						default:
							version = rs.getString(1);
							if (version != null && !version.isEmpty()) {
								out.add(version);
	   		   				}
					}
				}
				rs.close();
	   			stmt.close();
	   			_tables = out;
	   			return out;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**	===================================================== */
	/** Retorna a lista de Views disponíveis.		          */
	/**	-> Implementado para MsSQL e MySQL					  */
	/**	----------------------------------------------------- */
	public List<String> getViews() {
		if (getDatabase() == null) {
			return null;
		}
		try {
			if (isConnected()) {
				ResultSet rs = null;
				List<String> out = new ArrayList<String>();
				if (!_connection.getAutoCommit()) {
					_connection.commit();
				}
				Statement stmt = _connection.createStatement();
  				String version = null;
  				switch (getServerType()) {
  					// -- MySQL
  					case 0:
  						version = getVariable("version");
		   				if (version.startsWith("4.")) {
		   					return null;
		   				}
		   				stmt.execute("select TABLE_NAME from information_schema.views WHERE TABLE_SCHEMA='" + getDatabase() + "'");
		   				break;
		   			// -- Oracle
		   			case 1:
		   				return null;
		   			// -- Postgree		-> não implementado.
		   			case 2:
		   				return null;
		   			// -- SQL Server	-> não implementado.
		   			case 3:
		   				stmt.execute("EXEC sp_tables");
		   				break;
		   		}
  				rs = stmt.getResultSet();
   				while (rs != null && rs.next()) {
   					switch (getServerType()) {
   						case 3:
   							if (getDatabase() != null && 
   								rs.getString(1).equalsIgnoreCase(getDatabase()) &&	// nome da database
   								rs.getString(2).equalsIgnoreCase("DBO") &&			// owner
   								rs.getString(4).equalsIgnoreCase("VIEW")) {			// tipo de objeto
   								out.add(rs.getString(3));
   							}
   							break;
   						default:
   							version = rs.getString(1);
   		   					if (version != null && !version.isEmpty()) {
   		   						out.add(version);
   		   					}
   					}
   				}
   				rs.close();
   				stmt.close();
   				_views = out;
   				return out;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**	===================================================== */
	/** Verifica se o parametro passado é uma Tabela.	      */
	/**	----------------------------------------------------- */
	public boolean isTable(String name) {
		if (name == null) {
			return false;
		}
		else {
			name = name.replace("`", "");
		}
		if (_tables == null || _tables.size() == 0) {
			_tables = getTables();
		}
		if (_tables != null && _tables.size() > 0) {
			for (String table : _tables) {
				if (table.equalsIgnoreCase(name.trim())) {
					return true;
				}
			}
		}
		return false;
	}
   
	/**	===================================================== */
	/** Verifica se o parametro passado é uma View.		      */
	/**	----------------------------------------------------- */
	public boolean isView(String name) {
		if (name == null) {
			return false;
		}
		else {
			name = name.replace("`", "");
		}
		if (_views == null || _views.size() == 0) {
			_views = getViews();
		}
		if (_views != null && _views.size() > 0) {
			for (String table : _views) {
				if (table.equalsIgnoreCase(name.trim())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**	===================================================== */
	/** Retorna uma lista com todos as Tabelas e Views.	      */
	/**	----------------------------------------------------- */
	public List<String> getAllObjects() {
		List<String> a = getViews();
		List<String> b = getTables();
		List<String> out = new ArrayList<String>();
		if (a != null) {
			for (String item : a) {
				out.add(item);
			}
		}
		if (b != null) {
			for (String item : b) {
				out.add(item);
			}
		}
		return out;
	}
	
	
	/**	===================================================== */
	/** Executa um comando SQL.							      */
	/** Retorna um exception em caso de erro ou null.		  */
	/**	----------------------------------------------------- */
	public Exception executeUpdate(String sql_query) {
		if (sql_query == null || sql_query.isEmpty()) {
			return null;
		}
		_time = System.currentTimeMillis();
		_ready = false;
		try {
			if (_statement_update != null) {
				_statement_update.close();
			}
			_statement_update = _connection.prepareStatement(sql_query);
			_statement_update.execute();
			if (!_connection.getAutoCommit()) {
				_connection.commit();
			}
			_statement_update.close();
			_time = (System.currentTimeMillis() - _time) / 1000.D;
			sql_query = sql_query.replace("  ", " ").trim().toLowerCase();
		}
		catch (Exception e) {
			_ready = true;
			return e;
		}  
		if ((getServerType() == 0 || getServerType() == 3) && sql_query.startsWith("use")) {
			setDatabase(sql_query.split(" ")[1].replace("`", ""));
		}
		else if (getServerType() == 1 && sql_query.startsWith("alter session set current_schema")) {
			_database = sql_query.split("=")[1].trim();
		}
		_ready = true;
		return null;
	}
	
	/**	===================================================== */
	/** Executa uma consulta SQL.						      */
	/** Retorna um ResultSet ou null.						  */
	/**	----------------------------------------------------- */
	public ResultSet executeQuery(String consulta) {
		_ready = false;
		ResultSet result = null;  
		try {
			if ((_connection != null) && (isConnected())) {
				_time = System.currentTimeMillis();
				if (_default_statement != null) {
					_default_statement.close();
				}
				_last_exception = null;
				_default_statement = _connection.prepareStatement(consulta);  
				result = _default_statement.executeQuery();
				if (!_connection.getAutoCommit()) {
					_connection.commit();
				}
				_time = (System.currentTimeMillis() - _time) / 1000.D;
			}
			_ready = true;
			return result;
		}
		catch (Exception e) {
			if (e.getMessage() != null) {
				JQueryPane pane = null;
				if (MainWindow.getActiveTab() instanceof JQueryPane) {
					pane = (JQueryPane)MainWindow.getActiveTab();
					pane.updateQueryStatus("A consulta SQL: <font color=blue><u>" + consulta.subSequence(0, consulta.length() > 64 ? 64 : consulta.length()) + "</u></font> retornou a seguinte messagem de erro: ", e);
				}
				_last_exception = e;
				e.printStackTrace();
			}
			_ready = true;
			return null;
		}  
	}
	
	
	/**	===================================================== */
	/** Retorna o tipo de servidor.						      */
	/** 													  */
	/** DB_MYSQL	 = 0						 			  */
	/** DB_ORACLE 	 = 1									  */
	/** DB_POSTGREE	 = 2									  */
	/** DB_SQLSERVER = 3									  */
	/**	----------------------------------------------------- */
	public int getServerType() {
		return _server_type;
	}
	
	/**	===================================================== */
	/** Retorna o último erro durante um comando SQL.	      */
	/**	----------------------------------------------------- */
	public Exception getLastError() {
		return _last_exception;
	}
	
	/**	================================================================= */
	/** Método específico para bancos de dados Oracle.	    			  */
	/**	Sintaxe:											 			  */
	/** @INSERT REGISTER_CLOB col INTO table WHERE cond VALUES ('ABCDE')  */
	/**	----------------------------------------------------------------- */
	@SuppressWarnings("deprecation")
	public boolean insertClob(String query)  {
		   ResultSet rs = null;
		   PreparedStatement statement = null;
		   StringTokenizer st = null;
		   String sql = null;
		   String field = null;
		   String table = null;
		   String pk_name = null;
		   String text = null;
		   StringBuffer value = new StringBuffer();
		   boolean trigger = false;
		   try {
			   st = new StringTokenizer(query, " ");
			   String token = null;
			   while (st.hasMoreTokens()) {
				   token = st.nextToken();
				   if (trigger) {
					   value.append(token + " ");
				   }
				   else {
					   token = (token != null && token.isEmpty() ? "" : token.trim().toLowerCase());
					   if (token.equalsIgnoreCase("REGISTER_CLOB")) {
						   sql = "SELECT ";// cadastro, imagem, texto from teste where cadastro = 1 for update");
					   }   
					   else if (token.equalsIgnoreCase("INTO")) {
						   sql += " FROM ";
					   }
					   else if (token.equalsIgnoreCase("TARGET")) {
						   sql += " WHERE ";
					   }
					   else if (token.equalsIgnoreCase("VALUES")) {
						   sql += " FOR UPDATE";
						   trigger = true;
					   }
					   else if (sql != null) {
						   if (sql.endsWith("SELECT ")) {
							   field = token;
						   }
						   else if (sql.endsWith(" FROM ")) {
							   table = token;
						   }
						   else if (sql.endsWith(" WHERE ")) {
							   pk_name = token;
							   try {
								   statement = _connection.prepareStatement("SELECT " + table + "_" + pk_name + "_SEQ.currval FROM dual");
								   rs = statement.executeQuery();
								   if (rs != null && rs.next()) {
									   sql += pk_name + "=" + rs.getString(1);
								   }
								   rs.close();
								   statement.close();
							   }
							   catch (Exception e) {
								   e.printStackTrace();
							   }
							   continue;
						   }
						   sql += token + " ";
					   }   
				   }
			   }
			   statement = _connection.prepareStatement(sql);
			   rs = statement.executeQuery();
			   if (rs != null && rs.next()) {
				   Clob clob = rs.getClob(field);  
				   char[] cbuf = new char[1024];
				   text = getDefaultString(value.toString());
				   Reader cin = new StringReader(text.substring(2,text.length() - 4).replace("'", "''"));
				   // -- Específico para o driver oracle
				   Writer cout = ((CLOB)clob).getCharacterOutputStream();
				   int charsRead = 0;  
				   while ((charsRead = cin.read(cbuf)) != -1) {  
				       cout.write(cbuf, 0, charsRead);  
				   }  
				   cin.close();  
				   cout.close();
				   if (!_connection.getAutoCommit()) {
					   _connection.commit();
				   }
				   rs.close();
				   statement.close();
				   return true;
			   }
		   }
		   catch (Exception e) {
			   e.printStackTrace();
			   try {
				   if (rs != null) {
					   rs.close();
				   }
				   if (statement != null) {
					   statement.close();
				   }
			   }
			   catch (Exception e1) {
				   e1.printStackTrace();
			   }
		   }
		   return false;
	   }
	
	
	/**	===================================================== */
	/** Retorna o tempo de execução do último comando SQL.    */
	/**	----------------------------------------------------- */
	public double getLastQueryTime() {
		return _time;
	}
	
	/**	===================================================== */
	/** Define o tempo de execução do último comando SQL .    */
	/**	----------------------------------------------------- */
	public void setLastQueryTime(double time) {
		_time = time;
	}

	/**	===================================================== */
	/** Retorna informações sobre a conexão atual, definidos  */
	/** no momento de execução do construtor.				  */
	/**	----------------------------------------------------- */
	public String getConnectionInfo() {
		return _connection_info;
	}
	
	/**	===================================================== */
	/** Define o Nome da instância de conexão JDBC. 	      */
	/**	----------------------------------------------------- */
	public void setName(String name) {
		_name = name;
	}
	
	/**	===================================================== */
	/** Retorna o Nome da instância de conexão JDBC. 	      */
	/**	----------------------------------------------------- */
	public String getName() {
		return _name;
	}
	
	/**	===================================================== */
	/** Retorna o status atual da conexão JDBC.		 	      */
	/**	----------------------------------------------------- */
	public int getConnectionStatus() {
		if (this.isConnected()) {
			//if (_is_busy == false) return 1;
			//else return 2;
			// -- retorna 2 apenas se a conexão estiver aguardando uma resposta...
			return 1;
		}
		return 0;
	}
   
	/**	===================================================== */
	/** Retorna a versão do banco de dados selecionado.		  */
	/** Implementado para [MySQL] e [SQL Server]			  */
	/**	----------------------------------------------------- */
	public String getVersion() {
		String version = null;
		switch (getServerType()) {
			case DB_MYSQL:
				version = getVariable("version");
				try {
					if (version == null && _connection != null && !_connection.isClosed()) {
						return "MY4";
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				if (version != null) {
					return "MY" + version.substring(0, version.indexOf("."));
				}
				break;
				
			case DB_SQLSERVER:
				String[][] versions = {
						{"7.00.62", "Sphinx", "MS7"},			// SQL Server 7.0
						{"7.00.69", "Sphinx", "MS7"}, 
						{"7.00.84", "Sphinx", "MS7"}, 
						{"7.00.96", "Sphinx", "MS7"}, 
						{"7.00.10", "Sphinx", "MS7"},
						{"8.00.19", "Shiloh", "MS2000"},		// SQL Server 2000
						{"8.00.38", "Shiloh", "MS2000"},
						{"8.00.53", "Shiloh", "MS2000"},
						{"8.00.76", "Shiloh", "MS2000"},
						{"8.00.20", "Shiloh", "MS2000"},
						{"9.00.13", "Yukon", "MS2005"},			// SQL Server 2005
						{"9.00.20", "Yukon", "MS2005"},
						{"9.00.30", "Yukon", "MS2005"},
						{"9.00.40", "Yukon", "MS2005"},
						{"9.00.50", "Yukon", "MS2005"},
						{"10.00.16", "Katmai", "MS2008"},		// SQL Server 2008
						{"10.00.25", "Katmai", "MS2008"},
						{"10.00.40", "Katmai", "MS2008"},
						{"10.00.55", "Katmai", "MS2008"},
						{"10.50.16", "Kilimanjaro", "MS2008"},	// SQL Server 2008 R2
						{"10.50.25", "Kilimanjaro", "MS2008"},
						{"10.50.40", "Kilimanjaro", "MS2008"},
						{"11.00.21", "Denali", "MS2012"},		// SQL Server 2012
						{"11.00.30", "Denali", "MS2012"},
						{"11.00.50", "Denali", "MS2012"},
						{"12.00.20", "Hekaton", "MS2014"}		// SQL Server 2014
				};
				version = getVariable("productversion");
				if (version != null && !version.isEmpty()) {
					// release oficial.
					for (String[] ver : versions) {
						if (version.contains(ver[0])) {
							return ver[2];
						}
					}
					// release extra oficial.
					for (String[] ver : versions) {
						if (version.startsWith(ver[0].substring(0, 4))) {
							return ver[2];
						}
					}
				}
				break;
		}
		return null;
	}
	
	
	
	/**	===================================================== */
	/** Retorna o parte ou todos dos registros de uma tabela. */
	/** Caso os valores start = offset = 0 retorna tudo.      */
	/**	----------------------------------------------------- */
	public ResultSet getTableData(String table, int start, int offset) {
		PreparedStatement st = null;
		ResultSet rs = null;
		String pk = null;
		try {
			// -- obtem informacoes sobre a chave primaria e colunas.
			DatabaseMetaData meta = _connection.getMetaData();
			rs = meta.getPrimaryKeys(null, null, table);
			try {
				while (rs.next()) {
					pk = rs.getString("COLUMN_NAME");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (rs != null && !rs.isClosed()) {
					rs.close();
					rs = null;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			int limit = offset - start;
			switch (getServerType()) {
			
				// -- MySQL
				case DB_MYSQL:
					if (start == 0 && offset == 0) {
						st = _connection.prepareStatement(("SELECT /*40001 SQL_NO_CACHE */ * FROM [table]").replace("[table]", table));
					}
					else {
						st = _connection.prepareStatement(("SELECT /*40001 SQL_NO_CACHE */ * FROM [table] LIMIT ? OFFSET ?").replace("[table]", table));
						st.setInt(1, limit/*start*/);
						st.setInt(2, start/*offset*/);
					}
					break;
					
				// -- SQL Server
				case DB_SQLSERVER:
					if (start == 0 && offset == 0 || pk == null) {
						st = _connection.prepareStatement(("SELECT * FROM [table]").replace("[table]", table));
					}
					else {
						switch (getVersion()) {
							case "MS2000":
								st = _connection.prepareStatement(("SELECT * FROM [table] WHERE [pk] IN (SELECT TOP [1] [pk] FROM (SELECT TOP [2] [pk] FROM [table] ORDER BY [pk] ASC) AS table1 ORDER BY [pk] DESC) ORDER BY [pk] ASC").replace("[table]", table).replace("[pk]", pk).replace("[1]", String.valueOf(limit)).replace("[2]", String.valueOf(offset)));
								break;
							case "MS2012":
							case "MS2014":
								st = _connection.prepareStatement(("SELECT * FROM [table] ORDER BY [pk] OFFSET [1] ROWS FETCH NEXT [2] ROWS ONLY").replace("[table]", table).replace("[pk]", pk).replace("[1]", String.valueOf(start)).replace("[2]", String.valueOf(limit)));
								break;
						}
						
						
					}
					break;
					
					
			}
			rs = st.executeQuery();
		}
		catch (Exception e) {
			// -- void --
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public boolean isReady() {
		return _ready;
	}
	
	/**	===================================================== */
	/** Retorna a estrutura da tabela indicada.				  */
	/** -> A estrutura da tabela depende do banco de destino  */
	/** -> Implementado para MSSQL e MySQL [crossdb] 		  */
	/**	----------------------------------------------------- */
	public String getTableStructure(String table, int destination) {
		table = table.replace("`", "");
		String pk = null;
		Statement st = null;
		DatabaseMetaData meta = null;
		ResultSetMetaData rs_meta = null;
		ResultSet rs = null;
		StringBuffer out = new StringBuffer();

		try {
			meta = _connection.getMetaData();
			rs = meta.getPrimaryKeys(null, null, table);
			
			// ------------------------------------------
			// -> Obtem a chave PK da tabela           ->
			// ------------------------------------------
			try {
				while (rs.next()) {
					pk = rs.getString("COLUMN_NAME");
				}
			}
			catch (Exception e) { e.printStackTrace(); }
			finally {
				if (rs != null && !rs.isClosed()) {
					rs.close();
					rs = null;
				}
			}
			
			// ------------------------------------------
			// -> Obtem o metadata das colunas         ->
			// ------------------------------------------
			try {
				st = _connection.createStatement();
				st.execute(("SELECT * FROM [table] WHERE 1=2").replace("[table]", table));
				rs = st.getResultSet();
				rs_meta = (rs != null ? rs.getMetaData() : null);
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		catch (Exception e) { e.printStackTrace(); }

		
		switch (destination) {
			case DB_MYSQL:
				if (getServerType() == DB_MYSQL) {
					try {
						if (rs != null && !rs.isClosed()) {
							rs.close();
						}
						if (st != null && !st.isClosed()) {
							st.close();
						}
						st = _connection.createStatement();
						st.execute(("SHOW CREATE TABLE [table]").replace("[table]", table));
						rs = st.getResultSet();
						while (rs != null && !rs.isClosed() && rs.next()) {
							out.append(rs.getString(2));
						}
						out.append(";\r\n");
					}
					catch (Exception e) { e.printStackTrace(); }
				}
				else {
					try {
						out.append(("CREATE TABLE [table] (\r\n").replace("[table]", table));
						for (int i = 1; i <= rs_meta.getColumnCount(); i++) {
							out.append("\t");
							out.append(getFieldSQL(rs_meta.getColumnName(i), rs_meta, destination));
							out.append(i == rs_meta.getColumnCount() ? "" : ",\r\n");
						}
						if (pk != null) {
							out.append((",\r\n\tPRIMARY KEY ([pk])").replace("[pk]", pk == null ? "" : pk));
						}
						out.append("\r\n);\r\n");
					}
					catch (Exception e) { e.printStackTrace(); }
				}
				return (out.length() > 0 ? out.toString() : null);
				
			case DB_MSSQL:
				try {
					out.append(("CREATE TABLE [table] (\r\n").replace("[table]", table));
					for (int i = 1; i <= rs_meta.getColumnCount(); i++) {
						out.append("\t");
						out.append(getFieldSQL(rs_meta.getColumnName(i), rs_meta, destination));
						out.append(i == rs_meta.getColumnCount() ? "" : ",\r\n");
					}
					if (pk != null) {
						out.append((",\r\n\tPRIMARY KEY ([pk])").replace("[pk]", pk == null ? "" : pk));
					}
					out.append("\r\n);\r\n");
				}
				catch (Exception e) { e.printStackTrace(); }
				return (pk != null ? out.toString() : null);
		}
		return null;
	}
	
	/**	===================================================== */
	/** Retorna a estrutura dos índices de uma tabela		  */
	/** -> Implementado para MSSQL e MySQL [crossdb] 		  */
	/**	----------------------------------------------------- */
	public String getTableIndexes(String table, int destination) {
		table = table.replace("`", "");
		DatabaseMetaData meta = null;
		ResultSet rs = null;
		String model = null, index = null, index_name = null, index_fields = null, index_type = null;
		String[] index_types = null;
		StringBuilder out = new StringBuilder();
		try {
			meta = _connection.getMetaData();
			rs = meta.getIndexInfo(null, null, table, false, false);
			
			switch (destination) {
				case DB_MSSQL:
					model = "-- Index [name]\r\n";
					model += "IF EXISTS (SELECT name FROM sys.indexes WHERE name='[name]' AND object_id=OBJECT_ID('[table]')) DROP INDEX [name] ON [table];\r\n";
					model += "CREATE [type] INDEX [name] ON [table] ([fields])";
					index_types = new String[]{"UNIQUE", ""};		// -> unique - non_unique
					break;
					
				case DB_MYSQL:
					model = "ALTER TABLE [table] ADD [type] [name] ([fields])";
					index_types = new String[]{"UNIQUE", "INDEX"};	// -> unique - non_unique
					break;
			}
			
			while (rs != null && rs.next()) {
				if (rs.getString("INDEX_NAME") != null && !rs.getString("INDEX_NAME").startsWith("PK_") && !rs.getString("INDEX_NAME").startsWith("PRIMARY")) {
					index_type = rs.getString("NON_UNIQUE");
					index_type = (index_type.equalsIgnoreCase("FALSE") ? "0" : (index_type.equalsIgnoreCase("TRUE") ? "1" : index_type));
					if (index_name != null && !index_name.equalsIgnoreCase(rs.getString("INDEX_NAME"))) {
						index = model;
						index = index.replace("[table]", table);
						index = index.replace("[type]", index_types[Integer.parseInt(index_type)]/*(index_type.equals("0") ? "INDEX" : "UNIQUE")*/);
						index = index.replace("[name]", index_name);
						index = index.replace("[fields]", index_fields);
						out.append(index);
						out.append(";\r\n");
						index_name = index_fields = "";
					}
					
					if (index_name == null || index_name.isEmpty()) {
						index_name = rs.getString("INDEX_NAME");
						index_fields = "";
					}
					
					if (index_name.equalsIgnoreCase(rs.getString("INDEX_NAME"))) {
						index_fields += (index_fields == null || index_fields.isEmpty() ? "" : ", ") + rs.getString("COLUMN_NAME");
					}
				}
			}
			
			if (index_name != null) {
				index = model;
				index = index.replace("[table]", table);
				index = index.replace("[type]", index_types[Integer.parseInt(index_type)]/*(index_type.equals("0") ? "INDEX" : "UNIQUE")*/);
				index = index.replace("[name]", index_name);
				index = index.replace("[fields]", index_fields);
				out.append(index);
				out.append(";\r\n");
			}
			
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			return out.toString();
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	
	
	
	
	
	
	private static String getFieldSQL(String field, ResultSetMetaData meta, int destination) {
		if (field == null || meta == null) {
			return null;
		}
		String name = null;
		boolean auto_increment = false;
		int type = 0, length = 0, precision = 0, isnullable = 0;
		try {
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if (field.equalsIgnoreCase(meta.getColumnName(i))) {
					name = meta.getColumnName(i);
					type = meta.getColumnType(i);
					length = meta.getColumnDisplaySize(i);
					precision = length - meta.getPrecision(i);
					field = meta.getColumnClassName(i);
					isnullable = meta.isNullable(i);
					auto_increment = meta.isAutoIncrement(i);
					break;
				}
			}
			
			if (name != null) {
				StringBuffer out = new StringBuffer();
				out.append(name);
				out.append(" ");
				switch (destination) {
					case DB_MYSQL:
						switch (type) {
							case Types.BIT:
								out.append("BIT");
								break;
							case Types.BOOLEAN:
							case Types.TINYINT:
								out.append("TINYINT");
								break;
							case Types.SMALLINT:
								out.append("SMALLINT");
								break;
							case Types.INTEGER:
								out.append(("INT([length])").replace("[length]", String.valueOf(length)));
								break;
							case Types.BIGINT:
								out.append("BIGINT");
								break;
							case Types.FLOAT:
							case Types.DOUBLE:
							case Types.DECIMAL:
							case Types.NUMERIC:
							case Types.REAL:
								out.append(("DECIMAL([length],[precision])").replace("[length]", String.valueOf(length)).replace("[precision]", String.valueOf(precision)));
								break;
							case Types.DATE:
								out.append("DATE");
								break;
							case Types.TIMESTAMP:
								out.append("DATETIME");
								break;
							case Types.CLOB:
							case Types.LONGVARCHAR:
							case Types.VARCHAR:
							case Types.CHAR:
								out.append(length < 255 ? ("VARCHAR([length])").replace("[length]", String.valueOf(length)) : "TEXT");
								break;
							case Types.BINARY:
								out.append(("BINARY([length])".replace("[length]", String.valueOf(length))));
								break;
							case Types.LONGVARBINARY:
							case Types.VARBINARY:
							case Types.BLOB:
								out.append(length < 255 ? "VARBINARY" : "BLOB");
								break;
							default:
								System.out.println("<- src.MainWindowComponents.SQLConnectionManager.getFieldSQL ->> Unknow field type (MSSQL): name=" + name + ", field=" + field + ", type=" + type + ", length=" + length + ", precision=" + precision + ", isnull=" + isnullable);
						}
						if (auto_increment) {
							out.append(" AUTO_INCREMENT");
						}
						else if (isnullable == 1) {
							out.append(" DEFAULT NULL");
						}
						break;
				
					case DB_MSSQL:
						switch (type) {
							case Types.BINARY:
								out.append(("BINARY([length])".replace("[length]", String.valueOf(length))));
								break;
							case Types.LONGVARBINARY:
							case Types.VARBINARY:
							case Types.BLOB:
								out.append(length < 255 ? "VARBINARY" : "IMAGE");
								break;
							case Types.CLOB:
							case Types.LONGVARCHAR:
							case Types.VARCHAR:
							case Types.CHAR:
								out.append(length < 255 ? ("VARCHAR([length])").replace("[length]", String.valueOf(length)) : "TEXT");
								break;
							case Types.BIGINT:
							case Types.TINYINT:
							case Types.INTEGER:
								out.append("INT");
								break;
							case Types.BIT:
								out.append("TINYINT");
								break;
							case Types.TIMESTAMP:
								out.append("DATETIME");
								break;
							case Types.NUMERIC:
							case Types.DECIMAL:
							case Types.REAL:
								out.append(("NUMERIC([length],[precision])").replace("[length]", String.valueOf(length)).replace("[precision]", String.valueOf(precision)));
								break;
							default:
								System.out.println("<- src.MainWindowComponents.SQLConnectionManager.getFieldSQL ->> Unknow field type (MSSQL): name=" + name + ", field=" + field + ", type=" + type + ", length=" + length + ", precision=" + precision + ", isnull=" + isnullable);
						}
						if (auto_increment) {
							out.append(" IDENTITY(1,1) NOT NULL");
						}
						else if (isnullable == 1) {
							out.append(" NULL");
						}
						break;
				}
				return out.toString();
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	
	public String getHostInfo(String key) {
	   StringTokenizer st = new StringTokenizer(_connection_string, "/");
	   if (st.countTokens() < 3) {
			return "Indefinido. ";
	   }
	   else {
		   String result = "";
		   for (int i = st.countTokens(); i > 0 ; i--) {
			   result = st.nextToken();
			   if ((key.contentEquals("driver") && i == 3) || (key.contentEquals("host") && i == 2) || (key.contentEquals("database") && i == 1)) {
				   return result; 
			   }
		   }
		   return "(..)";
	   }
   }
     
  
   
  
   

   
   
   
   
   
   /** finaliza a conexão MySQL */
  
   

   
   
   
   
   
   
   
   
    
  
   
   
   
   
   
   
	
	
	
	
	
	
   

   
   
   
   
	
	
	
	public ResultSet getDumpData(String table, int dest_db, int limit, int offset) {
		// -- obtem informacoes das colunas a serem utilizadas para dumpar os dados das tabelas:
		StringBuffer fields = new StringBuffer();
		String pk_name = null;
		try {
			if (this.getServerType() == 3) { // apenas para sql server.
				ResultSet rs = executeQuery(("SELECT * FROM [table] WHERE 1=0").replace("[table]", table));
				ResultSetMetaData column_structure = rs.getMetaData();
				for (int i = 1; i <= column_structure.getColumnCount(); i++) {
					if (column_structure.isAutoIncrement(i) && column_structure.isNullable(i) == 0) {
						pk_name = column_structure.getColumnName(i);
						if (dest_db != 3) {
							fields.append(column_structure.getColumnName(i));
							fields.append(", ");
						}
					}
					else {
						fields.append(column_structure.getColumnName(i));
						fields.append(", ");
					}
				}
				fields.append("#FOE#");
				rs.close();
				rs  = executeQuery(("SELECT COUNT(*) FROM [table]").replace("[table]", table));
				int reg_count = 0;
				try {
					while (rs.next()) {
						reg_count = rs.getInt(1);
					}
				}
				catch (Exception e) { e.printStackTrace(); }
				limit  = Math.min(reg_count, limit + offset > reg_count ? Math.abs(reg_count - offset) : limit);
				offset = Math.min(reg_count, offset + limit);
				if (fields.length() == 0) {
					rs.close();
					return null;
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		switch (this.getServerType()) {
			case DB_MYSQL:
			case DB_POSTGREE:
				try {
					Connection con = getConnection();
					if (con != null && !con.isClosed()) {
						Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						ResultSet rs = null;
						String sql = null;
						if (limit > 0) {
							sql = ("SELECT [fields] FROM [table] LIMIT [a] OFFSET [b]");
							sql = sql.replace("[fields]", dest_db == 3 ? fields.toString().replace(", #FOE#", "") : "*");
							sql = sql.replace("[table]", table);
							sql = sql.replace("[a]", String.valueOf(limit));
							sql = sql.replace("[b]", String.valueOf(offset));
							rs = statement.executeQuery(sql);
							return rs; 
						}
						else {
							sql = ("SELECT [fields] FROM [table]");
							sql = sql.replace("[fields]", dest_db == 3 ? fields.toString().replace(", #FOE#", "") : "*");
							sql = sql.replace("[table]", table);
							rs = statement.executeQuery(sql);
							return rs; 							
						}
					}
				}
				catch (SQLException e1) {
					e1.printStackTrace();
				}
				return null;
				
			case DB_ORACLE:
				try {
					Connection con = getConnection();
					if (con != null && !con.isClosed()) {
						
					}			
					return null;
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				break;
				
			case DB_MSSQL:
				try {
					Connection con = getConnection();
					if (con != null && !con.isClosed()) {
						Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						ResultSet rs = null;
						String sql = null;
						if (pk_name != null && limit > 0) {
							sql = "SELECT " + (dest_db == 3 ? fields.toString().replace(", #FOE#", "") : "*") + " FROM " + table + " WHERE " + pk_name + " IN (SELECT TOP " + limit + " "  + pk_name + " FROM (SELECT TOP " + offset + " " + pk_name + " FROM " + table + " ORDER BY " + pk_name + " ASC) AS table_1 ORDER BY recno DESC) ORDER BY " + pk_name + " ASC";
							rs = statement.executeQuery(sql);
							return rs;
						}
						else {
							if (pk_name == null) {
								JOptionPane.showMessageDialog(null, "<html>Não foi possível localizar a definição de chave primária para a tabela: <b>" + table + "</b> portanto os dados não poderão ser exportados de forma paginada.<br><font color=gray>***<i> Esta consulta pode demorar alguns minutos... seja paciente!</i></font></html>", "Aviso!", JOptionPane.OK_OPTION);
							}
							sql = ("SELECT [fields] FROM [table]").replace("[fields]", dest_db == 3 ? fields.toString().replace(", #FOE#", "") : "*").replace("[table]", table);
							rs = statement.executeQuery(sql);
							return rs; 							
						}
					}					
					return null;
				}
				catch(Exception e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public String getDumpStructure(String table, int dest_db) {
		String out = null;
		ResultSet rs = null;
		StringBuffer fields = new StringBuffer();
		switch (getServerType()) {
			case DB_MYSQL: 
				rs = this.executeQuery("SHOW CREATE TABLE " + table);
				try {
					if (rs.next()) {
						out = rs.getString(2);
					}
					rs.close();
				}
				catch (Exception e) { e.printStackTrace(); }
				return out + ";\r\n";
				
			case DB_ORACLE:
				StringBuffer create_sequence = new StringBuffer();				
				try {
					List<String> pk_names  = new ArrayList<String>();
					DatabaseMetaData meta_data = _connection.getMetaData();
					rs = meta_data.getPrimaryKeys(null, null, table);
					boolean pk_exists = false;
					while (rs != null && rs.next()) {
						pk_exists = false;
						for (String pk : pk_names) {
							pk_exists = (pk_exists || rs.getString("COLUMN_NAME") == null || rs.getString("COLUMN_NAME").trim().equalsIgnoreCase(pk));
						}
						if (!pk_exists) {
							pk_names.add(rs.getString("COLUMN_NAME") != null ? rs.getString("COLUMN_NAME").trim().toUpperCase() : "");
						}
					}
					rs.close();
					rs = executeQuery("SELECT * FROM " + table + " WHERE 1=2");
					ResultSetMetaData column_structure = rs.getMetaData();
					for (int i = 1; i <= column_structure.getColumnCount(); i++) {
						fields.append("\t" + column_structure.getColumnName(i) + " ");
						switch(column_structure.getColumnType(i)) {
							case java.sql.Types.DATE:
							case java.sql.Types.TIME:
							case java.sql.Types.TIMESTAMP:
								fields.append("DATE,\n");
								break;
								
							case java.sql.Types.BIT:
							case java.sql.Types.TINYINT:
							case java.sql.Types.SMALLINT:
							case java.sql.Types.INTEGER:
							case java.sql.Types.BIGINT:
							case java.sql.Types.REAL:
							
							case java.sql.Types.FLOAT:
							case java.sql.Types.DOUBLE:
							case java.sql.Types.NUMERIC:
							case java.sql.Types.DECIMAL:
								fields.append("NUMBER(" + column_structure.getPrecision(i) + "," + column_structure.getScale(i) + "),\n");
								break;
								
							case java.sql.Types.CHAR:
								fields.append("CHAR(" + column_structure.getPrecision(i) + "),\n");
								break;
								
							case Types.VARCHAR: // varchar2
								fields.append("VARCHAR2(" + column_structure.getPrecision(i) + "),\n");
								break;

							case Types.LONGVARBINARY:
							case Types.BLOB:
								fields.append("BLOB,\n");
								break;

							case Types.LONGVARCHAR:
							case Types.CLOB:
								fields.append("CLOB,\n");
								break;
							
							default:
								System.out.println("tipo não reconhecido, tabela=" + table + ", tipo de campo=" + column_structure.getColumnType(i) + " [" + (column_structure.getColumnType(i) == Types.LONGVARCHAR) + "]");
						}
					}
					for (int k = 0; k < pk_names.size(); k++) {
						if (pk_names.get(k) == null) continue;
						fields.append("PRIMARY KEY (" + pk_names.get(k) + ")" + (k + 1 != pk_names.size() ? "," : ""));
						create_sequence.append("DROP SEQUENCE " + table + "_" + pk_names.get(k).trim().toUpperCase() + "_SEQ;\r\n");
						create_sequence.append("CREATE SEQUENCE " + table + "_" + pk_names.get(k).trim().toUpperCase() + "_SEQ MINVALUE 1 INCREMENT BY 1 NOCACHE ORDER;\r\n");
					}
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return "CREATE TABLE " + table + " (\n" + fields.toString() + "\n);\r\n" + create_sequence.toString(); 
				
			case DB_MSSQL:
				try {
					boolean pk_found = false;
					rs = executeQuery("SELECT * FROM " + table + " WHERE 1=0");
					ResultSetMetaData column_structure = rs.getMetaData();
					for (int i = 1; i <= column_structure.getColumnCount(); i++) {
						fields.append(column_structure.getColumnName(i) + " ");
						pk_found = column_structure.getColumnTypeName(i).toLowerCase().contains("identity");
						switch(column_structure.getColumnType(i)) {
							case java.sql.Types.DATE:
							case java.sql.Types.TIME:
							case java.sql.Types.TIMESTAMP:
				
							case java.sql.Types.BINARY:
							case java.sql.Types.VARBINARY:
							case java.sql.Types.BLOB:
						
							case java.sql.Types.BIT:

							case java.sql.Types.TINYINT:
							case java.sql.Types.SMALLINT:
							case java.sql.Types.INTEGER:
							case java.sql.Types.BIGINT:
							case java.sql.Types.REAL:
								if (column_structure.getColumnTypeName(i).toLowerCase().contains("int") && !column_structure.getColumnTypeName(i).toLowerCase().contains("tinyint")) {
									fields.append(" bigint");
								}
								else if (column_structure.getColumnTypeName(i).toLowerCase().contains("date")) {
									fields.append(" datetime");
								}
								else {
									fields.append(" " + column_structure.getColumnTypeName(i).toLowerCase().replace(pk_found ? " identity" : "", ""));	
								}
								break;
							case java.sql.Types.FLOAT:
							case java.sql.Types.DOUBLE:
							case java.sql.Types.NUMERIC:
							case java.sql.Types.DECIMAL:
								fields.append(" " + column_structure.getColumnTypeName(i).toLowerCase().replace(pk_found ? " identity" : "", "") +"(" + column_structure.getPrecision(i) + "," + column_structure.getScale(i) + ")");
								break;
							case java.sql.Types.CHAR:
							case java.sql.Types.VARCHAR:
							case java.sql.Types.CLOB:
							case java.sql.Types.LONGVARCHAR:
								if (column_structure.getPrecision(i) > 255 && column_structure.getColumnTypeName(i).toLowerCase().contains("varchar")) {
									fields.append(" text");
								}
								else {
									fields.append(" " + column_structure.getColumnTypeName(i).toLowerCase().replace(pk_found ? " identity" : "", "") +"(" + column_structure.getPrecision(i) + ")");
								}
								break;
							default:
								System.out.println("-> getStructure(" + table + "," + dest_db + ")[SQL_SERVER] - Unknow Field -> " + column_structure.getColumnType(i));
						}
						if (column_structure.isAutoIncrement(i) && column_structure.isNullable(i) == 0) {
							fields.append(" NOT NULL IDENTITY PRIMARY KEY");
						}
						if ((i + 1) <= column_structure.getColumnCount()) {
							fields.append(", \n");
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return "CREATE TABLE " + table + " (\n" + fields.toString() + "\n);\r\n"; 
			
			case DB_POSTGREE:
				//		  				  1                   2                         3                    4                       5                    6                 7                                 8                          9                                10                                                 
				rs = executeQuery("SELECT columns.table_name, columns.ordinal_position, columns.column_name, columns.column_default, columns.is_nullable, columns.udt_name, columns.character_maximum_length, columns.numeric_precision, columns.numeric_precision_radix, columns.numeric_scale FROM information_schema.columns  WHERE columns.table_schema::text = 'public'::text AND columns.table_name='" + table + "' ORDER BY columns.table_name, columns.ordinal_position");
				out = "CREATE TABLE IF NOT EXISTS " + table + " (";
				try {
					String t = null;
					String p = null;
					while (rs != null && rs.next()) {
						t = rs.getString(6);
						p = rs.getString(7);
						if (t == null) {
							t = "NULL";
						}
						else if (t.equalsIgnoreCase("varchar")) {
							t = (p != null && Integer.parseInt(p) > 255 ? "text" : "varchar(" + p + ")");
						}
						else if (t.equalsIgnoreCase("numeric")) {
							t = "numeric(" + rs.getString(8) + "," + rs.getString(10) + ")";
						}
						out += "\r\n\t" + rs.getString(3);
						out += " " + t + ",";
					}
					out += "EOL";
					out = out.replace(",EOL", "");
					out += ");\r\n\r\n";
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return out;			
		}
		return null;
	}
	
	
	public String getDefaultString(String text) {
		text = text.replace("\\" + "\\", "\\");
		text = text.replace("'" + "'", "\'");
		return text;
	}
}  