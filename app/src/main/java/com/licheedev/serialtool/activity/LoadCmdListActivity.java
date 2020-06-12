package com.licheedev.serialtool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

import com.bumptech.glide.Glide;
import com.licheedev.myutils.LogPlus;
import com.licheedev.serialtool.MainHttp;
import com.licheedev.serialtool.R;
import com.licheedev.serialtool.activity.base.BaseActivity;
import com.licheedev.serialtool.comn.SerialPortManager;
import com.licheedev.serialtool.dialog.SingleInputDialog;
import com.licheedev.serialtool.model.Command;
import com.licheedev.serialtool.model.server_api.AppCommandListResponse;
import com.licheedev.serialtool.model.server_api.AppInfoResponse;
import com.licheedev.serialtool.model.server_api.BaseResponse;
import com.licheedev.serialtool.util.BaseListAdapter;
import com.licheedev.serialtool.util.CommandParser;
import com.licheedev.serialtool.util.DeviceHelper;
import com.licheedev.serialtool.util.ListViewHolder;
import com.licheedev.serialtool.util.ToastUtil;

import cn.dlc.commonlibrary.okgo.rx.OkObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class LoadCmdListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final int REQUEST_FILE = 233;
    @BindView(R.id.btn_load_list)
    Button mBtnLoadList;
    @BindView(R.id.list_view)
    ListView mListView;

    @BindView(R.id.tv_app_name)
    TextView mTvAppName;

    @BindView(R.id.tv_sid)
    TextView mTvSid;

    @BindView(R.id.iv_app_icon)
    ImageView mIvAppIcon;

    private ExFilePicker mFilePicker;
    private CommandParser mParser;
    private InnerAdapter mAdapter;

    private SingleInputDialog singleInputDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_load_cmd_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar(true, "加载命令列表");
        initFilePickers();

        mParser = new CommandParser();

        mListView.setOnItemClickListener(this);
        mAdapter = new InnerAdapter();
        mListView.setAdapter(mAdapter);

        /*默认显示APP本地图标,加载了服务器端配置之后显示服务器端图标*/
        mIvAppIcon.setImageResource(R.mipmap.ic_launcher_round);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                File file = new File(result.getPath(), result.getNames().get(0));

                mParser.rxParse(file).subscribe(new Observer<List<Command>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Command> commands) {
                        mAdapter.setNewData(commands);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogPlus.e("解析失败", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                ToastUtil.showOne(this, "未选择文件");
            }
        }
    }

    /**
     * 初始化文件/文件夹选择器
     */
    private void initFilePickers() {

        mFilePicker = new ExFilePicker();
        mFilePicker.setNewFolderButtonDisabled(true);
        mFilePicker.setQuitButtonEnabled(true);
        mFilePicker.setUseFirstItemAsUpEnabled(true);
        mFilePicker.setCanChooseOnlyOneItem(true);
        mFilePicker.setShowOnlyExtensions("txt");
        mFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
    }

    @OnClick({R.id.btn_load_list, R.id.tv_sid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_load_list:
                mFilePicker.start(this, REQUEST_FILE);
                break;
            case R.id.tv_sid:
                showAppSidInputDialog();
                break;
        }
    }

    private void showAppSidInputDialog() {
        if (singleInputDialog == null) {
            singleInputDialog = new SingleInputDialog(this);
            singleInputDialog.setOnConfirmClickListener(input -> {
                if (TextUtils.isEmpty(input)) {
                    return;
                }
                DeviceHelper.saveAppSid(input);
                loadAppInfo(input);
            });
        }
        singleInputDialog.show("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Command item = mAdapter.getItem(position);
        SerialPortManager.instance().sendCommand(item.getCommand());
    }

    private static class InnerAdapter extends BaseListAdapter<Command> {
        @Override
        protected void inflateItem(ListViewHolder holder, int position) {

            Command item = getItem(position);

            String comment = String.valueOf(position + 1);
            comment = TextUtils.isEmpty(item.getComment()) ? comment : comment + " " + item.getComment();

            holder.setText(R.id.tv_comment, comment);
            holder.setText(R.id.tv_command, item.getCommand());
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_load_command_list;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCommandsFromServer();
    }

    private void loadCommandsFromServer() {
        AppInfoResponse appInfo = DeviceHelper.getAppInfo();
        if (appInfo == null) {
            showAppSidInputDialog();
            return;
        }
        setShowAppInfo(appInfo);
        MainHttp.get()
                .getAppCommandList(appInfo.data.id, 1, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OkObserver<AppCommandListResponse>() {
                    @Override
                    public void onSuccess(AppCommandListResponse response) {
                        parseToCommands(response);
                    }

                    @Override
                    public void onFailure(String s, Throwable throwable) {
                        LogPlus.d(s);
                    }
                });
    }

    private void parseToCommands(AppCommandListResponse response) {
        if (response.data.data == null || response.data.data.size() == 0) {
            return;
        }
        List<Command> commands = new ArrayList<>(response.data.data.size());
        for (AppCommandListResponse.DataBeanX.DataBean dataBean : response.data.data) {
            Command command = new Command();
            command.setCommand(dataBean.command_hex);
            command.setComment(dataBean.comment);
            commands.add(command);
        }
        this.mAdapter.setNewData(commands);
    }


    private void loadAppInfo(String sid) {
        MainHttp.get()
                .getAppInfo(sid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OkObserver<AppInfoResponse>() {
                    @Override
                    public void onSuccess(AppInfoResponse response) {
                        DeviceHelper.saveAppInfo(response);
                        setShowAppInfo(response);
                        loadCommandsFromServer();
                    }

                    @Override
                    public void onFailure(String s, Throwable throwable) {
                        LogPlus.d(s);
                    }
                });
    }

    private void setShowAppInfo(AppInfoResponse response) {
        if (response == null || response.data == null) {
            return;
        }
        mTvAppName.setText(response.data.name);
        mTvSid.setText(response.data.sid);
        Glide.with(LoadCmdListActivity.this).load(response.data.icon).into(mIvAppIcon);
    }
}
