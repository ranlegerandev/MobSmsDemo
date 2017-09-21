package com.shiran.mobsmsdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private static final String APP_KEY ="1906c8bda69aa";//Mob短信验证码Key
    private static final String APP_SECRET = "a0bb14448c09bb3a03be461a18c512ff";//Mob AppSecret
    private EditText mEtPhone = null;
    private EditText mEtPassWord = null;
    private EditText mEtCode = null;
    private Button mBtnSubmit = null;
    private CustomCodeButton mBtnCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSMSSDK();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mEtPhone = (EditText) findViewById(R.id.register_et_phone);
        mEtPassWord = (EditText) findViewById(R.id.register_et_pwd);
        mEtCode = (EditText) findViewById(R.id.register_et_code);
        mBtnCode = (CustomCodeButton) findViewById(R.id.register_btn_code);
        mBtnSubmit = (Button) findViewById(R.id.register_btn_submit);
        mBtnCode.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
//        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    /**
     * 初始化MobSDK
     */
    private void initSMSSDK() {
        MobSDK.init(this, APP_KEY, APP_SECRET);
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
               switch (event) {
                   case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: //校验验证码，返回校验的手机和国家代码
                       if (result == SMSSDK.RESULT_COMPLETE) {
                           Log.e(TAG, "验证成功");
                       } else {
                           Log.e(TAG, "验证失败");
                       }
                       break;
                   case SMSSDK.EVENT_GET_VERIFICATION_CODE: //请求发送验证码，无返回
                       if (result == SMSSDK.RESULT_COMPLETE) {
                           Log.e(TAG, "获取验证码成功");
                       } else {
                           Log.e(TAG, "获取验证码失败");
                       }
                       break;
                   default:
                       break;
               }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_code:
                String phone = mEtPhone.getText().toString();
                SMSSDK.getVerificationCode("86", phone);
                if (!TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, "已发送验证码", Toast.LENGTH_SHORT).show();
                    mBtnCode.start();
                } else {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register_btn_submit:
                final String phone1 = mEtPhone.getText().toString();
                final String password = mEtPassWord.getText().toString();

                if (TextUtils.isEmpty(phone1) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "手机号和密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEtPhone.length() < 11) {
                    Toast.makeText(this, "手机号不能少于11位", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEtPassWord.length() < 8) {
                    Toast.makeText(this, "密码不能低于8位", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mEtCode.length() < 4) {
                    Toast.makeText(this, "请输入正确的4位验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //因为测试 没有服务器暂时先让它跳转到临时界面 可根据自己的需求进行逻辑处理
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //防止使用短信验证码产生内存溢出问题
        SMSSDK.unregisterAllEventHandler();
    }
}
