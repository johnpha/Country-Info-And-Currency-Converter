package com.example.nation_currency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.List;

public class CountryViewAdapter extends ArrayAdapter<Country> {

    public CountryViewAdapter(Context context, List<Country> arrayList) {

        super(context, 0, arrayList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Country currentCountryPosition = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.custom_list_view, parent, false);

            viewHolder.flag = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.code = (TextView) convertView.findViewById(R.id.code);

            convertView.setTag(viewHolder);

        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        //Picasso.get().load(currentCountryPosition.getUrlFlagImage()).into(viewHolder.flag);


        Glide.with(getContext()).load(currentCountryPosition.getUrlFlagImage()).into(viewHolder.flag);

        viewHolder.code.setText(currentCountryPosition.getCode());
        viewHolder.name.setText(currentCountryPosition.getName());

        return convertView;
    }



    private static class ViewHolder{
        ImageView flag;
        TextView code;
        TextView name;
    }
}

