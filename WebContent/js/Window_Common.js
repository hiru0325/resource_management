/**
 *
 */

//↓ 履歴保持の無効化
history.pushState(null, null, null);
//↓ ウィンドウの戻るボタン無効化
window.addEventListener('popstate', function()
{
	alert("本ページの戻るボタンは禁止です。");
	history.pushState(null, null, null);
}, false);

//↓ ウィンドウを閉じる前に実行
window.onbeforeunload = function()
{
	if(Alive_flg == false)
	{
		//↓ セッション切断処理
		document.location.href="../Session_Out";
	}
};
