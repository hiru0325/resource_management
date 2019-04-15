package members;

import java.io.IOException;
import java.sql.Connection;
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
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String Auto_flg = "";				//← 自動ログインフラグ
		String User_Token = "";			//← ログイントークン
		Connection connection = null;		//← DB接続情報
		int sResult = 0;					//← SQL実行結果
		HttpSession Session = null;		//← セッション情報

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
					//↓ クライアント側のログイントークン取得
					User_Token = Token_Cookie.getValue();

					//↓ クライアント側のログイントークン削除
					Cookie Delete_Token = new Cookie("User_Token", "");
					Delete_Token.setMaxAge(0);
					response.addCookie(Delete_Token);

					break;
				}
			}

			//↓ 自動ログインが設定されているか確認
			if(Auto_flg != null && Auto_flg.equals("true"))
			{

				//自動ログイン設定がオンの場合

				//↓ 自動ログインをオフにする(後続でセッションは更新するが一応、変えておく。。。)
				Session.setAttribute("Auto_flg", "false");

				//↓ DB接続
				connection = Database_Util.DB_Connection();

				//↓ ログイントークン削除処理
				sResult = Database_Util.Delete_Token(connection, User_Token);

				try
				{
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
						log("ログイントークン「" + User_Token + "」によるログアウト処理時、ログイン情報DB削除処理が異常終了しました。SQL実行結果【sResult=" + sResult + "】");

						//↓ エラーコード設定(5:ログアウト処理時のログイントークン削除DBエラー)
						request.setAttribute("Error_Code", 5);

						//↓ 異常終了画面遷移
						RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
						dispatch.forward(request, response);
					}
				}
				catch(SQLException e)
				{
					//↓ ログ表示
					log("ログイントークン「" + User_Token + "」によるログアウト処理時にてSQL実行エラーが発生しました。");
					log("「" + User_Token + "」:" + e.getStackTrace());

					//↓ エラーコード設定(6:ログアウト処理想定外エラー)
					request.setAttribute("Error_Code", 6);

					//↓ 異常終了画面遷移
					RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
					dispatch.forward(request, response);
				}
			}
		}

		//↓ セッション破棄(セッションハイジャック対策)
		Session.invalidate();

		//↓ 新規セッションを開始
		Session = request.getSession();

		//↓ ログ表示
		log("ログイントークン「" + User_Token + "」によるログアウト処理が正常終了しました。");

		//アラートフラグ
		Session.setAttribute("Alert_flg", "Logout");

		//↓ ログイン画面へリダイレクト
		response.sendRedirect("../jsp/Login_Disp.jsp");
	}


}
