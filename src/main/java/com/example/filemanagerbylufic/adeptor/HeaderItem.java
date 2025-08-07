package com.example.filemanagerbylufic.adeptor;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanagerbylufic.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class HeaderItem extends AbstractItem<HeaderItem , HeaderItem.ViewHolder> {
    String header;

    public HeaderItem(String header) {
        this.header = header;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 123;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.header_item;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<HeaderItem>{

        TextView headertxt;
        public ViewHolder(View itemView) {
            super(itemView);
            headertxt = itemView.findViewById(R.id.headerTitle);
        }

        @Override
        public void bindView(HeaderItem item, List<Object> payloads) {
            headertxt.setText(item.header);
        }

        @Override
        public void unbindView(HeaderItem item) {

        }
    }

}
