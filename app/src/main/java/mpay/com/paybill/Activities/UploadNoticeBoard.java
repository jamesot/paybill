package mpay.com.paybill.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import mpay.com.paybill.Model.MyShortcuts;
import mpay.com.paybill.Model.VideoUpload;
import mpay.com.paybill.R;

public class UploadNoticeBoard extends AppCompatActivity {

    @Bind(R.id.btn_submit)
    Button submit;

    @Bind(R.id.editText)
    EditText editTextName;

    @Bind(R.id.buttonChoose)
    Button buttonChoose;

    private static final int SELECT_VIDEO = 3;

    private String selectedPath;
    static String name = "";
    private static Bitmap bitmap;
    static boolean imagetrue = false, imagetrue1 = false, imagetrue2 = false, imagetrue3 = false,imagetrue4 = false,imagetrue5 = false, imagetrue6 = false, videotrue = false;
    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = MyShortcuts.baseURL() + "uploadNotice.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String type = "  Paybill Number  ";
    ProgressDialog uploading;
    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_notice_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();

            }
        });



    }


    private void save() {
        if (!validate()) {
            onSignupFailed();
            /*mProgressView.stopAnim();
            ImageView imageView = (ImageView) findViewById(R.id.img);
            imageView.setVisibility(View.VISIBLE);
              mProgressView.setVisibility(View.INVISIBLE);*/

            submit.setEnabled(true);

            return;
        }


        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        MyShortcuts.showToast("Paybill saved! ", getBaseContext());
        if (MyShortcuts.hasInternetConnected(getBaseContext())) {

        } else {
            MyShortcuts.setDefaults("unsent", "true", getBaseContext());
        }
    }

    public void onSignupFailed() {
        MyShortcuts.showToast("Saving paybill failed because of the above error", getBaseContext());

        submit.setEnabled(true);

    }

    public boolean validate() {

        /*ImageView imageView = (ImageView) findViewById(R.id.img);
        imageView.setVisibility(View.INVISIBLE);
        _signupButton.setEnabled(false);
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.startAnim();
        mProgressView.bringToFront();*/

//        getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);
        boolean valid = true;

        String name = editTextName.getText().toString();


        if (name.isEmpty()) {
            editTextName.setError("at least 3 characters");
            valid = false;
        } else {
            editTextName.setError(null);
        }


        return valid;
    }


    /*
    * Image Handling
    * */
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String encodedImage = null;
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            Log.e("Bitmap", e.toString());

            /*Handler handler = new Handler() {
                public void handleMessage(Message msg) {

                            Toast.makeText(getBaseContext(),"Please Add a photo",Toast.LENGTH_SHORT).show();

                    super.handleMessage(msg);
                }
            };*/

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    MyShortcuts.showToast("Please Add a photo", getBaseContext());
                }
            });

//            MyShortcuts.showToast("Please Add a photo", getBaseContext());
        }
        return encodedImage;
    }

    private void uploadImage() {
        Log.e("name verified", name);
        //Showing the progress dialog
        loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        if (loading != null) {
                            loading.dismiss();
                        }
                        Log.e("response", s);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        //Showing toast message of the response
//                        Toast.makeText(SavePaybill.this, s, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        if (loading != null) {
                            loading.dismiss();
                        }
                        //Showing toast
//                        Toast.makeText(SavePaybill.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error response", volleyError.getMessage() + "");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String uname = editTextName.getText().toString().trim();


                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();


//                TODO uname(image_name) is the description and I should add video field in the paybill database field
                //Adding parameters
                params.put("image", image);
                params.put("image_name", "advertis");
//                Log.e("params",params.toString());
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    private void uploadEachImage(final String name) {
        Log.e("name verified", name);
        /*imagetrue1=false;
        imagetrue2=false;
        imagetrue3=false;
        imagetrue4=false;
        imagetrue5=false;
        imagetrue6=false;*/
        //Showing the progress dialog
        loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        if (loading != null) {
                            loading.dismiss();
                        }
                        Log.e("response", s);
                        MyShortcuts.showToast("uploaded successfully!", getBaseContext());



//                        if (imagetrue1 && imagetrue2 && imagetrue3&& imagetrue4&& imagetrue5&& imagetrue6) {


//                        }
                        //Showing toast message of the response
//                        Toast.makeText(SavePaybill.this, s, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        if (loading != null) {
                            loading.dismiss();
                        }
                        //Showing toast
//                        Toast.makeText(SavePaybill.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error response", volleyError.getMessage() + "");
                        MyShortcuts.showToast("error uploading, check your internet connection!", getBaseContext());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String uname = editTextName.getText().toString().trim();


                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();


//                TODO uname(image_name) is the description and I should add video field in the paybill database field
                //Adding parameters
                params.put("image", image);
                params.put("image_name", name);
//                Log.e("params",params.toString());
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

   /* private void showFileChooser(String name) {
        Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);



    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                imagetrue = true;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Log.e("image chosen", bitmap.toString());
                TextView tv = (TextView) findViewById(R.id.imageChosen);
                tv.setVisibility(View.VISIBLE);

//                if (imagetrue1) {
                    uploadEachImage("notice_"+System.currentTimeMillis());
               /* } else if (imagetrue2) {
                    uploadEachImage("advertis1");
                } else if (imagetrue3) {
                    uploadEachImage("advertis2");
                } else if (imagetrue4) {
                    uploadEachImage("advertis3");
                } else if (imagetrue5) {
                    uploadEachImage("advertis4");
                } else if (imagetrue6) {
                    uploadEachImage("advertis5");
                }*/


                //Setting the Bitmap to ImageView
//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                String[] each = selectedPath.split("/");
                name = MyShortcuts.baseURL() + "images/" + each[each.length - 1];
                Log.e("Name of file", name);
                videotrue = true;
                uploadVideo();
                TextView tv = (TextView) findViewById(R.id.imageChosenVideo);
                tv.setVisibility(View.VISIBLE);
//                tv.setText(selectedPath);
            }
        }
    }


    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }


    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadNoticeBoard.this, "Uploading Video", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                /*textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());*/

            }

            @Override
            protected String doInBackground(Void... params) {
                VideoUpload u = new VideoUpload();
                String msg = u.uploadVideo(selectedPath);
//                /storage/extSdCard/USA/20160310_153751.mp4
                Log.e("message video", msg);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }


    private void dismissProgressDialog() {
        if (uploading != null && uploading.isShowing()) {
            uploading.dismiss();
        }

        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        dismissProgressDialog();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            this.slidingMenu.toggle();
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                Intent intent2 = new Intent(getBaseContext(), Delete.class);
                startActivity(intent2);
                return true;

            case R.id.login:
                Intent intent = new Intent(getBaseContext(), SaveHomeAd.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadNoticeBoard.this);

        alertDialogBuilder.setTitle("Add another Image?");
        alertDialogBuilder.setMessage("choose Image");

        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setOrientation(LinearLayout.VERTICAL);


//        final EditText input = new EditText(getBaseContext());

        final Button button = new Button(getBaseContext());
        button.setTextColor(Color.BLACK);
        button.setHintTextColor(Color.BLACK);
        button.setHint("Choose Image");
        layout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }


        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

    }


}
