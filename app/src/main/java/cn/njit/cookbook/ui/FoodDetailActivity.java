package cn.njit.cookbook.ui;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import cn.njit.cookbook.App;
import cn.njit.cookbook.R;
import cn.njit.cookbook.config.Config;
import cn.njit.cookbook.model.BaseBean;
import cn.njit.cookbook.model.CommentBean;
import cn.njit.cookbook.model.FoodBean;
import cn.njit.cookbook.net.FoodPresenter;
import cn.njit.cookbook.ui.adapter.BaseDelegateAdapter;
import cn.njit.cookbook.ui.adapter.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;



public class FoodDetailActivity extends BaseActivity<FoodPresenter>
{
    @Bind(R.id.img_collect)
    ImageView mImgCollect;
    @Bind(R.id.ed_comment)
    EditText mEdComment;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private FoodBean mData = null;

    private DelegateAdapter mAdapter;
    private BaseDelegateAdapter mDetailAdapter, mStepAdapter, mCommentsTitleAdapter, mCommentsAdapter;

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_food_detail;
    }

    @Override
    protected void setStatusBarColor()
    {

    }

    @Override
    protected FoodPresenter createPresenter()
    {
        return new FoodPresenter(this, this);
    }

    @Override
    protected void initView(Bundle savedInstanceState)
    {
        super.initView(savedInstanceState);
        mData = getIntent().getParcelableExtra(FoodDetailActivity.class.getSimpleName());
        setLeftBack();
        mImgCollect.setImageResource((mData != null && mData.isCollected()) ? R.drawable.tab_faved : R.drawable.tab_fav);
        setTitle(mData != null ? mData.getTitle() : "");
        VirtualLayoutManager manager = new VirtualLayoutManager(mActivity);
        manager.setInitialPrefetchItemCount(5);
        mRecyclerView.setLayoutManager(manager);
        //关闭动效提升效率
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //Item高度固定，避免浪费资源
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(5);

        mAdapter = new DelegateAdapter(manager);
        mRecyclerView.setAdapter(mAdapter);

        initAdapter();
    }

    private void initAdapter()
    {
        /*菜品介绍*/
        mDetailAdapter = new BaseDelegateAdapter<FoodBean>(this, R.layout.layout_food_detail_introduce)
        {
            @Override
            public void convert(BaseViewHolder holder, int position, FoodBean data)
            {
                List<String> list = data.getAlbums();
                holder.setCircleImageResource((list != null && list.size() != 0) ? list.get(0) : "",
                        R.dimen.dp_62, R.id.item_image)
                        .setText(data.getTitle(), R.id.item_title)
                        .setText(data.getTags(), R.id.item_descript)
                        .setText(data.getImtro(), R.id.item_text);
                TextView introduce = holder.getView(R.id.layout).findViewById(R.id.item_introduce);
                introduce.setText(getString(R.string.tv_introduce));
            }
        };
        mAdapter.addAdapter(mDetailAdapter);
        mDetailAdapter.setData(mData);

        /*菜品做法步骤*/
        mStepAdapter = new BaseDelegateAdapter<FoodBean>(this, R.layout.layout_food_detail_steps)
        {
            @Override
            public void convert(BaseViewHolder holder, int position, FoodBean data)
            {
                //TODO 照理说这块应该放在子线程去生成做法字符串
                //TODO 但是我不想改了
                StringBuilder builder = new StringBuilder();
                if (data.getSteps() != null && (!data.getSteps().isEmpty()))
                {
                    for (FoodBean.StepsBean step : data.getSteps())
                    {
                        builder.append(step.getStep())
                                .append("\n");
                    }
                }
                holder.setText(builder, R.id.item_text);
                TextView introduce = holder.getView(R.id.layout).findViewById(R.id.item_introduce);
                introduce.setText(getString(R.string.tv_steps));
            }
        };
        mAdapter.addAdapter(mStepAdapter);
        mStepAdapter.setData(mData);

        /*评论标题*/
        mCommentsTitleAdapter = new BaseDelegateAdapter<String>(this, R.layout.layout_detail_introduce)
        {
            @Override
            public void convert(BaseViewHolder holder, int position, String s)
            {
                holder.setText(s, R.id.item_introduce);
            }
        };
        mAdapter.addAdapter(mCommentsTitleAdapter);
        mCommentsTitleAdapter.setData(getString(R.string.tv_comments));

        /*评论*/
        mCommentsAdapter = new BaseDelegateAdapter<CommentBean>(this, R.layout.item_comment)
        {
            @Override
            public void convert(BaseViewHolder holder, int position, CommentBean data)
            {
                holder.setCircleImageResource(data.getAvartar(), R.dimen.dp_30, R.id.item_image)
                        .setText(data.getNickName(), R.id.item_name)
                        .setText(data.getContent(), R.id.item_descript);
            }
        };
        mAdapter.addAdapter(mCommentsAdapter);
    }


    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.getCommentsData();
    }


    @Override
    public void showData(Object o)
    {
        super.showData(o);
        if (o instanceof List)
        {
            List<CommentBean> list = (List<CommentBean>) o;
            mCommentsAdapter.setData(list);
        }
    }


    @OnClick({R.id.img_collect, R.id.tv_commit})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.img_collect:
                final boolean collect = (mData != null && mData.isCollected());
                showMsgDialog(getString(collect ? R.string.dialog_cancel_collect : R.string.dialog_collect), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (collect)
                        {
                            alert(getString(R.string.alert_cancel_success));
                            mData.cancel();
                        } else
                        {
                            alert(getString(R.string.alert_collect_success));
                            mData.collect();
                        }
                        mImgCollect.setImageResource((mData != null && mData.isCollected()) ? R.drawable.tab_faved : R.drawable.tab_fav);
                        BaseBean data = new BaseBean();
                        data.setMsg(CollectActivity.class.getSimpleName());
                        EventBus.getDefault().post(data);
                        disMsgDialog();

                    }
                });
                break;
            case R.id.tv_commit:
                String descript = mEdComment.getText().toString();
                if (TextUtils.isEmpty(descript))
                {
                    alert(getString(R.string.alert_enter_comments));
                    return;
                }
                CommentBean data = new CommentBean();
                data.setAvartar(Config.BASE_HEADE);
                data.setNickName(App.getCacheUser() != null ? App.getCacheUser().getAccount() : "");
                data.setContent(descript);
                mCommentsAdapter.setData(mCommentsAdapter.getData().size(), data);
                mEdComment.setText("");
                mRecyclerView.scrollToPosition(mCommentsAdapter.getData().size());
                break;
        }
    }
}
