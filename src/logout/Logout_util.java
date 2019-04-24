package logout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Logout_util
{
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
