<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String Send_flg = "";				//← 送信区分
	int Error_Code = 0;				//← エラーコード
	String Error_Title = "";			//← エラーメッセージ(見出し)
	String Error_Content_1 = "";	//← エラーメッセージ(内容1)
	String Error_Content_2 = "";	//← エラーメッセージ(内容2)
	HttpSession Session;				//← セッション情報

	//↓ 現セッション取得
	Session = request.getSession();

	//↓ 送信区分取得
	if(Send_flg != null && Send_flg.equals("Return"))
	{
		//↓ セッション破棄(セッションハイジャック対策)
		Session.invalidate();
		//↓ 新規セッションを開始
		Session = request.getSession();

		//↓ ログイン画面へ画面遷移
		RequestDispatcher Login_Dispatch = request.getRequestDispatcher("../jsp/Main_Menu.jsp");
		Login_Dispatch.forward(request, response);

		return;
	}

	//↓ エラーコード取得
	if(Session.getAttribute("Error_Code") != null)
		Error_Code = (int)Session.getAttribute("Error_Code");
	else
		Error_Code = 99;

	//↓　いつかXMLファイルで管理する予定。。。

	//↓ エラーコード内容
	switch (Error_Code)
	{
		case 1:
					//↓ 1:登録処理中のDBロールバック
					Error_Title = "ログインエラー";
					Error_Content_1 = "ログイン処理中にエラーが発生しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
					break;
		case 2:
					//↓ 2:手動ログイン処理時の想定外エラー
					Error_Title = "手動ログイン処理中の想定外エラー";
					Error_Content_1 = "手動ログイン処理中に想定外のエラーが発生しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
					break;
		case 3:
					//↓ 3:有効期限切れログイントークン削除処理中のDBロールバック
					Error_Title = "有効期限切れ自動ログイン情報の削除エラー";
					Error_Content_1 = "有効期限切れ自動ログイン情報の削除処理が異常終了しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
					break;
		case 4:
					//↓ 4:自動ログイン処理時の想定外エラー
					Error_Title = "自動ログイン処理中の想定外エラー";
					Error_Content_1 = "自動ログイン処理中に想定外のエラーが発生しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
					break;
		case 5:
					//↓ 5:ログアウト処理時のログイントークン削除DBエラー
					Error_Title = "ログアウト処理時のログイン情報削除エラー";
					Error_Content_1 = "ログアウト処理中にエラーが発生しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
					break;
		case 6:
					//↓ 6:ログアウト処理想定外エラー
					Error_Title = "ログアウト処理時の想定外エラー";
					Error_Content_1 = "ログアウト処理中に想定外エラーが発生しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
					break;
		case 7:
					//↓ 7:ログイン情報取得エラー
					Error_Title = "ログイン情報取得エラー";
					Error_Content_1 = "ログイン情報取得に失敗しました。";
					Error_Content_2 = "再度、ログインしてください。";
					break;
		default:
					//↓ その他エラー
					Error_Title = "原因不明のエラー";
					Error_Content_1 = "原因不明のエラーが発生しました。";
					Error_Content_2 = "システム管理者へお問い合わせ下さい。";
	}
%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../css/Menu.css" />
<meta charset="UTF-8">
<title>異常終了画面</title>
</head>
<body>
	<script type="text/javascript" src="../js/Window_Common.js"></script>
	<script type="text/javascript">

		var Alive_flg;							//← 画面遷移フラグ

		window.onload = function()
		{
			//↓ 初期化
			Alive_flg = false;
		};

		//↓ 送信区分付与処理
		function Send_Login()
		{
			//↓ セッション破棄回避フラグをtrueにしてonbeforeunload回避
			Alive_flg = true;
			var Send_flg = document.createElement('input');
			Send_flg.setAttribute('type', 'hidden');
			Send_flg.setAttribute('name', 'Send_flg');
			Send_flg.setAttribute('value', 'Return');
			document.Error_Form.appendChild(Send_flg);
			//↓ フォーム内容を送信
			document.Error_Form.submit();
		}
	</script>
	<div class="parent">
		<div class="inner">
			<h3 id="Error_Title"><%= Error_Title %></h3>
			<br>
			<p id="Error_Content_1"><%= Error_Content_1 %></p>
			<p id="Error_Content_2"><%= Error_Content_2 %></p>
			<br>
			<br>
			<form name="Error_Form" method="post">
				<input type="button" name="back" value="ログイン画面へ戻る" onclick="Send_Login()" />
			</form>
			<br>
			<br>
		</div>
	</div>
</body>
</html>