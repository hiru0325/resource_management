package members;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Change_Pass_util
{
	//↓ パスワード検索処理
	public String Search_Password(Connection connection, String Session_ID) throws SQLException
	{
		String User_password = "";			//←　ユーザパスワード
		PreparedStatement pStmt = null;		//← SQL実行変数
		ResultSet rset = null;				//← SQL実行結果変数
		//↓ SQL文
		final String sQuery = "select "
							+ "USER_PASSWORD "
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

		//↓ 検索にヒットしたか確認するため、カーソルを最後尾へ移動
		rset.last();

		if(rset.getRow() > 1)
		{
			//↓ DBエラー
			throw new SQLException();
		}
		rset.first();
		User_password = rset.getString("USER_PASSWORD");

		pStmt = null;
		rset.close();

		return User_password;
	}

	//↓　パスワード更新処理
	public int Update_Password(Connection connection,String Session_ID, String After_pw) throws SQLException
	{
		//↓ SQL文
		final String sQuery = "update "
							+ "AUTO_MANAGEMENT_TBL "
							+ "inner join USER_MANAGEMENT_TBL "
							+ "on "
							+ "(AUTO_MANAGEMENT_TBL.USER_ID = USER_MANAGEMENT_TBL.USER_ID) "
							+ "set "
							+ "USER_PASSWORD = ? "
							+ "where "
							+ "SESSION_ID = ?";

		PreparedStatement pStmt = null;													//← SQL実行変数
		int sResult = 0;																	//← SQL実行結果格納変数

		//↓ SQL文セット(コンパイル)
		pStmt = connection.prepareStatement(sQuery);
		//↓ パラメータ代入
		pStmt.setString(1, After_pw);			//← 新パスワード
		pStmt.setString(2, Session_ID);			//← セッションID

		//↓ SQL文実行
		sResult = pStmt.executeUpdate();

		pStmt = null;
		return sResult;
	}
}
