// $ANTLR : "Recognizer.g" -> "RecognizerLexer.java"$

package Components.MainWindowComponents.Highlighter;

public interface RecognizerParserTokenTypes {
	String[] keywords = (
			// MYSQL
			"add|all|alter|analyze|and|as|asc|asensitive|before|between|bigint|binary|blob|both|by|call|cascade|case|change|char|character|check|collate|column|condition|constraint|continue|convert|create|cross|current_date|current_time|current_timestamp|current_user|cursor|database|databases|day_hour|day_microsecond|day_minute|day_second|dec|decimal|declare|default|delayed|delete|desc|describe|deterministic|distinct|distinctrow|div|double|drop|dual|each|else|elseif|enclosed|end|escaped|exists|exit|explain|false|fetch|float|float4|float8|for|force|foreign|from|fulltext|grant|group|having|high_priority|hour_microsecond|hour_minute|hour_second|if|ignore|in|index|infile|inner|inout|insensitive|insert|int|int1|int2|int3|int4|int8|integer|interval|into|is|iterate|join|key|keys|kill|leading|leave|left|like|limit|lines|load|localtime|localtimestamp|lock|long|longblob|longtext|loop|low_priority|match|mediumblob|mediumint||mediumtext|middleint|minute_microsecond|minute_second|mod|modifies|natural|not|no_write_to_binlog|null|numeric|on|optimize|option|optionally|or|order|out|outer|outfile|precision|primary|procedure|purge|read|reads|real|references|regexp|release|rename|repeat|replace|require|restrict|return|revoke|right|rlike|schema|schemas|second_microsecond|select|sensitive|separator|set|show|smallint|soname|spatial|specific|sql|sqlexception|sqlstate|sqlwarning|sql_big_result|sql_calc_found_rows|sql_small_result|ssl|starting|straight_join|table|terminated|then|tinyblob|tinyint|tinytext|to|trailing|trigger|true|undo|union|unique|unlock|unsigned|update|usage|use|using|utc_date|utc_time|utc_timestamp|values|varbinary|varchar|varcharacter|varying|when|where|while|with|write|xor|year_month|zerofill|"
			+ 
			// SQL SERVER
			"external|no|enum|date|bit|action|upgrade|label|connection|public|file|raiserror|fillfactor|any|readtext|reconfigure|freetext|authorization|freetexttable|replication|backup|restore|begin|full|function|break|goto|revert|browse|bulk|rollback|holdlock|rowcount|identity|rowguidcol|identity_insert|rule|checkpoint|identitycol|save|close|clustered|securityaudit|coalesce|semantickeyphrasetable|semanticsimilaritydetailstable|commit|intersect|semanticsimilaritytable|compute|session_user|setuser|containstable|shutdown|some|statistics|system_user|lineno|current|tablesample|merge|textsize|national|nocheck|nonclustered|top|tran|transaction|dbcc|nullif|deallocate|of|truncate|off|try_convert|offsets|tsequal|deny|open|opendatasource|unpivot|disk|openquery|openrowset|updatetext|distributed|openxml|user|dump|view|over|waitfor|errlvl|percent|escape|pivot|except|plan|exec|execute|within|print|writetext|proc|"
			+
			// ODBC
			"absolute|overlaps|pad|ada|partial|pascal|extract|position|allocate|prepare|first|preserve|are|prior|privileges|fortran|assertion|found|at|avg|get|global|relative|go|bit_length|rows|hour|cascaded|scroll|immediate|second|cast|section|catalog|include|session|char_length|indicator|initially|character_length|size|input|space|collation|sqlca|sqlcode|sqlerror|connect|isolation|substring|constraints|sum|language|corresponding|last|temporary|count|time|level|timestamp|timezone_hour|local|timezone_minute|lower|max|min|translate|minute|translation|day|module|trim|month|names|unknown|nchar|deferrable|next|upper|deferred|none|value|descriptor|diagnostics|disconnect|octet_length|domain|only|whenever|work|year|output|zone|exception|"
	).split("[|]");
	
	int EOF = 1;
	int SL_COMMENT	= 2;
	int ML_COMMENT	= 3;
	int STRING_D	= 4;
	int STRING_S	= 5;
	int DEC_DIGIT	= 6;
	int HEX_DIGIT	= 7;
	int DELIMITER	= 8;
	int ESCAPE		= 9;
	int IDENTIFIER  = 10;
	int ALPHA		= 11;
	int MISC		= 12;
			
	int KEYWORD		= 14;
	int OBJECT		= 15;
}
