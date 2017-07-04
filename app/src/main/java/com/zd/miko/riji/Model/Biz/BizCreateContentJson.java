package com.zd.miko.riji.Model.Biz;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.CustomView.RichEditText.DataImageView;
import com.zd.miko.riji.CustomView.RichEditText.RichTextEditor;
import com.zd.miko.riji.R;

import java.util.ArrayList;

/**
 * Created by Miko on 2017/2/12.
 */

public class BizCreateContentJson {
    private RichTextEditor richTextEditor;
    private Context context;

    public BizCreateContentJson(RichTextEditor richTextEditor) {
        this.richTextEditor = richTextEditor;
        this.context = richTextEditor.getContext();
    }

    public String create() {
        ContentJson jsonBean = new ContentJson();
        ArrayList<Element> elementList = new ArrayList<>();

        LinearLayout allLayout = richTextEditor.getAllLayout();
        for (int i = 0; i < allLayout.getChildCount(); i++) {
            View child = allLayout.getChildAt(i);
            if (child instanceof EditText) {
                Element textElement = new Element();
                textElement.setElementType("TEXT");
                textElement.setContent(((EditText) child).getText().toString());
                textElement.setIndex(i);
                elementList.add(textElement);
            } else if (child instanceof RelativeLayout) {
                DataImageView image = (DataImageView) child.findViewById(R.id.edit_imageView);
                Element imageElement = new Element();
                imageElement.setElementType("IMAGE");
                imageElement.setIndex(i);
                imageElement.setContent(image.getAbsolutePath());
                elementList.add(imageElement);
            }
            jsonBean.setElementList(elementList);
        }
        return new Gson().toJson(jsonBean, ContentJson.class);
    }

}
