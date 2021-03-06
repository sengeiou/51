package com.likeit.as51scholarship.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.likeit.as51scholarship.R;
import com.likeit.as51scholarship.adapters.SchoolFilterAdapter;
import com.likeit.as51scholarship.configs.AppConfig;
import com.likeit.as51scholarship.http.HttpUtil;
import com.likeit.as51scholarship.model.SchoolAttributeNameVo;
import com.likeit.as51scholarship.model.SchoolAttributeVo;
import com.likeit.as51scholarship.model.SchoolFilterEventBean;
import com.likeit.as51scholarship.utils.ToastUtil;
import com.likeit.as51scholarship.view.MessageEvent;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SchoolFilterActivity extends Container {
    @BindView(R.id.backBtn)
    Button btBack;
    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.tv_totle)
    TextView tvTotle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_reset)
    TextView tvReset;
    @BindView(R.id.school_filter_listview)
    ListView mListView;
    List<SchoolAttributeNameVo> data;
    private SchoolFilterAdapter mAdapter;
    private String total;
    private String attrName;
    private String attrId;
    private String stage, country, area, lang, nature, style, toefl, toeic, yasi;
    private List<SchoolFilterEventBean> attrData;
    private String filterId;
    private SchoolFilterEventBean mSchoolFilterEventBean;
    private String check01;
    private String check02;
    private boolean check03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_filter);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        filterId = intent.getStringExtra("filterId");
        initTotal();//初始化总数
        initData();//初始化筛选数据
        initView();
    }

    private void initTotal() {
        String url = AppConfig.LIKEIT_SCHOOL_FILTR_COUNT;
        RequestParams params = new RequestParams();
        params.put("ukey", ukey);
        params.put("stage", stage);
        params.put("country", country);
        params.put("area", area);
        params.put("lang", lang);
        params.put("nature", nature);
        params.put("style", style);
        params.put("toefl", toefl);
        params.put("toeic", toeic);
        params.put("yasi", yasi);
        HttpUtil.post(url, params, new HttpUtil.RequestListener() {
            @Override
            public void success(String response) {
                Log.d("TAG", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String code = obj.optString("code");
                    String message = obj.optString("message");
                    if ("1".equals(code)) {
                        total = obj.optJSONObject("data").optString("total");
                        showTotal();
                    } else {
                        total = "0";
                        showTotal();
                      //  ToastUtil.showS(mContext, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable e) {

            }
        });

    }

    private void showTotal() {
        tvTotle.setText("共" + total + "个高校符合条件>>");
    }

    private void initView() {
        tvHeader.setText("筛选院校");
        tvRight.setText("跳过");
        data = new ArrayList<SchoolAttributeNameVo>();
        mAdapter = new SchoolFilterAdapter(mContext, data);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initData() {
        String url = AppConfig.LIKEIT_SCHOOL_FILTR_PARAM;
        RequestParams params = new RequestParams();
        params.put("ukey", ukey);
        HttpUtil.post(url, params, new HttpUtil.RequestListener() {
            @Override
            public void success(String response) {
                Log.d("TAG", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String code = obj.getString("code");
                    if ("1".equals(code)) {
                        JSONArray json = obj.optJSONArray("data");
                        refreshAttrs(json);
                        Log.d("TAG", "data-->" + json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Throwable e) {
                ToastUtil.showS(mContext, "网络异常！");
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void initTotal01() {
        String url = AppConfig.LIKEIT_SCHOOL_FILTR_COUNT;
        RequestParams params = new RequestParams();
        params.put("ukey", ukey);
        HttpUtil.post(url, params, new HttpUtil.RequestListener() {
            @Override
            public void success(String response) {
                Log.d("TAG", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String code = obj.optString("code");
                    String message = obj.optString("message");
                    if ("1".equals(code)) {
                        total = obj.optJSONObject("data").optString("total");
                        showTotal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable e) {

            }
        });
    }

    private void refreshAttrs(JSONArray json) {
        data.clear();
        for (int i = 0; i < json.length(); i++) {
            SchoolAttributeNameVo schoolAttriName = new SchoolAttributeNameVo();
            JSONObject obj = json.optJSONObject(i);
            schoolAttriName.setName(obj.optString("title"));
            List<SchoolAttributeVo> list = new ArrayList<SchoolAttributeVo>();
            JSONArray array = obj.optJSONArray("type");
            for (int j = 0; j < array.length(); j++) {
                JSONObject object = array.optJSONObject(j);
                SchoolAttributeVo vo = new SchoolAttributeVo();
                vo.setAttr_name(object.optString("name"));
                vo.setAttr_id(object.optString("id"));
                list.add(vo);
            }
            schoolAttriName.setSchoolAttr(list);
            data.add(schoolAttriName);
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.backBtn, R.id.tv_reset, R.id.tv_totle, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:

                if ("2".equals(filterId)) {
                    onBackPressed();
                } else {
                    toActivityFinish(MainActivity.class);
                }
                break;
            case R.id.tv_reset:
                //onBackPressed();
                if (data != null && !data.isEmpty()) {
                    refresh();
                } else {
                    return;
                }
                //  refresh();
                break;
            case R.id.tv_totle:

                Intent intentSchool = new Intent(mContext, SearchSchoolActivity.class);
                intentSchool.putExtra("stage", stage);
                intentSchool.putExtra("country", country);
                intentSchool.putExtra("area", area);
                intentSchool.putExtra("lang", lang);
                intentSchool.putExtra("nature", nature);
                intentSchool.putExtra("style", style);
                intentSchool.putExtra("toefl", toefl);
                intentSchool.putExtra("toeic", toeic);
                intentSchool.putExtra("yasi", yasi);
                if ("1".equals(filterId)) {
                    startActivity(intentSchool);
                } else if ("2".equals(filterId)) {
                    startActivity(intentSchool);
                    finish();
                }

                break;
            case R.id.tv_right:
                Intent intentSchool01 = new Intent(mContext, SearchSchoolActivity.class);
                intentSchool01.putExtra("stage", stage);
                intentSchool01.putExtra("country", country);
                intentSchool01.putExtra("area", area);
                intentSchool01.putExtra("lang", lang);
                intentSchool01.putExtra("nature", nature);
                intentSchool01.putExtra("style", style);
                intentSchool01.putExtra("toefl", toefl);
                intentSchool01.putExtra("toeic", toeic);
                intentSchool01.putExtra("yasi", yasi);
                if ("1".equals(filterId)) {
                    startActivity(intentSchool01);
                } else if ("2".equals(filterId)) {
                    startActivity(intentSchool01);
                    finish();
                }
                break;
        }
    }

    private void refresh() {
        if (data != null && !data.isEmpty() && attrData != null && !attrData.isEmpty()) {
            data.clear();
            attrData.clear();
            stage = "";
            country = "";
            lang = "";
            nature = "";
            style = "";
            toefl = "";
            toeic = "";
            yasi = "";
            try {
                initData();
                initTotal01();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMoonEvent(MessageEvent messageEvent) {
        //tv_message.setText(messageEvent.getMessage());
        // ToastUtil.showS(mContext, messageEvent.getMessage());
        // Log.d("TAG",messageEvent.getMessage());
        attrData = new ArrayList<>();
        mSchoolFilterEventBean = new SchoolFilterEventBean();
        // String str = "";
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).getValues().size(); j++) {
                if (data.get(i).getValues().get(j).isChecked()) {
                    mSchoolFilterEventBean.setAttrId(data.get(i).getValues().get(j).getAttr_id());
                    mSchoolFilterEventBean.setAttrName(data.get(i).getName());
                    attrData.add(mSchoolFilterEventBean);
                    attrName = mSchoolFilterEventBean.getAttrName();
                    if ("攻读学位".equals(attrName)) {
                        stage = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("国家".equals(attrName)) {
                        country = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("上课语言".equals(attrName)) {
                        lang = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("学校性质".equals(attrName)) {
                        nature = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("学校类型".equals(attrName)) {
                        style = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("托福(可不选)".equals(attrName)) {
                        toefl = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("雅思(可不选)".equals(attrName)) {
                        yasi = data.get(i).getValues().get(j).getAttr_id();
                    } else if ("托业(可不选)".equals(attrName)) {
                        toeic = data.get(i).getValues().get(j).getAttr_id();
                    }
                }

            }

        }

        initTotal();
        // String attr=messageEvent.getMessage();
      Log.d("TAG222", "stage-->" + stage + "country-->" + country + "area-->" + area + "lang-->" + lang + "nature-->" + nature + "style-->" + style + "toefl-->" + toefl + "toeic-->" + toeic + "yasi-->" + yasi);
//        Log.d("TAG333", attrData.get(0).
//                getAttrName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册：一般在事件view销毁的时候
        EventBus.getDefault().unregister(this);
    }
}
