package members;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import info.Auth_Info;
import login.Login_auth_util;
import util.Database_Util;
import util.Cipher_Util;

public class Change_Servlet
{

	public Auth_Info Change_Password(HttpSession Session, String Before_pw, String After_pw)
	{
		Auth_Info Auth_Info = null;	//← 認証結果

		Connection connection = null;		//← DB接続情報格納変数
		ResultSet rset = null;				//← SQL実行結果格納変数

		//↓ Database_Utilインスタンス化
		Database_Util Database_Util = new Database_Util();
		//↓ Auth_Info インスタンス化
		Auth_Info = new Auth_Info();

		//↓ データベース接続
		connection = Database_Util.DB_Connection();

		//↓ 現パスワードチェック
		rset = Database_Util.Search_Session(connection, Session.getId());

		try
		{
			//↓ 検索にヒットしたか確認するため、カーソルを最後尾へ移動
			rset.last();
			//↓ 該当ユーザが存在するか確認
			if(rset.getRow() == 0 || rset.getRow() > 1)
			{
				//↓ 7:ログイン情報取得エラー
				Auth_Info.setError_Code(7);

				Auth_Info.setResult_Content("error");
				return Auth_Info;
			}
			else
			{
				//↓ 認証情報を取得するため、カーソルを先頭行へ移動
				rset.first();

				//↓ パスワード確認
				if(Cipher_Util.passcheck(Before_pw, rset.getString("USER_PASSWORD")))
				{
					// パスワード一致

				}
				else
				{
					// パスワード不一致

					Auth_Info.setResult_Content("false");
					return Auth_Info;
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return Auth_Info;
	}
}
