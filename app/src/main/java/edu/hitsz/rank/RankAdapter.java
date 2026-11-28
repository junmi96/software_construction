package edu.hitsz.rank;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import edu.hitsz.aircraftwar2026.R;

public class RankAdapter extends BaseAdapter {

    public interface OnDelete {
        void delete(Rank rank);
    }

    private final OnDelete onDelete;
    private Rank[] ranks = new Rank[0];

    public RankAdapter(OnDelete onDelete) {
        this.onDelete = onDelete;
    }

    public void setRanks(Rank[] ranks) {
        this.ranks = ranks;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ranks.length;
    }

    @Override
    public Rank getItem(int position) {
        return ranks[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rank, parent, false);
            convertView.setTag(new RowViews(convertView));
        }

        RowViews row = (RowViews) convertView.getTag();
        Rank rank = getItem(position);
        row.text.setText("第 " + rank.getRank() + " 名  "
                + rank.getName() + "  " + rank.getScore() + " 分");
        boolean first = rank.getRank() == 1;
        convertView.setBackgroundColor(first ? 0xFFFFE082 : Color.TRANSPARENT);
        row.text.setTextColor(first ? Color.BLACK : row.normalTextColor);
        row.delete.setOnClickListener(v -> onDelete.delete(rank));
        return convertView;
    }

    private static class RowViews {
        final TextView text;
        final Button delete;
        final int normalTextColor;

        RowViews(View view) {
            text = view.findViewById(R.id.rank_text);
            delete = view.findViewById(R.id.delete_rank);
            normalTextColor = text.getCurrentTextColor();
        }
    }
}