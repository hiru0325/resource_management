<%@page import="members.Return_Servlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="login.Login_Servlet" %>
<%@ page import="info.Auth_Info" %>
<%

	String Send_flg = "";					//← 画面遷移フラグ
	String Login_User = "";				//← ユーザ名
	HttpSession Session = null;		//← セッション情報

	//↓ セッション情報取得
	Session = request.getSession();

	//↓ 画面遷移フラグ取得
	Send_flg = request.getParameter("Send_flg");

	if(Send_flg == null || Send_flg.equals("Return"))
	{
		//↓ メインメニュー画面へ直接アクセスした場合はログイン画面へリダイレクト
		if(Session.getAttribute("Alert_flg") == null)
			Session.setAttribute("Alert_flg", "false");
		RequestDispatcher Login_Dispatch = request.getRequestDispatcher("Login_Disp.jsp");
		Login_Dispatch.forward(request, response);

		return;
	}

	//↓ ログイン処理
	if(Send_flg != null && Send_flg.equals("Login"))
	{
		int Return_Code = 0;
		//↓インスタンス化
		Login_Servlet Login_Servlet = new Login_Servlet();

		//↓ ログ表示
		log("「" + Session.getId() + "」がログイン処理を開始しました。");
		//↓ ログイン認証処理
		Return_Code = Login_Servlet.Authentication(request, response);

		if(Return_Code == 0)
		{
			// ログイン認証成功

			//↓ ログ表示
			log("「" + Session.getId() + "」がログイン処理で正常終了しました。");
			//送信区分を変更し、ユーザID取得
			Send_flg = "Once";
		}
		else
		{
			if(Return_Code == 9)
			{
				// 手動ログイン認証失敗

				log("「" + Session.getId() + "」が手動ログイン処理で認証失敗しました。");

				//↓ セッション破棄(セッションハイジャック対策)
				Session.invalidate();
				//↓ 新規セッションを開始
				Session = request.getSession();

				//↓ 認証失敗エラーフラグをFailedにし、ログイン画面へリダイレクト
				Session.setAttribute("Alert_flg", "Failed");
				response.sendRedirect("Main_Menu.jsp");

				return;
			}
			else
			{
				if(Return_Code == 8)
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
					response.sendRedirect("Main_Menu.jsp");

					return;
				}
				else
				{
					// 処理異常終了

					//↓ ログ表示
					log("「" + Session.getId() + "」がログイン処理にて異常終了しました。");

					//↓ セッションへエラーコード設定
					Session.setAttribute("Error_Code", Return_Code);
					//↓ 異常終了画面遷移
					RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
					Error_Dispatch.forward(request, response);

					return;
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
		Auth_Info = Return_Servlet.Select_Info(request, response);

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

		return;
	}

	//↓ログアウト処理
	if(Send_flg != null && Send_flg.equals("Logout"))
	{
		RequestDispatcher Logout_dispatch = request.getRequestDispatcher("/LogoutServlet");
		Logout_dispatch.forward(request, response);

		return;
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
	<script type="text/javascript" src="../js/Window_Common.js"></script>
	<script type="text/javascript">

	var Alive_flg;		//← セッション破棄回避フラグ
	var Send_flg;	//← リクエスト要求フラグ

	window.onload = function()
	{
		Alive_flg = false;
	};

	//↓ パスワード変更処理
	function Send_Change_Password()
	{
		//↓ 初期化
		Alive_flg = true;
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
		//↓ セッション破棄回避フラグをtrueにしてonbeforeunload回避
		Alive_flg = true;

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