package com.androiddevbook.onyourbike.chapter5.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androiddevbook.onyourbike.chapter5.helpers.SQLiteHelper;

/**
 * Routes
 * 
 * Routes for the "On Your Bike" application.
 * 
 * Copyright [2013] Pearson Education, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author androiddevbook.com
 * @version 1.0
 */
public class Routes {
//该方法返回一个Rote类型的列表，接受两个参数
    static public List<Route> getAll(SQLiteHelper helper,SQLiteDatabase database) {
        List<Route> routes = new ArrayList<Route>();
//        创建数据库游标
        Cursor cursor = database.rawQuery("select * from routes", null);
//        移动到该游标的第一个元素
        cursor.moveToFirst();
//        继续移动游标，遍历所有元素，并转化为Route实例，并通过cursorToRoute逐个将其添加到路线列表中。
        while (!cursor.isAfterLast()) {
            Route route = cursorToRoute(cursor);
            routes.add(route);
            cursor.moveToNext();
        }
//        关闭游标
        cursor.close();
//        返回已填充的路线列表
        return routes;
    }

//    将routes表字段转化为Route属性
    static private Route cursorToRoute(Cursor cursor) {
        Route route = new Route();
        route.setid(cursor.getInt(cursor.getColumnIndex("_id")));
        route.name = cursor.getString(cursor.getColumnIndex("name"));
        route.notes = cursor.getString(cursor.getColumnIndex("notes"));

        return route;
    }
}
