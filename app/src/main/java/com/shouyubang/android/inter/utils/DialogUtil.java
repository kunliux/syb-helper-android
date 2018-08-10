/**
 *
 */
package com.shouyubang.android.inter.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.shouyubang.android.inter.MainActivity;

public class DialogUtil
{
	// 定义一个显示消息的对话框
	public static void showDialog(final Context content
		, String msg , boolean goHome)
	{
		if(content == null)
            return;
		
		// 创建一个AlertDialog.Builder对象
		AlertDialog.Builder builder = new AlertDialog.Builder(content)
			.setMessage(msg).setCancelable(false);
		if(goHome)
		{
			builder.setPositiveButton("确定", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Intent i = new Intent(content , MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					content.startActivity(i);
				}
			});
		}
		else
		{
			builder.setPositiveButton("确定", null);
		}
		builder.create().show();
	}
	// 定义一个显示指定组件的对话框
	public static void showDialog(Context content , View view)
	{
		new AlertDialog.Builder(content)
			.setView(view).setCancelable(false)
			.setPositiveButton("确定", null)
			.create()
			.show();
	}

    public static void showProgressDialog(Context context, String message, boolean cancelable) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(context)
                .setMessage(message).setCancelable(cancelable);
        builder.setPositiveButton("确定", null);
        builder.create().show();
    }

	/**
	 * @function: 对屏幕中间显示一个Toast，其内容为msg
	 * */
	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}


}
