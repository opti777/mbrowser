package com.example.stevo.mbrowser;

import android.app.*;
import android.content.*;
import android.database.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;


//JSON
class JsonSearchTask extends AsyncTask<String, Void, Cursor>
{

    private	String search_item="";
	private	Context context;
	private	ListView sListView;
    private	AutoCompleteTextView editSearchBox;
	private	MatrixCursor cursor;
	private String m_lang;

    JsonSearchTask(Context context, String item, ListView sListView, AutoCompleteTextView editSearchBox)
	{
		try
		{
			this.context = context;
			this.sListView = sListView;
                    this.editSearchBox = editSearchBox;
			search_item = URLEncoder.encode(item, "utf-8");
		/*
			InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
			String localeString = ims.getLocale();
			Locale locale = new Locale(localeString);
			m_lang = locale.getLanguage();
		*/
			m_lang = context.getResources().getConfiguration().locale.getLanguage();
			

			if (m_lang == null || m_lang.length() <= 0)
				m_lang = "en";
			
			String[] COLUMNS = {
		BaseColumns._ID,
		SearchManager.SUGGEST_COLUMN_TEXT_1
	};
			cursor = new MatrixCursor(COLUMNS);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected Cursor doInBackground(String... arg0)
	{
		try
		{
			String	result = sendUrlQuery(search_item);
			JSONArray city= new JSONArray(result);
			JSONArray parsedResult =  city.getJSONArray(1);

			for (int i=0;i < 6;i++)
			{	
				if (isCancelled())
				{  return null; }
				String citi =parsedResult.getString(i);
				cursor.addRow(new String[]{ String.valueOf(i + 1), citi});
			}

		}
		catch (JSONException  | IOException e)
		{
			e.printStackTrace();
		}
		return cursor;
	}
	private String sendUrlQuery(String query) throws IOException
	{
		String result = "";
		
		URL	downUrl = new URL(
			"http://google.com/complete/search?q=" + query
			+ "&client=android&hl=" + m_lang + "&oe=utf-8");

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(downUrl.openStream()));

		String line ;
		while ((line = bufferedReader.readLine()) != null)
		{
			result += line;
		}
		bufferedReader.close();


		return result;
	}
	@Override
	protected void onPostExecute(Cursor cursor)
	{

		SuggestionsListAdapter mSuggestionsAdapter = new  SuggestionsListAdapter(context, cursor);

		sListView.setAdapter(mSuggestionsAdapter);
		mSuggestionsAdapter.notifyDataSetChanged();
		super.onPostExecute(cursor);
	}
	private class SuggestionsListAdapter extends CursorAdapter
	{
		private    class   ViewHolder
		{
			TextView   textSuggest;
			ImageView   arrowSuggest;

		}
		public SuggestionsListAdapter(Context context, Cursor c)
		{
			super(context, c, 0);

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			ViewHolder holder  =   (ViewHolder)    view.getTag();
			final int textIndex1 = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);

			final	String ts=cursor.getString(textIndex1);
                    holder.textSuggest.setText(ts,TextView.BufferType.EDITABLE);
			holder.textSuggest.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view)
					{
                                            if (editSearchBox != null)
						{
                                                    editSearchBox.setText(ts);
                                                    
                                                    
                                                    //	 editSearchBox.clearFocus();

						}

					}
				});
			holder.arrowSuggest.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view)
					{
                                            if (editSearchBox != null)
						{
                                                    editSearchBox.setText(ts);
                                                    
						}

					}
				});
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup  parent)
		{
			View   view    =   LayoutInflater.from(context).inflate(R.layout.suggest_row,  null);
			ViewHolder holder  =   new ViewHolder();
			holder.textSuggest    =   (TextView)  view.findViewById(R.id.textSuggest);
			holder.arrowSuggest    =   (ImageView)  view.findViewById(R.id.arrowSuggest);
			view.setTag(holder);

			return view;
		}
	}

}// End Json class
