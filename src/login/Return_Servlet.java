package login;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Database_util;

public class Return_Servlet
{
	public String Select_Info(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException
	{
		HttpSession Session = null;		//← セッション情報
		Connection connection = null;	//← DB接続情報
		String User_name = "";			//←　ユーザ名

		//↓ セッション情報取得
		Session = request.getSession();

		//↓Login_auth_util インスタンス化
		Login_auth_util login_util = new Login_auth_util();

		//↓　該当セッションのユーザ名を取得
		connection = Database_util.DB_Connection();

		//↓ セッション検索処理
		User_name = login_util.Search_Session(connection, Session.getId());

		//↓ DB切断
		connection.close();

		return User_name;
	}
}
