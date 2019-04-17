<%@page import="members.Change_Servlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="info.Auth_Info" %>
<%
	String Send_flg = "";		//← 送信区分
	String Alert_flg = "";		//← アラートフラグ

	//↓ 送信区分取得
	Send_flg = request.getParameter("Send_flg");

	//↓ パスワード変更処理
	if(Send_flg != null && Send_flg.equals("Change"))
	{
		HttpSession Session = null;
		Auth_Info Auth_Info = null;
		String Before_pw = "";
		String After_pw = "";

		//↓　セッション情報取得
		Session = request.getSession();

		Change_Servlet Change_Servlet = new Change_Servlet();

		//↓ 現パスワードをリクエストボディ部から受信
		Before_pw = request.getParameter("Before_pw");
		//↓ 新パスワードをリクエストボディ部から受信
		After_pw = request.getParameter("After_pw");

		//↓ パスワード変更処理
		Auth_Info = Change_Servlet.Change_Password(Session, Before_pw, After_pw);

		if(Auth_Info.getResult_Content().equals("true"))
		{
			//↓ パスワード変更処理正常終了

		}
		else
		{
			if(Auth_Info.getResult_Content().equals("false"))
			{
				//↓ 現パスワード認証失敗
				Alert_flg = "Failed";
			}
			else
			{
				if(Auth_Info.getResult_Content().equals("error"))
				{
					//  パスワード変更処理異常終了

					//↓ セッションへエラーコード設定
					Session.setAttribute("Error_Code", Auth_Info.getError_Code());
					//↓ 異常終了画面遷移
					RequestDispatcher Error_Dispatch = request.getRequestDispatcher("/WEB-INF/jsp/Error_Login_Disp.jsp");
					Error_Dispatch.forward(request, response);
				}
			}
		}
	}

	//↓ メインメニューへ戻る
	if(Send_flg != null && Send_flg.equals("Once"))
	{
		//↓ メインメニュー画面へ戻る
		RequestDispatcher Once_dispatch = request.getRequestDispatcher("../jsp/Main_Menu.jsp");
		Once_dispatch.forward(request, response);

		return;
	}

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="../css/Menu.css" />
<title>パスワード変更画面</title>
</head>
<body>
	<!-- ↓ javascript 処理-->
	<script type="text/javascript" src="../js/Window_Common.js"></script>
	<script type="text/javascript">

		var Alive_flg;							//← セッション破棄回避フラグ

		window.onload = function()
		{
			//↓ 初期化
			Alive_flg = false;

			//↓ 認証失敗時のアラートメッセージ
			if("<%= Alert_flg %>" == "Failed")
			{
				alert("認証に失敗しました。\nパスワードをご確認ください。");
			}
		};

		//↓ 送信区分付与処理
		function Send_Login()
		{
			//↓ セッション破棄回避フラグをtrueにしてonbeforeunload回避
			Alive_flg = true;

			var Send_flg = document.createElement('input');
			Send_flg.setAttribute('type', 'hidden');
			Send_flg.setAttribute('name', 'Send_flg');
			Send_flg.setAttribute('value', 'Once');
			document.Back_Form.appendChild(Send_flg);
			//↓ フォーム内容を送信
			document.Back_Form.submit();
		}

		//↓ 入力項目チェック処理
		function Check_Password()
		{
			//↓ セッション破棄回避フラグをtrueにしてonbeforeunload回避
			Alive_flg = true;

			//↓ 新しいパスワード項目制御
			var After_pw = document.getElementById("After_pw");
			//↓ 確認パスワード項目制御
			var Confirm_pw = document.getElementById("Confirm_pw");

			//↓ 新パスワードと確認パスワードの比較
			if(After_pw.value == Confirm_pw.value)
			{
				var Send_flg = document.createElement('input');
				Send_flg.setAttribute('type', 'hidden');
				Send_flg.setAttribute('name', 'Send_flg');
				Send_flg.setAttribute('value', 'Change');
				document.Change_Password_Form.appendChild(Send_flg);
				//↓ パスワード変更処理へ移行
				document.Change_Password_Form.submit();
			}
			else
			{
				//↓ アラートメッセージ表示
				alert("「新しいパスワード」と「新しいパスワード(確認)」の内容が不一致です。");
			}
		}
	</script>
	<!-- ↑ javascript処理 -->

	<div id="disp" class="parent">
		<div class="inner">
			<h3>パスワード変更画面</h3>
			<p>下記、項目を入力してください。</p>
			<form name="Change_Password_Form" method="post">
				<p>現在のパスワード</p>
				<input type="password" id="Before_pw" name="Before_pw" />
				<p>新しいパスワード</p>
				<input type="password" id="After_pw" name="After_pw" />
				<p>新しいパスワード(確認)</p>
				<input type="password" id="Confirm_pw" name="Confirm_pw" />
				<br>
				<br>
				<!-- パスワード変更ボタン -->
				<input type="button" name="update" value="パスワード変更" onclick="Check_Password()" />
				<br>
				<br>
			</form>
			<form name="Back_Form" method="post">
				<!-- 戻るボタン -->
				<input type="button" name="back" onclick="Send_Login()" value="メインメニューへ戻る" />
			</form>
		</div>
	</div>
</body>
</html>