package br.net.ccbr.ccbrowser;

import android.os.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.view.View.*;
import android.app.*;
import android.view.inputmethod.*;
import android.content.*;
import android.widget.TextView.*;
import java.util.regex.*;
import android.util.*;

import androidx.appcompat.app.AppCompatActivity;

public class home extends AppCompatActivity
{
	public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
		
		final EditText editText = (EditText) findViewById(R.id.urlbar);
		TextView mtxt = (TextView) findViewById(R.id.mainTxT);
		
		hideSoftKeyboard(home.this, editText);
		Typeface custom_font = Typeface.createFromAsset(getAssets(),  "RobotoSlab-Regular.ttf");
		editText.setTypeface(custom_font);
		mtxt.setTypeface(custom_font);
		
		editText.setOnEditorActionListener(new OnEditorActionListener() {
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_GO) {
						
						
						EditText urltext = (EditText) findViewById(R.id.urlbar);
						
						
						if(Patterns.WEB_URL.matcher(urltext.getText().toString().toLowerCase()).matches()) {
							String url = urltext.getText().toString();
							if (url.startsWith("http://")) {
								Intent i = new Intent(home.this, MainActivity.class);
								i.putExtra("url", urltext.getText().toString());
								startActivity(i); 
								finish();
							} else if(url.startsWith("https://")) {
								Intent i = new Intent(home.this, MainActivity.class);
								i.putExtra("url", urltext.getText().toString());
								startActivity(i); 
								finish();
							} else {
								Intent i = new Intent(home.this, MainActivity.class);
								i.putExtra("url", "http://" + urltext.getText().toString());
								startActivity(i); 
								finish();
							}
						} else {
							// SharedPreferences prefr = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE); 
							// String restoredText = prefr.getString("search_engine", null);
							//if (restoredText != null) {
							// wv.loadUrl(prefr.getString("search_engine", "http://duckduckgo.com/?q=") + urltext.getText().toString());
							// }
							Intent i = new Intent(home.this, MainActivity.class);
							i.putExtra("url", "https://www.google.com/search?q=" + urltext.getText().toString());
							startActivity(i); 
							finish();
						}

						return true;
					}
					return false;
				}
			});
	}
	
	public static void hideSoftKeyboard (Activity activity, View view) 
	{
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
	
	public void cancel(View v){
		EditText urb = (EditText) findViewById(R.id.urlbar);
		urb.setText("");
	}
}
