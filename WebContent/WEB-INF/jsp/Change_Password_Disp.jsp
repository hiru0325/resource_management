<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="Menu.css" />
<title>パスワード変更画面</title>
</head>
<body>
	<!-- ↓ javascript 処理-->
	<script type="text/javascript">
		var Alert_Message;
		//↓ ページ読み込み後、実行
		window.onload = function()
		{
			//↓ 警告メッセージ項目制御
			Alert_Message = document.getElementById("Alert_Message");
			//↓ 警告メッセージ項目非表示
			Alert_Message.style.visibility = "hidden";
		};
		//↓ 入力項目チェック処理
		function Check_Password()
		{
			//↓ 新しいパスワード項目制御
			var After_pw = document.getElementById("After_pw");
			//↓ 確認パスワード項目制御
			var Confirm_pw = document.getElementById("Confirm_pw");


			//↓ 新パスワードと確認パスワードの比較
			if(After_pw.value == Confirm_pw.value)
			{
				//↓ パスワード変更処理へ移行
				document.Change_Password_Form.submit();
			}
			else
			{
				//↓ 比較結果メッセージ表示
				Alert_Message.innerHTML = "「新しいパスワード」と「新しいパスワード(確認)」の内容が不一致です。";
				Alert_Message.style.visibility = "visible";
			}
		}
	</script>
	<!-- ↑ javascript処理 -->

	<div id="disp" class="parent">
		<div class="inner">
			<h3>パスワード変更画面</h3>
			<p>下記、項目を入力してください。</p>
			<form name="Change_Password_Form" method="post" action="">
				<p>ユーザID</p>
				<input type="text" id="User_ID" name="User_ID" />
				<p>現在のパスワード</p>
				<input type="password" id="Before_pw" name="Before_pw" />
				<p>新しいパスワード</p>
				<input type="password" id="After_pw" name="After_pw" />
				<p>新しいパスワード(確認)</p>
				<input type="password" id="Confirm_pw" name="Confirm_pw" />
				<br>
				<br>
				<p id="Alert_Message">アラートメッセージ</p>
				<br>
				<br>
				<!-- パスワード変更ボタン -->
				<input type="button" name="update" value="パスワード変更" onclick="Check_Password()" />
				<br>
				<br>
				<!-- 戻るボタン -->
				<input type="button" name="back" value="メインメニューへ戻る" onclick="history.back();" />
			</form>
		</div>
	</div>
</body>
</html>