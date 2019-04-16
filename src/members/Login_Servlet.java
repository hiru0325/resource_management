package members;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import info.Auth_Info;
import util.Cipher_Util;
import util.Database_Util;

public class Login_Servlet
{
	public Auth_Info Authentication(HttpServletRequest request, HttpServletResponse response, HttpSession Session)
	{
		response.setContentType("text/html; charset=UTF-8");

		String Auto_flg = "";				//← 自動ログインフラグ
		Auth_Info Auth_Info;				//← 認証情報

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

		return Auth_Info;
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

				//↓ DB切断
				connection.close();

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
					//↓ ログイン情報を保持(自動ログイン関係なく、ログイントークンを利用して情報管理していく)
					String User_Token = "";
					Date Current_Time = null;
					Date Expiration_Date = null;
					int sResult = 0;					//← SQL登録実行結果
					ResultSet Session_Result = null;	//← SQL抽出実行結果

					//↓ セッションID検索処理
					Session_Result = Database_Util.Search_Session(connection, Session.getId());

					Session_Result.last();
					if(Session_Result.getRow() >= 1)
					{
						// 既存のセッションが登録されている場合、ログイン情報を削除(戻しボタン対策)

						//↓ セッションID削除処理
						sResult = Database_Util.Delete_Session(connection, Session.getId());
					}

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
						sResult = Database_Util.Register_Session(connection, Session.getId(), User_ID, User_Token, new SimpleDateFormat(datePattern).format(Expiration_Date));
					}
					else
					{
						//↓ セッションIDのみDBへ登録
						sResult = Database_Util.Register_Session(connection, Session.getId(), User_ID);
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
							response.addCookie(cookie);

							//↓ 自動ログインフラグをセッションへ設定
							Session.setAttribute("Auto_flg", "true");
						}
					}
					else
					{
						//DB処理異常終了

						//↓ ログ出力
						//log("ユーザID「" + User_ID + "」さんがログイントークン登録処理に失敗しました。SQL出力結果【sResult=「" + sResult + "」】");

						//↓ DB処理結果ロールバック
						connection.rollback();

						//↓ エラーコード付与(1:登録処理中のロールバック)
						Auth_Info.setError_Code(1);

						//↓ 異常終了画面遷移
						Auth_Info.setResult_Content("error");
						return Auth_Info;
					}
					//↓ ユーザ名取得
					login_user = rset.getString("USER_NAME");
					Auth_Info.setLogin_User(login_user);

					//↓ DB切断
					connection.close();

					//↓ 認証成功
					Auth_Info.setResult_Content("true");
					return Auth_Info;
				}
				else
				{
					//↓ DB切断
					connection.close();

					//↓ 認証失敗
					Auth_Info.setResult_Content("false");
					return Auth_Info;
				}
			}

		} catch (SQLException e) {
			// ↓ログ出力
			e.getStackTrace();
		}

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
				//log("ログイントークン「" + User_Token + "」がDBに存在しませんでした。");

				//↓ クライアント側のログイントークンを削除
				Cookie Delete_Token = new Cookie("User_Token", "");
				Delete_Token.setMaxAge(0);
				response.addCookie(Delete_Token);

				//↓ DB切断
				connection.close();

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

						//↓ DB切断
						connection.close();

						//↓ リダイレクト
						Auth_Info.setResult_Content("redirect");
						return Auth_Info;
					}
					else
					{
						//削除処理異常終了

						//↓ DB処理結果ロールバック
						connection.rollback();

						//↓ エラーコード付与(3:有効期限切れログイントークン削除処理中のロールバック)
						Auth_Info.setError_Code(3);

						//↓ DB切断
						connection.close();

						//↓ 異常処理結果画面遷移
						Auth_Info.setResult_Content("error");
						return Auth_Info;

					}
				}
				//↓ ユーザ名取得
				Auth_Info.setLogin_User(Login_User);

				//↓ DB切断
				connection.close();

				//↓ 認証成功
				Auth_Info.setResult_Content("true");
				return Auth_Info;
			}
		} catch (SQLException e) {
			e.getStackTrace();
		}

		//↓ エラーコード付与(4:自動ログイン処理中の想定外エラー)
		Auth_Info.setError_Code(4);

		//↓ 最後まできてもバグなのでエラー
		Auth_Info.setResult_Content("error");
		return Auth_Info;
	}
}
