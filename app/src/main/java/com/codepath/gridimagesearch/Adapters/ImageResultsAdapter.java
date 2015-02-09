package com.codepath.gridimagesearch.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.gridimagesearch.Models.ImageResult;
import com.codepath.gridimagesearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by edmundye on 2/8/15.
 */
public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

    private static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
    }
    public ImageResultsAdapter (Context context, List <ImageResult> images ){
        super(context, R.layout.item_image_result, images);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageResult imageInfo = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivImage.setImageResource(0);
        viewHolder.tvTitle.setText(Html.fromHtml(imageInfo.title));
        Picasso.with(getContext()).load(imageInfo.thumbUrl).into(viewHolder.ivImage);
        return convertView;
    }
}
