package members;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Database_Util;

/**
 * Servlet implementation class sample
 */
@WebServlet("/Session_Out")
public class Session_Out extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Session_Out() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession Session = null;		//← セッション情報格納
		String Auto_flg = "";			//← 自動ログインフラグ
		Connection connection = null;	//← DB接続情報
		int sResult = 0;				//← SQL実行結果

		//↓ 現セッション取得
		Session = request.getSession();

		//↓ 自動ログインフラグ取得
		Auto_flg = (String)Session.getAttribute("Auto_flg");

		//↓ 自動ログインフラグ内容確認
		if(!(Auto_flg != null && Auto_flg.equals("true")))
		{
			//　自動ログインがオフの場合、セッション破棄

			//↓ DB接続
			connection = Database_Util.DB_Connection();

			//↓ ログイントークン削除処理
			sResult = Database_Util.Delete_Session(connection, Session.getId());

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
					if(sResult != 0)
					{
						//SQL実行異常終了

						//DB結果ロールバック
						connection.rollback();

						//↓ ログ表示
						log("ログイントークン「" + Session.getId() + "」によるログアウト処理時、ログイン情報DB削除処理が異常終了しました。SQL実行結果【sResult=" + sResult + "】");
					}
				}
				//↓ DB切断
				connection.close();
			}
			catch(SQLException e)
			{
				//↓ ログ表示
				log("ログイントークン「" + Session.getId() + "」によるログアウト処理時にてSQL実行エラーが発生しました。");
				log("「" + Session.getId() + "」:" + e.getStackTrace());
			}

			//↓ ログ表示
			log("ブラウザを閉じたため、ログイン情報を破棄します。");

			//↓ セッション破棄
			Session.invalidate();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
