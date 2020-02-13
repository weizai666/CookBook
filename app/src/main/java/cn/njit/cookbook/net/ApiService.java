package cn.njit.cookbook.net;

import cn.njit.cookbook.model.BaseBean;
import cn.njit.cookbook.model.BaseResultBean;
import cn.njit.cookbook.model.FoodBean;
import cn.njit.cookbook.model.FoodTypeBean;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService
{
    //获取菜式列表
    @GET("cook/category")
    Flowable<BaseBean<List<FoodTypeBean>>> getFoodTypeData(@Query("key") String key);

    //获取菜品图文列表
    @GET("cook/index")
    Flowable<BaseResultBean<List<FoodBean>>> getFoodListData(@Query("key") String key, @Query("cid") int id);

}
