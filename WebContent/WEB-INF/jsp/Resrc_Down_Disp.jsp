<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="resources.Resrc_Select_Info"%>
<%
	int result_flg = 0;										//← SQL実行結果フラグ変数
	ArrayList<Resrc_Select_Info> Resource_Result;	//← SQL実行結果データセットリスト変数
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ダウンロードファイル選択画面</title>
</head>
<body>
	<!-- ↓javascript -->
	<script type="text/javascript">
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
	</script>
	<!-- ↑javascript -->
	<br>
	<h3>ダウンロードファイル選択画面</h3>
	<br>
	<br>
	<% //↓ SQL実行結果に抽出データが存在するか確認
		   if(result_flg == 0)
		  {
				//↓ SQL実行結果データセットをリクエスト部より受け取る
				 Resource_Result = (ArrayList<Resrc_Select_Info>)request.getAttribute("Resource_Result");
	%>
		<table border="1">
			<tr>
				<td></td>
				<td>管理番号</td>
				<td>リソース名</td>
				<td>格納場所</td>
				<td>更新者</td>
				<td>更新日</td>
				<td>作成者</td>
				<td>初回更新日</td>
			</tr>
			<% for(Resrc_Select_Info Resrc_Select_Info : Resource_Result)
				  {
			%>
						<tr>
								<td><input type="radio" name="Down_Select"/></td>
								<td><%= Resrc_Select_Info.getResrc_His_id() %></td>					<!-- 管理番号 -->
								<td><%= Resrc_Select_Info.getResrc_Name() %></td>			<!-- リソース名 -->
								<td><%= Resrc_Select_Info.getResrc_Dir() %></td>				<!-- ディレクトリパス -->
								<td><%= Resrc_Select_Info.getUpdate_By_Name() %></td>	<!-- 更新者 -->
								<td><%= Resrc_Select_Info.getUpdate_Day() %></td>			<!-- 更新日 -->
								<td><%= Resrc_Select_Info.getCreate_By_Name() %></td>	<!-- 作成者 -->
								<td><%= Resrc_Select_Info.getFirst_Update_Day() %></td>	<!-- 初回更新日 -->
						</tr>
			<% } %>
		</table>
	<% } %>
	<br>
	<br>
	<form name="Back_Form" method="post">
		<!-- 戻るボタン -->
		<input type="button" name="back" onclick="Send_Login()" value="メインメニューへ戻る" />
	</form>
	<br>
</body>
</html>