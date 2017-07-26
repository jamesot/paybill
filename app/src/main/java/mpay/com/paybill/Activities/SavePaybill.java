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
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.transition.Explode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;
import mpay.com.paybill.Model.MyShortcuts;
import mpay.com.paybill.Model.Paybill;
import mpay.com.paybill.Model.VideoUpload;
import mpay.com.paybill.R;

/**
 * Created by stephineosoro on 02/08/16.
 */


public class SavePaybill extends AppCompatActivity {
    @Bind(R.id.btn_submit)
    Button submit;
    @Bind(R.id.input_name)
    EditText etName;
    @Bind(R.id.paybill_number)
    EditText etPaybill;
    @Bind(R.id.input_email)
    EditText etEmail;
    @Bind(R.id.editText)
    EditText editTextName;
    @Bind(R.id.description)
    EditText description;
    @Bind(R.id.buttonChooseVideo)
    Button buttonChooseVideo;
    boolean tru;
    private static final int SELECT_VIDEO = 3;

    private String selectedPath;
    static String name = "";
    private Button buttonChoose;
    private static Bitmap bitmap;
    static boolean imagetrue = false, videotrue = false;
    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = MyShortcuts.baseURL() + "upload.php";
    private String UPLOAD_URL_P = MyShortcuts.baseURL() + "UploadPaybill.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String type = "  Paybill Number  ";
    ProgressDialog uploading;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_save);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Paybill");

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        setTitle("Add Paybill");
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioOptions);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                      // checkedId is the RadioButton selected

                                                      RadioButton rb = (RadioButton) findViewById(checkedId);
                                                      if (rb.getText().toString().equals("Paybill Number")) {
                                                          type = "  Paybill Number  ";
                                                          Log.e("Paybill", "bills");
                                                      } else {
                                                          type = "Buy Goods/Services";
                                                          Log.e("Buy Gooods", "goods");
                                                      }

                                                  }


                                              }
        );
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        buttonChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                if (MyShortcuts.hasInternetConnected(getBaseContext())) {
                    /*if (imagetrue) {
                        if (videotrue) {
//                            uploadVideo();
                        }*/
                    uploadImage();
                   /* } else {
                        MyShortcuts.showToast("Please upload a photo!", getBaseContext());
                    }*/


                }

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

        Paybill paybill = new Paybill();
        paybill.setName(etName.getText().toString());
        paybill.setEmail(etEmail.getText().toString());
        paybill.setPaybill_number(etPaybill.getText().toString());
        paybill.setSent("false");
        paybill.setHistory("false");
        paybill.setType(type);
        paybill.setDescription(description.getText().toString());
        paybill.save();

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

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String descriptiondetail = description.getText().toString();
        String paybill = etPaybill.getText().toString();


        if (name.isEmpty()) {
            etName.setError("at least 3 characters");
            valid = false;
        } else {
            etName.setError(null);
        }

       /* if (descriptiondetail.isEmpty()) {
            description.setError("Enter short description");
            valid = false;
        } else {
            description.setError(null);
        }
*/
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter a valid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (paybill.isEmpty()) {
            etPaybill.setError("enter paybill number");
            valid = false;
        } else {
            etPaybill.setError(null);
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

//            tru = true;

//            if (tru) {
                Runnable runable;
                new Handler(Looper.getMainLooper()).post(
                        runable =new Runnable() {
                    @Override
                    public void run() {
                        MyShortcuts.showToast("Uploading without a photo", getBaseContext());
                        UploadPaybill();
//                        tru=false;

                    }
                });

//            }

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
                String email = etEmail.getText().toString();
                String paybillno = etPaybill.getText().toString();
                String pname = etName.getText().toString();
                String shortD = description.getText().toString();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();


//                TODO uname(image_name) is the description and I should add video field in the paybill database field
                //Adding parameters
                params.put("image", image);
                params.put("image_name", uname);
                params.put("name", pname);
                params.put("email", email);
                params.put("paybill_number", paybillno);
                params.put("video", name);
                params.put("description", shortD);
                params.put("history", "false");
                params.put("sent", "false");
                params.put("type", type);


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
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
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
                uploading = ProgressDialog.show(SavePaybill.this, "Uploading Video", "Please wait...", false, false);
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
                Intent intent = new Intent(getBaseContext(), SavePaybill.class);
                startActivity(intent);

                return true;
            case R.id.adhome:
                Intent intent3 = new Intent(getBaseContext(), SaveHomeAd.class);
                startActivity(intent3);

                return true;
            case R.id.adnotice:
                Intent intent4 = new Intent(getBaseContext(), UploadNoticeBoard.class);
                startActivity(intent4);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SavePaybill.this);

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

    /*
        compile 'com.daimajia.slider:library:1.1.5@aar'
        compile 'com.daimajia.androidanimations:library:1.0.3@aar'*/
    private void UploadPaybill() {
        loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL_P,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        if (loading != null) {
                            loading.dismiss();
                        }
                        Log.e("response", s);
                      /*  Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);*/
                        finish();
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
//                String image = getStringImage(bitmap);

                //Getting Image Name
                String uname = editTextName.getText().toString().trim();
                String email = etEmail.getText().toString();
                String paybillno = etPaybill.getText().toString();
                String pname = etName.getText().toString();
                String shortD = description.getText().toString();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();


//                TODO uname(image_name) is the description and I should add video field in the paybill database field
                //Adding parameters

                params.put("name", pname);
                params.put("email", email);
                params.put("paybill_number", paybillno);
                params.put("video", name);
                params.put("description", shortD);
                params.put("history", "false");
                params.put("sent", "false");
                params.put("type", type);


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

}
