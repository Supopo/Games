package com.xxx.games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.xxx.games.widget.ITurntableListener;
import com.xxx.games.widget.TurntableView;

import java.util.ArrayList;

/**
 * 真心话大冒险
 */
public class TruthOrDareActivity extends AppCompatActivity {

    private TurntableView mTurntable;
    private ImageView ivNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truth_or_dare);
        initView();
        initData();
    }

    private void initView() {
        mTurntable = findViewById(R.id.turntable);
        ivNode = findViewById(R.id.iv_node);
        ivNode.setOnClickListener(lis -> {
            mTurntable.startRotate(new ITurntableListener() {
                @Override
                public void onStart() {
                    Toast.makeText(TruthOrDareActivity.this, "开始抽奖", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEnd(int position, String name) {
                    Toast.makeText(TruthOrDareActivity.this, "玩家" +
                            name + "中奖了", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void initData() {
        int num = 12;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            names.add("第" + (i + 1));
        }
        mTurntable.setDatas(num, names);
    }
}