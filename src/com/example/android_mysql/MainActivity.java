package com.example.android_mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView listview;
	private ProgressDialog dialog;
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> studentList;
	private static String url_all_student = "http://192.168.1.4:88/android/show_student.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_STUDENT = "student";
	private static final String TAG_ID = "id";
	private static final String TAG_STUID = "stu_id";
	private static final String TAG_NAME = "name";
	private static final String TAG_TEL = "tel";

	JSONArray student = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new LoadAllStudent().execute();
		studentList = new ArrayList<HashMap<String, String>>();
		listview = (ListView) findViewById(R.id.listView1);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String sid = ((TextView) view.findViewById(R.id.ColID))
						.getText().toString();

				Intent in = new Intent(getApplicationContext(),
						EditStudentActivity.class);
				in.putExtra(TAG_ID, sid);
				startActivityForResult(in, 100);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	class LoadAllStudent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Loading student. Please waite...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			JSONObject json = jParser.makeHttpRequest(url_all_student, "GET",
					pairs);
			Log.d("All Student", json.toString());
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					student = json.getJSONArray(TAG_STUDENT);

					for (int i = 0; i < student.length(); i++) {
						JSONObject c = student.getJSONObject(i);

						String id = c.getString(TAG_ID);
						String stu_id = c.getString(TAG_STUID);
						String name = c.getString(TAG_NAME);
						String tel = c.getString(TAG_TEL);

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_ID, id);
						map.put(TAG_STUID, stu_id);
						map.put(TAG_NAME, name);
						map.put(TAG_TEL, tel);

						studentList.add(map);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ListAdapter adapter = new SimpleAdapter(MainActivity.this,
							studentList, R.layout.item_student, new String[] {
									TAG_ID, TAG_STUID, TAG_NAME, TAG_TEL },
							new int[] { R.id.ColID, R.id.ColStuID,
									R.id.ColName, R.id.ColTel });
					listview.setAdapter(adapter);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
	        case R.id.add_student:
	        	Intent intent = new Intent(this, AddStudentActivity.class);
	            this.startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
