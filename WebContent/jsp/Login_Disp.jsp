<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.*"%>

<%
	String Alert_flg = "";						//← アラートフラグ
	String Auto_flg = "";						//← 自動ログインフラグ
	HttpSession Session = null;			//← セッション情報
	String Send_flg = null;					//← 送信区分

	//↓ セッション情報取得
	Session = request.getSession();

	//↓ セッション情報より自動ログインフラグを取得
	Auto_flg = (String)Session.getAttribute("Auto_flg");

	//↓ 認証エラーフラグ取得
	Alert_flg = (String)Session.getAttribute("Alert_flg");

	//↓ 送信区分取得
	Send_flg = request.getParameter("Send_flg");

	//↓ セッション内の認証エラーフラグを消去
	Session.removeAttribute("Alert_flg");

	if(Send_flg != null && Send_flg.equals("Login"))
	{
		Session.setAttribute("Alive_flg", "true");
		//↓ メインメニュー画面へ画面遷移し、認証処理を実行する。
		RequestDispatcher Login_dispatch = request.getRequestDispatcher("./Main_Menu.jsp");
		Login_dispatch.forward(request, response);

		return;
	}

%>
<!-- 以下、html/css処理 -->
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<!-- BootstrapのCSS読み込み -->
	<link rel="stylesheet" type="text/css" href="../css/IO_Disp.css" />
    <link href="../css/bootstrap-4.1.3-css/bootstrap.min.css" rel="stylesheet">
    <!-- jQuery読み込み -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <!-- BootstrapのJS読み込み -->
    <script src="../js/bootstrap-4.1.3-js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../js/Window_Common.js"></script>
	<title>ログイン画面</title>
	<style>
	<!--
	body
	{
		padding-top: 70px;
	}
	.navbar-brand
	{
		font-size: 24px;
	}
	.form-group
	{
		margin-bottom: 5px;
	}
	-->
	</style>
</head>
<body>
	<!-- ↓ javascript処理 -->
	<script type="text/javascript">

		var Alive_flg;						//← 画面遷移フラグ

		//↓ ログイン画面読み込み時、実行
		//↓ セッション情報が保持されていた場合、オートログイン実施。
		window.onload = function()
		{
			//↓ 初期化
			Alive_flg = false;
			//↓ オートログインの初期設定(チェックをはずす)
			document.Login_Form.Auto_Login_Check.checked = false;

			if("<%= Auto_flg %>" == "true")
			{
				//↓ 画面遷移フラグをtrueにしてonbeforeunload回避
				Alive_flg = true;

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
					//↓ 画面遷移フラグをtrueにしてonbeforeunload回避
					Alive_flg = true;

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
<div class="container">
	<div class="row">
		<div class="col-lg-3"></div>
		<div class="col-lg-6">
			<div class="form-wrap">
				<div class="text-center"><h3>ログイン画面</h3></div>
				<form role="form" name="Login_Form" method="post">
				 	<div class="form-group">
						<label for="user_id" class="col-sm-2 control-label">User_ID</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="user_id" placeholder="User ID" name="user_id" value="" />
						</div>
					</div>
					 <div class="form-group">
						<label for="user_pw" class="col-sm-2 control-label">Password</label>
						<div class="col-sm-10">
							<input type="password" class="form-control" id="user_pw" placeholder="Password" name="user_pw" value=""/>
						</div>
					</div>
					<div class="form-group">
						<!-- オートログインのチェックボックス -->
						<input type="checkbox" id="Auto_Login_Check" name="Auto_Login_Check" /><label for="Auto_Login_Check">Remember me</label>
					</div>
					<div class="text-center">
						<!-- ログインボタン -->
						<input type="button" name="Login_Button" value="Sign in" onclick="Send_Function()" >
						<!-- リセットボタン -->
						<input type="reset" name="Clear_Button" value="Reset"  />
					</div>
				</form>
			</div>
		</div>
		<div class="col-lg-3"></div>
	</div>
</div>
</body>
</html>