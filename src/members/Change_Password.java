package members;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Database_Util;

/**
 * Servlet implementation class Change_Password
 */
@WebServlet("/Change_Password")
public class Change_Password extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Change_Password() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//↓ 文字コード設定
		response.setContentType("text/html; charset=UTF-8");

		String User_ID = "";		//← ユーザID
		String Before_pw = "";		//← 現パスワード
		String After_pw = "";		//← 新パスワード

		Connection connection = null;		//← DB接続情報格納変数
		ResultSet rset = null;				//← SQL実行結果格納変数

		//↓ ユーザIDをリクエストボディ部から受信
		User_ID = request.getParameter("User_ID");
		//↓ 現パスワードをリクエストボディ部から受信
		Before_pw = request.getParameter("Before_pw");
		//↓ 新パスワードをリクエストボディ部から受信
		After_pw = request.getParameter("After_pw");

		//↓ データベース接続
		connection = Database_Util.DB_Connection();

		//↓ 現パスワードチェック


		try
		{
			//↓ 検索にヒットしたか確認するため、カーソルを最後尾へ移動
			rset.last();
			//↓ 該当ユーザが存在するか確認
			if(rset.getRow() == 0 || rset.getRow() > 1)
			{
				//↓ 認証エラー
			}
			else
			{
				//↓ 認証情報を取得するため、カーソルを先頭行へ移動
				rset.first();

			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}





	}

}
