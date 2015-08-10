/*
 * author:eity
 * version:2012-07-24 14:06:00
 * describe:列表数据项点击事件，type类型，postion位置，v对象
 */
package com.gae.listener;

import android.view.View;

public interface ListItemClickListener {
	public void OnListItemClick(String type, int position, View v);
}
