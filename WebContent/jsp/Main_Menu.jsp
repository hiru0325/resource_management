<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String Send_flg = "";			//← 画面遷移フラグ
	HttpSession Session = null;		//← セッション情報

	Session = request.getSession();

	//リクエストよりログインユーザ名の取得
	String Login_User = request.getParameter("Login_User");

	//↓ 画面遷移フラグ取得
	Send_flg = request.getParameter("Send_flg");

	//↓パスワード変更処理
	if(Send_flg != null && Send_flg.equals("Change_Password"))
	{
		RequestDispatcher dispatch = request.getRequestDispatcher("../WEB-INF/jsp/Change_Password_Disp.jsp");
		dispatch.forward(request, response);
	}

	//↓ログアウト処理
	if(Send_flg != null && Send_flg.equals("Logout"))
	{
		RequestDispatcher dispatch = request.getRequestDispatcher("../LogoutServlet");
		dispatch.forward(request, response);
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

	var Send_flg;

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