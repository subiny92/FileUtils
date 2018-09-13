package org.subiny92.fileutils;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.subiny92.fileutils.sample.DirectoryActivity;
import org.subiny92.fileutils.sample.ZipActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter();
        myAdapter.setArray(array);
        recyclerView.setAdapter(myAdapter);
    }

    /**
     *  RecyclerView Item Set
     */
    private void init() {
        Resources res = getResources();
        array = new ArrayList<>();
        array.add(res.getString(R.string.ids_rv_item_1));
        array.add(res.getString(R.string.ids_rv_item_2));
        array.add(res.getString(R.string.ids_rv_item_3));
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        private ArrayList<String> array = new ArrayList<>();

        public void setArray(ArrayList<String> array) {
            this.array = array;
        }

        class MyHolder extends RecyclerView.ViewHolder {

            TextView tv_contents;

            MyHolder(View itemView) {
                super(itemView);
                tv_contents = (TextView) itemView.findViewById(R.id.tv_contents);
            }
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            String item = array.get(position);
            holder.itemView.setTag(position);
            holder.tv_contents.setText(item);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();

                    Intent intent = null;
                    if (index == 0) {
                        intent = new Intent(MainActivity.this, DirectoryActivity.class);
                    } else if (index == 1) {
                        intent = new Intent(MainActivity.this, ZipActivity.class);
                    }

                    if (intent != null) startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }
    }


}
