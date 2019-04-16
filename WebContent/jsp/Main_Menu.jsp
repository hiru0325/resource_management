<%@page import="members.Return_Servlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="members.Login_Servlet" %>
<%@ page import="info.Auth_Info" %>
<%

	String Send_flg = "";					//← 画面遷移フラグ
	String Login_User = "";				//← ユーザ名
	HttpSession Session = null;		//← セッション情報

	//↓ 画面遷移フラグ取得
	Send_flg = request.getParameter("Send_flg");

	//↓ セッション情報取得
	Session = request.getSession();

	if(Send_flg == null)
	{
		//↓ メインメニュー画面へ直接アクセスした場合はログイン画面へリダイレクト
		if(Session.getAttribute("Alert_flg") == null)
			Session.setAttribute("Alert_flg", "false");
		RequestDispatcher Login_Dispatch = request.getRequestDispatcher("Login_Disp.jsp");
		Login_Dispatch.forward(request, response);
	}

	//↓ ログイン処理
	if(Send_flg != null && Send_flg.equals("Login"))
	{
		Auth_Info Auth_Info = null;	//← 認証結果情報
		//↓インスタンス化
		Login_Servlet Login_Servlet = new Login_Servlet();

		//↓ ログ表示
		log("「" + Session.getId() + "」がログイン処理を開始しました。");
		//↓ ログイン認証処理
		Auth_Info = Login_Servlet.Authentication(request, response, Session);

		if(Auth_Info.getResult_Content().equals("true"))
		{
			// ログイン認証成功

			//↓ ログ表示
			log("「" + Session.getId() + "」がログイン処理で正常終了しました。");

			//ユーザ名取得
			Login_User = Auth_Info.getLogin_User();
		}
		else
		{
			if(Auth_Info.getResult_Content().equals("false"))
			{
				// 手動ログイン認証失敗

				log("「" + Session.getId() + "」が手動ログイン処理で認証失敗しました。");

				//↓ セッション破棄(セッションハイジャック対策)
				Session.invalidate();
				//↓ 新規セッションを開始
				Session = request.getSession();

				//↓ 認証失敗エラーフラグをFailedにし、ログイン画面へリダイレクト
				Session.setAttribute("Alert_flg", "Failed");
				RequestDispatcher Redirect_Dispatch = request.getRequestDispatcher("Login_Disp.jsp");
				Redirect_Dispatch.forward(request, response);

				return;
			}
			else
			{
				if(Auth_Info.getResult_Content().equals("redirect"))
				{
					// 自動ログイン認証失敗

					//↓ ログ表示
					log("「" + Session.getId() + "」が自動ログイン処理で認証失敗しました。");

					//↓ 自動ログインをオフにする(一応、変えておく。。。)
					Session.setAttribute("Auto_flg", "false");

					//↓ セッション破棄(セッションハイジャック対策)
					Session.invalidate();
					//↓ 新規セッションを開始
					Session = request.getSession();

					//↓ 認証失敗ではない為、エラーフラグはfalse
					Session.setAttribute("Alert_flg", "false");
					RequestDispatcher Redirect_Dispatch = request.getRequestDispatcher("Login_Disp.jsp");
					Redirect_Dispatch.forward(request, response);

					return;
				}
				else
				{
					if(Auth_Info.getResult_Content().equals("error"))
					{
						// 処理異常終了

						//↓ ログ表示
						log("「" + Session.getId() + "」がログイン処理にて異常終了しました。");

						//↓ セッションへエラーコード設定
						Session.setAttribute("Error_Code", Auth_Info.getError_Code());
						//↓ 異常終了画面遷移
						RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
						Error_Dispatch.forward(request, response);

						return;
					}
				}
			}
		}
	}

	//↓ 戻りページ処理
	if(Send_flg != null && Send_flg.equals("Once"))
	{
		Auth_Info Auth_Info = null;											//← 認証結果情報
		//↓ Return_Servlet インスタンス化
		Return_Servlet Return_Servlet = new Return_Servlet();

		//↓ ログイン情報取得処理
		Auth_Info = Return_Servlet.Select_Info(request, response, session);

		if(Auth_Info.getResult_Content().equals("true"))
		{
			//↓ ユーザ名取得
			Login_User = Auth_Info.getLogin_User();
		}
		else
		{
			//↓ セッションへエラーコード設定
			Session.setAttribute("Error_Code", Auth_Info.getError_Code());
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}
	}

	//↓パスワード変更処理
	if(Send_flg != null && Send_flg.equals("Change_Password"))
	{
		RequestDispatcher Change_Passeword_dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Change_Password_Disp.jsp");
		Change_Passeword_dispatch.forward(request, response);
	}

	//↓ログアウト処理
	if(Send_flg != null && Send_flg.equals("Logout"))
	{
		RequestDispatcher Logout_dispatch = request.getRequestDispatcher("/LogoutServlet");
		Logout_dispatch.forward(request, response);
	}

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="../css/Menu.css" />
<title>メインメニュー画面</title>
</head>
<body>
	<script type="text/javascript">

	var Send_flg;	//← リクエスト要求フラグ

	//↓ 履歴保持の無効化
	history.pushState(null, null, null);
	//↓ ウィンドウの戻るボタン無効化
	window.addEventListener('popstate', function()
	{
		alert("本ページの戻るボタンは禁止です。");
		history.pushState(null, null, null);
	}, false);

	//↓ 履歴保持の無効化
	history.pushState(null, null, null);
	//↓ ウィンドウの戻るボタン無効化
	window.addEventListener('popstate', function()
	{
		alert("本ページの戻るボタンは禁止です。");
		history.pushState(null, null, null);
	}, false);

	//↓ ウィンドウを閉じる前に実行
	window.onbeforeunload = function()
	{
		//↓ セッション切断処理
		document.location.href="../Session_Out";
	};

	//↓ パスワード変更処理
	function Send_Change_Password()
	{
		//↓ 画面遷移のフラグ設定
		Send_flg = document.createElement('input');
		Send_flg.setAttribute('type', 'hidden');
		Send_flg.setAttribute('name', 'Send_flg');
		Send_flg.setAttribute('value', 'Change_Password');
		document.Change_Password_Form.appendChild(Send_flg);
		//↓ フォーム内容を送信
		document.Change_Password_Form.submit();
	}

	//↓ ログアウト処理
	function Send_Logout()
	{
		//↓ 画面遷移のフラグ設定
		Send_flg = document.createElement('input');
		Send_flg.setAttribute('type', 'hidden');
		Send_flg.setAttribute('name', 'Send_flg');
		Send_flg.setAttribute('value', 'Logout');
		document.Logout_Form.appendChild(Send_flg);
		//↓ フォーム内容を送信
		document.Logout_Form.submit();
	}
	</script>

	<div class="parent">
		<div class="inner">
			<h3>メインメニュー画面</h3>
			<br>
			<p class="login_user">ようこそ、 <%= Login_User %> さん</p>
			<!--
			<button onclick="location.href='../jsp/Select_Resource'">ファイルダウンロード</button>
			 -->
			<br>
			<br>
			<!--
			<button onclick="location.href='./Change_Password_Disp.jsp'">パスワード変更</button>
			-->
			<form name="Change_Password_Form" method="post">
				<input type="button" name="Change_Password_btn" onclick="Send_Change_Password()" value="パスワード変更" />
			</form>
			<br>
			<br>
			<form name="Logout_Form" method="post">
				<input type="button" name="Logout_btn" onclick="Send_Logout()" value="ログアウト" />
			</form>
			<br>
			<br>
		</div>
	</div>
</body>
</html>