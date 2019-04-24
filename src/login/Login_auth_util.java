package login;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login_auth_util
{
	//↓ ユーザ索引処理
	public String Search_User(Connection connection, String User_ID) throws SQLException
	{
		final String sQuery = "SELECT USER_PASSWORD FROM USER_MANAGEMENT_TBL where USER_ID = ?";	//← ユーザ抽出用SQL文
		PreparedStatement pStmt = null;													//← SQL実行変数
		ResultSet rset = null;															//← SQL実行結果格納変数

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ パラメータ代入
		pStmt.setString(1, User_ID);
		//↓ SQL文実行
		rset = pStmt.executeQuery();

		rset.last();
		if(rset.getRow() == 0)
		{
			//↓ 該当情報が見つからない場合、nullで返す。
			return null;
		}
		else
		{
			if(rset.getRow() > 1)
			{
				//↓ 該当情報が2件以上存在する場合、DBエラー
				new SQLException();
			}
		}

		pStmt = null;
		rset.first();
		return rset.getString("USER_PASSWORD");
	}

	//↓ ログイントークン削除処理
	public int Delete_Token(Connection connection, String User_Token) throws SQLException
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		int sResult = 0;						//← SQL実行結果格納変数
		//↓ SQL文
		final String sQuery = "delete from "
								+ "AUTO_MANAGEMENT_TBL "
								+ "where "
								+ "USER_TOKEN = ?";

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ ログイントークン格納
		pStmt.setString(1, User_Token);
		sResult = pStmt.executeUpdate();

		pStmt = null;
		return sResult;
	}

	//↓　ログイントークン登録処理
	public int Register_Session(Connection connection,String Session_ID, String User_ID, String User_Token, String Expiration_Date) throws SQLException
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

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ パラメータ代入
		pStmt.setString(1, Session_ID);			//← セッションID
		pStmt.setString(2, User_ID);			//← ユーザID
		pStmt.setString(3, User_Token);			//← ログイントークン
		pStmt.setString(4, Expiration_Date);	//← 有効期限
		//↓ SQL文実行
		sResult = pStmt.executeUpdate();

		pStmt = null;
		return sResult;
	}

	//↓　セッションID登録処理
	public int Register_Session(Connection connection,String Session_ID, String User_ID) throws SQLException
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

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ パラメータ代入
		pStmt.setString(1, Session_ID);			//← セッションID
		pStmt.setString(2, User_ID);			//← ユーザID

		//↓ SQL文実行
		sResult = pStmt.executeUpdate();

		pStmt = null;
		return sResult;
	}

	//↓ 有効期限検索処理
	public Date Search_Token(Connection connection, String User_Token) throws SQLException
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		ResultSet rset = null;				//← SQL実行結果変数
		//↓ SQL文
		final String sQuery = "select "
							+ "EXPIRATION_DATE "
							+ "from "
							+ "AUTO_MANAGEMENT_TBL "
							+ "inner join USER_MANAGEMENT_TBL on (AUTO_MANAGEMENT_TBL.USER_ID = USER_MANAGEMENT_TBL.USER_ID) "
							+ "where "
							+ "USER_TOKEN = ?";

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ ログイントークン格納
		pStmt.setString(1, User_Token);
		rset = pStmt.executeQuery();

		rset.last();
		if(rset.getRow() == 0)
		{
			//↓ 該当情報が見つからない場合、nullで返す。
			return null;
		}
		else
		{
			if(rset.getRow() > 1)
			{
				//↓ 該当情報が2件以上存在する場合、DBエラー
				new SQLException();
			}
		}

		pStmt = null;
		rset.first();
		return rset.getDate("EXPIRATION_DATE");
	}

	//↓ セッションID検索処理
	public String Search_Session(Connection connection, String Session_ID) throws SQLException
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		ResultSet rset = null;				//← SQL実行結果変数
		//↓ SQL文
		final String sQuery = "select "
							+ "USER_NAME "
							+ "from "
							+ "AUTO_MANAGEMENT_TBL "
							+ "inner join USER_MANAGEMENT_TBL on (AUTO_MANAGEMENT_TBL.USER_ID = USER_MANAGEMENT_TBL.USER_ID) "
							+ "where "
							+ "Session_ID = ?";

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ ログイントークン格納
		pStmt.setString(1, Session_ID);
		rset = pStmt.executeQuery();

		rset.last();
		if(rset.getRow() > 1)
		{
			//↓ 取得失敗(処理異常終了)
			new SQLException();
		}

		//↓ ユーザ名取得
		rset.first();
		String User_name = rset.getString("USER_NAME");

		pStmt = null;
		rset.close();

		return User_name;
	}

	//↓ セッション削除処理
	public int Delete_Session(Connection connection, String Session_ID) throws SQLException
	{
		PreparedStatement pStmt = null;		//← SQL実行変数
		int sResult = 0;						//← SQL実行結果格納変数
		//↓ SQL文
		final String sQuery = "delete from "
								+ "AUTO_MANAGEMENT_TBL "
								+ "where "
								+ "SESSION_ID = ?";

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ ログイントークン格納
		pStmt.setString(1, Session_ID);
		sResult = pStmt.executeUpdate();

		pStmt = null;
		return sResult;
	}
}
