package org.talentware.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import org.talentware.android.DetailTextConst;
import org.talentware.android.R;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-9
 * Time: 下午8:20
 * To change this template use File | Settings | File Templates.
 */
public class DetailActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initialView();
    }

    private void initialView() {
        byte detailType = getIntent().getExtras().getByte("DetailType", (byte) 0);

        TextView mTV_Detail = (TextView) findViewById(R.id.tv_detail);

        String Content = "";
        switch (detailType) {
            case 0:
                Content = DetailTextConst.Content1;
                break;
            case 1:
                Content = DetailTextConst.Content2;
                break;
            case 3:
                Content = DetailTextConst.Content3;
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;

        }

        mTV_Detail.setText(Content);
    }
}