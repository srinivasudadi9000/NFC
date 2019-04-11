package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EmployEnroll extends Activity implements View.OnClickListener {
    TextView header_tv;
    EditText id_et, emp_id, emp_name, department_name, designation;
    ImageView save_img, emp_img;
    Uri iv_url2;
    File otherImagefile2 = null;
    String pic = "null", clicked = "not";
    int O_IMAGE2 = 2;
    AlertDialog show;
    ProgressDialog progressdilaog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employenroll);
        id_et = findViewById(R.id.id_et);
        emp_id = findViewById(R.id.emp_id);
        emp_name = findViewById(R.id.emp_name);
        department_name = findViewById(R.id.department_name);
        designation = findViewById(R.id.designation);

        save_img = findViewById(R.id.save_img);
        save_img.setOnClickListener(this);
        emp_img = findViewById(R.id.emp_img);
        emp_img.setOnClickListener(this);
        id_et.setText(getIntent().getStringExtra("payload").toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_img:
                Toast.makeText(getBaseContext(), "Successfully Registration Completed Thankyou ", Toast.LENGTH_SHORT).show();
                //finish();
                try {
                    progressdilaog = new ProgressDialog(EmployEnroll.this);
                    progressdilaog.setTitle("");
                    progressdilaog.setMessage("Please wait");
                    progressdilaog.setCancelable(false);
                    progressdilaog.show();
                    Getlogin(emp_id.getText().toString(),
                            emp_name.getText().toString(), id_et.getText().toString(), department_name.getText().toString(),
                            designation.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.emp_img:
                clicked = "clicked";
                pic = "clicked";
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/RecceImages/");
                myDir.mkdirs();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                otherImagefile2 = new File(myDir,
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
                //  iv_url2 = Uri.fromFile(otherImagefile2);

                iv_url2 = FileProvider.getUriForFile(getApplicationContext(),
                        getApplication().getPackageName() + ".provider", otherImagefile2);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, iv_url2);
                startActivityForResult(intent, O_IMAGE2);
                break;
        }
    }

    private String getStringImage(String path) {
        String encodedImage = null;
        try {

            if (path != null) {
                Bitmap mBitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] byteArrayImage = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            }
        } catch (Exception e) {
            Log.d("unable to read image", e.toString());
//            Toast.makeText(MainActivity1.this,"Retake picture",Toast.LENGTH_SHORT).show();
        }
        return encodedImage;
    }

    public void Getlogin(String empId, String nfcid, String empname, String designation,
                         String department) throws IOException {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("empId", empId)
                .add("empnfcId", nfcid)
                .add("name", empname)
                .add("email", "srinivasu@gmail.com")
                .add("phone", "8885270193")
                .add("designation", designation)
                .add("department", department)
                // .add("picture", getStringImage(otherImagefile2.getAbsolutePath()))
                .add("picture", "")
                .build();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "multipart/application/json")
                .url("http://primal.reassuregroups.com/api/Enrollment/Employee?")
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                progressdilaog.dismiss();
                Log.d("result dadi", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "IMEI Number or password doesnt exist", Toast.LENGTH_SHORT).show();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                progressdilaog.dismiss();
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            Toast.makeText(getBaseContext(), "IMEI Number or password doesnt exist", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("result dadi", response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    //  pd.dismiss();
                    String responseBody = response.body().string();
                    Log.d("result", responseBody.toString());
                    final JSONObject obj;
                    showAlert(responseBody.toString());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Camera request code......................" + resultCode);
        if (requestCode == O_IMAGE2 && resultCode == RESULT_OK) {
            try {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 8;
                opt.inMutable = true;
                Bitmap bmImage = BitmapFactory.decodeFile(otherImagefile2.getPath().toString(), opt);
                emp_img.setScaleType(ImageView.ScaleType.FIT_XY);
                emp_img.setImageBitmap(bmImage);
                compressImage(otherImagefile2.getAbsolutePath().toString());
            } catch (Exception e) {
                Log.e("msg", e.getMessage());
            }
        }
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        // String filename = getFilename();
        try {
            out = new FileOutputStream(imageUri);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return imageUri;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public void showAlert(String msg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployEnroll.this);
        LayoutInflater inflater = ((Activity) EmployEnroll.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.warning_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show = alertDialog.show();

            }
        });


        Button alertButton = (Button) alertView.findViewById(R.id.btn_ok);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

}
