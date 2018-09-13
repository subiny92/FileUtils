package org.subiny92.fileutils.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.subiny92.fileutils.R;
import org.subiny92.fileutils.utils.FileUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DirectoryActivity extends AppCompatActivity {

    private EditText etFolderName;
    private ArrayList<String> folderArray;
    private DirectoryAdapter mDirectoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        /// 뷰 객체 참조
        etFolderName = (EditText) findViewById(R.id.et_folder_name);
        Button btnCreate = (Button) findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(onCreateListener);

        folderArray = new ArrayList<>();
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mDirectoryAdapter = new DirectoryAdapter();
        rv.setAdapter(mDirectoryAdapter);

        /// ItemTouchHelp 객체 생성
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        /// ItemTouchHelper 와 RecyclerView 연결
        itemTouchHelper.attachToRecyclerView(rv);

    }

    View.OnClickListener onCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = etFolderName.getText().toString();
            etFolderName.setText("");
            /// 외부 저장 파일 쓰기 권한 체크하여 허용 여부 반환 (허용 == 0, 거부 == -1)
            int permission = ActivityCompat.checkSelfPermission(DirectoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    /// 파일 생성
                    createFile(name);
                }
                else {
                    ActivityCompat.requestPermissions(DirectoryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x001);
                }
            } else {
                createFile(name);
            }
        }
    };

    private void createFile(String folderName) {
        if (!folderName.equals("")) {
            /// 폴더명 설정
            FileUtils.getInstance().setFolderName(folderName);
        }

        /// 폴더 생성
        FileUtils.getInstance().createFileDirectory();

        try {
            folderArray.add(FileUtils.getInstance().getFile().getName());
            mDirectoryAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *  remove Item with onSwiped()
     */
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            try {
                // 폴더 삭제
                FileUtils.getInstance().removeDirectory(folderArray.get(position));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            folderArray.remove(position);
            mDirectoryAdapter.notifyItemRemoved(position);
        }
    };

    class DirectoryAdapter extends RecyclerView.Adapter <DirectoryAdapter.DirectoryViewHolder> {

        class DirectoryViewHolder extends RecyclerView.ViewHolder {

            TextView tvDirectoryName;

            DirectoryViewHolder(View itemView) {
                super(itemView);
                tvDirectoryName = (TextView) itemView.findViewById(R.id.tv_directory_name);
            }
        }

        @Override
        public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_directory, parent, false);
            return new DirectoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DirectoryViewHolder holder, int position) {
            String name = folderArray.get(position);
            holder.tvDirectoryName.setText(name);
        }

        @Override
        public int getItemCount() {
            return folderArray.size();
        }
    }
}
