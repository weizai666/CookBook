package cn.njit.cookbook.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import cn.njit.cookbook.App;
import cn.njit.cookbook.config.Config;
import cn.njit.cookbook.model.BaseBean;
import cn.njit.cookbook.model.BaseResultBean;
import cn.njit.cookbook.model.CommentBean;
import cn.njit.cookbook.model.FoodBean;
import cn.njit.cookbook.model.FoodTypeBean;
import cn.njit.cookbook.sql.CommentBeanDao;
import cn.njit.cookbook.sql.FoodBeanDao;
import cn.njit.cookbook.utils.RxUtils;
import cn.njit.cookbook.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;


public class FoodPresenter extends BasePresenter<BaseView>
{
    public FoodPresenter(BaseView view, LifecycleProvider<ActivityEvent> provider)
    {
        super(view, provider);
    }


    /*获取菜谱的分类*/
    public void getFoodTypeData()
    {
        add(
                RetrofitManager.getManager().getService().getFoodTypeData(Config.HTTP_NET_KEY)
                        .compose(this.<BaseBean<List<FoodTypeBean>>>loadTransformer())
                        .compose(RxUtils.<List<FoodTypeBean>>handleBaseData())
                        .subscribeWith(new BaseObserver<List<FoodTypeBean>>(mView,true)
                        {
                            @Override
                            public void onNext(List<FoodTypeBean> list)
                            {
                                if (mView != null)
                                {
                                    mView.showData(list);
                                }
                            }
                        })
        );
    }

    /*获取菜品图文列表*/
    public void getFoodListData(int id)
    {
        add(
                RetrofitManager.getManager().getService().getFoodListData(Config.HTTP_NET_KEY, id)
                        .compose(this.<BaseResultBean<List<FoodBean>>>loadTransformer())
                        .compose(RxUtils.<List<FoodBean>>handleBaseResult())
                        .subscribeWith(new BaseObserver<List<FoodBean>>(mView)
                        {
                            @Override
                            public void onNext(List<FoodBean> list)
                            {
                                if (mView != null)
                                {
                                    mView.showData(list);
                                }
                            }
                        })
        );
    }

    /*获取评论信息*/
    public void getCommentsData()
    {
        add(
                Flowable.create(new FlowableOnSubscribe<List<CommentBean>>()
                {
                    @Override
                    public void subscribe(FlowableEmitter<List<CommentBean>> e) throws Exception
                    {
                        CommentBeanDao dao = App.mSession.getCommentBeanDao();
                        List<CommentBean> list = dao.loadAll();
                        if (list == null || list.isEmpty())
                        {
                            String json = StringUtils.getJson(App.getContext(), "CommentBean.json");
                            Gson gson = new Gson();
                            list = gson.fromJson(json, new TypeToken<List<CommentBean>>()
                            {
                            }.getType());
                        }
                        e.onNext(list);
                        e.onComplete();
                    }
                }, BackpressureStrategy.BUFFER)
                        .compose(this.<List<CommentBean>>loadTransformer())
                        .subscribeWith(new BaseObserver<List<CommentBean>>(mView)
                        {
                            @Override
                            public void onNext(List<CommentBean> list)
                            {
                                if (mView != null)
                                {
                                    mView.showData(list);
                                }
                            }
                        })
        );
    }

    /*获取收藏数据*/
    public void getCollectedData()
    {
        add(
                Flowable.create(new FlowableOnSubscribe<List<FoodBean>>()
                {
                    @Override
                    public void subscribe(FlowableEmitter<List<FoodBean>> e) throws Exception
                    {
                        FoodBeanDao dao = App.mSession.getFoodBeanDao();
                        List<FoodBean> list = dao.loadAll();
                        e.onNext(list != null ? list : new ArrayList<FoodBean>());
                        e.onComplete();
                    }
                }, BackpressureStrategy.BUFFER)
                .compose(this.<List<FoodBean>>loadTransformer())
                .subscribeWith(new BaseObserver<List<FoodBean>>(mView)
                {
                    @Override
                    public void onNext(List<FoodBean> list)
                    {
                        if (mView != null)
                        {
                            mView.showData(list);
                        }
                    }
                })
        );
    }
}
