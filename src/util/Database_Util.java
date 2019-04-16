package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database_Util {

	private static final String DB_Driver = "com.mysql.cj.jdbc.Driver";								//← 接続用ドライバー
	private static final String DB_Url = "jdbc:mysql://localhost:3306/RESOURCE_MANAGEMENT?serverTimezone=JST";			//← 接続先URL
	private static final String DB_User = "resource_manamagent_user";											//← 接続用ユーザ名
	private static final String DB_Password = "MysqlRMU20190413";										//← 接続用パスワード

	//↓ データベース接続処理
	public static  Connection DB_Connection()
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

	//↓ ユーザ索引処理
	public static ResultSet Search_User(Connection connection, String User_ID, String User_Password)
	{
		final String sQuery = "SELECT * FROM USER_MANAGEMENT_TBL where USER_ID = ?";	//← ユーザ抽出用SQL文
		PreparedStatement pStmt = null;													//← SQL実行変数
		ResultSet rset = null;															//← SQL実行結果格納変数

		try
		{
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ パラメータ代入
			pStmt.setString(1, User_ID);
			//↓ SQL文実行
			rset = pStmt.executeQuery();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		pStmt = null;

		return rset;
	}
	//↓　ログイントークン登録処理
	public static int Register_Session(Connection connection,String Session_ID, String User_ID, String User_Token, String Expiration_Date)
	{
		//↓ ログイントークン登録SQL文
		final String sQuery = "insert into "
							+ "AUTO_MANAGEMENT_TBL "
							+ "("
							+ "SESSION_ID, "
							+ "USER_ID, "
							+ "USER_TOKEN, "
							+ "EXPIRATION_DATE "
							+ ") "
							+ "values "
							+ "( "
							+ "?, "
							+ "?, "
							+ "?, "
							+ "? "
							+ ");";
		PreparedStatement pStmt = null;													//← SQL実行変数
		int sResult = 0;																	//← SQL実行結果格納変数

		try
		{
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ パラメータ代入
			pStmt.setString(1, Session_ID);			//← セッションID
			pStmt.setString(2, User_ID);			//← ユーザID
			pStmt.setString(3, User_Token);			//← ログイントークン
			pStmt.setString(4, Expiration_Date);	//← 有効期限
			//↓ SQL文実行
			sResult = pStmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		pStmt = null;

		return sResult;
	}

	//↓　セッションID登録処理
	public static int Register_Session(Connection connection,String Session_ID, String User_ID)
	{
		//↓ SQL文
		final String sQuery = "insert into "
							+ "AUTO_MANAGEMENT_TBL "
							+ "("
							+ "SESSION_ID, "
							+ "USER_ID "
							+ ") "
							+ "values "
							+ "( "
							+ "?, "
							+ "? "
							+ ");";
		PreparedStatement pStmt = null;													//← SQL実行変数
		int sResult = 0;																	//← SQL実行結果格納変数

		try
		{
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ パラメータ代入
			pStmt.setString(1, Session_ID);			//← セッションID
			pStmt.setString(2, User_ID);			//← ユーザID

			//↓ SQL文実行
			sResult = pStmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		pStmt = null;

		return sResult;
	}

	//↓ セッションID検索処理
	public static ResultSet Search_Session(Connection connection, String Session_ID)
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		ResultSet rset = null;				//← SQL実行結果変数
		//↓ SQL文
		final String sQuery = "select "
							+ "USER_NAME, "
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

	//↓ ログイントークン検索処理
	public static ResultSet Search_Token(Connection connection, String User_Token)
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		ResultSet rset = null;				//← SQL実行結果変数
		//↓ SQL文
		final String sQuery = "select "
							+ "USER_NAME, "
							+ "EXPIRATION_DATE "
							+ "from "
							+ "AUTO_MANAGEMENT_TBL "
							+ "inner join USER_MANAGEMENT_TBL on (AUTO_MANAGEMENT_TBL.USER_ID = USER_MANAGEMENT_TBL.USER_ID) "
							+ "where "
							+ "USER_TOKEN = ?";

		try {
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ ログイントークン格納
			pStmt.setString(1, User_Token);
			rset = pStmt.executeQuery();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		pStmt = null;

		return rset;
	}

	//↓ セッション削除処理
	public static int Delete_Session(Connection connection, String Session_ID)
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		int sResult = 0;						//← SQL実行結果格納変数
		//↓ SQL文
		final String sQuery = "delete from "
								+ "AUTO_MANAGEMENT_TBL "
								+ "where "
								+ "SESSION_ID = ?";

		try {
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ ログイントークン格納
			pStmt.setString(1, Session_ID);
			sResult = pStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		pStmt = null;

		return sResult;
	}

	//↓ ログイントークン削除処理
	public static int Delete_Token(Connection connection, String User_Token)
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		int sResult = 0;						//← SQL実行結果格納変数
		//↓ SQL文
		final String sQuery = "delete from "
								+ "AUTO_MANAGEMENT_TBL "
								+ "where "
								+ "USER_TOKEN = ?";

		try {
			//↓ SQL文セット(コンパイル)
			pStmt = connection.prepareStatement(sQuery);
			//↓ ログイントークン格納
			pStmt.setString(1, User_Token);
			sResult = pStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		pStmt = null;

		return sResult;
	}
}
