<%@page import="members.Change_Pass_Servlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.SQLException" %>
<%
	HttpSession Session = null;		//← セッション情報
	String Alert_flg = "";					//← アラートフラグ

	//↓ セッション情報格納
	Session = request.getSession();
	//↓ アラートフラグ取得
	Alert_flg = (String)Session.getAttribute("Alert_flg");
	//↓ アラートフラグをセッション上から消去
	Session.removeAttribute("Alert_flg");
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

			//↓ 認証エラーフラグの確認
			switch("<%= Alert_flg %>")
			{
				case "Accept":
					//↓ パスワード変更処理正常終了
					alert("パスワード変更に成功しました。");
					break;
				case "Failed":
					//↓ 認証エラーメッセージ表示
					alert("パスワード認証に失敗しました。\n現在のパスワードをご確認ください。");
					break;

				default:
					break;
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

			//↓ 現在パスワード項目制御
			var Before_pw = document.getElementById("Before_pw");
			//↓ 新しいパスワード項目制御
			var After_pw = document.getElementById("After_pw");
			//↓ 確認パスワード項目制御
			var Confirm_pw = document.getElementById("Confirm_pw");


			if(Before_pw.value.trim() == "")
			{
				alert("「現在のパスワード」項目を入力してください。");
			}
			else
			{
				if(After_pw.value.trim() == "")
				{
					alert("「新しいパスワード」項目を入力してください。");
				}
				else
				{
					if(Confirm_pw.value.trim() == "")
					{
						alert("「新しいパスワード(確認)」項目を入力してください。");
					}
					else
					{
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
				}
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
				<input type="button" name="update" onclick="Check_Password()" value="パスワード変更"  />
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