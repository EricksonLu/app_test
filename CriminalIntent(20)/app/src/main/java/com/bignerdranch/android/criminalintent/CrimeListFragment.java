package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;
    private boolean mSubtitleVisible;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    Fragment的onCreateOptionsMenu方法是由FragmentManager负责调用的。因此，当activity接收到来自操作系统onCreateOptionsMenu方法回调请求时，必须明确告诉
        // FragmentManager：其管理的fragment应接受onCreateOptionsMenu方法的调用指令
//        setHasOptionsMenu这个方法就是告诉FragmentManager有这个fragment由菜单，需要接受选项菜单方法回调
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
//        保留crimelistfragment实例，用来在旋转设备的时候能够保存显示菜单subtitle的设置。
        setRetainInstance(true);
        mSubtitleVisible = false;
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

//        这里是显示操作栏的subtitle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {   
            if (mSubtitleVisible) {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }

//        从这里到return都是注册上下文菜单
//        这个资源是android自带的资源，注意如果资源加载的不对下面有的方法也不能执行会报错
//        在这个Fragment.onCreateView中使用ListFragment管理着的ListView获取资源ID
//        ListFragment也有一个getListView方法，但在onCreateView方法中无法使用，因为在onCreateView方法完成调用并返回视图之前
//        getListView方法返回永远是null值
        ListView listView = (ListView)v.findViewById(android.R.id.list);
//        因为上下文操作栏实现代码所使用的类和方法不支持小于HONEYCOMB的版本
//        这里叫使用编译版本常量，将设置选择模式代码区分开
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
//            由列表视图进入上下文操作模式时，可以开启他的多选模式。多选模式下，上下文操作栏上的任何操作都将同时应用于所有已选视图
//            此处是设置列表视图的选择模式为CHOICE_MODE_MULTIPLE_MODAL
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//            为ListView设置一个实现AbsListView.MultiChoiceModeListener接口的监听器。


            listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

//              该接口包含onItemCheckedStateChanged回调方法，视图在选中或撤销选中时会出发它
//              MultiChoiceModeListener实现了ActionMode.Callback接口，用户屏幕进入上下文操作模式时，会常见一个ActionMode类实例
//              随后进入其生命周期
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                }


//                下面
//                这个是在ActionMode对象创建后调用,也是实例化上下文菜单资源,并显示在上下文操作栏上的任务完成地方
//                在这个方法中,我们是从操作模式而非Activity中获取MenuInflater,操作模式负责对上下文操作栏进行配置.
//                如ActionMode.setTitle方法为上下文操作蓝设置标题
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
//                    这个是创建那个垃圾桶的删除图标
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }


//                在用户选中某个菜单项操作时调用,时响应上下文菜单项操作的地方
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_crime:
//                            mode.setTitle("Fuck");
                            CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
                            CrimeLab crimeLab = CrimeLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    crimeLab.deleteCrime(adapter.getItem(i));
                                }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

//                在onCreateActionMode方法之后,以及当前上下文操作蓝需要刷新显示新数据时调用
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

//                在用户推出上下文操作模式或所选菜单项操作已被相应,从而导致ActionMode对象将要被销毁时调用.
//                默认的实现会导致已选视图被反选,这里也可以实现在上下文操作模式下,响应菜单项操作而引发的响应Fragment更新
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
            
        }

        return v;
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        // get the Crime from the adapter
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
        // start an instance of CrimePagerActivity
//        创建pager
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivityForResult(i, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }


    /**这个叫操作栏*/
//    Fragment的onCreateOptionsMenu方法是由FragmentManager负责调用的。因此，当activity接收到来自操作系统onCreateOptionsMenu方法回调请求时，必须明确告诉
//    FragmentManager：其管理的fragment应接受onCreateOptionsMenu方法的调用指令
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        传入资源ID，填充到menu实例中
        inflater.inflate(R.menu.fragment_crime_list1, menu);
//        如果显示了subtitle就要把第二个菜单项标题设置成hidesubtitle
//        因为这个菜单每点一下呼出键就会被渲染一下，所以才能这么写，这也是所谓的“动态更改菜单项”的一个方法
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @TargetApi(11)
    @Override

//    点选菜单的响应方法
    public boolean onOptionsItemSelected(MenuItem item) {

//        这里有菜单里面有2个备选项，因为一个在xml设置成了never show所以没有办法显示。但是可以通过硬件调用来显示
        switch (item.getItemId()) {

//            如果是添加新的crime，弹出CrimeFragment，把id自动添加
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
//                这里注意这个CrimeLab.get方法，因为CrimeLab是一个单例模式，crimes都是静态的。通过这种方法来对静态变量赋值
                CrimeLab.get(getActivity()).addCrime(crime);
//                创建的crime，而不是创建的pager所以，不会实现pager的效果
                Intent i = new Intent(getActivity(), CrimeActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());

//                这里上网查这个0参数是resquestcode是什么意思？？？？？？？？？？？？？？？？
                startActivityForResult(i, 0);

                return true;
//            这个标题出来
            case R.id.menu_item_show_subtitle:
            	if (getActivity().getActionBar().getSubtitle() == null) {
//                    这里是设置操作懒得subtitle为一个字符串
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true;
                    item.setTitle(R.string.hide_subtitle);
            	}  else {
//                    这里是响应hidesubtitle的操作
            		getActivity().getActionBar().setSubtitle(null);
            		 mSubtitleVisible = false;
//                    设置第二个菜单项为show subtitle
            		item.setTitle(R.string.show_subtitle);
            	}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }




    /**
     * 这个叫上下文菜单，用来长按item时候弹出的删除按钮*/
//    创建上下文菜单。。。长按item后出来的的删除按钮
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        不像onCreateOptionsMenu方法，这个菜单回调方法不接受MenuInflater实例参数。所以要先获得与CrimeListActivity关联的MenuInflater
//        然后调用MenuInflater.inflate方法。
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

//    创建响应长按的item的选项
//    默认情况下，长按视图不会出发上下文菜单的创建，要出发菜单的创建，必须调用一下Fragment方法为浮动上下文菜单登记一个视图
//    @Override
//    public void registerForContextMenu(View view) {
//        super.registerForContextMenu(view);
//    }
//    该方法徐闯入出发上下文菜单的视图
//    在这个CriminalIntent应用里，点击任意列表项都能弹出上下文菜单。所以需要到ListView去登记见CrimeListFragment.onCreateView

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        调用MenuItem的getMenuInfo方法获取要删除的crime对象信息
//        ListView是AdapterView的子类，所以getMenuInfo返回一个AdapterView。AdapterContextMenuInfo实例
//        然后将getMenuInfo方法返回结果进行类型转换，获取列表项在数据集中的位置信息，最后使用列表项的位置，获取要删除的Crime对象
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
//                调用删除CrimeLab里的删除
                CrimeLab.get(getActivity()).deleteCrime(crime);
//                通知ListView的列表，ListView已经改变
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), android.R.layout.simple_list_item_1, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_crime, null);
            }

            // configure the view for this Crime
            Crime c = getItem(position);

            TextView titleTextView =
                (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView dateTextView =
                (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());
            CheckBox solvedCheckBox =
                (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}

