<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.*"%>

<%
	String Alert_flg = "";						//← アラートフラグ
	String Auto_flg = "";						//← 自動ログインフラグ
	HttpSession Session = null;					//← セッション情報

	//↓ セッション情報取得
	Session = request.getSession();

	//↓ セッション情報より自動ログインフラグを取得
	Auto_flg = (String)Session.getAttribute("Auto_flg");

	//↓ 認証エラーフラグ取得
	Alert_flg = (String)Session.getAttribute("Alert_flg");

	//↓ セッション内の認証エラーフラグを消去
	Session.removeAttribute("Alert_flg");

%>
<!-- 以下、html/css処理 -->
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<link rel="stylesheet" type="text/css" href="../css/IO_Disp.css" />
	<title>ログイン画面</title>
</head>
<body>
	<!-- ↓ javascript処理 -->
	<script type="text/javascript">

		//↓ 履歴保持の無効化
		history.pushState(null, null, null);
		//↓ ウィンドウの戻るボタン無効化
		window.addEventListener('popstate', function()
		{
			alert("本ページの戻るボタンは禁止です。");
			history.pushState(null, null, null);
		}, false);

		//↓ ログイン画面読み込み時、実行
		//↓ セッション情報が保持されていた場合、オートログイン実施。
		window.onload = function()
		{
			//↓ オートログインの初期設定(チェックをはずす)
			document.Login_Form.Auto_Login_Check.checked = false;

			if("<%= Auto_flg %>" == "true")
			{
				//↓ 一瞬だけログイン画面が表示されて邪魔なので画面そのものを非表示。
				document.getElementById("disp").style.display="none";
				var Login_flg = document.createElement('input');
				Login_flg.setAttribute('type', 'hidden');
				Login_flg.setAttribute('name', 'Send_flg');
				Login_flg.setAttribute('value', 'Login');
				document.Login_Form.appendChild(Login_flg);
				document.Login_Form.submit();
			}

			//↓ 認証エラーフラグの確認
			switch("<%= Alert_flg %>")
			{
				case "Failed":
					//↓ 認証エラーメッセージ表示
					alert("認証に失敗しました。\nユーザID又はパスワードをご確認ください。");
					break;

				case "Logout":
					//↓ ログアウトメッセージ
					alert("ログアウトしました。");
					break;

				default:
					break;
			}

		};

		//↓ ウィンドウを閉じる前に実行
		window.onbeforeunload = function()
		{
			//↓ セッション切断処理
			document.location.href="../Session_Out";
		};

		//↓ フォーム送信処理
		function Send_Function()
		{
			//↓ ユーザID入力チェック
			if(document.getElementById("user_id").value.trim() == "")
			{
				//↓ アラート表示
				alert("ユーザIDを入力してください。");
			}
			else
			{
				//↓ パスワード入力チェック
				if(document.getElementById("user_pw").value.trim() == "")
				{
					//↓ アラート表示
					alert("パスワードを入力してください。");
				}
				else
				{
					//↓ オートログインチェックボックスの確認
					if(document.Login_Form.Auto_Login_Check.checked == true)
					{
						document.Login_Form.Auto_Login_Check.value = "true";
					}
					var Login_flg = document.createElement('input');
					Login_flg.setAttribute('type', 'hidden');
					Login_flg.setAttribute('name', 'Send_flg');
					Login_flg.setAttribute('value', 'Login');
					document.Login_Form.appendChild(Login_flg);
					//↓ フォーム内容を送信
					document.Login_Form.submit();
				}
			}
		}
	</script>
	<!-- ↑ javascript処理 -->

	<div id="disp" class="parent">
		<div class="inner">
			<h3>ログイン画面</h3>
			<br>
			<!--
			<form name="Login_Form" method="post">
			 -->
			 <form name="Login_Form" method="post" action="./Main_Menu.jsp">
				<p>ユーザID</p>
				<input type="text" id="user_id" name="user_id" value="" />
				<p>パスワード</p>
				<input type="password" id="user_pw" name="user_pw" value="" />
				<br>
				<br>
				<!-- オートログインのチェックボックス -->
				<input type="checkbox" id="Auto_Login_Check" name="Auto_Login_Check" /><label for="Auto_Login_Check">自動ログインを設定する</label>
				<br>
				<br>
				<br>
				<!-- ログインボタン -->
				<input type="button" name="Login_Button" value="ログイン" onclick="Send_Function()" >
				<!-- リセットボタン -->
				<input type="reset" name="Clear_Button" value="リセット"  />
			</form>
		</div>
	</div>
</body>
</html>