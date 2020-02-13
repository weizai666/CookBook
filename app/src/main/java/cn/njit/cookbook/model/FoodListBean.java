package cn.njit.cookbook.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class FoodListBean
{
    private String totalNum;
    private String pn;
    private String rn;
    @SerializedName("data")
    private List<FoodBean> data;


    public String getTotalNum()
    {
        return totalNum;
    }

    public void setTotalNum(String totalNum)
    {
        this.totalNum = totalNum;
    }

    public String getPn()
    {
        return pn;
    }

    public void setPn(String pn)
    {
        this.pn = pn;
    }

    public String getRn()
    {
        return rn;
    }

    public void setRn(String rn)
    {
        this.rn = rn;
    }

    public List<FoodBean> getData()
    {
        return data;
    }

    public void setData(List<FoodBean> data)
    {
        this.data = data;
    }
}
