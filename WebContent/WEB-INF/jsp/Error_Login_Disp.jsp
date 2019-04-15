<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	int Error_Code = 0;			//← エラーコード
	String Error_Title = "";	//← エラーメッセージ(見出し)
	String Error_Content_1 = "";	//← エラーメッセージ(内容1)
	String Error_Content_2 = "";	//← エラーメッセージ(内容2)
	HttpSession Session;		//← セッション

	//↓ 現セッション取得
	Session = request.getSession();

	//↓ エラーコード取得
	Error_Code = (int)Session.getAttribute("Error_Code");

	//↓ セッション破棄(セッションハイジャック対策)
	Session.invalidate();

	//↓ 新規セッションを開始
	Session = request.getSession();

	//↓ エラーコード内容
	switch (Error_Code)
	{
		case 1:
					//↓ 1:登録処理中のDBロールバック
					Error_Title = "自動ログイン設定エラー";
					Error_Content_1 = "自動ログインの設定に失敗しました。";
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
<meta charset="UTF-8">
<title>異常終了画面</title>
</head>
<body>
	<div class="parent">
		<div class="inner">
			<h3 id="Error_Title"><%= Error_Title %></h3>
			<br>
			<p id="Error_Content_1"><%= Error_Content_1 %></p>
			<p id="Error_Content_2"><%= Error_Content_2 %></p>
			<br>
			<br>
			<button onclick="location.href='../jsp/Login_Disp.jsp'">ログイン画面に戻る</button>
			<br>
			<br>
		</div>
	</div>
</body>
</html>