package cn.njit.cookbook.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import cn.njit.cookbook.R;
import cn.njit.cookbook.model.FoodTypeBean;
import cn.njit.cookbook.net.FoodPresenter;
import cn.njit.cookbook.ui.adapter.BaseAdapter;
import cn.njit.cookbook.ui.adapter.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<FoodPresenter>
{

    @Bind(R.id.left)
    RecyclerView mLabel;
    @Bind(R.id.right)
    RecyclerView mRecyclerView;

    /*错误布局*/
    private ViewStub mError;

    /*适配器*/
    private BaseAdapter mLabelAdapter, mAdapter;

    /*是否正在加载数据*/
    private boolean isLoading = false;


    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_main;
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


        initLabelAdapter();
        initAdapter();
    }


    /*初始化菜谱分类适配器*/
    private void initLabelAdapter()
    {
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setInitialPrefetchItemCount(5);
        mLabel.setLayoutManager(manager);
        //关闭动效提升效率
        ((SimpleItemAnimator) mLabel.getItemAnimator()).setSupportsChangeAnimations(false);
        //Item高度固定，避免浪费资源
        mLabel.setHasFixedSize(true);
        mLabel.setItemViewCacheSize(5);

        mLabelAdapter = new BaseAdapter<FoodTypeBean>(this, R.layout.item_label)
        {
            @Override
            public void convert(BaseViewHolder holder, final int position, final FoodTypeBean data)
            {
                holder.setText(data.getName(), R.id.item_title)
                        .setTextColor(lastPosition == position ? Color.WHITE : getResources().getColor(R.color.textUnSelected), R.id.item_title)
                        .setBackgroundResource(lastPosition == position ? R.drawable.shape_contract_label : 0, R.id.item_title)
                        .itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (lastPosition == position) return;
                        lastPosition = position;
                        notifyDataSetChanged();
                        mAdapter.setData(data.getList());
                    }
                });
            }

            @Override
            protected int setLastPosition()
            {
                return 0;
            }
        };

        mLabelAdapter.bindRecyclerView(mLabel);
    }


    /*初始化适配器*/
    private void initAdapter()
    {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setInitialPrefetchItemCount(5);
        mRecyclerView.setLayoutManager(manager);
        //关闭动效提升效率
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //Item高度固定，避免浪费资源
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(5);

        mAdapter = new BaseAdapter<FoodTypeBean.ListBean>(this, R.layout.item_right)
        {
            @Override
            public void convert(BaseViewHolder holder, int position, final FoodTypeBean.ListBean data)
            {
                holder.setText(data.getName(), R.id.item_title)
                        .itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        openActivity(FoodListActivity.class, data);
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
        mPresenter.getFoodTypeData();
    }

    @OnClick(R.id.tv_collect)
    public void onViewClicked()
    {
        openActivity(CollectActivity.class, null);
    }

    @Override
    public void showData(Object o)
    {
        super.showData(o);
        if (mError != null)
        {
            mError.setVisibility(View.GONE);
        }
        if (o instanceof List)
        {
            List<FoodTypeBean> list = (List<FoodTypeBean>) o;
            mLabelAdapter.setData((List<FoodTypeBean>) o);
            mAdapter.setData((list != null && list.size() != 0) ? list.get(0).getList() : null);
        }
    }

    @Override
    public void showError()
    {
       /* super.showError();
        if (mError != null)
        {
            mError = findViewById(R.id.error);
            mError.inflate();
            TextView retry = findViewById(R.id.layout_error).findViewById(R.id.tv);
            retry.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initData();
                }
            });
        } else
        {
            mError.setVisibility(View.VISIBLE);
        }*/

        openActivity(FoodDetailActivity.class, null);
      //  openActivity(FoodListActivity.class, null);

    }
}
