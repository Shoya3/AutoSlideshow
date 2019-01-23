package jp.techacademy.shoya.oota.autoslideshow;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import android.view.View;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    Button mNextButton;
    Button mReturnButton;
    Button mAutoButton;
    Cursor cursor;
    ImageView imageView;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (Button) findViewById(R.id.next_button);
        mReturnButton = (Button) findViewById(R.id.return_button);
        mAutoButton = (Button) findViewById(R.id.auto_button);
        imageView = (ImageView) findViewById(R.id.imageView);




        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }



    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            imageView.setImageURI(imageUri);
        }
        //cursor.close();

        //次を押したとき
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //次の写真に行く処理

                //現在表示されている画像のindexの位置を取得


                //indexの次に画像があれば表示する
                if (cursor.moveToNext()) {
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                    imageView.setImageURI(imageUri);

                }else{
                    cursor.moveToFirst();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                    imageView.setImageURI(imageUri);
                }
                //cursor.close();


            }
        });

        //戻るを押したとき
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //前の写真に行く処理
                //
                if (cursor.moveToPrevious()) {
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                    imageView.setImageURI(imageUri);

                }else{
                    cursor.moveToLast();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                    imageView.setImageURI(imageUri);
                }
            }
        });

        mAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //自動再生する処理
                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //indexの次に画像があれば表示する
                                    if (cursor.moveToNext()) {
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                                        imageView.setImageURI(imageUri);

                                    }else{
                                        cursor.moveToFirst();
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                                        imageView.setImageURI(imageUri);
                                    }
                                }
                            });

                        }
                    }, 2000, 2000);
                    mNextButton.setEnabled(false);
                    mReturnButton.setEnabled(false);
                    mAutoButton.setText("停止");
                }else{
                    //タイマーを止める
                    mTimer.cancel();
                    mNextButton.setEnabled(true);
                    mReturnButton.setEnabled(true);
                    mAutoButton.setText("再生");
                    mTimer=null;
                }
            }
        });


    }

}
