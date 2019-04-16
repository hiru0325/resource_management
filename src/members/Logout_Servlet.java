package members;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Database_Util;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/LogoutServlet")
public class Logout_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout_Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String Auto_flg = "";				//← 自動ログインフラグ
		Connection connection = null;		//← DB接続情報
		ResultSet rset = null;
		int sResult = 0;					//← SQL実行結果
		HttpSession Session = null;			//← セッション情報

		//↓ セッション取得
		Session = request.getSession();

		//↓ 自動ログインフラグ取得
		Auto_flg = (String)Session.getAttribute("Auto_flg");

		Cookie cookie[] = request.getCookies();

		if(cookie != null)
		{
			for(Cookie Token_Cookie : cookie)
			{
				if(Token_Cookie.getName().equals("User_Token"))
				{
					//↓ 自動ログインがオンの場合、意図的にログイントークンを削除
					if(Auto_flg != null && Auto_flg.equals("true"))
					{
						//↓ 自動ログインをオフにする(後続でセッションは更新するが一応、変えておく。。。)
						Session.setAttribute("Auto_flg", "false");

						//↓ クライアント側のログイントークン削除
						Cookie Delete_Token = new Cookie("User_Token", "");
						Delete_Token.setMaxAge(0);
						response.addCookie(Delete_Token);
					}

					break;
				}
			}
		}

		//↓ DB接続
		connection = Database_Util.DB_Connection();

		//↓ セッションID検索処理
		rset = Database_Util.Search_Session(connection, Session.getId());

		try
		{
			rset.last();
			if(rset.getRow() >= 1)
			{
				// 既存のセッションが登録されている場合、ログイン情報を削除

				//↓ セッションID削除処理
				sResult = Database_Util.Delete_Session(connection, Session.getId());

				//↓ SQL実行結果確認
				if(sResult == 1)
				{
					//SQL実行正常終了

					//↓ DB結果反映
					connection.commit();
				}
				else
				{
					//SQL実行異常終了

					//DB結果ロールバック
					connection.rollback();

					//↓ ログ表示
					log("「" + Session.getId() + "」によるログアウト処理時、ログイン情報DB削除処理が異常終了しました。SQL実行結果【sResult=" + sResult + "】");

					//↓ エラーコード設定(5:ログアウト処理時のログイントークン削除DBエラー)
					request.setAttribute("Error_Code", 5);

					//↓ 異常終了画面遷移
					RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
					dispatch.forward(request, response);
				}
			}

			//↓ DB切断
			connection.close();
		}
		catch(SQLException e)
		{
			//↓ ログ表示
			log("「" + Session.getId() + "」によるログアウト処理時にてSQL実行エラーが発生しました。");
			log("「" + Session.getId() + "」:" + e.getStackTrace());

			//↓ エラーコード設定(6:ログアウト処理想定外エラー)
			request.setAttribute("Error_Code", 6);

			//↓ 異常終了画面遷移
			RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			dispatch.forward(request, response);
		}

		//↓ ログ表示
		log("「" + Session.getId() + "」によるログアウト処理が正常終了しました。");

		//↓ セッション破棄(セッションハイジャック対策)
		Session.invalidate();

		//↓ 新規セッションを開始
		Session = request.getSession();

		//アラートフラグ
		Session.setAttribute("Alert_flg", "Logout");

		//↓ ログイン画面へリダイレクト
		response.sendRedirect("../jsp/Login_Disp.jsp");
	}
}
