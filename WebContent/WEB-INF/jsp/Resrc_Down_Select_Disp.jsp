<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="info.Resrc_Select_Info"%>
<%@page import="java.util.ArrayList"%>
<%
	int result_flg = 0;										//← SQL実行結果フラグ変数
	ArrayList<Resrc_Select_Info> ResultList;	//← SQL実行結果データセットリスト変数

	//↓ SQL実行結果フラグをリクエスト部より受け取る
	result_flg =(int)request.getAttribute("result_flg");


	if(result_flg == 0)
	{

	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ダウンロードファイル選択画面</title>
</head>
<body>
	<br>
	<h3>ダウンロードファイル選択画面</h3>
	<br>
	<br>
	<% //↓ SQL実行結果に抽出データが存在するか確認
		   if(result_flg == 0)
		  {
				//↓ SQL実行結果データセットをリクエスト部より受け取る
				 ResultList = (ArrayList)request.getAttribute("ResultList");
	%>
		<table border="1">
			<tr>
				<td>管理番号</td>
				<td>リソース名</td>
				<td>格納場所</td>
				<td>更新者</td>
				<td>更新日</td>
				<td>作成者</td>
				<td>初回更新日</td>
			</tr>
			<% for(Resrc_Select_Info Resrc_Select_Info : ResultList)
				  {
			%>
						<tr>
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
	<button id="back" name="back" onclick="history.back();">メインメニューへ戻る</button>
	<br>
</body>
</html>