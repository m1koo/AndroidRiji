package com.zd.miko.riji.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.R;

public class ReaderActivity extends AppCompatActivity {

    private RichTextEditor richTextEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        initView();

        ViewTreeObserver vto2 = richTextEditor.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                richTextEditor.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                String json = getIntent().getStringExtra("json");
                ContentJson contentBean = new Gson().fromJson(json,ContentJson.class);

                for(Element e:contentBean.getElementList()){
                    if(e.getElementType().equals("TEXT"))
                        richTextEditor.insertText(e.getContent());
                    else if(e.getElementType().equals("IMAGE"))
                        richTextEditor.insertImage(e.getContent());

                }

            }
        });


    }


    private void initView(){
        richTextEditor = (RichTextEditor) findViewById(R.id.id_richedit_nativereader);
    }

}
