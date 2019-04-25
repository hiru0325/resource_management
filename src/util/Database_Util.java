package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database_util {

	private static final String DB_Driver = "com.mysql.cj.jdbc.Driver";								//← 接続用ドライバー
	private static final String DB_Url = "jdbc:mysql://localhost:3306/RESOURCE_MANAGEMENT?serverTimezone=JST";			//← 接続先URL
	private static final String DB_User = "matsumotohru";											//← 接続用ユーザ名
	private static final String DB_Password = "matsumotohru";										//← 接続用パスワード

	//↓ データベース接続処理
	public static Connection DB_Connection() throws ClassNotFoundException, SQLException
	{
		Connection connection = null;		//← 接続情報格納変数

		//↓ ドライバ宣言
		Class.forName(DB_Driver);
		//↓ データベース接続
		connection = DriverManager.getConnection(DB_Url, DB_User, DB_Password);

		//↓ 自動コミットをオフに設定
		if(connection.getAutoCommit() == true)
			connection.setAutoCommit(false);

		//↓ 接続情報を返却
		return connection;
	}
}