<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="../css/Menu.css" />
<title>パスワード変更画面</title>
</head>
<body>
	<!-- ↓ javascript 処理-->
	<script type="text/javascript">

		//↓ 履歴保持の無効化
		history.pushState(null, null, null);
		//↓ ウィンドウの戻るボタン無効化
		window.addEventListener('popstate', function()
		{
			alert("本ページの戻るボタンは禁止です。");
			history.pushState(null, null, null);
		}, false);

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
				<!--
				<input type="button" name="update" value="パスワード変更" onclick="Check_Password()" />
				 -->
				<br>
				<br>
			</form>
			<form method="post" action="../jsp/Main_Menu.jsp">
				<!-- 戻るボタン -->
				<input type="hidden" name="Send_flg" value="Once" />
				<input type="submit" name="back" value="メインメニューへ戻る" />
			</form>
		</div>
	</div>
</body>
</html>