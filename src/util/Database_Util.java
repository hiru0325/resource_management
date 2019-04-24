package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database_Util {

	private static final String DB_Driver = "com.mysql.cj.jdbc.Driver";								//← 接続用ドライバー
	private static final String DB_Url = "jdbc:mysql://localhost:3306/RESOURCE_MANAGEMENT?serverTimezone=JST";			//← 接続先URL
	private static final String DB_User = "matsumotohru";											//← 接続用ユーザ名
	private static final String DB_Password = "matsumotohru";										//← 接続用パスワード

	//↓ データベース接続処理
	public Connection DB_Connection()
	{
		Connection connection = null;		//← 接続情報格納変数
		try {
			//↓ ドライバ宣言
			Class.forName(DB_Driver);
			//↓ データベース接続
			connection = DriverManager.getConnection(DB_Url, DB_User, DB_Password);

			//↓ 自動コミットをオフに設定
			if(connection.getAutoCommit() == true)
				connection.setAutoCommit(false);

		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		//↓ 接続情報を返却
		return connection;
	}

	//↓ セッションID検索処理
	public static ResultSet Search_Session(Connection connection, String Session_ID)
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		ResultSet rset = null;				//← SQL実行結果変数
		//↓ SQL文
		final String sQuery = "select "
							+ "USER_NAME, "
							+ "USER_PASSWORD, "
							+ "EXPIRATION_DATE "
							+ "from "
							+ "AUTO_MANAGEMENT_TBL "
							+ "inner join USER_MANAGEMENT_TBL on (AUTO_MANAGEMENT_TBL.USER_ID = USER_MANAGEMENT_TBL.USER_ID) "
							+ "where "
							+ "Session_ID = ?";

		try {
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ ログイントークン格納
			pStmt.setString(1, Session_ID);
			rset = pStmt.executeQuery();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		pStmt = null;

		return rset;
	}
}