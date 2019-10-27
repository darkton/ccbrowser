package br.net.ccbr.ccbrowser;

import android.content.*;
import android.app.*;
import android.os.*;
import android.net.*;

public class RedirectWeb extends Activity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);

		Uri url = getIntent().getData();

		String urlm = url.toString();
		if (urlm.startsWith("http://")) {
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.putExtra("url", url.toString());
			startActivity(i);

		} else if(urlm.startsWith("https://")) {
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.putExtra("url", url.toString());
			startActivity(i);

		} else {
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.putExtra("url", "http://" + url.toString());
			startActivity(i);
		}

		finish();
	}
}
