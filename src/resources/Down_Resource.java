package resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import util.Database_util;

public class Down_Resource
{
	public int Select_Resource(ArrayList<Resrc_Select_Info> Resouce_Result)
	{
		String sQuery = "";							//← SQL文格納用変数
		Resrc_Select_Info Resrc_Select_Info;		//← SQL実行結果データ格納クラス変数
		//ArrayList<Resrc_Select_Info> ResultList;	//← SQL実行結果データセット格納リスト変数

		try {
			Connection connection = null;
			PreparedStatement pStmt = null;
			ResultSet rset = null;

			//↓ データベース接続処理
			connection = Database_util.DB_Connection();

			//↓ リソース内容を取得
			sQuery = "SELECT "
					+ "RESOURCE_HIS_ID, "
					+ "RESOURCE_NAME, "
					+ "RESOURCE_DIR, "
					+ "USER_NAME, "
					+ "UPDATE_DAY, "
					+ "CREATE_BY_NAME, "
					+ "FIRST_UPDATE_DAY "
					+ "FROM "
					+ "RESOURCE_HIS_TBL "
					+ "right outer join RESOURCE_TBL using (RESOURCE_ID) "
					+ "inner join USER_MANAGEMENT_TBL on (RESOURCE_HIS_TBL.UPDATE_BY_ID = USER_MANAGEMENT_TBL.USER_ID)";
			pStmt = connection.prepareStatement(sQuery);
			rset = pStmt.executeQuery();

			//↓ 抽出件数を調べるために、カーソルを後尾行へ移動
			rset.last();

			//↓ 抽出件数が0件の場合、該当情報なし表示
			if(rset.getRow() == 0)
				//↓ 該当情報なし表示
				return 1;
			else
			{
				Resrc_Select_Info = new Resrc_Select_Info();
				//ResultList = new ArrayList<Resrc_Select_Info>();
				//↓ データ抽出のため、カーソルを先頭行手前まで戻す
				rset.beforeFirst();
				//↓SQL実行結果データ取り出し
				while(rset.next())
				{
					Resrc_Select_Info.setResrc_His_id(rset.getInt("RESOURCE_HIS_ID"));				//← 履歴管理番号
					Resrc_Select_Info.setResrc_Name(rset.getString("RESOURCE_NAME"));			//← リソース名
					Resrc_Select_Info.setResrc_Dir(rset.getString("RESOURCE_DIR"));				//← リソースディレクトリパス
					Resrc_Select_Info.setUpdate_By_Name(rset.getString("USER_NAME"));			//← 更新者名
					Resrc_Select_Info.setUpdate_Day(rset.getString("UPDATE_DAY"));				//← 更新日
					Resrc_Select_Info.setCreate_By_Name(rset.getString("CREATE_BY_NAME"));		//← 作成者
					Resrc_Select_Info.setFirst_Update_Day(rset.getString("FIRST_UPDATE_DAY"));	//← 初回更新日

					//↓ 取得した実行結果をリストへ格納
					Resouce_Result.add(Resrc_Select_Info);
				}
			}
			//↓ 結果画面へ画面遷移
			//RequestDispatcher dispatch = request.getRequestDispatcher("/WEB-INF/Resrc_Down_Select_Disp.jsp");
			//dispatch.forward(request, response);

			return 0;
		}
		catch (SQLException e)
		{
			// TODO 自動生成された catch ブロック
			e.printStackTrace();

			return 1;
		}
		catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();

			return 1;
		}
	}
}
