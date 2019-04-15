package members;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import info.Auth_Info;
import util.Cipher_Util;
import util.Database_Util;

/**
 * Servlet implementation class Jdbcsample
 */
@WebServlet("/LoginServlet")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");

		String Auto_flg = "";				//← 自動ログインフラグ
		Auth_Info Auth_Info;				//← 認証情報
		PrintWriter display_out = null; 	//← 画面出力用変数
		HttpSession Session = null;		//← セッション情報

		//↓ セッション情報取得
		Session = request.getSession();

		//↓　自動ログインフラグ取得
		Auto_flg = (String)Session.getAttribute("Auto_flg");

		//↓ 自動ログインフラグの確認
		if(Auto_flg != null && Auto_flg.equals("true"))
			//↓　自動ログイン認証
			Auth_Info = Auto_Login(request, response, Session);
		else
			//↓ 手動ログイン認証
			Auth_Info = Input_Login(request, response, Session);

		//↓ 認証成功
		if(Auth_Info.getResult_Content().equals("true"))
		{
			display_out = response.getWriter();

			Session.setAttribute("Alert_flg", "false");
			Session.setAttribute("Login_User", Auth_Info.getLogin_User());

			//↓↓ ログインユーザ名をリクエストボディへ設定し、メインメニュー画面へ画面遷移
			display_out.println("<html>");
			display_out.println("<head>");
			display_out.println("<body onload=\"document.Login_Accept.submit()\">");
			display_out.println("<form method=\"post\" name=\"Login_Accept\" action=\"./Main_Menu.jsp\" >");
			display_out.println("<input type=\"hidden\" id=\"Login_User\" name=\"Login_User\" value=\"" + Auth_Info.getLogin_User() + "\">");
			display_out.println("</form>");
			display_out.println("</body>");
			display_out.println("</head>");
			display_out.println("</html>");
		}
		else
		{
			//↓ 手動ログイン失敗
			if(Auth_Info.getResult_Content().equals("false"))
			{
				//↓ セッション破棄(セッションハイジャック対策)
				Session.invalidate();

				//↓ 新規セッションを開始
				Session = request.getSession();

				//↓ 認証失敗エラーフラグをAFailedにし、ログイン画面へリダイレクト
				Session.setAttribute("Alert_flg", "Failed");

				response.sendRedirect("./Login_Disp.jsp");
			}
			else
			{
				//↓ 自動ログイン失敗
				if(Auth_Info.getResult_Content().equals("redirect"))
				{
					//↓ 自動ログインをオフにする(一応、変えておく。。。)
					Session.setAttribute("Auto_flg", "false");

					//↓ セッション破棄(セッションハイジャック対策)
					Session.invalidate();

					//↓ 新規セッションを開始
					Session = request.getSession();

					//↓ 認証失敗ではない為、エラーフラグはfalse
					Session.setAttribute("Alert_flg", "false");
					response.sendRedirect("./Login_Disp.jsp");
				}
				else
				{
					//↓ 異常終了
					if(Auth_Info.getResult_Content().equals("error"))
					{
						//↓ エラーコード取得
						request.setAttribute("Error_Code", Auth_Info.getError_Code());

						//↓ 異常終了画面遷移
						RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
						dispatch.forward(request, response);
					}
				}
			}
		}
	}

	//↓ 手動ログイン
	private Auth_Info Input_Login(HttpServletRequest request, HttpServletResponse response, HttpSession Session)
	{
		String User_ID = "";			//← リクエストユーザID
		String User_Password = "";		//← リクエストユーザパスワード
		String Auto_Login_Check = ""; 	//← オートログインチェックボックス値
		String login_user = "";			//← DBで取得したユーザ名
		Auth_Info Auth_Info = null;		//← 認証情報

		//↓ Auth_Infoインスタンス化
		Auth_Info = new Auth_Info();

		//↓ リクエストのユーザIDを格納
		User_ID = request.getParameter("user_id");

		//↓ リクエストのパスワードを格納
		User_Password = request.getParameter("user_pw");

		//※サニタイジングしたいけど、SQL文の条件式に影響があるし、
		//  入力値を画面出力することはない為、サニタイジングはしない方向で。。。

		//↓ オートログインチェックボックス値を格納
		Auto_Login_Check = request.getParameter("Auto_Login_Check");

		try
		{
			Connection connection = null;
			ResultSet rset = null;
			//↓ データベース接続処理
			connection = Database_Util.DB_Connection();

			//↓ ユーザ検索処理
			rset = Database_Util.Search_User(connection, User_ID, User_Password);

			//↓ 該当情報を抽出したか確認するため、カーソルを後尾行へ移動
			rset.last();
			//↓ カーソル行数が0件または2件以上の場合、ログイン失敗画面へ画面遷移
			if(rset.getRow() != 1)
			{
				//↓ ログ出力
				log("ユーザID「" + User_ID + "」さんが該当情報なしにより手動ログイン処理に失敗しました。");

				//↓ 認証失敗
				Auth_Info.setResult_Content("false");
				return Auth_Info;
			}
			else
			{
				//↓ 認証成功時、カーソルを先頭行へ戻す。
				rset.first();

				//↓ パスワードの比較
				if(Cipher_Util.passcheck(User_Password, rset.getString("USER_PASSWORD")))
				{
					//↓ オートログインのチェックが入っている場合、ログイン情報を保持
					if(Auto_Login_Check != null && Auto_Login_Check.equals("true"))
					{
						String User_Token = "";
						Date Current_Time = null;
						Date Expiration_Date = null;
						int sResult = 0;					//← SQL実行結果

						final String datePattern = "yyyy/MM/dd HH:mm:ss";

						//↓ ユーザIDを基にログイントークンを作成
						User_Token = Cipher_Util.encode(login_user);

						//↓ 現在の日時の取得
						Current_Time = new Date();
						//↓ 加算するためカレンダークラスへ変換
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(Current_Time);
						//↓ 有効期限発行(2ヶ月)
						calendar.add(Calendar.MONTH, 2);
						//↓　Calendar型の日時をDate型へ戻す
						Expiration_Date = calendar.getTime();

						//↓ ログイントークンをDBへ登録
						sResult = Database_Util.Register_Token(connection, Session.getId(), User_ID, User_Token, new SimpleDateFormat(datePattern).format(Expiration_Date));

						//↓ DB登録処理結果の確認
						if(sResult == 1)
						{
							//DB処理正常終了

							//↓ DB処理結果反映
							connection.commit();

							//↓ ログイントークンをクッキーへ登録
							Cookie cookie = new Cookie("User_Token", User_Token);
							response.addCookie(cookie);

							//↓ セッションで自動ログインフラグを保持
							Session.setAttribute("Auto_flg", "true");
						}
						else
						{
							//DB処理異常終了

							//↓ ログ出力
							log("ユーザID「" + User_ID + "」さんがログイントークン登録処理に失敗しました。SQL出力結果【sResult=「" + sResult + "」】");

							//↓ DB処理結果ロールバック
							connection.rollback();

							//↓ エラーコード付与(1:登録処理中のロールバック)
							Auth_Info.setError_Code(1);

							//↓ 異常終了画面遷移
							Auth_Info.setResult_Content("error");
							return Auth_Info;
						}
					}
					//↓ ユーザ名取得
					login_user = rset.getString("USER_NAME");
					Auth_Info.setLogin_User(login_user);

					//↓ ログ出力
					log("ユーザID「" + User_ID + "」さんが手動ログイン処理に成功しました。");

					//↓ 認証成功
					Auth_Info.setResult_Content("true");
					return Auth_Info;
				}
				else
				{
					//↓ ログ出力
					log("ユーザID「" + User_ID + "」さんがパスワード不一致により手動ログイン処理に失敗しました。");

					//↓ 認証失敗
					Auth_Info.setResult_Content("false");
					return Auth_Info;
				}
			}

		} catch (SQLException e) {
			// ↓ログ出力
			log("ユーザID「" + User_ID + "」さんの手動ログイン処理時、SQL実行エラーが発生しました。");
			log("ユーザID「" + User_ID + "」:" + e.getStackTrace());
		}

		//↓ ログ出力
		log("「" + User_ID + "」さんの手動ログイン処理時に最下行に到達しました。クライアントには異常終了画面を表示します。");

		//↓ エラーコード付与(2:手動ログイン処理時の想定外エラー)
		Auth_Info.setError_Code(2);

		//↓ 最後まできてもバグなのでリダイレクト
		Auth_Info.setResult_Content("error");
		return Auth_Info;
	}

	//↓ 自動ログイン
	private Auth_Info Auto_Login(HttpServletRequest request, HttpServletResponse response, HttpSession Session)
	{
		Cookie cookie[];					//← クッキー情報格納変数
		String Login_User = "";			//← 抽出したユーザ名
		String User_Token = "";			//← ログイントークン
		Date Expiration_Date = null;		//← ログイントークン有効期限
		Auth_Info Auth_Info = null;		//← 認証情報

		//↓ Auth_Infoインスタンス化
		Auth_Info = new Auth_Info();

		//↓ クライアント側のログイントークンを取得
		cookie = request.getCookies();

		//↓ クッキー情報が取得できているか確認
		if(cookie != null)
		{
			Boolean Acquire_flg = false;	//← ログイントークン取得フラグ

			for(Cookie data : cookie)
			{
				//↓ ログイントークン取得フラグをたてる
				Acquire_flg = true;
				//↓　ログイントークンを抽出し、取得
				if(data.getName().equals("User_Token"))
				{
					User_Token = data.getValue();
					break;
				}
			}
			//↓ ログイントークンを取得できなければ認証失敗
			if(!Acquire_flg)
			{
				//↓ ログ出力
				log("クッキー情報からログイントークンを取得できませんでした。");

		        //↓ ログイン画面へリダイレクト
				Auth_Info.setResult_Content("redirect");
				return Auth_Info;
			}
		}

		Connection connection = null;	//← DB接続
		ResultSet rset = null;			//← SQL実行結果

		//↓ データベース接続処理
		connection = Database_Util.DB_Connection();

		//↓ ログイントークン検索処理
		rset = Database_Util.Search_Token(connection, User_Token);

		try {

			//↓ 抽出件数を確認するため、カーソルを最下行へ移動
			rset.last();
			//↓ 検索ヒット数があれば認証成功、なければ認証失敗
			if(rset.getRow() != 1)
			{
				//↓ ログ出力
				log("ログイントークン「" + User_Token + "」がDBに存在しませんでした。");

				//↓ クライアント側のログイントークンを削除
				Cookie Delete_Token = new Cookie("User_Token", "");
				Delete_Token.setMaxAge(0);
				response.addCookie(Delete_Token);

				//↓ 認証失敗
				Auth_Info.setResult_Content("redirect");
				return Auth_Info;
			}
			else
			{
				//↓ SQL実行結果のカーソルを先頭行へ移動
				rset.first();
				//↓ ユーザ名取得
				Login_User = rset.getString("USER_NAME");
				//↓ 有効期限取得
				Expiration_Date = rset.getDate("EXPIRATION_DATE");

				//↓ 現在時刻を取得
				Date Current_Time = new Date();
				//↓ 有効期限が現在日付を経過していないか確認
				if(Expiration_Date.before(Current_Time))
				{
					int sResult = 0;	//← SQL実行結果

					//↓ クライアント側のログイントークンを削除
					Cookie Delete_Token = new Cookie("User_Token", "");
					Delete_Token.setMaxAge(0);
					response.addCookie(Delete_Token);

					//↓ DBから有効期限切れログイントークンを削除
					sResult = Database_Util.Delete_Token(connection, User_Token);

					//↓DB削除処理結果確認
					if(sResult == 1)
					{
						//DB削除処理正常終了

						//↓ DB処理結果反映
						connection.commit();

						//↓ ログ出力
						log("ログイントークン「" + User_Token + "」の有効期限切れログイントークン削除処理に成功しました。");

						//↓ リダイレクト
						Auth_Info.setResult_Content("redirect");
						return Auth_Info;
					}
					else
					{
						//削除処理異常終了

						//↓ ログ出力
						log("ログイントークン「" + User_Token + "」の有効期限切れログイントークン削除処理に失敗しました。SQL実行結果【sResult=" + sResult + "】");

						//↓ DB処理結果ロールバック
						connection.rollback();

						//↓ エラーコード付与(3:有効期限切れログイントークン削除処理中のロールバック)
						Auth_Info.setError_Code(3);

						//↓ 異常処理結果画面遷移
						Auth_Info.setResult_Content("error");
						return Auth_Info;

					}
				}
				//↓ ユーザ名取得
				Auth_Info.setLogin_User(Login_User);

				//↓ ログ出力
				log("ログイントークン「" + User_Token + "」による自動ログイン処理に成功しました。");

				//↓ 認証成功
				Auth_Info.setResult_Content("true");
				return Auth_Info;
			}
		} catch (SQLException e) {
			log("ログイントークン「" + User_Token + "」の自動ログイン処理時、SQL実行エラーが発生しました。");
			log("ログイントークン「" + User_Token + "」:" + e.getStackTrace());
		}

		//↓ ログ出力
		log("ログイントークン「" + User_Token + "」の自動ログイン処理時、最下行まで到達しました。クライアントには異常処理画面を表示します。");

		//↓ エラーコード付与(4:自動ログイン処理中の想定外エラー)
		Auth_Info.setError_Code(4);

		//↓ 最後まできてもバグなのでエラー
		Auth_Info.setResult_Content("error");
		return Auth_Info;
	}
}
