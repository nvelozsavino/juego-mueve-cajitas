package com.pocotopocopo.puzzlide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.multiplayer.ParticipantResult;

import java.util.List;

/**
 * Created by nico on 3/04/15.
 */
public class ResultAdapter extends ArrayAdapter<PlayerResult> {

    private Context context;
    protected List<PlayerResult> playerResultList;
    private LayoutInflater layoutInflater;

    private class ViewHolder {
        TextView txtName;
        TextView txtPos;
        TextView txtMovements;
        TextView txtTime;
        ImageView imgPicture;
    }



    public ResultAdapter(Context context, List<PlayerResult> playerResultList){
        super(context,R.layout.result_participant_layout,playerResultList);
        this.context=context;
        this.playerResultList=playerResultList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.result_participant_layout, parent, false);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtPos = (TextView) convertView.findViewById(R.id.txtPosition);
            holder.txtMovements = (TextView) convertView.findViewById(R.id.txtMovements);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            holder.imgPicture = (ImageView) convertView.findViewById(R.id.imgPicture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlayerResult playerResult = playerResultList.get(position);
        holder.txtName.setText(playerResult.getDisplayName());

        String strMovements= "";
        String strTime = "";
        String strPos = "x";
        if (playerResult.getPlacing()!= ParticipantResult.PLACING_UNINITIALIZED) {
            strMovements = context.getString(R.string.result_movements) + ": " + playerResult.getPlayerScore().getMovements();
            strTime = context.getString(R.string.result_time) + ": " + playerResult.getPlayerScore().getTime();
            strPos = Integer.toString(playerResult.getPlacing());
        }

        holder.txtMovements.setText(strMovements);
        holder.txtTime.setText(strTime);
        holder.txtPos.setText(strPos);
        if (!playerResult.getProfileImage(context, holder.imgPicture)){
            holder.imgPicture.setVisibility(View.GONE);
        }


        return convertView;
    }
}


