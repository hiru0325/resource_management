<%@page import="java.util.ArrayList"%>
<%@page import="resources.Down_Resource"%>
<%@page import="resources.Resrc_Select_Info"%>
<%@page import="login.Return_Servlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="login.Login_Servlet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="members.Change_Pass_Servlet" %>
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
		//↓　自動ログインフラグ取得
		String Auto_flg = (String)Session.getAttribute("Auto_flg");
		//↓インスタンス化
		Login_Servlet Login_Servlet = new Login_Servlet();

		//↓ ログ表示
		log("「" + Session.getId() + "」がログイン処理を開始しました。");
		//↓ ログイン認証処理

		try
		{
			//↓ 自動ログインフラグの確認
			if(Auto_flg != null && Auto_flg.equals("true"))
			{
				//↓ クライアント側のログイントークンを取得
				Cookie cookie[] = request.getCookies();
				//↓　自動ログイン認証
				Return_Code = Login_Servlet.Auto_Login(cookie, response);
			}
			else
			{
				String User_ID = request.getParameter("user_id");						//← 取得ユーザID
				String User_Password = request.getParameter("user_pw");					//← リクエストユーザパスワード
				String Auto_Login_Check = request.getParameter("Auto_Login_Check"); 	//← 自動ログインチェックボックス値
				//↓ 手動ログイン認証
				Return_Code = Login_Servlet.Input_Login(User_ID, User_Password, Auto_Login_Check, Session, response);
			}
		}
		catch (SQLException e)
		{
			log(e.getStackTrace().toString());

			//↓ セッションへエラーコード設定
			Session.setAttribute("Error_Code", 1);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;

		}
		catch (ClassNotFoundException e)
		{
			log(e.getStackTrace().toString());

			//↓ セッションへエラーコード設定
			Session.setAttribute("Error_Code", 1);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}

		switch(Return_Code)
		{
			case 0:
				// ログイン認証成功
				log("「" + Session.getId() + "」がログイン処理で正常終了しました。");
				//送信区分を変更し、ユーザID取得
				Send_flg = "Once";
				break;

			case 8:
				// 自動ログイン認証失敗
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

			case 9:
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

			default :
				// 処理異常終了
				log("「" + Session.getId() + "」がログイン処理にて異常終了しました。");

				//↓ セッションへエラーコード設定
				Session.setAttribute("Error_Code", Return_Code);
				//↓ 異常終了画面遷移
				RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
				Error_Dispatch.forward(request, response);

				return;
		}
	}

	//↓ 戻りページ処理
	if(Send_flg != null && Send_flg.equals("Once"))
	{
		//↓ Return_Servlet インスタンス化
		Return_Servlet Return_Servlet = new Return_Servlet();

		try
		{
		//↓ ログイン情報取得処理
		 Login_User = Return_Servlet.Select_Info(request, response);
		}
		catch(SQLException e)
		{
			log(e.getStackTrace().toString());
			//↓ セッションへエラーコード設定(ログイン情報取得エラー)
			Session.setAttribute("Error_Code", 7);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}
		catch(ClassNotFoundException e)
		{
			log(e.getStackTrace().toString());
			//↓ セッションへエラーコード設定(その他エラー)
			Session.setAttribute("Error_Code", 9);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}

		if(Login_User == null && Login_User.isEmpty() )
		{
			//↓ セッションへエラーコード設定(ログイン情報取得エラー)
			Session.setAttribute("Error_Code", 7);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}
	}

	//↓ ファイルダウンロード画面
	if(Send_flg != null && Send_flg.equals("File_Download"))
	{
		int Return_Code = 0;
		ArrayList<Resrc_Select_Info> Resource_Result = new ArrayList<Resrc_Select_Info>();

		Down_Resource Down_Resource = new Down_Resource();

		try
		{
			//↓ ダウンロードファイル照会
			Return_Code = Down_Resource.Select_Resource(Resource_Result);
		}
		catch(SQLException e)
		{
			log(e.getStackTrace().toString());
			//↓ SQLエラー
			Session.setAttribute("Error_Code", 1);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);
		}
		catch(ClassNotFoundException e)
		{
			log(e.getStackTrace().toString());
			//↓ セッションへエラーコード設定(その他エラー)
			Session.setAttribute("Error_Code", 9);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);
		}

		switch(Return_Code)
		{
			case 0 :
						request.setAttribute("Resource_Result", Resource_Result);
						//↓ 結果画面へ画面遷移
						RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Resrc_Down_Disp.jsp");
						dispatch.forward(request, response);
						break;
			case 1 :
						//↓ ダウンロードファイルなし

						break;
			default :

		}


		return;
	}

	//↓パスワード変更画面
	if(Send_flg != null && Send_flg.equals("Change_Password"))
	{
		RequestDispatcher Change_Password_dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Change_Password_Disp.jsp");
		Change_Password_dispatch.forward(request, response);

		return;
	}

	//↓ パスワード変更処理
	if(Send_flg != null && Send_flg.equals("Change"))
	{
		int Return_Code = 0;				//← 返り値
		String Before_pw = "";
		String After_pw = "";

		Change_Pass_Servlet Change_Pass_Servlet = new Change_Pass_Servlet();

		//↓ 現パスワードをリクエストボディ部から受信
		Before_pw = request.getParameter("Before_pw");
		//↓ 新パスワードをリクエストボディ部から受信
		After_pw = request.getParameter("After_pw");

		try
		{
			//↓ パスワード変更処理
			Return_Code = Change_Pass_Servlet.Change_Password(Session, Before_pw, After_pw);
		}
		catch(SQLException e)
		{
			log(e.getStackTrace().toString());
			//↓ セッションへエラーコード設定(ログイン情報取得エラー)
			Session.setAttribute("Error_Code", 7);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}
		catch(ClassNotFoundException e)
		{
			log(e.getStackTrace().toString());
			//↓ セッションへエラーコード設定(その他エラー)
			Session.setAttribute("Error_Code", 9);
			//↓ 異常終了画面遷移
			RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
			Error_Dispatch.forward(request, response);

			return;
		}

		switch(Return_Code)
		{
			case 0:
				Session.setAttribute("Alert_flg", "Accept");
				RequestDispatcher Accept_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Change_Password_Disp.jsp");
				Accept_Dispatch.forward(request, response);
				return;

			case 9:
				//↓ パスワード認証失敗
				log("「" + Session.getId() + "」がパスワード更新処理で認証失敗しました。");

				//↓ 認証失敗エラーフラグをFailedにし、ログイン画面へリダイレクト
				Session.setAttribute("Alert_flg", "Failed");
				RequestDispatcher Failed_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Change_Password_Disp.jsp");
				Failed_Dispatch.forward(request, response);

				return;

			default:
				log("「" + Session.getId() + "」がパスワード更新処理で異常終了しました。");
				//↓ セッションへエラーコード設定
				Session.setAttribute("Error_Code", Return_Code);
				//↓ 異常終了画面遷移
				RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
				Error_Dispatch.forward(request, response);

				return;
		}
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
	<!-- BootstrapのCSS読み込み -->
    <link href="../css/bootstrap-4.1.3-css/bootstrap.min.css" rel="stylesheet">
    <!-- jQuery読み込み -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- BootstrapのJS読み込み -->
    <script src="../js/bootstrap-4.1.3-js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../js/Window_Common.js"></script>
<title>メインメニュー画面</title>
</head>
<body>
	<script type="text/javascript">

	var Alive_flg;		//← セッション破棄回避フラグ
	var Send_flg;	//← リクエスト要求フラグ

	window.onload = function()
	{
		Alive_flg = false;
	};

	//↓ ファイルダウンロード
	function Send_File_Download()
	{
		//↓ 初期化
		Alive_flg = true;
		//↓ 画面遷移のフラグ設定
		Send_flg = document.createElement('input');
		Send_flg.setAttribute('type', 'hidden');
		Send_flg.setAttribute('name', 'Send_flg');
		Send_flg.setAttribute('value', 'File_Download');
		document.Change_Password_Form.appendChild(Send_flg);
		//↓ フォーム内容を送信
		document.Change_Password_Form.submit();
	}

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
			<br>
 			<form name="File_Download_Form" method="post">
				<input type="button" name="File_Down_btn" onclick="Send_File_Download()" value="ファイルダウンロード" />
			</form>
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