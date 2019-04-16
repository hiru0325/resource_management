package members;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import info.Auth_Info;
import util.Database_Util;

public class Return_Servlet
{
	public Auth_Info Select_Info(HttpServletRequest request, HttpServletResponse response, HttpSession session)
	{
		Connection connection = null;	//← DB接続情報
		ResultSet rset = null;			//←　SQL実行結果情報
		Auth_Info Auth_Info = null;		//← 照会結果格納

		//↓ DB接続処理
		connection = Database_Util.DB_Connection();
		//↓ セッション検索処理
		rset = Database_Util.Search_Session(connection, session.getId());

		//↓ Auth_Info インスタンス化
		Auth_Info = new Auth_Info();
		//↓　該当セッションのユーザ名を取得
		try {
			rset.last();
			if(rset.getRow() == 1)
			{
				//↓ ユーザ名取得
				rset.first();
				Auth_Info.setLogin_User(rset.getString("USER_NAME"));

				//↓ 照会成功
				Auth_Info.setResult_Content("true");
			}
			else
			{
				//↓ 取得失敗(処理異常終了)
				Auth_Info.setError_Code(7);
				Auth_Info.setResult_Content("error");
			}

			//↓ SQL実行結果破棄
			rset.close();
			//↓ DB切断
			connection.close();
		}
		catch (SQLException e)
		{
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return Auth_Info;
	}
}
