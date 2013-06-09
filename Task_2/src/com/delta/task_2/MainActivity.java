package com.delta.task_2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnClickListener {

	List<String> item = null;
	List<String> path = null;
	File f,f_copy,f_move;
	String root="/";
	ListView lv;
	TextView tvPath;
	Button bBack;
	ImageButton bRoot;
	int sort_choice,flag;
	double file_size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvPath = (TextView) findViewById(R.id.path);
		getDir(root);
		lv = getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				final File re_file = new File(path.get(pos));
				ArrayList<String> dummies = new ArrayList<String>();

				dummies.add("Rename");
				dummies.add("Copy");
				dummies.add("Move");
				dummies.add("Delete");

				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.setContentView(R.layout.list_dialog);
				dialog.setTitle("Options");
				ListView listView = (ListView) dialog.findViewById(R.id.lv);

				ArrayAdapter<String> adp = new ArrayAdapter<String>(
						MainActivity.this, R.layout.single_item,
						R.id.singleItem, dummies);
				listView.setAdapter(adp);

				listView.setOnItemClickListener(new OnItemClickListener() {
					AlertDialog.Builder editalert = new AlertDialog.Builder(
							MainActivity.this);

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						switch (arg2) {
						case 0:
							editalert.setTitle("Rename File");
							editalert.setMessage("Enter the new file name :");
							final EditText input = new EditText(
									MainActivity.this);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.FILL_PARENT,
									LinearLayout.LayoutParams.FILL_PARENT);
							input.setLayoutParams(lp);
							editalert.setView(input);
							editalert.setPositiveButton("Done",	new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int whichButton) {
											String new_name = input.getText().toString();
											String curr_name = re_file.getName();
											
											File directory = new File(re_file.getParent());
											File from = new File(directory,curr_name);
											File to = new File(directory,new_name);
											from.renameTo(to);
											getDir(re_file.getParent());
																					}
									});
							editalert.show();
							break;
						case 1:
							flag=1;
							f_copy=(re_file.getAbsoluteFile());
							break;
						case 2:
							flag=2;
							f_copy=(re_file.getAbsoluteFile());
							
							
							break;
						case 3:
							String dir=re_file.getParent();
							re_file.delete();
							getDir(dir);
							break;

						}

						dialog.dismiss();

					}
				});

				dialog.show();

				return false;
			}
		});

	}

	private void getDir(String dirPath) {
		// TODO Auto-generated method stub

		item = new ArrayList<String>();
		path = new ArrayList<String>();

		f = new File(dirPath);
		File[] files = f.listFiles();
		if (sort_choice == 0)
			Arrays.sort(files);				// ***********Sort by name!
		else if (sort_choice == 1) {
			Comparator comp = new Comparator() {
				public int compare(final Object o1, final Object o2) {
					String s1 = ((File) o1).getName().toLowerCase();
					String s2 = ((File) o2).getName().toLowerCase();
					final int s1Dot = s1.lastIndexOf('.');
					final int s2Dot = s2.lastIndexOf('.');

					if ((s1Dot == -1) == (s2Dot == -1)) { // either both are files or both are folders.
						s1 = s1.substring(s1Dot + 1); 	// to get the substring after the '.'
						s2 = s2.substring(s2Dot + 1);
						return s1.compareTo(s2);
					} else if (s1Dot == -1) { 			// only s2 has an extension, so s1 goes first
						return -1;
					} else { 						// only s1 has an extension, so s1 goes second
						return 1;
					}
				}
			};
			Arrays.sort(files, comp);		// ***********Sort by type!
		} else if (sort_choice == 2) {
			Comparator comp = new Comparator() {
				public int compare(final Object o1, final Object o2) {
					File f1 = (File) o1;
					File f2 = (File) o2;
					float file_size1 = Float.parseFloat(String.valueOf(f1
							.length() / 1024));
					float file_size2 = Float.parseFloat(String.valueOf(f2
							.length() / 1024));

					if (file_size1 < file_size2)
						return -1;
					else						
						return 1; 						

				}
			};
			Arrays.sort(files, comp);		// ***********Sort by size!
		}

		if (!dirPath.equals(root)) {
			
			bRoot = (ImageButton) findViewById(R.id.bRoot);
			bBack = (Button) findViewById(R.id.bBack);
			bRoot.setEnabled(true);
			bBack.setEnabled(true);
			bRoot.setOnClickListener(this);
			bBack.setOnClickListener(this);

		}
		tvPath.setText("Current Location: " + dirPath + "\nTotal no. of files:"
				+ files.length);
		
		if (dirPath.equals(root)) {
			
			bRoot = (ImageButton) findViewById(R.id.bRoot);
			bRoot.setEnabled(false);
			bBack = (Button) findViewById(R.id.bBack);
			bBack.setEnabled(false);
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			path.add(file.getPath());
			file_size = Float.parseFloat(String.valueOf(file.length() / 1024)); //Size of cuurent file in KB
			if (file.isDirectory())
				item.add(file.getName() + "/" + "\n\t\t\t\t\t\t\tFolder");

			else if(file_size<1024)
				item.add(file.getName() + "\n\t\t\t\t\t\t\tFile Size: "+ file_size + " KB");
			else{
				file_size = Float.parseFloat(String.valueOf(file.length() / 1024/1024));	//Converting into MB
				item.add(file.getName() + "\n\t\t\t\t\t\t\tFile Size: "+ file_size + " MB");
			}
		}

		ArrayAdapter<String> filelist = new ArrayAdapter<String>(this,R.layout.row, item);
		setListAdapter(filelist);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.popup_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.sort:
			ArrayList<String> optionlist = new ArrayList<String>();

			optionlist.add("Name");
			optionlist.add("Type");
			optionlist.add("Size");

			final Dialog dialog = new Dialog(MainActivity.this);
			dialog.setContentView(R.layout.list_dialog);
			dialog.setTitle("Sort By :");
			ListView listView = (ListView) dialog.findViewById(R.id.lv);

			ArrayAdapter<String> ad = new ArrayAdapter<String>(this,R.layout.single_item, R.id.singleItem, optionlist);
			listView.setAdapter(ad);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					switch (arg2) {
					case 0:
						sort_choice = 0;
						getDir(f.getAbsolutePath());
						break;
					case 1:
						sort_choice = 1;
						getDir(f.getAbsolutePath());
						break;
					case 2:
						sort_choice = 2;
						getDir(f.getAbsolutePath());
						break;

					}
					dialog.dismiss();
				}
			});

			dialog.show();

			break;
		case R.id.paste:
			File f_paste = null;
			if(flag==2){
				f_paste=new File(f.getAbsolutePath(),f_copy.getName());
				f_copy.renameTo(f_paste);
				Log.i("New file created",f_copy.getAbsolutePath());
				flag=0;
			}
			else if(flag==1){	
			f_paste=new File(f.getAbsolutePath(),f_copy.getName());				
			
			try {
				copyFiles(f_paste.getParent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			else{
				Toast.makeText(MainActivity.this, "No Files selected", Toast.LENGTH_SHORT).show();
			}
			getDir(f.getAbsolutePath());
			break;
		case R.id.exit:
			finish();
			break;
		}
		return false;
	}

	public void copyFiles(String destination) throws IOException {  //fn. to copy files 
	    
	    FileChannel in = null;  
	    FileChannel out = null;  
	     
	        try {  
	            in = new FileInputStream(f_copy).getChannel();  
	            File outFile = new File(destination, f_copy.getName());  
	            out = new FileOutputStream(outFile).getChannel();  
	            in.transferTo(0, in.size(), out);  
	        } finally {  
	            if (in != null)  
	                in.close();  
	            if (out != null)  
	                out.close();  
	        }  
	    }  
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		File file = new File(path.get(position));

		if (file.isDirectory()) {
			if (file.canRead()) {
				getDir(path.get(position));
			} else {
				Toast.makeText(this, "Access denied!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, file.getName(), Toast.LENGTH_SHORT).show(); //Display file name
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bRoot:
			getDir(root);
			break;
		case R.id.bBack:
			getDir(f.getParent());
			break;
		}
	}
}
