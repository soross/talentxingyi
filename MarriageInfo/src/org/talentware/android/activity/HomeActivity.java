package org.talentware.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import org.talentware.android.R;
import org.talentware.android.adapter.GridAdapter;

public class HomeActivity extends Activity {

    private static final int COLUMN_NUM = 3;

    private GridView mGridView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialView();
    }

    private void initialView() {
        mGridView = (GridView) findViewById(R.id.gv_home);
        mGridView.setNumColumns(COLUMN_NUM);
//        setPaddingAndSpacing();
//        this.initMyAPPScreenSize();
        GridAdapter homeAdapter = new GridAdapter(this);

        mGridView.setAdapter(homeAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent mIntent = new Intent();
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        bundle.putByte("DetailType", (byte) position);
                        mIntent.setClass(HomeActivity.this, DetailActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 1:
                        bundle.putByte("DetailType", (byte) position);
                        mIntent.setClass(HomeActivity.this, DetailActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 2:
                        bundle.putByte("DetailType", (byte) position);
                        mIntent.setClass(HomeActivity.this, DetailActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                }
            }
        });
    }
}
