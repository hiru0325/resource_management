package login;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Cipher_Util;
import util.Database_Util;

public class Login_Servlet
{
	public int Authentication(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html; charset=UTF-8");

		String Auto_flg = "";					//← 自動ログインフラグ
		HttpSession Session = null;				//← ログイン情報
		int Return_Code = 0;

		//↓ セッション情報取得
		Session = request.getSession();

		//↓　自動ログインフラグ取得
		Auto_flg = (String)Session.getAttribute("Auto_flg");

		try
		{
			//↓ 自動ログインフラグの確認
			if(Auto_flg != null && Auto_flg.equals("true"))
			{
				//↓ クライアント側のログイントークンを取得
				Cookie cookie[] = request.getCookies();
				//↓　自動ログイン認証
				Return_Code = Auto_Login(cookie, response);
			}
			else
			{
				String User_ID = request.getParameter("user_id");						//← 取得ユーザID
				String User_Password = request.getParameter("user_pw");					//← リクエストユーザパスワード
				String Auto_Login_Check = request.getParameter("Auto_Login_Check"); 	//← 自動ログインチェックボックス値
				//↓ 手動ログイン認証
				Return_Code = Input_Login(User_ID, User_Password, Auto_Login_Check, Session, response);
			}
		}
		catch (SQLException e)
		{
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("Return_Code:" + Return_Code);

		return Return_Code;
	}

	//↓ 手動ログイン
	private int Input_Login(String User_ID, String User_Password, String Auto_Login_Check, HttpSession Session, HttpServletResponse response)
			throws SQLException, ClassNotFoundException
	{
		String login_user = "";			//← DBで取得したユーザ名
		String Login_Password = "";		//← DBより取得したパスワード
		Connection connection = null;	//← DB接続情報

		//↓ login_utilインスタンス化
		Login_auth_util login_util = new Login_auth_util();

		//↓ データベース接続処理
		connection = login_util.Connection();

		//↓ ユーザ検索処理
		Login_Password = login_util.Search_User(connection, User_ID);

		//↓ Login_Passwordの値確認
		if(Login_Password == null || Login_Password.isEmpty())
			//↓ 該当情報なしで返却
			return 9;

		//↓ パスワードの比較
		if(Cipher_Util.passcheck(User_Password, Login_Password))
		{
			//↓ ログイン情報を保持(自動ログイン関係なく、ログイントークンを利用して情報管理していく)
			String User_Token = "";
			Date Current_Time = null;
			Date Expiration_Date = null;
			int sResult = 0;					//← SQL登録実行結果

			//↓ 既存のセッションが登録されている場合、ログイン情報を削除(戻しボタン対策)
			sResult = Database_Util.Delete_Session(connection, Session.getId());
			//↓　存在した場合、コミット
			if(sResult >= 1)
				connection.commit();

			//↓ 自動ログインチェックフラグがtrueの場合、セッションに自動ログインフラグを保持
			if(Auto_Login_Check != null && Auto_Login_Check.equals("true"))
			{
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

				//↓ セッションID、ログイントークンをDBへ登録
				sResult = login_util.Register_Session(connection,
													  Session.getId(),
													  User_ID,
													  User_Token,
													  new SimpleDateFormat(datePattern).format(Expiration_Date));
			}
			else
			{
				//↓ セッションIDのみDBへ登録
				sResult = login_util.Register_Session(connection, Session.getId(), User_ID);
			}

			//↓ DB登録処理結果の確認
			if(sResult == 1)
			{
				//DB処理正常終了

				//↓ DB処理結果反映
				connection.commit();
				//↓ 自動ログインチェックフラグがtrueの場合、セッションに自動ログインフラグを保持
				if(Auto_Login_Check != null && Auto_Login_Check.equals("true"))
				{
					//↓ ログイントークンをクッキーへ登録
					Cookie cookie = new Cookie("User_Token", User_Token);
					//↓ トークンにSecure属性付与(HTTPS通信切り替え時にコメント解除)
					//cookie.setSecure(true);
					//↓ javascriptからの取得を抑止
					cookie.setHttpOnly(true);
					response.addCookie(cookie);

					//↓ セッション有効期限はDBで管理するため、無制限に設定。
					Session.setMaxInactiveInterval(-1);

					//↓ 自動ログインフラグをセッションへ設定
					Session.setAttribute("Auto_flg", "true");
				}
			}
			else
			{
				//DB処理異常終了

				//↓ DB処理結果ロールバック
				connection.rollback();
				//↓ DB切断
				connection.close();

				//↓ 異常終了画面遷移
				return 1;
			}
			//↓ DB切断
			connection.close();
			//↓ 認証成功
			return 0;
		}
		else
		{
			//↓ DB切断
			connection.close();
			//↓ 認証失敗
			return 9;
		}
	}

	//↓ 自動ログイン
	private int Auto_Login(Cookie cookie[], HttpServletResponse response)
			throws SQLException, ClassNotFoundException
	{
		String User_Token = "";			//← ログイントークン
		Date Expiration_Date = null;	//← ログイントークン有効期限

		Login_auth_util login_util = new Login_auth_util();

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
		        //↓ ログイン画面へリダイレクト
				return 8;
			}
		}

		Connection connection = null;	//← DB接続

		//↓ データベース接続処理
		connection = login_util.Connection();

		//↓ ログイントークン検索処理
		Expiration_Date = login_util.Search_Token(connection, User_Token);

		//↓ 検索ヒット数があれば認証成功、なければ認証失敗
		if(Expiration_Date == null)
		{
			//↓ クライアント側のログイントークンを削除
			Cookie Delete_Token = new Cookie("User_Token", "");
			Delete_Token.setMaxAge(0);
			response.addCookie(Delete_Token);

			//↓ DB切断
			connection.close();
			//↓ 認証失敗(リダイレクト)
			return 8;
		}

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
			if(sResult >= 1)
			{
				//DB削除処理正常終了

				//↓ DB処理結果反映
				connection.commit();
				//↓ DB切断
				connection.close();
				//↓ 認証失敗(リダイレクト)
				return 8;
			}
		}
		//↓ DB切断
		connection.close();
		//↓ 認証成功
		return 0;
	}
}
