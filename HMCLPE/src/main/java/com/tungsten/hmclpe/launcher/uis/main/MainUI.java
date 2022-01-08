package com.tungsten.hmclpe.launcher.uis.main;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.local.game.GameListBean;
import com.tungsten.hmclpe.launcher.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.util.ArrayList;

public class MainUI extends BaseUI implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public LinearLayout mainUI;

    private LinearLayout startAccountUI;
    private LinearLayout startGameManagerUI;
    private LinearLayout startVersionListUI;
    private LinearLayout startDownloadUI;
    private LinearLayout startMultiPlayerUI;
    private LinearLayout startSettingUI;

    private LinearLayout startGame;
    private TextView launchVersionText;

    private ImageView accountSkinFace;
    private TextView accountName;
    private TextView accountType;

    private ImageView versionIcon;
    private LinearLayout noVersionAlert;
    private TextView currentVersionText;

    public MainUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainUI = activity.findViewById(R.id.ui_main);

        startAccountUI = activity.findViewById(R.id.start_ui_account);
        startGameManagerUI = activity.findViewById(R.id.start_ui_game_manager);
        startVersionListUI = activity.findViewById(R.id.start_ui_version_list);
        startDownloadUI = activity.findViewById(R.id.start_ui_download);
        startMultiPlayerUI = activity.findViewById(R.id.start_ui_multi_player);
        startSettingUI = activity.findViewById(R.id.start_ui_setting);

        startGame = activity.findViewById(R.id.launcher_play_button);
        launchVersionText = activity.findViewById(R.id.launch_version_text);

        accountSkinFace = activity.findViewById(R.id.account_skin_face);
        accountName = activity.findViewById(R.id.account_name_text);
        accountType = activity.findViewById(R.id.account_state_text);

        versionIcon = activity.findViewById(R.id.current_version_icon);
        noVersionAlert = activity.findViewById(R.id.no_version_alert_text);
        currentVersionText = activity.findViewById(R.id.current_version_name_text);

        startAccountUI.setOnClickListener(this);
        startGameManagerUI.setOnClickListener(this);
        startVersionListUI.setOnClickListener(this);
        startDownloadUI.setOnClickListener(this);
        startMultiPlayerUI.setOnClickListener(this);
        startSettingUI.setOnClickListener(this);

        startGame.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(mainUI,activity,context,true);
        activity.hideBarTitle();

        switch (activity.publicGameSetting.account.loginType){
            case 1:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(context.getString(R.string.item_account_type_offline));
                break;
            case 2:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(context.getString(R.string.item_account_type_mojang));
                break;
            case 3:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(context.getString(R.string.item_account_type_microsoft));
                break;
            case 4:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                //accountType.setText(context.getString(R.string.item_account_type_auth_lib));
                break;
            default:
                accountName.setText(context.getString(R.string.launcher_scroll_account_name));
                accountType.setText(context.getString(R.string.launcher_scroll_account_state));
                break;
        }

        ArrayList<GameListBean> gameList = SettingUtils.getLocalVersionInfo(activity.launcherSetting.gameFileDirectory,activity.publicGameSetting.currentVersion);
        ArrayList<String> names = new ArrayList<>();
        String currentVersion = "";
        if (!activity.publicGameSetting.currentVersion.equals("")){
            currentVersion = activity.publicGameSetting.currentVersion.substring(activity.publicGameSetting.currentVersion.lastIndexOf("/") + 1);
        }
        for (int i = 0;i < gameList.size();i++){
            names.add(gameList.get(i).name);
        }
        ArrayAdapter<String> launcherVersionAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,names);
        launcherVersionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner gameVersionSpinner = activity.findViewById(R.id.launcher_spinner_version);
        gameVersionSpinner.setAdapter(launcherVersionAdapter);
        gameVersionSpinner.setSelection(launcherVersionAdapter.getPosition(currentVersion));
        gameVersionSpinner.setOnItemSelectedListener(this);
        if (!currentVersion.equals("") && names.contains(currentVersion)){
            noVersionAlert.setVisibility(View.GONE);
            currentVersionText.setVisibility(View.VISIBLE);
            currentVersionText.setText(currentVersion);
            launchVersionText.setText(currentVersion);
        }
        else {
            noVersionAlert.setVisibility(View.VISIBLE);
            currentVersionText.setVisibility(View.GONE);
            launchVersionText.setText(context.getString(R.string.launcher_button_current_version));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(mainUI,activity,context,true);
    }

    @Override
    public void onClick(View v) {
        if (v == startAccountUI){
            activity.uiManager.switchMainUI(activity.uiManager.accountUI);
        }
        if (v == startGameManagerUI){
            if (noVersionAlert.getVisibility() == View.VISIBLE){
                activity.uiManager.switchMainUI(activity.uiManager.versionListUI);
            }
            else {
                activity.uiManager.switchMainUI(activity.uiManager.gameManagerUI);
            }
        }
        if (v == startVersionListUI){
            activity.uiManager.switchMainUI(activity.uiManager.versionListUI);
        }
        if (v == startDownloadUI){
            activity.uiManager.switchMainUI(activity.uiManager.downloadUI);
        }
        if (v == startMultiPlayerUI){

        }
        if (v == startSettingUI){
            activity.uiManager.switchMainUI(activity.uiManager.settingUI);
        }
        if (v == startGame){

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.publicGameSetting.currentVersion = activity.launcherSetting.gameFileDirectory + "/versions/" + parent.getItemAtPosition(position).toString();
        GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
        currentVersionText.setText(parent.getItemAtPosition(position).toString());
        launchVersionText.setText(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
