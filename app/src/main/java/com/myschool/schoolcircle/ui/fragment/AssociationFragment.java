package com.myschool.schoolcircle.ui.fragment;

import android.view.View;
import android.widget.ListView;

import com.myschool.schoolcircle.adapter.AssociationAdapter;
import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.entity.Association;
import com.myschool.schoolcircle.main.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class AssociationFragment extends BaseFragment {

    @Bind(R.id.lv_contact_association)
    ListView lvContactAssociation;

    private View view;

    private List<Association> associations;
    private AssociationAdapter adapter;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_fragment_association, null);
//        ButterKnife.bind(this, view);
//
//        initView();
//        return view;
//    }

    protected void initView() {
        associations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            associations.add(new Association("社团名"+i,"社团通知"+i,R.mipmap.ic_big_head));
        }
        adapter = new AssociationAdapter(activity,associations,R.layout.item_contact_association);
        lvContactAssociation.setAdapter(adapter);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_association;
    }

}
