package cn.njit.cookbook.ui;

import android.os.Bundle;

import android.view.View;

import cn.njit.cookbook.R;
import cn.njit.cookbook.model.BaseBean;
import cn.njit.cookbook.model.FoodBean;
import cn.njit.cookbook.net.FoodPresenter;
import cn.njit.cookbook.ui.adapter.BaseAdapter;
import cn.njit.cookbook.ui.adapter.BaseViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;


public class CollectActivity extends BaseActivity<FoodPresenter>
{
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private BaseAdapter mAdapter;

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_collect;
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
        EventBus.getDefault().register(this);
        setLeftBack();
        setTitle(getString(R.string.tv_collect));
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setInitialPrefetchItemCount(5);
        mRecyclerView.setLayoutManager(manager);
        //关闭动效提升效率
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //Item高度固定，避免浪费资源
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(5);

        mAdapter = new BaseAdapter<FoodBean>(this, R.layout.item_food_list)
        {
            @Override
            public void convert(BaseViewHolder holder, int position, final FoodBean data)
            {
                holder.setText(data.getTitle(), R.id.item_title)
                        .setText(data.getImtro(), R.id.item_descript)
                        .setText("阅读  " + data.getId(), R.id.item_read)
                        .setRoundImageResource(data.getImageUrls(),R.dimen.dp_5, R.id.item_image)
                        .itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        openActivity(FoodDetailActivity.class, data);
                    }
                });
            }
        };
        mAdapter.bindRecyclerView(mRecyclerView);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.getCollectedData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(BaseBean data)
    {
        if (data.getMsg().equals(CollectActivity.class.getSimpleName()))
        {
            initData();
        }
    }


    @Override
    public void showData(Object o)
    {
        super.showData(o);
        if (o instanceof List)
        {
            List<FoodBean> list = (List<FoodBean>) o;
            mAdapter.setData(list);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
