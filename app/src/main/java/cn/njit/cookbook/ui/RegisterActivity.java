package cn.njit.cookbook.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.njit.cookbook.R;
import cn.njit.cookbook.model.UserBean;
import cn.njit.cookbook.net.BasePresenter;
import cn.njit.cookbook.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;



public class RegisterActivity extends BaseActivity implements TextWatcher
{
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.tv_code)
    TextView mTvCode;
    @Bind(R.id.et_code)
    EditText mEtCode;
    @Bind(R.id.et_passWord)
    EditText mEtPassWord;
    @Bind(R.id.tv_action)
    TextView mTvAction;
    @Bind(R.id.tv_question)
    TextView mTvQuestion;

    /*当前是否正在进行验证码倒计时*/
    private boolean isCoding = false;

    /*短信倒计时器*/
    private Timer mTimer = new Timer();

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_register;
    }

    @Override
    protected void setStatusBarColor()
    {

    }

    @Override
    protected BasePresenter createPresenter()
    {
        return null;
    }


    @Override
    protected void initView(Bundle savedInstanceState)
    {
        super.initView(savedInstanceState);
        setLeftBack();

        mEtPhone.addTextChangedListener(this);
        mEtCode.addTextChangedListener(this);
        mEtPassWord.addTextChangedListener(this);
        mTvCode.setClickable(false);
        mTvAction.setClickable(false);
    }

    @OnClick({R.id.tv_code, R.id.tv_action, R.id.tv_toast})
    public void onViewClicked(View view)
    {
        if (isFastDoubleClick()) return;
        String account = mEtPhone.getText().toString();
        if (TextUtils.isEmpty(account) || (!StringUtils.isMobile(account)))
        {
            alert(getString(R.string.alert_enter_phone));
            return;
        }
        switch (view.getId())
        {
            case R.id.tv_code://获取手机验证码
                mTimer.start();
                break;
            case R.id.tv_action://确认注册账号
                String code = mEtCode.getText().toString();
                if (TextUtils.isEmpty(code) || code.length() < 6)
                {
                    alert(getString(R.string.alert_enter_code));
                    return;
                }

                String pass = mEtPassWord.getText().toString();
                if (TextUtils.isEmpty(pass) || pass.length() < 6)
                {
                    alert(getString(R.string.alert_enter_passWord));
                    return;
                }
                UserBean user = new UserBean();
                user.setAccount(account);
                user.setPassWord(pass);
                if (user.register())
                {
                    alert(getString(R.string.alert_register_success));
                    doFinish(200);
                }else
                {
                    alert(getString(R.string.alert_register_failed));
                }
                break;
            case R.id.tv_toast://用户协议
                alert(getString(R.string.alert_have_no_function));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        setCodeBtn();
        setActionBtn();
    }

    /*设置获取验证码按钮UI样式*/
    private void setCodeBtn()
    {
        String account = mEtPhone.getText().toString();
        boolean click = (!TextUtils.isEmpty(account)) && account.length() == 11 && (!isCoding);
        mTvCode.setClickable(click);
        mTvCode.setBackgroundColor(getResources().getColor(click ? R.color.color : R.color.textBG));
        mTvCode.setTextColor(getResources().getColor(click ? R.color.black : R.color.textUnSelected));
    }

    /*设置充值密码按钮UI演示*/
    private void setActionBtn()
    {
        String account = mEtPhone.getText().toString();
        String code = mEtCode.getText().toString();
        String pass = mEtPassWord.getText().toString();
        boolean isClick = (!TextUtils.isEmpty(account)) && (!TextUtils.isEmpty(code)) && (!TextUtils.isEmpty(pass));
        mTvAction.setClickable(isClick);
        mTvAction.setBackgroundColor(getResources().getColor(isClick ? R.color.color : R.color.textBG));
        mTvAction.setTextColor(getResources().getColor(isClick ? R.color.black : R.color.textUnSelected));
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mTimer != null)
        {
            mTimer.cancel();
        }
    }

    /*短信验证码倒计时器*/
    public class Timer extends CountDownTimer
    {
        public Timer()
        {
            super(60 * 1000, 1000);
        }

        @Override
        public void onTick(long times)
        {
            StringBuilder builder = new StringBuilder();
            builder.append(times / 1000).append("s");
            mTvCode.setText(builder.toString());
            isCoding = true;
            setCodeBtn();
        }

        @Override
        public void onFinish()
        {
            isCoding = false;
            setCodeBtn();
            mTvCode.setText(getString(R.string.tv_getCode));
        }
    }
}

