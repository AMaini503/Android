package com.example.aayush.redirectotp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by aayush on 13/4/16.
 */
public class SMSAdapter extends ArrayAdapter<SMSData> {
    private Context ctxt;
    private List<SMSData> list_sms;
    private int layout_resource;
    public SMSAdapter(Context ctxt, int layout_resource, List<SMSData> list_sms){
        super(ctxt, layout_resource, list_sms);
        this.ctxt = ctxt;
        this.list_sms = list_sms;
        this.layout_resource = layout_resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView != null){
            TextView txt_msg_add = (TextView)convertView.findViewById(R.id.msg_address);
            TextView txt_msg_body = (TextView)convertView.findViewById(R.id.msg_body);

            txt_msg_add.setText(list_sms.get(position).getNumber());
            txt_msg_body.setText(list_sms.get(position).getBody());
            return convertView;
        }
        else {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(ctxt.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_vw_messages_row, parent, false);

            TextView txt_msg_add = (TextView)rowView.findViewById(R.id.msg_address);
            TextView txt_msg_body = (TextView)rowView.findViewById(R.id.msg_body);

            txt_msg_add.setText(list_sms.get(position).getNumber());
            txt_msg_body.setText(list_sms.get(position).getBody());
            return rowView;
        }
    }
}
