package com.example.android_mysql;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditStudentActivity extends Activity implements OnClickListener {

	private Button btnsave, btndel;
	private EditText edt1, edt2, edt3;

	private ProgressDialog pDialog;

	String sid;

	JSONParser jsonParser = new JSONParser();

	private static final String url_student_detials = "http://192.168.1.4:88/android/student_details.php";

	private static final String url_update_student = "http://192.168.1.4:88/android/update_student.php";

	private static final String url_delete_student = "http://192.168.1.4:88/android/delete_student.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_STUDENT = "student";
	private static final String TAG_ID = "id";
	private static final String TAG_STUID = "stu_id";
	private static final String TAG_NAME = "name";
	private static final String TAG_TEL = "tel";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_layout);

		Intent i = getIntent();
		sid = i.getStringExtra(TAG_ID);

		new GetStudentDetails().execute();

		btnsave = (Button) findViewById(R.id.btnSave);
		btndel = (Button) findViewById(R.id.btnDelete);
		btnsave.setOnClickListener(this);
		btndel.setOnClickListener(this);

	}

	class GetStudentDetails extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditStudentActivity.this);
			pDialog.setMessage("Loading student details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			int success;
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", sid));

				JSONObject json = jsonParser.makeHttpRequest(
						url_student_detials, "GET", params);

				Log.d("Single Student Details", json.toString());

				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					JSONArray studentObj = json.getJSONArray(TAG_STUDENT);

					JSONObject student = studentObj.getJSONObject(0);

					edt1 = (EditText) findViewById(R.id.edt_1);
					edt2 = (EditText) findViewById(R.id.edt_2);
					edt3 = (EditText) findViewById(R.id.edt_3);

					edt1.setText(student.getString(TAG_STUID));
					edt2.setText(student.getString(TAG_NAME));
					edt3.setText(student.getString(TAG_TEL));

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();
		}

	}

	class SaveProductDetails extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditStudentActivity.this);
			pDialog.setMessage("Saving student ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			String stu_id = edt1.getText().toString();
			String name = edt2.getText().toString();
			String tel = edt3.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", sid));
			params.add(new BasicNameValuePair(TAG_STUID, stu_id));
			params.add(new BasicNameValuePair(TAG_NAME, name));
			params.add(new BasicNameValuePair(TAG_TEL, tel));

			JSONObject json = jsonParser.makeHttpRequest(url_update_student,
					"POST", params);
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Intent i = getIntent();
					setResult(100, i);
					finish();
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
		}
	}

	class DeleteStudent extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditStudentActivity.this);
			pDialog.setMessage("Deleting Student...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			int success;
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", sid));

				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_student, "POST", params);

				Log.d("Delete Student", json.toString());

				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent i = getIntent();
					setResult(100, i);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			new SaveProductDetails().execute();
			break;
		case R.id.btnDelete:
			new DeleteStudent().execute();
			break;
		default:
			break;
		}

	}
}
