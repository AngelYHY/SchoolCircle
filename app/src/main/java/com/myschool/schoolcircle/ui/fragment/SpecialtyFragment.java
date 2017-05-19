package com.myschool.schoolcircle.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.myschool.schoolcircle.adapter.PersonAdapter;
import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.entity.Person;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class SpecialtyFragment extends BaseFragment {

    @Bind(R.id.lv_contact_specialty)
    ListView lvContactSpecialty;
    private View view;

    private List<Person> persons;
    private PersonAdapter adapter;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_fragment_specialty, null);
//        ButterKnife.bind(this, view);
//
//        initView();
//        return view;
//    }

    protected void initView() {
        persons = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            persons.add(new Person("名字"+i,"个性签名"+i,R.mipmap.ic_big_head));
        }
        adapter = new PersonAdapter(activity,persons,R.layout.item_contact_specialty);
        lvContactSpecialty.setAdapter(adapter);
        lvContactSpecialty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastUtil.showToast(activity,i+"", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_specialty;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
