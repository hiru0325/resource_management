package members;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import util.Database_util;
import util.Cipher_util;

public class Change_Pass_Servlet
{

	public int Change_Password(HttpSession Session, String Before_pw, String After_pw)
			throws SQLException, ClassNotFoundException
	{

		String User_password = "";			//← ユーザパスワード
		int sResult = 0;					//← SQL文実行結果
		Connection connection = null;		//← DB接続情報格納変数

		Change_Pass_util Change_Pass_util = new Change_Pass_util();

		//↓ データベース接続
		connection = Database_util.DB_Connection();
		//↓ 現パスワードチェック
		User_password = Change_Pass_util.Search_Password(connection, Session.getId());

		if(User_password == null || User_password.isEmpty())
			//↓ ログイン情報取得エラー
			return 7;

		//↓ パスワード確認
		if(Cipher_util.passcheck(Before_pw, User_password))
		{
			//↓ 新パスワード暗号化
			After_pw = Cipher_util.encode(After_pw);
			// パスワード更新
			sResult = Change_Pass_util.Update_Password(connection, Session.getId(), After_pw);

			if(sResult != 1)
			{
				//↓ パスワード更新失敗
				connection.rollback();
				connection.close();
				return 2;
			}
			//↓ パスワード更新成功
			connection.commit();
			connection.close();
			return 0;
		}
		else
		{
			// パスワード不一致
			connection.close();
			return 9;
		}
	}
}
