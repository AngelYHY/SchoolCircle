package com.myschool.schoolcircle.bean;

import com.myschool.schoolcircle.entity.Tb_dynamic;

import java.util.List;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public class DynamicTest {

    /**
     * list : [{"_id":106,"avatar":"http://oap9ibq03.bkt.clouddn.com/FqTSGEX7is5HeOi-4FKeEI17Yrgn","username":"lll123","realName":"俊生","textContent":"阿鲁巴黎欧莱雅\n---------\n[转发]子旭：成长，改变总是不可避免，但要不忘初心。","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/FhZkSE9fCj5AcZ2AYRiMGhSelNb9\"]","images":["http://oap9ibq03.bkt.clouddn.com/FhZkSE9fCj5AcZ2AYRiMGhSelNb9"],"datetime":"2016-9-1 14:58","likeNum":0,"commentNum":0,"schoolName":"福建师范大学闽南科技学院"},{"_id":105,"avatar":"http://oap9ibq03.bkt.clouddn.com/FqTSGEX7is5HeOi-4FKeEI17Yrgn","username":"lll123","realName":"俊生","textContent":"哈哈\n---------\n[转发]子旭：。。。","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/FlW71Ip7OAE4sYzQ_Yw-6zxhN5hr\"]","images":["http://oap9ibq03.bkt.clouddn.com/FlW71Ip7OAE4sYzQ_Yw-6zxhN5hr"],"datetime":"2016-9-1 14:57","likeNum":0,"commentNum":0,"schoolName":"福建师范大学闽南科技学院"},{"_id":104,"avatar":"http://oap9ibq03.bkt.clouddn.com/FkFxeKO3EkUuBjxHxDA3-wzj-Fgy","username":"zhengrj","realName":"仁杰","textContent":"tgygyvygyvybbubybyb","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/FjEbfLTfTEV0NomglWpwRLOfc8Rk\",\"http://oap9ibq03.bkt.clouddn.com/Fk4YRKYCvW6Q1_pHl6vQPHuIkgFR\"]","images":["http://oap9ibq03.bkt.clouddn.com/FjEbfLTfTEV0NomglWpwRLOfc8Rk","http://oap9ibq03.bkt.clouddn.com/Fk4YRKYCvW6Q1_pHl6vQPHuIkgFR"],"datetime":"2016-9-1 14:24","likeNum":0,"commentNum":0,"schoolName":"福建师范大学闽南科技学院"},{"_id":103,"avatar":"http://oap9ibq03.bkt.clouddn.com/FkFxeKO3EkUuBjxHxDA3-wzj-Fgy","username":"zhengrj","realName":"仁杰","textContent":"yctvyv\n---------\n[转发]子旭：。。。。。。。。。\n---------\n[转发]仁杰：哈哈(ಡωಡ)hiahiahia ","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/Fkc61RWa2WdiRH382ROUlV3tdC8m\",\"http://oap9ibq03.bkt.clouddn.com/FnvrfYfzor7W2NTvJdZPH5IW1AfS\"]","images":["http://oap9ibq03.bkt.clouddn.com/Fkc61RWa2WdiRH382ROUlV3tdC8m","http://oap9ibq03.bkt.clouddn.com/FnvrfYfzor7W2NTvJdZPH5IW1AfS"],"datetime":"2016-9-1 14:22","likeNum":0,"commentNum":1,"schoolName":"福建师范大学闽南科技学院"},{"_id":102,"avatar":"http://oap9ibq03.bkt.clouddn.com/Fjc5mfwfq53aSKWecM2XA_bnQqJi","username":"zixu666","realName":"子旭","textContent":"。。。","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/FlW71Ip7OAE4sYzQ_Yw-6zxhN5hr\"]","images":["http://oap9ibq03.bkt.clouddn.com/FlW71Ip7OAE4sYzQ_Yw-6zxhN5hr"],"datetime":"2016-9-1 08:59","likeNum":1,"commentNum":1,"schoolName":"高博软件技术学院"},{"_id":101,"avatar":"http://oap9ibq03.bkt.clouddn.com/Fjc5mfwfq53aSKWecM2XA_bnQqJi","username":"zixu666","realName":"子旭","textContent":"。。。。。。。。。\n---------\n[转发]仁杰：哈哈(ಡωಡ)hiahiahia ","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/Fkc61RWa2WdiRH382ROUlV3tdC8m\",\"http://oap9ibq03.bkt.clouddn.com/FnvrfYfzor7W2NTvJdZPH5IW1AfS\"]","images":["http://oap9ibq03.bkt.clouddn.com/Fkc61RWa2WdiRH382ROUlV3tdC8m","http://oap9ibq03.bkt.clouddn.com/FnvrfYfzor7W2NTvJdZPH5IW1AfS"],"datetime":"2016-8-31 09:13","likeNum":1,"commentNum":7,"schoolName":"高博软件技术学院"},{"_id":98,"avatar":"http://oap9ibq03.bkt.clouddn.com/Fjc5mfwfq53aSKWecM2XA_bnQqJi","username":"zixu666","realName":"子旭","textContent":"PK快起来","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/Fp_vcCdYKP-8FC4ZHv7BiJQouEIF\"]","images":["http://oap9ibq03.bkt.clouddn.com/Fp_vcCdYKP-8FC4ZHv7BiJQouEIF"],"datetime":"2016-8-30 18:05","likeNum":0,"commentNum":2,"schoolName":"高博软件技术学院"},{"_id":85,"avatar":"http://oap9ibq03.bkt.clouddn.com/FkFxeKO3EkUuBjxHxDA3-wzj-Fgy","username":"zhengrj","realName":"仁杰","textContent":"哈哈(ಡωಡ)hiahiahia ","imageList":"[\"http://oap9ibq03.bkt.clouddn.com/Fkc61RWa2WdiRH382ROUlV3tdC8m\",\"http://oap9ibq03.bkt.clouddn.com/FnvrfYfzor7W2NTvJdZPH5IW1AfS\"]","images":["http://oap9ibq03.bkt.clouddn.com/Fkc61RWa2WdiRH382ROUlV3tdC8m","http://oap9ibq03.bkt.clouddn.com/FnvrfYfzor7W2NTvJdZPH5IW1AfS"],"datetime":"2016-8-30 00:42","likeNum":0,"commentNum":0,"schoolName":"福建师范大学闽南科技学院"}]
     * hasMore : true
     * errorMsg :
     * success : true
     */

    private boolean hasMore;
    private String errorMsg;
    private boolean success;
    private List<Tb_dynamic> list;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Tb_dynamic> getList() {
        return list;
    }

    public void setList(List<Tb_dynamic> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "DynamicTest{" +
                "hasMore=" + hasMore +
                ", errorMsg='" + errorMsg + '\'' +
                ", success=" + success +
                ", list=" + list +
                '}';
    }
}
